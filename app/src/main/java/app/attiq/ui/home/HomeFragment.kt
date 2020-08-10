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

package app.attiq.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import app.attiq.R
import app.attiq.common.BaseFragment
import app.attiq.common.TimeUpdateObserver
import app.attiq.databinding.FragmentHomeBinding
import app.attiq.ui.home.adapter.ItemsAdapter
import app.attiq.ui.home.adapter.LoadingStateAdapter
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter

@FlowPreview
class HomeFragment : BaseFragment(R.layout.fragment_home) {

  private val homeViewModel: HomeViewModel by viewModels()

  private lateinit var timeUpdateObserver: TimeUpdateObserver

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val binding = FragmentHomeBinding.bind(view)

    val itemsAdapter = ItemsAdapter { _, article ->
      val action = HomeFragmentDirections.openArticleAction(article.itemId, article.title)
      findNavController().navigate(action)
    }

    timeUpdateObserver = TimeUpdateObserver(view.context) {
      itemsAdapter.notifyItemRangeChanged(0, itemsAdapter.itemCount, ItemsAdapter.PAYLOAD_TIME)
    }

    val retry: () -> Unit = { itemsAdapter.retry() }
    binding.items.adapter = itemsAdapter.withLoadStateHeaderAndFooter(
      header = LoadingStateAdapter(retry),
      footer = LoadingStateAdapter(retry)
    )

    binding.refresher.setOnRefreshListener {
      itemsAdapter.refresh()
    }

    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
      itemsAdapter.loadStateFlow.collectLatest { loadStates ->
        binding.refresher.isRefreshing = loadStates.refresh is LoadState.Loading
      }
    }

    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
      homeViewModel.items.collectLatest {
        itemsAdapter.submitData(it)
      }
    }

    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
      itemsAdapter.loadStateFlow
        // Only emit when REFRESH LoadState for RemoteMediator changes.
        .distinctUntilChangedBy { it.refresh }
        // Only react to cases where Remote REFRESH completes i.e., NotLoading.
        .filter { it.refresh is LoadState.NotLoading }
        .collect { binding.items.scrollToPosition(0) }
    }
  }

  override fun onStart() {
    super.onStart()
    timeUpdateObserver.start()
  }

  override fun onStop() {
    super.onStop()
    timeUpdateObserver.stop()
  }
}
