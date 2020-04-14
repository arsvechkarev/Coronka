package com.arsvechkarev.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.text.BoringLayout
import android.text.Layout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import core.FontManager
import core.Loggable
import core.extenstions.dp
import core.extenstions.i

class BottomNavigationView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : View(context, attrs), Loggable {
  
  override val logTag = "BottomNavigationView"
  
  private val drawableIds = intArrayOf(
    R.drawable.ic_map,
    R.drawable.ic_bar_chart,
    R.drawable.ic_info
  )
  
  private val texts = arrayOf(
    resources.getString(R.string.label_map),
    resources.getString(R.string.label_stats),
    resources.getString(R.string.label_faq)
  )
  
  private var itemClickListener: (Int) -> Unit = {}
  
  private val verticalInset = 8.dp
  private val innerInset = 4.dp
  private val iconSize: Float
  private val middlePointsXCoords = FloatArray(drawableIds.size)
  
  private val icons = ArrayList<Drawable>(drawableIds.size)
  
  private val tempRect = RectF()
  private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val labels = ArrayList<Layout>(texts.size)
  private val labelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { typeface = FontManager.rubik }
  
  private var currentItemId: Int
  private var formerItemId = -1
  private var currentItemScale = 1f
  private var formerItemScale = 1f
  private val currentHolder = PropertyValuesHolder.ofFloat("currentHolder", 1f, 1.25f)
  private val formerHolder = PropertyValuesHolder.ofFloat("formerHolder", 1.25f, 1f)
  private val selectedItemAnimator = ValueAnimator
      .ofPropertyValuesHolder(currentHolder, formerHolder).apply {
        duration = 250
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener {
          currentItemScale = getAnimatedValue("currentHolder") as Float
          formerItemScale = getAnimatedValue("formerHolder") as Float
          invalidate()
        }
        addListener(object : AnimatorListenerAdapter() {
          override fun onAnimationStart(animation: Animator) {
            isClickable = false
          }
          
          override fun onAnimationEnd(animation: Animator) {
            isClickable = true
          }
        })
      }
  
  init {
    val attributes = context.obtainStyledAttributes(attrs, R.styleable.BottomNavigationView)
    currentItemId = attributes.getInteger(R.styleable.BottomNavigationView_defaultItemId, 0)
    iconSize = attributes.getDimension(R.styleable.BottomNavigationView_bitmapIconSize, -1f)
    val color = attributes.getColor(R.styleable.BottomNavigationView_iconColor, Color.BLACK)
    iconPaint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    val labelColor = attributes.getColor(R.styleable.BottomNavigationView_labelColor, Color.BLACK)
    labelPaint.colorFilter = PorterDuffColorFilter(labelColor, PorterDuff.Mode.SRC_ATOP)
    labelPaint.textSize =
        attributes.getDimension(R.styleable.BottomNavigationView_labelTextSize, 14.dp)
    attributes.recycle()
    (parent as? ViewGroup)?.clipChildren = false
    initIcons()
    initLayouts()
    startAnimation(currentItemId)
    setBackgroundResource(R.drawable.bg_the_bottom_navigation)
  }
  
