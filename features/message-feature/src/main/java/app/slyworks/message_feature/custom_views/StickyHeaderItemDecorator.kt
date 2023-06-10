package app.slyworks.message_feature.custom_views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.RecyclerView
import app.slyworks.message_feature.message.RVMessageAdapter


/**
 *Created by Joshua Sylvanus, 8:44 PM, 05/05/2022.
 */
class StickyHeaderItemDecorator(private val context: Context):RecyclerView.ItemDecoration() {
    //region Vars
    private val paint:Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, app.slyworks.base_feature.R.color.Grey)
    }

    private var currentHeaderBitmap:Bitmap? = null
    //endregion

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val stickyViewHolders: Sequence<RVMessageAdapter.HeaderViewHolder> =
            parent.children
                .map { parent.findContainingViewHolder(it) }
                .filter { it is RVMessageAdapter.HeaderViewHolder }
                .map { it as RVMessageAdapter.HeaderViewHolder }

        if(stickyViewHolders.toList().isEmpty())
            return

        stickyViewHolders.forEach { it.itemView.alpha = 1f }

        /*take first StickyViewHolder and its params*/
        val viewHolder: RVMessageAdapter.HeaderViewHolder = stickyViewHolders.first()

        val viewHolderBitmap: Bitmap = viewHolder.itemView.drawToBitmap()
        val viewHolderY:Float = viewHolder.itemView.y ?:0f

        /*init headerBitmap if needed*/
        if(currentHeaderBitmap == null || viewHolderY <= 0f)
            currentHeaderBitmap = viewHolderBitmap

        /*calculate bitmap top offset*/
        val bitmapHeight:Int = currentHeaderBitmap?.height ?: 0
        val bitmapTopOffset =
            if(0 <= viewHolderY && viewHolderY <= bitmapHeight)
               viewHolderY - bitmapHeight
            else
                0f

        /*hide view*/
        viewHolder.itemView.alpha =
            if(viewHolderY < 0f)
                0f
            else
                1f

        /*draw bitmap header*/
        currentHeaderBitmap?.let{
            c.drawBitmap(it, 0f, bitmapTopOffset, paint)
        }
    }


}