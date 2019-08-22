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

import java.util.LinkedList;
import java.util.List;
import org.terracotta.entity.EntityClientEndpoint;
import org.terracotta.entity.InvokeMonitor;
import org.terracotta.entity.MessageCodecException;
import org.terracotta.exception.EntityException;


public class TerracottaPing implements Ping {
  private final EntityClientEndpoint<PingMessage, PingResponse> endpoint;

  public TerracottaPing(EntityClientEndpoint<PingMessage, PingResponse> endpoint) {
    this.endpoint = endpoint;
  }
  
  @Override
  public void close() {
    this.endpoint.close();
  }

  @Override
  public PingData[] ping() {
    try {
    PingMonitor monitor = new PingMonitor();
    PingResponse resp = endpoint.beginInvoke()
        .message(new PingMessage("START", System.currentTimeMillis()))
        .ackReceived()
        .ackSent()
        .ackRetired()
        .ackCompleted()
        .asDeferredResponse()
        .monitor(monitor)
        .invoke()
        .get();
    
    monitor.getPingData().add(new PingData(resp.getLabel(), resp.getTime()));
    return monitor.toArray();
    } catch (EntityException | InterruptedException | MessageCodecException me) {
      throw new RuntimeException(me);
    }
  }

  private static class PingMonitor implements InvokeMonitor<PingResponse> {
    private final List<PingData> agg = new LinkedList<>();
    @Override
    public void accept(PingResponse pr) {
      agg.add(new PingData(pr.getLabel(), pr.getTime()));
    }

    public List<PingData> getPingData() {
      return agg;
    }
    
    public PingData[] toArray() {
      PingData[] list = new PingData[agg.size()];
      return agg.toArray(list);
    }
  }
}
