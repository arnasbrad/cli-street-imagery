package com.streetascii.guessinggame

import com.streetascii.clients.mapillary.Models.MapillaryImageId

import scala.util.Random

object CountryModels {
  case class GuesserLocation(id: MapillaryImageId, country: Country)

  trait Country {
    val name: String
  }

  case object Country {
    val values: List[Country] = List(
      UnitedKingdom,
      France,
      Germany,
      Belgium,
      Spain,
      Portugal,
      Italy,
      Ukraine,
      Lithuania,
      Estonia,
      Latvia,
      Poland,
      Greece,
      Hungary,
      Austria,
      Sweden,
      Bulgaria,
      Finland,
      Ireland,
      CzechRepublic,
      Denmark,
      Netherlands,
      Croatia,
      Luxembourg,
      Iceland,
      Cyprus,
      Malta
    )

    def randomPickedCountries(excludedCountry: Country): List[Country] = {
      val availableCountries =
        Country.values.filter(country => country != excludedCountry)

      val shuffledCountries = Random.shuffle(availableCountries)

      shuffledCountries.take(4)
    }

    case object UnitedKingdom extends Country {
      val name = "United Kingdom"
    }

    case object France extends Country {
      val name = "France"
    }

    case object Germany extends Country {
      val name = "Germany"
    }

    case object Belgium extends Country {
      val name = "Belgium"
    }

    case object Spain extends Country {
      val name = "Spain"
    }

    case object Portugal extends Country {
      val name = "Portugal"
    }

    case object Italy extends Country {
      val name = "Italy"
    }

    case object Ukraine extends Country {
      val name = "Ukraine"
    }

    case object Lithuania extends Country {
      val name = "Lithuania"
    }

    case object Estonia extends Country {
      val name = "Estonia"
    }

    case object Latvia extends Country {
      val name = "Latvia"
    }

    case object Poland extends Country {
      val name = "Poland"
    }

    case object Greece extends Country {
      val name = "Greece"
    }

    case object Hungary extends Country {
      val name = "Hungary"
    }

    case object Austria extends Country {
      val name = "Austria"
    }

    case object Sweden extends Country {
      val name = "Sweden"
    }

    case object Bulgaria extends Country {
      val name = "Bulgaria"
    }

    case object Finland extends Country {
      val name = "Finland"
    }

    case object Ireland extends Country {
      val name = "Ireland"
    }

    case object CzechRepublic extends Country {
      val name = "Czech Republic"
    }

    case object Denmark extends Country {
      val name = "Denmark"
    }

    case object Netherlands extends Country {
      val name = "Netherlands"
    }

    case object Croatia extends Country {
      val name = "Croatia"
    }

    case object Luxembourg extends Country {
      val name = "Luxembourg"
    }

    case object Iceland extends Country {
      val name = "Iceland"
    }

    case object Cyprus extends Country {
      val name = "Cyprus"
    }

    case object Malta extends Country {
      val name = "Malta"
    }

    case object Norway extends Country {
      val name = "Norway"
    }
  }
}
