package de.tuberlin.dima.plugalong;

import org.apache.mahout.math.CardinalityException;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.SparseRowMatrix;
import org.apache.mahout.math.Vector;

public class FastSparseRowMatrix extends SparseRowMatrix {

  public FastSparseRowMatrix(int rows, int columns, Vector[] rowVectors) {
    super(rows, columns, rowVectors);
  }

  public FastSparseRowMatrix(int rows, int columns, boolean randomAccess) {
    super(rows, columns, randomAccess);
  }

  public FastSparseRowMatrix(int rows, int columns, Vector[] vectors, boolean shallowCopy, boolean randomAccess) {
    super(rows, columns, vectors, shallowCopy, randomAccess);
  }

  public FastSparseRowMatrix(int rows, int columns) {
    super(rows, columns);
  }

  @Override
  public Vector times(Vector v) {
    int columns = columnSize();
    if (columns != v.size()) {
      throw new CardinalityException(columns, v.size());
    }
    int rows = rowSize();
    Vector w = new DenseVector(rows);
    for (int row = 0; row < rows; row++) {
      //w.setQuick(row, v.dot(viewRow(row)));
      //System.out.println(row + ":" + viewRow(row).getClass().getName());
      w.setQuick(row, viewRow(row).dot(v));
    }
    return w;
  }

}
