package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import dagger.internal.Factory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class SubmitBookForPublishingActivity_Factory
    implements Factory<SubmitBookForPublishingActivity> {
  private final Provider<PublishingStatusDao> publishingStatusDaoProvider;

  private final Provider<CatalogDao> catalogDaoProvider;

  public SubmitBookForPublishingActivity_Factory(
      Provider<PublishingStatusDao> publishingStatusDaoProvider,
      Provider<CatalogDao> catalogDaoProvider) {
    this.publishingStatusDaoProvider = publishingStatusDaoProvider;
    this.catalogDaoProvider = catalogDaoProvider;
  }

  @Override
  public SubmitBookForPublishingActivity get() {
    return new SubmitBookForPublishingActivity(
        publishingStatusDaoProvider.get(), catalogDaoProvider.get());
  }

  public static SubmitBookForPublishingActivity_Factory create(
      Provider<PublishingStatusDao> publishingStatusDaoProvider,
      Provider<CatalogDao> catalogDaoProvider) {
    return new SubmitBookForPublishingActivity_Factory(
        publishingStatusDaoProvider, catalogDaoProvider);
  }
}
