package com.streetascii.runner

import cats.data.EitherT
import cats.effect.IO
import com.streetascii.asciiart.Conversions
import com.streetascii.asciiart.Models.ImageInfo
import com.streetascii.clients.imgur.{Errors, ImgurClient}
import com.streetascii.clients.mapillary.Errors.MapillaryError
import com.streetascii.clients.mapillary.MapillaryClient
import com.streetascii.clients.mapillary.Models.MapillaryImageId
import com.streetascii.clients.traveltime.Errors.TravelTimeError
import com.streetascii.clients.traveltime.TravelTimeClient
import com.streetascii.common.Models._
import com.streetascii.socialmedia.SocialMedia

sealed trait Runner {
  def getHexStringsFromLocation(
      coordinates: Coordinates,
      radius: Radius = Radius.unsafeCreate(3)
  ): EitherT[IO, MapillaryError, ImageInfo]

  def getHexStringsFromId(
      imageId: MapillaryImageId
  ): EitherT[IO, MapillaryError, ImageInfo]

  def getCoordinatesFromAddress(
      address: String
  ): EitherT[IO, TravelTimeError, Option[Coordinates]]

  def generateSocialMediaLinks(
      imageBytes: Array[Byte]
  ): EitherT[IO, Errors.ImgurError, List[SocialMedia]]
}

case class RunnerImpl(
    mapillaryClient: MapillaryClient,
    imgurClient: ImgurClient,
    travelTimeClient: TravelTimeClient,
    conversions: Conversions = Conversions
) extends Runner {
  def getHexStringsFromId(
      imageId: MapillaryImageId
  ): EitherT[IO, MapillaryError, ImageInfo] = {
    for {
      res <- mapillaryClient.getImage(imageId)
      (byteArray, imageDetails) = res

      hex <- EitherT.liftF(
        conversions.convertBytesToHexImage(byteArray)
      )
    } yield ImageInfo(
      hex,
      imageId,
      imageDetails.compassAngle,
      imageDetails.sequenceId,
      imageDetails.coordinates
    )
  }

  def getHexStringsFromLocation(
      coordinates: Coordinates,
      radius: Radius
  ): EitherT[IO, MapillaryError, ImageInfo] = {
    for {
      imagesInfoResp <- mapillaryClient.getImagesInfoByLocation(
        coordinates,
        radius
      )

      imageId <- EitherT(
        IO.pure(
          imagesInfoResp.data.headOption
            .map(_.id)
            .toRight(
              MapillaryError.NotFoundError(
                s"No images found for coordinates = $coordinates, radius = $radius m. Try a different location or increasing the radius."
              )
            )
        )
      )

      res <- mapillaryClient.getImage(imageId)
      (imageByteArray, newImageDetails) = res
      hex <- EitherT.liftF(
        conversions.convertBytesToHexImage(imageByteArray)
      )
    } yield ImageInfo(
      hex,
      imageId,
      newImageDetails.compassAngle,
      newImageDetails.sequenceId,
      newImageDetails.coordinates
    )
  }

  def getCoordinatesFromAddress(
      address: String
  ): EitherT[IO, TravelTimeError, Option[Coordinates]] =
    for {
      resp <- travelTimeClient.geocodingSearch(address)
    } yield resp.features.headOption.map(_.coordinates)

  def generateSocialMediaLinks(
      imageBytes: Array[Byte]
  ): EitherT[IO, Errors.ImgurError, List[SocialMedia]] = {
    for {
      imgurLink <- imgurClient.uploadImage(imageBytes).map(_.data.link)

      xLink = SocialMedia.X(
        text = "Check out this cool image!",
        imgurLink = imgurLink
      )
      fbLink = SocialMedia.FaceBook(imgurLink = imgurLink)
    } yield List(xLink, fbLink)
  }
}
