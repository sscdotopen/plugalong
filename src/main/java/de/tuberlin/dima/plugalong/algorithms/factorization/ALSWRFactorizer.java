package de.tuberlin.dima.plugalong.algorithms.factorization;

import com.google.common.collect.Lists;
import de.tuberlin.dima.plugalong.ErrorCollector;
import de.tuberlin.dima.plugalong.algorithms.RatingDataBasedFixpointAlgorithm;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FullRunningAverage;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.common.RunningAverage;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.common.RandomUtils;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.als.AlternatingLeastSquaresSolver;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class ALSWRFactorizer implements RatingDataBasedFixpointAlgorithm {


  protected final Random RANDOM = new Random(1l);

  private DataModel dataModel;
  private FastByIDMap<Integer> userIDMapping;
  private FastByIDMap<Integer> itemIDMapping;
  /** number of features used to compute this factorization */
  private int numFeatures;
  /** parameter to control the regularization */
  private double lambda;


  @Override
  public String getTitle() {
    return "AlternatingLeastSquares";
  }

  private void buildMappings() throws TasteException {
    userIDMapping = createIDMapping(dataModel.getNumUsers(), dataModel.getUserIDs());
    itemIDMapping = createIDMapping(dataModel.getNumItems(), dataModel.getItemIDs());
  }

  protected Integer userIndex(long userID) {
    Integer userIndex = userIDMapping.get(userID);
    if (userIndex == null) {
      userIndex = userIDMapping.size();
      userIDMapping.put(userID, userIndex);
    }
    return userIndex;
  }

  protected Integer itemIndex(long itemID) {
    Integer itemIndex = itemIDMapping.get(itemID);
    if (itemIndex == null) {
      itemIndex = itemIDMapping.size();
      itemIDMapping.put(itemID, itemIndex);
    }
    return itemIndex;
  }

  private static FastByIDMap<Integer> createIDMapping(int size, LongPrimitiveIterator idIterator) {
    FastByIDMap<Integer> mapping = new FastByIDMap<Integer>(size);
    int index = 0;
    while (idIterator.hasNext()) {
      mapping.put(idIterator.nextLong(), index++);
    }
    return mapping;
  }

  static class Features {

    protected final Random RANDOM = new Random(123l);

    private final DataModel dataModel;
    private final int numFeatures;

    private final double[][] M;
    private final double[][] U;

    Features(ALSWRFactorizer factorizer) throws TasteException {
      dataModel = factorizer.dataModel;
      numFeatures = factorizer.numFeatures;
      M = new double[dataModel.getNumItems()][numFeatures];
      LongPrimitiveIterator itemIDsIterator = dataModel.getItemIDs();
      while (itemIDsIterator.hasNext()) {
        long itemID = itemIDsIterator.nextLong();
        int itemIDIndex = factorizer.itemIndex(itemID);
        M[itemIDIndex][0] = averageItemRating(itemID);
        for (int feature = 1; feature < numFeatures; feature++) {
          M[itemIDIndex][feature] = RANDOM.nextDouble() * 0.1;
        }
      }
      U = new double[dataModel.getNumUsers()][numFeatures];
    }

    protected Vector getUserFeatureColumn(int index) {
      return new DenseVector(U[index]);
    }

    protected Vector getItemFeatureColumn(int index) {
      return new DenseVector(M[index]);
    }

    protected void setFeatureColumnInU(int idIndex, Vector vector) {
      setFeatureColumn(U, idIndex, vector);
    }

    protected void setFeatureColumnInM(int idIndex, Vector vector) {
      setFeatureColumn(M, idIndex, vector);
    }

    protected void setFeatureColumn(double[][] matrix, int idIndex, Vector vector) {
      for (int feature = 0; feature < numFeatures; feature++) {
        matrix[idIndex][feature] = vector.get(feature);
      }
    }

    protected double averageItemRating(long itemID) throws TasteException {
      PreferenceArray prefs = dataModel.getPreferencesForItem(itemID);
      RunningAverage avg = new FullRunningAverage();
      for (Preference pref : prefs) {
        avg.addDatum(pref.getValue());
      }
      return avg.getAverage();
    }

    protected double averageUserRating(long userID) throws TasteException {
      PreferenceArray prefs = dataModel.getPreferencesFromUser(userID);
      RunningAverage avg = new FullRunningAverage();
      for (Preference pref : prefs) {
        avg.addDatum(pref.getValue());
      }
      return avg.getAverage();
    }
  }

  @Override
  public int run(DataModel dataModel, ErrorCollector collector) {

    try {

      numFeatures = 10;
      //lambda = 0.0000001;
      lambda = 0.065;

      this.dataModel = dataModel;
      buildMappings();

      System.out.println("starting to compute the factorization...");
      final AlternatingLeastSquaresSolver solver = new AlternatingLeastSquaresSolver();

      Features f = new Features(this);

      //double lastRmse = 2;
      double lastRmse = 10;
      double diff = 1;

      int iteration = 0;
      while (Math.abs(diff) > 0.0001) {
        System.out.println("iteration " + iteration);

        final Features features = f;
        /* fix M - compute U */
        ExecutorService queue = createQueue();
        LongPrimitiveIterator userIDsIterator = dataModel.getUserIDs();
        try {
          while (userIDsIterator.hasNext()) {
            final long userID = userIDsIterator.nextLong();
            final LongPrimitiveIterator itemIDsFromUser = dataModel.getItemIDsFromUser(userID).iterator();
            final PreferenceArray userPrefs = dataModel.getPreferencesFromUser(userID);
            queue.execute(new Runnable() {
              @Override
              public void run() {
                List<Vector> featureVectors = Lists.newArrayList();
                while (itemIDsFromUser.hasNext()) {
                  long itemID = itemIDsFromUser.nextLong();
                  featureVectors.add(features.getItemFeatureColumn(itemIndex(itemID)));
                }
                Vector userFeatures = solver.solve(featureVectors, ratingVector(userPrefs), lambda, numFeatures);
                features.setFeatureColumnInU(userIndex(userID), userFeatures);
              }
            });
          }
        } finally {
          queue.shutdown();
          try {
            queue.awaitTermination(dataModel.getNumUsers(), TimeUnit.SECONDS);
          } catch (InterruptedException e) {
            throw new RuntimeException("Error when computing user features", e);
          }
        }

        /* fix U - compute M */
        queue = createQueue();
        LongPrimitiveIterator itemIDsIterator = dataModel.getItemIDs();
        try {
          while (itemIDsIterator.hasNext()) {
            final long itemID = itemIDsIterator.nextLong();
            final PreferenceArray itemPrefs = dataModel.getPreferencesForItem(itemID);
            queue.execute(new Runnable() {
              @Override
              public void run() {
                List<Vector> featureVectors = Lists.newArrayList();
                for (Preference pref : itemPrefs) {
                  long userID = pref.getUserID();
                  featureVectors.add(features.getUserFeatureColumn(userIndex(userID)));
                }
                Vector itemFeatures = solver.solve(featureVectors, ratingVector(itemPrefs), lambda, numFeatures);
                features.setFeatureColumnInM(itemIndex(itemID), itemFeatures);
              }
            });
          }
        } finally {
          queue.shutdown();
          try {
            queue.awaitTermination(dataModel.getNumItems(), TimeUnit.SECONDS);
          } catch (InterruptedException e) {
            throw new RuntimeException("Error when computing item features", e);
          }
        }

        FullRunningAverage avg = new FullRunningAverage();
        LongPrimitiveIterator userIDs = dataModel.getUserIDs();
        while (userIDs.hasNext()) {
          long userID = userIDs.nextLong();
          for (Preference preference : dataModel.getPreferencesFromUser(userID)) {
            double prediction = features.getUserFeatureColumn(userIndex(userID)).dot(features.getItemFeatureColumn(itemIndex(preference.getItemID())));
            double err = prediction - preference.getValue();
            avg.addDatum(err * err);
          }
        }

        double rmse = Math.sqrt(avg.getAverage());
        diff = lastRmse - rmse;
        System.out.println("RMSE " + rmse);
        System.out.println("Error " + diff);
        lastRmse = rmse;
        if (iteration > 0) {
          //collector.collect(iteration, diff);
        }
        iteration++;
        f = stateForNextIteration(dataModel, iteration, f);
      }

      System.out.println("finished computation of the factorization...");

      return iteration;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected Features stateForNextIteration(DataModel dataModel, int iteration, Features features) {
    return features;
  }

  protected ExecutorService createQueue() {
    return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
  }

  protected Vector ratingVector(PreferenceArray prefs) {
    double[] ratings = new double[prefs.length()];
    for (int n = 0; n < prefs.length(); n++) {
      ratings[n] = prefs.get(n).getValue();
    }
    return new DenseVector(ratings);
  }
}