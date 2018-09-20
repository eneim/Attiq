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

package im.ene.attiq.ui.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * @author eneim (2018/03/17).
 */
open class BaseFragment : Fragment() {

  var TAG = "Attiq:Base"

  override fun onAttach(context: Context?) {
    super.onAttach(context)
    TAG = "Attiq:" + javaClass.simpleName.take(15)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    Log.d(TAG, "onCreateView")
    return super.onCreateView(inflater, container, savedInstanceState)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    Log.d(TAG, "onCreateView")
  }
}