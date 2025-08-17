package com.example.mustafakocer.data.network.util

/**
 * A method-level annotation to mark Retrofit API calls that require
 * an Authorization header to be automatically added by an interceptor.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Authenticated