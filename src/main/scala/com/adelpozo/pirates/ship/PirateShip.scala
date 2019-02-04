package com.adelpozo.pirates.ship

import com.adelpozo.pirates.commons.{LogBook, Treasure}
import com.adelpozo.pirates.ports.Port

/**
  * Simple class with name information about Ship
  * @param name
  */
final case class ShipInfo(name: String)

/**
  * This class is about the pirate ship information
  *
  * @param shipInfo with the basic information as a Name of Ship
  * @param shipLogBook with the logBook of Ship
  * @param treasure with the gold and rum
  */
final case class PirateShip(shipInfo: ShipInfo, shipLogBook: Seq[LogBook], treasure: Treasure) {

  /**
    * This method adds some gold to hold
    * @param gold with the coins to save
    * @return the self instance
    */
  def addGold(gold: Int): PirateShip = {
    if (treasure == null)
      copy(shipInfo, shipLogBook, Treasure(gold, 0))
    else
      copy(shipInfo, shipLogBook, Treasure(gold, treasure.rum))
  }

  /**
    * This method adds some rum to hold
    * @param rum with the number of rum barrels to save
    * @return the self instance
    */
  def addRum(rum: Int): PirateShip = {
    if (treasure == null)
      copy(shipInfo, shipLogBook, Treasure(0, rum))
    else
      copy(shipInfo, shipLogBook, Treasure(treasure.gold, rum))
  }

  /**
    * This methods create new ship travel
    *
    * @param to with the next destiny
    * @return the self instance
    */
  def newTravel(to: Port): PirateShip = {
    require(to != null, "Bastard!, The captain need the destiny!!!")

    def validateTravelLogBook(to: Port): PirateShip = {
      shipLogBook.seq.last match {
        case l: LogBook if l.to == null && l.from == null =>
          copy(shipInfo, Seq(LogBook(to, null)))
        case l: LogBook if l.to == null && l.from != null && shipLogBook.seq.size == 1 =>
          copy(shipInfo, Seq(LogBook(shipLogBook.seq.last.from, to)))
        case l: LogBook if l.to != null && l.from != null =>
          copy(shipInfo, shipLogBook :+ LogBook(shipLogBook.seq.last.to, to))
        case l: LogBook if l.from == to && l.to == to =>
          throw new IllegalArgumentException("OUCH! Other night drunk in the port")
        case _ => throw new IllegalArgumentException("Release the kraken!!!")
      }
    }

    validateTravelLogBook(to)
  }

}
