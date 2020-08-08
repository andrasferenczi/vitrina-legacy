package stoyck.vitrina.ui.subreddit

import stoyck.vitrina.persistence.data.PersistedSubredditData

interface OnSubredditClickedListener {

    fun onSubredditClicked(content: PersistedSubredditData, position: Int)

}