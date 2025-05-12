package com.example.moverconnect

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.progressindicator.LinearProgressIndicator

class CreateMoveRequestActivity : AppCompatActivity() {

    private lateinit var progressIndicator: LinearProgressIndicator
    private lateinit var stepIndicator: TextView
    private var currentStep = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_move_request)

        progressIndicator = findViewById(R.id.progressIndicator)
        stepIndicator = findViewById(R.id.stepIndicator)

        setupToolbar()
        showStep(1)
    }

    private fun setupToolbar() {
        findViewById<View>(R.id.toolbar).setOnClickListener {
            finish()
        }
    }

    private fun showStep(step: Int) {
        currentStep = step
        val progress = (step - 1) * 50 // 0, 50, or 100
        progressIndicator.progress = progress
        stepIndicator.text = "Step $step of 3"

        val fragment = when (step) {
            1 -> MoveRequestStep1Fragment()
            2 -> MoveRequestStep2Fragment()
            3 -> MoveRequestStep3Fragment()
            else -> throw IllegalArgumentException("Invalid step: $step")
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    fun goToNextStep() {
        if (currentStep < 3) {
            showStep(currentStep + 1)
        }
    }

    fun goToPreviousStep() {
        if (currentStep > 1) {
            showStep(currentStep - 1)
        }
    }

    fun finishMoveRequest() {
        // TODO: Save move request data
        setResult(RESULT_OK)
        finish()
    }
}

abstract class MoveRequestStepFragment : Fragment() {
    protected val activity: CreateMoveRequestActivity
        get() = getActivity() as CreateMoveRequestActivity
}

class MoveRequestStep1Fragment : MoveRequestStepFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.nextButton).setOnClickListener {
            // TODO: Validate inputs
            activity.goToNextStep()
        }
    }
}

class MoveRequestStep2Fragment : MoveRequestStepFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.backButton).setOnClickListener {
            activity.goToPreviousStep()
        }

        view.findViewById<View>(R.id.nextButton).setOnClickListener {
            // TODO: Validate inputs
            activity.goToNextStep()
        }
    }
}

class MoveRequestStep3Fragment : MoveRequestStepFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.backButton).setOnClickListener {
            activity.goToPreviousStep()
        }

        view.findViewById<View>(R.id.createButton).setOnClickListener {
            // TODO: Validate inputs
            activity.finishMoveRequest()
        }
    }
} 