package org.adridadou

import java.io.File

import services.clause.ClauseParser
import services.markdown.{ClauseValidation, ValidationSuccess}
import org.scalatest._

import scala.io.Source
/**
  * Created by davidroon on 05.06.16.
  * This code is released under Apache 2 license
  */
class ClauseParsingSpec extends FlatSpec with Matchers {

  "Clause Parser" should "read a file and create a clause object" in {
    val clauseSource = Source.fromFile("src/test/resources/JustATest.clause").reader()
    val parser = new ClauseParser
    val result = parser.parse(clauseSource).get._1

    new ClauseValidation().validate(result) should be (ValidationSuccess)
  }

  "Clause Parser" should "read this actual clause" in {
    val folder = new File("src/test/resources/legaldomain")
    folder.listFiles().filter(_.getName.endsWith(".clause")).foreach(file => {
      println("parsing " + file.getName)
      val parser = new ClauseParser
      val result = parser.parse(Source.fromFile(file).reader()).get._1

      new ClauseValidation().validate(result) should be (ValidationSuccess)
    })

  }
}
