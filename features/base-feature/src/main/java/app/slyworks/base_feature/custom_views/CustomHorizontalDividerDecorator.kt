package app.slyworks.base_feature.custom_views

import android.graphics.Rect
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView

class CustomDividerDecorator<R: View>(@IdRes private val id:Int)
    : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1){
            view.findViewById<R>(id).visibility = View.GONE
        }
    }
}
