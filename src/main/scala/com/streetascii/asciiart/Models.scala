package com.streetascii.asciiart

import com.streetascii.clients.mapillary.Models.MapillaryImageId
import com.streetascii.common.Models.Coordinates

object Models {
  case class ImageWidth(value: Int)

  case class ImageHeight(value: Int)

  case class HexImage(
      hexStrings: Array[String],
      width: ImageWidth,
      height: ImageHeight
  )

  case class ImageInfo(
      hexImage: HexImage,
      imageId: MapillaryImageId,
      coordinates: Coordinates
  )

  case class RGB(r: Int, g: Int, b: Int)

  case class ColoredPixels(
      grayscaleDecimals: Array[Array[String]],
      colors: Array[Array[RGB]]
  )
}
