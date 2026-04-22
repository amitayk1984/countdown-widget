package com.amitayk.countdownwidget.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class MidnightUpdateWorker_AssistedFactory_Impl implements MidnightUpdateWorker_AssistedFactory {
  private final MidnightUpdateWorker_Factory delegateFactory;

  MidnightUpdateWorker_AssistedFactory_Impl(MidnightUpdateWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public MidnightUpdateWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<MidnightUpdateWorker_AssistedFactory> create(
      MidnightUpdateWorker_Factory delegateFactory) {
    return InstanceFactory.create(new MidnightUpdateWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<MidnightUpdateWorker_AssistedFactory> createFactoryProvider(
      MidnightUpdateWorker_Factory delegateFactory) {
    return InstanceFactory.create(new MidnightUpdateWorker_AssistedFactory_Impl(delegateFactory));
  }
}
