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

import java.io.IOException;

import org.terracotta.entity.MessageCodec;
import org.terracotta.entity.MessageCodecException;
import org.terracotta.utilities.PrimitiveCodec;



public class EchoCodec implements MessageCodec<EchoMessage, EchoResponse> {

  @Override
  public byte[] encodeMessage(EchoMessage message) throws MessageCodecException {
    try {
      AbstractEchoOperation operation = message.getOperation();
      if(operation != null) {
        return OperationCodec.encode(message.getOperation());
      } else {
        throw new MessageCodecException("Corrupt Message Type", null);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public EchoMessage decodeMessage(byte[] payload) throws MessageCodecException {
    try {
      return new EchoMessage(OperationCodec.decode(payload));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public byte[] encodeResponse(EchoResponse response) throws MessageCodecException {
    try {
      Object toEncode = (null != response) ? response.response : null;
      return PrimitiveCodec.encode(toEncode);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public EchoResponse decodeResponse(byte[] payload) throws MessageCodecException {
    try {
      return new EchoResponse(PrimitiveCodec.decode(payload));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
