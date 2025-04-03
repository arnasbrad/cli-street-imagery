package asciiart

object Models {
  sealed trait AlgorithmConfig

  case class LuminanceConfig(
      input: Array[Array[String]],
      charset: Charset
  ) extends AlgorithmConfig

  case class EdgeDetectionConfig(
      input: Array[Array[String]],
      charset: Charset,
      invert: Boolean = true
  ) extends AlgorithmConfig

  case class BrailleConfig(
      input: Array[Array[String]],
      charset: Charset,
      threshold: Int = 118
  ) extends AlgorithmConfig

  case class ImageWidth(value: Int)

  case class ImageHeight(value: Int)

  case class HexImage(
      hexStrings: Array[String],
      width: ImageWidth,
      height: ImageHeight
  )
}
