# Alvaro Del Pozo Cuenca

Leve: Medium

This is the propousal challenge:

## BrethrenCourt

The Brethren Court, governed by pirates, wants you to design a system capable of keeping the
Arrival and Departure record as well as the state of the stock of the two most precious goods;
Barrels of Rum and Gold Coins, in all the pirate ports of the world updated.

The travel data they need to record is:

• Event Type (Arrival/Departure)
• Port (ship x arrival to/departure from port y)
• Content the ship’s hold ( n Barrels of Rum, m Gold Coins)

With this system they want to be able to:

• Know all the travel history of a ship or port and filter it by event type.
• Know the current stock of a Port.
• Reconstruct the diary log of a port of the designed system in the event the information
should be stollen.

---

For this challenged I have used TDD as methodology to design an approach of solution.

You can run all test as follows:

```
sbt test
```

## Register Service

The main class is __TravelDataService__. Initially, this service has a Empty information about travels.

You can create an initial instance as follows:

``
TravelDataService(List.empty[TravelInfo])
``

After that, you can to register some moves, for example

``
service.registerEventShip(myShip, Arrival)
``

Once moves added, you can ask person responsible of book registry like this

- All Ships moves: 
``
service.tellMeAboutShipsTravelled
``

- Specific ship and event:
``
service.tellMeAboutConcreteShip(empress.shipInfo, Arrival)
``

- About moves in a specific port:
``
service.tellMeAboutPortEvents(Port("Shanghái"), Arrival)
``

- And of course, ask for the treasure:
``
service.tellMeAboutTheCurrentStock(Port("Portugal"))
``

The test about this service is __TravelDataServiceSpec__

Note that first of all, we need to create ships and some travel in order to have a little information to play.
 
## Ships

 __PirateShip__ is the class with the ship information as name of ship, LogBook about the travels and obviously the most important thing, the treasure!!!
 
 You can create a simple ship as follows:
 
 ``
 PirateShip(ShipInfo("SimpleBoat"), Seq(LogBook(Port("MyHouse"), null)), Treasure(0,0))
 ``
 
 You can to add new travel as follows:
 
 ``
 val myShipMoving = myShip.newTravel(Port("Pinneapple"))
 ``
 
 Maybe, if in a travel they have good look they loot some gold and rum from other ship
 
 So you can add to hold gold and rum as follows:
 
 ``
  val myShipIsRich = myShip.addRum(3000).addGold(200)
 `` 
 
 The test of ships is __PirateShipSpec__
