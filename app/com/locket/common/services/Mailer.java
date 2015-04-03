package com.locket.common.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.locket.common.util.StringUtil;
import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

public class Mailer {

  private String from;
  private String subject;
  private String mailTemplate;
  
  public void send(String to) {
    send(from, to, subject, mailTemplate, new HashMap<String, Object>());
  }

  public void send(String from, String to, String subject, String mailTemplate) {
    send(from, to, subject, mailTemplate, new HashMap<String, Object>());
  }

  public void send(String from, String to, String subject, String mailTemplate,
      Map<String, Object> var) {
    try {
      MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
      mail.addFrom(from);
      mail.setSubject(subject);
      mail.addRecipient(to);
      String mailBody = loadHtml(mailTemplate, var);
      mail.sendHtml(mailBody.replace("@@user_email@@", to));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 
   * <pre>
   * template html 을 읽어들여 메일의 내용을 구성한다.
   * </pre>
   * 
   * @param htmlTemplate
   *          htmlTemplete 이름
   * @param var
   *          htmlTemplete 속의 변수에 세팅되어야 하는 값
   * @return String
   * @exception Exception
   */
  public String loadHtml(String mailTemplate, Map<String, Object> var) throws Exception {
    StringBuffer htmlStr = new StringBuffer();
    String resultStr = null;
    BufferedReader in = null;
    try {
      String htmlPathNName = "public/templates/email/" + mailTemplate + ".html";
      in = new BufferedReader(new FileReader(htmlPathNName));
      String str = null;
      while ((str = in.readLine()) != null) {
        htmlStr.append(str).append("\n");
      }
      if (htmlStr != null && htmlStr.length() > 0) {
        resultStr = replaceVariables(htmlStr.toString(), var);
      }
    } catch (Exception e) {
      throw new Exception(e);
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException e) {
        throw new Exception(e);
      }
    }
    return resultStr;
  }

  /**
   * ${userKnm} 과 같은 위치에 userKnm 을 key로 같은 값을 치완한다.
   * 
   * @param orgStr
   * @param var
   * @return String
   */
  public String replaceVariables(String orgStr, Map<String, Object> var) {
    if (var != null) {
      Object[] key = var.keySet().toArray();
      for (int i = 0; i < key.length; i++) {
        if (!((var.get((String) key[i])) instanceof String))
          continue;
        String value = (String) var.get((String) key[i]);
        value = value == null ? "" : value;
        orgStr =
            orgStr.replaceAll("\\$\\{" + ((String) key[i]) + "\\}",
                StringUtil.quoteReplacement(value));
      }
    }

    return orgStr;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getMailTemplate() {
    return mailTemplate;
  }

  public void setMailTemplate(String mailTemplate) {
    this.mailTemplate = mailTemplate;
  }


}
