package stoyck.vitrina.domain

class UserReadableException(
    val userReadableMessage: String
) : Exception(userReadableMessage)
