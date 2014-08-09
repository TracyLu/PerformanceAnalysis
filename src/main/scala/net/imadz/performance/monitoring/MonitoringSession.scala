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
class MonitoringSession(val sessionName: String, val monitors: List[Monitor]) {

  def run(f: => Unit) {
    monitors foreach (_.start)
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
  def start(): Unit

  def stop(): Unit
}

abstract class SSHBased(implicit val sshOps: SSHOptions) extends Monitor {

  lazy val ssh = SSH(sshOps)

  lazy val fileWriter = {
    lazy val format = new SimpleDateFormat("yyyyMMddhhmm")
    val time = format.format(new Date);
    val out = "report/" + time + "/" + name + ".log"
    val file: File = new File(out)
    def createFile {
      if (!file.createNewFile) throw new IllegalStateException("Cannot create file : " + file.getAbsolutePath)
    }
    def prepareFile {
      if (!file.getParentFile.exists) {
        if (file.getParentFile.mkdirs) {
          if (!file.createNewFile) throw new IllegalStateException("Cannot create file : " + file.getAbsolutePath)
        } else {
          throw new IllegalStateException("Cannot create directory : " + file.getParent)
        }
      } else {
        if (file.exists) {
          if (file.delete) createFile
          else throw new IllegalStateException("Cannot delete file : " + file.getAbsolutePath)

        }
        else createFile
      }
    }
    prepareFile
    new FileWriter(file)
  }

  def command: String

  def name: String

  override def start(): Unit = ssh.run(command, {
    case ExecPart(content) => {
      fileWriter.write(content + "\n")
      fileWriter.flush
    }
    case ExecEnd(rc) => println(command + " rc = " + rc)
    case ExecTimeout => println(command + " timeout ...")
  })

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
