package org.eredlab.g4.telcrm.web;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.eredlab.g4.arm.service.OrganizationService;
import org.eredlab.g4.arm.service.ResourceService;
import org.eredlab.g4.arm.service.UserService;
import org.eredlab.g4.arm.util.ArmConstants;
import org.eredlab.g4.arm.vo.UserInfoVo;
import org.eredlab.g4.ccl.datastructure.Dto;
import org.eredlab.g4.ccl.datastructure.impl.BaseDto;
import org.eredlab.g4.ccl.json.JsonHelper;
import org.eredlab.g4.ccl.util.G4Utils;
import org.eredlab.g4.ccl.util.GlobalConstants;
import org.eredlab.g4.rif.util.WebUtils;
import org.eredlab.g4.rif.web.BaseAction;
import org.eredlab.g4.rif.web.CommonActionForm;
import org.eredlab.g4.telcrm.service.TelcrmService;
import org.eredlab.g4.telcrm.service.util.DateUtil;
import org.eredlab.g4.telcrm.service.util.IDManager;

/**
 * 客户管理
 * 
 * @author XiongChun
 * @since 2010-04-21
 * @see BaseAction
 */
public class SalesAction extends BaseAction {

	private TelcrmService telcrmService = (TelcrmService) super
			.getService("telcrmService");
	private UserService userService = (UserService) super
			.getService("userService");
	private OrganizationService organizationService = (OrganizationService) super
			.getService("organizationService");
	private ResourceService resourceService = (ResourceService) super
			.getService("resourceService");

