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
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

public class PrimitiveCodec {

  public static byte[] encode(Object o) throws IOException {
    byte[] encoded = null;
    if (null != o) {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(bytes);
      writeTo(output, o);
      output.close();
      encoded = bytes.toByteArray();
    } else {
      encoded = new byte[0];
    }
    return encoded;
  }

  public static Object decode(byte[] bytes) throws IOException {
    Object decoded = null;
    if (bytes.length > 0) {
      DataInput input = new DataInputStream(new ByteArrayInputStream(bytes));
      decoded = readFrom(input);
    }
    return decoded;
  }

  public static Object readFrom(DataInput inputStream) throws IOException {
    byte b = inputStream.readByte();
    if (b == 0) {
      return inputStream.readUTF();
    } else if (b == 1) {
      return inputStream.readInt();
    } else if (b == 2) {
      return inputStream.readLong();
    } else if (b == 3) {
      return inputStream.readBoolean();
    }
    return null;
  }

  public static void writeTo(DataOutput os, Object o) throws IOException {
    if (o instanceof String) {
      os.write(0);
      os.writeUTF((String) o);
    } else if (o instanceof Integer) {
      os.write(1);
      os.writeInt((Integer) o);
    } else if (o instanceof Long) {
      os.write(2);
      os.writeLong((Long) o);
    } else if (o instanceof Boolean) {
      os.write(3);
      os.writeBoolean((Boolean) o);
    } else {
      os.write(0xFF); // null
    }
  }
}
