package com.awspaass.user.apps.businesscar;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;


public class missionStatusUpdate extends ExecuteListener implements ExecuteListenerInterface {
    public String getDescription() {
        return "更新任务分配表中的行车任务单状态！";
    }

    public void execute(ProcessExecutionContext pec) throws Exception {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
