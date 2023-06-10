package app.slyworks.firebase_commons_lib

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class MChildEventListener
    (private var onChildAddedFunc:((snapshot: DataSnapshot) -> Unit)? = null,
     private var onChildChangedFunc:((snapshot: DataSnapshot) ->Unit)? = null,
     private var onChildMovedFunc:((snapshot: DataSnapshot) ->Unit)? = null,
     private var onChildRemovedFunc:((snapshot: DataSnapshot) -> Unit)? = null,
     private var onCancelledFunc: ((error: DatabaseError) -> Unit)? = null): ChildEventListener {

    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        onChildAddedFunc?.invoke(snapshot)
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        onChildChangedFunc?.invoke(snapshot)
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        onChildRemovedFunc?.invoke(snapshot)
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        onChildMovedFunc?.invoke(snapshot)
    }

    override fun onCancelled(error: DatabaseError) {
        onCancelledFunc?.invoke(error)
    }
}