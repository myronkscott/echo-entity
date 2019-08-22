/*
 * All content copyright (c) 2014 Terracotta, Inc., except as may otherwise
 * be noted in a separate copyright notice. All rights reserved.
 */
package org.terracotta.entity.echo;

import com.tc.classloader.BuiltinService;
import java.util.Arrays;
import java.util.Collection;
import org.terracotta.entity.PlatformConfiguration;
import org.terracotta.entity.ServiceConfiguration;
import org.terracotta.entity.ServiceProvider;
import org.terracotta.entity.ServiceProviderCleanupException;
import org.terracotta.entity.ServiceProviderConfiguration;

/**
 *
 */
@BuiltinService
public class EchoServiceProvider implements ServiceProvider {

  @Override
  public boolean initialize(ServiceProviderConfiguration configuration, PlatformConfiguration platformConfiguration) {
    return true;
  }

  @Override
  public <T> T getService(long consumerID, ServiceConfiguration<T> configuration) {
    return configuration.getServiceType().cast(new EchoImpl());
  }

  @Override
  public Collection<Class<?>> getProvidedServiceTypes() {
    return Arrays.asList(EchoService.class);
  }

  @Override
  public void prepareForSynchronization() throws ServiceProviderCleanupException {

  }
}
