package org.eredlab.g4.telcrm.service.util;

import org.eredlab.g4.arm.util.idgenerator.IdGenerator;
import org.eredlab.g4.ccl.id.generator.DefaultIDGenerator;

public class IDManager {
	/**
	 * 客户ID
	 */
	private static DefaultIDGenerator defaultIDGenerator_customerid = null;
	private static DefaultIDGenerator defaultIDGenerator_salesPlanid = null;
	private static DefaultIDGenerator defaultIDGenerator_salesid = null;
	static {
		IdGenerator idGenerator_customerid = new IdGenerator();
		idGenerator_customerid.setFieldname("TELCRMCUSTOMERID");
		defaultIDGenerator_customerid = idGenerator_customerid
				.getDefaultIDGenerator();
	}
	
	static {
		IdGenerator idGenerator_salesid = new IdGenerator();
		idGenerator_salesid.setFieldname("TELCRMSALESID");
		defaultIDGenerator_salesid = idGenerator_salesid
				.getDefaultIDGenerator();
	}
	
	static {
		IdGenerator idGenerator_salesPlanid = new IdGenerator();
		idGenerator_salesPlanid.setFieldname("TELCRMSALESPLANID");
		defaultIDGenerator_salesPlanid = idGenerator_salesPlanid
				.getDefaultIDGenerator();
	}

	public static String getCustomerID() {
		return defaultIDGenerator_customerid.create();
	}
	
	public static String getSalesPlanID()
	{
		return defaultIDGenerator_salesPlanid.create();
	}
	public static String getSalesID()
	{
		return defaultIDGenerator_salesid.create();
	}
}
