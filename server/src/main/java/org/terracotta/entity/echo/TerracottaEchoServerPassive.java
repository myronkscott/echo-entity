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

import org.terracotta.echo.EchoMessage;
import org.terracotta.echo.EchoResponse;
import org.terracotta.entity.InvokeContext;
import org.terracotta.entity.PassiveServerEntity;


public class TerracottaEchoServerPassive implements PassiveServerEntity<EchoMessage, EchoResponse> {
  @Override
  public void invokePassive(InvokeContext context, EchoMessage input) {
    // Passives do not do anything for this entity type.
  }

  @Override
  public void createNew() {
    // No create logic.
  }

  @Override
  public void destroy() {
    // Nothing to destroy
  }

  @Override
  public void startSyncEntity() {
    // Do nothing.
  }

  @Override
  public void endSyncEntity() {
    // Do nothing.
  }

  @Override
  public void startSyncConcurrencyKey(int concurrencyKey) {
    // TODO:  Add synchronization support.
    throw new UnsupportedOperationException();
  }

  @Override
  public void endSyncConcurrencyKey(int concurrencyKey) {
    // TODO:  Add synchronization support.
    throw new UnsupportedOperationException();
  }
}
