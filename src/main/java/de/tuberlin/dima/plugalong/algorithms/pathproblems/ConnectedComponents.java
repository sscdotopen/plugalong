package de.tuberlin.dima.plugalong.algorithms.pathproblems;

import de.tuberlin.dima.plugalong.Edge;
import de.tuberlin.dima.plugalong.ErrorCollector;
import de.tuberlin.dima.plugalong.Graph;
import de.tuberlin.dima.plugalong.algorithms.GraphBasedFixpointAlgorithm;
import de.tuberlin.dima.plugalong.errors.ChangedComponentsErrorMeasure;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SparseMatrix;
import org.apache.mahout.math.Vector;

import java.util.Iterator;

public class ConnectedComponents implements GraphBasedFixpointAlgorithm {

  @Override
  public String getTitle() {
    return "ConnectedComponents";
  }

  @Override
  public int run(Graph graph, ErrorCollector collector) {

    collector.setErrorMeasure(new ChangedComponentsErrorMeasure());

    double[] arr = new double[graph.numVertices()];
    for (int n = 0; n < graph.numVertices(); n++) {
      arr[n] = n;
    }

    Vector c = new DenseVector(arr);

    Matrix A = new SparseMatrix(graph.numVertices(), graph.numVertices());

    System.out.println("Reading graph...");
    for (Edge edge : graph) {
      A.setQuick(edge.source(), edge.target(), 1.0);
      A.setQuick(edge.target(), edge.source(), 1.0);
    }

    int iteration = 0;
    int numChanged = Integer.MAX_VALUE;
    while (numChanged > 0) {
      Vector cNext = c.clone();
      numChanged = 0;
      for (int vertex = 0; vertex < graph.numVertices(); vertex++) {
        double currentComponent = c.getQuick(vertex);
        boolean changed = false;
        Iterator<Vector.Element> neighbors = A.viewRow(vertex).iterateNonZero();
        while (neighbors.hasNext()) {
          int neighbor = neighbors.next().index();
          if (currentComponent < c.getQuick(neighbor)) {
            changed = true;
            currentComponent = c.getQuick(neighbor);
          }
        }
        if (changed) {
          cNext.setQuick(vertex, currentComponent);
          numChanged++;
        }
      }

      //collector.collect(iteration, numChanged);
      c = stateForNextIteration(iteration, cNext);

      collector.collect(c.clone());
      iteration++;
    }

    collector.markConverged();

    return iteration;
  }

  protected Vector stateForNextIteration(int iteration, Vector cNext) {
    return cNext;
  }
}
