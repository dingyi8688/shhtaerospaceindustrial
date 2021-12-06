package com.awspaas.user.apps.shhtaerospaceindustrial.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListenerInterface;
import com.actionsoft.bpms.commons.database.ColumnMapRowMapper;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;

public class validateUnitCePingResut implements InterruptListenerInterface {
	@Override
	public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
		String processInstId = processExecutionContext.getProcessInstance().getId();
		List<Map<String, Object>> userinfolist = DBSql.query("SELECT * FROM BO_EU_VISITOR_MANAGE_MX WHERE BINDID = ? ",
				new ColumnMapRowMapper(), new Object[] { processInstId });

		List<BO> ceitemlist = SDK.getBOAPI().query("BO_EU_MYD_CEPING_UNIT_CPITEM", true)
				.addQuery("BINDID =", processInstId).asc().list();
		HashMap<String, Object> cpitemmap = new HashMap();
		if (ceitemlist != null && !ceitemlist.isEmpty()) {
			for (int i = 0; i < ceitemlist.size(); i++) {
				BO ceitembo = ceitemlist.get(i);
				String ID = objToStr(ceitembo.get("ID"));
				String CEPINGTYPE = objToStr(ceitembo.get("CEPINGTYPE"));
				String CEPINGITEM = objToStr(ceitembo.get("CEPINGITEM"));
				Object CEPITEMAVAGNUM = objToStr(ceitembo.get("CEPITEMAVAGNUM"));
				if (CEPITEMAVAGNUM == null || ("").equals(CEPITEMAVAGNUM)) {
					throw new BPMNError(CEPINGTYPE + "分类下面子项" + CEPINGITEM + "的平均分还未计算,请计算分数后再提交.");
				}

			}
		}

		return true;
	}

	public static String objToStr(Object obj) {
		return obj == null ? "" : obj.toString();
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}
}
