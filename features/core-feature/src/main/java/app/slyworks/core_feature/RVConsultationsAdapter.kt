package app.slyworks.core_feature

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.slyworks.utils_lib.utils.displayImage
import de.hdodenhof.circleimageview.CircleImageView


/**
 * Created by Joshua Sylvanus, 1:47 PM, 1/12/2022.
 */
enum class ConsultationType{ PATIENT, DOCTOR }
data class Consultation(var personImageUri:String, var personName:String, var time:String, var type: ConsultationType)
class RVConsultationsAdapter : RecyclerView.Adapter<RVConsultationsAdapter.ViewHolder>() {
    //region Vars
    private var list:MutableList<Consultation> = mutableListOf()

    //endregion
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.li_consultation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entity: Consultation = list.get(position) ?: return
        holder.bind(entity)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        //region Vars
        private val ivProfile:CircleImageView = itemView.findViewById(R.id.ivProfile_li_consultation)
        private val tvPersonName: TextView = itemView.findViewById(R.id.tvPersonTitle_li_consultation)
        private val tvConsultationTime:TextView =itemView.findViewById(R.id.tvTime_li_consultation)
        private val ivSetReminder:ImageView = itemView.findViewById(R.id.ivAlarm_li_consultation)
        private val ivVideoCall:ImageView = itemView.findViewById(R.id.ivVideoCall_li_consultation)
        private val ivVoiceCall:ImageView = itemView.findViewById(R.id.ivVoiceCall_li_consultation)
        //endregion

        fun bind(entity: Consultation){
            ivProfile.displayImage(entity.personImageUri)
            tvConsultationTime.text = entity.time

            if(entity.type == ConsultationType.DOCTOR)
                tvPersonName.text = tvPersonName.context.getString(app.slyworks.base_feature.R.string.consultation_doctor, entity.personName)
            else tvPersonName.text = entity.personName
        }
    }
}