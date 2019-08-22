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

import java.util.Collections;
import java.util.Set;
import org.terracotta.entity.BasicServiceConfiguration;
import org.terracotta.entity.ConcurrencyStrategy;
import org.terracotta.entity.ConfigurationException;
import org.terracotta.entity.MessageCodec;
import org.terracotta.entity.EntityServerService;
import org.terracotta.entity.ExecutionStrategy;
import org.terracotta.entity.IEntityMessenger;
import org.terracotta.entity.ServiceException;
import org.terracotta.entity.ServiceRegistry;
import org.terracotta.entity.SyncMessageCodec;


public class TerracottaPingServerEntityService implements EntityServerService<PingMessage, PingResponse> {
  private final PingCodec CODEC = new PingCodec();
  private final ConcurrencyStrategy<PingMessage> CONCURRENCY = new ConcurrencyStrategy<PingMessage>() {
    @Override
    public int concurrencyKey(PingMessage message) {
      return ConcurrencyStrategy.UNIVERSAL_KEY;
    }

    @Override
    public Set<Integer> getKeysForSynchronization() {
      return Collections.emptySet();
    }
      
  };
  
  @Override
  public long getVersion() {
    return 1;
  }

  @Override
  public ExecutionStrategy<PingMessage> getExecutionStrategy(byte[] configuration) {
    return new ExecutionStrategy<PingMessage>() {
      @Override
      public ExecutionStrategy.Location getExecutionLocation(PingMessage message) {
        if (!message.getLabel().equals("PASSIVE")) {
          return ExecutionStrategy.Location.ACTIVE;
        } else {
          return ExecutionStrategy.Location.PASSIVE;
        }
      }
    };
  }

  @Override
  public ConcurrencyStrategy<PingMessage> getConcurrencyStrategy(byte[] configuration) {
    return CONCURRENCY;
  }

  @Override
  public MessageCodec<PingMessage, PingResponse> getMessageCodec() {
    return CODEC;
  }

  @Override
  public SyncMessageCodec<PingMessage> getSyncMessageCodec() {
    return null;
  }

  @Override
  public boolean handlesEntityType(String typeName) {
    return "org.terracotta.ping.Ping".equals(typeName);
  }

  @Override
  public TerracottaPingServer createActiveEntity(ServiceRegistry registry, byte[] configuration) throws ConfigurationException {
    try {
      IEntityMessenger<PingMessage, PingResponse> messenger = (IEntityMessenger)registry.getService(new BasicServiceConfiguration<>(IEntityMessenger.class));
      return new TerracottaPingServer(messenger);
    } catch (ServiceException se) {
      throw new ConfigurationException("service exception", se);
    }
  }

  @Override
  public TerracottaPingServer createPassiveEntity(ServiceRegistry registry, byte[] configuration) throws ConfigurationException {
    return new TerracottaPingServer(null);
  }
}
