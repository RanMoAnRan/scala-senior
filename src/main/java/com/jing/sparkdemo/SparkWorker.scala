package com.jing.sparkdemo

import java.util.UUID

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

class SparkWorker extends Actor {
  private val workid: String = UUID.randomUUID().toString
  val masterUrl = "akka.tcp://sparkMaster@127.0.0.1:8877/user/Master"
  var masteractorref: ActorSelection = _

  override def preStart(): Unit = {
    masteractorref = context.actorSelection(masterUrl)
  }

  override def receive: Receive = {
    case "start" => {
      println("worker已经启动。。。")
      masteractorref ! RegisterWorkerInfo(workid, 4, 32 * 1024)
    }
    case RegisteredWoker => {
      println("worker注册成功")
      import context.dispatcher //使用调度器的时候，必须导入dispatcher
      //启动一个调度器，定期向Master汇报（心跳）
      context.system.scheduler.schedule(0 millis,1500 millis,self,SendHeartBeat)
    }

    case SendHeartBeat=>{
      masteractorref ! HeartBeat(workid) //把Worker的id发给Master
    }
  }
}


object SparkWorker extends App {
  val host: String = "127.0.0.1"
  val port: Int = 2222
  val config = ConfigFactory.parseString(
    s"""
       |akka.actor.provider="akka.remote.RemoteActorRefProvider"
       |akka.remote.netty.tcp.hostname=$host
       |akka.remote.netty.tcp.port=$port
     """.stripMargin)

  private val systemSystem = ActorSystem("sparkworker", config)
  private val workerActorRef: ActorRef = systemSystem.actorOf(Props[SparkWorker], "worker-1")
  workerActorRef ! "start"
}
