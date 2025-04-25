package com.streetascii.cli

import cats.data.Validated
import com.monovore.decline._
import com.streetascii.cli.Models.{
  AddressEntryArgs,
  CoordinatesEntryArgs,
  GuessingArgs,
  ImageIdEntryArgs
}
import com.streetascii.clients.mapillary.Models.MapillaryImageId
import com.streetascii.common.Models.Coordinates

import scala.util.Try

object Cli {
  private val imageIdArg =
    Opts
      .argument[String](metavar = "IMAGE_ID")
      .map(str => MapillaryImageId(str))

  private val coordinatesArg =
    Opts
      .argument[String](metavar = "LAT,LON")
      .mapValidated(str => {
        val coordsAttempt = Try {
          val parts = str.split(",")
          val lat   = parts(0).toDouble
          val lng   = parts(1).toDouble
          Coordinates.create(lat, lng)
        }

        coordsAttempt match {
          case scala.util.Success(Right(coords)) =>
            Validated.valid(coords)
          case scala.util.Success(Left(error)) =>
            Validated.invalidNel(s"Invalid coordinates: $error")
          case scala.util.Failure(exception) =>
            Validated.invalidNel(
              s"Could not parse coordinates: ${exception.getMessage}"
            )
        }
      })

  private val addressArg =
    Opts
      .argument[String](metavar = "ADDRESS")

  val idCommand: Opts[ImageIdEntryArgs] = Opts.subcommand(
    Command(name = "id", header = "Start with a Mapillary image ID")(
      imageIdArg.map(ImageIdEntryArgs)
    )
  )

  val coordinatesCommand: Opts[CoordinatesEntryArgs] = Opts.subcommand(
    Command(name = "coords", header = "Start with geographic coordinates")(
      coordinatesArg.map(CoordinatesEntryArgs)
    )
  )

  val addressCommand: Opts[AddressEntryArgs] = Opts.subcommand(
    Command(name = "address", header = "Start with a street address")(
      addressArg.map(AddressEntryArgs)
    )
  )

  val guessingCommand: Opts[GuessingArgs] = Opts.subcommand(
    Command(name = "guessing", header = "Start in guessing mode")(
      Opts(GuessingArgs())
    )
  )
}
