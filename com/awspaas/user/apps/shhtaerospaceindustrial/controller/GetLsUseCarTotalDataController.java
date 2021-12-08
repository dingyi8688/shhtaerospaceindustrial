package com.awspaas.user.apps.shhtaerospaceindustrial.controller;

import com.actionsoft.bpms.commons.database.ColumnMapRowMapper;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.awspaas.user.apps.shhtaerospaceindustrial.util.CoreUtil;

import java.util.List;
import java.util.Map;

/**
 * @author DingYi
 * @date 2021年12月08日 20:47
 */
@Controller
public class GetLsUseCarTotalDataController {
    /**
     * @Description 获取指定时段的部门用用车记录-> 入参：sid|开始日期|startdate|结束日期|enddate
     * 出参:部门名称,任务数量、状态（0：成功|1：失败）、信息提示（失败时携带）
     * @date 2021年12月08日
     */
    @Mapping("com.awspaas.user.apps.shhtaerospaceindustrial_getDeptTotalData")
    public String getDeptTotalData(UserContext uc, String sid, String startdate, String enddate) {
        JSONObject returnData = new JSONObject();

        try {
            String userId = uc.getUID();


            String querySql = "select t.applydeptname,sum(t.id) as missioncount from VIEW_YQBZ_LSCARMISSIONINFO t " +
                    "where t.usecardate between '" + startdate + "' and '" + enddate + "' group by t.applydeptname ";

            List<Map<String, Object>> dataList = DBSql.query(querySql, new ColumnMapRowMapper());

            if (dataList == null || dataList.isEmpty()) {
                returnData.put("status", "0");
                JSONArray weekDataNull = new JSONArray();
                returnData.put("weekData", weekDataNull);
                returnData.put("message", "查询的时段内没有临时用车任务记录");
                return returnData.toString();
            }


            JSONArray deptArr = new JSONArray();// 部门数组
            JSONArray miscountArr = new JSONArray();// 任务数量数组

            String portalUrl = SDK.getPortalAPI().getPortalUrl();

            for (Map<String, Object> dataMap : dataList) {
//				JSONObject deptItem = new JSONObject();
                String applydeptname = CoreUtil.objToStr(dataMap.get("applydeptname"));
                String missioncount = CoreUtil.objToStr(dataMap.get("missioncount"));

                deptArr.add(applydeptname);
                miscountArr.add(missioncount);

            }


            // 成功
            returnData.put("status", "0");
            returnData.put("deptList", deptArr);
            returnData.put("missioncountList", miscountArr);

        } catch (Exception e) {
            e.printStackTrace();
            returnData.put("status", "1");
            returnData.put("message", e.getMessage());
        }

        return returnData.toString();
    }

    /**
     * @Description 获取指定时段的部门用用车记录-> 入参：sid|开始日期|startdate|结束日期|enddate,部门名称|orgname
     * 出参:用车人,用车日期,上下车地点,车辆类型,用车时长,用车里程,用车费用、状态（0：成功|1：失败）、信息提示（失败时携带）
     * @date 2021年12月08日
     */
    @Mapping("com.awspaas.user.apps.shhtaerospaceindustrial_getDeptDetialData")

    public String getDeptDetialData(UserContext uc, String sid, String startdate, String enddate, String orgname) {
        JSONObject returnData = new JSONObject();

        try {
            String userId = uc.getUID();


            String querySql = "select t.applyusername,t.usecardate,t.boardingplace,t.targetplace,t.vehicletype,t.qrlc,t.USECARTIME,t.totalmoney from VIEW_YQBZ_LSCARMISSIONINFO t" +
                    "where t.applydeptname='" + orgname + "' and  t.usecardate between '" + startdate + "' and '" + enddate + "' ";

            List<Map<String, Object>> dataList = DBSql.query(querySql, new ColumnMapRowMapper());
            if (dataList == null || dataList.isEmpty()) {
                returnData.put("status", "0");
                JSONArray deptWeekNull = new JSONArray();
                returnData.put("deptWeekList", deptWeekNull);
                returnData.put("message", "时段内未查询到临时用车任务记录");
                return returnData.toString();
            }
            JSONArray deptDetialArr = new JSONArray();

            for (Map<String, Object> dataMap : dataList) {
                JSONObject orderItem = new JSONObject();

                String applyusername = CoreUtil.objToStr(dataMap.get("applyusername"));
                String usecardate = CoreUtil.objToStr(dataMap.get("usecardate"));
                String boardingplace = CoreUtil.objToStr(dataMap.get("boardingplace"));
                String targetplace = CoreUtil.objToStr(dataMap.get("targetplace"));
                String vehicletype = CoreUtil.objToStr(dataMap.get("vehicletype"));

                double qrlc = objToDouble(dataMap.get("qrlc"));
                double USECARTIME = objToDouble(dataMap.get("USECARTIME"));
                double totalmoney = objToDouble(dataMap.get("totalmoney"));

                orderItem.put("userName", applyusername);
                orderItem.put("usecarDate", usecardate);
                orderItem.put("boardingPlace", boardingplace);
                orderItem.put("targetPlace", targetplace);
                orderItem.put("vehicleType", vehicletype);


                orderItem.put("qrlc", qrlc);
                orderItem.put("usecartime", USECARTIME);
                orderItem.put("totalmoney", totalmoney);
                deptDetialArr.add(orderItem);
            }

            // 成功
            returnData.put("status", "0");
            returnData.put("deptdataist", deptDetialArr);
        } catch (Exception e) {
            e.printStackTrace();
            returnData.put("status", "1");
            returnData.put("message", e.getMessage());
        }

        return returnData.toString();
    }
    public static Double objToDouble(Object obj) {
        return obj == null ? 0: Double.parseDouble(CoreUtil.objToStr(obj));
    }
}
