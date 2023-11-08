package dbscan_distributed.utils

import dbscan_distributed.model.Point
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import scala.collection.mutable

object FileManager {
  def loadData(spark: SparkSession, filename: String): RDD[Point] = {
    val data = spark.read.option("header", "true")
      .csv(filename).rdd.map(row => Point(row.getString(0).toDouble, row.getString(1).toDouble))
      .map((_, 1)).reduceByKey((v1, v2) => v1 + v2).keys

    data
  }

  def saveClusters(spark: SparkSession, data: Seq[(Double, Double, Int)], filename: String): Unit = {
    print(s"Saving file to $filename...")

    spark
      .createDataFrame(data)
      .toDF("latitude", "longitude", "clusterid")
      .coalesce(1)
      .write
      .option("header", true)
      .option("delimiter", ",")
      .mode("overwrite")
      .csv(filename)

    println(" Complete")

  }

  /*
   * Saving DBSCAN result in CSV with the following structure:
   * ————————————————————————————————————————
   * || CLUSTER_ID | POINT_LON | POINT_LAT ||
   * ————————————————————————————————————————
   */
  def saveToCsv(filename: String, points: mutable.Map[Point, Int]): Unit = {
    print("Saving file...")
    val csv_file = new java.io.PrintWriter(new java.io.File(filename))

    val columnNames = "CLUSTER_ID,POINT_LAT,POINT_LON"
    csv_file.println(columnNames)

    val pointRows = points.map {
      case (point, clusterId) =>
        s"$clusterId,${point.longitude},${point.latitude}"
    }
    pointRows.foreach(csv_file.println)
    csv_file.close()
    println(" Complete")
  }

  /*
  // Save execution time for both sequential and parallel versions
  def saveExecutionTime(filename: String, executionType: ExecutionType, time: Double): Unit = {
    print("Saving execution time...")
    val csv_file = new java.io.PrintWriter(new java.io.File(filename))

    csv_file.println("TYPE,TIME")
    csv_file.println(s"${executionType},${time}")
    csv_file.close()
    println(" Complete")
  }
   */
}
