package net.imadz.performance.monitoring

import fr.janalyse.ssh.{ExecTimeout, ExecPart, ExecEnd}
import jassh._
import java.text.SimpleDateFormat
import java.util.Date
import java.io.{FileWriter, File}
import scala.collection.mutable.ListBuffer

/**
 * Created by geek on 14-8-9.
 */
case class MonitoringSession(val sessionName: String, val monitors: List[Monitor]) {

  def run(f: => Unit) {
    monitors foreach (_.start(sessionName))
    try {
      f
    } finally {
      monitors foreach (_.stop)
    }
  }
}


class MonitoringSessionBuilder(val sessionName: String) {

  val monitors: ListBuffer[Monitor] = new ListBuffer[Monitor]

  def collect(monitor: Metric)(implicit sshOps: SSHOptions) = {
    monitors += monitor.apply
    this
  }

  def collect(monitorList: List[Metric])(implicit sshOps: SSHOptions) = {
    monitors ++= monitorList map (_.apply)
    this
  }

  def run(f: => Unit): Unit = {
    new MonitoringSession(sessionName, monitors.toList) run f
  }
}


trait Monitor {
  val format = new SimpleDateFormat("yyyyMMddhhmmss")
  val time = format.format(new Date);
  var logFilePath: Option[String] = None

  def start(sessionName: String): Unit

  def stop: Unit

  def name: String

  def logFile = logFilePath

  protected def fileWriter(sessionName: String) = {
    logFilePath = Some(logFile(sessionName))
    val file: File = new File(logFilePath.get)
    prepareFile(file)
    new FileWriter(file)
  }

  def logFile(sessionName: String): String = {
    ("report/" + time + File.separator + sessionName + File.separator + name + ".log") replaceAll(" +", "_")
  }

  private def prepareFile(file: File) {
    if (!file.getParentFile.exists) {
      if (file.getParentFile.mkdirs) {
        if (!file.createNewFile) throw new IllegalStateException("Cannot create file : " + file.getAbsolutePath)
      } else {
        throw new IllegalStateException("Cannot create directory : " + file.getParent)
      }
    } else {
      if (file.exists) {
        if (file.delete) createFile(file)
        else throw new IllegalStateException("Cannot delete file : " + file.getAbsolutePath)

      }
      else createFile(file)
    }
  }

  private def createFile(file: File) {
    if (!file.createNewFile) throw new IllegalStateException("Cannot create file : " + file.getAbsolutePath)
  }
}

abstract class SSHBased(implicit val sshOps: SSHOptions) extends Monitor {

  lazy val ssh = SSH(sshOps)

  def command: String


  override def start(sessionName: String): Unit = {
    val writer = fileWriter(sessionName)
    ssh.run(command, {
      case ExecPart(content) => {
        writer.write(content + "\n")
        writer.flush
      }
      case ExecEnd(rc) => {
        println(command + " rc = " + rc)
        writer.close
      }
      case ExecTimeout => {
        println(command + " timeout ...")
        writer.close
      }
    })

  }

  override def stop(): Unit = {
    ssh.close
  }

}

class CpuMonitor(val interval: Int)(implicit sshOps: SSHOptions) extends SSHBased {
  override def command = "mpstat " + interval

  override def name = "cpu"
}

class MemMonitor(val interval: Int)(implicit sshOps: SSHOptions) extends SSHBased {
  override def command = "sar -r " + interval

  override def name = "mem"
}


class IOMonitor(val interval: Int)(implicit sshOps: SSHOptions) extends SSHBased {
  override def command = "iostat -x -k -d " + interval

  override def name = "io"
}


class NetworkMonitor(val interval: Int)(implicit sshOps: SSHOptions) extends SSHBased {
  override def command = "sar -n DEV " + interval

  override def name = "network"
}

trait Metric {
  def apply(implicit sshOps: SSHOptions): Monitor
}

case object Cpu extends Metric {
  def apply(implicit sshOps: SSHOptions): Monitor = new CpuMonitor(1)
}

case object Mem extends Metric {
  def apply(implicit sshOps: SSHOptions): Monitor = new MemMonitor(1)
}

case object IO extends Metric {
  def apply(implicit sshOps: SSHOptions): Monitor = new IOMonitor(1)
}

case object Network extends Metric {
  def apply(implicit sshOps: SSHOptions): Monitor = new NetworkMonitor(1)
}
