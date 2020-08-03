package stoyck.vitrina.util

/**
 * Similar to kotlin result, but this one can be used as a return value
 */
class VResult<T> private constructor(
    //
    private val value: Any
) {

    val isSuccess
        get() = value !is Failure

    val isFailure
        get() = value is Failure

    fun getOrNull(): T? =
        when {
            isFailure -> null
            else -> value as T
        }

    fun exceptionOrNull(): Throwable? =
        when (value) {
            is Failure -> value.exception
            else -> null
        }

    private class Failure(val exception: Throwable)

    companion object {

        fun <T : Any> success(value: T) = VResult<T>(value)

        fun <T : Any> failure(exception: Throwable) = VResult<T>(Failure(exception))

    }

}