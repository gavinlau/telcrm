<%@ page contentType="text/html; charset=utf-8"%>
<%@ include file="/common/include/taglib.jsp"%>
<eRedG4:html title="我的客户">
<eRedG4:import src="/telcrm/js/manageSaling.js"/>
<eRedG4:ext.codeStore fields="CUSTOMER_STATUS" showCode="true"/>
<eRedG4:body>
<eRedG4:div key="custTypeTreeDiv"></eRedG4:div>
<eRedG4:div key="userGridDiv"></eRedG4:div>
</eRedG4:body>
<eRedG4:script>
   var root_userid = '<eRedG4:out key="rootUserid" scope="request"/>';
   var root_deptname = '<eRedG4:out key="rootUserid" scope="request"/>';
    var root_deptid = '<eRedG4:out key="rootUserid" scope="request"/>';
    
   var root_name = '我的客户';
   var login_account = '<eRedG4:out key="login_account" scope="request"/>';
</eRedG4:script>
</eRedG4:html>