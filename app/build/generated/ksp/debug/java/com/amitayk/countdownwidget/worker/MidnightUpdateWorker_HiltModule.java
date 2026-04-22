package com.amitayk.countdownwidget.worker;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = MidnightUpdateWorker.class
)
public interface MidnightUpdateWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.amitayk.countdownwidget.worker.MidnightUpdateWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(
      MidnightUpdateWorker_AssistedFactory factory);
}
