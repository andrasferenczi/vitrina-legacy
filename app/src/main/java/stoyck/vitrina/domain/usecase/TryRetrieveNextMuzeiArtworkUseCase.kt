package stoyck.vitrina.domain.usecase

import android.net.Uri
import com.google.android.apps.muzei.api.provider.Artwork
import stoyck.vitrina.network.data.RedditPost
import stoyck.vitrina.network.data.fullPostLink
import stoyck.vitrina.persistence.data.PersistedPostData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TryRetrieveNextMuzeiArtworkUseCase @Inject constructor(
    private val tryRetrieveNextPostUseCase: TryRetrieveNextPostAndMarkAsShownUseCase,
) {

    private fun RedditPost.toArtwork(): Artwork {
        return Artwork(
            token = id,
            title = subredditNamePrefixed,
            byline = title,
            attribution = author,
            persistentUri = Uri.parse(url),
            webUri = Uri.parse(fullPostLink)
        )
    }

    // Todo: This is not good
    suspend operator fun invoke(): List<Artwork> {
        // Load images already takes the previous posts into account
        val posts = loadImagesUseCase()

        val artworks = posts
            .map { it.toArtwork() }

        updatePersistedPosts(posts)

        return artworks
    }

    private suspend fun updatePersistedPosts(posts: List<RedditPost>) {
        val previousPosts = loadPostsUseCase()
        val newPosts = posts.map {
            PersistedPostData(
                id = it.id,
                imageLink = it.url
            )
        }

        val allPosts = listOf(
            *previousPosts.toTypedArray(),
            *newPosts.toTypedArray()
        )
            // do not save thousands of posts - bad for serialization
            .takeLast(200)

        savePostsUseCase(allPosts)
    }

}