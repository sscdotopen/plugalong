package de.tuberlin.dima.plugalong;

import de.tuberlin.dima.plugalong.algorithms.*;
import de.tuberlin.dima.plugalong.algorithms.graph.ConnectedComponents;
import de.tuberlin.dima.plugalong.algorithms.graph.FailingConnectedComponents;
import de.tuberlin.dima.plugalong.algorithms.graph.FailingPageRank;
import de.tuberlin.dima.plugalong.algorithms.graph.PageRank;
import org.jfree.ui.RefineryUtilities;

import java.io.IOException;

public class GraphPlay {

  public static void main(String[] args) throws IOException {

    JFreeChartExecutor executor = new JFreeChartExecutor(
        new Graph[] { /*Graphs.FACEBOOK,*/ Graphs.YOUTUBE, /*Graphs.GOOGLE_WEB*/ },
        new GraphBasedFixpointAlgorithm[] { new PageRank(), new FailingPageRank() });

//    JFreeChartExecutor executor = new JFreeChartExecutor(
//        new Graph[] { /*Graphs.ENRON, Graphs.FACEBOOK*/ Graphs.YOUTUBE/*, Graphs.GOOGLE_WEB*/ },
//        new GraphBasedFixpointAlgorithm[] { new ConnectedComponents(), new FailingConnectedComponents() });

    executor.pack();
    RefineryUtilities.centerFrameOnScreen(executor);
    executor.setVisible(true);
  }
}
