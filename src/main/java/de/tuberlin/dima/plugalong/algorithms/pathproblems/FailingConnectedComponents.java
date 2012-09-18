package de.tuberlin.dima.plugalong.algorithms.pathproblems;

import org.apache.mahout.math.Vector;

import java.util.Random;

public class FailingConnectedComponents extends ConnectedComponents {

  private final Random random = new Random();

  @Override
  public String getTitle() {
    return "FailingConnectedComponents";
  }

  @Override
  protected Vector stateForNextIteration(int iteration, Vector cNext) {

    if (iteration == 3 || iteration == 5) {
      for (int vertex = 0; vertex < cNext.size(); vertex++) {
        if (random.nextDouble() < 0.2) {
          cNext.setQuick(vertex, vertex);
        }
      }
    }

    return cNext;
  }
}
