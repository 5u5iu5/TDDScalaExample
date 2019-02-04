package com.adelpozo.pirates

import java.lang.{IllegalArgumentException => IAE}

import com.adelpozo.pirates.commons.{LogBook, Treasure}
import com.adelpozo.pirates.events.Events._
import com.adelpozo.pirates.ports.Port
import com.adelpozo.pirates.ship.{PirateShip, ShipInfo}
import org.scalatest.{Matchers, WordSpec}

class TravelDataServiceSpec extends WordSpec with Matchers {

  val travelDataService: TravelDataService = TravelDataService(List.empty[TravelInfo])

  val blackPearl = PirateShip(ShipInfo("Black Pearl"), Seq(LogBook(Port("Shanghái"), null)), Treasure(0, 0))
  val hmsInterceptor = PirateShip(ShipInfo("HMS Interceptor"), Seq(LogBook(Port("Florida"), null)), Treasure(56000, 32))
  val empress = PirateShip(ShipInfo("Empress"), Seq(LogBook(Port("Portugal"), null)), Treasure(3000, 100))
  val venganza = PirateShip(ShipInfo("Venganza"), Seq(LogBook(Port("Puerto Rico"), null)), Treasure(0, 0))
  val dyingGull = PirateShip(ShipInfo("Dying Gull"), Seq(LogBook(Port("Cuba"), Port("Portugal"))), Treasure(3450, 112))
  val ghostShip = PirateShip(ShipInfo("Ghost Ship"), Seq(LogBook(Port("Portugal"), null)), Treasure(1000, 112))
  val blackPearlWithGoldAnd = PirateShip(ShipInfo("Black Pearl"), Seq(LogBook(Port("Shanghái"), Port("Singapur"))), Treasure(0, 0))
  val noLogBookBoat = PirateShip(ShipInfo("NoPortBoat"), null, Treasure(0, 0))

