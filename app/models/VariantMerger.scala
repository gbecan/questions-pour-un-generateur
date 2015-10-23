package models

import java.io.{FileWriter, File}
import scala.sys.process._

/**
 * Created by gbecan on 10/21/15.
 */
class VariantMerger {

  def merge(variant: List[String]) : Option[File] = {
    // Create input file
    val listFile = File.createTempFile("qpug_", ".txt")
    val writer = new FileWriter(listFile)
    for (elem <- variant) {
      writer.write("file '" + elem + "'\n")
    }
    writer.close()

    // Create output file
    val outputFile = File.createTempFile("qpug_", ".mp3")

    // Execute ffmpeg
    val cmd = "ffmpeg -y -f concat -i " + listFile.getAbsolutePath + " -c copy " + outputFile.getAbsolutePath
    val result = cmd ! ProcessLogger(_ => (), _ => ())

    // Clean input file
    listFile.delete()


    if (result == 0) {
      Some(outputFile)
    } else {
      None
    }
  }

}
