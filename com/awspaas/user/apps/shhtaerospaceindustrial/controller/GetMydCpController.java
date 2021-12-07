package com.awspaas.user.apps.shhtaerospaceindustrial.controller;

import java.util.List;
import java.util.Map;
import com.actionsoft.bpms.commons.database.ColumnMapRowMapper;
import com.actionsoft.bpms.server.SSOUtil;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.alibaba.fastjson.JSONObject;
import com.awspaas.user.apps.shhtaerospaceindustrial.util.CoreUtil;

@Controller
public class GetMydCpController {
	
	/**
	 * @Desc 根据任务分配表中的id去行车任务表中找到流程实例ID和任务实例ID和任务办理者然后查看内外租流程的单子
	 * @param uc
	 * @param
	 * @return
	 */
	@Mapping("com.awspaas.user.apps.shhtaerospaceindustrial_UnitCpOpenForm")
	public static String UnitCpOpenForm(UserContext uc, String id) {
		JSONObject result = new JSONObject();
		try {
			String processInstIdSql = "SELECT BINDID FROM BO_EU_MYD_CEPING_UNIT_HEAD WHERE SOURCEPORJECTID = '"+id+"'";
			String processInstId = CoreUtil.objToStr(DBSql.getString(processInstIdSql, "BINDID"));
			if(processInstId.equals("")) {//没有进行派单
				result.put("status", "1");
				result.put("message", "此条任务还未进行分配，请确认！");
				return result.toString();
			}
			//内租、外租流程第一节点的定义IDobj_c8f1f74f91b00001a4a53941155bca40、obj_055dc6822a5547c489578e750084c3ba
			String queryUrl = "SELECT ID,TARGET FROM WFC_TASK WHERE PROCESSINSTID = '"+processInstId+"'"
							+ "UNION SELECT ID,TARGET FROM WFH_TASK WHERE PROCESSINSTID = '"+processInstId+"' ";
			List<Map<String, Object>> urlList = DBSql.query(queryUrl, new ColumnMapRowMapper(), new Object[]{});
			if(urlList == null || urlList.isEmpty()) {
				result.put("status", "1");
				result.put("message", "此条任务还未进行分配，请确认！");
				return result.toString();
			}
			for (int i = 0; i < urlList.size(); i++) {
				Map<String, Object> urlMap = urlList.get(i);
				String taskInstId = CoreUtil.objToStr(urlMap.get("ID"));
				String target = CoreUtil.objToStr(urlMap.get("TARGET"));
				SSOUtil ssoUtil = new SSOUtil();
				//根据文件创建者创建sid，formFile.getCreateUser()为userId
				String sidPc = ssoUtil.registerClientSessionNoPassword(target, "cn", "", "pc");
				String sidMobile = ssoUtil.registerClientSessionNoPassword(target, "cn", "", "mobile");
				String portalUrl = SDK.getPortalAPI().getPortalUrl();//http://localhost:8088/portal
				String urlPc = portalUrl + "/r/w?sid="+sidPc+"&cmd=CLIENT_BPM_FORM_MAIN_PAGE_OPEN&processInstId="+processInstId+"&openState=2&taskInstId="+taskInstId+"&displayToolbar=true";
				String urlMobile = portalUrl + "/r/w?sid="+sidMobile+"&cmd=CLIENT_BPM_FORM_MAIN_PAGE_OPEN&processInstId="+processInstId+"&openState=2&taskInstId="+taskInstId+"&displayToolbar=true";
				result.put("status", "0");
				result.put("urlPc", urlPc);
				result.put("urlMobile", urlMobile);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("status", "1");
			result.put("message", e.getMessage());
		}
		return result.toString();
	}
	
	@Mapping("com.awspaas.user.apps.shhtaerospaceindustrial_PsnCpOpenForm")
	public static String PsnCpOpenForm(UserContext uc, String id) {
		JSONObject result = new JSONObject();
		try {
			String processInstIdSql = "SELECT BINDID FROM BO_EU_MYD_CEPING_HEAD WHERE SOURCEPORJECTID = '"+id+"'";
			String processInstId = CoreUtil.objToStr(DBSql.getString(processInstIdSql, "BINDID"));
			if(processInstId.equals("")) {//没有进行派单
				result.put("status", "1");
				result.put("message", "此条任务还未进行派单，请确认！");
				return result.toString();
			}
			//内租、外租流程第一节点的定义IDobj_c8f1f74f91b00001a4a53941155bca40、obj_055dc6822a5547c489578e750084c3ba
			String queryUrl = "SELECT ID,TARGET FROM WFC_TASK WHERE PROCESSINSTID = '"+processInstId+"'"
							+ "UNION SELECT ID,TARGET FROM WFH_TASK WHERE PROCESSINSTID = '"+processInstId+"' ";
			List<Map<String, Object>> urlList = DBSql.query(queryUrl, new ColumnMapRowMapper(), new Object[]{});
			if(urlList == null || urlList.isEmpty()) {
				result.put("status", "1");
				result.put("message", "此条任务还未进行派单，请确认！");
				return result.toString();
			}
			for (int i = 0; i < urlList.size(); i++) {
				Map<String, Object> urlMap = urlList.get(i);
				String taskInstId = CoreUtil.objToStr(urlMap.get("ID"));
				String target = CoreUtil.objToStr(urlMap.get("TARGET"));
				SSOUtil ssoUtil = new SSOUtil();
				//根据文件创建者创建sid，formFile.getCreateUser()为userId
				String sidPc = ssoUtil.registerClientSessionNoPassword(target, "cn", "", "pc");
				String sidMobile = ssoUtil.registerClientSessionNoPassword(target, "cn", "", "mobile");
				String portalUrl = SDK.getPortalAPI().getPortalUrl();//http://localhost:8088/portal
				String urlPc = portalUrl + "/r/w?sid="+sidPc+"&cmd=CLIENT_BPM_FORM_MAIN_PAGE_OPEN&processInstId="+processInstId+"&openState=2&taskInstId="+taskInstId+"&displayToolbar=true";
				String urlMobile = portalUrl + "/r/w?sid="+sidMobile+"&cmd=CLIENT_BPM_FORM_MAIN_PAGE_OPEN&processInstId="+processInstId+"&openState=2&taskInstId="+taskInstId+"&displayToolbar=true";
				result.put("status", "0");
				result.put("urlPc", urlPc);
				result.put("urlMobile", urlMobile);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("status", "1");
			result.put("message", e.getMessage());
		}
		return result.toString();
	}
	
	
	
	
}
