package com.adzenglish.adzenglish.utils

data class DataStatus<T>(
    val status: Status,
    val data: T? = null,
    val message: String? = null
) {
    enum class Status {
        SUCCESS, ERROR
    }

    companion object {
        fun <T> success(data: T): DataStatus<T> {
            return DataStatus(Status.SUCCESS, data)
        }

        fun <T> error(error: String): DataStatus<T> {
            return DataStatus(Status.ERROR, message = error)
        }
    }
}
