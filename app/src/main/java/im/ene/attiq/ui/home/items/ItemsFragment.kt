/*
 * Copyright (c) 2018 Nam Nguyen, nam@ene.im
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

package im.ene.attiq.ui.home.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import attiq.di.Injectable
import im.ene.attiq.ui.base.BaseFragment
import im.ene.attiq.ui.item.ItemDetailActivity
import im.ene.attiq.R
import kotlinx.android.synthetic.main.fragment_items.itemList
import javax.inject.Inject

/**
 * @author eneim (2018/06/12).
 */
class ItemsFragment : BaseFragment(), Injectable {

  companion object {
    fun newInstance() = ItemsFragment()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_items, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    initAdapter()
  }

  @Inject
  lateinit var viewModel: ItemsViewModel

  private fun initAdapter() {
    val adapter = ItemsAdapter { _, id ->
      startActivity(ItemDetailActivity.createIntent(requireContext(), id))
    }

    itemList.adapter = adapter
    itemList.layoutManager = LinearLayoutManager(requireContext())
    viewModel.posts.data.observe(this, Observer { adapter.submitList(it) })
    viewModel.posts.networkState.observe(this, Observer {
      adapter.setNetworkState(it)
    })
  }
}