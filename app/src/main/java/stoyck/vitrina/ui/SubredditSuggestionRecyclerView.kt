package stoyck.vitrina.ui

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SubredditSuggestionRecyclerView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    var onSubredditSuggestionClicked: ((data: SubredditSuggestionData) -> Unit)? = null

    private val onClick: (data: SubredditSuggestionData) -> Unit = {
        this.onSubredditSuggestionClicked?.invoke(it)
    }

    private val suggestionsAdapter = SubredditSuggestionsAdapter(this.onClick)

    init {
        layoutManager = LinearLayoutManager(context)
        adapter = suggestionsAdapter
    }

    fun setData(data: List<SubredditSuggestionData>) {
        suggestionsAdapter.data.let {
            it.clear()
            it.addAll(data)
        }

        suggestionsAdapter.notifyDataSetChanged()
    }

}
