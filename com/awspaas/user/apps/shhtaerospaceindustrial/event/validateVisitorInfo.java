package com.awspaas.user.apps.shhtaerospaceindustrial.event;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListenerInterface;
import com.actionsoft.bpms.commons.database.ColumnMapRowMapper;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.BPMNError;
import com.awspaas.user.apps.shhtaerospaceindustrial.util.CoreUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class validateVisitorInfo implements InterruptListenerInterface {
    @Override
    public boolean execute(ProcessExecutionContext processExecutionContext) throws Exception {
        String processInstId = processExecutionContext.getProcessInstance().getId();
        List<Map<String, Object>> userinfolist = DBSql.query("SELECT * FROM BO_EU_VISITOR_MANAGE_MX WHERE BINDID = ? ", new ColumnMapRowMapper(), processInstId);
        if (userinfolist == null || userinfolist.isEmpty()) {
            throw new BPMNError("人员明细为空，请添加人员信息后提交");
        }
        List<String> certlist = new ArrayList<String>();

        for (Map<String, Object> map : userinfolist) {
            String VISITORNAME = CoreUtil.objToStr(map.get("VISITORNAME"));
            String VISITORCELL = CoreUtil.objToStr(map.get("VISITORCELL"));

            String CERTNO = CoreUtil.objToStr(map.get("CERTNO"));
            certlist.add(CERTNO);
            String ABOUTFILE = CoreUtil.objToStr(map.get("ABOUTFILE"));

            if (ABOUTFILE == "") {

                throw new BPMNError("请确认访客：" + VISITORNAME + "的证件照是否上传！");
            }
        }
        long certnocount = certlist.stream().distinct().count();
        List dumplist = certlist.stream()

                .collect(Collectors.toMap(e -> e, e -> 1, Integer::sum))

                .entrySet()

                .stream()

                .filter(e -> e.getValue() > 1)

                .map(Map.Entry::getKey)

                .collect(Collectors.toList());
        if (dumplist.size() > 0) {
            String certstr = "";
            for (Object val : dumplist) {
                certstr = val + ";";
            }
            System.out.println(certstr);
            throw new BPMNError("来访人员中重复身份证号：" + certstr);
        }
		
		/*if(certnocount!=certlist.size()) {
			throw new BPMNError("来访人员信息中有重复身份证号！");
		}*/

        List<Map<String, Object>> carinfolist = DBSql.query("SELECT * FROM BO_EU_VISITOR_MANAGE_CARMX WHERE BINDID = ? ", new ColumnMapRowMapper(), processInstId);
        List<String> carnolist = new ArrayList<String>();

        if (carinfolist.size() > 0) {
            for (Map<String, Object> map : carinfolist) {
                String CARNO = CoreUtil.objToStr(map.get("CARNO"));
                if (CARNO != "") {
                    carnolist.add(CARNO);
                }
            }
            long carnocount = carnolist.stream().distinct().count();
            if (carnocount != carnolist.size()) {
                throw new BPMNError("来访车辆信息里有重复车牌号！");
            }
        }

        return true;
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
