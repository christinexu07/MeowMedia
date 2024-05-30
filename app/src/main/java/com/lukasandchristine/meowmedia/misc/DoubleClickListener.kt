package com.lukasandchristine.meowmedia.misc

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class DoubleClickListener(
    private val onSingleClick: (position: Int) -> Unit,
    private val onDoubleClick: (position: Int) -> Unit
) : RecyclerView.OnItemTouchListener {

    private var lastClickTime = 0L
    private var lastClickPosition = -1

    override fun onInterceptTouchEvent(recyclerView: RecyclerView, event: MotionEvent): Boolean {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        val position = if (view != null) recyclerView.getChildAdapterPosition(view) else -1

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime < DOUBLE_CLICK_TIMEOUT && lastClickPosition == position) {
                    onDoubleClick(position)
                }
                lastClickTime = currentTime
                lastClickPosition = position
            }
        }

        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    companion object {
        private const val DOUBLE_CLICK_TIMEOUT = 300L
    }
}