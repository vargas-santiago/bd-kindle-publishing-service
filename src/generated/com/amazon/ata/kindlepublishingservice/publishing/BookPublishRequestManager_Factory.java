package com.amazon.ata.kindlepublishingservice.publishing;

import dagger.internal.Factory;
import javax.annotation.processing.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class BookPublishRequestManager_Factory implements Factory<BookPublishRequestManager> {
  private static final BookPublishRequestManager_Factory INSTANCE =
      new BookPublishRequestManager_Factory();

  @Override
  public BookPublishRequestManager get() {
    return new BookPublishRequestManager();
  }

  public static BookPublishRequestManager_Factory create() {
    return INSTANCE;
  }
}
