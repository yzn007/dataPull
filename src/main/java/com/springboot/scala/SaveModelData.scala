package com.springboot.scala

import java.sql.BatchUpdateException
import java.util.Date
import java.util.logging.{Level, Logger}

import scala.collection.mutable.ArrayBuffer


/**
  * Created by DFJX on 2019/6/19.
  */
object SaveModelData {
  def main(args: List[Array[String]]): Unit = {

    //    val list: java.util.List[Int] = Seq(1,2,3,4).asJava
    //    val buffer: scala.collection.mutable.Buffer[Int] = list.asScala

    // 屏蔽不必要的日志 ,在终端上显示需要的日志
    Logger.getLogger("org.apache.spark").setLevel(Level.OFF)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
    Logger.getLogger("org.apache.kafka.clients.consumer").setLevel(Level.OFF)

    val conn = ConnectPoolUtil.getConnection //ConnectPoolUtil是我创建的一个数据库连接池，getConnection是它的一个方法
    conn.setAutoCommit(false); //设为手动提交
    val stmt = conn.createStatement()
    val lis = new ArrayBuffer[String]
    var string = new StringBuffer()
    try {
      args.foreach(word => {
        //      stmt.addBatch("truncate log")

        word.foreach(w => {
          stmt.addBatch(w)
          lis.+=(w)
        })
      })
      stmt.executeBatch()
      conn.commit()
    } catch {
        case ex: BatchUpdateException=>{
//          println("batch lock:" + ex)
          try{
            string.append("\r\n")
            lis.foreach(w=>{
              stmt.addBatch(w)
              string.append(w+";")
            })
            conn.rollback()
            Thread.sleep(500)
            stmt.executeBatch()
            conn.commit()
          }catch{
            case e1:Exception=> {
              println(new Date()+" second fail:" + e1 )
//              for (i <- 0 until lis.length)
              //                println(lis(i)+";")
//              println(string.toString())
              conn.rollback()
              throw new Exception
            }
          }
        }

        case et: Exception => {
          println(et)
          conn.rollback()
          throw new Exception
        }
    } finally {
      conn.close()
    }


  }
}
