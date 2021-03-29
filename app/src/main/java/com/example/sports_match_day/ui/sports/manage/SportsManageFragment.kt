package com.example.sports_match_day.ui.sports.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.sports_match_day.R
import com.example.sports_match_day.databinding.FragmentAddSportBinding
import com.example.sports_match_day.model.Gender
import com.example.sports_match_day.model.SportType
import com.example.sports_match_day.ui.MainActivity
import com.example.sports_match_day.ui.OnTouchListener
import com.example.sports_match_day.ui.base.BaseFragment
import com.example.sports_match_day.ui.sports.SportsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by Kristo on 15-Mar-21
 */
class SportsManageFragment : BaseFragment() {
    private val args: SportsManageFragmentArgs by navArgs()
    private val viewModel: SportsManageViewModel by viewModel()

    private var _binding: FragmentAddSportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddSportBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupSaveButton()
        setupNameEditText()
        setupParticipantCountEditText()
        setupHelpButton()
        setUpForEdit()
    }

    private fun setUpForEdit() {
        if(args.sportId > -1) {
            viewModel.loadSport(args.sportId)
            binding.toggleGender.isEnabled = false
            binding.toggleGender.alpha = 0.5f
            binding.toggleType.isEnabled = false
            binding.toggleType.alpha = 0.5f
            binding.editTextParticipantsCount.isEnabled = false
        }
    }

    private fun setupObservers() {
        viewModel.sport.observe(viewLifecycleOwner, {

            binding.editTextName.setText(
                it.name,
                TextView.BufferType.EDITABLE
            )
            binding.toggleType.isChecked = it.type == SportType.SOLO
            binding.toggleGender.isChecked = it.gender == Gender.MALE

            binding.editTextParticipantsCount.setText(
                "${it.participantCount}",
                TextView.BufferType.EDITABLE
            )
        })

        viewModel.isDataLoading.observe(viewLifecycleOwner, {
            binding.progressLoading.isVisible = it
        })

        viewModel.saveSuccessful.observe(viewLifecycleOwner, {
            if(it) {
                val navController =
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                //This will trigger the SportsFragment to refresh.
                navController.previousBackStackEntry?.savedStateHandle?.set(SportsFragment.SPORTS_REFRESH_KEY, true)
                navController.navigateUp()
            }
        })

        viewModel.apiErrorMessage.observe(viewLifecycleOwner, {
            showErrorPopup(it)
        })
    }

    private fun setupParticipantCountEditText(){
        binding.editTextParticipantsCount.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val country = binding.editTextParticipantsCount.editableText.toString()
                if (country.isBlank()) {
                    binding.editTextParticipantsCount.error =  getString(R.string.error_blank)
                } else {
                    binding.editTextParticipantsCount.error = null
                }
            }
        }
    }

    private fun setupHelpButton(){
        with(binding) {
            //Gender
            buttonHelpGender.setOnClickListener {
                containerGenderHelp.visibility = View.VISIBLE
            }

            //Type
            buttonHelpType.setOnClickListener {
                containerTypeHelp.visibility = View.VISIBLE
            }

            //Count
            buttonHelpCount.setOnClickListener {
                containerParticipantsHelp.visibility = View.VISIBLE
            }
        }
    }
    private fun setupNameEditText() {
        binding.editTextName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val name = binding.editTextName.editableText.toString()
                if (name.isBlank()) {
                    binding.editTextName.error =  getString(R.string.error_blank)
                } else {
                    binding.editTextName.error = null
                }
            }
        }
    }

    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            val gender = binding.toggleGender.isChecked
            val type = binding.toggleType.isChecked

            val count = binding.editTextParticipantsCount.text.toString().trim()
            val name = binding.editTextName.text.toString().trim()

            if(validateData())
                if(viewModel.sport.value == null) {
                    viewModel.addSport(name, type, gender, count.toInt())
                }else{
                    viewModel.updateSport(args.sportId, name, type, gender, count.toInt())
                }
        }
    }

    private fun validateData(): Boolean{
        var pass = true

        val name = binding.editTextName.text.toString()

        if(binding.editTextName.error != null){
            pass = false
        }

        if(name.isBlank()){
            binding.editTextName.error = getString(R.string.error_blank)
            pass = false
        }

        val count = binding.editTextParticipantsCount.text.toString()

        if(binding.editTextParticipantsCount.error != null){
            pass = false
        }

        if(count.isBlank()){
            binding.editTextParticipantsCount.error = getString(R.string.error_blank)
            pass = false
        }
        return pass
    }

    override fun onResume() {
        super.onResume()

        (requireActivity() as MainActivity).onTouchListener = object: OnTouchListener{
            override fun onTouch() {
                binding.containerTypeHelp.visibility = View.GONE
                binding.containerGenderHelp.visibility = View.GONE
                binding.containerParticipantsHelp.visibility = View.GONE
            }
        }
    }
    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).onTouchListener = null
    }
}