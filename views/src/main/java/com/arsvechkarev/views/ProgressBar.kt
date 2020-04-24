package com.arsvechkarev.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import core.extenstions.block
import core.extenstions.cancelIfRunning
import core.extenstions.dp
import core.extenstions.f

class ProgressBar @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : View(context, attrs) {
  
  private val innerStartedAngle = 50f
  private val outerStartedAngle = 120f
  private val minSize = 32.dp.toInt()
  private val sweepAngle = 260f
  private val trackWidth: Float
  
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.BLACK
    style = Paint.Style.STROKE
    strokeCap = Paint.Cap.ROUND
  }
  private val outerOval = RectF()
  private val innerOval = RectF()
  private var outerRotationAngle = 0f
  private var innerRotationAngle = 0f
  
  private val outerAnimator = ValueAnimator().apply {
    configure(1500L) { outerRotationAngle = animatedValue as Float }
  }
  
  private val innerAnimator = ValueAnimator().apply {
    configure(800L) { innerRotationAngle = -(animatedValue as Float) }
  }
  
  init {
    val attributes = context.obtainStyledAttributes(attrs, R.styleable.ProgressBar, 0, 0)
    trackWidth = attributes.getDimension(R.styleable.ProgressBar_trackWidth, -1f)
    paint.color = attributes.getColor(R.styleable.ProgressBar_color, Color.BLACK)
    attributes.recycle()
  }
  
  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    innerAnimator.start()
    outerAnimator.start()
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = resolveSize(minSize, widthMeasureSpec)
    val height = resolveSize(minSize, heightMeasureSpec)
    setMeasuredDimension(width, height)
  }
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    if (trackWidth == -1f) {
      paint.strokeWidth = w / 9f
    } else {
      paint.strokeWidth = trackWidth
    }
    val outerInset = paint.strokeWidth / 2
    outerOval.set(paddingStart.f + outerInset, paddingTop.f + outerInset,
      w.f - paddingEnd - outerInset, h.f - paddingBottom - outerInset)
    val innerInset = paint.strokeWidth * 2 + outerInset
    innerOval.set(paddingStart.f + innerInset, paddingTop + innerInset,
      w.f - paddingEnd - innerInset, h.f - paddingBottom - innerInset)
  }
  
  override fun onDraw(canvas: Canvas) {
    canvas.block {
      rotate(outerRotationAngle, width / 2f, height / 2f)
      canvas.drawArc(outerOval, outerStartedAngle, sweepAngle, false, paint)
      rotate(-outerRotationAngle + innerRotationAngle, width / 2f, height / 2f)
      canvas.drawArc(innerOval, innerStartedAngle, sweepAngle, false, paint)
    }
  }
  
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    innerAnimator.cancelIfRunning()
    outerAnimator.cancelIfRunning()
  }
  
  private fun ValueAnimator.configure(duration: Long, onUpdate: ValueAnimator.() -> Unit) {
    setFloatValues(0f, 360f)
    addUpdateListener {
      onUpdate(this)
      invalidate()
    }
    interpolator = LinearInterpolator()
    repeatCount = ValueAnimator.INFINITE
    this.duration = duration
  }
}