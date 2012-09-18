package de.tuberlin.dima.plugalong;


import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.function.DoubleDoubleFunction;
import org.apache.mahout.math.function.Functions;

public class Dataset {

  private final TrainingExample[] examples;
  private final int numFeatures;

  private static final DoubleDoubleFunction ABS_MAX = new DoubleDoubleFunction() {
    @Override
    public double apply(double a, double b) {
      return Math.max(Math.abs(a), Math.abs(b));
    }
  };

  public Dataset(TrainingExample[] examples, int numFeatures) {
    this.examples = examples;
    this.numFeatures = numFeatures;
  }

  public void normalize() {
    /* center data */
    Vector averages = new DenseVector(numFeatures);
    for (TrainingExample sample : examples) {
      averages.assign(sample.features(), Functions.PLUS);
    }
    averages.assign(Functions.DIV, examples.length);

    for (TrainingExample sample : examples) {
      sample.features().assign(averages, Functions.MINUS);
    }

    /* normalize to -1 / 1 */
    Vector absoluteMaxima = new DenseVector(numFeatures);
    for (TrainingExample sample : examples) {
      absoluteMaxima.assign(sample.features(), ABS_MAX);
    }

    for (TrainingExample sample : examples) {
      sample.features().assign(absoluteMaxima, Functions.DIV);
    }
  }

  public TrainingExample[] examples() {
    return examples;
  }

  public int numFeatures() {
    return numFeatures;
  }

}
