package com.ingenico.demoacclib.feature.demo_pay

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import com.ingenico.acc.*
import com.ingenico.acc.Currency
import com.ingenico.acc.workflow.*
import com.ingenico.acc.workflow.model.GetCardModel
import com.ingenico.acc.workflow.model.GoOnChipModel
import com.ingenico.acc.workflow.model.OnlinePinModel
import com.ingenico.demoacclib.data.model.CardDataModel
import com.ingenico.demoacclib.data.model.TransactionModel
import com.ingenico.demoacclib.databinding.FragmentDemoPayBinding
import com.ingenico.ingp.emv.ActionAnalysisResult
import com.ingenico.ingp.emv.DetectionInterface
import com.ingenico.ingp.emv.KernelId
import com.ingenico.ingp.emv.kernel.ServiceType
import com.ingenico.ingp.types.Amount
import com.ingenico.ingp.types.ber.TagValue
import com.ingenico.ingp.types.emv.TransactionType
import java.text.SimpleDateFormat
import java.util.*

class DemoPayFragment : Fragment(), IStepGetCard, ISecurePayment, IStepGoOnChip, IStepOnlinePin {
    private lateinit var binding: FragmentDemoPayBinding
    private lateinit var viewModel: DemoPayViewModel

    private val stepGetCard: StepGetCard = StepGetCard(
        SecurePayment, this
    )

    private val stepGoOnChip: StepGoOnChip = StepGoOnChip(
        SecurePayment, this
    )

    private lateinit var getCardModel: GetCardModel
    private lateinit var cardDataModel: CardDataModel
    var valueAmount = 0L
    var startTransaction = ObservableBoolean(false)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDemoPayBinding.inflate(inflater, container, false)
        viewModel = DemoPayViewModel()
        binding.viewModel = viewModel
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        SecurePayment.setInterface(this)
        binding.editTextValue.hint = "00"
        configCardModel()

        binding.buttonNewProcess.setOnClickListener {
            val intent = Intent(view.context, KeyDukptActivity::class.java)
            startActivity(intent)
        }

        binding.buttonStartTransaction.setOnClickListener {
            configAmount()
            if (valueAmount > 0)
                startEmvTransaction()
            else
                Toast.makeText(requireContext(), "El valor debe ser mayor a 0", Toast.LENGTH_LONG)
                    .show()

            hideKeyboard()
        }

