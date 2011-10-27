package org.eredlab.g4.telcrm.service.impl;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.sourceforge.jtds.jdbcx.JtdsDataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;
import org.eredlab.g4.arm.service.OrganizationService;
import org.eredlab.g4.arm.util.ArmConstants;
import org.eredlab.g4.arm.util.idgenerator.IDHelper;
import org.eredlab.g4.arm.util.idgenerator.IdGenerator;
import org.eredlab.g4.bmf.base.BaseServiceImpl;
import org.eredlab.g4.bmf.util.SpringBeanLoader;
import org.eredlab.g4.ccl.datastructure.Dto;
import org.eredlab.g4.ccl.datastructure.impl.BaseDto;
import org.eredlab.g4.ccl.util.G4Utils;
import org.eredlab.g4.telcrm.service.TelcrmService;
import org.eredlab.g4.telcrm.service.util.DateUtil;
import org.eredlab.g4.telcrm.service.util.IDManager;
import org.eredlab.g4.telcrm.service.util.MiscUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.util.Assert;

import com.ctc.wstx.util.DataUtil;

public class TelcrmServiceImpl extends BaseServiceImpl implements TelcrmService {

	// ~Customer
	@Override
	public Dto saveCustomer(Dto pDto) {
		Dto outDto = new BaseDto();
		pDto.put("id", IDManager.getCustomerID());
		g4Dao.insert("insert_telcrmCustomer", pDto);
		outDto.put("msg", "用户数据新增成功");
		outDto.put("success", new Boolean(true));
		return outDto;
	}

	@Override
	public List<Dto> queryCustomers(Dto dto) {
		List customerList = g4Dao.queryForPage("telcrmCustomer_selectByExample",
				dto);
		//
		return customerList;
	}

	@Override
	public Integer queryCustomersForManageForPageCount(Dto dto) {
		
		Integer pageCount = (Integer) g4Dao.queryForObject(
				"queryCustomersForSailingForPageCount", dto);
		return pageCount;
	}

	@Override
	public Integer queryCustomersForSailingForPageCount(Dto dto) {
		
		Integer pageCount = (Integer) g4Dao.queryForObject(
				"queryCustomersForSailingForPageCount", dto);
		return pageCount;
	}
	
	
	
	@Override
	public void deleteCustomerByID(Dto pDto) {
		Dto dto = new BaseDto();
		String[] arrChecked = pDto.getAsString("strChecked").split(",");
		for (int i = 0; i < arrChecked.length; i++) {
			dto.put("id", arrChecked[i]);
			g4Dao.update("deleteCustomerInCustomerManage",dto);
		}
	}
	

	@Override
	public Dto updateCusotmerItem(Dto pDto) {
		g4Dao.update("updateCustomerItem", pDto);
		return null;
	}
	
	

	@Override
	public Dto addSalesPlan(Dto pDto) {
		pDto.put("id", IDManager.getSalesPlanID());
		g4Dao.insert("insert_salesPlan", pDto);
		return null;
	}

	
	
	@Override
	public Dto addSales(Dto pDto) {
		String salesid=IDManager.getSalesID();
		pDto.put("id", salesid);
		g4Dao.insert("insert_sales", pDto);
		
		//update customer last call time for recycling
		Dto custDto=new BaseDto();
		custDto.put("id", pDto.get("custid"));
		custDto.put("lastcalltime", new Date());
		g4Dao.update("updateCustomerItem", custDto);
		return null;
	}
	

	@Override
	public List<Dto> getCallRecords(Dto pDto) {
		List salesList = g4Dao.queryForPage("telcrmCustomer_SalesRecords", pDto);
		return salesList;
	}

	@Override
	public Integer getCallRecordsCount(Dto dto) {
		Integer pageCount = (Integer) g4Dao.queryForObject(
				"telcrmCustomer_getCallRecordsCount", dto);
		return pageCount;
	}
	

	@Override
	public Dto deleteSalesPlan(Dto inDto) {
		g4Dao.delete("telcrmSales_deleteSalesPlan", inDto);
		return null;
	}
//
	public synchronized Dto saveDeptItem(Dto pDto) {
		String deptid = IdGenerator.getDeptIdGenerator(pDto.getAsString("parentid"));
		pDto.put("deptid", deptid);
		pDto.put("leaf", ArmConstants.LEAF_Y);
		// MYSQL下int类型字段不能插入空字符
		pDto.put("sortno",
				G4Utils.isEmpty(pDto.getAsString("sortno")) ? Integer.valueOf("0") : pDto.getAsString("sortno"));
		g4Dao.insert("telcrm_saveDeptItem", pDto);
		Dto updateDto = new BaseDto();
		updateDto.put("deptid", pDto.getAsString("parentid"));
		updateDto.put("leaf", ArmConstants.LEAF_N);
		g4Dao.update("updateLeafFieldInEaDept", updateDto);
		return null;
	}
	
