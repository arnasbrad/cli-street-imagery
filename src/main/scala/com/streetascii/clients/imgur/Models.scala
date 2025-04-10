package com.streetascii.clients.imgur

object Models {
  case class ImgurData(link: String, deletehash: String, id: String)
  case class ImgurResponse(data: ImgurData, success: Boolean, status: Int)
}
