package clients.mapillary

object Models {
  case class MapillaryImageDetails(
      id: String,
      thumb2048Url: Option[String] = None,
      thumbOriginalUrl: Option[String] = None
  )
}
