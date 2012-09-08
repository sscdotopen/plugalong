package de.tuberlin.dima.plugalong;

import java.io.File;

public class Graphs {

  //static final Graph MINI = new Graph(new File("/home/ssc/Entwicklung/projects/plugalong/src/main/resources/mini.csv"), "mini", 4);
  static final Graph FACEBOOK = new Graph(new File("/home/ssc/Entwicklung/projects/plugalong/src/main/resources/facebook.csv"), "facebook", 63732);
  static final Graph GOOGLE_WEB = new Graph(new File("/home/ssc/Entwicklung/projects/plugalong/src/main/resources/googleweb.csv"), "googleweb", 916428);
  static final Graph YOUTUBE = new Graph(new File("/home/ssc/Entwicklung/projects/plugalong/src/main/resources/youtube.csv"), "youtube", 100199);
  static final Graph ENRON = new Graph(new File("/home/ssc/Entwicklung/projects/plugalong/src/main/resources/enron.csv"), "enron", 36692);
}