	public Dto saveUserItem(Dto pDto) {
		Dto outDto = new BaseDto();
		Integer temp = (Integer) g4Dao.queryForObject("checkAccount", pDto);
		if (temp.intValue() != 0) {
			outDto.put("msg", "登录账户" + outDto.getAsString("account") + "已被占用,请尝试其它帐户!");
			outDto.put("success", new Boolean(false));
			return outDto;
		}
		String password = pDto.getAsString("password");
		//String mPasswor = G4Utils.encryptBasedDes(password);
		//pDto.put("password", mPasswor);
		g4Dao.insert("telcrm_saveUserItem", pDto);
		g4Dao.insert("telcrm_saveEausersubinfoItem", pDto);
		outDto.put("msg", "用户数据新增成功");
		outDto.put("success", new Boolean(true));
		return outDto;
	}
	
	
	/**
	 * 保存角色用户关联信息
	 * @param pDto
	 * @return
	 */
	public Dto saveSelectUser(Dto pDto){
			pDto.put("authorizeid", IDHelper.getAuthorizeid4User());
			g4Dao.insert("telcrm_saveSelectUser", pDto);
		    return null;
	}
	@Override
	public void dispatchCustomers(Dto pDto) {
               System.out.println(pDto.getAsString("empChecked"));
               System.out.println(pDto.getAsString("custChecked"));
               
               String custs=pDto.getAsString("custChecked");
               String emps=pDto.getAsString("empChecked");
               
               
               String[] custids=custs.split(",");
               List<String> custslist=MiscUtils.shuffleArrayToList(custids);
             
               
              
               String[] empids=emps.split(",");
               List<String> emplist=MiscUtils.shuffleArrayToList(empids);
               
               int custssize=custslist.size();
               int custcounter=0;
               
               int empsize=emplist.size();
                
               System.out.println(empsize+":"+custssize);
            	   int round=custssize/empsize;
            	   
            	   for(int i=0;i<round;i++)
            	   {
            		  for(int j=0;j<empsize;j++)
            		  {
            			  Dto paramDto=new BaseDto();
                 		  paramDto.put("ownerid", emplist.get(j));
       
                 		  Dto userParam=new BaseDto();
                 		  userParam.put("userid",  emplist.get(j));
    					  List<Dto> users=g4Dao.queryForList("telcrm_queryUsersForManage", userParam);
    					  Assert.isTrue(users.size()==1);
    					  paramDto.put("deptid", users.get(0).get("deptid"));
    					  
    					  
                 		  paramDto.put("id", custslist.get(custcounter));
                 		  
                 		  System.out.println(emplist.get(j)+"R"+ custslist.get(custcounter));
                 		  g4Dao.update("updateCustomerItem", paramDto);
                 		  //
                 		  custcounter++;
            		  }
            	   }
            	   
            	   int consumed=round*empsize;
            	   int lefted=custssize-consumed;
            	   for(int i=0;i<lefted;i++)
             	  {
             		  Dto paramDto=new BaseDto();
             		  paramDto.put("ownerid", emplist.get(i));
             		  
             		  Dto userParam=new BaseDto();
            		  userParam.put("userid",  emplist.get(i));
					  List<Dto> users=g4Dao.queryForList("telcrm_queryUsersForManage", userParam);
					  Assert.isTrue(users.size()==1);
					  paramDto.put("deptid", users.get(0).get("deptid"));
					  
             		  paramDto.put("id", custslist.get(custcounter));
             		  
             		  
             		  
             		  System.out.println(emplist.get(i)+"L"+ custslist.get(custcounter));
             		  g4Dao.update("updateCustomerItem", paramDto);
             		  custcounter++;
             	  }
	}
	
//	
@Override
	public void callRecycleCustomers() {
		System.out.println("recycle starting......");
		//
		System.out.println("synchronizing dept......");
		Object  telcrm_external_jdbcTemplate = SpringBeanLoader.getSpringBean("telcrm_external_jdbcTemplate");
	    JdbcTemplate jt=(JdbcTemplate)telcrm_external_jdbcTemplate;
	    String str_dept="select id,departmentname from department";
	    jt.query(str_dept, new Object[]{},new RowCallbackHandler(){
			@Override
			public void processRow(ResultSet rs) throws SQLException {
			   String bid=rs.getString("id");
			   String deptname=rs.getString("departmentname");
			   //String managerid=rs.getString("managerid");
			   
			   Dto deptParam=new BaseDto();
			   deptParam.put("bid", bid);
			   List<Dto> depts=g4Dao.queryForList("telcrm_queryDeptsForManage", deptParam);
			   if(depts.size()==0)
			   {
				   Dto tmp=new BaseDto();
				   tmp.put("parentid", "001003");
				   tmp.put("deptname", deptname);
				   tmp.put("bid", bid);
				   saveDeptItem(tmp);
			   }
			   else if(depts.size()==1)
			   {
				   Dto tmp=new BaseDto();
				   tmp.put("deptname", deptname);
				   tmp.put("bid", bid);
				   g4Dao.update("telcrm_updateDeptItem", tmp);
			   }
			   else
			   {
				   throw new RuntimeException("部门数据不正确");
			   }
			   
			}
	    });
	    //==================================================================================== 
		System.out.println("synchronizing employees......");
		 String str_emp="select id,no,employename,departmentid,password,status from employe";
		 jt.query(str_emp, new Object[]{},new RowCallbackHandler(){
				@Override
				public void processRow(ResultSet rs) throws SQLException {
					String bid=rs.getString("id");
					String no=rs.getString("no");
					String employename=rs.getString("employename");
					String departmentid=rs.getString("departmentid");
					String password=rs.getString("password");
					String status=rs.getString("status");
					
					 Dto employeeParam=new BaseDto();
					 employeeParam.put("bid", bid);
					 List<Dto> emps=g4Dao.queryForList("telcrm_queryUsersForManage", employeeParam);
					 if(emps.size()==0)
					   {
						
						   Dto tmp=new BaseDto();
						   String userid=IDHelper.getUserID();
						   tmp.put("userid", userid);
						   tmp.put("username", employename);
						   
						   tmp.put("account", no);
						   tmp.put("password", password);
						   //
						   Dto deptParam=new BaseDto();
						   deptParam.put("bid", departmentid);
						   List<Dto> depts=g4Dao.queryForList("telcrm_queryDeptsForManage", deptParam);
						   System.out.println(employename+":"+departmentid+":"+userid);
						   if(depts.size()==1)
						   {
							   String deptid=depts.get(0).get("deptid").toString();
							   tmp.put("deptid", deptid);
						   }
						   else
						   {
							   throw new RuntimeException("部门数据不正确");
						   }
						   //
						   tmp.put("locked", 0);
						   tmp.put("usertype", 1);
						   tmp.put("sex", 0);
						   tmp.put("bid", bid);
						   saveUserItem(tmp);
						   //
						   //
						   Dto grantRoleDto=new BaseDto();
						   grantRoleDto.put("userid", userid); 
						   grantRoleDto.put("roleid", "10000062");
						   saveSelectUser(grantRoleDto);
						   //
						   
					   }
					   else if(emps.size()==1)
					   {
						  
					   }
					   else
					   {
						   throw new RuntimeException("部门数据不正确");
					   }
					 
				}
		    });
		
		System.out.println("synchronizing customers......");
		String str_cust="select id,customerName,tel,fax,email,addressDetail,employeid,insertuser,inserttime,updateuser,comment from customer";
		jt.query(str_cust, new RowCallbackHandler(){
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				   String bid=rs.getString("id");
				   String customerName=rs.getString("customerName");
				   String tel=rs.getString("tel");
				   String fax=rs.getString("fax");
				   String email=rs.getString("email");
				   String addressDetail=rs.getString("addressDetail");
				   String employeid=rs.getString("employeid");
				   String insertuser=rs.getString("insertuser");
				   String inserttime=rs.getString("inserttime");
				   String updateuser=rs.getString("updateuser");
				   String comment=rs.getString("comment");
				   
				   Dto customerParam=new BaseDto();
				   customerParam.put("bid", bid);
				   List<Dto> custs=g4Dao.queryForList("telcrm_selectCustomers", customerParam);
				   if(custs.size()==0)
				   {

					   //id, name, linkname, tel, phone, fax, email, addr, status, ownerid,
					   //      deptid, recycletime, sgusrid, inusrid, intime, lastcalltime, lstupdateusrid, deleted, remark
					   Dto tmp=new BaseDto();
					   tmp.put("name", customerName);
					   
					   //
					   Object  telcrm_external_jdbcTemplate2 = SpringBeanLoader.getSpringBean("telcrm_external_jdbcTemplate");
					   JdbcTemplate jt2=(JdbcTemplate)telcrm_external_jdbcTemplate2;
					   
					   String sql_linkMan="select customerid,linkmanname,mobileTel from linkman where customerid='"+bid+"'";
					   List rows=jt2.queryForList(sql_linkMan);
					   if(rows.size()>0)
					   {
						   //
						   Map row=(Map)rows.get(0);
						   //
						 
						   //
						   String linkmanname=null;
						   String mobileTel=null;
						   if(row.get("linkmanname")!=null)
						       linkmanname=row.get("linkmanname").toString();
						   
						   if(row.get("mobileTel")!=null)
						        mobileTel=row.get("mobileTel").toString();
						   
						   tmp.put("linkname", linkmanname);
						   tmp.put("phone", mobileTel);
					   }
					   //deptid employeid
					   Dto employeeParam=new BaseDto();
					   employeeParam.put("bid", employeid);
					   List<Dto> emps=g4Dao.queryForList("telcrm_queryUsersForManage", employeeParam);
					   if(emps.size()==1)
					   {
						   tmp.put("deptid", emps.get(0).get("deptid"));
						   tmp.put("ownerid", emps.get(0).get("userid"));
						   tmp.put("sgusrid", emps.get(0).get("userid"));
						   tmp.put("inusrid", emps.get(0).get("userid"));
						   tmp.put("lstupdateusrid", emps.get(0).get("userid"));
					   }
					   else
					   {
						  // throw new RuntimeException("数据不完整");
					   }
					   
					   tmp.put("linkname", customerName);
					  // tmp.put("phone", phone);
					   
					   tmp.put("tel", tel);
					   tmp.put("fax", fax);
					   tmp.put("email", email);
					   tmp.put("addr", addressDetail);
					   //
					   tmp.put("intime", inserttime);
					   tmp.put("remark", comment);
					   tmp.put("deleted", "0");//-2 -1 0
					   tmp.put("lastcalltime", "2011-05-30");
					   tmp.put("bid", bid);
					   tmp.put("status", 5);
					   //
					   saveCustomer(tmp);
				   }
				   else if(custs.size()==1)
				   {
					   
				   }
				   else
				   {
					   throw new RuntimeException("客户数据不正确");
				   }
				   
			}
		});
		System.out.println("clean status......");
		//
		
		
		//checking status 2
		//find all customer status is changed to 2 from ext sys a with bizids;
		//change the status with these ids;
		String sqlContractIn="select id from customer where id in (select distinct customerId from contract where status=4 or status=8)";
		jt.query(sqlContractIn, new RowCallbackHandler(){
			@Override
			public void processRow(ResultSet rs) throws SQLException {
                 String bid=rs.getString("id");
                 Dto tmp=new BaseDto();
                 tmp.put("bid", bid);
				 tmp.put("status", 2);
				g4Dao.update("updateCustomerItem2", tmp);
			}
		});
		//checking status 1
		//find all customer status is changed to 1 from ext sys a with bizids; with finished date
		//change the status with these ids;
		//FIXME:status 1
		
		
		System.out.println("recycling......");
		Dto condtions=new BaseDto();
		List<Dto> all_customers=g4Dao.queryForList("telcrmCustomer_selectByExample", condtions);
		//check the normal
		//rule 1 data : [['线索客户', '5'],['意向客户', '4'], ['待成交客户', '3'],['无效用户', '0']]
		
