/*
 * Copyright (C) 2012 Sebastian Schelter <sebastian.schelter [at] tu-berlin.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package de.tuberlin.dima.plugalong;

import com.google.common.base.Preconditions;
import com.google.common.collect.UnmodifiableIterator;
import org.apache.mahout.common.iterator.FileLineIterator;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * A class to iterate over a ratings file in a streaming manner
 */
public class Graph extends UnmodifiableIterator<Edge> implements Iterable<Edge> {

  private final File edges;
  private final String name;
  private Iterator<String> lines;
  private final Edge edge;
  private final int numVertices;

  private static final Pattern SEP = Pattern.compile("[,\t]");

  public Graph(File edges, String name, int numVertices) {
    Preconditions.checkNotNull(edges);
    this.edges = edges;
    this.name = name;
    this.edge = new Edge();
    this.numVertices = numVertices;
    try {
      this.lines = new FileLineIterator(edges);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public String name() {
    return name;
  }

  public int numVertices() {
    return numVertices;
  }

  @Override
  public Iterator<Edge> iterator() {
    return this;
  }

  @Override
  public Graph clone() {
    return new Graph(edges, name, numVertices);
  }

  @Override
  public boolean hasNext() {
    return lines.hasNext();
  }

  @Override
  public Edge next() {
    String line = lines.next();
    String[] parts = SEP.split(line);
    int user = Integer.parseInt(parts[0]);
    int item = Integer.parseInt(parts[1]);
    double value = parts.length > 2 ? Double.parseDouble(parts[2]) : 1.0;

    edge.set(user, item, value);

    return edge;
  }

}