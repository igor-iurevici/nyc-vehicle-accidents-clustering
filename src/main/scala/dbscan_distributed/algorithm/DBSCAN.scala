package dbscan_distributed.algorithm

import dbscan_distributed.config.AlgorithmConfig._
import dbscan_distributed.model.Point
import dbscan_distributed.utils.FileManager.loadData
import dbscan_distributed.config.SparkConfig._

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import scala.collection.mutable
import scala.language.postfixOps

object DBSCAN {
  /**
   * Fits a DBSCAN clustering model to the data using Spark.
   *
   * DBSCAN (Density-Based Spatial Clustering of Applications with Noise) is a density-based
   * clustering algorithm that identifies clusters in a dataset based on the density of data points.
   *
   * @param spark       The SparkSession to use for distributed computing.
   * @param filename    The name of the input file or dataset containing the data to be clustered.
   * @param eps         The maximum radius of the neighborhood around a data point for it to be
   *                    considered as a core point.
   * @param minPoints   The minimum number of data points within the neighborhood of a core point
   *                    for it to be considered a part of a cluster.
   *
   * @return A DBSCAN clustering model representing the identified clusters in the data.
   */
  def fit(spark: SparkSession, filename: String, eps: Double, minPoints: Int): Model = {
    val sc = spark.sparkContext
    //var points: RDD[Point] = loadData(spark, filename)
    //val pointsDriver: Array[Point] = points.collect()
    var points: RDD[(Point, Long)] = loadData(spark, filename).zipWithIndex()
    val pointsDriver: Array[(Point, Long)] = points.collect()
    //val clusters = mutable.Map(points.collect().map((_, UNKNOWN)) toSeq: _*)
    val clusters = mutable.Map(pointsDriver.map { case (_, index) => (index, UNKNOWN) } toSeq: _*)
    println(s"Size of clusters map: ${clusters.size}")
    var clusterId = 0

    for (p <- pointsDriver) {
      if (clusters(p._2) != UNKNOWN) {}
      else {
        var queue = p._1.findNeighbors(pointsDriver, eps).toSet

        if (queue.size < minPoints) clusters(p._2) = NOISE
        else {
          clusterId += 1
          clusters(p._2) = clusterId
          queue = queue.filter(p1 => clusters(p1._2) <= NOISE)

          queue.foreach { case (_, index) => clusters(index) = clusterId }
          //queue.foreach(clusters(_._2) = clusterId)

          // to EXECUTOR
          while (!queue.isEmpty) {
            points = sc.parallelize(queue.toSeq, PARTITIONS)
            val clustersBC = sc.broadcast(clusters)

            // init aggregator
            def expand(current: Set[(Point, Long)], p1: (Point, Long)): Set[(Point, Long)] = {
              var neighbors = p1._1.findNeighbors(pointsDriver, eps)
              if (neighbors.size < minPoints) current
              else {
                neighbors = neighbors.filter(p => clustersBC.value(p._2) <= NOISE)
                current ++ neighbors.toSet
              }
            }

            // back to DRIVER
            queue = points.aggregate(Set.empty[(Point, Long)])(expand, (set1, set2) => set1 ++ set2)
            clustersBC.destroy()
            //queue.foreach(clusters(_) = clusterId)
            queue.foreach { case (_, index) => clusters(index) = clusterId}
          }
        }
      }
    }

    val result: Seq[(Point, Int)] = pointsDriver.map {
      case (point, index) => (point, clusters(index))
    }

    println("Result size: " + result.size)

    new Model(clusterId, result)
  }
}


class Model(val clustersNum: Int, val clusters: Seq[(Point, Int)]) {

  // Total number of clusters
  def getClustersNum(): Int = clustersNum

  // Cluster by Id
  def getClusterById(id: Int): Seq[(Point, Int)] = clusters.filter(_._2 == id)

  // Noise points
  def getNoisePoints(): Seq[(Point, Int)] = clusters.filter(_._2 == NOISE)

  // Largest cluster id
  def getLargestClusterId(): Int = clusters.groupBy(_._2).maxBy(_._2.size)._1

  // All clusters
  def getClusters(): Seq[(Double, Double, Int)] = {
    val data = clusters.map {
      case (point, clusterId) => (point.latitude, point.longitude, clusterId)
    }

    data
  }

  // Largest cluster
  def getLargestCluster(): Seq[(Point, Int)] = getClusterById(getLargestClusterId())
}
