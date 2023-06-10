package app.slyworks.message_feature.custom_views

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.slyworks.message_feature.message.RVMessageAdapter
import app.slyworks.utils_lib.utils.px

/**
 *Created by Joshua Sylvanus, 7:27 PM, 05/05/2022.
 */
typealias MHolder = RVMessageAdapter.MViewHolder
class EdgeItemDecorator : RecyclerView.ItemDecoration() {
    //region Vars
    val path = Path()
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var isLastInGroup:Boolean = false
    //endregion


    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
         path.reset()

        for(i in 0 until parent.childCount){
            val view:View = parent.getChildAt(i)
            val currentViewHolder: RVMessageAdapter.MViewHolder? =
                parent.getChildViewHolder(view) as RVMessageAdapter.MViewHolder
            var previousViewHolder: RVMessageAdapter.MViewHolder? = null
            var nextViewHolder: RVMessageAdapter.MViewHolder? =  null

            currentViewHolder?.let {
                when(it){
                    is RVMessageAdapter.ToViewHolder -> paint.color = ContextCompat.getColor(parent.context, app.slyworks.base_feature.R.color.appBlue_li_message_to)
                    is RVMessageAdapter.FromViewHolder -> paint.color = ContextCompat.getColor(parent.context, app.slyworks.base_feature.R.color.appGrey_li_message_from)
                }
                previousViewHolder =
                parent.findViewHolderForAdapterPosition(currentViewHolder.bindingAdapterPosition?.minus(1)) as RVMessageAdapter.MViewHolder

                nextViewHolder =
                parent.findViewHolderForAdapterPosition(currentViewHolder.bindingAdapterPosition?.plus(1)) as RVMessageAdapter.MViewHolder
            }

            if(currentViewHolder is RVMessageAdapter.HeaderViewHolder){
                super.onDraw(c, parent, state)
                continue
            }
            else if(nextViewHolder == null || previousViewHolder == null){
                isLastInGroup = true
            } else if(currentViewHolder is RVMessageAdapter.ToViewHolder &&
                (nextViewHolder is RVMessageAdapter.FromViewHolder ||
                        nextViewHolder is RVMessageAdapter.HeaderViewHolder)){
                /*its different*/
                isLastInGroup = true
            }else if(currentViewHolder is RVMessageAdapter.FromViewHolder &&
                (nextViewHolder is RVMessageAdapter.ToViewHolder ||
                        nextViewHolder is RVMessageAdapter.HeaderViewHolder)){
                isLastInGroup = true
            }

            if(isLastInGroup){
                /*lol..just trying to get comfortable with destructuring in Kotlin*/
                var (x,y) = 0f to 0f
                if(currentViewHolder is RVMessageAdapter.ToViewHolder){
                    /*on the right*/
                    x = view.right.toFloat()
                    y = view.bottom.toFloat()
                    path.moveTo(x,y)
                    path.lineTo(x + 8.px, y)

                }else if(currentViewHolder is RVMessageAdapter.FromViewHolder){
                    x = view.left.toFloat()
                    y = view.bottom.toFloat()
                    path.moveTo(x,y)
                    path.lineTo(x - 8.px, y)
                }

                path.lineTo(x, y - 8.px)
                path.close()
                c.drawPath(path,paint)
            }
        }
    }


}