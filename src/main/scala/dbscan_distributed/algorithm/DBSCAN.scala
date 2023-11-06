package dbscan_distributed.algorithm

import dbscan_distributed.config.AlgorithmConfig._
import dbscan_distributed.model.Point
import dbscan_distributed.utils.FileManager.loadData
import dbscan_distributed.config.SparkConfig._

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import scala.collection.mutable
import scala.language.postfixOps

object DBSCAN extends Serializable {
  def fit(spark: SparkSession, filename: String, eps: Double, minPoints: Int): Model = {
    val sc = spark.sparkContext
    var points: RDD[Point] = loadData(spark, filename)
    val pointsDriver: Array[Point] = points.collect()
    val clusters = mutable.Map(points.collect().map((_, UNKNOWN)) toSeq: _*)
    var clusterId = 0

    for (p <- pointsDriver) {
      if (clusters(p) != UNKNOWN) {}
      else {
        var queue = p.findNeighbors(pointsDriver, eps).toSet

        if (queue.size < minPoints) clusters(p) = NOISE
        else {
          clusterId += 1
          clusters(p) = clusterId
          queue = queue.filter(p1 => clusters(p1) <= NOISE)
          queue.foreach(clusters(_) = clusterId)

          // to EXECUTOR
          while (!queue.isEmpty) {
            points = sc.parallelize(queue.toSeq, PARTITIONS)
            val clustersBC = sc.broadcast(clusters)

            // init aggregator
            def expand(current: Set[Point], p1: Point): Set[Point] = {
              var neighbors = p1.findNeighbors(pointsDriver, eps)
              if (neighbors.size < minPoints) current
              else {
                neighbors = neighbors.filter(p2 => clustersBC.value(p2) <= NOISE)
                current ++ neighbors.toSet
              }
            }

            // back to DRIVER
            queue = points.aggregate(Set(): Set[Point])(expand, _ ++ _)
            clustersBC.destroy()
            queue.foreach(clusters(_) = clusterId)
          }
        }
      }
    }

    /*
    val clustersRDD: RDD[(Point, Int)] = sc.parallelize(clusters.toSeq)
    val largestClusterId = clustersRDD
      .map { case (_, clusterId) => (clusterId, 1) }
      .reduceByKey(_ + _)
      .max()(Ordering.by[(Int, Int), Int](_._2))
      ._1
     */

    new Model(clusterId, clusters)
  }
}


class Model(val clustersNum: Int, val clusters: mutable.Map[Point, Int]) {

  // Total number of clusters
  def getClustersNum(): Int = clustersNum

  // Cluster by Id
  def getClusterById(id: Int): Seq[Point] = clusters.filter( _._2 == id).keys.toSeq

  // Noise points
  def getNoisePoints(): Seq[Point] = clusters.filter(_._2 == NOISE).keys.toSeq

  // Largest cluster id
  def getLargestClusterId(): Int = clusters.groupBy(_._2).maxBy(_._2.size)._1

  // All clusters
  def getClusters(): Seq[(Double, Double, Int)] = {
    val data = clusters.map {
      case (point, clusterId) => (point.latitude, point.longitude, clusterId)
    }.toSeq

    data
  }

  // Largest cluster
  def getLargestCluster(): Seq[Point] = getClusterById(getLargestClusterId())
}
