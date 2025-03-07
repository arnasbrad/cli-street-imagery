package navigation

import cats.data.EitherT
import cats.effect.IO
import clients.mapillary.{Errors, MapillaryClient}
import clients.mapillary.Models.{ImageData, MapillaryImageId}
import common.Models.{Coordinates, Radius}

sealed trait Navigation {
  def findPossibleNavigationOptions(
      currentImageId: MapillaryImageId,
      currentCoordinates: Coordinates,
      radius: Radius,
      maxAmount: Int
  )(implicit
      mapillaryClient: MapillaryClient
  ): EitherT[IO, Errors.MapillaryError, List[ImageData]]
}

object Navigation extends Navigation {
  // given coordinates, find the closest surrounding images
  def findPossibleNavigationOptions(
      currentImageId: MapillaryImageId,
      currentCoordinates: Coordinates,
      radius: Radius,
      maxAmount: Int
  )(implicit
      mapillaryClient: MapillaryClient
  ): EitherT[IO, Errors.MapillaryError, List[ImageData]] = {
    for {
      imagesResponse <- mapillaryClient.getImagesInfoByLocation(
        currentCoordinates,
        radius
      )

      otherImages = imagesResponse.data.filter(_.id != currentImageId)

      imagesWithDistance = otherImages.map { imageData =>
        (
          imageData,
          calculateDistance(currentCoordinates, imageData.coordinates)
        )
      }

      // Sort by distance and take top maxAmount
      closestImages = imagesWithDistance
        .sortBy(_._2) // Sort by distance (second element of tuple)
        .take(maxAmount)
        .map(_._1) // Extract just the ImageData

    } yield closestImages
  }

  // Helper function to calculate distance between two coordinates using Haversine formula
  private def calculateDistance(
      coord1: Coordinates,
      coord2: Coordinates
  ): Double = {
    val earthRadius = 6371000 // Earth radius in meters

    val lat1Rad    = Math.toRadians(coord1.lat)
    val lat2Rad    = Math.toRadians(coord2.lat)
    val latDiffRad = Math.toRadians(coord2.lat - coord1.lat)
    val lngDiffRad = Math.toRadians(coord2.lng - coord1.lng)

    val a = Math.sin(latDiffRad / 2) * Math.sin(latDiffRad / 2) +
      Math.cos(lat1Rad) * Math.cos(lat2Rad) *
      Math.sin(lngDiffRad / 2) * Math.sin(lngDiffRad / 2)

    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    earthRadius * c // Distance in meters
  }
}

// TODO: maybe add more types, like sequence based. Currently my sequence logic was wrong, so I deleted it
/*
  private class SequenceBasedNavigation extends Navigation {
  }
 */
