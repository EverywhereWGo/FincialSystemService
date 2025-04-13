-- ----------------------------
-- 财务系统菜单SQL
-- ----------------------------

-- 删除若依原有菜单
update sys_menu set visible = '1' where menu_id in (1, 2, 3, 4);

-- 新增财务系统一级菜单
insert into sys_menu values('2000', '财务系统', '0', '1', 'finance', null, '', 'Finance', 1, 0, 'M', '0', '0', '', 'money', 'admin', sysdate(), '', null, '财务系统目录');

-- 二级菜单
insert into sys_menu values('2001', '用户管理', '2000', '1', 'user', 'finance/user/index', '', 'FinanceUser', 1, 0, 'C', '0', '0', 'finance:user:list', 'user', 'admin', sysdate(), '', null, '用户管理菜单');
insert into sys_menu values('2002', '分类管理', '2000', '2', 'category', 'finance/category/index', '', 'FinanceCategory', 1, 0, 'C', '0', '0', 'finance:category:list', 'dict', 'admin', sysdate(), '', null, '分类管理菜单');
insert into sys_menu values('2003', '交易记录', '2000', '3', 'transaction', 'finance/transaction/index', '', 'FinanceTransaction', 1, 0, 'C', '0', '0', 'finance:transaction:list', 'money', 'admin', sysdate(), '', null, '交易记录菜单');
insert into sys_menu values('2004', '预算管理', '2000', '4', 'budget', 'finance/budget/index', '', 'FinanceBudget', 1, 0, 'C', '0', '0', 'finance:budget:list', 'chart', 'admin', sysdate(), '', null, '预算管理菜单');
insert into sys_menu values('2005', '统计报表', '2000', '5', 'statistics', 'finance/statistics/index', '', 'FinanceStatistics', 1, 0, 'C', '0', '0', 'finance:statistics:list', 'chart', 'admin', sysdate(), '', null, '统计报表菜单');
insert into sys_menu values('2006', '通知管理', '2000', '6', 'notification', 'finance/notification/index', '', 'FinanceNotification', 1, 0, 'C', '0', '0', 'finance:notification:list', 'message', 'admin', sysdate(), '', null, '通知管理菜单');
insert into sys_menu values('2007', '登录历史', '2000', '7', 'loginHistory', 'finance/loginHistory/index', '', 'FinanceLoginHistory', 1, 0, 'C', '0', '0', 'finance:loginHistory:list', 'logininfor', 'admin', sysdate(), '', null, '登录历史菜单');
insert into sys_menu values('2008', '分类规则', '2000', '8', 'categoryRule', 'finance/categoryRule/index', '', 'FinanceCategoryRule', 1, 0, 'C', '0', '0', 'finance:categoryRule:list', 'build', 'admin', sysdate(), '', null, '分类规则菜单');

-- 用户管理按钮
insert into sys_menu values('2101', '用户查询', '2001', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:user:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2102', '用户新增', '2001', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:user:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2103', '用户修改', '2001', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:user:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2104', '用户删除', '2001', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:user:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2105', '用户导出', '2001', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:user:export', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2106', '用户导入', '2001', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:user:import', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2107', '重置密码', '2001', '7', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:user:resetPwd', '#', 'admin', sysdate(), '', null, '');

-- 分类管理按钮
insert into sys_menu values('2201', '分类查询', '2002', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:category:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2202', '分类新增', '2002', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:category:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2203', '分类修改', '2002', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:category:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2204', '分类删除', '2002', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:category:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2205', '分类导出', '2002', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:category:export', '#', 'admin', sysdate(), '', null, '');

-- 交易记录按钮
insert into sys_menu values('2301', '交易查询', '2003', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:transaction:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2302', '交易新增', '2003', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:transaction:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2303', '交易修改', '2003', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:transaction:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2304', '交易删除', '2003', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:transaction:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2305', '交易导出', '2003', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:transaction:export', '#', 'admin', sysdate(), '', null, '');

-- 预算管理按钮
insert into sys_menu values('2401', '预算查询', '2004', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:budget:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2402', '预算新增', '2004', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:budget:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2403', '预算修改', '2004', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:budget:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2404', '预算删除', '2004', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:budget:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2405', '预算导出', '2004', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:budget:export', '#', 'admin', sysdate(), '', null, '');

-- 统计报表按钮
insert into sys_menu values('2501', '统计查询', '2005', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:statistics:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2502', '统计导出', '2005', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:statistics:export', '#', 'admin', sysdate(), '', null, '');

-- 通知管理按钮
insert into sys_menu values('2601', '通知查询', '2006', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:notification:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2602', '通知新增', '2006', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:notification:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2603', '通知修改', '2006', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:notification:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2604', '通知删除', '2006', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:notification:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2605', '通知导出', '2006', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:notification:export', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2606', '标记已读', '2006', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:notification:read', '#', 'admin', sysdate(), '', null, '');

-- 登录历史按钮
insert into sys_menu values('2701', '历史查询', '2007', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:loginHistory:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2702', '历史删除', '2007', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:loginHistory:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2703', '历史导出', '2007', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:loginHistory:export', '#', 'admin', sysdate(), '', null, '');

-- 分类规则按钮
insert into sys_menu values('2801', '规则查询', '2008', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:categoryRule:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2802', '规则新增', '2008', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:categoryRule:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2803', '规则修改', '2008', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:categoryRule:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2804', '规则删除', '2008', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:categoryRule:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2805', '规则导出', '2008', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:categoryRule:export', '#', 'admin', sysdate(), '', null, ''); 