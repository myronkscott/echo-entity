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
package org.terracotta.entity.echo;

import org.terracotta.echo.Echo;
import org.terracotta.echo.EchoCodec;
import org.terracotta.echo.EchoConfig;
import org.terracotta.echo.EchoMessage;
import org.terracotta.echo.EchoResponse;
import org.terracotta.entity.EntityClientEndpoint;
import org.terracotta.entity.EntityClientService;
import org.terracotta.entity.MessageCodec;

@SuppressWarnings("rawtypes")
public class TerracottaEchoService implements EntityClientService<Echo, EchoConfig, EchoMessage, EchoResponse, Void> {
  private final EchoCodec CODEC = new EchoCodec();
  @Override
  public boolean handlesEntityType(Class<Echo> cls) {
    return cls == Echo.class;
  }

  @Override
  public byte[] serializeConfiguration(EchoConfig configuration) {
    if (configuration == null) {
      return new byte[0];
    } else {
      return configuration.serialize();
    }
  }

  @Override
  public EchoConfig deserializeConfiguration(byte[] configuration) {
    if (configuration.length == 0) {
      return null;
    } else {
      return EchoConfig.deserialize(configuration);
    }
  }

  @Override
  public Echo create(EntityClientEndpoint endpoint, Void ignored) {
    return new TerracottaEcho(endpoint);
  }

  @Override
  public MessageCodec<EchoMessage, EchoResponse> getMessageCodec() {
    return CODEC;
  }
}
