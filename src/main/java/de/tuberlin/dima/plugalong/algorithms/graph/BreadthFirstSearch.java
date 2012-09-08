package de.tuberlin.dima.plugalong.algorithms.graph;

import de.tuberlin.dima.plugalong.Edge;
import de.tuberlin.dima.plugalong.ErrorCollector;
import de.tuberlin.dima.plugalong.Graph;
import de.tuberlin.dima.plugalong.algorithms.GraphBasedFixpointAlgorithm;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SparseColumnMatrix;
import org.apache.mahout.math.Vector;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

public class BreadthFirstSearch implements GraphBasedFixpointAlgorithm {


  private static final Random random = new Random();

  @Override
  public String getTitle() {
    return "BreadthFirstSearch";
  }

  @Override
  public int run(Graph graph, ErrorCollector collector) {

    Matrix A = new SparseColumnMatrix(graph.numVertices(), graph.numVertices());

    System.out.println("Reading graph...");
    for (Edge edge : graph) {
      A.setQuick(edge.source(), edge.target(), 1);
    }

    int sourceVertex = 1;
    Vector d = vector(graph.numVertices(), Double.POSITIVE_INFINITY);
    d.setQuick(sourceVertex, 0);

    int iteration = 0;

    int numChanged = Integer.MAX_VALUE;

    while (numChanged != 0) {
      numChanged = 0;
      for (int vertex = 0; vertex < graph.numVertices(); vertex++) {
        boolean changed = false;
        Iterator<Vector.Element> incidentNeighbors = A.viewColumn(vertex).iterateNonZero();
        while (incidentNeighbors.hasNext()) {
          Vector.Element neighbor = incidentNeighbors.next();
          double neighborDistance = d.get(neighbor.index());
          if (!Double.isInfinite(neighborDistance)) {
            double candidateDistance = neighborDistance + neighbor.get();
            if (Double.isInfinite(d.getQuick(vertex)) || candidateDistance < d.getQuick(vertex)) {
              d.setQuick(vertex, candidateDistance);
              changed = true;
            }
          }
        }
        if (changed) {
          numChanged++;
        }
      }
      iteration++;
      //collector.collect(iteration, numChanged);
      d = stateForNextIteration(iteration, d);
    }

    return iteration;
  }

  protected Vector stateForNextIteration(int iteration, Vector dNext) {
    return dNext;
  }

  private Vector vector(int numVertices, double initialValue) {
    double[] initialValues = new double[numVertices];
    Arrays.fill(initialValues, initialValue);
    return new DenseVector(initialValues);
  }
}
