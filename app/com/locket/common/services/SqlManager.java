package com.locket.common.services;

import java.io.IOException;
import java.io.Reader;
import java.util.Observable;
import java.util.Observer;

import org.apache.ibatis.io.Resources; 
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSessionManager; 

import play.Configuration;
import play.Play;

public class SqlManager implements Observer {

  public static final long serialVersionUID = 1L;

  private static SqlSessionFactory sqlSessionFactory;

  private static SqlSessionManager sqlSessionManager;

  private static SqlManager singleton;

  /**
   * 싱글턴 대상이 null일 경우 인스턴스화 한다.
   * 
   * @return SqlManager 객체
   */
  public static synchronized SqlManager getInstance() {
    try {
      if (singleton == null) {
        singleton = new SqlManager();
      }
      return singleton;
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  public SqlManager() {
    init();
  }

  public void init() {
    Reader reader;
    try {
      Configuration config = Play.application().configuration();

      String resource = config.getString("mybatis.configuration");

      try {
        reader = Resources.getResourceAsReader(resource);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    } catch (Exception e) {
      e.printStackTrace();
    }
    sqlSessionManager = SqlSessionManager.newInstance(sqlSessionFactory);
  }

  public SqlSessionFactory getSqlSessionFactory() {
    return sqlSessionFactory;
  }

  public static SqlSessionFactory getSqlSession() {
    return SqlManager.getInstance().getSqlSessionFactory();
  }

  public SqlSessionManager getSqlSessionManager() {
    return sqlSessionManager;
  }

  public static SqlSessionManager getSqlManager() {
    return SqlManager.getInstance().getSqlSessionManager();
  }

  public static SqlSession getClient() {
    try {
      if (getSqlSession() == null) {
        getInstance().init();
      } else {
        return getSqlSession().openSession();
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

//  public static void begin() {
//    getSqlManager().startManagedSession();
//  }

//  public static void end() {
//    try {
//      getSqlManager().commit();
//    } catch (Throwable t) {
//      getSqlManager().rollback();
//      System.out.println(t);
//    } finally {
//      getSqlManager().close();
//    }
//  }

  @Override
  public void update(Observable o, Object arg) {
    // TODO Auto-generated method stub

  }
}