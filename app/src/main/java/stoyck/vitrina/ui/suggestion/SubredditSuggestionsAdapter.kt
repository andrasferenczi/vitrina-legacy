package stoyck.vitrina.ui.suggestion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import stoyck.vitrina.R

class SubredditSuggestionsAdapter(
    private val onItemClicked: (data: SubredditSuggestionData) -> Unit
) : RecyclerView.Adapter<SubredditSuggestionsViewHolder>() {

    val data: MutableList<SubredditSuggestionData> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubredditSuggestionsViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_subreddit_suggestion_entry,
                parent,
                false
            )

        return SubredditSuggestionsViewHolder(
            view,
            this.onItemClicked
        )
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: SubredditSuggestionsViewHolder, position: Int) {
        holder.bind(data[position])
    }
}