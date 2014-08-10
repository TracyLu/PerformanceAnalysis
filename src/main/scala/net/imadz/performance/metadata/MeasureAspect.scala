package net.imadz.performance.metadata

/**
 * Created by Scala on 14-8-9.
 */
class MeasureAspect{
}

case class CPU() extends MeasureAspect{
}
case class Memory() extends MeasureAspect
case class Network() extends MeasureAspect
case class DiskIO() extends MeasureAspect
