package app.slyworks.controller_lib

/*TODO:create something similar to CompositeDisposable*/
class CompositeSubscription(){
   private val l:MutableList<Subscription> = mutableListOf()

   fun add(s: Subscription):Unit { l.add(s) }
   fun dispose():Unit = l.clear()
   fun clear():Unit = l.clear()
}