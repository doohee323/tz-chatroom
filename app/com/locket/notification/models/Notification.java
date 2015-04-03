package com.locket.notification.models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.ebean.Model;

@Entity
@Table(name = "NOTIFICATION")
public class Notification extends Model{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static Finder<Long, Notification> find = new Finder<Long, Notification>(Long.class,
      Notification.class);

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "ID", nullable = false)
  private int id;

  @Column(name = "USER_ID", nullable = false)
  private String userId;

  @Column(name = "TITLE", nullable = false)
  private String title;

  @Column(name = "CREATED_AT", columnDefinition = "datetime default now()")
  private Date createdAt;

  @Column(name = "UPDATED_AT", columnDefinition = "datetime default now()")
  private Date updatedAt;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public static Notification findByUserId(String userId) {
    List<Notification> list = find.where().eq("userId", userId).findList();
    if(list.size() == 0) {
      return null;
    } else {
      return list.get(0);
      //Notification noti = find.where().eq("userId", userId).findUnique();
    }
  }

  public static List<Notification> findByCreatedAt(String createdAt) {
    List<Notification> notis = find.where().eq("createdAt", createdAt).findList();
    return notis;
  }
}