	/**
	 * 用户管理与授权页面初始化
	 * 
	 * @param
	 * @return
	 */
	public ActionForward salesInit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		super.removeSessionAttribute(request, "deptid");
		Dto inDto = new BaseDto();
		String deptid = super.getSessionContainer(request).getUserInfo()
				.getDeptid();
		String userid = getSessionContainer(request).getUserInfo().getUserid();
		inDto.put("deptid", deptid);
		Dto outDto = organizationService.queryDeptinfoByDeptid(inDto);
		request.setAttribute("rootUserid", userid);
		UserInfoVo userInfoVo = getSessionContainer(request).getUserInfo();
		request.setAttribute("login_account", userInfoVo.getAccount());
		return mapping.findForward("manageMyCustomerView");
	}
	
	/**
	 * 电话营销初始化
	 * 
	 * @param
	 * @return
	 */
	public ActionForward salingInit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		super.removeSessionAttribute(request, "deptid");
		Dto inDto = new BaseDto();
		String deptid = super.getSessionContainer(request).getUserInfo()
				.getDeptid();
		String userid = getSessionContainer(request).getUserInfo().getUserid();
		inDto.put("deptid", deptid);
		Dto outDto = organizationService.queryDeptinfoByDeptid(inDto);
		request.setAttribute("rootUserid", userid);
		UserInfoVo userInfoVo = getSessionContainer(request).getUserInfo();
		request.setAttribute("login_account", userInfoVo.getAccount());
		return mapping.findForward("manageSalingView");
	}

	/**
	 * 部门管理树初始化
	 * 
	 * @param
	 * @return
	 */
	public ActionForward customerTypeTreeInit(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Dto dto = new BaseDto();
		dto.put("queryParam", "CUSTOMER_STATUS");
		List<Dto> statusList = resourceService.getParamListByParam(dto);
		//
		String parentid = getSessionContainer(request).getUserInfo()
				.getUserid();
		for (int i = 0; i < statusList.size(); i++) {
			String codedesc = statusList.get(i).get("codedesc").toString();
			String codeid = statusList.get(i).get("code").toString();

			statusList.get(i).put("leaf", true);
			statusList.get(i).put("leaf", true);
			statusList.get(i).put("text", codedesc);
			statusList.get(i).put("id", codeid);
			statusList.get(i).put("parentid", parentid);
		}
		//
		String jsonString = JsonHelper.encodeObject2Json(statusList);
		super.write(jsonString, response);
		return mapping.findForward(null);
	}

	/**
	 * 找出该部门下的所有员工
	 * 
	 * @param
	 * @return
	 */
	public ActionForward cascadeUserFromDepartment(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CommonActionForm aForm = (CommonActionForm) form;
		Dto dto = aForm.getParamAsDto(request);
		List userList = g4Reader.queryForList("queryUsersForManage", dto);// from
																			// basic
		String jsonString = JsonHelper.encodeObject2Json(userList);
		super.write(jsonString, response);
		return mapping.findForward(null);
	}

	/**
	 * 查询用户列表
	 * 
	 * @param
	 * @return
	 */
	public ActionForward queryCustomerForManage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CommonActionForm aForm = (CommonActionForm) form;
		Dto dto = aForm.getParamAsDto(request);
		String userid = getSessionContainer(request).getUserInfo().getUserid();
		
		
		String stautsid = request.getParameter("status");
		if (G4Utils.isNotEmpty(stautsid)) {
			setSessionAttribute(request, "status", stautsid);
		}
		
		if (!G4Utils.isEmpty(request.getParameter("firstload"))) {
			//dto.put("stauts", super.getSessionContainer(request).getUserInfo().getDeptid());
		} else {
			dto.put("status", super.getSessionAttribute(request, "status"));
		}	
       
		dto.put("ownerid", userid);
		
		Object statusObj = dto.get("status");
		
         //tree root click 
		if (statusObj != null) {
			String status = dto.get("status").toString();
			if (status.equals(userid)) {
				dto.remove("status");
			}
		}
		
		//query conditions
		String contacted = request.getParameter("contacted");
		String timegap = request.getParameter("timegap");
		if(contacted!=null && !contacted.equals(""))
		{
			if(contacted.equals("1") && timegap!=null && !timegap.equals(""))
			{
				dto.put("contacted_true", "contacted_true");
				if(timegap!=null && !timegap.equals(""))
				{
					int tg=Integer.parseInt(timegap);
					java.util.Calendar c = java.util.Calendar.getInstance();
					String toCallLastTime=DateUtil.convertCalendarToDateString(c)+" 23:59:59";//now
					c.add(Calendar.DAY_OF_MONTH, tg);
					String fromLastCallTime=DateUtil.convertCalendarToDateString(c)+" 23:59:59";
					System.out.println(fromLastCallTime+":"+toCallLastTime);
					dto.put("fromLastCallTime", fromLastCallTime);
					dto.put("toCallLastTime", toCallLastTime);
					
				}
			}
			else if(contacted.equals("2")&& timegap!=null && !timegap.equals(""))
			{
				    dto.put("contacted_false", "contacted_false");
					int tg=Integer.parseInt(timegap);
					java.util.Calendar c = java.util.Calendar.getInstance();
					String toCallLastTime=DateUtil.convertCalendarToDateString(c)+" 23:59:59";//now
					c.add(Calendar.DAY_OF_MONTH, tg);
					String fromLastCallTime=DateUtil.convertCalendarToDateString(c)+" 23:59:59";
					System.out.println(fromLastCallTime+":"+toCallLastTime);
					dto.put("fromLastCallTime", fromLastCallTime);
					dto.put("toCallLastTime", toCallLastTime);
				
			}
			else if(contacted.equals("0"))
			{
				//Nothing to do
			}
		}
		//and between t1 and t2
		//
		
		
		List customerList = telcrmService.queryCustomers(dto);
		Integer pageCount = telcrmService
				.queryCustomersForManageForPageCount(dto);
		
		String jsonString = JsonHelper.encodeList2PageJson(customerList, pageCount,
				GlobalConstants.FORMAT_Date);
		write(jsonString, response);
		return mapping.findForward(null);
	}
	
	
	/**
	 * 查询销售对象
	 * 
	 * @param
	 * @return
	 */
	public ActionForward querySalingForManage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CommonActionForm aForm = (CommonActionForm) form;
		Dto dto = aForm.getParamAsDto(request);
		
		String userid = getSessionContainer(request).getUserInfo().getUserid();
		
		String stautsid = request.getParameter("status");
		if (G4Utils.isNotEmpty(stautsid)) {
			setSessionAttribute(request, "status", stautsid);
		}
		
		if (!G4Utils.isEmpty(request.getParameter("firstload"))) {
			//dto.put("stauts", super.getSessionContainer(request).getUserInfo().getDeptid());
		} else {
			dto.put("status", super.getSessionAttribute(request, "status"));
		}	
		
		

		dto.put("ownerid", userid);
		
		Object statusObj = dto.get("status");
         //tree root click 
		if (statusObj != null) {
			String status = dto.get("status").toString();
			if (status.equals(userid)) {
				dto.remove("status");
			}
		}
		dto.put("salesplanid", "salesplanid");
		
		List customerList = telcrmService.queryCustomers(dto);
		Integer pageCount = telcrmService
				.queryCustomersForSailingForPageCount(dto);
		String jsonString = JsonHelper.encodeList2PageJson(customerList, pageCount,
				null);
		write(jsonString, response);
		return mapping.findForward(null);
	}

	/**
	 * 保存客户
	 * 
	 * @param
	 * @return
	 */
	public ActionForward saveCustomerItem(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CommonActionForm aForm = (CommonActionForm) form;
		Dto inDto = aForm.getParamAsDto(request);
		UserInfoVo userInfoVo = getSessionContainer(request).getUserInfo();
		
		inDto.put("deptid", userInfoVo.getDeptid());
        inDto.put("ownerid", userInfoVo.getUserid());
        inDto.put("sgusrid", userInfoVo.getUserid());
		inDto.put("inusrid", userInfoVo.getUserid());
		inDto.put("intime", new Date());
		inDto.put("deleted", 0);
		Dto outDto = telcrmService.saveCustomer(inDto);
		String jsonString = JsonHelper.encodeObject2Json(outDto);
		write(jsonString, response);
		return mapping.findForward(null);
	}
	/**
	 * 修改客户
	 * 
	 * @param
	 * @return
	 */
	public ActionForward updateCustomerItem(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CommonActionForm aForm = (CommonActionForm) form;
		Dto inDto = aForm.getParamAsDto(request);
		telcrmService.updateCusotmerItem(inDto);
		Dto outDto = new BaseDto();
		outDto.put("success", new Boolean(true));
		outDto.put("msg", "更新成功!");
		String jsonString = JsonHelper.encodeObject2Json(outDto);
		write(jsonString, response);
		return mapping.findForward(null);
	}
	/**
	 * 删除客户
	 * 
	 * @param
	 * @return
	 */
	public ActionForward deleteCustomerItems(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String strChecked = request.getParameter("strChecked");
		Dto inDto = new BaseDto();
		inDto.put("strChecked", strChecked);
		telcrmService.deleteCustomerByID(inDto);
		Dto outDto = new BaseDto();
		outDto.put("success", new Boolean(true));
		outDto.put("msg", "客户数据删除成功!");
		write(outDto.toJson(), response);
		return mapping.findForward(null);
	}
	
	/**
	 * 添加客户到营销计划
	 * 
	 * @param
	 * @return
	 */
	public ActionForward addCustomersToSalePlan(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String custid = request.getParameter("custid");
		Dto inDto = new BaseDto();
		UserInfoVo userInfoVo = getSessionContainer(request).getUserInfo();
		inDto.put("custid", custid);
		inDto.put("userid", userInfoVo.getUserid());
		telcrmService.addSalesPlan(inDto);
		return mapping.findForward(null);
	}

	/**
	 * 保存销售记录
	 */
	public ActionForward addSalesItem(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CommonActionForm aForm = (CommonActionForm) form;
		Dto inDto = aForm.getParamAsDto(request);
		UserInfoVo userInfoVo = getSessionContainer(request).getUserInfo();
		inDto.put("userid", userInfoVo.getUserid());
		//
		Object objCustID=inDto.get("id");
		if(objCustID!=null)
		{
			String custid=objCustID.toString();
			inDto.put("custid", custid);
		}
		Object objRecallTime=inDto.get("reCallTime");
		if(objRecallTime!=null)
		{
			if(objRecallTime.toString().equals(""))
			{
				inDto.put("reCallTime", null);
			}
		}
		
		Date callTime=new Date();
		inDto.put("callTime", callTime);
		//
		telcrmService.addSales(inDto);
		//Delete it from sales plan
		telcrmService.deleteSalesPlan(inDto);
		
		Dto outDto =new BaseDto();
		outDto.put("msg", "数据保存成功");
		outDto.put("success", new Boolean(true));
		String jsonString = JsonHelper.encodeObject2Json(outDto);
		write(jsonString, response);
		return mapping.findForward(null);
	}
	
	public ActionForward querySalesRecords(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CommonActionForm aForm = (CommonActionForm) form;
		Dto inDto = aForm.getParamAsDto(request);
		String custid=request.getParameter("custid");
		inDto.put("custid", custid);
		List userList = telcrmService.getCallRecords(inDto);
		Integer pageCount = telcrmService.getCallRecordsCount(inDto);
		String jsonString = JsonHelper.encodeList2PageJson(userList, pageCount,
				GlobalConstants.FORMAT_Date);
		write(jsonString, response);
		return mapping.findForward(null);
	}
	
}
