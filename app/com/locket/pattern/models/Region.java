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
@Table(name = "UIP_REGION")
public class Region extends Model {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static Finder<Long, Region> find = new Finder<Long, Region>(Long.class, Region.class);

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "ID", nullable = false)
  private int id;

  @Column(name = "UIP_CENTER_ID", nullable = false)
  private int uip_center_id;
  @Column(name = "CODE", nullable = false)
  private String code;
  @Column(name = "REGION_CODE", nullable = false)
  private String region_code;
  @Column(name = "NAME", nullable = false)
  private String name;
  @Column(name = "CHIEF")
  private String chief;
  @Column(name = "ADDRESS")
  private String address;
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

  public int getUip_center_id() {
    return uip_center_id;
  }

  public void setUip_center_id(int uip_center_id) {
    this.uip_center_id = uip_center_id;
  }

  public String getRegion_code() {
    return region_code;
  }

  public void setRegion_code(String region_code) {
    this.region_code = region_code;
  }

  public static Region findByCenterCode(String queryCenterCode, String input) {
    List<Region> list = find.where().eq("UIP_CENTER_ID", queryCenterCode).findList();
    if (list.size() == 0) {
      return null;
    } else {
      return list.get(0);
      // Region result = find.where().eq("code", code).findUnique();
    }
  }

  public static Region findByCode(String input) {
    List<Region> list = find.where().eq("code", input).findList();
    if (list.size() == 0) {
      return null;
    } else {
      return list.get(0);
      // Region result = find.where().eq("code", code).findUnique();
    }
  }

  public static List<Region> findByCreatedAt(String createdAt) {
    List<Region> result = find.where().eq("createdAt", createdAt).findList();
    return result;
  }

}
