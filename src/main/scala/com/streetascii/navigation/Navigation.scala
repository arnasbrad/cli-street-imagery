package com.streetascii.navigation

import cats.data.EitherT
import cats.effect.IO
import com.streetascii.clients.mapillary.Models.{ImageData, MapillaryImageId}
import com.streetascii.clients.mapillary.{Errors, MapillaryClient}
import com.streetascii.common.Models.{Coordinates, Radius}

sealed trait Navigation {
  def findNextImages()(implicit
      mapillaryClient: MapillaryClient
  ): EitherT[IO, Errors.MapillaryError, List[ImageData]]
}

object Navigation {
  case class RadiusBased(
      currentImageId: MapillaryImageId,
      currentCoordinates: Coordinates,
      radius: Radius,
      maxAmount: Int
  ) extends Navigation {
    // given coordinates, find the closest surrounding images
    def findNextImages()(implicit
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

  }

  // Helper function to calculate distance between two coordinates using Haversine formula
  def calculateDistance(
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
