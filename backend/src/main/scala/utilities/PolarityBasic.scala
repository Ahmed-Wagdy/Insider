package utilities

import java.io.{IOException, File}
import java.util
import java.util.Properties

import com.aliasi.classify.DynamicLMClassifier
import com.aliasi.util.Files
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentClass
import edu.stanford.nlp.util.CoreMap
import scala.collection.JavaConversions._

object PolarityBasic {
  val props = new Properties()

  props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
  val pipeline: StanfordCoreNLP = new StanfordCoreNLP(props);


  var mPolarityDir: File  = null
  var mCategories: Array[String] = null
  var mClassifier: DynamicLMClassifier = null

    println("\nBASIC POLARITY DEMO")
    this.mPolarityDir = new File("/home/ahmed/training", "txt_sentoken")
    println("\nData Directory=" + this.mPolarityDir)
    this.mCategories = this.mPolarityDir.list
    val nGram: Int = 8
    val bounded: Boolean = false
    this.mClassifier = new DynamicLMClassifier(this.mCategories, nGram, bounded)

  @throws(classOf[ClassNotFoundException])
  @throws(classOf[IOException])
  val run =  {
    this.train
  }

  def isTrainingFile(file: File): Boolean = {
    return file.getName.charAt(2) != '9'
  }

  @throws(classOf[IOException])
  def train {
    var numTrainingCases: Int = 0
    var numTrainingChars: Int = 0
    println("\nTraining.")

      var i: Int = 0
      while (i < this.mCategories.length) {

          val category: String = this.mCategories(i)
          val file: File = new File(this.mPolarityDir, this.mCategories(i))
          val trainFiles: Array[File] = file.listFiles()

            var j: Int = 0
            while (j < trainFiles.length) {
                val trainFile: File = trainFiles(j)
                if (!this.isTrainingFile(trainFile)){

                }
                else {
                  numTrainingCases += 1

                  val review: String = Files.readFromFile(trainFile.asInstanceOf[File])
                  numTrainingChars += review.length
                  this.mClassifier.train(category, review.asInstanceOf[CharSequence])
                }
              j += 1;
            }
          i += 1;
      }

    println("  # Training Cases=" + numTrainingCases)
    println("  # Training Chars=" + numTrainingChars)
  }

  @throws(classOf[IOException])
  def evaluate(tweet: String): String = {
    var sen:String = "good"
    val annotation: Annotation = pipeline.process(tweet);
    val sentences: util.List[CoreMap] = annotation.get(classOf[SentencesAnnotation])

    for (sentence :CoreMap <- sentences) {
      val sentiment: String = sentence.get(classOf[SentimentClass])

      if (sentiment == "Neutral") sen = sentiment
      else sen = mClassifier.classify(tweet.asInstanceOf[AnyRef]).bestCategory
    }
    sen
  }

}