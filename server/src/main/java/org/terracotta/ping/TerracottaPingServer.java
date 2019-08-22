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

import org.terracotta.entity.ActiveInvokeChannel;
import org.terracotta.entity.ActiveInvokeContext;
import org.terracotta.entity.ActiveServerEntity;
import org.terracotta.entity.ClientDescriptor;
import org.terracotta.entity.EntityUserException;
import org.terracotta.entity.IEntityMessenger;
import org.terracotta.entity.InvokeContext;
import org.terracotta.entity.MessageCodecException;
import org.terracotta.entity.PassiveServerEntity;
import org.terracotta.entity.PassiveSynchronizationChannel;


public class TerracottaPingServer implements ActiveServerEntity<PingMessage, PingResponse>, PassiveServerEntity<PingMessage, PingResponse> {

  private final IEntityMessenger messenger;
  
  public TerracottaPingServer(IEntityMessenger messenger) {
    this.messenger = messenger;
  }
  
  @Override
  public void connected(ClientDescriptor clientDescriptor) {
  }

  @Override
  public ReconnectHandler startReconnect() {
    return (c, e)->{
    // Do nothing.
    };
  }

  @Override
  public void disconnected(ClientDescriptor clientDescriptor) {
  }

  @Override
  public PingResponse invokeActive(ActiveInvokeContext<PingResponse> context, PingMessage processed) {
    ActiveInvokeChannel<PingResponse> response = context.openInvokeChannel();
    response.sendResponse(new PingResponse("invoke", System.currentTimeMillis()));
    try {
      messenger.messageSelf(new PingMessage("PASSIVE", System.currentTimeMillis()), r->{
        response.sendResponse(new PingResponse("passive", System.currentTimeMillis()));
        response.close();
      });
    } catch (MessageCodecException codec) {
      throw new RuntimeException(codec);
    }
    return new PingResponse("done", System.currentTimeMillis());
  }

  @Override
  public void invokePassive(InvokeContext context, PingMessage message) throws EntityUserException {
    // no op
  }

  @Override
  public void startSyncEntity() {

  }

  @Override
  public void endSyncEntity() {

  }

  @Override
  public void startSyncConcurrencyKey(int concurrencyKey) {

  }

  @Override
  public void endSyncConcurrencyKey(int concurrencyKey) {

  }
  
  

  @Override
  public void createNew() {
    // No create logic.
  }

  @Override
  public void loadExisting() {
    // We have no starting state.
  }

  @Override
  public void destroy() {
    // Nothing to destroy
  }

  @Override
  public void synchronizeKeyToPassive(PassiveSynchronizationChannel syncChannel, int concurrencyKey) {
    // TODO:  Add synchronization support.
    throw new UnsupportedOperationException();
  }
}
