package stoyck.vitrina.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_subreddit_suggestion_entry.view.*

class SubredditSuggestionsViewHolder(
    itemView: View,
    private val onClicked: (data: SubredditSuggestionData) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    fun bind(data: SubredditSuggestionData) {
        with(super.itemView) {
            suggestionContainer.setOnClickListener {
                onClicked(data)
            }
            suggestionName.text = data.name
        }
    }

}