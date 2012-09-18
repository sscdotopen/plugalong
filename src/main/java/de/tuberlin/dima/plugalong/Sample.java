package de.tuberlin.dima.plugalong;

import org.apache.mahout.math.Vector;

public class Sample {

  private final Vector features;
  private final double label;

  public Sample(Vector features, double label) {
    this.features = features;
    this.label = label;
  }

  public Vector features() {
    return features;
  }

  public double label() {
    return label;
  }
}
