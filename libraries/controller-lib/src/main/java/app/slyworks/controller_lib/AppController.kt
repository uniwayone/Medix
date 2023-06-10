package app.slyworks.controller_lib

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

fun Subscription.clear() {
    AppController.observers[this.event]?.remove(this)
    AppController.eventMap[this.event]?.second?.remove(this)
}

fun Subscription.clearAndRemove() {
    if (AppController.observers[this.event] == null) return

    if (AppController.observers[this.event]!!.contains(this))
        AppController.observers[this.event]!!.remove(this)

    if (AppController.eventMap[this.event]?.second?.contains(this) == true)
        AppController.eventMap.remove(this.event)

    AppController.removeEvent(this.event)
}

class AppController {
    companion object {
        //region Vars
        private var events: MutableList<String> = mutableListOf();
        internal var observers: MutableMap<String, MutableSet<Subscription>> = mutableMapOf()
        internal var eventMap: MutableMap<String, Pair<DataHolder, MutableSet<Subscription>>> = mutableMapOf();

        private var topicList: MutableList<String> = mutableListOf()
        private var topicObservers: MutableMap<String, PublishSubject<Any>> = mutableMapOf()
        //endregion

        @JvmStatic
        private fun addEvent(event: String) {
            if (events.contains(event)) return

            events.add(event)
            observers[event] = mutableSetOf()
        }

        @JvmStatic
        internal fun removeEvent(event: String) {
            events.remove(event)
            observers.remove(event)
            eventMap.remove(event)
        }

        @JvmStatic
        fun subscribeTo(event: String, observer: Observer, notifyMethod: NotifyMethod = NotifyMethod.PUSH_IMMEDIATELY): Subscription {
            if (!events.contains(event))
                addEvent(event)

            val s = Subscription(event, observer, notifyMethod)
            observers[event]!!.add(s)
            return s
        }

        @JvmStatic
        fun unsubscribe(subscription: Subscription) {
            observers[subscription.event]!!.remove(subscription)
        }

        @JvmStatic
        fun <T> notifyObservers(event: String, data: T) {
            //Timber.e("notifyObservers: ${Thread.currentThread().name}" )

            val observers: MutableSet<Subscription>? = observers[event]
            if (observers.isNullOrEmpty()) return

            observers.forEach { subscriber: Subscription ->
                if (subscriber.notifyMethod == NotifyMethod.PUSH_IMMEDIATELY)
                    subscriber.observer.notify(event, data)

                /*to avoid caching unnecessarily, only cache if there is a "cold" Observer subscribed to event*/
                if (subscriber.notifyMethod == NotifyMethod.WAIT_FOR_PULL)
                    cacheData(event, subscriber, data as Any)
            }
        }

        @JvmStatic
        private fun cacheData(event: String, subscriber: Subscription, data: Any) {
            eventMap[event]?.let {
                it.first.data = data
                it.second.add(subscriber)
                return
            }

            eventMap[event] = Pair<DataHolder, MutableSet<Subscription>>(
                DataHolder(data), mutableSetOf(subscriber)
            )
        }

        @JvmStatic
        fun <T> pullData(event: String, observer: Observer): T {
            return eventMap[event]?.first?.data as T
        }

        @JvmStatic
        fun addTopic(topic: String) {
            if (topicList.contains(topic))
                return

            topicList.add(topic)
            topicObservers.put(topic, PublishSubject.create<Any>())
        }

        @JvmStatic
        fun <T> pushToTopic(topic: String, data: T) {
            topicObservers.get(topic)?.onNext(topic)
        }

        @JvmStatic
        fun <T> subscribeTo(topic: String): Observable<in T> {
            if (!topicList.contains(topic))
                addTopic(topic)

            return topicObservers.get(topic)!!.hide() as Observable<T>
        }
    }
}