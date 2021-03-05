package com.springboot.scala

import java.io.FileInputStream
import java.sql.{Connection, PreparedStatement, ResultSet}
import java.util.Properties

import org.apache.commons.dbcp.BasicDataSource

/**
  * Created by DFJX on 2019/6/19.
  */
object ConnectPoolUtil {

  private var bs:BasicDataSource = null

  /**
    * 创建数据源
    * @return
    */
  def getDataSource():BasicDataSource={
    val properties = new Properties()
//    val path = Thread.currentThread().getContextClassLoader.getResource("project.properties").getPath //文件要放到resource文件夹下
//    properties.load(new FileInputStream(path))
    properties.load(Thread.currentThread().getContextClassLoader.getResourceAsStream("project.properties"))
    if(bs==null){
      bs = new BasicDataSource()
      bs.setDriverClassName(properties.getProperty("DriverClassName"))
      bs.setUrl(properties.getProperty("Url"))
      bs.setUsername(properties.getProperty("User"))
      bs.setPassword(properties.getProperty("Passwd"))
      bs.setMaxActive(properties.getProperty("MaxActive").toInt)           //设置最大并发数
      bs.setInitialSize(properties.getProperty("InitialSize").toInt)          //数据库初始化时，创建的连接个数
      bs.setMinIdle(properties.getProperty("MinIdle").toInt)              //最小空闲连接数
      bs.setMaxIdle(properties.getProperty("MaxIdle").toInt)             //数据库最大连接数
      bs.setMaxWait(properties.getProperty("MaxWait").toInt)
      bs.setMinEvictableIdleTimeMillis(properties.getProperty("MinEvictableIdleTimeMillis").toLong)     //空闲连接60秒中后释放
      bs.setTimeBetweenEvictionRunsMillis(properties.getProperty("TimeBetweenEvictionRunsMillis").toLong)      //5分钟检测一次是否有死掉的线程
      bs.setTestOnBorrow(true)
    }
    bs
  }

  /**
    * 释放数据源
    */
  def shutDownDataSource(){
    if(bs!=null){
      bs.close()
    }
  }

  /**
    * 获取数据库连接
    * @return
    */
  def getConnection():Connection={
    var con:Connection = null
    try {
      if(bs!=null){
        con = bs.getConnection()
      }else{
        con = getDataSource().getConnection()
      }
    } catch{
      case e:Exception => println(e.getMessage)
    }
    con
  }

  /**
    * 关闭连接
    */
  def closeCon(rs:ResultSet ,ps:PreparedStatement,con:Connection){
    if(rs!=null){
      try {
        rs.close()
      } catch{
        case e:Exception => println(e.getMessage)
      }
    }
    if(ps!=null){
      try {
        ps.close()
      } catch{
        case e:Exception => println(e.getMessage)
      }
    }
    if(con!=null){
      try {
        con.close()
      } catch{
        case e:Exception => println(e.getMessage)
      }
    }
  }
}
