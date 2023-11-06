package dbscan_distributed.model

object Metrics extends Enumeration {
  type Metric = Value
  val EUCLIDEAN, HAVERSINE = Value
}
