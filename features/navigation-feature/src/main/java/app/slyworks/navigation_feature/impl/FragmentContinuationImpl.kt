package app.slyworks.navigation_feature.impl

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import app.slyworks.navigation_feature.interfaces.FragmentContinuation


/**
 *Created by Joshua Sylvanus, 6:05 AM, 25/08/2022.
 */
data class FragmentContinuationImpl(private var _fragmentManager: FragmentManager?)
    : FragmentContinuation {
    private var fragmentManager: FragmentManager = _fragmentManager!!

    private var _transaction: FragmentTransaction? = null
    private var transaction: FragmentTransaction
    private var containerID: Int = 0

    init {
        _transaction = fragmentManager.beginTransaction()

        transaction = _transaction!!
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    }

    override fun into(@IdRes containerID:Int): FragmentContinuation {
        if(this.containerID != 0)
            throw UnsupportedOperationException("ContainerID has already been set")

        this.containerID = containerID
        return this
    }


    fun replace(f: Fragment): FragmentContinuation {
        transaction.addToBackStack("${f::class.simpleName}")
        transaction.replace(containerID, f)
        return this
    }

    override fun hideCurrent(): FragmentContinuation {
        if(fragmentManager.backStackEntryCount > 0)
            transaction.hide(fragmentManager.findFragmentById(containerID)!!)

        return this
    }

    override fun show(f: Fragment, currentTag:String?): FragmentContinuation {
        if(fragmentManager.findFragmentByTag(f::class.simpleName) != null){
            /*its been added before*/
            transaction.hide(fragmentManager.findFragmentByTag(currentTag)!!)
            transaction.show(fragmentManager.findFragmentByTag(f::class.simpleName)!!)
        }else{
            if(currentTag != null)
            /*hide currently visible Fragment*/
                transaction.hide(fragmentManager.findFragmentByTag(currentTag)!!)

            transaction.addToBackStack("${f::class.simpleName}")
            transaction.add(containerID, f, "${f::class.simpleName}")
        }

        return this
    }

    override fun show(f: Fragment): FragmentContinuation {
        if(fragmentManager.findFragmentByTag(f::class.simpleName) != null){
            /*its been added before*/
            transaction.hide(fragmentManager.findFragmentById(containerID)!!)
            transaction.show(fragmentManager.findFragmentByTag(f::class.simpleName)!!)
        }else{
            if(fragmentManager.findFragmentById(containerID) != null)
            /*hide currently visible Fragment*/
                transaction.hide(fragmentManager.findFragmentById(containerID)!!)

            transaction.addToBackStack("${f::class.simpleName}")
            transaction.add(containerID, f, "${f::class.simpleName}")
        }

        return this
    }

    override fun after(block:() -> Unit): FragmentContinuation {
        block()
        return this
    }

    override fun navigate(){
        transaction.commit()

        _transaction = null
        _fragmentManager = null
    }
}