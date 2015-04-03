package com.locket.notification.daos;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.locket.common.services.SqlManager;
import com.locket.notification.models.Notification;

/**
 * @author TZ
 * 
 */
public class NotificationDAO {

  public List<Notification> getAllNotifications() {
    SqlSession sqlSession = SqlManager.getClient();
    return sqlSession.selectList("getAllNotifications", Notification.class);
  }

  public Notification getNotification(String requestAt) {
    SqlSession sqlSession = SqlManager.getClient();
    return sqlSession.selectOne("getNotification", requestAt);
  }
  
  public int createNotification(Notification input) {
    
    SqlSession sqlSession = SqlManager.getClient();
    int row = sqlSession.insert("createNotification", input);
    return row;
  }

  public int updateNotification(Notification input) {
    SqlSession sqlSession = SqlManager.getClient();
    return sqlSession.update("updateNotification", input);
  }

  public int deleteNotification(Notification input) {
    SqlSession sqlSession = SqlManager.getClient();
    return sqlSession.delete("deleteNotification", input);
  }
}
