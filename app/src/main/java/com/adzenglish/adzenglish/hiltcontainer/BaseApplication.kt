package com.adzenglish.adzenglish.hiltcontainer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.migration.CustomInject

@HiltAndroidApp
class BaseApplication: Application(){}