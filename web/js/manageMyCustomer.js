/**
 * 客户管理
 * 
 * @author Gavin
 * @since 2011-08-20
 */
Ext
		.onReady(function() {
			var root = new Ext.tree.AsyncTreeNode({
				text : root_name,
				expanded : true,
				id : root_userid
			});
			
			var custTypeTree = new Ext.tree.TreePanel({
				loader : new Ext.tree.TreeLoader({
					baseAttrs : {},
					dataUrl : './sales.ered?reqCode=customerTypeTreeInit'
				}),
				root : root,
				title : '',
				applyTo : 'custTypeTreeDiv',
				autoScroll : false,
				animate : false,
				useArrows : false,
				border : false
			});
			custTypeTree.root.select();
			custTypeTree.on('click', function(node) {
				var contacted='';
				var timegap='';
				if(Ext.getCmp('contacted_all').checked) contacted = '0';
				else if (Ext.getCmp('contacted_true').checked) contacted = '1';
				else if (Ext.getCmp('contacted_false').checked) contacted = '2';
				timegap=Ext.getCmp('timeGapCombo').getValue();
				//
				statusid = node.attributes.id;
				store.load({
					params : {
						start : 0,
						limit : bbar.pageSize,
						status : statusid,
						queryParam : Ext.getCmp('queryParam').getValue(),
						contacted:contacted,
						timegap:timegap
					}
				});
			});
			
			

			var sm = new Ext.grid.CheckboxSelectionModel();
			var cm = new Ext.grid.ColumnModel(
					[
							new Ext.grid.RowNumberer({width:30}),
							sm,
							{
								header : '营销',
								dataIndex : 'id',
								renderer : function(value, cellmeta, record) {
									var salesplanid=record.data['salesplanid'];	
									var exp = salesplanid;
									if (typeof exp == "undefined" || salesplanid==null || salesplanid=='')
									{
										return '<img src="../resource/image/ext/edit1.png"/>';
									}
									return '<a href="javascript:void(0);"><img src="../resource/image/ext/edit2.png"/></a>';
								},
								width : 45
							},
							
                            {
								
								header : '记录',
								dataIndex : 'id',
								renderer : function(value, cellmeta, record) {
									return '<a href="javascript:void(0);"><img src="../resource/image/ext/comments2.png"/></a>';
								},
								width : 45
							},
							
							{
								header : '公司id',
								dataIndex : 'id',
								hidden:true
							},
							{
								header : '公司名称',
								dataIndex : 'name',
								width : 250
							}, 
							{
								header : '回收时间',
								dataIndex : 'lastcalltime',
								renderer:function(value,cellMetaData,record) {
									var result;
									var status=record.get('status');
									var strRet;
									if(value!=null && value!="")
									{ 
										var date=new Date();
										var year="",month="",day="";
										year=date.getFullYear();
										month=date.getMonth()+1;
										day= date.getDate();
										var start=value;
										var end=year+"-"+month+"-"+day; //now
										var startDate=new Date(start.replace(/-/g,"/"));
										var endDate=new Date(end.replace(/-/g,"/"));
										result = parseInt((endDate - startDate) / (1000*60*60*24)); 	
								    }
									if(status=='0')
								    {
										
									}
									if(status=='1')
								    {
										result=180-result;
									}
									if(status=='2')
								    {
										strRet="合同中用户";
										return strRet;
									}
									if(status=='3')
								    {
										result=90-result;
									}
									if(status=='4')
								    {
										result=60-result;
									}
									if(status=='5')
								    {
										result=30-result;
									}
									return "<span style='color:red; font-weight:bold'>"+result+"天后回收</span>";
//									if(result==0)
//									{
//										return "<span style='color:green; font-weight:bold'>已回收</span>";
//									}
//									if (result <=10) {
//										return "<span style='color:green; font-weight:bold'>"+result+"回收</span>";
//									}
//									if (result <=30) {
//										return "<span style='color:blue; font-weight:bold'>"+result+"回收</span>";
//									}
//									if (result <=60) {
//										return "<span style='color:red; font-weight:bold'>"+result+"回收</span>";
//									}
//									if (result <=90) {
//										return "<span style='color:yellow; font-weight:bold'>"+result+"回收</span>";
//									}
//									if (result >180) {
//										return "<span style='color:gray; font-weight:bold'>"+result+"回收</span>";
//									}
//									else (result >365) 
//									{
//										return "<span style='color:gray; font-weight:bold'>"+result+"回收</span>";
//									}
									
								},
								width : 80
							}, 
							{
								header : '联系人',
								dataIndex : 'linkname',
								width : 130
							}, {
								header : '电话',
								dataIndex : 'tel',
								width : 130
							}, 
							 {
								header : '手机',
								dataIndex : 'phone',
								width : 130
							},
							{
								header : '传真',
								dataIndex : 'fax',
								width : 130
							},
							{
								header : '公司地址',
								dataIndex : 'addr',
								width : 130
							},
							{
								header : '客户性质',
								dataIndex : 'statusname',
								width : 130
							},
							{
								header : 'Email',
								dataIndex : 'email',
								width : 130
							},
							{
								id : 'remark',
								header : '备注',
								dataIndex : 'remark'
							},{
								header : '状态',
								dataIndex : 'status',
								hidden:true
							}]);

			/**
			 * 数据存储
			 */
			var store = new Ext.data.Store({
				proxy : new Ext.data.HttpProxy({
					url : './sales.ered?reqCode=queryCustomerForManage'
				}),
				reader : new Ext.data.JsonReader({
					totalProperty : 'TOTALCOUNT',
					root : 'ROOT'
				}, [ {
					name : 'id'
				}, {
					name : 'name'
				}, {
					name : 'linkname'
				}, {
					name : 'tel'
				}, {
					name : 'phone'
				}, {
					name : 'fax'
				}, {
					name : 'email'
				}, {
					name : 'addr'
				}, {
					name : 'deptname'
				},
				{
					name : 'deptid'
				}, 
				{
					name : 'ownerid'
				},
				{
					name : 'ownername'
				}, 
				{
					name : 'status'
				},
				{
					name : 'statusname'
				}, 
				{
					name : 'salesplanid'
				},
				{
					name : 'lastcalltime'
				},
				{
					name : 'remark'
				} ])
			});
			
			// 翻页排序时带上查询条件
			store.on('beforeload', function() {
				//
				var contacted='';
				var timegap='';
				if(Ext.getCmp('contacted_all').checked) contacted = '0';
				else if (Ext.getCmp('contacted_true').checked) contacted = '1';
				else if (Ext.getCmp('contacted_false').checked) contacted = '2';
				timegap=Ext.getCmp('timeGapCombo').getValue();
				//
				this.baseParams = {
					queryParam : Ext.getCmp('queryParam').getValue(),contacted:contacted,timegap:timegap
				};
			});
			var pagesize_combo = new Ext.form.ComboBox({
				name : 'pagesize',
				hiddenName : 'pagesize',
				typeAhead : true,
				triggerAction : 'all',
				lazyRender : true,
				mode : 'local',
				store : new Ext.data.ArrayStore({
					fields : [ 'value', 'text' ],
					data : [ [ 10, '10条/页' ], [ 20, '20条/页' ], [ 50, '50条/页' ],
							[ 100, '100条/页' ], [ 250, '250条/页' ],
							[ 500, '500条/页' ] ]
				}),
				valueField : 'value',
				displayField : 'text',
				value : '50',
				editable : false,
				width : 85
			});
			var number = parseInt(pagesize_combo.getValue());
			pagesize_combo.on("select", function(comboBox) {
				bbar.pageSize = parseInt(comboBox.getValue());
				number = parseInt(comboBox.getValue());
				store.reload({
					params : {
						start : 0,
						limit : bbar.pageSize
					}
				});
			});

			var bbar = new Ext.PagingToolbar({
				pageSize : number,
				store : store,
				displayInfo : true,
				displayMsg : '显示{0}条到{1}条,共{2}条',
				emptyMsg : "没有符合条件的记录",
				items : [ '-', '&nbsp;&nbsp;', pagesize_combo ]
			});
			
			var queryTimeGap = new Ext.data.SimpleStore({
				fields : ['text', 'value'],
				data : [['一天', '-1'],['一周', '-7'], ['半月', '-15'],['一月', '-30'],['三月', '-90'],['六月', '-180'],['一年', '-365']]
			});
			var timeGapCombo = new Ext.form.ComboBox({
				id:'timeGapCombo',
				hiddenName : 'timegap', // 往后台传值字段名
				store : queryTimeGap, // 引用的代码表数据源和<eRedG4:ext.codeStore// />标签对应
				mode : 'local',
				triggerAction : 'all',
				valueField : 'value',
				displayField : 'text',
				value : '', // 初始选中的列表项
				fieldLabel : '时间间隔',
				emptyText : '几天...',
				allowBlank : true,
				//forceSelection : true, // 选中内容必须为下拉列表的子项
				editable : true, // 选择输入框可编辑
				typeAhead : true, // 输入的时候自动匹配待选项目
				anchor : '100%',
				width : 75,
				bodyStyle : 'padding:5 5 0'
			});
					
			var grid = new Ext.grid.GridPanel({
				title : '<span style="font-weight:normal">客户信息</span>',
				iconCls : 'groupIcon',
				renderTo : 'custGridDiv',
				height : 500,
				// width:600,
				autoScroll : true,
				region : 'center',
				store : store,
				loadMask : {
					msg : '正在加载表格数据,请稍等...'
				},
				stripeRows : true,
				frame : true,
				autoExpandColumn : 'remark',
				cm : cm,
				sm : sm,
				tbar : [ {
					text : '新增',
					iconCls : 'page_addIcon',
					handler : function() {
						addInit();
					}
				}, '-', {
					text : '修改',
					iconCls : 'page_edit_1Icon',
					handler : function() {
						editInit();
					}
				},'->',
				    new Ext.form.Radio({
					id:'contacted_all',
					name : 'contacted',// 名字相同的单选框会作为一组
					fieldLabel : 'contacted',
					inputValue:"0",
					checked:true,
					boxLabel : '所有'
				}),'-',
				    new Ext.form.Label({
				    	 text:"最近"
				    })
				    , timeGapCombo,'-', new Ext.form.Radio({
					id:'contacted_true',
					name : 'contacted',
					inputValue:"1",
					boxLabel : '联系过'
				}), new Ext.form.Radio({
					name : 'contacted',
					id:'contacted_false',
					inputValue:"2",
					boxLabel : '未联系过'
				}),
				new Ext.form.TextField({
					id : 'queryParam',
					name : 'queryParam',
					emptyText : '请输入人员名称',
					enableKeyEvents : true,
					listeners : {
						specialkey : function(field, e) {
							if (e.getKey() == Ext.EventObject.ENTER) {
								queryCustomerItem();
							}
						}
					},
					width : 130
				}), {
					text : '查询',
					iconCls : 'previewIcon',
					handler : function() {
						queryCustomerItem();
					}
				}, '-', {
					text : '刷新',
					iconCls : 'arrow_refreshIcon',
					handler : function() {
						store.reload();
					}
				}, new Ext.form.TextField({
					id : 'custid',
					hidden:true
				})],
				bbar : bbar
			});
			store.load({
				params : {
					start : 0,
					limit : bbar.pageSize,
					firstload : 'true'
				}
			});
			grid.on('rowdblclick', function(grid, rowIndex, event) {
				editInit();
			});
			grid.on('sortchange', function() {
				// grid.getSelectionModel().selectFirstRow();
			});
			grid.on("cellclick",
					function(grid, rowIndex, columnIndex, e) {
				       if (columnIndex == 2) {
				    	   var store = grid.getStore();
							var record = store.getAt(rowIndex);
							var custid=record.get('id');
							var salesplanid=record.get('salesplanid');
							//showWaitMsg();
							
							if (salesplanid==null || salesplanid=='')
							{
								Ext.Ajax
								.request({
									url : './sales.ered?reqCode=addCustomersToSalePlan',
									success : function(response) {
										store.reload();
									},
									failure : function(response) {
										
									},
									params : {
										custid : custid
									}
								});
							}
				       }
				       else if(columnIndex == 3)
				       {
				    	   var store = grid.getStore();
							  var record = store.getAt(rowIndex);
							  var custid=record.get('id');
							  Ext.getCmp("custid").setValue(custid);
							  reStore.load({
									params : {
										start : 0,
										limit : reBbar.pageSize,
										firstload : 'true',
										custid:Ext.getCmp("custid").getValue()
									}
								});
							  recordsWindow.show();
				       }
						
					
					});

			bbar.on("change", function() {
				// grid.getSelectionModel().selectFirstRow();
			});
			
			var custSailingStatusStore = new Ext.data.SimpleStore({
				fields : ['text', 'value'],
				data : [['线索客户', '5'],['意向客户', '4'], ['待成交客户', '3'],['合同中客户', '2'],['无效用户', '0']]
			});
			
			var custSailingStatusStore_min = new Ext.data.SimpleStore({
				fields : ['text', 'value'],
				data : [['线索客户', '5'],['意向客户', '4'], ['待成交客户', '3'],['无效用户', '0']]
			});

			var statusCombo = new Ext.form.ComboBox({
				id:'statusCombo',
				hiddenName : 'status', // 往后台传值字段名
				store : custSailingStatusStore, // 引用的代码表数据源和<eRedG4:ext.codeStore
										// />标签对应
				mode : 'local',
				triggerAction : 'all',
				valueField : 'value',
				displayField : 'text',
				value : '', // 初始选中的列表项
				fieldLabel : '客户性质',
				emptyText : '请选择...',
				allowBlank : false,
				forceSelection : true, // 选中内容必须为下拉列表的子项
				editable : true, // 选择输入框可编辑
				typeAhead : true, // 输入的时候自动匹配待选项目
				anchor : '100%'
			});

			var addCustomerFormPanel = new Ext.form.FormPanel({
				id : 'addCustomerFormPanel',
				name : 'addCustomerFormPanel',
				defaultType : 'textfield',
				labelAlign : 'right',
				labelWidth : 65,
				frame : false,
				bodyStyle : 'padding:5 5 0',
				items : [ {
					name : 'id',
					id : 'id',
					hidden:true
				},{
					fieldLabel : '公司名称',
					name : 'name',
					id : 'name',
					allowBlank : false,
					anchor : '99%'
				},
				statusCombo,
				{
					fieldLabel : '联系人',
					name : 'linkname',
					id : 'linkname',
					allowBlank : false,
					anchor : '99%'
				}, 
				{
					fieldLabel : '电话',
					name : 'tel',
					id : 'tel',
					allowBlank : true,
					anchor : '99%'
				}, {
					fieldLabel : '手机',
					name : 'phone',
					id : 'phone',
					allowBlank : true,
					anchor : '99%'
				}, {
					fieldLabel : '传真',
					name : 'fax',
					id : 'fax',
					allowBlank : true,
					anchor : '99%'
				}, 
				{
					fieldLabel : 'EMAIL',
					name : 'email',
					id : 'email',
					allowBlank : true,
					anchor : '99%'
				},
				{
					fieldLabel : '公司地址',
					name : 'addr',
					id : 'addr',
					allowBlank : true,
					anchor : '99%'
				},		
				{
					fieldLabel : '备注',
					name : 'remark',
					allowBlank : true,
					anchor : '99%'
				},
				{
					id : 'windowmode',
					name : 'windowmode',
					hidden : true
				}, 
				{
					id : 'updatemode',
					name : 'updatemode',
					hidden : true
				} ]
			});

			var addCustomerWindow = new Ext.Window(
					{
						layout : 'fit',
						width : 400,
						height : 410,
						resizable : false,
						draggable : true,
						closeAction : 'hide',
						modal : true,
						title : '<span style="font-weight:normal">新增客户</span>',
						// iconCls : 'page_addIcon',
						collapsible : true,
						titleCollapse : true,
						maximizable : false,
						buttonAlign : 'right',
						border : false,
						animCollapse : true,
						pageY : 20,
						pageX : document.body.clientWidth / 2 - 420 / 2,
						animateTarget : Ext.getBody(),
						constrain : true,
						items : [ addCustomerFormPanel ],
						buttons : [
								{
									text : '保存',
									iconCls : 'acceptIcon',
									handler : function() {
										if (runMode == '0') {
											Ext.Msg
													.alert('提示',
															'系统正处于演示模式下运行,您的操作被取消!该模式下只能进行查询操作!');
											return;
										}
										var mode = Ext.getCmp('windowmode')
												.getValue();
										if (mode == 'add')
											saveCustomerItem();
										else
											updateCustomerItem();
									}
								}, {
									text : '重置',
									id : 'btnReset',
									iconCls : 'tbar_synchronizeIcon',
									handler : function() {
										clearForm(addCustomerFormPanel.getForm());
									}
								}, {
									text : '关闭',
									iconCls : 'deleteIcon',
									handler : function() {
										addCustomerWindow.hide();
									}
								} ]
					});

			/**
			 * 布局
			 */
			var viewport = new Ext.Viewport({
				layout : 'border',
				items : [ {
					title : '<span style="font-weight:normal">客户分类</span>',
					iconCls : 'chart_organisationIcon',
					tools : [ {
						id : 'refresh',
						handler : function() {
							custTypeTree.root.reload()
						}
					} ],
					collapsible : true,
					width : 210,
					minSize : 160,
					maxSize : 280,
					split : true,
					region : 'west',
					autoScroll : true,
					// collapseMode:'mini',
					items : [ custTypeTree ]
				}, {
					region : 'center',
					layout : 'fit',
					items : [ grid ]
				} ]
			});

			/**
			 * 根据条件查询人员
			 */
			function queryCustomerItem() {
				var contacted='';
				var timegap='';
				if(Ext.getCmp('contacted_all').checked) contacted = '0';
				else if (Ext.getCmp('contacted_true').checked) contacted = '1';
				else if (Ext.getCmp('contacted_false').checked) contacted = '2';
				timegap=Ext.getCmp('timeGapCombo').getValue();
				//
				store.load({
					params : {
						start : 0,
						limit : bbar.pageSize,
						queryParam : Ext.getCmp('queryParam').getValue(),
						contacted:contacted,
						timegap:timegap
					}
				});
			}

			/**
			 * 新增客户初始化
			 */
			function addInit() {
				var flag = Ext.getCmp('windowmode').getValue();
				if (typeof (flag) != 'undefined') {
					addCustomerFormPanel.form.getEl().dom.reset();
				} else {
					clearForm(addCustomerFormPanel.getForm());
				}
				var selectModel = custTypeTree.getSelectionModel();
				var selectNode = selectModel.getSelectedNode();
				
				
				Ext.getCmp('name').setDisabled(false);
				
				Ext.getCmp('statusCombo').enable();
				
				
				Ext.getCmp('statusCombo').store=custSailingStatusStore_min;
				//Ext.getCmp('name').setFieldClass("");
				
				Ext.getCmp('windowmode').setValue('add');
				Ext.getCmp('btnReset').show();
				
				addCustomerWindow.show();
				addCustomerWindow
						.setTitle('<span style="font-weight:normal">新增客户</span>');
			}

			

			/**
			 * 修改客户信息
			 */
			function editInit() {
				var record = grid.getSelectionModel().getSelected();
				if (Ext.isEmpty(record)) {
					Ext.MessageBox.alert('提示', '请先选择要修改的项目!');
				}
				addCustomerFormPanel.getForm().loadRecord(record);
				restrictStatusCombo=Ext.getCmp('statusCombo').getValue();
				Ext.getCmp('statusCombo').store=custSailingStatusStore;
				if(restrictStatusCombo=='2')
				{
					Ext.getCmp('statusCombo').disable();
				}
				else
				{
					Ext.getCmp('statusCombo').enable();
					Ext.getCmp('statusCombo').store=custSailingStatusStore_min;
				}
				
				addCustomerWindow.show();
				addCustomerWindow
						.setTitle('<span style="font-weight:normal">修改客户</span>');
				//Ext.MessageBox.alert(addCustomerFormPanel.getForm().);
				//Ext.MessageBox.alert(Ext.getCmp('name').);
				Ext.getCmp('name').disable();
				//Ext.getCmp('name').setFieldClass("x-custom-field-disabled");
				Ext.getCmp('windowmode').setValue('edit');
				Ext.getCmp('btnReset').hide();
			}
			
			
			/**
			 * 保存人员数据
			 */
			function saveCustomerItem() {
				if (!addCustomerFormPanel.form.isValid()) {
					return;
				}
				addCustomerFormPanel.form.submit({
					url : './sales.ered?reqCode=saveCustomerItem',
					waitTitle : '提示',
					method : 'POST',
					waitMsg : '正在处理数据,请稍候...',
					success : function(form, action) {
						addCustomerWindow.hide();
						store.reload();
						form.reset();
						Ext.MessageBox.alert('提示', action.result.msg);
					},
					failure : function(form, action) {
						var msg = action.result.msg;
						Ext.MessageBox.alert('提示', '人员数据保存失败:<br>' + msg);
					}
				});
			}
			
			/**
			 * 更新
			 */
			function updateCustomerItem() {
				if (!addCustomerFormPanel.form.isValid()) {
					return;
				}
				addCustomerFormPanel.form.submit({
					url : './sales.ered?reqCode=updateCustomerItem',
					waitTitle : '提示',
					method : 'POST',
					waitMsg : '正在处理数据,请稍候...',
					success : function(form, action) {
						addCustomerWindow.hide();
						store.reload();
						form.reset();
						Ext.MessageBox.alert('提示', action.result.msg);
					},
					failure : function(form, action) {
						var msg = action.result.msg;
						Ext.MessageBox.alert('提示', '客户数据修改失败:<br>' + msg);
					}
				});
			}
           //~==========================================================================================================
			//~Form the customer's records history grid
			var reRownum = new Ext.grid.RowNumberer({
				header : 'NO',
				width : 28
			});
			// 定义列模型
			var recm = new Ext.grid.ColumnModel([reRownum, {
				header : '营销人员', // 列标题
				dataIndex : 'username', // 数据索引:和Store模型对应
				sortable : true  // 是否可排序
				},{
					header : '内容',
					dataIndex : 'content',
					sortable : true
				}, {
				header : '联系时间',
				dataIndex : 'calltime',
				sortable : true
			}, {
				header : '提醒时间',
				dataIndex : 'recalltime'
			}]);
			
			/**
			 * 数据存储
			 */
			var reStore = new Ext.data.Store({
						// 获取数据的方式
						proxy : new Ext.data.HttpProxy({
									url : './sales.ered?reqCode=querySalesRecords'
								}),
						reader : new Ext.data.JsonReader({
									totalProperty : 'TOTALCOUNT', // 记录总数
									root : 'ROOT' // Json中的列表数据根节点
								}, [{
											name : 'username' // Json中的属性Key值
										}, {
											name : 'content'
										}, {
											name : 'calltime',
										}, {
											name : 'recalltime'
									}])
					});
					/**
					 * 翻页排序时候的参数传递
					 */
					reStore.on('beforeload', function() {
								var custid=Ext.getCmp("custid").getValue();
								this.baseParams={custid:custid};
							});
				// 每页显示条数下拉选择框
				var rePagesize_combo = new Ext.form.ComboBox({
							name : 'pagesize',
							triggerAction : 'all',
							mode : 'local',
							store : new Ext.data.ArrayStore({
										fields : ['value', 'text'],
										data : [[10, '10条/页'], [20, '20条/页'], [50, '50条/页'], [100, '100条/页'], [250, '250条/页'], [500, '500条/页']]
									}),
							valueField : 'value',
							displayField : 'text',
							value : '20',
							editable : false,
							width : 85
						});
				var reNumber = parseInt(rePagesize_combo.getValue());
				
				// 分页工具栏
				var reBbar = new Ext.PagingToolbar({
							pageSize : reNumber,
							store : reStore,
							displayInfo : true,
							displayMsg : '显示{0}条到{1}条,共{2}条',
							plugins : new Ext.ux.ProgressBarPager(), // 分页进度条
							emptyMsg : "没有符合条件的记录",
							items : ['-', '&nbsp;&nbsp;', rePagesize_combo]
						});
				
				// 改变每页显示条数reload数据
				rePagesize_combo.on("select", function(comboBox) {
							reBbar.pageSize = parseInt(comboBox.getValue());
							reNumber = parseInt(comboBox.getValue());
							reStore.reload({
										params : {
											start : 0,
											limit : reBbar.pageSize
										}
									});
						});
				
			
				// 表格实例
				var salesRecord = new Ext.grid.GridPanel({
							// 表格面板标题,默认为粗体，我不喜欢粗体，这里设置样式将其格式为正常字体
							title : '<span style="font-weight:normal">营销记录</span>',
							height : 300,
							autoScroll : true,
							frame : true,
							region : 'center', // 和VIEWPORT布局模型对应，充当center区域布局
							store : reStore, // 数据存储
							stripeRows : true, // 斑马线
							cm : recm, // 列模型
							//tbar : tbar, // 表格工具栏
							bbar : reBbar,// 分页工具栏
							viewConfig : {
				            // 不产横向生滚动条, 各列自动扩展自动压缩, 适用于列数比较少的情况
							// forceFit : true
							},
							loadMask : {
								msg : '正在加载表格数据,请稍等...'
							}
						});
				
				//~ Form the records window
					var recordsWindow = new Ext.Window({
						title : '<span style="font-weight:normal">销售记录<span>', // 窗口标题
						layout : 'border', // 设置窗口布局模式
						width : 450, // 窗口宽度
						height : 500, // 窗口高度
						//closable : false, // 是否可关闭
						collapsible : true, // 是否可收缩
						//maximizable : true, // 设置是否可以最大化
						border : false, // 边框线设置
						constrain : true, // 设置窗口是否可以溢出父容器
						animateTarget : Ext.getBody(),
						pageY : 20, // 页面定位Y坐标
						pageX : document.body.clientWidth / 2 - 600 / 2, // 页面定位X坐标
						items : [salesRecord], // 嵌入的表单面板
						close:function(){ 
							    this.hide();
							} 
					});
		});