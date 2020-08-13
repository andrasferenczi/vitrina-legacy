package stoyck.vitrina.util

import java.io.File
import java.io.OutputStream

object FileUtil {
    fun copy(source: File, destination: File) {
        return copy(source, destination.outputStream())
    }

    fun copy(source: File, outputStream: OutputStream) {
        // output stream is already open, so it comes first
        outputStream.use { destinationStream ->
            source.inputStream().use { sourceStream ->
                val buffer = ByteArray(1024)

                while (true) {
                    val read = sourceStream.read(buffer)

                    if (read <= 0) {
                        break
                    }
                    destinationStream.write(buffer, 0, read)
                }
            }
        }
    }
}