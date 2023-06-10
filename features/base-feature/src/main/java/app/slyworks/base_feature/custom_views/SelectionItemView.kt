package app.slyworks.base_feature.custom_views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import app.slyworks.base_feature.R
import app.slyworks.utils_lib.utils.plusAssign
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject


/**
 * Created by Joshua Sylvanus, 7:39 PM, 21/10/2022.
 */
class SelectionItemView
@JvmOverloads
constructor(
    context: Context,
    attrs:AttributeSet? = null,
    defStyle:Int = 0,
    defStyleAttr:Int = 0): ConstraintLayout(context, attrs, defStyle, defStyleAttr) {

    //region Vars
    private var view:ConstraintLayout
    private var radioButton:RadioButton
    private var tvTitle:TextView
    private var tvSubtitle:TextView

    private var title:String
    private var subtitle:String
    private var isCurrentlySelected:Boolean = false

    private val disposables:CompositeDisposable = CompositeDisposable()
    private val internalSubject:PublishSubject<Boolean> = PublishSubject.create()
    private val subject:PublishSubject<Boolean> = PublishSubject.create()
    //endregion

    init{
        val a:TypedArray =
         context.theme.obtainStyledAttributes(attrs, R.styleable.SelectionItemView, 0, 0)

        try{
          title = a.getString(R.styleable.SelectionItemView_siv_title) ?: "not_set"
          subtitle = a.getString(R.styleable.SelectionItemView_siv_sub_title) ?: "not_set"
        }finally{
          a.recycle()
        }

        view = inflate(context, R.layout.selection_item, this) as ConstraintLayout
        radioButton = view.findViewById(R.id.siv_rb)
        tvTitle = view.findViewById(R.id.siv_title)
        tvSubtitle = view.findViewById(R.id.siv_sub_title)

        tvTitle.setText(title)
        tvSubtitle.setText(subtitle)

        view.setOnClickListener {
            if(isCurrentlySelected)
                return@setOnClickListener

            handleStateChange(!isCurrentlySelected)
        }

        radioButton.setOnClickListener {
            if(isCurrentlySelected)
                return@setOnClickListener

            handleStateChange(!isCurrentlySelected)
        }

        disposables +=
        internalSubject.subscribe(::handleStateChange)
    }

    private fun handleStateChange(status:Boolean){
        isCurrentlySelected = status
        radioButton.isChecked = isCurrentlySelected

        view.setBackgroundColor(ContextCompat.getColor(this.context, android.R.color.transparent))
        if(isCurrentlySelected)
            view.setBackgroundResource(R.drawable.siv_background)
        else
            view.setBackgroundResource(R.drawable.siv_background_default)

        if(subject.hasObservers())
            subject.onNext(isCurrentlySelected)
    }

    fun observeChanges(): Observable<Boolean> = subject.hide()
    fun setCurrentStatus(status:Boolean):Unit = internalSubject.onNext(status)

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        subject.onComplete()
        internalSubject.onComplete()
        disposables.clear()
    }
}