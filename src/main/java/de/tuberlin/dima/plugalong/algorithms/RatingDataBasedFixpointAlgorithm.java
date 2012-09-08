package de.tuberlin.dima.plugalong.algorithms;

import de.tuberlin.dima.plugalong.ErrorCollector;
import org.apache.mahout.cf.taste.model.DataModel;

public interface RatingDataBasedFixpointAlgorithm {

  String getTitle();
  int run(DataModel dataModel, ErrorCollector collector);
}
