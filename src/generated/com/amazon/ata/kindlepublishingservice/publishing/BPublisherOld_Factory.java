package com.amazon.ata.kindlepublishingservice.publishing;

import dagger.internal.Factory;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class BPublisherOld_Factory implements Factory<BPublisherOld> {
  private final Provider<ScheduledExecutorService> scheduledExecutorServiceProvider;

  private final Provider<Runnable> publishTaskProvider;

  public BPublisherOld_Factory(
      Provider<ScheduledExecutorService> scheduledExecutorServiceProvider,
      Provider<Runnable> publishTaskProvider) {
    this.scheduledExecutorServiceProvider = scheduledExecutorServiceProvider;
    this.publishTaskProvider = publishTaskProvider;
  }

  @Override
  public BPublisherOld get() {
    return new BPublisherOld(scheduledExecutorServiceProvider.get(), publishTaskProvider.get());
  }

  public static BPublisherOld_Factory create(
      Provider<ScheduledExecutorService> scheduledExecutorServiceProvider,
      Provider<Runnable> publishTaskProvider) {
    return new BPublisherOld_Factory(scheduledExecutorServiceProvider, publishTaskProvider);
  }
}
