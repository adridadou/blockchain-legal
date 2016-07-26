package services.markdown
import services.clause.Clause
import services.fields.ContractField
import org.parboiled2._

import scala.util.{Failure, Success, Try}
/**
  * Created by davidroon on 05.06.16.
  * This code is released under Apache 2 license
  */
class ClauseValidation {
  def validate(clause:Clause):ValidationResult  = {
    val parser = new MarkdownValidationParser(clause.markdown,clause.parameters)
    parser.rootRule.run().map(lookForValidationErrors).getOrElse(ValidationError)
  }

  private def lookForValidationErrors(seq:Seq[AST]):ValidationResult = {
    val isValid = seq.map {
      case AText(_) => true
      case AEnd => true
      case AKey(_, Success(_)) => true
      case AKey(_, Failure(_)) => false
    }.reduce(_ && _)

    if(isValid) ValidationSuccess else ValidationError
  }
}

sealed trait AST

case class AText(in: String) extends AST
case class AKey(in: String, result:Try[String]) extends AST
case object AEnd extends AST

class MarkdownValidationParser(val input: ParserInput, val parameters:Map[String,ContractField]) extends Parser {
  val openS = "[["
  val closeS = "]]"

  def NormalChar: Rule0 = rule { !(openS | closeS) ~  ANY }
  def keyChar: Rule0 = rule { !(openS | closeS ) ~ ANY }

  def Characters: Rule0 = rule { oneOrMore(NormalChar)  } // word
  def charsAST: Rule1[AST] = rule { capture(Characters) ~> ((s: String) => AText(s)) }
  def charsKeyAST: Rule1[AST] = rule { capture(zeroOrMore(keyChar)) ~> ((s: String) => AKey(s, validateKey(s)))  }

  def KeyInner : Rule1[AST] = rule { openS ~ charsKeyAST ~ closeS }
  def Key: Rule1[AST] = rule { &(openS) ~ KeyInner  }

  def rootRule: Rule1[Seq[AST]] = rule { zeroOrMore (Key | charsAST) ~ EOI}

  def validateKey(value:String) : Try[String] = parameters.get(value).map(fieldType => Success(value)).getOrElse(Failure(ParameterNotFound(value)))
}

sealed trait ValidationResult
case object ValidationSuccess extends ValidationResult
case object ValidationError extends ValidationResult

case class ParameterNotFound(value:String) extends Exception
