package de.tuberlin.dima.plugalong.algorithms.poweriteration;

import org.apache.mahout.math.Vector;
import org.apache.mahout.math.list.IntArrayList;
import org.apache.mahout.math.set.OpenIntHashSet;

import java.util.Iterator;
import java.util.Random;

public class FailingPageRank extends PageRank {

  private final Random random = new Random();

  @Override
  public String getTitle() {
    return "FailingPageRank";
  }

  @Override
  protected Vector stateForNextIteration(int iteration, Vector pNext) {

    if (iteration == 5 || iteration == 10 || iteration == 30) {

      OpenIntHashSet lostVertices = new OpenIntHashSet();
      double lostRank = 0;

      Iterator<Vector.Element> elems = pNext.iterateNonZero();
      while (elems.hasNext()) {
        Vector.Element elem = elems.next();
        if (random.nextDouble() < 0.20) {
          lostVertices.add(elem.index());
          lostRank += elem.get();
        }
      }

      System.out.println("\tfailure -> reinitializing " + lostVertices.size() + " vertices ");

      double reinitializedRank = lostRank / (double) lostVertices.size();

      IntArrayList lostVertexIndexes = lostVertices.keys();
      for (int n = 0; n < lostVertexIndexes.size(); n++) {
        int index = lostVertexIndexes.get(n);
        pNext.setQuick(index, reinitializedRank);
      }

    }

    return pNext;
  }
}
