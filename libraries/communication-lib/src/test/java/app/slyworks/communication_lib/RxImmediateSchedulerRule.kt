package app.slyworks.communication_lib

import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement


/**
 *Created by Joshua Sylvanus, 7:45 PM, 02/11/2022.
 */
class RxImmediateSchedulerRule : TestRule {
    private val immediateScheduler: Scheduler = Schedulers.trampoline()

    override fun apply(base: Statement?, description: Description?): Statement {
      return object : Statement(){
          override fun evaluate() {
              RxJavaPlugins.setInitIoSchedulerHandler { immediateScheduler }
              RxJavaPlugins.setIoSchedulerHandler { immediateScheduler }

              RxJavaPlugins.setInitComputationSchedulerHandler { immediateScheduler }
              RxJavaPlugins.setComputationSchedulerHandler { immediateScheduler }

              RxJavaPlugins.setInitNewThreadSchedulerHandler { immediateScheduler }
              RxJavaPlugins.setNewThreadSchedulerHandler { immediateScheduler }

              RxJavaPlugins.setInitNewThreadSchedulerHandler { immediateScheduler }
              RxJavaPlugins.setNewThreadSchedulerHandler { immediateScheduler }

              RxJavaPlugins.setInitSingleSchedulerHandler { immediateScheduler }
              RxJavaPlugins.setSingleSchedulerHandler { immediateScheduler }

              RxAndroidPlugins.setInitMainThreadSchedulerHandler{ immediateScheduler }
              RxAndroidPlugins.setMainThreadSchedulerHandler { immediateScheduler }


              try{
                  base?.evaluate()
              }finally{
                  RxJavaPlugins.reset()
                  RxAndroidPlugins.reset()
              }
          }
      }
    }
}