package stoyck.vitrina.util

//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.drawable.Drawable
//import android.net.Uri
//import com.bumptech.glide.Glide
//import com.bumptech.glide.request.FutureTarget
//import com.bumptech.glide.request.target.BitmapImageViewTarget
//import com.bumptech.glide.request.target.CustomTarget
//import com.bumptech.glide.request.transition.Transition
//import com.squareup.picasso.Picasso
//import com.squareup.picasso.RequestCreator
//import com.squareup.picasso.Target
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import java.io.File
//import kotlin.coroutines.resume
//import kotlin.coroutines.resumeWithException
//import kotlin.coroutines.suspendCoroutine
//
///**
// * `use` solution, because the bitmap is recycled by glide
// */
////suspend fun useImage(
////    context: Context,
////    file: File
////) : Bitmap {
////    val target = object: CustomTarget<Bitmap>() {
////        override fun onLoadCleared(placeholder: Drawable?) {
////            TODO("Not yet implemented")
////        }
////
////        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
////            TODO("Not yet implemented")
////        }
////
////
////    }
////
////    Glide.with(context)
////        .asBitmap()
////        .load(file)
////        .into(BitmapImageViewTarget)
////
////    withContext(Dispatchers.IO) {
////        val bitmap = futureTarget.get()
////    }
////}
//
////suspend fun RequestCreator.intoBitmap(): Bitmap {
////    return withContext(Dispatchers.Main) {
////        // this somehow did not work when single function
////        this@intoBitmap.intoBitmapFromAnyThread()
////    }
////}
////
////private suspend fun RequestCreator.intoBitmapFromAnyThread(): Bitmap {
////    // Picasso:
////    // java.lang.IllegalStateException: Method call should happen from the main thread.
////    return suspendCoroutine {
////        // needs a strong reference to target so it is not garbage collected
////        // Picasso stores it as weak reference
////        val target = object : Target {
////            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
////                // TODO("Not yet implemented")
////            }
////
////            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
////                it.resumeWithException(e ?: RuntimeException())
////            }
////
////            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
////                if (bitmap == null) {
////                    it.resumeWithException(RuntimeException("Bitmap is null"))
////                    return
////                }
////
////                it.resume(bitmap)
////            }
////        }
////
////        this.into(target)
////    }
////}
////
////
////suspend fun loadImage(file: File): Bitmap {
////    return Picasso.get().load(file).intoBitmap()
////}
////
////suspend fun loadImage(uri: Uri): Bitmap {
////    return Picasso.get().load(uri).intoBitmap()
////}
