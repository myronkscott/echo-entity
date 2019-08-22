/*
* All content copyright (c) 2014 Terracotta, Inc., except as may otherwise be noted in a separate copyright
* notice. All rights reserved.
*/
package org.terracotta.utilities;

/**
 */
public interface Codec {
  byte[] encode(Object value);
  Object decode(byte[] buffer);
}
