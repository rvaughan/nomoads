package edu.uci.nomoads.training;

import org.json.simple.JSONObject;

import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

/**
 * Intermediate and final results during training a classifier for the domain,os.
 * */
class MetaEvaluationMeasures {
    public double falsePositiveRate = -1;
    public double falseNegativeRate = -1;
    public double trainingTime = -1;
    public double populatingTime = -1;

    public int numTotal = -1;
    public int numPositive = -1;
    public int numNegative = -1;
    public int numOfPossibleFeatures = -1;

    public double AUC = -1;
    public double fMeasure = -1;
    public double numInstance = -1;
    public int numCorrectlyClassified = -1;
    public double accuracy = -1; // = NumCorrectlyClassified / NumTotal
    public double specificity = -1;
    public double recall = -1;

    public double TP = -1;
    public double TN = -1;
    public double FP = -1;
    public double FN = -1;
    public Info info;

    /*
    |--------------------------------------------------------------------------
    | Fields that represent the tree being evaluated
    |--------------------------------------------------------------------------
    */

    public double treeSize;
    public double numLeaves;
    public double numNonLeafNodes;

    public MetaEvaluationMeasures() {}

    public MetaEvaluationMeasures(Info info) {
        this.info = info;
    }

    /**
     * Do evaluation on trained classifier/model, including the summary, false
     * positive/negative rate, AUC, running time
     *
     * @param classifier
     *            - the trained classifier
     */
    protected void doEvaluation(Classifier classifier, Instances tras, int numFolds) {
        try {
            Evaluation evaluation = new Evaluation(tras);

            if (numFolds > 1)
                evaluation.crossValidateModel(classifier, tras, numFolds, new Random(1));
            else
                evaluation.evaluateModel(classifier, tras);

            numInstance = evaluation.numInstances();
            double M = evaluation.numTruePositives(1)
                    + evaluation.numFalseNegatives(1);
            numPositive = (int) M;
            AUC = evaluation.areaUnderROC(1);
            numCorrectlyClassified = (int) evaluation.correct();
            accuracy = 1.0 * numCorrectlyClassified / numInstance;
            falseNegativeRate = evaluation.falseNegativeRate(1);
            falsePositiveRate = evaluation.falsePositiveRate(1);
            fMeasure = evaluation.fMeasure(1);
            TP = evaluation.numTruePositives(1);
            TN = evaluation.numTrueNegatives(1);
            FP = evaluation.numFalsePositives(1);
            FN = evaluation.numFalseNegatives(1);

            specificity = evaluation.trueNegativeRate(1);
            recall = evaluation.recall(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public JSONObject getJSONobj() {
        JSONObject obj = new JSONObject();
        obj.put("accuracy", this.accuracy);
        obj.put("fpr", falsePositiveRate);
        obj.put("fnr", falseNegativeRate);
        obj.put("f_measure", fMeasure);
        obj.put("auc", AUC);
        obj.put("traing_time", trainingTime);
        obj.put("populating_time", populatingTime);

        if (info != null) {
            obj.put("init_num_pos", info.initNumPos);
            obj.put("init_num_neg", info.initNumNeg);
            obj.put("init_num_total", info.initNumTotal);
        }

        obj.put("specificity", specificity);
        obj.put("recall", recall);

        obj.put("numNonLeafNodes", numNonLeafNodes);

        return obj;
    }

}
