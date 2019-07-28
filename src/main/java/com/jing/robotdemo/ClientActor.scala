package com.jing.robotdemo

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

/**
  * 客户端
  */
class ClientActor extends Actor {
  var serverActorRef: ActorSelection = _

  override def preStart() = {
    serverActorRef = context.actorSelection("akka.tcp://system@127.0.0.1:8888/user/test")
  }

  override def receive: Receive = {
    case "start" => println("客户端已启动。。。。")
    case msg: String => {
      serverActorRef ! ClientMessage(msg)
    }
    case ServerMessage(msg) => {
      println("服务器消息：" + msg)
    }
  }
}


object ClientActor extends App {
  val host: String = "127.0.0.1"
  val port: Int = 9999
  val config = ConfigFactory.parseString(
    s"""
       |akka.actor.provider="akka.remote.RemoteActorRefProvider"
       |akka.remote.netty.tcp.hostname=$host
       |akka.remote.netty.tcp.port=$port
     """.stripMargin)

  private val systemSystem = ActorSystem("client", config)
  private val clientActorRef: ActorRef = systemSystem.actorOf(Props[ClientActor], "client")
  clientActorRef ! "start"

  while (true) {
    val line = StdIn.readLine()
    clientActorRef ! line
  }
}

