package de.tuberlin.dima.plugalong.errors;

import org.apache.mahout.math.Vector;

public interface ErrorMeasure {
  double error(Vector one, Vector two);
}
