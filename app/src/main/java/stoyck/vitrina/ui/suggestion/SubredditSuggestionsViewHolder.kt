package stoyck.vitrina.ui.suggestion

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_subreddit_suggestion_entry.view.*
import stoyck.vitrina.R

class SubredditSuggestionsViewHolder(
    itemView: View,
    private val onClicked: (data: SubredditSuggestionData) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val members = itemView.context.applicationContext.resources.getString(R.string.members)

    fun bind(data: SubredditSuggestionData) {
        with(super.itemView) {
            suggestionContainer.setOnClickListener {
                onClicked(data)
            }
            subredditName.text = data.name
            subscriberCount.text =
                String.format(members, String.format("%,d", data.subscriberCount))
        }
    }

}