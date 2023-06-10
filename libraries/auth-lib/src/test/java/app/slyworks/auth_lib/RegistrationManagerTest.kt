package app.slyworks.auth_lib

import com.slyworks.models.models.Outcome
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Assert.*
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import java.util.concurrent.TimeUnit


/**
 * Created by Joshua Sylvanus, 8:06 PM, 08/06/2022.
 */
/*fixme:this tests *were not* implemented properly, don't take as definitive result */
class RegistrationManagerTest{

    /*testing that FlatMap is what i need to run tasks sequentially*/
    @Test
    fun isFlatMap_rightForUseCase(){
        val o1 = Observable.just(Outcome.SUCCESS("one")).delay(100, TimeUnit.MILLISECONDS)
        val o2 = Observable.just(Outcome.SUCCESS("two")).delay(300, TimeUnit.MILLISECONDS)
        val o3 = Observable.just(Outcome.FAILURE(value = "three")).delay(500, TimeUnit.MILLISECONDS)
        val o4 = Observable.just(Outcome.SUCCESS("four")).delay(400, TimeUnit.MILLISECONDS)

        /*final emission should be "three"*/
        val o = o1.flatMap {
            when {
                it.isSuccess -> o2
                else -> Observable.just(it)
            }
        }.flatMap {
            when {
                it.isSuccess -> o3
                else -> Observable.just(it)
            }
        }.flatMap {
            when {
                it.isSuccess -> o4
                else -> Observable.just(it)
            }
        }
            .blockingFirst()



        assertThat(o.getValue() as String, IsEqual("three"))
    }

    @Test
    fun isConcatMap_rightForUseCase(){
        val o1 = Observable.just(Outcome.SUCCESS("one")).delay(100, TimeUnit.MILLISECONDS)
        val o2 = Observable.just(Outcome.SUCCESS("two")).delay(300, TimeUnit.MILLISECONDS)
        val o3 = Observable.just(Outcome.FAILURE(value = "three")).delay(500, TimeUnit.MILLISECONDS)
        val o4 = Observable.just(Outcome.SUCCESS("four")).delay(400, TimeUnit.MILLISECONDS)

        val r =  o1.concatMap {
            when {
                it.isSuccess -> o2
                else -> Observable.just(it)
            }
        }.concatMap {
            when {
                it.isSuccess -> o3
                else -> Observable.just(it)
            }
        }.concatMap {
            when {
                it.isSuccess -> o4
                else -> Observable.just(it)
            }
        }
            .blockingFirst()

        assertThat(r.getValue() as String, IsEqual("three"))
    }
}