import dbscan_distributed.model.Point

def apply(startLon: Double, startLat: Double, endLon: Double, endLat: Double): Double = {
  val R = 6378137d

  val dLat = math.toRadians(endLat - startLat)
  val dLon = math.toRadians(endLon - startLon)
  val lat1 = math.toRadians(startLat)
  val lat2 = math.toRadians(endLat)

  val a =
    math.sin(dLat / 2) * math.sin(dLat / 2) +
      math.sin(dLon / 2) * math.sin(dLon / 2) * math.cos(lat1) * math.cos(lat2)
  val c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))

  R * c
}

val p1 = Point(40.663303,-73.96049)
val p2 = Point(40.607685,-74.13892)

println("Euclidean distance: " + p1.distanceTo(p2))
println("Haversine distance: " + p1.haversineDistanceTo(p2))
println("Haversine2 distance: " + apply(p1.longitude, p1.latitude, p2.longitude, p2.latitude))

