package com.streetascii.clients.imgur

import com.streetascii.common.Errors.{EmptyInputError, ValidationError}

object Models {
  case class ImgurData(link: String, deletehash: String, id: String)
  case class ImgurResponse(data: ImgurData, success: Boolean, status: Int)

  case class ClientId private (value: String)
  case object ClientId {
    def create(v: String): Either[ValidationError, ClientId] = {
      Either.cond(v.nonEmpty, new ClientId(v), EmptyInputError)
    }

    def unsafeCreate(v: String): ClientId = {
      new ClientId(v)
    }
  }
}
