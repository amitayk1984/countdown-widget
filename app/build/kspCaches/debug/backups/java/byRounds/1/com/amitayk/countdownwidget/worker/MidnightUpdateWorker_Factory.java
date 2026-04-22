package com.amitayk.countdownwidget.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class MidnightUpdateWorker_Factory {
  public MidnightUpdateWorker_Factory() {
  }

  public MidnightUpdateWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params);
  }

  public static MidnightUpdateWorker_Factory create() {
    return new MidnightUpdateWorker_Factory();
  }

  public static MidnightUpdateWorker newInstance(Context context, WorkerParameters params) {
    return new MidnightUpdateWorker(context, params);
  }
}
