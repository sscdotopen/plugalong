package de.tuberlin.dima.plugalong.errors;

import org.apache.mahout.math.Vector;

import java.util.Iterator;

public class ChangedComponentsErrorMeasure implements ErrorMeasure {

  @Override
  public double error(Vector one, Vector two) {
    double numChanged = 0;
    Vector diff = one.minus(two);
    Iterator<Vector.Element> elems = diff.iterator();
    while (elems.hasNext()) {
      Vector.Element e = elems.next();
      if (e.get() != 0) {
        numChanged++;
      }
    }
    return numChanged;
  }
}
