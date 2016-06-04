import com.aliasi.classify.Classification;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Files;

import java.io.*;

public class PolarityBasic {
    File mPolarityDir;
    String[] mCategories;
   static DynamicLMClassifier mClassifier ;

    PolarityBasic(String[] args) {
        System.out.println("\nBASIC POLARITY DEMO");
        this.mPolarityDir = new File("training", "txt_sentoken");
        System.out.println("\nData Directory=" + this.mPolarityDir);
        this.mCategories = this.mPolarityDir.list();
        int nGram = 8;
        boolean bounded = false;
        this.mClassifier = new DynamicLMClassifier(this.mCategories, nGram, bounded);
    }

    void run() throws ClassNotFoundException, IOException {
        this.train();
       this.evaluate("fady");
    }

    boolean isTrainingFile(File file) {
        return file.getName().charAt(2) != '9';
    }

    void train() throws IOException {
        int numTrainingCases = 0;
        int numTrainingChars = 0;
        System.out.println("\nTraining.");
        for (int i = 0; i < this.mCategories.length; ++i) {
            String category = this.mCategories[i];
            File file = new File(this.mPolarityDir, this.mCategories[i]);
            File[] trainFiles = file.listFiles();
            for (int j = 0; j < trainFiles.length; ++j) {
                File trainFile = trainFiles[j];
                if (!this.isTrainingFile(trainFile)) continue;
                ++numTrainingCases;
                String review = Files.readFromFile((File)trainFile);
                numTrainingChars += review.length();
                this.mClassifier.train(category, (CharSequence)review);
            }
        }
        System.out.println("  # Training Cases=" + numTrainingCases);
        System.out.println("  # Training Chars=" + numTrainingChars);
//        try {
//            OutputStream file = new FileOutputStream("polarityBasicModelSmall.ser");
//            OutputStream buffer = new BufferedOutputStream(file);
//            ObjectOutput output = new ObjectOutputStream(buffer);
//
//            mClassifier.compileTo(output);
//        }
//
//        catch(IOException ex){
//            System.out.println("Error during writing object" + ex.getMessage());
//            ex.printStackTrace();
//        }

//        AbstractExternalizable.compileTo(mClassifier, classifierFile);
    }

    public static String evaluate(String tweet) throws IOException {
//        System.out.println("\nEvaluating.");
//        int numTests = 0;
//        int numCorrect = 0;
//        for (int i = 0; i < this.mCategories.length; ++i) {
//            String category = this.mCategories[i];
//            File file = new File(this.mPolarityDir, this.mCategories[i]);
//            File[] trainFiles = file.listFiles();
//            for (int j = 0; j < trainFiles.length; ++j) {
//                File trainFile = trainFiles[j];
//                if (this.isTrainingFile(trainFile)) continue;
//                String review = Files.readFromFile((File)trainFile);
//                ++numTests;
//                Classification classification = this.mClassifier.classify((Object)review);
//                if (!classification.bestCategory().equals(category)) continue;
//                ++numCorrect;
//            }
//        }
//        System.out.println("  # Test Cases=" + numTests);
//        System.out.println("  # Correct=" + numCorrect);
//        System.out.println("  % Correct=" + (double)numCorrect / (double)numTests);
//        System.out.println("\n Evaluating");
//        String review =  "Dear people who write spoilers online for #GoT. I hate you. I hope whatever happened to that character happens to you.";
//        Classification classification = mClassifier.classify((Object)review);
//        System.out.println(classification.bestCategory());
//


            Classification classification1 = mClassifier.classify((Object)tweet);
            return classification1.bestCategory();


//        File file = new File("polarityBasicModel.ser");
//
//        try {
//            mClassifier = (DynamicLMClassifier) AbstractExternalizable.readObject(file);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        String review =  "Dear people who write spoilers online for #GoT. I hate you. I hope whatever happened to that character happens to you.";
//        Classification classification = this.mClassifier.classify((Object)review);
//        System.out.println(classification.bestCategory());
    }

//    public static void main(String[] args) {
//        try {
//            new PolarityBasic(args).run();
//        }
//        catch (Throwable t) {
//            System.out.println("Thrown: " + t);
//            t.printStackTrace(System.out);
//        }
//    }
}

