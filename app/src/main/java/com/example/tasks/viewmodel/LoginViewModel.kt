package com.example.tasks.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tasks.R
import com.example.tasks.service.model.HeaderModel
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.helper.FingerprintHelper
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.listener.ValidationListener
import com.example.tasks.service.repository.PersonRepository
import com.example.tasks.service.repository.PriorityRepository
import com.example.tasks.service.repository.local.SecurityPreferences
import com.example.tasks.service.repository.remote.RetrofitClient

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext: Context = application
    private val mPersonRepository = PersonRepository(application)
    private val mPriorityRepository = PriorityRepository(application)
    private val mSharedPreferences = SecurityPreferences(application)

    private val mLogin = MutableLiveData<ValidationListener>()
    var login: LiveData<ValidationListener> = mLogin

    private val mFigerPrint = MutableLiveData<Boolean>()
    var fingerprint: LiveData<Boolean> = mFigerPrint

    /**
     * Faz login usando API
     */
    fun doLogin(email: String, password: String) {

        if(email.isBlank())
        {
            mLogin.value = ValidationListener(mContext.getString(R.string.enter_email))
            return
        }

        if(password.isBlank())
        {
            mLogin.value = ValidationListener(mContext.getString(R.string.enter_password))
            return
        }

        mPersonRepository.login(email, password, object : APIListener<HeaderModel> {
            override fun onSuccess(model: HeaderModel) {

                mSharedPreferences.store(TaskConstants.SHARED.TOKEN_KEY, model.token)
                mSharedPreferences.store(TaskConstants.SHARED.PERSON_KEY, model.personKey)
                mSharedPreferences.store(TaskConstants.SHARED.PERSON_NAME, model.name)

                RetrofitClient.addHeader(model.token, model.personKey)

                mLogin.value = ValidationListener()
            }

            override fun onFailure(str: String) {
                mLogin.value = ValidationListener(str)
            }
        })
    }

    /**
     * Verifica se usuário está logado
     */
    fun verifyLoggedUser() {

        val token = mSharedPreferences.get(TaskConstants.SHARED.TOKEN_KEY)
        val person = mSharedPreferences.get(TaskConstants.SHARED.PERSON_KEY)

        RetrofitClient.addHeader(token, person)

        val logged = (token.isNotBlank() && person.isNotBlank())

        if(!logged) {
            mPriorityRepository.all()
        }

        mFigerPrint.value = logged
    }

    fun isAuthenticationAvaliable() {

        val token = mSharedPreferences.get(TaskConstants.SHARED.TOKEN_KEY)
        val person = mSharedPreferences.get(TaskConstants.SHARED.PERSON_KEY)

        RetrofitClient.addHeader(token, person)

        val everLogged = (token.isNotBlank() && person.isNotBlank())

        if(!everLogged) {
            mPriorityRepository.all()
        }

        if(FingerprintHelper.isAuthenticationAvaliable(mContext)) {
            mFigerPrint.value = everLogged
        }
    }
}