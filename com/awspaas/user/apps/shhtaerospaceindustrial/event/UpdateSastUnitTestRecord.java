/**
 * @Description 任务完成后更新任务分配表中的行车任务单状态
 * @author WU LiHua
 * @date 2020年2月8日 下午3:35:45
 */
package com.awspaas.user.apps.shhtaerospaceindustrial.event;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.commons.database.ColumnMapRowMapper;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.awspaas.user.apps.shhtaerospaceindustrial.util.CoreUtil;

import java.util.List;
import java.util.Map;

public class UpdateSastUnitTestRecord extends ExecuteListener implements ExecuteListenerInterface {

    @Override
    public String getDescription() {
        return "更新院测评,单位记录信息.";
    }

    @Override
    public void execute(ProcessExecutionContext pec) throws Exception {
        try {
            String bindId = pec.getProcessInstance().getId();//流程实例ID
            String queryResourceTaskFpId = "SELECT ID,SOURCEPORJECTID FROM BO_EU_MYD_CEPING_UNIT_HEAD WHERE BINDID" +
                    " = '" + bindId + "'";
            List<Map<String, Object>> resourceTaskFpIdList = DBSql.query(queryResourceTaskFpId, new ColumnMapRowMapper());
            if (resourceTaskFpIdList != null && !resourceTaskFpIdList.isEmpty()) {
                for (int i = 0; i < resourceTaskFpIdList.size(); i++) {
                    Map<String, Object> resourceTaskFpIdMap = resourceTaskFpIdList.get(i);
                    String SOURCEPORJECTID = CoreUtil.objToStr(resourceTaskFpIdMap.get("SOURCEPORJECTID"));//来源任务分配单ID
                    String ID = CoreUtil.objToStr(resourceTaskFpIdMap.get("ID"));

                    if(SOURCEPORJECTID!=null && (!("").equals(SOURCEPORJECTID))) {
                        if (SDK.getTaskAPI().isChoiceActionMenu(pec.getTaskInstance(), "确认"))
                       {
                            String updatesql = "UPDATE BO_EU_MYD_CEPING_YB_UNITJB SET CEPINGRECORDID = '" + ID + "'," +
                                    "ISCLOSED='是' WHERE  ID = '" + SOURCEPORJECTID + "'";
                            int updateflag = DBSql.update((updatesql));

                            System.out.println("更新操作的结果是：" + updateflag);
                        }
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
