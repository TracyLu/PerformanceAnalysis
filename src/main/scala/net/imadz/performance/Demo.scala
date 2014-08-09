package net.imadz.performance

import fr.janalyse.ssh._
import fr.janalyse.ssh.SSHOptions

import net.imadz.performance.monitoring._

/**
 * Created by geek on 14-8-9.
 */
object Demo extends App {

  implicit val sshOptions = SSHOptions(host = "dbserver", username = "techop", password = SSHPassword.string2password("hai_5631"))

  "Query against Binary UUID" collect (List(Cpu, Mem, IO, Network)) run {
    Thread.sleep(10000L)
    println("Hello")
  }


}
