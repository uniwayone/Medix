package app.slyworks.core_feature

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import app.slyworks.utils_lib.utils.displayImage

data class HealthAreas(val icon:Int, val text:String)
class RvHealthAreasAdapter : RecyclerView.Adapter<RvHealthAreasAdapter.ViewHolder>(){
    //region Vars
    private val list:List<HealthAreas> = listOf(
        HealthAreas(app.slyworks.base_feature.R.drawable.ic_covid, "Covid-19"),
        HealthAreas(app.slyworks.base_feature.R.drawable.ic_pregnancy, "Obstetrics"),
        HealthAreas(app.slyworks.base_feature.R.drawable.ic_diagnose, "Physiotherapy"),
        HealthAreas(app.slyworks.base_feature.R.drawable.ic_pediatrics, "Pediatrics"),
        HealthAreas(app.slyworks.base_feature.R.drawable.ic_family, "Family Planning"),
        HealthAreas(app.slyworks.base_feature.R.drawable.ic_patient, "Recuperation"),
    )
    //endregion


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.li_health_areas, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(list[position])
    }

    override fun getItemCount(): Int {
       return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        //region Vars
        private val rootView:CardView = itemView.findViewById(R.id.rootView)
        private val ivIcon:ImageView = itemView.findViewById(R.id.ivIcon_li_health_areas)
        private val tvText: TextView = itemView.findViewById(R.id.tvText_li_health_areas)
        //private val bgColor:Int
        //private val tvTextColor:Int
        //endregion

        init {
           /* val p = ViewUtils.getColorPair()
            bgColor = p.first
            tvTextColor = p.second*/
        }

        fun bind(entity: HealthAreas){
            //rootView.setCardBackgroundColor(ContextCompat.getColor(App.getContext(), bgColor))
            //tvText.setTextColor(ContextCompat.getColor(App.getContext(), tvTextColor))
            ivIcon.displayImage(entity.icon)
            tvText.text = entity.text
        }
    }

}
