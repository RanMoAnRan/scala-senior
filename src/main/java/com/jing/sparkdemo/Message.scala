package com.jing.sparkdemo

//Worker向Master发送注册消息
case class RegisterWorkerInfo(wid: String, core: Int, ram: Long)

case class HeartBeat(wkid: String)

//Worker的信息类
class WorkerInfo(val id: String, core: Int, ram: Long) {

  var lastHeartBeatTIme: Long = _

}

//Master向Worker发送注册已成功消息
case object RegisteredWoker

//worker -> worker
case object SendHeartBeat

//Master给自己发送检查Worker超时的消息
case object CheckTimeOutWorker
//Master给自己发送删除超时Worker的消息
case object RemoveTimeOutWorker