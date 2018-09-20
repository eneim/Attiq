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

package attiq.di

import androidx.lifecycle.ViewModel
import im.ene.attiq.ui.base.BaseViewModel
import im.ene.attiq.ui.home.items.ItemsViewModel
import im.ene.attiq.ui.item.ItemDetailViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * @author eneim (2018/04/22).
 */
@Module
abstract class ViewModelModule {
  @Binds
  @IntoMap
  @ViewModelKey(ItemDetailViewModel::class)
  abstract fun bindItemDetailViewModel(viewModel: ItemDetailViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(ItemsViewModel::class)
  abstract fun bindItemsViewModel(viewModel: ItemsViewModel): ViewModel

  @Binds
  @IntoMap
  @ViewModelKey(BaseViewModel::class)
  abstract fun bindBaseViewModel(viewModel: BaseViewModel): ViewModel
}