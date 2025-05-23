package com.streetascii.texttoimage

import cats.effect.IO
import com.streetascii.asciiart.Models.ImageInfo
import com.streetascii.clients.mapillary.Models.{ImageData, MapillaryImageId}
import com.streetascii.guessinggame.CountryModels.Country
import com.streetascii.navigation.Navigation

import scala.util.Random
import java.text.DecimalFormat

object Constants {
  object Help {
    val mainHelp: String =
      """| [N] - NAVIGATION
         | [S] - SHARE
         | [C] - CAPTURE (SAVE AS PNG)
         | [H] - HELP
         | [R] - RE-RENDER
         | [Q] - QUIT""".stripMargin

    val mainHelpWithGuessing: String =
      s"""| [G] - GUESS COUNTRY
          |$mainHelp""".stripMargin

    val radiusNavHelp: String =
      """| [1 - N] - GO TO SELECTED LOCATION
         | [R] - RE-RENDER
         | [N] - BACK TO NAVIGATION
         | [Q] - QUIT""".stripMargin

    val sequenceNavHelp: String =
      """| [F] - GO FORWARDS
         | [B] - GO BACKWARDS
         | [R] - RE-RENDER
         | [N] - BACK TO NAVIGATION
         | [Q] - QUIT""".stripMargin

    val guessingHelp: String =
      """| [1 - 5] - GUESS
         | [G] - BACK TO GUESSING
         | [Q] - QUIT""".stripMargin
  }

  val exiting: String = "GOODBYE!"

  def correctGuess(correctCountry: Country): String =
    s"""| ${correctCountry.name.toUpperCase} IS CORRECT!
        | PRESS [ENTER] TO CONTINUE""".stripMargin

  def wrongGuess(correctCountry: Country): String = {
    s"""| INCORRECT GUESS
        | CORRECT WAS ${correctCountry.name.toUpperCase}
        | GAME OVER""".stripMargin
  }

  // Define angle ranges for each direction
  private val ranges = Array(
    (-22.5  -> 22.5, '↑'),   // North
    (22.5   -> 67.5, '↗'),   // Northeast
    (67.5   -> 112.5, '→'),  // East
    (112.5  -> 157.5, '↘'),  // Southeast
    (157.5  -> -157.5, '↓'), // South (wrapping around from 157.5 to -157.5)
    (-157.5 -> -112.5, '↙'), // Southwest
    (-112.5 -> -67.5, '←'),  // West
    (-67.5  -> -22.5, '↖')   // Northwest
  )

  def radiusNavOptionsList(
      currentImageData: ImageInfo,
      navOptions: List[ImageData]
  ): String = {
    def angleToDirectionChar(normalizedAngle: Double): Char = {
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
        .getOrElse('↑') // Default to North if no match (shouldn't happen)
    }

    if (navOptions.isEmpty) "NO NEARBY\nLOCATIONS"
    else
      navOptions.zipWithIndex
        .map { case (navOpt, index) =>
          val distance = Navigation.calculateDistance(
            currentImageData.coordinates,
            navOpt.coordinates
          )
          val formatter   = new DecimalFormat("0.0")
          val distanceStr = formatter.format(distance)
          val turnAngle = Navigation.calculateTurnAngle(
            currentImageData.compassAngle,
            currentImageData.coordinates,
            navOpt.coordinates
          )
          s"[${index + 1}] ${distanceStr}M ${angleToDirectionChar(turnAngle)}"
        }
        .mkString("\n")
  }

  def sequenceNavOptsList(
      backwardsOpt: Option[MapillaryImageId],
      forwardsOpt: Option[MapillaryImageId]
  ): String = {
    val fString =
      if (forwardsOpt.isDefined) "[F] FORWARDS ↑" else "[F] NOT AVAILABLE"
    val bString =
      if (backwardsOpt.isDefined) "[B] BACKWARDS ↓" else "[B] NOT AVAILABLE"

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
          s"[${index + 1}] ${guessOpt.name.toUpperCase}"
        }
        .mkString("\n")
      index = shuffledCountriesList.indexOf(correctCountry)
    } yield (str, index)
  }
}
