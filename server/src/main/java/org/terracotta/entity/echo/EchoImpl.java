/*
 * All content copyright (c) 2014 Terracotta, Inc., except as may otherwise
 * be noted in a separate copyright notice. All rights reserved.
 */
package org.terracotta.entity.echo;

/**
 *
 */
public class EchoImpl implements EchoService {

  @Override
  public Object ping(Object data) {
    return data;
  }
  
}
