package app.slyworks.navigation_feature.impl

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import app.slyworks.navigation_feature.ContainerAlreadySetException
import app.slyworks.navigation_feature.interfaces.FragmentContinuation
import app.slyworks.navigation_feature.interfaces.FragmentContinuationStateful


/**
 * Created by Joshua Sylvanus, 6:04 AM, 25/08/2022.
 */

data class FragmentContinuationStatefulImpl(private var _fragmentManager: FragmentManager?)
    : FragmentContinuationStateful {
    //region Vars
    private var fragmentManager: FragmentManager = _fragmentManager!!

    private var _transaction: FragmentTransaction? = null
    private lateinit var transaction: FragmentTransaction
    private var containerID: Int = 0

    private var afterFunc:(() -> Unit)? = null

    private var currentFragmentTag:String? = null
    private var fragmentTagList:MutableList<String> = mutableListOf()
    //endregion

    init { init() }

    private fun init(){
        _transaction = fragmentManager.beginTransaction()

        transaction = _transaction!!
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    }

    override fun into(@IdRes containerID:Int): FragmentContinuation {
        if(this.containerID != 0)
            throw ContainerAlreadySetException()

        this.containerID = containerID
        return this
    }

    fun replace(f: Fragment): FragmentContinuation {
        transaction.addToBackStack("${f::class.simpleName}")
        transaction.replace(containerID, f)
        currentFragmentTag = f::class.simpleName
        return this
    }

    override fun hideCurrent(): FragmentContinuation {
        if(currentFragmentTag != null) {
            val f: Fragment = fragmentManager.findFragmentByTag(currentFragmentTag!!)!!
            transaction.hide(f)
            fragmentTagList.removeLast()
            //currentFragmentTag = f::class.simpleName
            fragmentTagList.add(currentFragmentTag!!)
        }

        return this
    }

    override fun show(f: Fragment, currentTag: String?): FragmentContinuation {
        /*no op, no need since the whole purpose of this implementation is to remove the need for this method*/
        show(f)
        return this
    }

    override fun show(f: Fragment): FragmentContinuation {
        if(fragmentManager.findFragmentByTag(f::class.simpleName) != null){
            /*its been added before*/
            transaction.hide(fragmentManager.findFragmentByTag(currentFragmentTag!!)!!)
            transaction.show(fragmentManager.findFragmentByTag(f::class.simpleName)!!)
        }else{
            if(fragmentManager.findFragmentById(containerID) != null)
            /*hide currently visible Fragment*/
                transaction.hide(fragmentManager.findFragmentByTag(currentFragmentTag!!)!!)

            transaction.addToBackStack("${f::class.simpleName}")
            transaction.add(containerID, f, "${f::class.simpleName}")
        }

        currentFragmentTag = f::class.simpleName
        fragmentTagList = fragmentTagList.filter { it != f::class.simpleName } as MutableList<String>
        fragmentTagList.add(currentFragmentTag!!)

        return this
    }

    override fun after(block:() -> Unit): FragmentContinuation {
        afterFunc = block
        return this
    }

    override fun popBackStack(also:((String)->Unit)?):Boolean{
        var wasPopped = false
        if(fragmentManager.backStackEntryCount > 1){
            fragmentManager.popBackStack()
            fragmentTagList.removeLast()
            currentFragmentTag = fragmentTagList.last()

            also?.invoke(currentFragmentTag!!)

            wasPopped = true
        }

        return wasPopped
    }

    override fun onDestroy(block: (() -> Unit)?) {
        block?.invoke()
        _fragmentManager = null
    }

    override fun navigate(){
        transaction.commit()
        afterFunc?.invoke()

        init()
    }
}

