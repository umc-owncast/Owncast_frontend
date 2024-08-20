package kr.dori.android.own_cast.study

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalMarginItemDecoration(private val margin: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (position == 0) {
            outRect.left = margin * 2 // 첫 번째 아이템에 더 큰 여백
            outRect.right = margin / 2
        } else if (position == parent.adapter?.itemCount?.minus(1)) {
            outRect.left = margin / 2
            outRect.right = margin * 2 // 마지막 아이템에 더 큰 여백
        } else {
            outRect.left = margin / 2
            outRect.right = margin / 2
        }
    }
}