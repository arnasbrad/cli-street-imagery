package com.streetascii.texttoimage

import com.streetascii.asciiart.Models.ImageInfo
import com.streetascii.clients.mapillary.Models.{ImageData, MapillaryImageId}
import com.streetascii.guessinggame.CountryModels.Country
import com.streetascii.navigation.Navigation

import scala.util.Random

object Constants {
  val help: String =
    """--------------------------
      |General inputs
      |[q] - quit
      |[n] - navigation
      |--------------------------------
      |-Sequence navigation inputs    -
      |-[f] - move forwards           -
      |-[b] - move backwards          -
      |--------------------------------""".stripMargin

  def radiusNavOptionsList(
      currentImageData: ImageInfo,
      navOptions: List[ImageData]
  ): String = {
    def angleToDirectionChar(normalizedAngle: Double): Char = {
      // Define angle ranges for each direction
      val ranges = Array(
        (-22.5  -> 22.5, '↑'),   // North
        (22.5   -> 67.5, '↗'),   // Northeast
        (67.5   -> 112.5, '→'),  // East
        (112.5  -> 157.5, '↘'),  // Southeast
        (157.5  -> -157.5, '↓'), // South (wrapping around from 157.5 to -157.5)
        (-157.5 -> -112.5, '↙'), // Southwest
        (-112.5 -> -67.5, '←'),  // West
        (-67.5  -> -22.5, '↖')   // Northwest
      )

      // Find the matching range
      ranges
        .find { case ((min, max), _) =>
          if (min <= max) {
            normalizedAngle >= min && normalizedAngle < max
          } else {
            // Handle the wrap-around case for South
            normalizedAngle >= min || normalizedAngle < max
          }
        }
        .map(_._2)
        .getOrElse('⇑') // Default to North if no match (shouldn't happen)
    }

    navOptions.zipWithIndex
      .map { case (navOpt, index) =>
        val distance = Navigation.calculateDistance(
          currentImageData.coordinates,
          navOpt.coordinates
        )
        val turnAngle = Navigation.calculateTurnAngle(
          currentImageData.compassAngle,
          currentImageData.coordinates,
          navOpt.coordinates
        )
        s"[${index + 1}] ${distance.toString.take(3)}m, ${angleToDirectionChar(turnAngle)}"
      }
      .mkString("\n")
  }

  def radiusNavOptionsMap(navOptions: List[ImageData]): String = ""

  def sequenceNavOptsList(
      backwardsOpt: Option[MapillaryImageId],
      forwardsOpt: Option[MapillaryImageId]
  ): String = {
    val fString =
      if (forwardsOpt.isDefined) "[f] Forwards ↑" else "[g] Not available"
    val bString =
      if (backwardsOpt.isDefined) "[b] Backwards ↓" else "[b] Not available"

    s"""$fString
       |$bString
       |""".stripMargin
  }

  def guessingOptsList(
      correctCountry: Country,
      otherCounties: List[Country]
  ): String = {
    val allCountriesList      = correctCountry :: otherCounties
    val shuffledCountriesList = Random.shuffle(allCountriesList)

    shuffledCountriesList.zipWithIndex
      .map { case (guessOpt, index) =>
        s"[${index + 1}] ${guessOpt.name}"
      }
      .mkString("\n")
  }
}
