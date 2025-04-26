package com.streetascii.texttoimage

import cats.effect.IO
import com.streetascii.asciiart.Models.ImageInfo
import com.streetascii.clients.mapillary.Models.{ImageData, MapillaryImageId}
import com.streetascii.guessinggame.CountryModels.Country
import com.streetascii.navigation.Navigation

import scala.util.Random

object Constants {
  object Help {
    val mainHelp: String =
      """| [n] - navigation
         | [s] - share
         | [h] - help
         | [q] - quit""".stripMargin

    val mainHelpWithGuessing: String =
      s"""| [g] - guess country
          |$mainHelp""".stripMargin

    val radiusNavHelp: String =
      """| [1 - n] - go to selected location
         | [r] - re-render
         | [n] - back to navigation
         | [q] - quit""".stripMargin

    val sequenceNavHelp: String =
      """| [f] - go forwards
         | [b] - go backwards
         | [r] - re-render
         | [n] - back to navigation
         | [q] - quit""".stripMargin

    val guessingHelp: String =
      """| [1 - 5] - guess
         | [g] - back to guessing
         | [q] - quit""".stripMargin
  }

  val exiting: String = "Goodbye!"

  val correctGuess: String =
    """| Correct guess
       | press [enter] to continue""".stripMargin

  def wrongGuess(correctCountry: Country): String = {
    s"""| Incorrect guess
        | correct was ${correctCountry.name}
        | GAME OVER""".stripMargin
  }

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
      if (forwardsOpt.isDefined) "[f] Forwards ↑" else "[f] Not available"
    val bString =
      if (backwardsOpt.isDefined) "[b] Backwards ↓" else "[b] Not available"

    s"""| $fString
        | $bString""".stripMargin
  }

  def guessingOptsList(
      correctCountry: Country,
      otherCounties: List[Country]
  ): IO[(String, Int)] = {
    val allCountriesList = correctCountry :: otherCounties
    for {
      shuffledCountriesList <- IO(Random.shuffle(allCountriesList))
      str = shuffledCountriesList.zipWithIndex
        .map { case (guessOpt, index) =>
          s"[${index + 1}] ${guessOpt.name}"
        }
        .mkString("\n")
      index = shuffledCountriesList.indexOf(correctCountry)
    } yield (str, index)
  }
}
