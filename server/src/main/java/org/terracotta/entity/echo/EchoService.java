/*
 * All content copyright (c) 2014 Terracotta, Inc., except as may otherwise
 * be noted in a separate copyright notice. All rights reserved.
 */
package org.terracotta.entity.echo;

import com.tc.classloader.CommonComponent;

/**
 *
 */
@CommonComponent
public interface EchoService {
  Object ping(Object data);
}
