package de.tuberlin.dima.plugalong.algorithms.graph;

import org.apache.mahout.math.Vector;

import java.util.Random;

public class FailingConnectedComponents extends ConnectedComponents {

  private final Random random = new Random();

  @Override
  protected Vector stateForNextIteration(int iteration, Vector cNext) {

    if (iteration == 3) {
      for (int vertex = 0; vertex < cNext.size(); vertex++) {
        if (random.nextDouble() < 0.2) {
          cNext.setQuick(vertex, vertex);
        }
      }
    }

    return cNext;
  }
}
