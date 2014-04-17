package net.iryndin.yacaparser

abstract class YacaRubrics
case class RootRubrics(name: String, url: String) extends YacaRubrics

object RubricsParser {
    def main(args: Array[String]) = {
        println("RubricsParser work")
        
        //val text = scala.io.Source.fromInputStream(getClass.getResourceAsStream("root.json")).mkString
        val rootJson = io.Source.fromFile("root.json", "UTF-8").mkString
        val rootRubrics = util.parsing.json.JSON.parseFull(rootJson)
        rootRubrics match {
            case Some(x) => x match {
                    case l: List[_] => parseChildRubrics(l)
                    case _ => println("Error, get not list")
                }
            case None => println("Failed to parse json")
        }
    }
    
    def parseChildRubrics(rubrics: List[_]): Unit = {
        val parList = rubrics.par
        parList.foreach(parseRubrics)
    }
    
    def parseRubrics(x: Any): Unit = {
        println(x)
        /*
        x match {
            case m:Map[String,B] => println(m.get("url"))
            case _ => 
        }
        */
    }
    
    
}