package com.adelpozo.pirates.events

/**
  * Objects with the events
  */
object Events {
  sealed trait Event
  case object Arrival extends Event
  case object Departure extends Event
}
