package net.imadz.performance.monitoring

import fr.janalyse.ssh.{SSHPassword, ExecTimeout, ExecPart, ExecEnd}
import jassh._
import java.text.SimpleDateFormat
import java.util.Date
import java.io.File
import net.imadz.performance.{PerformanceDataUI, DataConverter}
/**
 * Created by Scala on 14-8-11.
 */
object LearnSSh extends App{
  implicit val sshOptions = SSHOptions(host = "dbserver", username = "techop", password = SSHPassword.string2password("2014@YouAndMe"))

  lazy val ssh = SSH(sshOptions)

  def command: String = "echo \"show status like '%innodb%'\" | mysql -uboo -p1q2w3e4r5t"

  ssh.run(command, {

    case ExecPart(content) => {
      println(content)
    }
    case ExecEnd(rc) => {
      println(command + " rc = " + rc)
    }
    case ExecTimeout => {
      println(command + " timeout ...")
    }
  })

  //println(ssh.execute(command))
  ssh.close()
}
