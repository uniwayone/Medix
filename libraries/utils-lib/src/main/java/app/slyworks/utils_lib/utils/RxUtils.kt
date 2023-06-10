package app.slyworks.utils_lib.utils

import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.Subject


/**
 *Created by Joshua Sylvanus, 8:56 PM, 19/08/2022.
 */

inline fun <T> ObservableEmitter<T>.onNextAndComplete(value:T){
   onNext(value)
   onComplete()
}

inline fun <T> Subject<T>.onNextAndComplete(value:T){
   onNext(value)
   onComplete()
}

operator fun CompositeDisposable.plusAssign(d: Disposable){
    this.add(d)
}