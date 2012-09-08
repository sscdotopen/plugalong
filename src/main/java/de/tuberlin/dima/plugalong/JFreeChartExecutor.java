package de.tuberlin.dima.plugalong;

import com.google.common.collect.Lists;
import de.tuberlin.dima.plugalong.algorithms.GraphBasedFixpointAlgorithm;
import de.tuberlin.dima.plugalong.algorithms.RatingDataBasedFixpointAlgorithm;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.math.Vector;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import java.io.IOException;
import java.util.List;

public class JFreeChartExecutor extends ApplicationFrame {

  public JFreeChartExecutor(Graph[] graphs, GraphBasedFixpointAlgorithm[] algorithms) throws IOException {
    super("Convergence tests");

    JFreeChartCollector collector = new JFreeChartCollector();

    for (Graph graph : graphs) {
      for (GraphBasedFixpointAlgorithm nextAlgorithm : algorithms) {
        collector.register(nextAlgorithm.getTitle() + " - " + graph.name());
        nextAlgorithm.run(graph.clone(), collector);
      }
    }

    JFreeChart chart = ChartFactory.createXYLineChart("Convergence behavior", "iteration", "error",
        collector.fetchCollectedData(), PlotOrientation.VERTICAL, true, false, false);
    ChartPanel chartPanel = new ChartPanel(chart, false);
    chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
    chartPanel.setMouseZoomable(true, false);
    setContentPane(chartPanel);
  }

  public JFreeChartExecutor(DataModel[] dataModels, RatingDataBasedFixpointAlgorithm[] algorithms) throws IOException {
    super("Convergence tests");

    JFreeChartCollector collector = new JFreeChartCollector();

    for (DataModel dataModel : dataModels) {
      for (RatingDataBasedFixpointAlgorithm nextAlgorithm : algorithms) {
        collector.register(nextAlgorithm.getTitle());
        nextAlgorithm.run(dataModel, collector);
      }
    }

    JFreeChart chart = ChartFactory.createXYLineChart("Convergence behavior", "iteration", "error",
        collector.fetchCollectedData(), PlotOrientation.VERTICAL, true, false, false);
    ChartPanel chartPanel = new ChartPanel(chart, false);
    chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
    chartPanel.setMouseZoomable(true, false);
    setContentPane(chartPanel);
  }

  static class JFreeChartCollector implements ErrorCollector {

    private int norm = 2;

    private final XYSeriesCollection errors = new XYSeriesCollection();;
    private XYSeries errorsToFixpoint;
    private XYSeries errorsToPrevious;

    private List<Vector> states = Lists.newArrayList();

    @Override
    public void setNorm(int norm) {
      this.norm = norm;
    }

    @Override
    public void collect(Vector state) {
      states.add(state);
    }

    @Override
    public void markConverged() {
      Vector fixpoint = states.get(states.size() - 1);

      for (int n = 0; n < states.size(); n++) {
        double error = states.get(n).minus(fixpoint).norm(norm);
        errorsToFixpoint.add(n, error);

        if (n > 0) {
          error = states.get(n).minus(states.get(n - 1)).norm(norm);
          errorsToPrevious.add(n, error);
        }
      }
    }

    void register(String title) {
      states.clear();
      errorsToFixpoint = new XYSeries(title + " (error to fixpoint)");
      errorsToPrevious = new XYSeries(title + " (error to previous state)");

      errors.addSeries(errorsToFixpoint);
      errors.addSeries(errorsToPrevious);
    }

    XYSeriesCollection fetchCollectedData() {
      return errors;
    }
  }

}
