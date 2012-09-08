package de.tuberlin.dima.plugalong.algorithms;

import de.tuberlin.dima.plugalong.ErrorCollector;
import de.tuberlin.dima.plugalong.Graph;

public interface GraphBasedFixpointAlgorithm {

  String getTitle();
  int run(Graph graph, ErrorCollector collector);
}
