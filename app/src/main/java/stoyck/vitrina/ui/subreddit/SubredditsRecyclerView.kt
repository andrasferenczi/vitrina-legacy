package stoyck.vitrina.ui.subreddit

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import stoyck.vitrina.R
import stoyck.vitrina.persistence.data.PersistedSubredditData
import stoyck.vitrina.ui.recyclerview.CustomItemTouchHelper
import stoyck.vitrina.ui.recyclerview.TouchCallback
import stoyck.vitrina.ui.recyclerview.TouchHelperAdapter
import stoyck.vitrina.util.Debouncer

class SubredditsRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle),
    TouchHelperAdapter,
    OnSubredditClickedListener {

    private val subredditAdapter: SubredditAdapter

    private var onSubredditClickedListener: OnSubredditClickedListener? = null
    var onSave: ((subreddits: List<PersistedSubredditData>) -> Unit)? = null

    // Sending a message to the user
    var onMessage: ((message: String) -> Unit)? = null

    private val onClickDebouncer = Debouncer()
    private val onSaveDebouncer = Debouncer()

    private val removedMessage = context.getString(R.string.message_subreddit_removed)

    init {
        val callback = TouchCallback(this)
        val touchHelper = CustomItemTouchHelper(callback)

        subredditAdapter = SubredditAdapter(touchHelper, this)
        touchHelper.attachToRecyclerView(this)

        layoutManager = LinearLayoutManager(context)
        adapter = subredditAdapter
    }

    fun setData(data: List<PersistedSubredditData>) {
        val currentData = subredditAdapter.data

        if (data.size == currentData.size) {
            // Deep equals so it does not stop the dragging
            if (data == currentData) {
                return
            }
        }

        currentData.let {
            it.clear()
            it.addAll(data)
        }

        subredditAdapter.notifyDataSetChanged()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        subredditAdapter.onItemMove(fromPosition, toPosition)

        this.onSaveDebouncer {
            val data = subredditAdapter.data.toList()
            onSave?.invoke(data)
        }
    }

    override fun onItemDismiss(position: Int) {
        val itemToBeRemoved = subredditAdapter.data[position]
        val message = this.removedMessage.format("/r/${itemToBeRemoved.name}")

        subredditAdapter.onItemDismiss(position)

        this.onSaveDebouncer {
            val data = subredditAdapter.data.toList()
            onSave?.invoke(data)
            onMessage?.invoke(message)
        }
    }

    override fun onSubredditClicked(content: PersistedSubredditData, position: Int) {
        this.onClickDebouncer {
            onSubredditClickedListener?.onSubredditClicked(content, position)
        }
    }
}