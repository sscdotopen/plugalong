package de.tuberlin.dima.plugalong;

import de.tuberlin.dima.plugalong.errors.ErrorMeasure;
import org.apache.mahout.math.Vector;

public interface ErrorCollector {

  void setErrorMeasure(ErrorMeasure measure);
  void collect(Vector state);

  void markConverged();
}
