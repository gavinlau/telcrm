package org.eredlab.g4.telcrm.service;

import java.util.List;

import org.eredlab.g4.ccl.datastructure.Dto;

public interface TelcrmService {
	//~CustomerAction
	public Dto saveCustomer(Dto pDto);
	public List<Dto> queryCustomers(Dto dto);
	public Integer queryCustomersForManageForPageCount(Dto dto);
	public Integer queryCustomersForSailingForPageCount(Dto dto);
	public void dispatchCustomers(Dto pDto);
	
	public void deleteCustomerByID(Dto dto);//'-1:删除 0:正常'
	public Dto updateCusotmerItem(Dto pDto);
	//~SalesAction
	public Dto addSalesPlan(Dto pDto);
	public Dto deleteSalesPlan(Dto inDto);
	public Dto addSales(Dto pDto);
	public List<Dto> getCallRecords(Dto pDto);
	public Integer getCallRecordsCount(Dto dto);
	
	
	
	//~recycle
	public void callRecycleCustomers();
	//~test facility
	public void testFacility();
}
