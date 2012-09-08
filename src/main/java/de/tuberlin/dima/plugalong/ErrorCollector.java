package de.tuberlin.dima.plugalong;

import org.apache.mahout.math.Vector;

public interface ErrorCollector {

  void setNorm(int norm);
  void collect(Vector state);

  void markConverged();
}
