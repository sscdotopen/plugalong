package de.tuberlin.dima.plugalong.algorithms.factorization;

import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.math.Vector;

public class FailingALSWRFactorizer extends ALSWRFactorizer {

  @Override
  public String getTitle() {
    return "FailingALSWRFactorizer";
  }

  @Override
  protected Features stateForNextIteration(DataModel dataModel, int iteration, Features features) {

    try {
      if (iteration == 3) {
        LongPrimitiveIterator userIDs = dataModel.getUserIDs();
        while (userIDs.hasNext()) {
          long userID = userIDs.nextLong();
          if (RANDOM.nextDouble() < 0.025) {
            int userIndex = userIndex(userID);
            Vector userFeatureVector = features.getUserFeatureColumn(userIndex);

            userFeatureVector.setQuick(0, features.averageUserRating(userID));
            for (int feature = 1; feature < userFeatureVector.size(); feature++) {
              userFeatureVector.setQuick(feature, RANDOM.nextDouble() * 0.1);
            }
            features.setFeatureColumnInU(userIndex, userFeatureVector);
          }
        }

        LongPrimitiveIterator itemIDs = dataModel.getItemIDs();
        while (itemIDs.hasNext()) {
          long itemID = itemIDs.nextLong();
          if (RANDOM.nextDouble() < 0.025) {
            int itemIndex = itemIndex(itemID);
            Vector itemFeatureVector = features.getItemFeatureColumn(itemIndex);

            itemFeatureVector.setQuick(0, features.averageItemRating(itemID));
            for (int feature = 1; feature < itemFeatureVector.size(); feature++) {
              itemFeatureVector.setQuick(feature, RANDOM.nextDouble() * 0.1);
            }
            features.setFeatureColumnInM(itemIndex, itemFeatureVector);
          }
        }
      }


      return features;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }
}
