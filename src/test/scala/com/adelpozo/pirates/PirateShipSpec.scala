package com.adelpozo.pirates

import java.lang.{IllegalArgumentException => IAE}

import com.adelpozo.pirates.commons.{LogBook, Treasure}
import com.adelpozo.pirates.ports.Port
import com.adelpozo.pirates.ship.{PirateShip, ShipInfo}
import org.scalatest.{Matchers, WordSpec}

class PirateShipSpec extends WordSpec with Matchers {

  "PirateShip" should {
    "Create a simple ship with a initial travel " in {
      val ship = PirateShip(ShipInfo("Sponje Bob Boat"), Seq(LogBook(Port("Pinneapple"), null)), null)
      assert(ship != null)
    }

    "Throw IllegalArgumentException because the new travel is null " in {
      val ship = PirateShip(ShipInfo("Sponje Bob Boat"), Seq(LogBook(Port("Pinneapple"), null)), null)
      an[IAE] should be thrownBy ship.newTravel(null)
    }

    "Create ship initialization without origin. Ship has not origin port travel and the added new travel as from " in {
      val ship = PirateShip(ShipInfo("Sponje Bob Boat"), Seq(LogBook(null, null)), null)
        .newTravel(Port("Pinneapple"))
      ship.shipLogBook.seq.last.from.name should be("Pinneapple")
    }

    "Create the first travel " in {
      val ship = PirateShip(ShipInfo("Sponje Bob Boat"), Seq(LogBook(Port("Pinneapple"), null)), null)
        .newTravel(Port("Krusty Krab"))

      assert(ship != null)
      ship.shipLogBook.seq.size should be(1)
      ship.shipLogBook.seq.last.from.name should be("Pinneapple")
      ship.shipLogBook.seq.last.to.name should be("Krusty Krab")
    }

    "Add new travel " in {
      val ship = PirateShip(ShipInfo("Sponje Bob Boat"), Seq(LogBook(Port("Pinneapple"), null)), null)
        .newTravel(Port("Krusty Krab"))
        .newTravel(Port("Madrid"))
      assert(ship != null)
      ship.shipLogBook.seq.size should be(2)
      ship.shipLogBook.seq.last.from.name should be("Krusty Krab")
      ship.shipLogBook.seq.last.to.name should be("Madrid")
    }

    "Add some gold into ship hold with Treasure null " in {
      val ship = PirateShip(ShipInfo("Sponje Bob Boat"), Seq(LogBook(Port("Pinneapple"), null)), null)
        .addGold(3000)
      assert(ship != null)
      ship.treasure.gold should be(3000)
    }

    "Add some rum into barrels with Treasure null " in {
      val ship = PirateShip(ShipInfo("Sponje Bob Boat"), Seq(LogBook(Port("Pinneapple"), null)), null)
        .addRum(3)
      assert(ship != null)
      ship.treasure.rum should be(3)
    }

    "Suffer a looting on Treasure!! " in {
      val ship = PirateShip(ShipInfo("Sponje Bob Boat"), Seq(LogBook(Port("Pinneapple"), null)), Treasure(10000, 400))
        .addRum(0)
        .addGold(0)
      assert(ship != null)
      ship.treasure.rum should be(0)
      ship.treasure.gold should be(0)
    }
  }
}
