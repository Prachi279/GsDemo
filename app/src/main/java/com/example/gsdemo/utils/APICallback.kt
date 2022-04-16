package com.example.gsdemo.utils

/**
 * The APICallback interface
 */
interface APICallback {
    /**
     * The apiError method, to retrieve api error response
     */
    fun apiError(response: Any)
}