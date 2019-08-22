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
import org.terracotta.entity.BasicServiceConfiguration;

import org.terracotta.entity.ClientCommunicator;
import org.terracotta.entity.ConcurrencyStrategy;
import org.terracotta.entity.ConfigurationException;
import org.terracotta.entity.MessageCodec;
import org.terracotta.entity.NoConcurrencyStrategy;
import org.terracotta.entity.EntityServerService;
import org.terracotta.entity.ServiceException;
import org.terracotta.entity.ServiceRegistry;
import org.terracotta.entity.SyncMessageCodec;


public class TerracottaEchoServerEntityService implements EntityServerService<EchoMessage, EchoResponse> {
  private final EchoCodec CODEC = new EchoCodec();
  private final EchoSyncMessageCodec SYNC_CODEC = new EchoSyncMessageCodec();
  private final ConcurrencyStrategy<EchoMessage> CONCURRENCY = new NoConcurrencyStrategy<>();
  
  @Override
  public long getVersion() {
    return Echo.VERSION;
  }

  @Override
  public ConcurrencyStrategy<EchoMessage> getConcurrencyStrategy(byte[] configuration) {
    return CONCURRENCY;
  }

  @Override
  public MessageCodec<EchoMessage, EchoResponse> getMessageCodec() {
    return CODEC;
  }

  @Override
  public SyncMessageCodec<EchoMessage> getSyncMessageCodec() {
    return SYNC_CODEC;
  }

  @Override
  public boolean handlesEntityType(String typeName) {
    return "org.terracotta.entity.echo.Echo".equals(typeName);
  }

  @Override
  public TerracottaEchoServer createActiveEntity(ServiceRegistry registry, byte[] configuration) throws ConfigurationException {
    try {
      ClientCommunicator clientCommunicator = registry.getService(new BasicServiceConfiguration<>(ClientCommunicator.class));
      // This entity absolutely depends on the client communicator so it better exist.
      EchoConfig echoConfig = EchoConfig.deserialize(configuration);
      EchoState state = new EchoState();

      EchoService echo = registry.getService(new BasicServiceConfiguration<>(EchoService.class));
      return new TerracottaEchoServer(clientCommunicator, echo, state);
    } catch (ServiceException se) {
      throw new ConfigurationException("service exception", se);
    }
  }

  @Override
  public TerracottaEchoServerPassive createPassiveEntity(ServiceRegistry registry, byte[] configuration) throws ConfigurationException {
    return new TerracottaEchoServerPassive();
  }
}
