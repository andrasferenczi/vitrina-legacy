package stoyck.vitrina.domain.usecase

import android.content.Context
import retrofit2.HttpException
import stoyck.vitrina.R
import stoyck.vitrina.domain.UserReadableException
import stoyck.vitrina.network.RedditService
import stoyck.vitrina.network.data.RedditAbout
import stoyck.vitrina.persistence.data.PersistedSubredditData
import stoyck.vitrina.util.VResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TryAddSubredditUseCase @Inject constructor(
    // Reference not saved
    context: Context,
    private val loadSubreddits: LoadSubredditsUseCase,
    private val saveSubreddits: SaveSubredditsUseCase,
    private val redditService: RedditService
) {

    private val subredditDoesNotExistMessage =
        context.resources.getString(R.string.error_subreddit_does_not_exist)

    suspend operator fun invoke(requestedSubredditName: String):
            VResult<Pair<PersistedSubredditData, List<PersistedSubredditData>>> {
        val subreddit = try {
            redditService.retrieveSubreddit(requestedSubredditName)
        } catch (e: Exception) {
            if (e is HttpException) {
                if (e.code() == 404) {
                    val message =
                        subredditDoesNotExistMessage.format("/r/$requestedSubredditName")
                    return VResult.failure(UserReadableException(message))
                }
            }

            // Something else, handle it above
            throw e
        }

        // is it the default
        val subredditExists = subreddit != RedditAbout()

        if (!subredditExists) {
            val message = String.format(subredditDoesNotExistMessage, subreddit.displayName)
            return VResult.failure(UserReadableException(message))
        }

        val existingSubreddits = loadSubreddits()
        val newSubreddit = PersistedSubredditData.fromAbout(subreddit)

        val newSubreddits = listOf(
            // Add to the start
            newSubreddit,
            *existingSubreddits.toTypedArray()
        )

        saveSubreddits(newSubreddits)

        return VResult.success(newSubreddit to newSubreddits)
    }

}