package com.amazon.ata.kindlepublishingservice.publishing;

import dagger.internal.Factory;
import javax.annotation.processing.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class NoOpTask_Factory implements Factory<NoOpTask> {
  private static final NoOpTask_Factory INSTANCE = new NoOpTask_Factory();

  @Override
  public NoOpTask get() {
    return new NoOpTask();
  }

  public static NoOpTask_Factory create() {
    return INSTANCE;
  }
}
