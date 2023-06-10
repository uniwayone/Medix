package app.slyworks.firebase_commons_lib

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

/**
 *Created by Joshua Sylvanus, 10:29 PM, 26/04/2022.
 */
/*TODO: 11:17PM 26/04/2022
*  1:cancelling subscription on network change should cancel any running firebase query
*  2:updating message person in Firebase should be happening in background on message send or received*/

class MValueEventListener(private var onDataChangeFunc:(data: DataSnapshot) -> Unit,
                          private var onCancelledFunc:((error: DatabaseError) ->Unit)? = null ) : ValueEventListener {
    override fun onCancelled(error: DatabaseError) { onCancelledFunc?.invoke(error) }
    override fun onDataChange(snapshot: DataSnapshot) { onDataChangeFunc.invoke(snapshot) }
}