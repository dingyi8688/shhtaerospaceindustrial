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
import com.awspaas.user.apps.shhtaerospaceindustrial.util.CoreUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class createUnitPsnTestBillProsses extends ValueListener {

    @Override
    public String execute(ProcessExecutionContext param) throws Exception {
        // TODO Auto-generated method stub
        ResponseObject respon = ResponseObject.newOkResponse();
        String bindid = param.getProcessInstance().getId();

        String queryDdy = "SELECT APPLYUNIT,YEARINFO,SEASONINFO FROM BO_EU_MYD_CEPING_UNIT_HEAD WHERE BINDID = '" + bindid + "'";
        String APPLYUNIT = CoreUtil.objToStr(DBSql.getString(queryDdy, "APPLYUNIT"));
        String YEARINFO = CoreUtil.objToStr(DBSql.getString(queryDdy, "YEARINFO"));
        String SEASONINFO = CoreUtil.objToStr(DBSql.getString(queryDdy, "SEASONINFO"));


        System.out.println("bindid::" + bindid);
        String querySql = "select ID,DEPTNAME,PSNNAME,PSNUSERID,CELLPHONE from " +
                "BO_EU_MYD_CEPING_UNIT_PSN where " +
                "bindid='" + bindid + "' and  ISSTARTCEPING='否'";
        List<Map<String, Object>> dataList = DBSql.query(querySql, new ColumnMapRowMapper());
        if (dataList != null && !dataList.isEmpty())
        {
            for (Map<String, Object> dataMap : dataList) {
//				JSONObject orderItem = new JSONObject();
                String ID = CoreUtil.objToStr(dataMap.get("ID"));

                String PSNNAME = CoreUtil.objToStr(dataMap.get("PSNNAME"));
                String CELLPHONE = CoreUtil.objToStr(dataMap.get("CELLPHONE"));
                String PSNUSERID = CoreUtil.objToStr(dataMap.get("PSNUSERID"));
                String DEPTNAME = CoreUtil.objToStr(dataMap.get("DEPTNAME"));

                if (PSNUSERID != null && !("").equals(PSNUSERID)) {
                    String prosuuid = "obj_244ab6ba9bb3472397e7386e76604921"; //个人测评流程定义ID
                    ProcessAPI proapi = SDK.getProcessAPI();
                    // 创建了一个流程实例，第一个参数为流程UUID
                    ProcessInstance processInstance = proapi.createProcessInstance(prosuuid, PSNUSERID, YEARINFO +
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
                    String boname = "BO_EU_MYD_CEPING_HEAD"; //单位测评主表
                    BO boRecordData = new BO();
                    boRecordData.set("APPLYUNIT", APPLYUNIT);
                    boRecordData.set("APPLYDEPTNAME", DEPTNAME);
                    boRecordData.set("APPLYUSERNAME", PSNNAME);
                    boRecordData.set("APPLYUSERZH", PSNUSERID);
                    boRecordData.set("YEARINFO", YEARINFO);
                    boRecordData.set("SEASONINFO", SEASONINFO);
                    boRecordData.set("SOURCEPORJECTID", ID);

                    int actionflag = boAPI.create(boname, boRecordData, processInstance, param.getUserContext());

                    String queryItemSql = "select CEPINGTYPE,CEPINGITEM,ID from BO_EU_MYD_CEPINGDOC where  ISOK='是'";
                    List<Map<String, Object>> itemdataList = DBSql.query(queryItemSql, new ColumnMapRowMapper()
					);

                    if (itemdataList != null && !itemdataList.isEmpty()) {
                        List<BO> items = new ArrayList<BO>();
                        String itemboname = "BO_EU_MYD_CEPING_BODY";
                        for (Map<String, Object> itemdataMap : itemdataList) {
                            String CEPINGTYPE = CoreUtil.objToStr(itemdataMap.get("CEPINGTYPE"));
                            String CEPINGITEM = CoreUtil.objToStr(itemdataMap.get("CEPINGITEM"));
                            BO itemRecordData = new BO();
                            itemRecordData.set("CEPINGTYPE", CEPINGTYPE);
                            itemRecordData.set("CEPINGITEM", CEPINGITEM);
                            itemRecordData.set("MANYIDU", "基本满意");
                            items.add(itemRecordData);
                        }
                        if (items.size() > 0) {
                            int[] itemsnum = boAPI.create(itemboname, items, processInstance,
                                    param.getUserContext());

                            System.out.println("创建数量：" + itemsnum);

                            if (actionflag > 0 && (itemsnum != null && itemsnum.length > 0)) {
                                int updateflag = DBSql.update("UPDATE BO_EU_MYD_CEPING_UNIT_PSN SET ISSTARTCEPING = '是' " +
                                        "WHERE ID = '" + ID + "'");
                                System.out.println("更新操作的结果是：" + updateflag);
                            }


                        }
                    }
                }
            }
        }else{
            return "未查询到需要启动的记录";
        }
//		respon.put("action", "success");

        return "操作结束";
    }

}
