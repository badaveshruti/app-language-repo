package qnopy.com.qnopyandroid.flowWithAdmin.ui.signIn

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.*
import dagger.hilt.android.AndroidEntryPoint
import qnopy.com.qnopyandroid.R
import qnopy.com.qnopyandroid.customView.CustomButton
import qnopy.com.qnopyandroid.databinding.ActivitySignInBinding
import qnopy.com.qnopyandroid.databinding.AlertWelcomeUserBinding
import qnopy.com.qnopyandroid.databinding.BottomsheetPhoneNoBinding
import qnopy.com.qnopyandroid.databinding.BottomsheetVerifyOtpBinding
import qnopy.com.qnopyandroid.flowWithAdmin.ui.signIn.model.GenerateTokenResponse
import qnopy.com.qnopyandroid.flowWithAdmin.ui.signIn.model.SignInRequest
import qnopy.com.qnopyandroid.flowWithAdmin.ui.signIn.model.SignInResponse
import qnopy.com.qnopyandroid.flowWithAdmin.utility.ApiState
import qnopy.com.qnopyandroid.flowWithAdmin.utility.Log
import qnopy.com.qnopyandroid.flowWithAdmin.utility.Utils
import qnopy.com.qnopyandroid.network.CheckNetwork
import qnopy.com.qnopyandroid.uiutils.ProgressDialogActivity
import qnopy.com.qnopyandroid.util.Util
import qnopy.com.qnopyandroid.util.VectorDrawableUtils
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class SignInActivity : ProgressDialogActivity() {

    private lateinit var bottomSheetGenerateOtp: BottomSheetDialog
    private lateinit var bottomSheetVerifyOtp: BottomSheetDialog
    private var isResendCode: Boolean = false
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var storedVerificationId: String
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var cellWithCountryCode: String
    private var signInReq: SignInRequest? = null
    private lateinit var binding: ActivitySignInBinding
    private val viewModel: SignInViewModel by viewModels()

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, SignInActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val title = "Sign up for an account"
        Utils.setToolbarTitleAndBackBtn(title, true, this)

        addObserver()
        setUpUi()
    }

    private fun addObserver() {
        val job = lifecycleScope.launchWhenStarted {
            with(viewModel) {
                signInState.collect {
                    when (it) {
                        is ApiState.Loading -> {
                        }
                        is ApiState.Success -> {
                            cancelAlertProgress()
                            val response = it.response as SignInResponse

                            if (response.success) {
                                welcomeAlert()
                            } else {
                                Toast.makeText(
                                    this@SignInActivity,
                                    response.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is ApiState.Failure -> {
                            cancelAlertProgress()
                        }
                        is ApiState.Empty -> {

                        }
                        else -> {
                            cancelAlertProgress()
                        }
                    }
                }
            }
        }

        val jobToken = lifecycleScope.launchWhenStarted {
            with(viewModel) {
                getTokenState.collect {
                    when (it) {
                        is ApiState.Loading -> {
                            showAlertProgress(getString(R.string.please_wait))
                        }
                        is ApiState.Success -> {
                            val response = it.response as GenerateTokenResponse

                            if (response.success) {
                                if (signInReq != null
                                    && response.data != null
                                )
                                    viewModel.signIn(response.data, signInReq!!)
                            } else {
                                cancelAlertProgress()
                                Toast.makeText(
                                    this@SignInActivity,
                                    response.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is ApiState.Failure -> {
                            cancelAlertProgress()
                        }
                        is ApiState.Empty -> {

                        }
                    }
                }
            }
        }
    }

    private fun setUpUi() {
        binding.btnSubmit.setOnClickListener {
            Util.hideKeyboard(this)
            if (CheckNetwork.isInternetAvailable(this)) {
                val signInRequest =
                    SignInRequest(
                        firstName = binding.tiFirstName.editText?.text.toString(),
                        lastName = binding.tiLastName.editText?.text.toString(),
                        mobileNumber = cellWithCountryCode,
                        password = binding.tiPassword.editText?.text.toString(),
                        primaryEmail = binding.tiEmail.editText?.text.toString()
                    )

                validateSingInReq(signInRequest)
            } else {
                Toast.makeText(
                    this, R.string.please_check_internet_connection,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //upon otp verification user will be able to add sign in details with prefilled verified contact
        showOTPAuthBottomSheet()
    }

    private fun validateSingInReq(signInRequest: SignInRequest) {
        if (signInRequest.firstName.isEmpty())
            Toast.makeText(this, "Enter first name", Toast.LENGTH_SHORT).show()
        else if (signInRequest.lastName.isEmpty())
            Toast.makeText(this, "Enter last name", Toast.LENGTH_SHORT).show()
        else if (signInRequest.mobileNumber.isEmpty())
            Toast.makeText(this, "Enter mobile number", Toast.LENGTH_SHORT).show()
/*        else if (binding.tiContact.editText?.text?.length != 10)
            Toast.makeText(this, "Enter valid mobile number", Toast.LENGTH_SHORT).show()*/
        else if (!Utils.isValidEmail(signInRequest.primaryEmail))
            Toast.makeText(this, "Enter valid email", Toast.LENGTH_SHORT).show()
        else if (signInRequest.password.isEmpty())
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
        else {
            signInReq = signInRequest
            viewModel.genToken()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun welcomeAlert() {
        val builder = AlertDialog.Builder(this, R.style.WrapContentDialog)

        val welcomeUserBinding = AlertWelcomeUserBinding.inflate(LayoutInflater.from(this))
        builder.setView(welcomeUserBinding.root)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        welcomeUserBinding.btnGoToLogin.setOnClickListener {
            dialog.cancel()
            finish()
        }
    }

    private fun showOTPAuthBottomSheet() {
        try {

            val sheetBinding = BottomsheetPhoneNoBinding.inflate(LayoutInflater.from(this))
            bottomSheetGenerateOtp = BottomSheetDialog(this)
            bottomSheetGenerateOtp.setContentView(sheetBinding.root)
            bottomSheetGenerateOtp.setCancelable(false)
            bottomSheetGenerateOtp.show()

            // Remove default white color background
            val bottomSheetDialog: FrameLayout? =
                bottomSheetGenerateOtp.findViewById(R.id.design_bottom_sheet)
            bottomSheetDialog?.background = null

            sheetBinding.llGenerateOTP.setOnClickListener {
                if (sheetBinding.tiContact.editText?.text.toString().isEmpty()) {
                    Toast.makeText(this, "Enter cell phone number", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else if (sheetBinding.tiContact.editText?.text?.length != 10) {
                    Toast.makeText(this, "Enter valid cell phone number", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                cellWithCountryCode =
                    sheetBinding.codePicker.selectedCountryCodeWithPlus +
                            sheetBinding.tiContact.editText?.text.toString()

                sendVerificationCode(null)
            }

            sheetBinding.llCancel.setOnClickListener {
                bottomSheetGenerateOtp.cancel()
                finish()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendVerificationCode(
        verifySheetBinding: BottomsheetVerifyOtpBinding?
    ) {

        if (!CheckNetwork.isInternetAvailable(this, true))
            return

        showAlertProgress(getString(R.string.please_wait))

        val authCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                cancelAlertProgress()

                //added this delay in case onCodeSent() and onVerificationCompleted() worked simultaneously
                //otherwise if otp alert is shown after verification done than the popup doesn't cancel as it is not initialised yet
                //and so leading to a blocker for signup
                Handler(Looper.getMainLooper()).postDelayed({
                    signInWithPhoneAuthCredential(credential)
                }, 1000)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)
                cancelAlertProgress()

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")
                cancelAlertProgress()

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                if (this@SignInActivity::bottomSheetGenerateOtp.isInitialized) {
                    bottomSheetGenerateOtp.cancel()
                    showVerifyOTPBottomSheet()
                }

                verifySheetBinding?.let { addCountDownTimer(it) }
            }
        }

        val options = PhoneAuthOptions.newBuilder()
            .setPhoneNumber(cellWithCountryCode)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(authCallbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun showVerifyOTPBottomSheet() {
        try {

            val sheetBinding = BottomsheetVerifyOtpBinding.inflate(LayoutInflater.from(this))
            bottomSheetVerifyOtp = BottomSheetDialog(this)
            bottomSheetVerifyOtp.setContentView(sheetBinding.root)
            bottomSheetVerifyOtp.setCancelable(false)
            bottomSheetVerifyOtp.show()

            // Remove default white color background
            val bottomSheetDialog: FrameLayout? =
                bottomSheetVerifyOtp.findViewById(R.id.design_bottom_sheet)
            bottomSheetDialog?.background = null

            val cellPhoneText =
                sheetBinding.tvVerifyInfo.text.toString() + "<b><font color='#007AFF'>" + cellWithCountryCode + "</font></b>"
            sheetBinding.tvVerifyInfo.text = Util.fromHtml(cellPhoneText)

            sheetBinding.tvResendOtp.setOnClickListener {
                if (isResendCode)
                    sendVerificationCode(sheetBinding)
            }

            addCountDownTimer(sheetBinding)

            addTextWatcherAndKeyEvent(sheetBinding)

            sheetBinding.llVerifyCode.setOnClickListener {
                val code = sheetBinding.edtCode1.text.toString()
                    .trim() + sheetBinding.edtCode2.text.toString()
                    .trim() + sheetBinding.edtCode3.text.toString()
                    .trim() + sheetBinding.edtCode4.text.toString()
                    .trim() + sheetBinding.edtCode5.text.toString()
                    .trim() + sheetBinding.edtCode6.text.toString().trim()

                if (code.length != 6) {
                    Toast.makeText(
                        this@SignInActivity,
                        "Please enter valid code..",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                } else {
                    val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
                    signInWithPhoneAuthCredential(
                        credential
                    )
                }
            }

            sheetBinding.llCancel.setOnClickListener {
                bottomSheetVerifyOtp.cancel()
                finish()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addCountDownTimer(sheetBinding: BottomsheetVerifyOtpBinding) {
        val SIXTY_SEC: Long = 60000
        val ONE_SEC: Long = 1000

        object : CountDownTimer(SIXTY_SEC, ONE_SEC) {
            override fun onTick(duration: Long) {
                isResendCode = false
                val Mmin = duration / 1000 / 60
                val Ssec = duration / 1000 % 60
                val time = "$Ssec" + "s"
                sheetBinding.tvResendOtp.text = "Resend code in $time"
            }

            override fun onFinish() {
                isResendCode = true
                val resendCode = "<b><font color='#007AFF'>Resend code</font></b>"
                sheetBinding.tvResendOtp.text = Util.fromHtml(resendCode)
            }
        }.start()
    }

    private fun addTextWatcherAndKeyEvent(sheetBinding: BottomsheetVerifyOtpBinding) {
        //add text watcher
        sheetBinding.edtCode1.addTextChangedListener(
            GenericTextWatcher(
                sheetBinding.edtCode1,
                sheetBinding.edtCode2
            )
        )
        sheetBinding.edtCode2.addTextChangedListener(
            GenericTextWatcher(
                sheetBinding.edtCode2,
                sheetBinding.edtCode3
            )
        )
        sheetBinding.edtCode3.addTextChangedListener(
            GenericTextWatcher(
                sheetBinding.edtCode3,
                sheetBinding.edtCode4
            )
        )
        sheetBinding.edtCode4.addTextChangedListener(
            GenericTextWatcher(
                sheetBinding.edtCode4,
                sheetBinding.edtCode5
            )
        )
        sheetBinding.edtCode5.addTextChangedListener(
            GenericTextWatcher(
                sheetBinding.edtCode5,
                sheetBinding.edtCode6
            )
        )
        sheetBinding.edtCode6.addTextChangedListener(
            GenericTextWatcher(
                sheetBinding.edtCode6,
                null
            )
        )
        //end: add text watcher

        //set key event
        sheetBinding.edtCode2.setOnKeyListener(
            GenericKeyEvent(
                sheetBinding.edtCode2,
                sheetBinding.edtCode1
            )
        )
        sheetBinding.edtCode3.setOnKeyListener(
            GenericKeyEvent(
                sheetBinding.edtCode3,
                sheetBinding.edtCode2
            )
        )
        sheetBinding.edtCode4.setOnKeyListener(
            GenericKeyEvent(
                sheetBinding.edtCode4,
                sheetBinding.edtCode3
            )
        )
        sheetBinding.edtCode5.setOnKeyListener(
            GenericKeyEvent(
                sheetBinding.edtCode5,
                sheetBinding.edtCode4
            )
        )
        sheetBinding.edtCode6.setOnKeyListener(
            GenericKeyEvent(
                sheetBinding.edtCode6,
                sheetBinding.edtCode5
            )
        )
        //end: set key event
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        showAlertProgress("Verifying cell phone number...")
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                cancelAlertProgress()
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user

                    if (this@SignInActivity::bottomSheetGenerateOtp.isInitialized
                        && bottomSheetGenerateOtp.isShowing
                    )
                        bottomSheetGenerateOtp.cancel()

                    if (this@SignInActivity::bottomSheetVerifyOtp.isInitialized
                        && bottomSheetVerifyOtp.isShowing
                    )
                        bottomSheetVerifyOtp.cancel()
                    prefillContactNo()
                } else {
                    // Sign in failed, display a message and update the UI
                    task.exception?.let { Log.w(TAG, "signInWithCredential:failure", it) }
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(
                            this@SignInActivity,
                            "Invalid verification code entered..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    // Update UI
                }
            }
    }

    private fun prefillContactNo() {
        Toast.makeText(this, "Verification successful", Toast.LENGTH_SHORT).show()
        binding.tiContact.editText?.setText(cellWithCountryCode)
        binding.tiContact.editText?.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            VectorDrawableUtils.getDrawable(
                this,
                qnopy.com.qnopyandroid.R.drawable.ic_circle_check_outlined,
                R.color.task_ongoing_green
            ), null
        )
        binding.tiContact.isEnabled = false
    }
}