package com.streetascii.guessinggame

import com.streetascii.clients.mapillary.Models.MapillaryImageId
import com.streetascii.guessinggame.CountryModels.{Country, GuesserLocation}

object GuessingLocations {
  val locations = List(
    GuesserLocation(
      MapillaryImageId("1688256144933335"),
      Country.UnitedKingdom
    ),
    GuesserLocation(MapillaryImageId("287684379512568"), Country.France),
    GuesserLocation(MapillaryImageId("580734819572453"), Country.Spain),
    GuesserLocation(MapillaryImageId("305098338039383"), Country.Portugal),
    GuesserLocation(MapillaryImageId("820862425187499"), Country.Estonia),
    GuesserLocation(MapillaryImageId("1107931448003280"), Country.Latvia),
    GuesserLocation(MapillaryImageId("2589275534702797"), Country.Lithuania),
    GuesserLocation(MapillaryImageId("4148188075240535"), Country.Poland),
    GuesserLocation(MapillaryImageId("135609815260274"), Country.Italy),
    GuesserLocation(MapillaryImageId("2459116864441145"), Country.Greece),
    GuesserLocation(MapillaryImageId("731714614943735"), Country.Austria)
  )
}
