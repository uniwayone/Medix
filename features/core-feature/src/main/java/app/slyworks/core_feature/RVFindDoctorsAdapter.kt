package app.slyworks.core_feature

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.slyworks.constants_lib.EVENT_OPEN_VIEW_PROFILE_FRAGMENT
import app.slyworks.controller_lib.AppController
import app.slyworks.data_lib.models.FBUserDetailsVModel
import app.slyworks.utils_lib.utils.displayImage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


/**
 *Created by Joshua Sylvanus, 4:54 AM, 1/8/2022.
 */
class RVFindDoctorsAdapter : RecyclerView.Adapter<RVFindDoctorsAdapter.ViewHolder>() {
    //region Vars
    private val list:MutableList<FBUserDetailsVModel> = mutableListOf()
    //endregion

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.li_find_doctors, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doctor = list.get(position)
        holder.bind(doctor)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setDataList(list:MutableList<FBUserDetailsVModel>){
        val startIndex = if(this.list.isEmpty()) 0 else this.list.size - 1
        this.list.addAll(list)

        notifyItemRangeInserted(startIndex, list.size)
    }

    fun addDoctor(doctor: FBUserDetailsVModel){
        val index = list.size - 1
        list.add(doctor)

        notifyItemInserted(index)
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        //region Vars
        private val TAG: String? = RVFindDoctorsAdapter::class.simpleName

        private val ivProfile:CircleImageView =itemView.findViewById(R.id.ivProfile_find_doctor)
        private val tvDoctorName: TextView = itemView.findViewById(R.id.tvName_find_doctor)
        private val tvSpecialisation: TextView = itemView.findViewById(R.id.tvSpecialization_find_doctor)
        //endregion
        fun bind(entity: FBUserDetailsVModel){
             ivProfile.displayImage(entity.imageUri)

             val firstName = entity.firstName
             val secondName = entity.lastName
             val _firstName = firstName.substring(0,1)
                 .uppercase(Locale.getDefault())
                                     .plus(firstName.substring(1,firstName.length))
            val _secondName = secondName.substring(0, 1)
                .uppercase(Locale.getDefault())
                                        .plus(secondName.substring(1, secondName.length))
            val fullName:String = "$_firstName $_secondName"
             tvDoctorName.text = "Dr. $fullName"

            val sb:StringBuilder = StringBuilder()
            entity.specialization?.forEach{
                if(it != entity.specialization!!.last()){
                    sb.append("${it},")
                }

                sb.append(it)
            }
            tvSpecialisation.text = tvSpecialisation.context.getString(app.slyworks.base_feature.R.string.specialization_holder, sb.toString())

            itemView.setOnClickListener {
                AppController.notifyObservers(EVENT_OPEN_VIEW_PROFILE_FRAGMENT, entity)
            }

        }
    }
}