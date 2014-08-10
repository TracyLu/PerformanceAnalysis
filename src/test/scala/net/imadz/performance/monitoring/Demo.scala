package net.imadz.performance.monitoring

import fr.janalyse.ssh._
import fr.janalyse.ssh.SSHOptions

/**
 * Created by geek on 14-8-9.
 */
object Demo extends App {

  implicit val sshOptions = SSHOptions(host = "dbserver", username = "techop", password = SSHPassword.string2password("hai_5631"))

  "Query against Binary UUID" collect (List(Cpu, Mem, IO, Network)) run {
    println("Hello")
  }


}
