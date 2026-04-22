package com.amitayk.countdownwidget;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@QualifierMetadata
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
public final class CountdownApp_MembersInjector implements MembersInjector<CountdownApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public CountdownApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<CountdownApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new CountdownApp_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(CountdownApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.amitayk.countdownwidget.CountdownApp.workerFactory")
  public static void injectWorkerFactory(CountdownApp instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
