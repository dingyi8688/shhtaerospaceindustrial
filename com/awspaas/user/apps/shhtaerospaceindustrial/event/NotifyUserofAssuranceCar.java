package com.awspaas.user.apps.shhtaerospaceindustrial.event;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.commons.database.ColumnMapRowMapper;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.BPMNError;
import com.awspaas.user.apps.shhtaerospaceindustrial.sms.SmsUtil;
import com.awspaas.user.apps.shhtaerospaceindustrial.util.CoreUtil;

import java.util.List;
import java.util.Map;

public class NotifyUserofAssuranceCar extends ExecuteListener implements ExecuteListenerInterface {
    @Override
    public void execute(ProcessExecutionContext ctx) throws Exception {
        String processInstId = ctx.getProcessInstance().getId();
        List<Map<String, Object>> orderinfo = DBSql.query("select *  from BO_EU_YBOFFICEUSECAR ,ORGUSER where SJXM =USERNAME and BINDID= ? ", new ColumnMapRowMapper(), processInstId);

        if (orderinfo == null) {
            throw new BPMNError("无订单信息");
        }
        String[] cartype = {"轿车", "商务车", "面包车", "中型客车", "大型客车"};
        Map<String, Object> order = orderinfo.get(0);
        String APPLYUSERNAME = CoreUtil.objToStr(order.get("APPLYUSERNAME"));
        String APPLYUSERMOBILE = CoreUtil.objToStr(order.get("APPLYUSERMOBILE"));
        String BDATE = CoreUtil.objToStr(order.get("BDATE"));
        String SJXM = CoreUtil.objToStr(order.get("SJXM"));
        String CPH = CoreUtil.objToStr(order.get("CPH"));
        String VEHICLETYPE = CoreUtil.objToStr(order.get("VEHICLETYPE"));
        String carType = cartype[Integer.valueOf(VEHICLETYPE) - 1];

        String ISUSECAR = CoreUtil.objToStr(order.get("ISUSECAR"));
        String MOBILE = CoreUtil.objToStr(order.get("MOBILE"));
        String CONTACTPERSON = CoreUtil.objToStr(order.get("CONTACTPERSON"));
        String CONTACTPHONE = CoreUtil.objToStr(order.get("CONTACTPHONE"));
        SmsUtil message = new SmsUtil();
        if ("1".equals(ISUSECAR) == false) {
            String param_user = "{'APPLYUSERNAME':'" + CONTACTPERSON + "','SJLXFS':'" + MOBILE + "','UDATE':'" + BDATE + "','SJXM':'" + SJXM + "','CPH':'" + CPH + "','VEHICLETYPE':'" + carType + "'}";
            String param_orderuser = "{'APPLYUSERNAME':'" + APPLYUSERNAME + "','SJLXFS':'" + MOBILE + "','UDATE':'" + BDATE + "','SJXM':'" + SJXM + "','CPH':'" + CPH + "','VEHICLETYPE':'" + carType + "'}";
            //String param_dirver = "{'APPLYUSERNAME':'"+CONTACTPERSON+"','APPUSERCELLPHONE':'"+CONTACTPHONE+"','UDATE':'"+BDATE+"','SJXM':'"+SJXM+"','CPH':'"+CPH+"','VEHICLETYPE':'"+carType+"'}";
            String param_driver = "{'SJXM':'" + SJXM + "','UDATE':'" + BDATE + "','APPLYUSERNAME':'" + CONTACTPERSON + "','APPUSERCELLPHONE':'" + CONTACTPHONE + "','CPH':'" + CPH + "','VEHICLETYPE':'" + carType + "'}";
            SmsUtil.sendSms(APPLYUSERMOBILE, "SMS_226415988", param_orderuser);
            SmsUtil.sendSms(CONTACTPHONE, "SMS_226415988", param_user);
            SmsUtil.sendSms(MOBILE, "SMS_226530016", param_driver);
        } else {
            String param_user = "{'APPLYUSERNAME':'" + APPLYUSERNAME + "','SJLXFS':'" + MOBILE + "','UDATE':'" + BDATE + "','SJXM':'" + SJXM + "','CPH':'" + CPH + "','VEHICLETYPE':'" + carType + "'}";
            //String param_dirver = "{'APPLYUSERNAME':'"+APPLYUSERNAME+"','APPUSERCELLPHONE':'"+APPLYUSERMOBILE+"','UDATE':'"+BDATE+"','SJXM':'"+SJXM+"','CPH':'"+CPH+"','VEHICLETYPE':'"+carType+"'}";
            String param_driver = "{'SJXM':'" + SJXM + "','UDATE':'" + BDATE + "','APPLYUSERNAME':'" + APPLYUSERNAME + "','APPUSERCELLPHONE':'" + APPLYUSERMOBILE + "','CPH':'" + CPH + "','VEHICLETYPE':'" + carType + "'}";
            SmsUtil.sendSms(APPLYUSERMOBILE, "SMS_226415988", param_user);
            SmsUtil.sendSms(MOBILE, "SMS_226530016", param_driver);
        }


    }
}
