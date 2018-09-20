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

package im.ene.attiq.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import im.ene.attiq.ui.auth.WebAuthActivity
import im.ene.attiq.ui.base.BaseActivity
import im.ene.attiq.ui.home.items.ItemsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import im.ene.attiq.R
import kotlinx.android.synthetic.main.activity_home.navigation
import javax.inject.Inject


class HomeActivity : BaseActivity(), HasSupportFragmentInjector {

  private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
    when (item.itemId) {
      R.id.navigation_home -> {
        return@OnNavigationItemSelectedListener true
      }
      R.id.navigation_dashboard -> {
        return@OnNavigationItemSelectedListener true
      }
      R.id.navigation_notifications -> {
        startActivity(WebAuthActivity.createIntent(this))
        return@OnNavigationItemSelectedListener true
      }
    }
    false
  }

  @Inject
  lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

  override fun supportFragmentInjector() = dispatchingAndroidInjector

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home)
    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    if (savedInstanceState == null) {
      supportFragmentManager.beginTransaction()
          .replace(R.id.fragment, ItemsFragment.newInstance())
          .commit()
    }
  }
}
