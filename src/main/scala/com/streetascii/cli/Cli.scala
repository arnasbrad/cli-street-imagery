package com.streetascii.cli

import com.monovore.decline._
import com.streetascii.cli.Models.{GuessingArgs, ImageIdEntryArgs}
import com.streetascii.clients.mapillary.Models.MapillaryImageId

object Cli {
  // Fixed parameter name to use kebab-case without spaces
  private val imageId =
    Opts
      .option[String](
        "image-id", // Changed from "Image Id" to "image-id"
        help = "Mapillary image ID"
      )
      .map(str => MapillaryImageId(str))

  val idCommand: Opts[ImageIdEntryArgs] = Opts.subcommand(
    Command(name = "id", header = "Start with ID")(
      imageId.map(ImageIdEntryArgs)
    )
  )

  // Adding the guessing command with no parameters
  val guessingCommand: Opts[GuessingArgs] = Opts.subcommand(
    Command(name = "guessing", header = "Start in guessing mode")(
      Opts(GuessingArgs()) // Use Opts.apply with the default instance
    )
  )
}
