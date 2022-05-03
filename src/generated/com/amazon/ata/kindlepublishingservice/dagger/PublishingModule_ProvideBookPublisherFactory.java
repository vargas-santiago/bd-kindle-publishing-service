package com.amazon.ata.kindlepublishingservice.dagger;

import com.amazon.ata.kindlepublishingservice.publishing.BookPublisher;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class PublishingModule_ProvideBookPublisherFactory implements Factory<BookPublisher> {
  private final PublishingModule module;

  private final Provider<ScheduledExecutorService> scheduledExecutorServiceProvider;

  public PublishingModule_ProvideBookPublisherFactory(
      PublishingModule module,
      Provider<ScheduledExecutorService> scheduledExecutorServiceProvider) {
    this.module = module;
    this.scheduledExecutorServiceProvider = scheduledExecutorServiceProvider;
  }

  @Override
  public BookPublisher get() {
    return Preconditions.checkNotNull(
        module.provideBookPublisher(scheduledExecutorServiceProvider.get()),
        "Cannot return null from a non-@Nullable @Provides method");
  }

  public static PublishingModule_ProvideBookPublisherFactory create(
      PublishingModule module,
      Provider<ScheduledExecutorService> scheduledExecutorServiceProvider) {
    return new PublishingModule_ProvideBookPublisherFactory(
        module, scheduledExecutorServiceProvider);
  }

  public static BookPublisher proxyProvideBookPublisher(
      PublishingModule instance, ScheduledExecutorService scheduledExecutorService) {
    return Preconditions.checkNotNull(
        instance.provideBookPublisher(scheduledExecutorService),
        "Cannot return null from a non-@Nullable @Provides method");
  }
}
