package stoyck.vitrina.ui.subreddit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import stoyck.vitrina.R
import stoyck.vitrina.persistence.data.PersistedSubredditData
import stoyck.vitrina.ui.recyclerview.OnStartDragListener
import stoyck.vitrina.ui.recyclerview.TouchHelperAdapter
import java.util.*

class SubredditAdapter(
    private val dragListener: OnStartDragListener,
    private val onSubredditClickedListener: OnSubredditClickedListener
) : RecyclerView.Adapter<SubredditViewHolder>(), TouchHelperAdapter {

    val data: MutableList<PersistedSubredditData> = ArrayList(
        listOf(
            PersistedSubredditData(
                id = "w",
                name = "xxtest",
                minUpvoteCount = 2220
            ),
            PersistedSubredditData(
                id = "w",
                name = "xxteswdt",
                minUpvoteCount = 2520
            ),
            PersistedSubredditData(
                id = "w",
                name = "xxteswdt",
                minUpvoteCount = 2520
            )
        )

    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubredditViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_subreddit_entry, parent, false)

        return SubredditViewHolder(view, dragListener, onSubredditClickedListener)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(viewHolder: SubredditViewHolder, position: Int) {
        viewHolder.bind(data[position])
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(data, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(data, i, i - 1)
            }
        }

        notifyItemMoved(fromPosition, toPosition)
    }
}