package runner

import asciiart.Conversions
import asciiart.Models.ImageInfo
import cats.data.EitherT
import cats.effect.IO
import clients.imgur.{Errors, ImgurClient}
import clients.mapillary.Errors.MapillaryError
import clients.mapillary.MapillaryClient
import clients.mapillary.Models.MapillaryImageId
import common.Models.{Coordinates, Radius}
import navigation.Navigation
import socialmedia.SocialMedia

sealed trait Runner {
  def getHexStringsFromLocation(
      coordinates: Coordinates,
      radius: Radius = Radius.unsafeCreate(3)
  ): EitherT[IO, MapillaryError, ImageInfo]

  def getHexStringsFromId(
      imageId: MapillaryImageId
  ): EitherT[IO, MapillaryError, ImageInfo]

  def getNeighborImageIds(
      currentImageId: MapillaryImageId,
      currentCoordinates: Coordinates,
      radius: Radius,
      maxAmount: Int
  ): EitherT[IO, MapillaryError, List[MapillaryImageId]]

  def generateSocialMediaLinks(
      text: String,
      imageBytes: Array[Byte]
  ): EitherT[IO, Errors.ImgurError, List[SocialMedia]]
}

object Runner {
  def make(
      mapillaryClient: MapillaryClient,
      imgurClient: ImgurClient,
      conversions: Conversions = Conversions
  ): Runner = {
    new RunnerImpl()(
      conversionLogic = conversions,
      mapillaryClient = mapillaryClient,
      imgurClient = imgurClient
    )
  }

  private class RunnerImpl(
  )(implicit
      conversionLogic: Conversions,
      mapillaryClient: MapillaryClient,
      imgurClient: ImgurClient
  ) extends Runner {
    def getHexStringsFromId(
        imageId: MapillaryImageId
    ): EitherT[IO, MapillaryError, ImageInfo] = {
      for {
        res <- mapillaryClient.getImage(imageId)
        (byteArray, coordinates) = res

        hex <- EitherT.liftF(
          conversionLogic.convertBytesToHexImage(byteArray)
        )
      } yield ImageInfo(hex, imageId, coordinates)
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
        (imageByteArray, newCoordinates) = res
        hex <- EitherT.liftF(
          conversionLogic.convertBytesToHexImage(imageByteArray)
        )
      } yield ImageInfo(hex, imageId, newCoordinates)
    }

    def getNeighborImageIds(
        currentImageId: MapillaryImageId,
        currentCoordinates: Coordinates,
        radius: Radius,
        maxAmount: Int
    ): EitherT[IO, MapillaryError, List[MapillaryImageId]] = {
      for {
        neighborsData <- Navigation.findPossibleNavigationOptions(
          currentImageId = currentImageId,
          currentCoordinates = currentCoordinates,
          radius = radius,
          maxAmount = maxAmount
        )
      } yield neighborsData.map(_.id)
    }

    def generateSocialMediaLinks(
        text: String,
        imageBytes: Array[Byte]
    ): EitherT[IO, Errors.ImgurError, List[SocialMedia]] = {
      for {
        imgurLink <- imgurClient.uploadImage(imageBytes).map(_.data.link)

        xLink  = SocialMedia.X(text = text, imgurLink = imgurLink)
        fbLink = SocialMedia.FaceBook(imgurLink = imgurLink)
      } yield List(xLink, fbLink)
    }
  }
}
