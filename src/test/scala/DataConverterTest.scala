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
}
