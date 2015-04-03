package com.locket.pattern.models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.ebean.Model;

/**
 * @author TZ
 * 
 */

@Entity
@Table(name = "UIP_CENTER")
public class Center extends Model{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static Finder<Long, Center> find = new Finder<Long, Center>(Long.class, Center.class);

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "ID", nullable = false)
  private int id;

  @Column(name = "CODE", nullable = false)
  private String code;
  @Column(name = "NAME", nullable = false)
  private String name;
  @Column(name = "CHIEF")
  private String chief;
  @Column(name = "ADDRESS")
  private String address;
  @Column(name = "PHONE")
  private String phone;
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

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getChief() {
    return chief;
  }

  public void setChief(String chief) {
    this.chief = chief;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public static Center findByCode(String input) {
    List<Center> list = find.where().eq("code", input).findList();
    if (list.size() == 0) {
      return null;
    } else {
      return list.get(0);
      // Center result = find.where().eq("code", code).findUnique();
    }
  }

  public static List<Center> findByCreatedAt(String createdAt) {
    List<Center> result = find.where().eq("createdAt", createdAt).findList();
    return result;
  }

}
