package app.slyworks.core_feature

import androidx.recyclerview.widget.DiffUtil
import app.slyworks.data_lib.models.CallHistoryVModel
import app.slyworks.data_lib.models.PersonVModel

/**
 *Created by Joshua Sylvanus, 5:47 AM, 28/04/2022.
 */
class PersonDiffUtilCallback : DiffUtil.ItemCallback<PersonVModel>(){
    override fun areItemsTheSame(oldItem: PersonVModel, newItem: PersonVModel): Boolean {
        return oldItem.firebaseUID == newItem.firebaseUID
    }

    override fun areContentsTheSame(oldItem: PersonVModel, newItem: PersonVModel): Boolean {
        return oldItem == newItem
    }
}

class CallsHistoryDiffUtilCallback : DiffUtil.ItemCallback<CallHistoryVModel>(){
    override fun areItemsTheSame(oldItem: CallHistoryVModel, newItem: CallHistoryVModel): Boolean {
        return oldItem.callerUID == newItem.callerUID
    }

    override fun areContentsTheSame(oldItem: CallHistoryVModel, newItem: CallHistoryVModel): Boolean {
        return oldItem.status == newItem.status
    }
}