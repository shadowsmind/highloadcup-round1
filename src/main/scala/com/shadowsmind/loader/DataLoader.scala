package com.shadowsmind.loader

import java.nio.charset.StandardCharsets
import java.nio.file.Files

import better.files._
import com.shadowsmind.api.protocol._
import com.shadowsmind.persistence.{ LocationRepository, UserRepository, VisitRepository }
import spray.json._

import scala.concurrent.ExecutionContextExecutor
import scala.util.{ Failure, Success, Try }

object DataLoader {

  import ApiJsonProtocol._

  def load(dataPath: String, isZip: Boolean)(implicit ex: ExecutionContextExecutor, onFailure: () ⇒ Unit): Unit = {
    Try {
      val directory = if (isZip) {
        File(dataPath).unzip()
      } else {
        File(dataPath)
      }

      val files = directory.list.toSeq.sortBy(_.name)

      files.foreach { file ⇒
        if (file.name.contains("options")) {
          // TODO: logic for parse timestamp from options.txt
        } else {
          // TODO: save date to DB with actors
          val source = new String(Files.readAllBytes(file.path), StandardCharsets.UTF_8)
          val json = source.parseJson
          if (file.name.startsWith("users")) {
            UserRepository.saveAll(json.convertTo[Users].users)
          } else if (file.name.startsWith("locations")) {
            LocationRepository.saveAll(json.convertTo[Locations].locations)
          } else if (file.name.startsWith("visits")) {
            VisitRepository.saveAll(json.convertTo[Visits].visits)
          }
        }
      }

      files.size
    } match {
      case Success(s) ⇒
        println(s"Loaded $s files. Import started asynchronously...")

      case Failure(e) ⇒
        println(s"Data load failure: ${e.getMessage}")
        onFailure()
    }
  }

}
