package org.adridadou

import services.clause.Clause
import services.fields.{ContractDate, ContractField, FullName}
import services.markdown.{ClauseValidation, ValidationError, ValidationSuccess}
import org.scalatest._

/**
  * Created by davidroon on 05.06.16.
  * This code is released under Apache 2 license
  */
class ClauseValidationSpec extends FlatSpec with Matchers {

  "A Validator" should "check that every parameter is defined" in {

    val clauseText = """
        |This is my clause. {{contractor}}. And I am born in {{contractorBirthdate}}
      """.stripMargin

    val validator = new ClauseValidation()
    validator.validate(Clause(clauseText, Map[String,ContractField](
      "contractor" -> FullName,
      "contractorBirthdate" -> ContractDate
    ))) should be (ValidationSuccess)
  }

  it should "return an error if a parameter is missing" in {

    val clauseText = """
                       |This is my clause. [[contractor]]. And I am born in [[contractorBirthdate]]
                     """.stripMargin

    val validator = new ClauseValidation()
    validator.validate(Clause(clauseText, Map[String,ContractField](
      "contractor" -> FullName
    ))) should be (ValidationError)
  }
}