		//get all customers exclude deleted=0
		//update each deleted=-2 according the status
		for(Dto cust :all_customers)
		{
			Dto dto=null;
			Object objCust=cust.get("status");
			if(objCust!=null && !objCust.equals(""))
			{
				Date dnow=null;
				Date tdate=null;
				java.util.Calendar now = java.util.Calendar.getInstance();
				String strNow=DateUtil.convertCalendarToDateString(now);//now
				
				DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
				try {
					 dnow=df.parse(strNow);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				Object objlct=cust.get("lastcalltime");
				try {
					tdate=df.parse(objlct.toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				 long diff = dnow.getTime() - tdate.getTime();
				 long days = diff / (1000 * 60 * 60 * 24);
				
				
				//分配后时间统一调整为分配当天
				//recycletime 分配时的时间
				int custstatus=Integer.parseInt(objCust.toString());
				boolean flag=(custstatus==5 && days>=MiscUtils.CUSTOMER_STATUS_CLUE_RECICLE_DAY) ||
				             (custstatus==4 && days>=MiscUtils.CUSTOMER_STATUS_INTENTION_RECICLE_DAY) ||
				             (custstatus==3 && days>=MiscUtils.CUSTOMER_STATUS_TOSALE_RECICLE_DAY) || 
				             (custstatus==1 && days>=MiscUtils.CUSTOMER_STATUS_CONTRACTOVER_RECICLE_DAY); 
				if(flag)
				{
					dto=new BaseDto();
					dto.put("id", cust.get("id"));
					dto.put("deleted", -2);
					dto.put("recycletime", new Date());
					g4Dao.update("updateCustomerItem", dto);
				}
				if(custstatus==2)
				{
					//NOP
				}
				 // (custstatus==2 && days>=90 && 1==1) || //合同中客户  每天做一次同步  在账客户
	            // (custstatus==1 && days>=90 && 1==1);   //可同结束客户  每天做一次同步  转为线索客户

				
			}
		}		
		System.out.println("recycle over......");
	}

	//~================================================================================	
	public void testPopluateCustomers(int num)
	{
		//insert into telcrm_customer (id, name, linkname, tel, phone, fax, email, addr, status, ownerid,       deptid, recycletime, sgusrid, inusrid, intime, lastcalltime, lstupdateusrid, deleted, remark)     values ('10000117', 'a1', 'a', 'a', 'a',       'a', 'a', 'a', '4', '10004886',       '001003002', '', '10004886', '10000001',       Mon Oct 03 21:39:12 CST 2011, '', '', 0,       'a')
	    for(int i=1;i<=num;i++)
	    {
	    	Dto dto=new BaseDto();
			dto.put("name", "客户"+i);
			dto.put("linkname", "联系人"+i);
			dto.put("tel", "电话"+i);
			dto.put("phone", "手机"+i);
			dto.put("fax", "传真"+i);
			dto.put("email", "Email"+i);
			dto.put("addr", "地址"+i);
			dto.put("status", 4);
			dto.put("ownerid", "10004886");
			dto.put("deptid", "001003002");
			//dto.put("recycletime", null);
			dto.put("sgusrid", "10004886");
			dto.put("inusrid", "10004886");
			dto.put("intime", new Date());
			//dto.put("lastcalltime", new Date());
			//dto.put("lstupdateusrid","");
			dto.put("deleted",0);
			dto.put("remark","remark"+i);
			this.saveCustomer(dto);
	    }
		
	}
	
	public void testModifyLastCallTime()
	{
		Dto custDto=new BaseDto();
		custDto.put("lastcalltime","2011-9-1 23:59:59");
		g4Dao.update("updateCustomerItem", custDto);
	}
	
	public void testPopulateSalesRecords(int num)
	{
		//INSERT INTO telcrm_records ( 		   id, userid, custid,content,callTime,reCallTime)  		VALUES ('10000264', '10004886', '10000118','we',Wed Oct 05 14:41:38 CST 2011,'')
		for(int i=1;i<=num;i++)
	    {
			Dto dto=new BaseDto();
			dto.put("userid", "10004886");
			dto.put("custid", "10000118");
			dto.put("content", "content"+i);
			dto.put("callTime", new Date());
			this.addSales(dto);
	    }
		
	}

	public void testFacility()
	{
		//this.testPopluateCustomers(200);
		//this.testPopulateSalesRecords(200);
		//this.testModifyLastCallTime();
		this.callRecycleCustomers();
	}
	
	public static void main(String[] args) {
		ApplicationContext wac = SpringBeanLoader.getApplicationContext();

		Object springBean = SpringBeanLoader.getSpringBean("telcrmService");
		TelcrmService telCRMService = (TelcrmService) (springBean);
	    //
		telCRMService.testFacility();
	
        
//        String empChecked="a";
//        String custChecked="1,2,3,4,5,6,7,8";
//        
//        Dto dto=new BaseDto();
//		dto.put("empChecked", empChecked);
//		dto.put("custChecked",custChecked);
//		
//		telCRMService.dispatchCustomers(dto);
		
	}


}
