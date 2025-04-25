package com.streetascii.guessinggame

import cats.effect.IO
import cats.effect.std.Random
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
    GuesserLocation(MapillaryImageId("731714614943735"), Country.Austria),
    GuesserLocation(MapillaryImageId("1208230263816261"), Country.Malta),
    GuesserLocation(MapillaryImageId("304924917820437"), Country.Malta),
    GuesserLocation(MapillaryImageId("180811743908406"), Country.Cyprus),
    GuesserLocation(MapillaryImageId("133382588813611"), Country.Cyprus),
    GuesserLocation(MapillaryImageId("142785287856963"), Country.Ireland),
    GuesserLocation(MapillaryImageId("305630367954048"), Country.Ireland),
    GuesserLocation(MapillaryImageId("139303768181070"), Country.Finland),
    GuesserLocation(MapillaryImageId("223043279739419"), Country.Finland),
    GuesserLocation(MapillaryImageId("4021031041325582"), Country.Norway),
    GuesserLocation(MapillaryImageId("325072856070839"), Country.Norway),
    GuesserLocation(MapillaryImageId("1422546425099886"), Country.Sweden),
    GuesserLocation(MapillaryImageId("2884572628376803"), Country.Sweden),
    GuesserLocation(MapillaryImageId("434518845092520"), Country.Latvia),
    GuesserLocation(MapillaryImageId("465916271156606"), Country.Lithuania),
    GuesserLocation(MapillaryImageId("897739650771327"), Country.Hungary),
    GuesserLocation(
      MapillaryImageId(
        "160900319252801" // TODO: this is 360, change to normal
      ),
      Country.CzechRepublic
    ),
    GuesserLocation(
      MapillaryImageId("1087518123060280"),
      Country.CzechRepublic
    ),
    GuesserLocation(MapillaryImageId("528763121834493"), Country.Iceland),
    GuesserLocation(MapillaryImageId("534169211078386"), Country.Iceland),
    GuesserLocation(MapillaryImageId("190103909628976"), Country.Poland)
  )

  def getRandomLocation: IO[GuesserLocation] = {
    for {
      index <- Random
        .scalaUtilRandom[IO]
        .flatMap(_.betweenInt(0, locations.length - 1))
    } yield locations(index)
  }
}
