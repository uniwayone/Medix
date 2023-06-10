package app.slyworks.base_feature.custom_views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import app.slyworks.base_feature.R
import kotlin.properties.Delegates


/**
 * Created by Joshua Sylvanus, 8:26 PM, 09/05/2022.
 */
class CurvedView
    @JvmOverloads
    constructor(
        context: Context,
        attrs:AttributeSet? = null,
        defStyleAttr:Int = 0,
        defStyleRes:Int = 0): View(context,attrs,defStyleAttr,defStyleRes) {
    //region Vars
    private var mColor by Delegates.notNull<Int>()
    private lateinit var mPaint:Paint
    private var mPath:Path = Path()
    private var mRect: RectF = RectF()
    //endregion

    init {
       val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CurvedView)

       try{
          mColor = typedArray.getColor(
              R.styleable.CurvedView_curvedView_backgroundColor,
              ContextCompat.getColor(this.context, R.color.appBlue))

           mPaint = Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
               color = mColor
               style = android.graphics.Paint.Style.FILL
           }
       } finally {
           /*TODO:get background color*/
           typedArray.recycle()
       }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val viewHeight:Float = getHeight().toFloat()
        val viewWidth:Float = getWidth().toFloat()

       /* canvas?.drawRect(0F,0F,viewWidth * 0.75F, viewHeight,mPaint)
        canvas?.drawArc(
            mRect.also{
                it.left = viewWidth * 0.75F
                it.top = -viewHeight
                it.right = (viewWidth * 0.25F) * 2
                it.bottom = viewHeight * 2
            },
            0F,
            90F,
            true,
            mPaint)*/


        with(mPath){
            reset()
//            move to origin
            moveTo(0F,0F)

//            draw left side
            lineTo(0F, viewHeight * 0.94F)

//            draw bottom to point where curve will start
            val bottomLength:Float = viewWidth * 0.75F
            //lineTo(bottomLength, viewHeight.toFloat())

//            center of circle ,since its like 1 quadrant of a circle, 75% of width on the top
            val x:Float = viewWidth * 0.75F
            val y:Float = 0F
            val _top:Float = viewWidth - x
            /*with(mRect){
                left = 0F
                top = 0F
                right = viewWidth
                bottom = viewHeight
            }
            arcTo(mRect, 360F, 90F, true)*/

            cubicTo(viewWidth * 0.1F, viewHeight * 0.5F,
                    viewWidth * 0.7F, viewHeight * 0.95F,
                        viewWidth, 0F)
           /* move without drawing a line to top-right corner*/
            moveTo(viewWidth, -viewHeight)

//            drawing from the right top end to 0,0
            lineTo(-width.toFloat(), 0F)

            close()
        }

        canvas?.drawPath(mPath, mPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
       /* val specWidth:Int = MeasureSpec.getSize(widthMeasureSpec)
        val specHeight:Int = MeasureSpec.getSize(heightMeasureSpec)

        val width:Int = resolveSizeAndState(specWidth, widthMeasureSpec, 0)
        val height:Int = resolveSizeAndState(specHeight, heightMeasureSpec, 0)

         setMeasuredDimension(width, height)*/
    }
}