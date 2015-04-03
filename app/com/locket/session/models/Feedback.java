package com.locket.session.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.joda.time.DateTime;

import play.db.ebean.Model;

@JsonSerialize(include = Inclusion.NON_NULL)
@Entity
@Table(name = "FEEDBACK")
public class Feedback extends Model{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  public Long id;
  
  public String subject;

  public String feedback;

  @Column(name = "user_id")
  public Long userId;
  
  @Column(name = "created_at")
  public DateTime createdAt;

  public Feedback(Long userId ,String subject,String feedback) {
    this.userId = userId;
    this.subject = subject;
    this.feedback = feedback;
    this.createdAt = new DateTime();
  }

}