  fun setOnItemClickListener(action: (Int) -> Unit) {
    this.itemClickListener = action
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    var maxIconHeight = -1
    if (iconSize == -1f) {
      icons.forEach { if (it.intrinsicHeight > maxIconHeight) maxIconHeight = it.intrinsicHeight }
    } else {
      maxIconHeight = iconSize.toInt()
    }
    var maxTextHeight = -1
    labels.forEach { if (it.height > maxTextHeight) maxTextHeight = it.height }
    val maxHeight = paddingTop + maxIconHeight + verticalInset.toInt() * 2 +
        innerInset.toInt() + maxTextHeight + paddingBottom
    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
      resolveSize(maxHeight, heightMeasureSpec))
  }
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    val size = icons.size
    val distance = w / size
    var middlePoint = distance / 2f
    middlePointsXCoords[0] = middlePoint
    for (i in 1 until size) {
      middlePoint += distance
      middlePointsXCoords[i] = middlePoint
    }
  }
  
  override fun onDraw(canvas: Canvas) {
    for (i in 0 until icons.size) {
      val middlePoint = middlePointsXCoords[i]
      val icon = icons[i]
      val iconHalfWidth: Int
      val iconHalfHeight: Int
      if (iconSize == -1f) {
        iconHalfWidth = icon.intrinsicWidth / 2
        iconHalfHeight = icon.intrinsicHeight / 2
      } else {
        iconHalfWidth = (iconSize / 2).toInt()
        iconHalfHeight = (iconSize / 2).toInt()
      }
      val iconCenterY = paddingTop + verticalInset + iconHalfHeight
      tempRect.set(
        middlePoint - iconHalfWidth, iconCenterY - iconHalfHeight,
        middlePoint + iconHalfWidth, iconCenterY + iconHalfHeight
      )
      icon.setBounds(tempRect.left.i, tempRect.top.i, tempRect.right.i, tempRect.bottom.i)
      canvas.withIconScaleIfNeeded(i, middlePoint) {
        icon.draw(canvas)
      }
    }
    for (i in texts.indices) {
      canvas.save()
      val middlePoint = middlePointsXCoords[i]
      val layout = labels[i]
      val iconHeight = if (iconSize == -1f) icons[i].intrinsicHeight else iconSize.toInt()
      canvas.translate(middlePoint - layout.width / 2,
        paddingTop + verticalInset + iconHeight + innerInset)
      canvas.withTextScaleIfNeeded(i) { layout.draw(canvas) }
      canvas.restore()
    }
  }
  
  override fun onTouchEvent(event: MotionEvent): Boolean {
    when (event.action) {
      ACTION_DOWN -> {
        val step = width / icons.size
        var clickedIconId = 0
        var distance = 0
        for (i in icons.indices) {
          distance += step
          if (event.x < distance) {
            clickedIconId = i
            break
          }
        }
        if (clickedIconId != currentItemId) {
          
          startAnimation(clickedIconId)
          itemClickListener(clickedIconId)
        }
        return true
      }
      ACTION_UP -> {
        return performClick()
      }
    }
    return false
  }
  
  private fun startAnimation(clickedNum: Int) {
    formerItemId = currentItemId
    currentItemId = clickedNum
    selectedItemAnimator.start()
  }
  
  private fun Canvas.withIconScaleIfNeeded(
    idToCheck: Int,
    middlePointX: Float,
    action: Canvas.() -> Unit
  ) {
    save()
    val scale = when (idToCheck) {
      currentItemId -> currentItemScale
      formerItemId -> formerItemScale
      else -> 1f
    }
    scale(scale, scale, middlePointX, height / 2f)
    action(this)
    restore()
  }
  
  private fun Canvas.withTextScaleIfNeeded(
    idToCheck: Int,
    action: Canvas.() -> Unit
  ) {
    save()
    val scale = when (idToCheck) {
      currentItemId -> currentItemScale
      formerItemId -> formerItemScale
      else -> 1f
    }
    val iconHeight = if (iconSize != -1f) iconSize.toInt() else icons[idToCheck].intrinsicHeight
    val distanceToTop = paddingTop + verticalInset + iconHeight + innerInset
    val py = height / 2f - distanceToTop
    scale(scale, scale, labels[idToCheck].width / 2f, py)
    action(this)
    restore()
  }
  
  
  private fun initIcons() {
    for (i in drawableIds.indices) {
      icons.add(resources.getDrawable(drawableIds[i], context.theme))
    }
  }
  
  private fun initLayouts() {
    for (i in texts.indices) {
      val text = texts[i]
      val metrics = BoringLayout.isBoring(text, labelPaint)
      val layout = BoringLayout(text, labelPaint, labelPaint.measureText(text).toInt(),
        Layout.Alignment.ALIGN_NORMAL, 0f, 0f, metrics, false)
      labels.add(layout)
    }
  }
  
    override fun onSaveInstanceState(): Parcelable? {
      val superState = super.onSaveInstanceState() ?: return null
      val myState = BottomNavigationState(
        superState)
      myState.currentItemId = this.currentItemId
      myState.formerItemId = this.formerItemId
      return myState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
      super.onRestoreInstanceState(state)
      val savedState = state as BottomNavigationState
      currentItemId = savedState.currentItemId
      formerItemId = savedState.formerItemId
      startAnimation(currentItemId)
    }
  
  class BottomNavigationState : BaseSavedState {
    var currentItemId = -1
    var formerItemId = -1
    
    constructor(parcelable: Parcelable) : super(parcelable)
    
    constructor(parcel: Parcel) : super(parcel) {
      currentItemId = parcel.readInt()
      formerItemId = parcel.readInt()
    }
    
    override fun writeToParcel(parcel: Parcel, flags: Int) {
      super.writeToParcel(parcel, flags)
      parcel.writeInt(currentItemId)
      parcel.writeInt(formerItemId)
    }
    
    companion object CREATOR : Parcelable.Creator<BottomNavigationState> {
      
      override fun createFromParcel(parcel: Parcel): BottomNavigationState {
        return BottomNavigationState(
          parcel)
      }
      
      override fun newArray(size: Int): Array<BottomNavigationState?> {
        return arrayOfNulls(size)
      }
    }
  }
}