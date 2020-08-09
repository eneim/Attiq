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

package app.attiq.ui.home.view

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadState.Error
import androidx.paging.LoadState.Loading
import app.attiq.R
import app.attiq.common.BaseViewHolder
import app.attiq.databinding.HolderNetworkStateBinding

class NetworkStateViewHolder(
  parent: ViewGroup,
  val retry: () -> Unit
) : BaseViewHolder(parent, R.layout.holder_network_state) {

  private val binding = HolderNetworkStateBinding.bind(itemView)

  init {
    binding.retryButton.setOnClickListener { retry() }
  }

  override fun onBind(payload: Any?) {
    (payload as LoadState).let { loadState ->
      binding.progressBar.isVisible = loadState is Loading
      binding.retryButton.isVisible = loadState is Error
      binding.errorMsg.isVisible = (loadState is Error) && !loadState.error.message.isNullOrBlank()
      binding.errorMsg.text = (loadState as? Error)?.error?.message
    }
  }
}
