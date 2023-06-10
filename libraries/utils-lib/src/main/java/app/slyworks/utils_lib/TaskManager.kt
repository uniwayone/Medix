package app.slyworks.utils_lib

import androidx.collection.SimpleArrayMap
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 *Created by Joshua Sylvanus, 2:16 AM, 1/3/2022.
 */
object TaskManager {
    //region Vars
    private val NUMBER_OF_CORES:Int = Runtime.getRuntime().availableProcessors()
    private var mExecutorsMap:SimpleArrayMap<Long,ExecutorService> = SimpleArrayMap()
    //endregion

   /* fun runOnLaunchCoroutine(func:() -> Unit):Unit{
        CoroutineScope(Dispatchers.IO).launch(::func)
    }

    fun <T> runOnAsyncCoroutine(func:() -> T):T{
        return CoroutineScope(Dispatchers.IO).async { }
    }*/

    fun newSingleThreadExecutor(executorID:Long): ExecutorService {
        val e =  Executors.newSingleThreadExecutor()
        mExecutorsMap!!.put(executorID,e)
        return e
    }

    fun newThreadPoolExecutor(executorID: Long): ExecutorService{
        val e = Executors.newFixedThreadPool(NUMBER_OF_CORES)
        mExecutorsMap!!.put(executorID, e)
        return e
    }

    fun shutdownExecutor(executorID: Long){
       mExecutorsMap?.get(executorID)?.shutdownNow()
    }

    fun runOnSingleThread(task:Runnable){
        val e = Executors.newSingleThreadExecutor()
        e.submit(task)
        e.shutdown()
    }

    fun <T, R, S> runOnSingleThread(
        param1: R?,
        param2: S?,
        task: (p1: R?, p2: S?) -> T
    ) /*where S:Parcelable, R: Parcelable*/ : T {
        val e = Executors.newSingleThreadExecutor()
        val data = e.submit(Callable<T>{ task(param1, param2) }).get()
        e.shutdownNow()
        return data
    }

    fun <T>runOnSingleThread(task:Callable<T>):T{
        val e = Executors.newSingleThreadExecutor()
        return execute<T>(e,task)
    }

    fun runOnThreadPool(task:Runnable){
        val e = Executors.newFixedThreadPool(NUMBER_OF_CORES)
        e.submit(task)
        e.shutdown()
    }

    fun <T>runOnThreadPool(task: Callable<T>):T{
        return Executors.newFixedThreadPool(NUMBER_OF_CORES).submit(task).get()
    }

    //fixme:not sure creating THREADS 2x the NUM_OF_CORES is a good idea. find a better way
    fun <T>runOnComputationThreadPool(task:Callable<T>):T{
        val e = Executors.newFixedThreadPool(NUMBER_OF_CORES * 2)
        return execute<T>(e,task)
    }

    private fun <T>execute(e:ExecutorService, task:Callable<T>):T{
        val data = e.submit(task).get()
        e.shutdownNow()
        return data
    }

}