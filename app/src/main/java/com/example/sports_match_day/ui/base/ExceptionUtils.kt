package com.example.sports_match_day.ui.base

import android.content.Context
import com.example.sports_match_day.BuildConfig
import com.example.sports_match_day.R

/**
 * Created by Kristo on 16-Mar-21
 */

class WrongValue(code: Int) : BaseApiException(code)

abstract class BaseApiException(
    val code: Int
) : Exception()


object ExceptionUtils {
    fun getApiErrorMessage(context: Context, exception: Throwable): String {
        var error = when (exception) {
            is WrongValue -> context.getString(R.string.wrong_value) + "(${exception.code}): "
            else -> {
                context.getString(R.string.something_went_wrong)
            }
        }

        if(BuildConfig.DEBUG)
            error += exception.message
        return error
    }
}