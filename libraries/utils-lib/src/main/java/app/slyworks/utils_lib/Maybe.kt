package app.slyworks.utils_lib

import java.util.*
import kotlin.NoSuchElementException
import kotlin.jvm.Throws


/**
 * Created by Joshua Sylvanus, 7:17 PM, 16/10/2022.
 */
typealias Action<T> = (t:T) -> Unit
class Maybe <T> {
    //region Vars
    private var value:T? = null
    //endregion

    private constructor(){
        this.value = null
    }
    private constructor(value:T){
        this.value = value
    }

    companion object{
        private val EMPTY: Maybe<*> = Maybe<Any>()

        fun <T> empty(): Maybe<T> = EMPTY as Maybe<T>

        fun <T> of(value:T): Maybe<T> = Maybe<T>(value)

        fun <T> ofNullable(value:T?): Maybe<T> = if(value == null) empty() else of(value)
    }

    fun get():T{
      if(this.value == null)
          throw NoSuchElementException("no value present")

      return this.value!!
    }

    fun isPresent():Boolean  = this.value != null

    fun isEmpty():Boolean = this.value == null

    fun ifPresent(action:(t:T) -> Unit):Unit{
        if(this.value != null)
            action.invoke(this.value!!)
    }

    fun ifPresentOrElse(action:(t:T) -> Unit, emptyAction:() -> Unit){
       if(this.value != null)
           action.invoke(this.value!!)
       else
           emptyAction.invoke()
    }

    fun filter(predicate:(t:T) -> Boolean): Maybe<T> {
        return if(!this.isPresent())
            this
        else {
            if (predicate.invoke(this.value!!)) this else empty()
        }
    }

    fun <U> map(mapper:(t:T) -> U): Maybe<in U> {
        return if(!this.isPresent()) empty() else ofNullable(mapper.invoke(this.value!!))
    }

    fun <U> flatMap(mapper:(t:T) -> Maybe<in U>): Maybe<in U> {
        return if(!this.isPresent())
            empty()
        else
            mapper.invoke(this.value!!)
    }

    fun or(supplier:() -> Maybe<in T>): Maybe<in T> {
        if(this.isPresent())
            return this
        else
            return supplier.invoke()
    }

    fun orElseGet(supplier:() -> T):T{
        return if(this.isEmpty()) this.value!! else supplier.invoke()
    }

    @Throws(Exception::class)
    fun <X:Throwable> orElseThrow(supplier:() -> X):T{
        if(this.value != null)
            return this.value!!

        throw supplier.invoke()
    }

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            (other is Maybe<*>).not() -> false
            else -> Objects.equals(this.value, (other as Maybe<*>).value)
        }
    }

    override fun hashCode():Int = Objects.hashCode(this.value)

    override fun toString():String{
        return if (value != null) String.format("Maybe[%s]", value) else "Maybe.empty"
    }
}