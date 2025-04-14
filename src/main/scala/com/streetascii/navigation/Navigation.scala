package com.streetascii.navigation

import cats.data.EitherT
import cats.effect.IO
import com.streetascii.clients.mapillary.Models.{
  ImageData,
  MapillaryImageId,
  MapillarySequenceId
}
import com.streetascii.clients.mapillary.{Errors, MapillaryClient}
import com.streetascii.common.Models.{Coordinates, Radius}

object Navigation {
  // given coordinates, find the closest surrounding images
  def findNearbyImages(
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

  // given coordinates, find the closest surrounding images
  def findSequenceNeighbors(
      currentImageId: MapillaryImageId,
      sequenceId: MapillarySequenceId
  )(implicit
      mapillaryClient: MapillaryClient
  ): EitherT[
    IO,
    Errors.MapillaryError,
    (Option[MapillaryImageId], Option[MapillaryImageId])
  ] = {

    for {
      imageIdsInSequence <- mapillaryClient.getImageIdsBySequence(sequenceId)

      neighborTuple = listNeighbors(
        imageIdsInSequence,
        currentImageId
      )
    } yield neighborTuple
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

  def calculateTurnAngle(
      currentCompassAngle: Double,
      currentCoords: Coordinates,
      targetCoords: Coordinates
  ): Double = {
    def calculateBearing(
        currentCoords: Coordinates,
        targetCoords: Coordinates
    ): Double = {
      // Convert to radians
      val lat1 = Math.toRadians(currentCoords.lat)
      val lng1 = Math.toRadians(currentCoords.lng)
      val lat2 = Math.toRadians(targetCoords.lat)
      val lng2 = Math.toRadians(targetCoords.lng)

      val deltaLng = lng2 - lng1

      val y = Math.sin(deltaLng) * Math.cos(lat2)
      val x = Math.cos(lat1) * Math.sin(lat2) -
        Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLng)

      var bearing = Math.atan2(y, x)

      // Convert to degrees
      bearing = Math.toDegrees(bearing)

      // Normalize to 0-360 degrees
      bearing = (bearing + 360) % 360

      bearing
    }
    val turnAngle =
      calculateBearing(currentCoords, targetCoords) - currentCompassAngle

    // Normalize to -180 to +180 degrees
    var normalizedAngle = turnAngle
    while (normalizedAngle > 180) normalizedAngle -= 360
    while (normalizedAngle <= -180) normalizedAngle += 360

    normalizedAngle
  }

  def listNeighbors(
      list: List[MapillaryImageId],
      target: MapillaryImageId
  ): (Option[MapillaryImageId], Option[MapillaryImageId]) = {
    list match {
      case Nil => (None, None)

      // If target is the first element
      case `target` :: next :: _ => (None, Some(next))

      // If target is somewhere in the middle or end
      case _ =>
        // Use zipWithIndex for better performance than separate indexOf
        val withIndices = list.zipWithIndex

        withIndices.find(_._1 == target) match {
          case Some((_, idx)) =>
            val prev = if (idx > 0) Some(list(idx - 1)) else None
            val next =
              if (idx < list.length - 1) Some(list(idx + 1)) else None
            (prev, next)
          case None => (None, None)
        }
    }
  }
}
