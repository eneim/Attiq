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

package im.ene.attiq.ui.item

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import attiq.binding.BindingComponent
import attiq.di.Injectable
import im.ene.attiq.ui.base.BaseActivity
import im.ene.attiq.R
import im.ene.attiq.databinding.ActivityItemDetailBinding
import javax.inject.Inject

class ItemDetailActivity : BaseActivity(), Injectable {

  companion object {
    private const val EXTRA_ITEM_ID = "attiq:extra:item_id"
    fun createIntent(context: Context, itemId: String): Intent {
      val intent = Intent(context, ItemDetailActivity::class.java)
      intent.putExtra(EXTRA_ITEM_ID, itemId)
      return intent
    }
  }

  @Inject
  lateinit var viewModel: ItemDetailViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val component = BindingComponent()
    val binding = DataBindingUtil.setContentView(this,
        R.layout.activity_item_detail, component) as ActivityItemDetailBinding
    setSupportActionBar(binding.toolbar)

    binding.setLifecycleOwner(this)
    binding.viewModel = viewModel
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    viewModel.setItemId(intent.getStringExtra(
        EXTRA_ITEM_ID)!!)
  }
}