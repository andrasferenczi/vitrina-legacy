package stoyck.vitrina.domain.usecase

import android.content.Context
import android.net.ConnectivityManager
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class IsNetworkAvailableUseCase @Inject constructor(
    private val context: Context
) {

    suspend operator fun invoke(): Boolean {
//        val connectivityManager =
//            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
//        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        return connectivityManager?.activeNetworkInfo != null
                && connectivityManager.activeNetworkInfo?.isConnected ?: false
    }

}