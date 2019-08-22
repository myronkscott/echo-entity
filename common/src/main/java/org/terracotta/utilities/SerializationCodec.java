/*
 *
 *  The contents of this file are subject to the Terracotta Public License Version
 *  2.0 (the "License"); You may not use this file except in compliance with the
 *  License. You may obtain a copy of the License at
 *
 *  http://terracotta.org/legal/terracotta-public-license.
 *
 *  Software distributed under the License is distributed on an "AS IS" basis,
 *  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 *  the specific language governing rights and limitations under the License.
 *
 *  The Covered Software is Connection API.
 *
 *  The Initial Developer of the Covered Software is
 *  Terracotta, Inc., a Software AG company
 *
 */
package org.terracotta.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Implementation to encode/decode VOLTRON messages using JAVA serialization.
 * Similar to Proxy's Layer codec. Picked up from terracotta-platform.
 *
 * @author RKAV
 */
public class SerializationCodec implements Codec {
  @Override
  public byte[] encode(final Object value) {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    try {
      ObjectOutputStream oout = new ObjectOutputStream(bout);
      oout.writeObject(value);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        bout.close();
      } catch (IOException ignore) {
      }
    }
    return bout.toByteArray();
  }

  @Override
  public Object decode(final byte[] buffer) {
    ByteArrayInputStream bin = new ByteArrayInputStream(buffer);
    try {
      ObjectInputStream ois = new ObjectInputStream(bin);
      try {
        return ois.readObject();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      } catch (Exception e) {
        System.out.println("" + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException(e);
      } finally {
        ois.close();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        bin.close();
      } catch (IOException ignore) {
      }
    }
  }
}