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


public class createUnitPsnTestBillProsses extends ValueListener {

    @Override
    public String execute(ProcessExecutionContext param) throws Exception {
        // TODO Auto-generated method stub
        ResponseObject respon = ResponseObject.newOkResponse();
        String bindid = param.getProcessInstance().getId();

        String queryDdy = "SELECT APPLYUNIT,YEARINFO,SEASONINFO FROM BO_EU_MYD_CEPING_UNIT_HEAD WHERE BINDID = '" + bindid + "'";
        String APPLYUNIT = objToStr(DBSql.getString(queryDdy, "APPLYUNIT"));
        String YEARINFO = objToStr(DBSql.getString(queryDdy, "YEARINFO"));
        String SEASONINFO = objToStr(DBSql.getString(queryDdy, "SEASONINFO"));


        System.out.println("bindid::" + bindid);
        String querySql = "select ID,DEPTNAME,PSNNAME,PSNUSERID,CELLPHONE from " +
                "BO_EU_MYD_CEPING_UNIT_PSN where " +
                "bindid='" + bindid + "' and  ISSTARTCEPING='0'";
        List<Map<String, Object>> dataList = DBSql.query(querySql, new ColumnMapRowMapper());
        if (dataList != null && !dataList.isEmpty())
        {
            for (Map<String, Object> dataMap : dataList) {
//				JSONObject orderItem = new JSONObject();
                String ID = objToStr(dataMap.get("ID"));

                String PSNNAME = objToStr(dataMap.get("PSNNAME"));
                String CELLPHONE = objToStr(dataMap.get("CELLPHONE"));
                String PSNUSERID = objToStr(dataMap.get("PSNUSERID"));
                String DEPTNAME = objToStr(dataMap.get("DEPTNAME"));

                if (PSNUSERID != null && !("").equals(PSNUSERID)) {
                    String prosuuid = "obj_244ab6ba9bb3472397e7386e76604921"; //????????????????ID
                    ProcessAPI proapi = SDK.getProcessAPI();
                    // ????????????????????????????????????UUID
                    ProcessInstance processInstance = proapi.createProcessInstance(prosuuid, PSNUSERID, YEARINFO +
                            "??" + SEASONINFO + "????,??????????????????");
                    processInstance.getEndActivityId();

                    System.out.println("????????????????" + processInstance + "\n????????ID:" + processInstance.getId());
                    // ????????????
                    String proState = processInstance.getControlState();
                    System.out.println("??????????????" + proState);
                    // ??????????????????
                    proapi.start(processInstance, "");
                    // ??????????taskId??????????????????????????
                    String taskid1 = processInstance.getStartTaskInstId();
                    System.out.println("taskID1::" + taskid1);

                    // ????????
                    BOAPI boAPI = SDK.getBOAPI();
                    String boname = "BO_EU_MYD_CEPING_HEAD"; //????????????
                    BO boRecordData = new BO();
                    boRecordData.set("APPLYUNIT", APPLYUNIT);
                    boRecordData.set("APPLYDEPTNAME", DEPTNAME);
                    boRecordData.set("APPLYUSERNAME", PSNNAME);
                    boRecordData.set("APPLYUSERZH", PSNUSERID);
                    boRecordData.set("YEARINFO", YEARINFO);
                    boRecordData.set("SEASONINFO", SEASONINFO);
                    boRecordData.set("SOURCEPORJECTID", ID);

                    int actionflag = boAPI.create(boname, boRecordData, processInstance, param.getUserContext());

                    String queryItemSql = "select CEPINGTYPE,CEPINGITEM,ID from BO_EU_MYD_CEPINGDOC where  ISOK='??'";
                    List<Map<String, Object>> itemdataList = DBSql.query(queryItemSql, new ColumnMapRowMapper()
					);

                    if (itemdataList != null && !itemdataList.isEmpty()) {
                        List<BO> items = new ArrayList<BO>();
                        String itemboname = "BO_EU_MYD_CEPING_BODY";
                        for (Map<String, Object> itemdataMap : itemdataList) {
                            String CEPINGTYPE = objToStr(itemdataMap.get("CEPINGTYPE"));
                            String CEPINGITEM = objToStr(itemdataMap.get("CEPINGITEM"));
                            BO itemRecordData = new BO();
                            itemRecordData.set("CEPINGTYPE", CEPINGTYPE);
                            itemRecordData.set("CEPINGITEM", CEPINGITEM);
                            itemRecordData.set("MANYIDU", "????????");
                            items.add(itemRecordData);
                        }
                        if (items.size() > 0) {
                            int[] itemsnum = boAPI.create(itemboname, items, processInstance,
                                    param.getUserContext());

                            System.out.println("??????????" + itemsnum);

                            if (actionflag > 0 && (itemsnum != null && itemsnum.length > 0)) {
                                int updateflag = DBSql.update("UPDATE BO_EU_MYD_CEPING_UNIT_PSN SET ISSTARTCEPING = '1' " +
                                        "WHERE ID = '" + ID + "'");
                                System.out.println("??????????????????" + updateflag);
                            }


                        }
                    }
                }
            }
        }else{
            return "??????????????????????";
        }
//		respon.put("action", "success");

        return "????????";
    }
	public static String objToStr(Object obj) {
		return obj == null ? "" : obj.toString();
	}

}
