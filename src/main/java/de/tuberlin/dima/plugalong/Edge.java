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

/**
 * Reusable class to hold a value
 */
public class Edge implements Cloneable {

  private int source;
  private int target;
  private double value;

  void set(int source, int target, double value) {
    this.source = source;
    this.target = target;
    this.value = value;
  }

  public int source() {
    return source;
  }

  public int target() {
    return target;
  }

  public double value() {
    return value;
  }

  @Override
  public Edge clone() {
    Edge clone = new Edge();
    clone.set(source, target, value);
    return clone;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Edge) {
      Edge other = (Edge) o;
      return source == other.source && target == other.target;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return 31 * source + target;
  }
}