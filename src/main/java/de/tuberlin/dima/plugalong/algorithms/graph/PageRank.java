package de.tuberlin.dima.plugalong.algorithms.graph;

import de.tuberlin.dima.plugalong.Edge;
import de.tuberlin.dima.plugalong.ErrorCollector;
import de.tuberlin.dima.plugalong.FastSparseRowMatrix;
import de.tuberlin.dima.plugalong.Graph;
import de.tuberlin.dima.plugalong.algorithms.GraphBasedFixpointAlgorithm;
import de.tuberlin.dima.plugalong.algorithms.GraphBasedFixpointAlgorithm;
import de.tuberlin.dima.plugalong.errors.NormBasedErrorMeasure;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SparseMatrix;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.function.Functions;
import org.apache.mahout.math.list.IntArrayList;
import org.apache.mahout.math.set.OpenIntHashSet;

import java.util.Arrays;
import java.util.Iterator;


public class PageRank implements GraphBasedFixpointAlgorithm {

  private static final double EPSILON = 0.0001;

  @Override
  public String getTitle() {
    return "PageRank";
  }

  @Override
  public int run(final Graph graph, ErrorCollector collector) {

    collector.setErrorMeasure(new NormBasedErrorMeasure(1));

    Matrix A = new SparseMatrix(graph.numVertices(), graph.numVertices());

    System.out.println("Reading graph...");
    for (Edge edge : graph) {
      A.setQuick(edge.source(), edge.target(), edge.value());
    }

    OpenIntHashSet danglingVertices = new OpenIntHashSet();

    Matrix M = new FastSparseRowMatrix(graph.numVertices(), graph.numVertices(), false);
    for (int n = 0; n < graph.numVertices(); n++) {
      Vector row = A.viewRow(n);
      if (row.getNumNondefaultElements() > 0) {
        Iterator<Vector.Element> elems = row.normalize(1.0).iterateNonZero();
        while (elems.hasNext()) {
          Vector.Element elem = elems.next();
          M.setQuick(elem.index(), n, elem.get());
        }
      } else {
        danglingVertices.add(n);
      }
    }

    Vector p = vector(graph.numVertices(), 1.0 / (double) graph.numVertices());

    double diff = Double.POSITIVE_INFINITY;

    int iteration = 0;
    while (diff > EPSILON) {

      Vector pNext = M.times(p);
      pNext.assign(Functions.mult(0.85));

      vector(graph.numVertices(), 0.15 / (double) graph.numVertices());
      double danglingRank = 0;
      IntArrayList danglingVertexIndexes = danglingVertices.keys();
      for (int n = 0; n < danglingVertices.size(); n++) {
        danglingRank += p.get(danglingVertexIndexes.get(n));
      }
      pNext.assign(Functions.plus(danglingRank / (double) graph.numVertices()));

      diff = pNext.minus(p).norm(1);

      p = stateForNextIteration(iteration, pNext);

      collector.collect(p.clone());
      iteration++;
    }

    collector.markConverged();

    return iteration;
  }

  protected Vector stateForNextIteration(int iteration, Vector pNext) {
    return pNext;
  }

  private Vector vector(int numVertices, double initialValue) {
    double[] initialValues = new double[numVertices];
    Arrays.fill(initialValues, initialValue);
    return new DenseVector(initialValues);
  }
}

