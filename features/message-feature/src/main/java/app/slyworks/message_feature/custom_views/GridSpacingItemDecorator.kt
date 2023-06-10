package app.slyworks.message_feature.custom_views

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import app.slyworks.message_feature.R


/**
 * Created by Joshua Sylvanus, 12:34 AM, 18/1/2022.
 */
class GridSpacingItemDecorator(private val context: Context) : RecyclerView.ItemDecoration() {
    //region Vars
    private val space:Int = context.resources.getDimensionPixelSize(app.slyworks.base_feature.R.dimen.grid_layout_spacing)
    //endregion

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
       outRect.left = space
       outRect.right = space
       outRect.bottom = space

        //add top margin only for the first item
        /*if(parent.getChildLayoutPosition(view) == 0)
            outRect.top = space
        else
            outRect.top = 0*/
    }
}