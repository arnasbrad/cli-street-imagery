package com.streetascii.cli

import com.streetascii.clients.mapillary.Models.MapillaryImageId

object Models {
  case class ImageIdEntryArgs(imageId: MapillaryImageId)
  case class GuessingArgs() // Empty case class for the guessing command
}
