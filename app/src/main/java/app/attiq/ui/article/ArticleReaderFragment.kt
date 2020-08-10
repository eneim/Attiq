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

package app.attiq.ui.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import app.attiq.R
import app.attiq.common.BaseDialogFragment
import app.attiq.databinding.FragmentArticleBinding

class ArticleReaderFragment : BaseDialogFragment() {

  private val args: ArticleReaderFragmentArgs by navArgs()
  private val viewModel: ArticleViewModel by viewModels()

  override fun getTheme(): Int {
    return R.style.Theme_Attiq_Dialog_Infinity
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.fragment_article, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val binding: FragmentArticleBinding = FragmentArticleBinding.bind(view)
    binding.itemTitle.text = args.itemTitle
    viewModel.itemDetail.observe(viewLifecycleOwner) {
      binding.content.renderArticle(args.itemTitle, it.renderedBody)
    }

    viewModel.itemId.value = args.itemId
  }
}
