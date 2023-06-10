package app.slyworks.message_feature.custom_views

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import app.slyworks.message_feature.message.RVMessageAdapter


/**
 *Created by Joshua Sylvanus, 6:05 AM, 06/05/2022.
 */
class SpacingItemDecorator:RecyclerView.ItemDecoration() {
    //region Vars
    //endregion

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val currentViewHolder: RVMessageAdapter.MViewHolder =
            parent.getChildViewHolder(view) as RVMessageAdapter.MViewHolder

        val nextViewHolder: RVMessageAdapter.MViewHolder? =
            parent.findViewHolderForAdapterPosition(currentViewHolder.bindingAdapterPosition + 1)
                    as? RVMessageAdapter.MViewHolder

        var isLastInGroup:Boolean = false
        if(nextViewHolder == null){
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

        /*giving small spacing only if its different ViewHolder i.e its already 'last in the group of messages'*/
        if(isLastInGroup){
            outRect.bottom = 10
        }
    }
}