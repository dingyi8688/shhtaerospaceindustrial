package com.awspaass.user.apps.tempcar;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.commons.database.ColumnMapRowMapper;
import com.actionsoft.bpms.util.DBSql;
import com.awspaas.user.apps.shhtaerospaceindustrial.util.CoreUtil;

import java.util.List;
import java.util.Map;

public class EventInnerDriverAcceptMission extends ExecuteListener implements ExecuteListenerInterface {
    public String getDescription() {
        return "内租车司机接单更新表状态";
    }

    public void execute(ProcessExecutionContext pec) throws Exception {
        try {
            String bindId = pec.getProcessInstance().getId();// 流程实例ID
            String queryMissionSql = "SELECT a.RESOURCETASKFPID FROM BO_EU_SH_VEHICLEORDER_MISSION WHERE BINID='" + bindId + "'";
            List<Map<String, Object>> missionList = DBSql.query(queryMissionSql, new ColumnMapRowMapper()
            );
            if (missionList != null && !missionList.isEmpty()) {
                Map<String, Object> MissonInfo = missionList.get(0);
                String RESOURCETASKFPID = CoreUtil.objToStr(MissonInfo.get("RESOURCETASKFPID"));
                DBSql.update("UPDATE BO_EU_SH_VEHICLEORDER_MISSION SET MISSIONSTATUS = '2' WHERE BINDID = '"
                        + bindId + "'");
                DBSql.update("UPDATE BO_EU_SH_VEHICLEORDER_ASSIGMIS SET MISSIONSTATUS = '2' WHERE ID = '"
                        + RESOURCETASKFPID + "'");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
