package de.tuberlin.dima.plugalong.algorithms.graph;

import org.apache.mahout.math.Vector;

import java.util.Random;

public class FailingBreadthFirstSearch extends BreadthFirstSearch {

  private final Random random = new Random();

  @Override
  public String getTitle() {
    return "FailingBreadthFirstSearch";
  }

  @Override
  protected Vector stateForNextIteration(int iteration, Vector dNext) {

    if (iteration == 5) {
      for (int vertex = 0; vertex < dNext.size(); vertex++) {
        if (random.nextDouble() < 0.05) {
          dNext.setQuick(vertex, Double.POSITIVE_INFINITY);
        }
      }
    }

    return dNext;
  }

}
