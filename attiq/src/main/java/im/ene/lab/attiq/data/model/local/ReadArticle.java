/*
 * Copyright 2016 eneim@Eneim Labs, nam@ene.im
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

package im.ene.lab.attiq.data.model.local;

import im.ene.lab.attiq.data.model.two.Article;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by eneim on 1/23/16.
 */
public class ReadArticle extends RealmObject {

  public static final String FIELD_ARTICLE_ID = "articleId";

  public static final String FIELD_LAST_VIEW = "lastView";

  @PrimaryKey
  private String articleId;

  private Long lastView;

  private Article article;

  public String getArticleId() {
    return articleId;
  }

  public void setArticleId(String articleId) {
    this.articleId = articleId;
  }

  public Long getLastView() {
    return lastView;
  }

  public void setLastView(Long lastView) {
    this.lastView = lastView;
  }

  public Article getArticle() {
    return article;
  }

  public void setArticle(Article article) {
    this.article = article;
  }
}
