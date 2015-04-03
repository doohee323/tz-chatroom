package com.locket.notification.daos;

import java.util.ArrayList;
import java.util.List;

import play.Logger;

import com.locket.common.services.Mailer;
import com.locket.curate.models.CuratorContent;
import com.locket.session.models.User;

public class EmailNodificationDAO {

  public List<String> sendEmailFromEvent(String requestAt) {

    CuratorContent curatorContent = new CuratorContent();
    List<CuratorContent> curatorContents = curatorContent.getCuratorContents("2014-02-07");

    List<String> toEmails = new ArrayList<String>();
    for (CuratorContent cc : curatorContents) {
      cc.getCurator_id();
      toEmails.add(User.findById(cc.getCurator_id()).getEmail());
    }

    Mailer mailer = new Mailer();
    mailer.setFrom("doohee323@gmail.com");
    mailer.setSubject("Email noti!");
    mailer.setMailTemplate("");
    for (String toEmail : toEmails) {
      try {
        mailer.send(toEmail);
      } catch (Exception e) {
        Logger.debug("This is the exception2 " + e);
      }
    }

    return toEmails;
  }

}
