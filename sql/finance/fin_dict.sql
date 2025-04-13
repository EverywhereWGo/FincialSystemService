-- ----------------------------
-- 1. 添加字典类型
-- ----------------------------
insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark) 
values(100, '财务分类类型', 'fin_category_type', '0', 'admin', sysdate(), '', null, '财务分类类型（1:支出,2:收入）');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark) 
values(101, '通知类型', 'notification_type', '0', 'admin', sysdate(), '', null, '通知类型（budget_warning/system_notice）');

insert into sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark) 
values(102, '用户角色', 'fin_user_role', '0', 'admin', sysdate(), '', null, '用户角色（admin/user）');

-- ----------------------------
-- 2. 添加字典数据
-- ----------------------------
-- 财务分类类型
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
values(1000, 1, '支出', '1', 'fin_category_type', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '支出类型');

insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
values(1001, 2, '收入', '2', 'fin_category_type', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '收入类型');

-- 通知类型
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) 
values(1002, 1, '预算预警', 'budget_warning', 'notification_type', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '预算预警通知');

insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) 
values(1003, 2, '系统通知', 'system_notice',  'notification_type', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '系统通知');

-- 用户角色
insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) 
values(1004, 1, '管理员', 'admin', 'fin_user_role', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '管理员角色');

insert into sys_dict_data(dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) 
values(1005, 2, '普通用户', 'user',  'fin_user_role', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '普通用户角色');
