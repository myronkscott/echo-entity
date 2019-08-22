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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.terracotta.echo.AbstractEchoOperation;
import org.terracotta.echo.Echo;
import org.terracotta.echo.EchoConfig;
import org.terracotta.echo.EchoListener;
import org.terracotta.echo.EchoMessage;
import org.terracotta.echo.EchoOperation;
import org.terracotta.echo.EchoResponse;
import org.terracotta.entity.EndpointDelegate;
import org.terracotta.entity.EntityClientEndpoint;
import org.terracotta.entity.EntityResponse;
import org.terracotta.entity.InvocationBuilder;
import org.terracotta.entity.MessageCodecException;


public class TerracottaEcho<T> implements Echo<T>, EndpointDelegate {
  private final EntityClientEndpoint<EchoMessage, EchoResponse> endpoint;
  @SuppressWarnings("unused")
  private final EchoConfig config;
  private EchoListener listener;
  private final ExecutorService messages = Executors.newSingleThreadExecutor();

  public TerracottaEcho(EntityClientEndpoint<EchoMessage, EchoResponse> endpoint) {
    this.endpoint = endpoint;
    this.config = EchoConfig.deserialize(endpoint.getEntityConfiguration());
    endpoint.setDelegate(this);
  }

  @Override
  public void setListener(EchoListener listener) {
    this.listener = listener;
  }
  
  @Override
  public void close() {
    this.endpoint.close();
    messages.shutdown();
    try {
      messages.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException ie) {
      throw new RuntimeException(ie);
    }
  }

  @Override
  public T echoAsResponse(T message) {
    return invokeAfterReceivedWithReturn(new EchoOperation(AbstractEchoOperation.Type.ECHO_SYNC, message));
  }

  @Override
  public void echoAsync(T message) {
    invokeAfterReceivedWithReturn(new EchoOperation(AbstractEchoOperation.Type.ECHO_ASYNC, message));
  }
  
  @Override
  public boolean echoConfirm(T message) {
    return (Boolean)invokeAfterReceivedWithReturn(new EchoOperation(AbstractEchoOperation.Type.ECHO_CONFIRM, message));
  }

  @Override
  public Future<T> echoAsFutureResponseWithAck(T message, boolean waitOnSent, boolean waitOnReceived, boolean waitOnCompleted) {
    return invokeAsFuture(new EchoOperation(AbstractEchoOperation.Type.ECHO_SYNC, message), waitOnSent, waitOnReceived, waitOnCompleted);
  }

  private Future<T> invokeAsFuture(AbstractEchoOperation operation, boolean waitOnSent, boolean waitOnReceived, boolean waitOnCompleted) {
    try {
      InvocationBuilder<EchoMessage, EchoResponse> invocation = endpoint.beginInvoke().message(new EchoMessage(operation));
      // See if we want an ack.
      if (waitOnSent) {
        invocation = invocation.ackSent();
      }
      if (waitOnReceived) {
        invocation = invocation.ackReceived();
      }
      if (waitOnCompleted) {
        invocation = invocation.ackCompleted();
      }
      return new DecodingFuture<>(invocation.replicate(true).invoke(), (EchoResponse from) -> (T) from.response);
    } catch (MessageCodecException e) {
      // We aren't expecting or handling this.
      throw new RuntimeException("Invoking '" + operation.operationType().name() + "'", e);
    }
  }

  private T invokeAfterReceivedWithReturn(AbstractEchoOperation operation) {
    boolean waitOnSent = false;
    boolean waitOnReceived = true;
    boolean waitOnCompleted = false;
    try {
      Future<T> future = invokeAsFuture(operation, waitOnSent, waitOnReceived, waitOnCompleted);
      return future.get();
    } catch (InterruptedException e) {
      // We were interrupted so the call probably didn't complete.  We want to throw this.
      throw new RuntimeException("Interrupted while invoking '" + operation.operationType().name() + "'", e);
    } catch (ExecutionException e) {
      // We weren't expecting an exception here.
      throw new RuntimeException("EntityException in '" + operation.operationType().name() + "'", e);
    }
  }

  @Override
  public void handleMessage(EntityResponse fromServer) {
    if (null != this.listener) {
      String message = (String) ((EchoResponse)fromServer).response;
      messages.submit(()->this.listener.onEcho(message));
    }
  }

  @Override
  public byte[] createExtendedReconnectData() {
    // Ignored for this entity.
    return null;
  }

  @Override
  public void didDisconnectUnexpectedly() {
    // There are some tests where we force a disconnect but we don't have special handling for it, here.
  }
}
