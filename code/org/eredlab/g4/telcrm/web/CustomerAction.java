package org.eredlab.g4.telcrm.web;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.eredlab.g4.arm.service.OrganizationService;
import org.eredlab.g4.arm.service.UserService;
import org.eredlab.g4.arm.util.ArmConstants;
import org.eredlab.g4.arm.vo.UserInfoVo;
import org.eredlab.g4.ccl.datastructure.Dto;
import org.eredlab.g4.ccl.datastructure.impl.BaseDto;
import org.eredlab.g4.ccl.json.JsonHelper;
import org.eredlab.g4.ccl.util.G4Utils;
import org.eredlab.g4.rif.util.WebUtils;
import org.eredlab.g4.rif.web.BaseAction;
import org.eredlab.g4.rif.web.CommonActionForm;
import org.eredlab.g4.telcrm.service.TelcrmService;

/**
 * 客户管理
 * 
 * @author XiongChun
 * @since 2010-04-21
 * @see BaseAction
 */
public class CustomerAction extends BaseAction {
	
	private TelcrmService telcrmService = (TelcrmService) super.getService("telcrmService");
	private UserService userService = (UserService) super.getService("userService");
	private OrganizationService organizationService = (OrganizationService) super.getService("organizationService");


	/**
	 * 用户管理与授权页面初始化
	 * 
	 * @param
	 * @return
	 */
	public ActionForward customerInit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		super.removeSessionAttribute(request, "deptid");
		Dto inDto = new BaseDto();
		String deptid = super.getSessionContainer(request).getUserInfo().getDeptid();
		inDto.put("deptid", deptid);
		Dto outDto = organizationService.queryDeptinfoByDeptid(inDto);
		request.setAttribute("rootDeptid", outDto.getAsString("deptid"));
		request.setAttribute("rootDeptname", outDto.getAsString("deptname"));
		UserInfoVo userInfoVo = getSessionContainer(request).getUserInfo();
		request.setAttribute("login_account", userInfoVo.getAccount());
		return mapping.findForward("manageCustomerView");
	}

	/**
	 * 部门管理树初始化
	 * 
	 * @param
	 * @return
	 */
	public ActionForward departmentTreeInit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Dto dto = new BaseDto();
		String nodeid = request.getParameter("node");
		dto.put("parentid", nodeid);
		Dto outDto = organizationService.queryDeptItems(dto);
		write(outDto.getAsString("jsonString"), response);
		return mapping.findForward(null);
	}
    
	/**
	 * 找出该部门下的所有员工
	 * 
	 * @param
	 * @return
	 */
	public ActionForward cascadeUserFromDepartment(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CommonActionForm aForm = (CommonActionForm) form;
		Dto dto = aForm.getParamAsDto(request);
		List userList = g4Reader.queryForList("queryUsersForManage", dto);//from basic
		String jsonString = JsonHelper.encodeObject2Json(userList);
		super.write(jsonString, response);
		return mapping.findForward(null);
	}
    
	/**
	 * 查询客户列表
	 * 
	 * @param
	 * @return
	 */
	public ActionForward queryCustomerForManage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CommonActionForm aForm = (CommonActionForm)form;
		Dto dto = aForm.getParamAsDto(request);
		String deptid = request.getParameter("deptid");
		if (G4Utils.isNotEmpty(deptid)) {
			setSessionAttribute(request, "deptid", deptid);
		}
		if (!G4Utils.isEmpty(request.getParameter("firstload"))) {
			dto.put("deptid", super.getSessionContainer(request).getUserInfo().getDeptid());
		} else {
			dto.put("deptid", super.getSessionAttribute(request, "deptid"));
		}
		//Query conditions
		String name=request.getParameter("name");
        String ifRecycled=request.getParameter("ifRecycled");
		String owner=request.getParameter("owner");
		String linkname=request.getParameter("linkname");
		
		
		if(name==null||name.equals(""))
		{
			dto.remove("name");
		}
		
		if(ifRecycled==null||ifRecycled.equals(""))
		{
			dto.remove("ifRecycled");
		}
		else
		{
			   dto.put("deleted", ifRecycled);
		}
		
		if(owner==null||owner.equals(""))
		{
			dto.remove("owner");
		}
		else
		{
			dto.put("ownername", owner);
		}
		
		if(linkname==null||linkname.equals(""))
		{
			dto.remove("linkname");
		}
		
		System.out.println(name+":"+ifRecycled+":"+owner+":"+linkname);
		
		List customerList = telcrmService.queryCustomers(dto);
		Integer pageCount = telcrmService.queryCustomersForManageForPageCount(dto);
		String jsonString = JsonHelper.encodeList2PageJson(customerList, pageCount, null);
		write(jsonString, response);
		return mapping.findForward(null);
	}

	/**
	 * 保存客户
	 * 
	 * @param
	 * @return
	 */
	public ActionForward saveCustomerItem(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CommonActionForm aForm = (CommonActionForm) form;
		Dto inDto = aForm.getParamAsDto(request);
		UserInfoVo userInfoVo = getSessionContainer(request).getUserInfo();
		
		inDto.put("inusrid", userInfoVo.getUserid());
		inDto.put("intime", new Date());
		inDto.put("deleted", 0);
		Dto outDto = telcrmService.saveCustomer(inDto);
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
	public ActionForward deleteCustomerItems(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String strChecked = request.getParameter("strChecked");
		Dto inDto = new BaseDto();
		inDto.put("strChecked", strChecked);
		telcrmService.deleteCustomerByID(inDto);
		Dto outDto = new BaseDto();
		outDto.put("success", new Boolean(true));
		outDto.put("msg", "用户数据删除成功!");
		write(outDto.toJson(), response);
		return mapping.findForward(null);
	}

	/**
	 * 修改客户
	 * 
	 * @param
	 * @return
	 */
	public ActionForward updateCustomerItem(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CommonActionForm aForm = (CommonActionForm) form;
		Dto inDto = aForm.getParamAsDto(request);
		telcrmService.updateCusotmerItem(inDto);
		Dto outDto = new BaseDto();
		outDto.put("success", new Boolean(true));
		outDto.put("msg", "客户数据修改成功!");
		write(outDto.toJson(), response);
		return mapping.findForward(null);
	}
	
	/**
	 * 删除客户
	 * 
	 * @param
	 * @return
	 */
	public ActionForward dispatchCustomers(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String empChecked = request.getParameter("empChecked");
		String custChecked = request.getParameter("custChecked");
		if(empChecked==null || empChecked.equals("") || custChecked==null || custChecked.equals(""))
		{
			Dto outDto = new BaseDto();
			outDto.put("failure", new Boolean(false));
			outDto.put("msg", "请选中客户与员工!");
			write(outDto.toJson(), response);
			return mapping.findForward(null);
		}
		
		Dto inDto = new BaseDto();
		inDto.put("empChecked", empChecked);
		inDto.put("custChecked", custChecked);
		telcrmService.dispatchCustomers(inDto);
		Dto outDto = new BaseDto();
		outDto.put("success", new Boolean(true));
		outDto.put("msg", "客户分配成功!");
		write(outDto.toJson(), response);
		return mapping.findForward(null);
	}
}
