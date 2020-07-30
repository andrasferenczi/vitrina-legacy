package stoyck.vitrina.network.data

// Null-safety is necessary here, because gson creates null values
val RedditAbout.doesSubExist: Boolean
    get() = this.id.isNotBlank()
