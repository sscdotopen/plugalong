package de.tuberlin.dima.plugalong;

import de.tuberlin.dima.plugalong.algorithms.RatingDataBasedFixpointAlgorithm;
import de.tuberlin.dima.plugalong.algorithms.factorization.ALSWRFactorizer;
import de.tuberlin.dima.plugalong.algorithms.factorization.FailingALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.jfree.ui.RefineryUtilities;

import java.io.File;

public class RatingPlay {

  public static void main(String[] args) throws Exception {

    FileDataModel dataModel = new FileDataModel(new File("/home/ssc/Entwicklung/projects/plugalong/src/main/resources/movielens.csv"));
    //FileDataModel dataModel = new FileDataModel(new File("/home/ssc/Entwicklung/projects/plugalong/src/main/resources/bookcrossing.csv"));

    JFreeChartExecutor executor = new JFreeChartExecutor(
        new DataModel[] { dataModel },
        new RatingDataBasedFixpointAlgorithm[] { new ALSWRFactorizer(), new FailingALSWRFactorizer() });


    executor.pack();
    RefineryUtilities.centerFrameOnScreen(executor);
    executor.setVisible(true);
  }

}