        startTransaction.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (startTransaction.get()) binding.buttonStartTransaction.visibility = View.GONE
                else
                    binding.buttonStartTransaction.visibility = View.VISIBLE
                binding.textPin.visibility = View.GONE
            }

        })
    }

    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = requireActivity().currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(requireActivity())
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun configAmount() {
        valueAmount = if (binding.editTextValue.text.toString().isEmpty())
            0L else binding.editTextValue.text.toString().toLong()
        getCardModel.amount = Amount(
            value = valueAmount,  //Collection value
            decimalShift = 0, //number of decimal places allowed
            currencyCode = 604 //country currency code
        )
    }

    private fun configCardModel() {
        getCardModel = GetCardModel(
            supportSwipe = true,
            supportChip = true,
            supportCless = true,
            timeoutSearchCard = 40,
            serviceType = ServiceType.PURCHASE, //Service type
            amount = Amount(
                value = valueAmount,  //Collection value
                decimalShift = 0, //number of decimal places allowed
                currencyCode = 604 //country currency code
            ),
            otherAmount = Amount(0),
            transactionType = TransactionType.PURCHASE, //Transaction type
            date = SimpleDateFormat(
                "yyMMdd",
                Locale.getDefault()
            ).format(Calendar.getInstance().time),
            time = SimpleDateFormat("HHmmss", Locale.getDefault())
                .format(Calendar.getInstance().time),
            sequenceCounter = 1
        )
    }

    override fun onDestroy() {
        try {
            stepGetCard.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    private fun startEmvTransaction() {
        viewModel.getEmvInitialized(requireContext()).observe(viewLifecycleOwner) { emvConfig ->
            binding.textTitle.visibility = View.VISIBLE
            binding.textTitle.text = "Insert or TAP your card"
            startTransaction.set(true)
            stepGetCard.execute(
                emvConfig, getCardModel
            )
        }
    }

    //@Don't exit in new version Acc-library
    /* override fun onCardReadingResult(
         result: Int,
         cardDetectedOn: DetectionInterface,
         aid: String,
         kernelId: KernelId?
     ) {
         viewModel.transactionModel.let {
             stepGetCard.executeContinueEmv(
                 getCardModel,
                 cardDetectedOn,
                 kernelId
             )
         }
     }*/

    override fun onSetSpecificEmvParams(
        aid: String,
        kernelId: KernelId?,
        entryMode: DetectionInterface
    ): List<TagValue> {
        return EmvParamsConfig(requireContext()).getBySpecificAid(
            aid,
            kernelId,
            Currency.LOCAL,
            entryMode
        )
    }

    override fun onShowMenuAppSelection(
        appArrayList: Array<String?>,
        menuResult: IStepGetCard.MenuResult
    ) {
        if (appArrayList.size == 1)
            menuResult.setResult(0)
    }

    override fun onShowProcessing() {}

    override fun onEmvFlow(result: GetCardEmvErrors, emvFlowResult: IStepGetCard.EmvFlowResult) {
        when (result) {
            GetCardEmvErrors.EMV_SYSTEM_ERROR,
            GetCardEmvErrors.EMV_DOUBLE_CARD_IN_RANGE,
            GetCardEmvErrors.EMV_SHOW_CARD_AGAIN,
            GetCardEmvErrors.EMV_MISSING_TAG,
            GetCardEmvErrors.EMV_EXCEED_CTLMT,
            GetCardEmvErrors.EMV_NO_APP,
            GetCardEmvErrors.EMV_BLOCKED_APP,
            GetCardEmvErrors.EMV_POWERUP_FAIL,
            GetCardEmvErrors.EMV_KERNEL_ERROR,
            GetCardEmvErrors.EMV_ACTIVATE_FAIL,
            GetCardEmvErrors.EMV_SEE_PHONE_INSTRUCTIONS,
            GetCardEmvErrors.EMV_CARD_NOT_SUPPORTED,
            GetCardEmvErrors.EMV_CLESS_NO_KERNEL -> {
                Log.e("Error EMV", result.name)
                requireActivity().runOnUiThread {
                    binding.textTitle.text = result.name
                    startTransaction.set(false)
                }
            }
            GetCardEmvErrors.EMV_CLESS_TAP_CARD_AGAIN -> {
                Log.e("Error EMV", "Try Emv")
                requireActivity().runOnUiThread {
                    binding.textTitle.text = "TAP CARD AGAIN"
                }
                emvFlowResult.setResult(FlowResult.EMV_CONTINUE)
            }
            GetCardEmvErrors.EMV_REMOVE_CARD -> {
                Log.e("Error EMV", "Remove card")
                requireActivity().runOnUiThread {
                    binding.textTitle.text = "REMOVE CARD"
                }
                emvFlowResult.setResult(FlowResult.EMV_CONTINUE)
            }
        }
    }

    override fun onOutputResult(result: Int, getCardResult: GetCardResult?) {
        when (result) {
            ResultCode.SPA_CANCEL -> {
                Log.e("SPA_CANCEL_GetCardResult", "SPA_CANCEL.eMv")
                requireActivity().runOnUiThread {
                    requireActivity().runOnUiThread {
                        binding.textTitle.text = "Transacción cancelada."
                        startTransaction.set(false)
                        //Next process use Transaction Model
                    }
                }
            }
            ResultCode.SPA_TIMEOUT -> {
                Log.e("SPA_TIMEOUT EMV", "SPA_TIMEOUT.eMv")
            }
            ResultCode.SPA_RETRY_GET_CARD -> {
                Log.e("SPA_RETRY_GET_CARD EMV", "SPA_RETRY_GET_CARD.eMv")
                requireActivity().runOnUiThread {
                    viewModel.cardReadRetries++
                }
            }
            ResultCode.SPA_ERROR -> {
                Log.e("GetCardResult_ERROR", "SPA_ERROR.eMv")
                return
            }
            ResultCode.SPA_OK -> {
                Log.e("SPA_OK EMV", "SPA_OK.eMv")
                requireActivity().runOnUiThread {
                    assert(getCardResult != null)

                    if (getCardResult?.entryMode == Transaction.MODE_CTLS)
                        UsdkManager.getBeeper()?.startBeep(100)

                    viewModel.checkGetCardResult(getCardResult).observe(this) {
                        when {
                            it.invalidTrack == true -> {
                                Log.e("card_read_error_Invatrack", "try again")
                                requireActivity().runOnUiThread {
                                    binding.textTitle.text = "try again Invalid track"
                                    reInitProcess()
                                }
                            }
                            it.serviceCode != null -> {
                                //Change Support card to only Chip
                                getCardModel.supportChip = true
                                getCardModel.supportSwipe = false
                                getCardModel.supportCless = false

                                requireActivity().runOnUiThread {
                                    binding.textTitle.text = "Insert your card"
                                }

                                reInitProcess()

                                Log.e("card_read_error", "try again Insert")
                            }
                            it.onSuccess != null -> {
                                cardDataModel = it.onSuccess!!
                                val timer = if (cardDataModel.entryMode == Transaction.MODE_CHIP)
                                    2000
                                else 0L

                                Handler(Looper.getMainLooper()).postDelayed({
                                    if (cardDataModel.entryMode == Transaction.MODE_MAG) { //If the card is band
                                        //Optional: always request pin for band
                                        startPinOnline()
                                        //startTransaction()
                                    } else {
                                        //Config Entity Transaction
                                        viewModel.transactionModel.totalAmount =
                                            getCardModel.amount.value

                                        viewModel.setGetCardDataInfo(cardDataModel)
                                            .observe(this) {
                                                if (getCardModel.transactionType == TransactionType.PURCHASE)
                                                    startGoOnChip()
                                            }
                                    }
                                }, timer)
                            }

                        }
                    }


                }
            }
        }

    }

    private fun startPinOnline() {
        Log.e("startPinOnline", "startPinOnline")

        Log.d("DemoPayViewModel", "DUKPT PIN : " + Constant.getKeyPrefix() + Constant.getKeyDukptPin())
        Toast.makeText(context, "DUKPT PIN : " + Constant.getKeyPrefix() + " " + Constant.getKeyDukptPin(), Toast.LENGTH_LONG).show()
        StepOnlinePin(SecurePayment, this).execute(
            OnlinePinModel(
                //"0043 04",
                Constant.getKeyPrefix() + " " + Constant.getKeyDukptPin(),
                EnumKeyAlgorithm.DUKPT,
                cardDataModel.pan,
                50
            )
        )
    }

    private fun startGoOnChip() {
        //Log.d("LUCIFER", "REQUEST PIN")
        Log.d("DemoPayViewModel", "DUKPT PIN : " + Constant.getKeyPrefix() + Constant.getKeyDukptPin())
        Toast.makeText(context, "DUKPT PIN : " + Constant.getKeyPrefix() + " " + Constant.getKeyDukptPin(), Toast.LENGTH_LONG).show()
        stepGoOnChip.execute(
            GoOnChipModel(
                //pinKeyId = "0043 04",
                Constant.getKeyPrefix() + " " + Constant.getKeyDukptPin(),
                pinAlgorithm = EnumKeyAlgorithm.DUKPT,
                pinTimeout = 40,
                tagList = mutableListOf(
                    "4F", "82", "84", "8E", "95", "9A", "9B", "9C",
                    "9E", "5F24", "5F28", "5F34", "5F2A", "9F02",
                    "9F03", "9F06", "9F07", "9F0D", "9F0E", "9F0F",
                    "9F10", "9F17", "9F12", "9F1A", "9F1E", "9F26",
                    "9F27", "9F33", "9F34", "9F35", "9F36", "9F37",
                    "9F40", "9F53", "9F66", "9F2D", "9F2E", "9F2F", "9F17", "99"
                )
            )
        )
    }

    override fun onPinKeyboardEvent(
        pinEvent: EnumPinKeyEvent,
        pinResult: ISecurePayment.PinResult
    ) {
        requireActivity().runOnUiThread {
            val stringBuilder = StringBuilder()
            when (pinEvent) {
                EnumPinKeyEvent.SHOW_KEYBOARD -> {
                    binding.textPin.visibility = View.VISIBLE
                    binding.textPin.text = "Entry pin: "
                }
                EnumPinKeyEvent.NUMERIC_KEY -> {
                    stringBuilder.append(binding.textPin.text)
                    stringBuilder.append('\u25CF')
                    binding.textPin.text = stringBuilder.toString()
                }
                EnumPinKeyEvent.KEY_CLEAR -> {
                    stringBuilder.append(binding.textPin.text)
                    if (stringBuilder.isNotEmpty() && stringBuilder.indexOf('\u25CF') != -1) {
                        stringBuilder.deleteCharAt(stringBuilder.length - 1)
                        binding.textPin.text = stringBuilder.toString()
                    }
                }
                EnumPinKeyEvent.OFFLINE_PIN_LAST_TRY -> {
                    Log.e("PIN", "OFFLINE_PIN_LAST_TRY")
                }
                EnumPinKeyEvent.OFFLINE_PIN_HAS_BEEN_BLOCKED -> {
                    Log.e("PIN", "PIN_BLOCKED")
                }
                EnumPinKeyEvent.OFFLINE_PIN_WRONG -> {
                    Log.e("PIN", "OFFLINE_PIN_WRONG")
                }
                else -> {
                    Log.e("PIN", "N/A")
                }
            }
        }
    }

    override fun onOutputResult(result: Int, goOnChipResult: GoOnChipResult?) {
        //Log.d("LUCIFER", "RESULT : " + result)
        when (result) {
            ResultCode.SPA_CANCEL -> {
                Log.e("SPA_CANCEL_GoOnChipResult", "SPA_CANCEL.eMv")
                stepGetCard.stop()
                requireActivity().runOnUiThread {
                    requireActivity().runOnUiThread {
                        binding.textTitle.text = "Transacción cancelada"
                        startTransaction.set(false)
                        //Next process use Transaction Model
                    }
                }
                //Close process and view
            }
            ResultCode.SPA_TIMEOUT -> {
                Log.e("SPA_TIMEOUT EMV", "SPA_TIMEOUT.eMv")
            }
            ResultCode.SPA_ERROR -> {
                Log.e("GoOnChipResult_ERROR", "ERROR_PROCESSING_CARD")
                //reinitProcess()
            }
            else -> {
                requireActivity().runOnUiThread {
                    when (goOnChipResult!!.desicion) {
                        ActionAnalysisResult.ARQC -> {
                            requireActivity().runOnUiThread {
                                viewModel.setGoOnChipDataInfo(goOnChipResult)
                                    .observe(viewLifecycleOwner) { transactionModel ->
                                        startTransaction(transactionModel)
                                    }
                            }
                        }
                        ActionAnalysisResult.AAC -> {
                            Log.e("ERROR", "DECLINED CARD")
                        }
                        else -> {
                            Log.e("ERROR", "CANCEL")
                            //Close process and view
                        }
                    }
                }
            }
        }
    }

    override fun onOutputResult(result: Int, onlinePinResult: OnlinePinResult?) {
        when (result) {
            ResultCode.SPA_OK -> {
                requireActivity().runOnUiThread {
                    startTransaction()
                }
            }
            ResultCode.SPA_CANCEL -> {
                stepGetCard.stop()
            }
            ResultCode.SPA_TIMEOUT -> {
                Log.e("SPA_TIMEOUT EMV", "SPA_TIMEOUT.eMv")
                requireActivity().runOnUiThread {
                    binding.textTitle.text = "TIME OUT"
                }
            }
            ResultCode.SPA_RETRY_GET_CARD -> {
                Log.e("SPA_RETRY_GET_CARD EMV", "SPA_RETRY_GET_CARD.eMv")
                requireActivity().runOnUiThread {
                    viewModel.cardReadRetries++
                }
            }
            ResultCode.SPA_ERROR -> {
                Log.e("SPA_ERROR EMV", "SPA_ERROR.eMv")
            }
        }

    }

    private fun startTransaction(transaction: TransactionModel) {
        SecurePayment.setInterface(null)
        stepGetCard.stop()

        requireActivity().runOnUiThread {
            requireActivity().runOnUiThread {
                binding.textTitle.text = "Obtención de datos finalizado"
                startTransaction.set(false)
                //Next process use Transaction Model
            }
        }
    }

    private fun startTransaction() {
        requireActivity().runOnUiThread {
            SecurePayment.setInterface(null)
            stepGetCard.stop()

            binding.textTitle.text = "Obtención de datos finalizado"
            startTransaction.set(false)
            //Next process - Use cardDataModel
        }
    }

    private fun reInitProcess() {
        requireActivity().runOnUiThread {
            stepGetCard.stop()
            startEmvTransaction()
        }
    }
}