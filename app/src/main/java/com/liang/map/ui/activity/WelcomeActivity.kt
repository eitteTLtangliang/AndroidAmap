package com.liang.map.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.liang.map.databinding.ActivityWelcomeBinding
import com.liang.map.ui.activity.base.BaseActivity
import com.liang.map.util.Constants
import com.liang.map.util.DataStoreUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>() {
    override fun getViewBinding(): ActivityWelcomeBinding {
        return ActivityWelcomeBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        if (!DataStoreUtil.readBooleanData(Constants.PRIVACY_AGREE)) {
            showPrivacyDialog{
                if (it){
                    startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
                }
            }
        } else {
            lifecycleScope.launch {
                delay(500)
                startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
            }
        }
    }
}