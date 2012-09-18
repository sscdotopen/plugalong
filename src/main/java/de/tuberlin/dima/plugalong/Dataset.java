package de.tuberlin.dima.plugalong;


import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.function.DoubleDoubleFunction;
import org.apache.mahout.math.function.Functions;

public class Dataset {

  private final Sample[] samples;
  private final int numFeatures;

  private static final DoubleDoubleFunction ABS_MAX = new DoubleDoubleFunction() {
    @Override
    public double apply(double a, double b) {
      return Math.max(Math.abs(a), Math.abs(b));
    }
  };

  public Dataset(Sample[] samples, int numFeatures) {
    this.samples = samples;
    this.numFeatures = numFeatures;
  }

  public void normalize() {
    /* center data */
    Vector averages = new DenseVector(numFeatures);
    for (Sample sample : samples) {
      averages.assign(sample.features(), Functions.PLUS);
    }
    averages.assign(Functions.DIV, samples.length);

    for (Sample sample : samples) {
      sample.features().assign(averages, Functions.MINUS);
    }

    /* normalize to -1 / 1 */
    Vector absoluteMaxima = new DenseVector(numFeatures);
    for (Sample sample : samples) {
      absoluteMaxima.assign(sample.features(), ABS_MAX);
    }

    for (Sample sample : samples) {
      sample.features().assign(absoluteMaxima, Functions.DIV);
    }
  }

  public Sample[] examples() {
    return samples;
  }

  public int numFeatures() {
    return numFeatures;
  }

}
