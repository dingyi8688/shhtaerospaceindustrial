package com.awspaass.user.apps.syncwechataddress;

import java.io.*;

public class inicfg {


    private String name;
    private String awsid;
    private String department;
    private String companywechatid;
    private String companywechatseret;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAwsid() {
        return awsid;
    }

    public void setAwsid(String awsid) {
        this.awsid = awsid;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCompanywechatid() {
        return companywechatid;
    }

    public void setCompanywechatid(String companywechatid) {
        this.companywechatid = companywechatid;
    }

    public String getCompanywechatseret() {
        return companywechatseret;
    }

    public void setCompanywechatseret(String companywechatseret) {
        this.companywechatseret = companywechatseret;
    }

    public String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }
}
