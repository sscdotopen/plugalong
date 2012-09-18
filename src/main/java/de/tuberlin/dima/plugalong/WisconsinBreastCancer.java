package de.tuberlin.dima.plugalong;

import de.tuberlin.dima.plugalong.algorithms.linearmodels.RegularizedLogisticRegression;
import org.apache.mahout.common.iterator.FileLineIterator;
import org.apache.mahout.math.DenseVector;

import java.io.File;
import java.io.IOException;
import org.apache.mahout.math.Vector;

public class WisconsinBreastCancer {

  public static void main(String[] args) throws IOException {

    FileLineIterator lines =
        new FileLineIterator(new File("/home/ssc/Entwicklung/projects/plugalong/src/main/resources/wdbc.data"));

    TrainingExample[] examples = new TrainingExample[569];
    int example = 0;

    while (lines.hasNext()) {
      String line = lines.next();
      String[] parts = line.split(",");

      double label = "M".equals(parts[1]) ? 1 : 0;

      //System.out.println(label);

      Vector features = new DenseVector(30);

      for (int n = 0; n < 30; n++) {
        double feature = Double.parseDouble(parts[n + 2]);
        features.setQuick(n, feature);
      }

      examples[example++] = new TrainingExample(features, label);
    }

    Dataset dataset = new Dataset(examples, 30);

    RegularizedLogisticRegression lr = new RegularizedLogisticRegression(dataset, -0.05, 0.001, 0.8);

    lr.trainWithBatchGradientDescent(50);
    System.out.println(lr.accuracy());
    lr.trainWithBatchGradientDescent(50);
    System.out.println(lr.accuracy());
    lr.trainWithBatchGradientDescent(50);
    System.out.println(lr.accuracy());
    lr.trainWithBatchGradientDescent(50);
    System.out.println(lr.accuracy());
    lr.trainWithBatchGradientDescent(50);
    System.out.println(lr.accuracy());
  }

}
