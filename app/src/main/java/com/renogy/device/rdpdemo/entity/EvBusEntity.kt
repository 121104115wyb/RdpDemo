package com.renogy.device.rdpdemo.entity


/**
 * @author Create by 17474 on 2023/6/20.
 * Email： lishuwentimor1994@163.com
 * Describe：传值蓝牙信息的实体类
 */
class EvBusEntity : BaseEntity() {

    var bleMac = ""

    var bleName = ""

    var hexResp = ""

    var hexCmd = ""

    var cmdTag = ""

    //mtu的状态码
    var mtuStatus = -1

    //mtu数据长度
    var mtu = -1

    //原intent的action
    var action = ""
    //蓝牙状态
    var bleState = -1
}