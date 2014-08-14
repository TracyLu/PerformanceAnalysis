import net.imadz.performance.DataConverter
import org.scalacheck.Prop._
import org.scalacheck.Properties
import scalaz.concurrent.Task
import scalaz.stream.{text, io}

/**
 * Created by Scala on 14-8-7.
 */
object DataConverterTest extends Properties("DataImport") {
  property("space2tab") = secure {
    DataConverter.space2tab("   abc    de") == "\tabc\tde"
    DataConverter.space2tab(" abc    de") == "\tabc\tde"
    DataConverter.space2tab("   abc    de") != "abc\tde"
  }
  property("ioconverterfunc") = secure {
    val sourceFileName = "testdata/autoincremental_io.log";
    val targetFileName = "testdata/handled/autoincremental_io.log";

    val converter: Task[Unit] =
    DataConverter.ioconverterfunc(8, 415, sourceFileName, targetFileName)

    converter.run
    true
  }
  property("cpu convert") = secure {
    val sourceFileName = "testdata/cpu.log";
    val targetFileName = "testdata/handled/cpu.log";

    val converter: Task[Unit] =
      DataConverter.ioconverterfunc(sourceFileName, targetFileName)

    converter.run
    true
  }
  property("memory convert") = secure {
    val sourceFileName = "testdata/mem.log";
    val targetFileName = "testdata/handled/mem.log";

    val converter: Task[Unit] =
      DataConverter.ioconverterfunc(sourceFileName, targetFileName)

    converter.run
    true
  }
  property("network convert") = secure {
    val sourceFileName = "testdata/network.log";
    val targetFileName = "testdata/handled/network.log";

    val converter: Task[Unit] =
      DataConverter.ioconverterfunc(sourceFileName, targetFileName)

    converter.run
    true
  }
}
