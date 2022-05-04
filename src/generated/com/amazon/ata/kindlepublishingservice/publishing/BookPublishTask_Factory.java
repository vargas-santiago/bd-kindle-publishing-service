package com.amazon.ata.kindlepublishingservice.publishing;

import dagger.internal.Factory;
import javax.annotation.processing.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class BookPublishTask_Factory implements Factory<BookPublishTask> {
  private static final BookPublishTask_Factory INSTANCE = new BookPublishTask_Factory();

  @Override
  public BookPublishTask get() {
    return new BookPublishTask();
  }

  public static BookPublishTask_Factory create() {
    return INSTANCE;
  }
}
