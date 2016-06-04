package utilities

/**
  * Created by droidman on 28/05/16.
  */
object SparkHelper {

  case class InitializeStream(s :String)

  case class KillActor(s :String)

  case class senResult(var posCount:Int,var negCount:Int)

}
