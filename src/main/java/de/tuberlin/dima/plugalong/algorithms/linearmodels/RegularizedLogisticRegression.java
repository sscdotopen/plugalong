package de.tuberlin.dima.plugalong.algorithms.linearmodels;

import com.google.common.collect.Lists;
import de.tuberlin.dima.plugalong.Dataset;
import de.tuberlin.dima.plugalong.TrainingExample;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.function.Functions;

import java.util.List;
import java.util.Random;

public class RegularizedLogisticRegression {

  private final Dataset dataset;
  private final List<TrainingExample> trainingSamples;
  private final List<TrainingExample> testSamples;


  private final double alpha;
  private final double lambda;

  private Vector theta;

  public RegularizedLogisticRegression(Dataset dataset, double alpha, double lambda,
      double trainingRatio) {

    this.dataset = dataset;
    this.alpha = alpha;
    this.lambda = lambda;

    theta = zeros(dataset.numFeatures());

    dataset.normalize();

    trainingSamples = Lists.newArrayList();
    testSamples = Lists.newArrayList();

    Random random = new Random();
    for (TrainingExample sample : dataset.examples()) {
      if (random.nextDouble() < trainingRatio) {
        trainingSamples.add(sample);
      } else {
        testSamples.add(sample);
      }
    }

  }

  public double accuracy() {
    int numCorrect = 0;

    for (TrainingExample example : testSamples) {
      double prediction = hypothesis(example.features());

      if ((example.label() == 1 && prediction > 0.5) || (example.label() == 0 && prediction <= 0.5)) {
        numCorrect++;
      }
    }

    return (double) numCorrect / testSamples.size();
  }

  public double hypothesis(Vector x) {
    return 1d / (1d + Math.exp(-1d * theta.dot(x)));
  }

  public void trainWithBatchGradientDescent(int numIterations) {
    for (int n = 0; n < numIterations; n++) {
      batchGradientDescentSinglePass();
    }
  }

  public Vector theta() {
    return theta;
  }

  private void batchGradientDescentSinglePass() {

    double m = trainingSamples.size();
    double alphaDivM = alpha / m;


    Vector gradients = zeros(dataset.numFeatures());

    for (TrainingExample example : trainingSamples) {
      Vector partialGradient = partialGradient(example);

      partialGradient.assign(Functions.MULT, alphaDivM);

      gradients.assign(partialGradient, Functions.MINUS);
    }

    theta = theta.minus(gradients);
  }

  private Vector zeros(int cardinality) {
    return new DenseVector(cardinality);
  }

  private Vector partialGradient(TrainingExample example) {

    int m = dataset.examples().length;
    Vector x = example.features();
    double y = example.label();

    Vector regularization = theta.times(lambda / m);
    regularization.set(0, 0);


    return x.times(hypothesis(x) - y).minus(regularization);
  }
}
