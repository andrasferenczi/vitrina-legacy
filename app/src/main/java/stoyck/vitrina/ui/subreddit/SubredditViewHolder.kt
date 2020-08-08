package stoyck.vitrina.ui.subreddit

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_subreddit_entry.view.*
import stoyck.vitrina.R
import stoyck.vitrina.persistence.data.PersistedSubredditData
import stoyck.vitrina.ui.recyclerview.OnStartDragListener

class SubredditViewHolder(
    itemView: View,
    private val dragListener: OnStartDragListener,
    private val onSubredditClickedListener: OnSubredditClickedListener
) : RecyclerView.ViewHolder(itemView) {


    @SuppressLint("ClickableViewAccessibility")
    fun bind(content: PersistedSubredditData) {
        with(super.itemView) {
            val resources = this.context.applicationContext.resources

            subredditOuterLayout.setOnClickListener {
                onSubredditClickedListener.onSubredditClicked(
                    content,
                    this@SubredditViewHolder.adapterPosition
                )
            }

            subredditNameTextView.text = content.name
            subredditUpvoteCountTextView.text = resources.getString(R.string.subreddit_min_upvote_count_text, content.minUpvoteCount)

            reorderButton.setOnTouchListener { _, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        dragListener.onStartDrag(this@SubredditViewHolder)
                        true
                    }
                    else -> false
                }
            }
        }
    }

}