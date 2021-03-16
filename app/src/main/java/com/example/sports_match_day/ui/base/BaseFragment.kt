package com.example.sports_match_day.ui.base

import androidx.fragment.app.Fragment
import com.example.sports_match_day.utils.PopupManager

/**
 * Created by Kristo on 16-Mar-21
 */
abstract class BaseFragment : Fragment() {

    fun showErrorPopup(exception: Throwable) {
        PopupManager.simplePopupMessage(
            requireActivity(),
            ExceptionUtils.getApiErrorMessage(requireActivity(), exception)
        )
    }
}