package com.streetascii.cli

import com.streetascii.clients.mapillary.Models.MapillaryImageId
import com.streetascii.common.Models.Coordinates

object Models {
  case class ImageIdEntryArgs(imageId: MapillaryImageId)
  case class CoordinatesEntryArgs(coordinates: Coordinates)
  case class AddressEntryArgs(address: String)
  case class GuessingArgs() // Empty case class for the guessing command
}
