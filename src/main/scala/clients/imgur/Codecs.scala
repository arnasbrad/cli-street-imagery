package clients.imgur

import cats.effect.IO
import clients.imgur.Models.{ImgurData, ImgurResponse}
import io.circe.{Decoder, HCursor}
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

object Codecs {
  implicit val imgurDataDecoder: Decoder[ImgurData] = (c: HCursor) =>
    for {
      link       <- c.get[String]("link")
      deletehash <- c.get[String]("deletehash")
      id         <- c.get[String]("id")
    } yield ImgurData(link, deletehash, id)

  implicit val imgurResponseDecoder: Decoder[ImgurResponse] = (c: HCursor) =>
    for {
      data    <- c.get[ImgurData]("data")
      success <- c.get[Boolean]("success")
      status  <- c.get[Int]("status")
    } yield ImgurResponse(data, success, status)

  implicit val imgurResponseEntityDecoder: EntityDecoder[IO, ImgurResponse] =
    jsonOf[IO, ImgurResponse]
}
