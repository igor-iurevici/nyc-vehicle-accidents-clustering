package dbscan_distributed.model

import Metrics._
import dbscan_distributed.config.AlgorithmConfig._

case class Point(latitude: Double, longitude: Double) extends Serializable {

  // euclidean distance
  def distanceTo(that: Point): Double = {
    val diff_longitude = longitude - that.longitude
    val diff_latitude = latitude - that.latitude
    math.sqrt(math.pow(diff_longitude, 2) + math.pow(diff_latitude, 2))
  }

  // haversine distance
  def haversineDistanceTo(that: Point): Double = {
    val diff_longitude = math.toRadians(that.longitude - longitude)
    val diff_latitude = math.toRadians(that.latitude - latitude)
    val rad_latitude1 = math.toRadians(latitude)
    val rad_latitude2 = math.toRadians(that.latitude)

    val a = math.pow(math.sin(diff_latitude / 2), 2) +
      math.pow(math.sin(diff_longitude / 2), 2) *
      math.cos(rad_latitude1) * math.cos(rad_latitude2)

    val c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))

    c * EARTH_RADIUS
  }

  // point's neighbors
  def findNeighbors(data: Seq[(Point, Long)], eps: Double, metric: Metric = HAVERSINE): Seq[(Point, Long)] = {
    metric match {
      case EUCLIDEAN => data.filter { case (point, _) => distanceTo(point) <= eps }
      case HAVERSINE => data.filter { case (point, _) => haversineDistanceTo(point) <= eps }
      case _ => throw new IllegalArgumentException("[ERROR] Metric not valid")
    }
  }
}
