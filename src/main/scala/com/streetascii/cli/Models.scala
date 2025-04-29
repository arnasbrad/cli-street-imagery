package com.streetascii.cli

import com.streetascii.clients.mapillary.Models.MapillaryImageId
import com.streetascii.common.Models.Coordinates

object Models {
  case class ImageIdEntryArgs(imageId: MapillaryImageId, configPath: String)
  case class CoordinatesEntryArgs(coordinates: Coordinates, configPath: String)
  case class AddressEntryArgs(address: String, configPath: String)
  case class GuessingArgs(configPath: String)
}
