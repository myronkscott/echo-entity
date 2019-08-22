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
package org.terracotta.ping;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.terracotta.entity.MessageCodec;
import org.terracotta.entity.MessageCodecException;



public class PingCodec implements MessageCodec<PingMessage, PingResponse> {
  private static final Charset CHARSET = Charset.forName("ASCII");
  
  @Override
  public byte[] encodeMessage(PingMessage message) throws MessageCodecException {
    String label = message.getLabel();
    byte[] data = label.getBytes(CHARSET);
    ByteBuffer buf = ByteBuffer.allocate(Short.BYTES + data.length + 8);
    buf.putShort((short)data.length);
    buf.put(data);
    buf.putLong(message.getTime());
    return buf.array();
  }

  @Override
  public PingMessage decodeMessage(byte[] payload) throws MessageCodecException {
    ByteBuffer wrap = ByteBuffer.wrap(payload);
    int len = wrap.getShort();
    byte[] data = new byte[len];
    wrap.get(data);
    return new PingMessage(new String(data,CHARSET), wrap.getLong());
  }

  @Override
  public byte[] encodeResponse(PingResponse response) throws MessageCodecException {
    String label = response.getLabel();
    byte[] data = label.getBytes(CHARSET);
    ByteBuffer buf = ByteBuffer.allocate(Short.BYTES + data.length + 8);
    buf.putShort((short)data.length);
    buf.put(data);
    buf.putLong(response.getTime());
    return buf.array();
  }

  @Override
  public PingResponse decodeResponse(byte[] payload) throws MessageCodecException {
    ByteBuffer wrap = ByteBuffer.wrap(payload);
    int len = wrap.getShort();
    byte[] data = new byte[len];
    wrap.get(data);
    return new PingResponse(new String(data,CHARSET), wrap.getLong());
  }
}
