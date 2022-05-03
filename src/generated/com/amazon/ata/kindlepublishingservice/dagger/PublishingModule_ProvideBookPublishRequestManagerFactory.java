package com.amazon.ata.kindlepublishingservice.dagger;

import com.amazon.ata.kindlepublishingservice.publishing.BookPublishRequestManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.processing.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class PublishingModule_ProvideBookPublishRequestManagerFactory
    implements Factory<BookPublishRequestManager> {
  private final PublishingModule module;

  public PublishingModule_ProvideBookPublishRequestManagerFactory(PublishingModule module) {
    this.module = module;
  }

  @Override
  public BookPublishRequestManager get() {
    return Preconditions.checkNotNull(
        module.provideBookPublishRequestManager(),
        "Cannot return null from a non-@Nullable @Provides method");
  }

  public static PublishingModule_ProvideBookPublishRequestManagerFactory create(
      PublishingModule module) {
    return new PublishingModule_ProvideBookPublishRequestManagerFactory(module);
  }

  public static BookPublishRequestManager proxyProvideBookPublishRequestManager(
      PublishingModule instance) {
    return Preconditions.checkNotNull(
        instance.provideBookPublishRequestManager(),
        "Cannot return null from a non-@Nullable @Provides method");
  }
}
