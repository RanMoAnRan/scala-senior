package com.jing.sparkdemo

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.collection.mutable

class SparkMaster extends Actor {
  val map = mutable.HashMap[String, WorkerInfo]()

  override def receive: Receive = {
    case "start" => println("sparkmaster已经启动。。。")
    case RegisterWorkerInfo(wid, core, ram) => {
      if (!map.contains(wid)) {
        val workerInfo = new WorkerInfo(wid, core, ram)
        map.put(wid, workerInfo)
        println("编号worker" + wid + "开始注册")
        sender() ! RegisteredWoker
      }
    }

    case HeartBeat(wkid) => {
      val workerInfo = map(wkid)
      //修改心跳时间
      workerInfo.lastHeartBeatTIme = System.currentTimeMillis()
      println(wkid + "还活着"+Math.random())
    }

    case CheckTimeOutWorker => {
      //接收检查Worker超时的消息
      import context.dispatcher //使用调度器的时候，必须导入dispatcher
      //定期检查超时的Worker
      context.system.scheduler.schedule(0 millis, 6000 millis, self, RemoveTimeOutWorker)
    }
    case RemoveTimeOutWorker => {
      //接收删除超时Worker的消息
      //删除超时的Worker信息（从集合中删除）
      val workerInfos = map.values //获取所有的Worker信息
      val currentTime = System.currentTimeMillis()
      //过滤超时的Worker
      workerInfos
        .filter(workinfo =>
          //拿当前的系统时间 - 最后一次心跳时间 > 3000  (认为超时)
          currentTime - workinfo.lastHeartBeatTIme > 3000)
        .foreach(wk => {
          map.remove(wk.id)
          println(wk.id+"已经死了")
        })
    }
  }
}

object SparkMaster extends App {
  val host: String = "127.0.0.1"
  val port: Int = 8877
  val config = ConfigFactory.parseString(
    s"""
       |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
       |akka.remote.netty.tcp.hostname = $host
       |akka.remote.netty.tcp.port = $port
     """.stripMargin)
  //ActorSystem
  val masterActorSystem = ActorSystem("sparkMaster", config)
  //ActorRef
  val masterActorRef = masterActorSystem.actorOf(Props[SparkMaster], "Master")
  //自己给自己发送消息
  masterActorRef ! "start" //CheckTimeOutWorker
  masterActorRef ! CheckTimeOutWorker
}
