package app.slyworks.message_feature.message

import androidx.recyclerview.widget.DiffUtil
import app.slyworks.data_lib.models.MessageVModel

/**
 * Created by Joshua Sylvanus, 10:50 PM, 28/11/2022.
 */
class MessageDiffUtilCallback : DiffUtil.ItemCallback<MessageVModel>(){
    override fun areItemsTheSame(oldItem: MessageVModel, newItem: MessageVModel): Boolean {
        return oldItem.messageID == newItem.messageID
    }

    override fun areContentsTheSame(oldItem: MessageVModel, newItem: MessageVModel): Boolean {
        return oldItem.content == newItem.content
    }

}
