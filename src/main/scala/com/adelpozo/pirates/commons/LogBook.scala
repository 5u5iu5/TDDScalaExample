package com.adelpozo.pirates.commons

import com.adelpozo.pirates.ports.Port

/**
  * This class saves all information about the ships moves.
  *
  * @param from: origin port
  * @param to: destiny port
  */
final case class LogBook(from: Port, to: Port)
