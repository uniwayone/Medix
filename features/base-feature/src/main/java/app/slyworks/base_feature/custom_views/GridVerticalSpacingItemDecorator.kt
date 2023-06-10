package app.slyworks.base_feature.custom_views

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


/**
 *Created by Joshua Sylvanus, 3:37 PM, 1/12/2022.
 */
class GridVerticalSpacingItemDecorator() : RecyclerView.ItemDecoration() {
    //region Vars
    //endregion
    companion object{
        val spanCount = 2
        //val spacing:Int = App.getContext().resources.getDimensionPixelSize(R.dimen.grid_layout_spacing)
         val spacing = 20
    }
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position: Int = parent.getChildAdapterPosition(view)
        val column: Int = position % spanCount

            outRect.left = column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

           if(position >= spanCount)
               outRect.top = spacing //item top
    }
}
