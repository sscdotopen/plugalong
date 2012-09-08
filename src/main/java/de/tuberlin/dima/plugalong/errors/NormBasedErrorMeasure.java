package de.tuberlin.dima.plugalong.errors;

import org.apache.mahout.math.Vector;

public class NormBasedErrorMeasure implements ErrorMeasure {

  private final int norm;

  public NormBasedErrorMeasure(int norm) {
    this.norm = norm;
  }

  @Override
  public double error(Vector one, Vector two) {
    return one.minus(two).norm(norm);
  }
}
