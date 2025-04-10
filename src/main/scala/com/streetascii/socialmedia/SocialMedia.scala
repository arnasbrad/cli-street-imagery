package com.streetascii.socialmedia

import org.http4s.Uri

trait SocialMedia {
  def uri: Uri
}

object SocialMedia {
  case class X(text: String, imgurLink: String) extends SocialMedia {
    def uri: Uri = {
      val baseUri = Uri.unsafeFromString("https://x.com/intent/post")

      baseUri
        .withQueryParam("text", text)
        .withQueryParam("url", imgurLink)
    }
  }

  case class FaceBook(imgurLink: String) extends SocialMedia {
    def uri: Uri = {
      val baseUri =
        Uri.unsafeFromString("https://www.facebook.com/sharer/sharer.php")

      baseUri
        .withQueryParam("u", imgurLink)
    }
  }
}
