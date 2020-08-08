package stoyck.vitrina.ui.subreddit

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_subreddit_entry.view.*
import stoyck.vitrina.persistence.data.PersistedSubredditData
import stoyck.vitrina.ui.recyclerview.OnStartDragListener
import stoyck.vitrina.util.DebouncedTextWatcher

class SubredditViewHolder(
    itemView: View,
    private val dragListener: OnStartDragListener,
    private val onSubredditClickedListener: OnSubredditClickedListener
) : RecyclerView.ViewHolder(itemView) {


    @SuppressLint("ClickableViewAccessibility")
    fun bind(content: PersistedSubredditData) {
        val position = adapterPosition

        with(super.itemView) {
            fun updateData() {
                val upvoteCount =
                    subredditUpvoteCountEditText.text?.toString()?.toLongOrNull() ?: 0
                val newData = content.copy(minUpvoteCount = upvoteCount)
                onSubredditClickedListener.onSubredditChanged(
                    newData,
                    position
                )
            }


            subredditOuterLayout.setOnClickListener {
                // Not used
                onSubredditClickedListener.onSubredditClicked(
                    content,
                    position
                )
            }

            subredditNameTextView.text = content.name

            subredditUpvoteCountEditText.setText(content.minUpvoteCount.toString())
            subredditUpvoteCountEditText.addTextChangedListener(
                DebouncedTextWatcher(delayMillis = 300) {
                    updateData()
                }
            )
            subredditUpvoteCountEditText.setOnEditorActionListener { view, actionId, _ ->
                return@setOnEditorActionListener when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        // hide keyboard
                        view?.let { v ->
                            val imm =
                                context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                            imm?.hideSoftInputFromWindow(v.windowToken, 0)
                        }

                        subredditUpvoteCountEditText.clearFocus()
                        updateData()

                        true
                    }
                    else -> false
                }
            }

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