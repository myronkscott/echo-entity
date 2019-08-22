/*
 * All content copyright (c) 2014 Terracotta, Inc., except as may otherwise
 * be noted in a separate copyright notice. All rights reserved.
 */
package org.terracotta.ping;

import org.terracotta.entity.EntityMessage;

/**
 *
 */
public class PingMessage implements EntityMessage {
  private final String label;
  private final long time;
  

  public PingMessage(String label, long time) {
    this.label = label;
    this.time = time;
  }
  
  public long getTime() {
    return this.time;
  }

  public String getLabel() {
    return label;
  }
  
  
}
