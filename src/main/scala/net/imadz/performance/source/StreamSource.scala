package net.imadz.performance.source

import jassh._
import fr.janalyse.ssh.{ExecTimeout, ExecEnd, ExecPart}
import java.text.SimpleDateFormat
import java.util.Date
import java.io.{FileWriter, File}

/**
 * Created by geek on 14-8-8.
 */
object StreamSource extends App {

  val ssh = SSH("dbserver", "techop", "hai_5631")

  val format = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss")
  val time = format.format(new Date);

  val out = "report/" + time + "/io.log"

  private val file: File = new File(out)
  if (file.getParentFile.exists) {
    if (!file.getParentFile.delete) throw new IllegalStateException("Cannot delete directory : " + file.getParent)
  }
  if (file.getParentFile.mkdirs && file.createNewFile) {
  } else {
    throw new IllegalStateException("Cannot delete directory : " + file.getParent)
  }

  val fileWriter = new FileWriter(file)

  ssh.run("iostat -d 1", {
    case ExecPart(content) => {
      fileWriter.write(content + "\n")
      fileWriter.flush
    }
    case ExecEnd(rc) => println("rc = " + rc)
    case ExecTimeout => println("timeout ...")
  })

  do {
    Console.println("Use stop and enter to stop ssh clients")
  } while (!Console.readLine().equals("stop"))

  ssh.close

}
