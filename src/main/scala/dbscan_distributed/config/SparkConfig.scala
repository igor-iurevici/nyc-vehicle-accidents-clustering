package dbscan_distributed.config

import org.apache.spark.sql.SparkSession

object SparkConfig {
  var PARTITIONS = 1 // number of partitions

  private def _sparkSession(master: String): SparkSession = {
    var builder = SparkSession.builder.appName("NYC-Accidents-Clustering")

    if (master != "default") {
      builder = builder.master(master)
    }

    builder.getOrCreate()
  }

  def sparkSession(master: String, partitions: Int): SparkSession = {
    val session = _sparkSession(master)

    PARTITIONS = partitions
    session.sparkContext.setLogLevel("WARN")

    session
  }
}
