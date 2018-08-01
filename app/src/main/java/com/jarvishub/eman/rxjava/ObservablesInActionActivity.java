package com.jarvishub.eman.rxjava;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

public class ObservablesInActionActivity extends AppCompatActivity {

    private String TAG = "ObservablesInActionActivity";
    private Disposable disposable;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observables_in_action);

        //simpleObserver();
        //singleAndSingleObserver();
        //completableAndCompletableObserver();
        flowableAndObserver();

    }

    private void flowableAndObserver() {

        /**
         * Simple example of Flowable just to show the syntax
         * the use of Flowable is best explained when used with BackPressure
         * Read the below link to know the best use cases to use Flowable operator
         * https://github.com/ReactiveX/RxJava/wiki/What%27s-different-in-2.0#when-to-use-flowable
         * -
         * Flowable : SingleObserver
         *
         * Flowable observable should be used when an Observable is generating huge amount of events/data than the Observer can handle. As per doc, Flowable can be used when the source is generating 10k+ events and subscriber can’t consume it all.
         */


        Flowable<Integer> flowableObservable = getFlowableObservable();

        SingleObserver<Integer> observer = getFlowableObserver();

        flowableObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .reduce(0, new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer result, Integer number) {
                        //Log.e(TAG, "Result: " + result + ", new number: " + number);
                        return result + number;
                    }
                })
                .subscribe(observer);
    }


    private void completableAndCompletableObserver() {
        /**
         * Completable won't emit any item, instead it returns
         * Success or failure state
         * Consider an example of making a PUT request to server to update
         * something where you are not expecting any response but the
         * success status
         * -
         * Completable : CompletableObserver
         */

        Log.i(TAG, "completableAndCompletableObserver: ");

        Note note = new Note(1, "Home work!");

        Completable completableObservable = updateNote(note);
        CompletableObserver completableObserver = completableObserver();
        completableObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(completableObserver);

    }
    private void singleAndSingleObserver() {

        /**
         * Single Observable emitting single Note
         * Single Observable is more useful in making network calls
         * where you expect a single response object to be emitted
         * -
         * Single : SingleObserver
         */
        Log.i(TAG, "singleAndSingleObserver: ");

        Single<Note> noteObservable = getNoteObservable();
        SingleObserver<Note> singleObserver = getSingleObserver();
        noteObservable
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(singleObserver);


        singleAndSingleObserver();
        completableAndCompletableObserver();

    }
    @SuppressLint("CheckResult")
    private void simpleObserver() {


        /**
         * Simple Observable emitting multiple Notes
         * -
         * Observable : Observer
         */

        Log.i(TAG, "simpleObserver: ");
        Observable<Note> notesObservable = getNotesObservable();
        Observer<Note> notesObserver = getNotesObserver();
        notesObservable.observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribeWith(notesObserver);
    }


    private SingleObserver<Integer> getFlowableObserver() {
        return new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
                disposable = d;
            }

            @Override
            public void onSuccess(Integer integer) {
                Log.d(TAG, "onSuccess: " + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }
        };
    }

    private Flowable<Integer> getFlowableObservable() {
        return Flowable.range(1, 100);
    }


    /**
     * Assume this making PUT request to server to update the Note
     */
    private Completable updateNote(Note note) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    Thread.sleep(1000);
                    emitter.onComplete();
                }
            }
        });
    }

    private CompletableObserver completableObserver() {
        return new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
                disposable = d;
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: Note updated successfully!");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }
        };
    }


    /////Single Observable
    private SingleObserver<Note> getSingleObserver() {
        return new SingleObserver<Note>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
                disposable = d;
            }

            @Override
            public void onSuccess(Note note) {
                Log.e(TAG, "onSuccess: " + note.getNote());
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }
        };
    }

    private Single<Note> getNoteObservable() {
        return Single.create(new SingleOnSubscribe<Note>() {
            @Override
            public void subscribe(SingleEmitter<Note> emitter) throws Exception {
                Note note = new Note(1, "Buy milk!");
                emitter.onSuccess(note);
            }
        });
    }




    /////Simple Observable

    private Observer<Note> getNotesObserver() {
        return new Observer<Note>() {

            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
                disposable = d;
            }

            @Override
            public void onNext(Note note) {
                Log.d(TAG, "onNext: " + note.getNote());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
    }
    private Observable<Note> getNotesObservable() {
        final List<Note> notes = prepareNotes();

        return Observable.create(new ObservableOnSubscribe<Note>() {
            @Override
            public void subscribe(ObservableEmitter<Note> emitter) throws Exception {
                for (Note note : notes) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(note);
                    }
                }

                // all notes are emitted
                if (!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }
        });
    }

    private List<Note> prepareNotes() {
        List<Note> notes = new ArrayList<>();
        notes.add(new Note(1, "Buy tooth paste!"));
        notes.add(new Note(2, "Call brother!"));
        notes.add(new Note(3, "Watch Narcos tonight!"));
        notes.add(new Note(4, "Pay power bill!"));
        return notes;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    class Note {
        int id;
        String note;

        public Note(int id, String note) {
            this.id = id;
            this.note = note;
        }

        public int getId() {
            return id;
        }

        public String getNote() {
            return note;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }

}
