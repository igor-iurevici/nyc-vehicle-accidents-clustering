package dbscan_distributed

import dbscan_distributed.algorithm.DBSCAN
import dbscan_distributed.utils.FileManager._
import dbscan_distributed.config.SparkConfig._

object Main {
  def main(args: Array[String]) {
    /*
     * INPUT ARGS (6)
     */
    val master = args(0)            // Builder URL
    val partitions = args(1).toInt  // Number of partitions
    val fromFile = args(2)          // Input file path
    val eps = args(3).toDouble      // Epsilon value
    val minPoints = args(4).toInt   // Min points value
    val toFile = args(5)            // Path / Filename for output file

    println("*****************************************************")
    println("Running DBSCAN with the following configuration:")
    println(s"* Master: $master")
    println(s"* Number of partitions: $partitions")
    println(s"* Input data loaded from: $fromFile")
    println(s"* Epsilon: $eps")
    println(s"* Minimum points: $minPoints")
    println(s"* Output data saved in: $fromFile")
    println("*****************************************************")

    /*
     * SPARK SESSION
     */
    val spark = sparkSession(master, partitions)

    /*
     * EXECUTION / TIMING / SAVING TO CSV
     */
    val t0 = System.nanoTime
    val model = DBSCAN.fit(spark, fromFile, eps, minPoints)
    val t1 = System.nanoTime
    println("Elapsed time : " + (t1 - t0) / math.pow(10, 9) + " seconds")
    println("Found " + model.getClustersNum() + " clusters")
    saveClusters(spark, model.getClusters(), toFile)
    //saveLargestCluster(spark, model.getLargestCluster(), toFile)
    spark.stop()

  }
}
