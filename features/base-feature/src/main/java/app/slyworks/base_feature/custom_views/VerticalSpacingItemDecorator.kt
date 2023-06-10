package app.slyworks.base_feature.custom_views

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


/**
 *Created by Joshua Sylvanus, 3:50 PM, 1/12/2022.
 */
class VerticalSpacingItemDecorator(var spacing:Int = 18) :RecyclerView.ItemDecoration() {
    //region Vars
    //endregion

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if(parent.getChildAdapterPosition(view) == 0){
            outRect.top = spacing
        }
        if (parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount - 1) {
            outRect.bottom = spacing
        }
    }
}