  "TravelDataService" should {
    "Not Register new incoming pirate ship with null logbook and throw an IllegalArgumentException " in {
      an[IAE] should be thrownBy travelDataService.registerEventShip(noLogBookBoat, Arrival)
    }

    "Register new arrival pirate ship into first port without gold and rum. Only the first travel of Black Pearl" in {
      val travel = travelDataService.registerEventShip(blackPearl, Arrival)
      val logBook: Seq[TravelInfo] = travel.travelInfo
      logBook.seq.size should be(1)
      assert(logBook.seq.exists(travel => travel.registerShip == blackPearl))
      assert(logBook.seq.exists(travel => travel.event == Arrival))
      assert(logBook.seq.exists(travel => travel.treasure.gold == 0 || travel.treasure.rum == 0))
    }

    "Register new arrival pirate ship into first port with a lot of gold and rum. Only the first travel of Black Pearl" in {
      val travel = travelDataService.registerEventShip(blackPearl, Arrival)
      val logBook: Seq[TravelInfo] = travel.travelInfo
      logBook.seq.size should be(1)
      assert(logBook.seq.exists(travel => travel.registerShip == blackPearl))
      assert(logBook.seq.exists(travel => travel.event == Arrival))
      assert(logBook.seq.exists(travel => travel.treasure.gold == 0 || travel.treasure.rum == 0))
    }

    "Register an arrival and departure. And ship is moving to other port with some gold and rum. " in {
      val travelDeparture: TravelDataService = mockTwoBlackPearlMoves
      val logBook: Seq[TravelInfo] = travelDeparture.travelInfo
      logBook.seq.size should be(2)
      val last = logBook.seq.last
      last.registerShip.shipInfo.name should be("Black Pearl")
      last.port.name should be("Shanghái")
      last.event should be(Departure)
      last.treasure.gold should be(3000)
      last.treasure.rum should be(400)
    }

    "Register an arrival and departure of same ship and arrival of different ship. " in {
      val travelBlackPearlTwoMoves: TravelDataService = mockTwoBlackPearlMoves
      val travelWithHMSInterceptor: TravelDataService = travelBlackPearlTwoMoves.registerEventShip(hmsInterceptor, Arrival)
      val logBook: Seq[TravelInfo] = travelWithHMSInterceptor.travelInfo
      logBook.size should be(3)
      assert(logBook.contains(TravelInfo(PirateShip(ShipInfo("Black Pearl"), List(LogBook(Port("Shanghái"), null)), Treasure(0, 0)), Arrival, Port("Shanghái"), Treasure(0, 0))))
      assert(logBook.contains(TravelInfo(PirateShip(ShipInfo("Black Pearl"), List(LogBook(Port("Shanghái"), Port("Cuba"))), Treasure(3000, 400)), Departure, Port("Shanghái"), Treasure(3000, 400))))
      assert(logBook.contains(TravelInfo(PirateShip(ShipInfo("HMS Interceptor"), List(LogBook(Port("Florida"), null)), Treasure(56000, 32)), Arrival, Port("Florida"), Treasure(56000, 32))))
    }

    "Register a lots of movements from different ships but only I have the register of a specific ship. " in {
      val travelBlackPearlTwoMoves: TravelDataService = mockTwoBlackPearlMoves
      val travelWithHMSInterceptor: TravelDataService = travelBlackPearlTwoMoves.registerEventShip(hmsInterceptor, Arrival)
      val travelWithEmpress: TravelDataService = mockTwoEmpressMoves(travelWithHMSInterceptor)
      val travelVenganza: TravelDataService = travelWithEmpress.registerEventShip(venganza, Arrival)
      val listOfVenfanzaArrivals = travelVenganza.tellMeAboutConcreteShip(empress.shipInfo, Arrival)
      listOfVenfanzaArrivals.seq.mkString should be("The ship Empress has sailed: Started in Portugal.. with treasure 3000 gold coins and 100 of Rum")
      val listOfVenfanzaDeparture = travelVenganza.tellMeAboutConcreteShip(empress.shipInfo, Departure)
      listOfVenfanzaDeparture.seq.mkString should be("The ship Empress has sailed: Ship moves from Portugal to Brazil.. with treasure 3000 gold coins and 100 of Rum")
    }

    "Register a lots of movements from different ships but only I have the register of a specific port. " in {
      val travelBlackPearlTwoMoves: TravelDataService = mockTwoBlackPearlMoves
      val travelWithHMSInterceptor: TravelDataService = travelBlackPearlTwoMoves.registerEventShip(hmsInterceptor, Arrival)
      val travelWithEmpress: TravelDataService = mockTwoEmpressMoves(travelWithHMSInterceptor)
      val travelVenganza: TravelDataService = travelWithEmpress.registerEventShip(venganza, Arrival)
      val listPortsMovements = travelVenganza.tellMeAboutPortEvents(Port("Shanghái"), Arrival)
      println(travelVenganza.travelInfo.seq.toString().mkString)
      listPortsMovements.seq.mkString should be("The ship Black Pearl has sailed: Started in Shanghái.. with treasure 0 gold coins and 0 of Rum")
    }

    "Register a lots of movements from different ships about specific port and now we want sum all gold and rum. " in {
      val travelBlackPearlTwoMoves: TravelDataService = mockTwoBlackPearlMoves
      val travelWithHMSInterceptor: TravelDataService = travelBlackPearlTwoMoves.registerEventShip(hmsInterceptor, Arrival)
      val travelWithEmpress: TravelDataService = mockTwoEmpressMoves(travelWithHMSInterceptor)
      val travelVenganza: TravelDataService = travelWithEmpress.registerEventShip(venganza, Departure)
      val venganzaMoves = venganza.newTravel(Port("Portugal"))
      val vengazaMovedWithGoldAndRum = venganzaMoves.addGold(3240).addRum(223)
      val finalTravel: TravelDataService = travelVenganza
        .registerEventShip(vengazaMovedWithGoldAndRum, Arrival)
        .registerEventShip(ghostShip, Arrival)
        .registerEventShip(dyingGull, Departure)
      val totalGoldAndRumInPort = finalTravel.tellMeAboutTheCurrentStock(Port("Portugal"))
      totalGoldAndRumInPort._1 should be(7240)
      totalGoldAndRumInPort._2 should be(435)
    }
  }


  private def mockTwoEmpressMoves(travelWithHMSInterceptor: TravelDataService) = {
    val travelWithEmpress: TravelDataService = travelWithHMSInterceptor.registerEventShip(empress, Arrival)
    val empressMoving = empress.newTravel(Port("Brazil"))
    val travelWithEepressMoved: TravelDataService = travelWithEmpress.registerEventShip(empressMoving, Departure)
    travelWithEepressMoved
  }

  private def mockTwoBlackPearlMoves = {
    val travelArrival = travelDataService.registerEventShip(blackPearl, Arrival)
    val blackPearlMoving = blackPearl.newTravel(Port("Cuba"))
    val blackPearlWithGold = blackPearlMoving.addGold(3000).addRum(400)
    val travelDeparture = travelArrival.registerEventShip(blackPearlWithGold, Departure)
    travelDeparture
  }
}
