package com.streetascii.navigation

object Models {
  sealed trait NavigationType

  object NavigationType {
    case object RadiusBased extends NavigationType

    case object SequenceBased extends NavigationType

    case object SequenceBasedFast extends NavigationType
  }
}
