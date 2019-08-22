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
package org.terracotta.echo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class OperationCodec {
  private static final AbstractEchoOperation.Type[] ECHO_OPERATION_TYPES = AbstractEchoOperation.Type.values();

  public static AbstractEchoOperation decode(byte[] bytes) throws IOException {
    DataInput input = new DataInputStream(new ByteArrayInputStream(bytes));
    byte type = input.readByte();
    
    switch (ECHO_OPERATION_TYPES[type]) {
      case ECHO_SYNC:
      case ECHO_ASYNC:
      case ECHO_CONFIRM:
        return EchoOperation.readFrom(ECHO_OPERATION_TYPES[type], input);
      default:
        throw new IllegalArgumentException("Unknown map operation type " + type);
    } 
  }

  public static byte[] encode(AbstractEchoOperation operation) throws IOException {
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    DataOutputStream output = new DataOutputStream(byteOut);

    output.writeByte(operation.operationType().ordinal());
    operation.writeTo(output);

    output.close();
    return byteOut.toByteArray();
  }
}
