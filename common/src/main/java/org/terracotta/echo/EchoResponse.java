/*
 * All content copyright (c) 2014 Terracotta, Inc., except as may otherwise
 * be noted in a separate copyright notice. All rights reserved.
 */
package org.terracotta.echo;

import org.terracotta.entity.EntityResponse;


public class EchoResponse implements EntityResponse {
  public final Object response;
  
  public EchoResponse(Object response) {
    this.response = response;
  }
}
