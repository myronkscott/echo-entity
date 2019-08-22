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

import org.terracotta.echo.AbstractEchoOperation;
import static org.terracotta.echo.AbstractEchoOperation.Type.ECHO_ASYNC;
import static org.terracotta.echo.AbstractEchoOperation.Type.ECHO_CONFIRM;
import static org.terracotta.echo.AbstractEchoOperation.Type.ECHO_SYNC;
import org.terracotta.echo.EchoMessage;
import org.terracotta.echo.EchoOperation;
import org.terracotta.echo.EchoResponse;
import org.terracotta.entity.ActiveInvokeContext;
import org.terracotta.entity.ActiveServerEntity;
import org.terracotta.entity.ClientCommunicator;
import org.terracotta.entity.ClientDescriptor;
import org.terracotta.entity.MessageCodecException;
import org.terracotta.entity.PassiveSynchronizationChannel;


public class TerracottaEchoServer implements ActiveServerEntity<EchoMessage, EchoResponse> {
  private final ClientCommunicator communicator;
  private final EchoService echoService;
  private final EchoState echoState;

  public TerracottaEchoServer(ClientCommunicator communicator, EchoState state) {
    this.communicator = communicator;
    this.echoState = state;
    this.echoService = null;
  }

  public TerracottaEchoServer(ClientCommunicator communicator, EchoService service, EchoState state) {
    this.communicator = communicator;
    this.echoState = state;
    this.echoService = service;
  }
  
  @Override
  public void connected(ClientDescriptor clientDescriptor) {
  }

  @Override
  public ReconnectHandler startReconnect() {
    return (ClientDescriptor clientDescriptor, byte[] extendedReconnectData)->{
    // Do nothing.
    };
  }

  @Override
  public void disconnected(ClientDescriptor clientDescriptor) {
  }

  @Override
  public EchoResponse invokeActive(ActiveInvokeContext context, EchoMessage processed) {
    EchoResponse toEcho = null;
    AbstractEchoOperation input = processed.getOperation();
    switch (input.operationType()) {
      case ECHO_SYNC:
        // We just want to send the echo as the response.
        {
          EchoOperation echoOperation = (EchoOperation) input;
          Object message = echoOperation.getMessage();
          if (this.echoService != null) {
            message = this.echoService.ping(message);
          }
          toEcho = new EchoResponse(this.echoState.echoMessage(message));
        }
        break;
      case ECHO_ASYNC:
        // Here, we want to send the echo as an async message from server->client.
        {
          EchoOperation echoOperation = (EchoOperation) input;
          Object message = echoOperation.getMessage();
          if (this.echoService != null) {
            message = this.echoService.ping(message);
          }
          String asyncSend = (String) this.echoState.echoMessage(message);
          try {
            communicator.sendNoResponse(context.getClientDescriptor(), new EchoResponse(asyncSend));
          } catch (MessageCodecException e) {
            throw new RuntimeException(e);
          }
        }
        break;
      case ECHO_CONFIRM:
        {
          EchoOperation echoOperation = (EchoOperation) input;
          Object message = echoOperation.getMessage();
          if (this.echoService != null) {
            message = this.echoService.ping(message);
          }
          toEcho = new EchoResponse(this.echoState.verifyLast(message));
        }
        break;
      default:
        throw new UnsupportedOperationException("Unknown operation");
    }

    return toEcho;
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
