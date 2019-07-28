package com.jing.akkahello

import akka.actor.{Actor, ActorRef, ActorSystem, Props}


class Helloworld extends Actor {
  override def receive: Receive = {
    case "hello" => println("hello")
    case "scala" => println("scala")
    case "hadoop" => println("hadoop")
  }
}


object Helloworld {
  private val helloSystem = ActorSystem("hello")
  private val actorRef: ActorRef = helloSystem.actorOf(Props[Helloworld])

  def main(args: Array[String]): Unit = {
    actorRef ! "hello"
    actorRef ! "scala"
    actorRef ! "hadoop"
  }
}
