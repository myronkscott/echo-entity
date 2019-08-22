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

import com.tc.classloader.CommonComponent;
import java.util.concurrent.Future;
import org.terracotta.connection.entity.Entity;


// tag::classBody[]
@CommonComponent()
public interface Echo<T> extends Entity {
  public static final long VERSION = 1;

  public void setListener(EchoListener listener);
  
  public T echoAsResponse(T message); // <1>
  public void echoAsync(T message); // <2>
  public boolean echoConfirm(T message); // <3>

  /**
   * Similar to echoAsResponse, above, but internally waits on the given ack, prior to returning.
   * 
   * @param message The message to echo.
   * @param waitOnSent true if we should block on the sent ack
   * @param waitOnReceived true if we should block on the received ack
   * @param waitOnCompleted true if we should block on the completed ack
   * @return The InvokeFuture to access the asynchronous response.
   */
  public Future<T> echoAsFutureResponseWithAck(T message, boolean waitOnSent, boolean waitOnReceived, boolean waitOnCompleted);
}
// tag::classBody[]
