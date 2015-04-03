package com.locket.curate.models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.joda.time.DateTime;

import play.Logger;
import play.db.ebean.Model;

import com.locket.common.util.DateUtil;

@JsonSerialize(include = Inclusion.NON_NULL)
@Entity
@Table(name = "CURATOR_CONTENT")
public class CuratorContent extends Model {

  private static final long serialVersionUID = 1L;
  public static Finder<Long, CuratorContent> find = new Finder<Long, CuratorContent>(Long.class,
      CuratorContent.class);

  @Id
  public Long id;

  @Column(name = "curator_id")
  public long curator_id;

  @Column(name = "is_approved")
  public Boolean is_approved;

  @Column(name = "created_at")
  public DateTime created_at;

  @Column(name = "preview_url")
  public String preview_url;

  @Column(name = "title")
  public String title;

  @Column(name = "source")
  public String source;

  @Column(name = "image_url")
  public String image_url;

  @Column(name = "tags")
  public String tags;

  public CuratorContent() {
  }

  public CuratorContent(Long id, String title, String source, String previewUrl, String imageUrl,
      String tags) {
    this.preview_url = previewUrl;
    Logger.error("" + previewUrl);
    this.curator_id = id;
    Logger.error("" + id);
    this.title = title;
    Logger.error(title);
    this.source = source;
    Logger.error(source);
    this.image_url = imageUrl;
    Logger.error(imageUrl);
    this.tags = tags;
    Logger.error(tags);
    this.created_at = new DateTime();
  }

  public long getCurator_id() {
    return curator_id;
  }

  /*
   * to-do : think about join!
   * http://www.avaje.org/ebean/introquery_joinquery.html
   */
  public List<CuratorContent> getCuratorContents(String requestAt) {
    Date data = DateUtil.parseDate(requestAt);
    List<CuratorContent> curatorContents = find.where().ge("created_at", data).findList();
    return curatorContents;
  }
}
