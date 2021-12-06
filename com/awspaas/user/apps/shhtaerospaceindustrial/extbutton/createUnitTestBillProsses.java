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
                    String prosuuid = "obj_f255e8bfefec4434bdd1b8c390004d98"; //��λ�������̶���ID
                    ProcessAPI proapi = SDK.getProcessAPI();
                    // ������һ������ʵ������һ������Ϊ����UUID
                    ProcessInstance processInstance = proapi.createProcessInstance(prosuuid, APPLYUSERNZH, YEARINFO +
                            "��" + SEASONINFO + "����,԰����������Ȳ���");
                    processInstance.getEndActivityId();

                    System.out.println("����ʵ�������ǣ�" + processInstance + "\n����ʵ��ID:" + processInstance.getId());
                    // ��ȡ����״̬
                    String proState = processInstance.getControlState();
                    System.out.println("��ǰ����״̬��" + proState);
                    // �������������ʵ��
                    proapi.start(processInstance, "");
                    // ������̵�taskId���������������ʵ���Ĵ���
                    String taskid1 = processInstance.getStartTaskInstId();
                    System.out.println("taskID1::" + taskid1);

                    // ׼������
                    BOAPI boAPI = SDK.getBOAPI();
                    String boname = "BO_EU_MYD_CEPING_UNIT_HEAD"; //��λ��������
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

                    String queryItemSql = "select CEPINGTYPE,CEPINGITEM,ID from BO_EU_MYD_CEPINGDOC where  ISOK='��'";
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
//                            itemRecordData.set("MANYIDU", "��������");
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
                        System.out.println("���²����Ľ���ǣ�" + updateflag);
                    }

                }

            }

        }else{
            return "δ��ѯ����Ҫ�����ļ�¼";
        }


//		respon.put("action", "success");

        return "�����ɹ�";
    }
	public static String objToStr(Object obj) {
		return obj == null ? "" : obj.toString();
	}

}
