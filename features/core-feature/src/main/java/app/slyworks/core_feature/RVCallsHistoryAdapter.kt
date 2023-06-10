package app.slyworks.core_feature

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.slyworks.constants_lib.*
import app.slyworks.data_lib.models.CallHistoryVModel
import app.slyworks.utils_lib.TimeHelper
import app.slyworks.utils_lib.utils.displayImage
import de.hdodenhof.circleimageview.CircleImageView

class RVCallsHistoryAdapter(private val timeHelper: TimeHelper,
                            diffUtil: DiffUtil.ItemCallback<CallHistoryVModel> = CallsHistoryDiffUtilCallback()
): ListAdapter<CallHistoryVModel, RVCallsHistoryAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.li_call_history, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList.get(position))
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        //region Vars
        private val ivProfile:CircleImageView = itemView.findViewById(R.id.ivProfile_li_calls_history)
        private val tvName: TextView = itemView.findViewById(R.id.tvName_li_calls_history)
        private val ivCallType: ImageView = itemView.findViewById(R.id.ivCallType_li_calls_history)
        private val ivCallStatus:ImageView = itemView.findViewById(R.id.ivStatus_li_calls_history)
        private val tvDetails:TextView = itemView.findViewById(R.id.tvCallDetails_li_calls_history)
        //endregion
        fun bind(entity: CallHistoryVModel){
            ivProfile.displayImage(entity.senderImageUri)
            tvName.text = entity.callerName

            val callType:Int =
                when(entity.type){
                    VOICE_CALL -> app.slyworks.base_feature.R.drawable.ic_voice_call
                    VIDEO_CALL -> app.slyworks.base_feature.R.drawable.ic_videocall
                    else -> throw IllegalArgumentException()
               }
            ivCallType.displayImage(callType)

            val callStatus:Int =
                when(entity.status){
                    INCOMING_CALL -> app.slyworks.base_feature.R.drawable.ic_incoming_call
                    OUTGOING_CALL -> app.slyworks.base_feature.R.drawable.ic_outgoing_call
                    MISSED_CALL -> app.slyworks.base_feature.R.drawable.ic_missed_call
                    else -> throw IllegalArgumentException()
                }
            ivCallStatus.displayImage(callStatus)

            val duration:String = timeHelper.convertTimeToDuration(entity.duration)
            val timeStamp:String = timeHelper.convertTimeToString(entity.timeStamp)
            tvDetails.text = tvDetails.context.getString(app.slyworks.base_feature.R.string.li_calls_history, duration, timeStamp)
        }
    }
}
