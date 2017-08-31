package com.shadowsmind

import akka.actor.{ Actor, ActorLogging, Props }

import scala.concurrent.duration._

class SystemActor extends Actor with ActorLogging {

  import SystemActor._
  import context.dispatcher

  val runtime = Runtime.getRuntime
  val mb = 1024 * 1024

  context.system.scheduler.schedule(1.seconds, 10.seconds, self, ShowMemoryUsage)

  override def receive: Receive = {
    case ShowMemoryUsage ⇒
      log.debug("Memory usage: " + (runtime.totalMemory - runtime.freeMemory) / mb)
  }

}

object SystemActor {

  def props = Props(classOf[SystemActor])

  sealed trait SystemCommand
  case object ShowMemoryUsage extends SystemCommand

}

