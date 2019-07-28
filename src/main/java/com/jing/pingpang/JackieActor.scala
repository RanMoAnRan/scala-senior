package com.jing.pingpang

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

class JackieActor(daren: ActorRef) extends Actor {
  override def receive: Receive = {
    case "start" => {
      println("jackie  start...")
      daren ! "发球"
    }
    case "接球" =>{
      println("daren接了一个好球")
    }
  }
}

class DarengeActor extends Actor {
  override def receive: Receive = {
    case "start" => {
      println("darenge ...start")
    }
    case "发球" =>{
      println("jackie发了一个球")
      sender() ! "接球"
    }
  }
}

object Pingpang extends App{
  private val actorsystem = ActorSystem("pingpang")
  private val daren: ActorRef = actorsystem.actorOf(Props[DarengeActor],"daren")
  private val jackie: ActorRef = actorsystem.actorOf(Props(new JackieActor(daren)),"jackie")
  daren ! "start"
  jackie ! "start"

}
