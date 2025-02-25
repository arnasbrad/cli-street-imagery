package clients.mapillary

object Errors {
  class NoImageThumbnailFoundException(id: String)
      extends Exception(s"Image $id not found")
}
