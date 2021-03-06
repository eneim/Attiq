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

package attiq.data.model

/**
 * @author eneim (2018/03/30).
 */
class UserModel(
    val userId: String,
    val userName: String,
    val organization: String?,
    val profileImageUrl: String,
    val githubName: String?,
    val twitterName: String?,
    val facebookName: String?,
    val linkedinName: String?,
    val websiteUrl: String?,
    val location: String?,
    val followerCount: Int,
    val foloweeCount: Int,
    val itemCount: Int
)