package com.awspaas.user.apps.shhtaerospaceindustrial.extbutton;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ValueListener;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.commons.database.ColumnMapRowMapper;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.BOAPI;
import com.actionsoft.sdk.local.api.ProcessAPI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class createUnitTestBillProsses extends ValueListener {

    @Override
    public String execute(ProcessExecutionContext param) throws Exception {
        // TODO Auto-generated method stub
        ResponseObject respon = ResponseObject.newOkResponse();
        String bindid = param.getProcessInstance().getId();

        String queryDdy = "SELECT YEARINFO,SEASONINFO FROM BO_EU_MYD_CEPING_YB_HEAD WHERE BINDID = '" + bindid + "'";
        String YEARINFO = objToStr(DBSql.getString(queryDdy, "YEARINFO"));
        String SEASONINFO = objToStr(DBSql.getString(queryDdy, "SEASONINFO"));


        System.out.println("bindid::" + bindid);
        String querySql = "select ID,APPLYUNIT,APPLYDEPTNAME,APPLYUSERNAME,APPLYUSERNZH,NEEDCENUM from " +
                "BO_EU_MYD_CEPING_YB_UNITJB where " +
                "bindid='" + bindid + "' and  ISSTARTCEPING='0'";
        List<Map<String, Object>> dataList = DBSql.query(querySql, new ColumnMapRowMapper());
        if (dataList != null && !dataList.isEmpty())
        {
            for (Map<String, Object> dataMap : dataList) {
//				JSONObject orderItem = new JSONObject();
                String ID = objToStr(dataMap.get("ID"));
                String APPLYUNIT = objToStr(dataMap.get("APPLYUNIT"));
                String APPLYDEPTNAME = objToStr(dataMap.get("APPLYDEPTNAME"));
                String APPLYUSERNAME = objToStr(dataMap.get("APPLYUSERNAME"));
                String APPLYUSERNZH = objToStr(dataMap.get("APPLYUSERNZH"));
                String NEEDCENUM = objToStr(dataMap.get("NEEDCENUM"));

                if (APPLYUSERNZH != null && !("").equals(APPLYUSERNZH)) {
                    String prosuuid = "obj_f255e8bfefec4434bdd1b8c390004d98"; //单位测评流程定义ID
                    ProcessAPI proapi = SDK.getProcessAPI();
                    // 创建了一个流程实例，第一个参数为流程UUID
                    ProcessInstance processInstance = proapi.createProcessInstance(prosuuid, APPLYUSERNZH, YEARINFO +
                            "年" + SEASONINFO + "季度,园区服务满意度测评");
                    processInstance.getEndActivityId();

                    System.out.println("流程实例对象是：" + processInstance + "\n流程实例ID:" + processInstance.getId());
                    // 获取流程状态
                    String proState = processInstance.getControlState();
                    System.out.println("当前流程状态：" + proState);
                    // 启动了这个流程实例
                    proapi.start(processInstance, "");
                    // 获得流程的taskId，启动后才有任务实例的存在
                    String taskid1 = processInstance.getStartTaskInstId();
                    System.out.println("taskID1::" + taskid1);

                    // 准备数据
                    BOAPI boAPI = SDK.getBOAPI();
                    String boname = "BO_EU_MYD_CEPING_UNIT_HEAD"; //单位测评主表
                    BO boRecordData = new BO();
                    boRecordData.set("APPLYUNIT", APPLYUNIT);
                    boRecordData.set("APPLYDEPTNAME", APPLYDEPTNAME);
                    boRecordData.set("APPLYUSERNAME", APPLYUSERNAME);
                    boRecordData.set("APPLYUSERZH", APPLYUSERNZH);
                    boRecordData.set("NEEDCENUM", NEEDCENUM);
                    boRecordData.set("YEARINFO", YEARINFO);
                    boRecordData.set("SEASONINFO", SEASONINFO);
                    boRecordData.set("SOURCEPORJECTID", ID);

                    int actionflag = boAPI.create(boname, boRecordData, processInstance, param.getUserContext());

                    String queryItemSql = "select CEPINGTYPE,CEPINGITEM,ID from BO_EU_MYD_CEPINGDOC where  ISOK='是'";
                    List<Map<String, Object>> itemdataList = DBSql.query(queryItemSql, new ColumnMapRowMapper()
                    );

                    if (itemdataList != null && !itemdataList.isEmpty()) {
                        List<BO> items = new ArrayList<BO>();
                        String itemboname = "BO_EU_MYD_CEPING_UNIT_CPITEM";
                        for (Map<String, Object> itemdataMap : itemdataList) {
                            String CEPINGTYPE = objToStr(itemdataMap.get("CEPINGTYPE"));
                            String CEPINGITEM = objToStr(itemdataMap.get("CEPINGITEM"));
                            BO itemRecordData = new BO();
                            itemRecordData.set("CEPINGTYPE", CEPINGTYPE);
                            itemRecordData.set("CEPINGITEM", CEPINGITEM);
//                            itemRecordData.set("MANYIDU", "基本满意");
                            items.add(itemRecordData);
                        }
                        if (items.size() > 0) {
                            int[] itemsnum = boAPI.create(itemboname, items, processInstance,
                                    param.getUserContext());
                        }
                    }


                    if (actionflag > 0) {
                        int updateflag = DBSql.update("UPDATE BO_EU_MYD_CEPING_YB_UNITJB SET ISSTARTCEPING = '1' " +
                                "WHERE ID = '" + ID + "'");
                        System.out.println("更新操作的结果是：" + updateflag);
                    }

                }

            }

        }else{
            return "未查询到需要启动的记录";
        }


//		respon.put("action", "success");

        return "操作成功";
    }
	public static String objToStr(Object obj) {
		return obj == null ? "" : obj.toString();
	}

}
