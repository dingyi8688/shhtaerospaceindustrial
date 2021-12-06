package com.awspaas.user.apps.shhtaerospaceindustrial.extbutton;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ValueListener;
import com.actionsoft.bpms.commons.database.ColumnMapRowMapper;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class calUnitPsnTestAvgDegree extends ValueListener {

    @Override
    public String execute(ProcessExecutionContext param) throws Exception {
        // TODO Auto-generated method stub
        ResponseObject respon = ResponseObject.newOkResponse();
        String bindid = param.getProcessInstance().getId();

        List<BO> ceitemlist =
                SDK.getBOAPI().query("BO_EU_MYD_CEPING_UNIT_CPITEM", true).addQuery("BINDID =", bindid).asc().list();
        HashMap<String, Object> cpitemmap = new HashMap();
        if (ceitemlist != null && !ceitemlist.isEmpty()) {
            for (int i = 0; i < ceitemlist.size(); i++) {
//				JSONObject orderItem = new JSONObject();
                BO ceitembo = ceitemlist.get(i);
                String ID = objToStr(ceitembo.get("ID"));
                String CEPINGTYPE = objToStr(ceitembo.get("CEPINGTYPE"));
                String CEPINGITEM = objToStr(ceitembo.get("CEPINGITEM"));
                cpitemmap.put(CEPINGTYPE + CEPINGITEM, ID);
            }
        }


        String querySql = "select t.cepingtype,  t.cepingitem,t.cepingtype||t.cepingitem as itemkeyword," +
                " round(sum(t.manyigudegree) / sum(t.manyigucount), 2) as itemavg " +
                "  from VIEW_SHHT_MYDTESTFORPSN t   where t.sourceporjectid "
                + "in (select id from BO_EU_MYD_CEPING_UNIT_PSN where bindid='" + bindid + "' and  ISSTARTCEPING='1')" +
                " group by t.cepingtype, t.cepingitem";
        List<Map<String, Object>> dataList = DBSql.query(querySql, new ColumnMapRowMapper());
        if (dataList != null && !dataList.isEmpty()) {
            for (Map<String, Object> dataMap : dataList) {
//				JSONObject orderItem = new JSONObject();
                String itemkeyword = objToStr(dataMap.get("itemkeyword"));
                Object itemavgdegree = objToStr(dataMap.get("itemavg"));

                if (itemkeyword != null && !("").equals(itemkeyword)) {
                    if (cpitemmap.containsKey(itemkeyword)) {
                        String bid = (String) cpitemmap.get(itemkeyword);

                        int updateflag = DBSql.update("UPDATE BO_EU_MYD_CEPING_UNIT_CPITEM SET cepitemavagnum = '" + itemavgdegree +
                                "' " +
                                "WHERE ID = '" + bid + "'");
                        System.out.println("更新操作的结果是：" + updateflag);
                    }
                }


            }
        }


        return "操作结束";
    }
	public static String objToStr(Object obj) {
		return obj == null ? "" : obj.toString();
	}

}
