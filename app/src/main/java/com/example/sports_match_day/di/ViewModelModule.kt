package com.example.sports_match_day.di

import com.example.sports_match_day.ui.LoginViewModel
import com.example.sports_match_day.ui.MainActivityViewModel
import com.example.sports_match_day.ui.athletes.AthletesViewModel
import com.example.sports_match_day.ui.athletes.add.AthletesAddViewModel
import com.example.sports_match_day.ui.home.HomeViewModel
import com.example.sports_match_day.ui.home.manage.MatchesManageViewModel
import com.example.sports_match_day.ui.sports.SportsViewModel
import com.example.sports_match_day.ui.sports.manage.SportsManageViewModel
import com.example.sports_match_day.ui.squads.SquadsViewModel
import com.example.sports_match_day.ui.squads.add.SquadsAddViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by Kristo on 07-Mar-21
 */
val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { MainActivityViewModel() }

    viewModel { HomeViewModel(get()) }
    viewModel { MatchesManageViewModel(get()) }

    viewModel { AthletesViewModel(get()) }
    viewModel { AthletesAddViewModel(get()) }

    viewModel { SquadsViewModel(get()) }
    viewModel { SquadsAddViewModel(get()) }

    viewModel { SportsViewModel(get()) }
    viewModel { SportsManageViewModel(get()) }
}