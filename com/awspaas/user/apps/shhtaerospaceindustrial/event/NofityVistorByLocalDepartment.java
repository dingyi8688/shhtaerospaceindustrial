package com.awspaas.user.apps.shhtaerospaceindustrial.event;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.commons.database.ColumnMapRowMapper;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;
import com.awspaas.user.apps.shhtaerospaceindustrial.sms.SmsUtil;
import com.awspaas.user.apps.shhtaerospaceindustrial.util.CoreUtil;

import java.util.List;
import java.util.Map;

public class NofityVistorByLocalDepartment extends ExecuteListener implements ExecuteListenerInterface {
    @Override
    public void execute(ProcessExecutionContext ctx) throws Exception {

        boolean flag_localdepart_ok = SDK.getTaskAPI().isChoiceActionMenu(ctx.getTaskInstance(), "同意");
        //if(flag_localdepart_ok) {
        if (true) {
            String[] car_sms_str = {"carnos", "carno2", "carno3"};
            String[] doorno_sms_str = {"doorno", "doorno2", "doorno3"};
            String[] permitdates_sms_str = {"permitdates", "permitdate2", "permitdate3"};
            String[] sms_template = {"SMS_227258595", "SMS_227263575", "SMS_227248755"};
            int door = 3;
            boolean isOneday = true;
            boolean isDepartment = false;
            boolean haveCar = false;
            String processInstId = ctx.getProcessInstance().getId();
            String carinfo_str = "";
            List<Map<String, Object>> visitorinfo = DBSql.query("SELECT * FROM BO_EU_VISITOR_MANAGE_MX WHERE BINDID = ? ", new ColumnMapRowMapper(), processInstId);
            List<Map<String, Object>> employeeinfo = DBSql.query("SELECT * FROM BO_EU_VISITOR_MANAGE WHERE BINDID = ? ", new ColumnMapRowMapper(), processInstId);
            List<Map<String, Object>> carinfo = DBSql.query("SELECT * FROM BO_EU_VISITOR_MANAGE_CARMX WHERE BINDID = ? ", new ColumnMapRowMapper(), processInstId);
            String carnos = "";
            String indate = "";
            String outdate = "";
            String sms_code = null;
            if (employeeinfo == null || employeeinfo.isEmpty()) {
                throw new BPMNError("无接待人员信息");
            }
            if (visitorinfo == null || visitorinfo.isEmpty()) {
                throw new BPMNError("无访客信息");
            }
            if (carinfo.size() > 0) {
                haveCar = true;
                int i = 0;
                //for(Map<String, Object> map : carinfo) {
                for (i = 0; i < 3 && i < carinfo.size(); i++) {
                    String carno = CoreUtil.objToStr(carinfo.get(i).get("CARNO"));
                    String instr = CoreUtil.objToStr(carinfo.get(i).get("INPARKDATE"));
                    indate = instr.substring(0, instr.lastIndexOf(" "));

                    String outstr = CoreUtil.objToStr(carinfo.get(i).get("OUTPARKDATE"));
                    outdate = outstr.substring(0, outstr.lastIndexOf(" "));
                    String time_in = "";
                    if (indate.equals(outdate)) {
                        time_in = indate;
                    } else {
                        time_in = indate + "至" + outdate;
                    }

                    String doorno = CoreUtil.objToStr(carinfo.get(i).get("INOUTDOOR"));
                    if ("园区三号门".equals(doorno)) {
                        door = 3;
                    } else if (("园区一号门").equals(doorno)) {
                        door = 1;
                    }

                    //carinfo_str+=carno+"入园时间："+indate+",出园时间:"+outdate+"，入园门号："+door;
                    carinfo_str += "','" + car_sms_str[i] + "':'" + carno + "','" + doorno_sms_str[i] + "':'" + doorno + "','" + permitdates_sms_str[i] + "':'" + time_in;

                }
                if (i >= 1) {
                    sms_code = sms_template[i - 1];
                }

            } else {
                haveCar = false;
            }
            Map<String, Object> employee = employeeinfo.get(0);
            String e_name = CoreUtil.objToStr(employee.get("TARGETMAN"));
            String e_unit = CoreUtil.objToStr(employee.get("TARGETUNIT"));
            System.out.println(e_unit);
            isDepartment = "上海航天技术研究院院部".equals(e_unit) || ("院部").equals(e_unit);
            String visittype = CoreUtil.objToStr(employee.get("VISITTYPE"));
            isOneday = "单日".equals(visittype);
            String e_department = CoreUtil.objToStr(employee.get("TARGETDEPT"));
            String e_date = CoreUtil.objToStr(employee.get("UPDATEDATE"));
            String date = e_date.substring(0, e_date.lastIndexOf(":"));
            String e_phone = CoreUtil.objToStr(employee.get("TARGETMANPHONE"));


            SmsUtil sms = new SmsUtil();
            System.out.println("部门节点批准：-----------");
            System.out.println(isOneday ? "单日" : "短期");
            System.out.println(door == 1 ? "一号门" : "三号门");
            System.out.println(haveCar ? "有车" : "无车");
            System.out.println(isDepartment ? "院部申请单" : "院部申请单");
            if (flag_localdepart_ok) {
                if (haveCar == false || door == 3 || isDepartment) {
                    if (haveCar == false) {

                        for (Map<String, Object> map : visitorinfo) {

                            String v_name = CoreUtil.objToStr(map.get("VISITORNAME"));
                            String message = "{'VISITORNAME':'" + v_name + "','date':'" + date + "','unitname':'" + e_unit + "','deptname':'" + e_department + "','psnname':'" + e_name + "','mobilephone':'" + e_phone + "'}";
                            String v_phone = CoreUtil.objToStr(map.get("VISITORCELL"));
                            System.out.println(message);
                            SmsUtil.sendSms(v_phone, "SMS_227253695", message);
                        }
                    } else {


                        System.out.println(sms_code);
                        for (Map<String, Object> map : visitorinfo) {

                            String v_name = CoreUtil.objToStr(map.get("VISITORNAME"));
                            String message = "{'VISITORNAME':'" + v_name + "','date':'" + date + "','unitname':'" + e_unit + "','deptname':'" + e_department + "','psnname':'" + e_name + "','mobilephone':'" + e_phone + carinfo_str + "'}";
                            System.out.println("发送短信内容：");
                            System.out.println(message);
                            String v_phone = CoreUtil.objToStr(map.get("VISITORCELL"));
                            SmsUtil.sendSms(v_phone, sms_code, message);
                        }
                    }

                }
            }
					/*
					if(SDK.getTaskAPI().isChoiceActionMenu(ctx.getTaskInstance(), "退回")) {
						String message = "{'date':'"+date+"','unitname':'"+e_unit+"','psnname':'"+e_name+"','mobilephone':'"+e_phone+"'}";
						for (Map<String, Object> map : visitorinfo) {
							
							String v_name = CoreUtil.objToStr(map.get("VISITORNAME"));
							String v_phone = CoreUtil.objToStr(map.get("VISITORCELL"));
							sms.sendSms(v_phone, "SMS_227260006",message);
						}
					}
					*/

            return;
        }


    }
}
