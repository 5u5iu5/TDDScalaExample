package com.adelpozo.pirates

import com.adelpozo.pirates.commons.{LogBook, Treasure}
import com.adelpozo.pirates.events.Events.{Arrival, Departure, Event}
import com.adelpozo.pirates.ports.Port
import com.adelpozo.pirates.ship.{PirateShip, ShipInfo}

/**
  * This class is the service responsible of registers pirates events
  *
  * @param travelInfo with the information about Travels
  */
final case class TravelDataService(travelInfo: Seq[TravelInfo]) {

  /**
    * This method is the ship pirates event register.
    *
    * @param ship: With the ship information.
    * @param typeEvent: The event, could be Arrival or Departure
    * @return the instance of this service.
    */
  def registerEventShip(ship: PirateShip, typeEvent: Event): TravelDataService = {
    require(ship != null || ship.shipInfo != null, "Ghost boats not supported, rat!!")
    require(typeEvent != null, "Arrival or Departure. But I need something!! ")
    require(ship.shipLogBook != null, "Damn cabin boy, we need the logbook")
    copy(travelInfo :+ createTravelInfoByJourney(ship, typeEvent))
  }

  /**
    * This method count the port stock
    * @param port the specific port
    * @return a tuple of two, with gold  and rum.
    */
  def tellMeAboutTheCurrentStock(port: Port): (Int, Int) = (countTotalGoldInPort(port), countTotalRumBarrelInPort(port))

  /**
    * This method retrieves all information about the ships travels
    * @return a Sequence of events
    */
  def tellMeAboutShipsTravelled: Seq[String] = travelInfo.seq.map(printTheSchedule)

  /**
    * This method retrieves the information about the specific ship and concrete event.
    * @param shipInfo with the basic ship information
    * @param event with the event, could be Arrival or Departure
    * @return a Sequence of events
    */
  def tellMeAboutConcreteShip(shipInfo: ShipInfo, event: Event): Seq[String] =
    travelInfo.seq
      .filter(_.registerShip.shipInfo == shipInfo)
      .filter(_.event == event).map(printTheSchedule)

  /**
    * This method retrieves the information by port and concrete Event
    * @param port with the Port
    * @param event with the event, could be Arrival or Departure
    * @return a Sequence of events
    */
  def tellMeAboutPortEvents(port: Port, event: Event): Seq[String] =
    travelInfo.seq
      .filter(_.port == port)
      .filter(_.event == event)
      .map(printTheSchedule)

  /**
    * This method counts total gold and rum by Port
    * @param port with the port
    * @return total gold
    */
  private def countTotalGoldInPort(port: Port) = travelInfo
    .filter(_.port == port)
    .filter(_.event == Arrival)
    .foldRight(0)((info, acc) => {
      acc + info.treasure.gold
    })

  /**
    * This method counts total rum barrel by Port
    * @param port with the port
    * @return total Rum barrel
    */
  private def countTotalRumBarrelInPort(port: Port) = travelInfo
    .filter(_.port == port)
    .filter(_.event == Arrival)
    .foldRight(0)((info, acc) => {
      acc + info.treasure.rum
    })

  /**
    * This method retrieves the journey from total ships registered.
    * @param shipLogBook: with the schedule of travels
    * @return a List with some explanation about the travels
    */
  private def whereHasTravelledAll(shipLogBook: Seq[LogBook]): List[String] = {
    shipLogBook.map(travelIntrerpeter).toList
  }

  /**
    * This methods create a TravelInfo object
    * @param ship with the Pirate Ship
    * @param typeEvent with the kind of event
    * @return a Travel Info object
    */
  private def createTravelInfoByJourney(ship: PirateShip, typeEvent: Event): TravelInfo = {
    typeEvent match {
      case Arrival if ship.shipLogBook.last.to != null => TravelInfo(ship, Arrival, ship.shipLogBook.seq.last.to, ship.treasure)
      case Arrival if ship.shipLogBook.last.to == null => TravelInfo(ship, Arrival, ship.shipLogBook.seq.last.from, ship.treasure)
      case Departure => TravelInfo(ship, Departure, ship.shipLogBook.seq.last.from, ship.treasure)
    }
  }

  /**
    * This method only return a text with information about the travels
    * @param info: with the travel information
    * @return the string with the complete information
    */
  private def printTheSchedule(info: TravelInfo): String = {
    s"The ship ${info.registerShip.shipInfo.name} has sailed: ${whereHasTravelledAll(info.registerShip.shipLogBook).mkString}.. " +
      s"with treasure ${info.treasure.gold} gold coins and ${info.treasure.rum} of Rum"
  }

  /**
    * This method is a interpreted of book
    * @param book with the LogBook Ship
    * @return a String with the concrete type of literals
    */
  private def travelIntrerpeter(book: LogBook): String = {
    book match {
      case LogBook(from, null) => s"Started in ${from.name}"
      case LogBook(from, to) => s"Ship moves from ${from.name} to ${to.name}"
    }
  }
}

/**
  * This class is the main domain object with the information needed in register
  * @param registerShip with the pirate ship basic information
  * @param event Kind of event, could be Arrival or Departure
  * @param port With the port
  * @param treasure with the gold and rum
  */
final case class TravelInfo(registerShip: PirateShip, event: Event, port: Port, treasure: Treasure)
