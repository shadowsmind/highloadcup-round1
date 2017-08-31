package com.shadowsmind.loader

import java.nio.file.Files

import better.files._
import com.shadowsmind.api.protocol._
import com.shadowsmind.persistence.{ LocationRepository, UserRepository, VisitRepository }
import spray.json._

import scala.concurrent.ExecutionContextExecutor

object DataLoader {

  import ApiJsonProtocol._

  def load(archivePath: String)(implicit ex: ExecutionContextExecutor): Unit = {
    val files = File(archivePath).unzip().list.toSeq

    files.foreach { file ⇒
      val source = new String(Files.readAllBytes(file.path))
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

}
