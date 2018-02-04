package com.sample;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.LiteCycle;
import com.chaining.Lazy;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by Ahmed Adel Ismail on 2/4/2018.
 */

public class ReactiveActivity extends AppCompatActivity {

    private final Observable<String> ticker =
            Observable.interval(2, TimeUnit.SECONDS)
                    .map(String::valueOf);

    private final Lazy<TextView> textView =
            Lazy.defer(() -> (TextView) findViewById(R.id.text_view));

    private final BehaviorSubject<Disposable> tickerDisposable =
            LiteCycle.with((Disposable) null)
                    .forLifeCycle(this)
                    .onCreateUpdate(disposable -> ticker.subscribe(textView.call()::setText))
                    .onDestroyInvoke(this::dispose)
                    .observe();

    private final BehaviorSubject<Integer> contentDisplayer =
            LiteCycle.with(R.layout.activity_main)
                    .forLifeCycle(this)
                    .onCreateInvoke(this::setContentView)
                    .observe();

    private final BehaviorSubject<LocationRetriever> locationRetriever =
            LiteCycle.defer(this::locationRetriever)
                    .forLifeCycle(this)
                    .onStartInvoke(LocationRetriever::start)
                    .onStopInvoke(LocationRetriever::stop)
                    .observe();

    private void dispose(Disposable disposable) {
        if (!disposable.isDisposed()) disposable.dispose();
    }

    @NonNull
    private LocationRetriever locationRetriever() {
        return new LocationRetriever() {

            @Override
            public void onLocationChanged(Location location) {
                textView.call().setText(String.valueOf(location.getLatitude()));
            }
        };
    }


}
