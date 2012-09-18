package de.tuberlin.dima.plugalong;

import de.tuberlin.dima.plugalong.algorithms.*;
import de.tuberlin.dima.plugalong.algorithms.pathproblems.ConnectedComponents;
import de.tuberlin.dima.plugalong.algorithms.pathproblems.FailingConnectedComponents;
import org.jfree.ui.RefineryUtilities;

import java.io.IOException;

public class GraphPlay {

  public static void main(String[] args) throws IOException {

    JFreeChartExecutor executor = new JFreeChartExecutor(
        new Graph[] { Graphs.FACEBOOK, /*Graphs.YOUTUBE,*/ /*Graphs.GOOGLE_WEB*/ },
    //    new GraphBasedFixpointAlgorithm[] { new PageRank(), new FailingPageRank() });
        new GraphBasedFixpointAlgorithm[] { new ConnectedComponents(), new FailingConnectedComponents() });

//    JFreeChartExecutor executor = new JFreeChartExecutor(
//        new Graph[] { /*Graphs.ENRON, Graphs.FACEBOOK*/ Graphs.YOUTUBE/*, Graphs.GOOGLE_WEB*/ },
//        new GraphBasedFixpointAlgorithm[] { new ConnectedComponents(), new FailingConnectedComponents() });

    executor.pack();
    RefineryUtilities.centerFrameOnScreen(executor);
    executor.setVisible(true);
  }
}
