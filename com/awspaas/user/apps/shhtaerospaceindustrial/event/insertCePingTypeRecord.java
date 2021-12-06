/**
 * @Description 任务完成后更新任务分配表中的行车任务单状态
 * @author WU LiHua
 * @date 2020年2月8日 下午3:35:45
 */
package com.awspaas.user.apps.shhtaerospaceindustrial.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.commons.database.ColumnMapRowMapper;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.BOAPI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class insertCePingTypeRecord extends ExecuteListener implements ExecuteListenerInterface {

    @Override
    public String getDescription() {
        return "更新单位测评标中,个人记录信息.";
    }

    @Override
    public void execute(ProcessExecutionContext pec) throws Exception {
        try {
            String bindId = pec.getProcessInstance().getId();//流程实例ID

            String queryCount = "SELECT count(id) as SL FROM BO_EU_MYD_CEPING_YB_CEITEMXX WHERE " +
                    "BINDID" +
                    " = '" + bindId + "'";
            int sl = objToInt(DBSql.getInt(queryCount, "SL"));
            if (sl == 0) {

                String queryResource = "select distinct t.cepingtype from BO_EU_MYD_CEPINGDOC t";
                List<Map<String, Object>> resourceList = DBSql.query(queryResource, new ColumnMapRowMapper());
                if (resourceList != null && !resourceList.isEmpty()) {
                    BOAPI boAPI = SDK.getBOAPI();
                    String itemboname = "BO_EU_MYD_CEPING_YB_CEITEMXX";
                    List<BO> items = new ArrayList<BO>();
                    for (int i = 0; i < resourceList.size(); i++) {
                        Map<String, Object> resourceTaskFpIdMap = resourceList.get(i);
                        String cepingtype = objToStr(resourceTaskFpIdMap.get("cepingtype"));//来源任务分配单ID
                        BO itemRecordData = new BO();
                        itemRecordData.set("CEPINGTYPE", cepingtype);
                        items.add(itemRecordData);
                    }
                    if (items.size() > 0) {

                        int[] itemsnum = boAPI.create(itemboname, items, pec.getProcessInstance(),
                                pec.getUserContext());
                    }
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	public static String objToStr(Object obj) {
		return obj == null ? "" : obj.toString();
	}
	public static int objToInt(Object obj) {
		return obj == null ? 0 : Integer.parseInt(objToStr(obj));
	}
}
