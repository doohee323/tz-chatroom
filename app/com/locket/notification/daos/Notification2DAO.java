package com.locket.notification.daos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.locket.common.services.SqlManager;
import com.locket.notification.models.Notification;

/**
 * using Map
 * 
 * @author TZ
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Notification2DAO {

  public ArrayList<HashMap<String, String>> getAllNotifications() {
    SqlSession sqlSession = SqlManager.getClient();
    ArrayList<HashMap<String, String>> list =
        (ArrayList) sqlSession.selectList("getAllNotifications", new HashMap<String, String>());

    return list;
  }

  public ArrayList<HashMap<String, String>> getNotification(Map<String, String> input) {
    SqlSession sqlSession = SqlManager.getClient();
    ArrayList<HashMap<String, String>> list =
        (ArrayList) sqlSession.selectList("getNotification", input);
    return list;
  }

  public int createNotification(Map<String, String> input) {
    SqlSession sqlSession = SqlManager.getClient();
    return sqlSession.insert("createNotification", input);
  }

  public int updateNotification(Map<String, String> input) {
    SqlSession sqlSession = SqlManager.getClient();
    return sqlSession.update("updateNotification", input);
  }

  public int deleteNotification(Map<String, String> input) {
    SqlSession sqlSession = SqlManager.getClient();
    return sqlSession.delete("deleteNotification", input);
  }

}
