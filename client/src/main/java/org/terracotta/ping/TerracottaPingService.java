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

import org.terracotta.entity.EntityClientEndpoint;
import org.terracotta.entity.EntityClientService;
import org.terracotta.entity.MessageCodec;

@SuppressWarnings("rawtypes")
public class TerracottaPingService implements EntityClientService<Ping, Void, PingMessage, PingResponse, Void> {
  private final PingCodec CODEC = new PingCodec();
  @Override
  public boolean handlesEntityType(Class<Ping> cls) {
    return cls == Ping.class;
  }

  @Override
  public byte[] serializeConfiguration(Void configuration) {
    return new byte[0];
  }

  @Override
  public Void deserializeConfiguration(byte[] configuration) {
    return null;
  }

  @Override
  public Ping create(EntityClientEndpoint endpoint, Void ignored) {
    return new TerracottaPing(endpoint);
  }

  @Override
  public MessageCodec<PingMessage, PingResponse> getMessageCodec() {
    return CODEC;
  }
}
