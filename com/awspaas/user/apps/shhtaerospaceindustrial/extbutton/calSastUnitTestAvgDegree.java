package com.awspaas.user.apps.shhtaerospaceindustrial.extbutton;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ValueListener;
import com.actionsoft.bpms.commons.database.ColumnMapRowMapper;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.awspaas.user.apps.shhtaerospaceindustrial.util.CoreUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class calSastUnitTestAvgDegree extends ValueListener {

    @Override
    public String execute(ProcessExecutionContext param) throws Exception {
        // TODO Auto-generated method stub
        ResponseObject respon = ResponseObject.newOkResponse();
        String bindid = param.getProcessInstance().getId();

        List<BO> ceitemlist =
                SDK.getBOAPI().query("BO_EU_MYD_CEPING_YB_CEITEMXX", true).addQuery("BINDID =", bindid).asc().list();
        HashMap<String, Object> cpitemmap = new HashMap();
        if (ceitemlist != null && !ceitemlist.isEmpty()) {
            for (int i = 0; i < ceitemlist.size(); i++) {
//				JSONObject orderItem = new JSONObject();
                BO ceitembo = ceitemlist.get(i);
                String ID = CoreUtil.objToStr(ceitembo.get("ID"));
                String CEPINGTYPE = CoreUtil.objToStr(ceitembo.get("CEPINGTYPE"));
                cpitemmap.put(CEPINGTYPE, ID);
            }
        }


        String querySql = "select t.cepingtype, round(avg(t.cepitemavagnum),2) as cetypeavg " +
                "  from view_shht_mydtestforunit t  where t.sourceporjectid in (select id from " +
                "BO_EU_MYD_CEPING_YB_UNITJB where bindid= '" + bindid + "' and  ISSTARTCEPING='是')" +
                " group by t.cepingtype";
        List<Map<String, Object>> dataList = DBSql.query(querySql, new ColumnMapRowMapper());
        if (dataList != null && !dataList.isEmpty()) {
            for (Map<String, Object> dataMap : dataList) {
//				JSONObject orderItem = new JSONObject();
                String cepingtype = CoreUtil.objToStr(dataMap.get("cepingtype"));
                Object cetypeavg = CoreUtil.objToStr(dataMap.get("cetypeavg"));

                if (cepingtype != null && !("").equals(cepingtype)) {
                    if (cpitemmap.containsKey(cepingtype)) {
                        String bid = (String) cpitemmap.get(cepingtype);

                        int updateflag = DBSql.update("UPDATE BO_EU_MYD_CEPING_YB_CEITEMXX SET CEPINGTYPEAVGNUM = '" + cetypeavg +
                                "' " +
                                "WHERE ID = '" + bid + "'");
                        System.out.println("更新操作的结果是：" + updateflag);
                    }
                }


            }
        }


        return "操作结束";
    }

}
