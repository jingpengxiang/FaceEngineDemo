package com.lu.face.faceenginedemo.engine;

import java.io.Serializable;

/**
 * 项目名称： FaceEngineDemo
 * 创建人： Jing
 * 创建时间： 2018/7/28  16:53
 * 修改备注：
 */
public class IdCardMsg implements Serializable {
    public String name;
    public String sex;
    public String nation_str;
//    public String imagePath;

    public String birth_year;
    public String birth_month;
    public String birth_day;
    public String address;
    public String id_num;
    public String sign_office;

    public String useful_s_date_year;
    public String useful_s_date_month;
    public String useful_s_date_day;

    public String useful_e_date_year;
    public String useful_e_date_month;
    public String useful_e_date_day;
    public byte[] photo;
    public byte[] fpdata;

}
