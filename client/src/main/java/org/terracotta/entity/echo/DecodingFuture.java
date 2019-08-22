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
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.terracotta.entity.EntityResponse;
import org.terracotta.entity.InvokeFuture;
import org.terracotta.exception.EntityException;


public  class DecodingFuture<F extends EntityResponse, T> implements Future<T> {
  private final InvokeFuture<F> underlying;
  private final Decoder<F, T> decoder;

  public DecodingFuture(InvokeFuture<F> invoke, Decoder<F, T> decoder) {
    this.underlying = invoke;
    this.decoder = decoder;
  }

  @Override
  public boolean isDone() {
    return underlying.isDone();
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return false;
  }

  @Override
  public boolean isCancelled() {
    return false;
  }

  @Override
  public T get() throws InterruptedException, ExecutionException {
    try {
      return decoder.decode(underlying.get());
    } catch (EntityException e) {
      throw new ExecutionException(e);
    }
  }

  @Override
  public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    try {
      return decoder.decode(underlying.getWithTimeout(timeout, unit));
    } catch (EntityException e) {
      throw new ExecutionException(e);
    }
  }

  @Override
  public String toString() {
    return underlying.toString();
  }

  public interface Decoder<F, T> {
      T decode(F from);
  }
}
