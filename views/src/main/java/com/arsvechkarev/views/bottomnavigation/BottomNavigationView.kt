package com.arsvechkarev.views.bottomnavigation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
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
import com.arsvechkarev.views.R
import core.FontManager
import core.Loggable
import core.extenstions.dp
import core.extenstions.toBitmap

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
  
  private val verticalInset = dp(8)
  private val innerInset = dp(4)
  private val iconSize: Float
  private val middlePointsXCoords = FloatArray(drawableIds.size)
  
  private val icons = ArrayList<Bitmap>(drawableIds.size)
  
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
    iconPaint.color = attributes.getColor(R.styleable.BottomNavigationView_iconColor, Color.BLACK)
    val labelColor = attributes.getColor(R.styleable.BottomNavigationView_labelColor, Color.BLACK)
    labelPaint.colorFilter = PorterDuffColorFilter(labelColor, PorterDuff.Mode.SRC_ATOP)
    labelPaint.textSize =
        attributes.getDimension(R.styleable.BottomNavigationView_labelTextSize, dp(14))
    attributes.recycle()
    (parent as? ViewGroup)?.clipChildren = false
    initIcons()
    initLayouts()
    startAnimation(currentItemId)
  }
  
  fun setOnItemClickListener(action: (Int) -> Unit) {
    this.itemClickListener = action
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    var maxIconHeight = -1
    if (iconSize == -1f) {
      icons.forEach { if (it.height > maxIconHeight) maxIconHeight = it.height }
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
        iconHalfWidth = icon.width / 2
        iconHalfHeight = icon.height / 2
      } else {
        iconHalfWidth = (iconSize / 2).toInt()
        iconHalfHeight = (iconSize / 2).toInt()
      }
      val iconCenterY = paddingTop + verticalInset + icon.height / 2
      tempRect.set(
        middlePoint - iconHalfWidth, iconCenterY - iconHalfHeight,
        middlePoint + iconHalfWidth, iconCenterY + iconHalfHeight
      )
      canvas.withIconScaleIfNeeded(i, middlePoint) {
        drawBitmap(icon, null, tempRect, iconPaint)
      }
    }
    for (i in texts.indices) {
      canvas.save()
      val middlePoint = middlePointsXCoords[i]
      val layout = labels[i]
      canvas.translate(middlePoint - layout.width / 2,
        paddingTop + verticalInset + icons[i].height + innerInset)
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
    val iconHeight = if (iconSize != -1f) iconSize.toInt() else icons[idToCheck].height
    val distanceToTop = paddingTop + verticalInset + iconHeight + innerInset
    val py = height / 2f - distanceToTop
    scale(scale, scale, labels[idToCheck].width / 2f, py)
    action(this)
    restore()
  }
  
  
  private fun initIcons() {
    for (i in drawableIds.indices) {
      icons.add(resources.getDrawable(drawableIds[i], context.theme).toBitmap())
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
}