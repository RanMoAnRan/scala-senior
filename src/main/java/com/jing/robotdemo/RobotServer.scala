package com.jing.robotdemo

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * 服务端
  */
class RobotServer extends Actor {
  override def receive: Receive = {
    case "start" => println("服务器端已经启动。。。。。。。。。")
    case ClientMessage(msg) =>{
      msg match {
        case "你叫什么" =>sender() ! ServerMessage("我不告诉你")
        case "你是男是女" =>sender() ! ServerMessage("你来了就知道了")
        case "你多大了" =>sender() ! ServerMessage("你猜！")
        case "你有男票吗" =>sender() ! ServerMessage("mimi")
        case "你在干嘛" =>sender() ! ServerMessage("和你聊天呀")
        case _ => sender() ! ServerMessage("没听懂你的意思")
      }
    }
  }
}


object RobotServer extends App {
  val host: String = "127.0.0.1"
  val port: Int = 8888
  val config = ConfigFactory.parseString(
    s"""
       |akka.actor.provider="akka.remote.RemoteActorRefProvider"
       |akka.remote.netty.tcp.hostname=$host
       |akka.remote.netty.tcp.port=$port
     """.stripMargin)

  private val systemSystem = ActorSystem("system", config)
  private val actorRef: ActorRef = systemSystem.actorOf(Props[RobotServer], "test")
  actorRef ! "start"
}

