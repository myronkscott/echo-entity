/*
* All content copyright (c) 2014 Terracotta, Inc., except as may otherwise be noted in a separate copyright
* notice. All rights reserved.
*/
package org.terracotta.utilities;

/**
 *
 * @author RKAV
 */
public class NoCodec implements Codec {
  @Override
  public byte[] encode(Object value) {
    return (byte[])value;
  }

  @Override
  public Object decode(byte[] buffer) {
    return buffer;
  }
}
