package stoyck.vitrina.util

import java.io.File

object FileUtil {
    fun copy(source: File, destination: File) {
        source.inputStream().use { sourceStream ->
            destination.outputStream().use { destinationStream ->
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