/*
 * Copyright (c) 2020 Nam Nguyen, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.attiq.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class TimeUpdateObserver(
  private val context: Context,
  private val doOnTimeUpdated: () -> Unit
) {

  private val timeUpdateIntentFilter: IntentFilter = IntentFilter().apply {
    addAction(Intent.ACTION_TIME_TICK)
    addAction(Intent.ACTION_TIME_CHANGED)
  }

  private var timeUpdateReceiver: TimeUpdateBroadcastReceiver? = null

  fun start() {
    unregisterBroadcastReceiver()
    registerBroadcastReceiver()
    doOnTimeUpdated()
  }

  fun stop(): Unit = unregisterBroadcastReceiver()

  private fun registerBroadcastReceiver() {
    timeUpdateReceiver = TimeUpdateBroadcastReceiver(doOnTimeUpdated)
    context.registerReceiver(timeUpdateReceiver, timeUpdateIntentFilter)
  }

  private fun unregisterBroadcastReceiver() {
    timeUpdateReceiver?.let(context::unregisterReceiver)
    timeUpdateReceiver = null
  }

  private class TimeUpdateBroadcastReceiver(val doOnReceive: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
      if (
        intent?.action == Intent.ACTION_TIME_TICK ||
        intent?.action == Intent.ACTION_TIME_CHANGED
      ) {
        doOnReceive()
      }
    }
  }
}
