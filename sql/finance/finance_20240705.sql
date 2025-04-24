-- ----------------------------
-- 1、用户表
-- ----------------------------
drop table if exists fin_users;
create table fin_users (
  id                 bigint(20)      not null auto_increment    comment '用户ID',
  username           varchar(50)     not null                   comment '用户名',
  password           varchar(64)     not null                   comment '密码（SHA-256加密）',
  salt               varchar(32)     not null                   comment '密码盐值',
  nickname           varchar(50)     default null               comment '昵称',
  name               varchar(50)     default null               comment '姓名',
  email              varchar(100)    default null               comment '邮箱',
  phone              varchar(20)     default null               comment '手机号',
  role               varchar(20)     not null default 'user'    comment '角色（admin/user）',
  avatar             varchar(255)    default null               comment '用户头像地址',
  failed_attempts    int(11)         not null default '0'       comment '登录失败次数',
  locked_until       datetime        default null               comment '锁定截止时间',
  wechat             varchar(100)    default null               comment '微信号',
  qq                 varchar(20)     default null               comment 'QQ号',
  del_flag           char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  last_login_time    datetime        default null               comment '最后登录时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (id),
  unique key uk_username (username),
  unique key uk_phone (phone),
  unique key uk_email (email)
) engine=innodb auto_increment=100 comment = '用户表';

-- ----------------------------
-- 初始化-用户表数据
-- ----------------------------
insert into fin_users values(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'admin123', '管理员', '系统管理员', 'admin@example.com', '13800138000', 'admin', null, 0, null, null, null, '0', 'admin', sysdate(), '', null, sysdate(), '系统管理员');
insert into fin_users values(2, 'test', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'admin123', '测试用户', '测试用户', 'test@example.com', '13900139000', 'user', null, 0, null, null, null, '0', 'admin', sysdate(), '', null, sysdate(), '测试用户');

-- ----------------------------
-- 2、登录历史表
-- ----------------------------
drop table if exists fin_login_history;
create table fin_login_history (
  id                bigint(20)      not null auto_increment    comment '历史ID',
  user_id           bigint(20)      not null                   comment '用户ID',
  login_time        datetime        not null                   comment '登录时间',
  ip_address        varchar(50)     default null               comment 'IP地址',
  device_info       text                                       comment '设备信息',
  success           tinyint(1)      not null default '1'       comment '是否成功',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (id),
  key idx_user_id (user_id)
) engine=innodb auto_increment=100 comment = '登录历史表';

-- ----------------------------
-- 3、分类表
-- ----------------------------
drop table if exists fin_categories;
create table fin_categories (
  id                bigint(20)      not null auto_increment    comment '分类ID',
  name              varchar(50)     not null                   comment '分类名称',
  type              tinyint(4)      not null                   comment '类型（1:支出,2:收入）',
  icon              varchar(255)     default null               comment '图标',
  color             varchar(20)     default null               comment '颜色代码',
  user_id           bigint(20)      default null               comment '用户ID（NULL表示系统预设）',
  display_order     int(11)         not null default '0'       comment '显示顺序',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (id),
  key idx_user_id (user_id),
  key idx_type (type)
) engine=innodb auto_increment=100 comment = '分类表';

-- ----------------------------
-- 初始化-分类表数据
-- ----------------------------
-- 支出分类
insert into fin_categories values(1, '餐饮', 1, 'food', '#FF5722', null, 1, '0', 'admin', sysdate(), '', null, '餐厅、饭店、食堂、外卖等');
insert into fin_categories values(2, '购物', 1, 'shopping', '#4CAF50', null, 2, '0', 'admin', sysdate(), '', null, '超市、商场、网购等');
insert into fin_categories values(3, '交通', 1, 'traffic', '#2196F3', null, 3, '0', 'admin', sysdate(), '', null, '公交、地铁、打车、高铁等');
insert into fin_categories values(4, '住房', 1, 'house', '#9C27B0', null, 4, '0', 'admin', sysdate(), '', null, '房租、水电、物业、宽带等');
insert into fin_categories values(5, '娱乐', 1, 'entertainment', '#FFC107', null, 5, '0', 'admin', sysdate(), '', null, '电影、游戏、KTV、旅游等');
-- 收入分类
insert into fin_categories values(6, '工资', 2, 'salary', '#3F51B5', null, 1, '0', 'admin', sysdate(), '', null, '工资收入');
insert into fin_categories values(7, '奖金', 2, 'bonus', '#E91E63', null, 2, '0', 'admin', sysdate(), '', null, '奖金收入');
insert into fin_categories values(8, '理财', 2, 'finance', '#009688', null, 3, '0', 'admin', sysdate(), '', null, '理财收入');

-- ----------------------------
-- 4、交易记录表
-- ----------------------------
drop table if exists fin_transactions;
create table fin_transactions (
  id                bigint(20)      not null auto_increment    comment '交易ID',
  user_id           bigint(20)      not null                   comment '用户ID',
  category_id       bigint(20)      not null                   comment '分类ID',
  amount            decimal(12,2)   not null                   comment '金额',
  type              tinyint(4)      not null                   comment '类型（1:支出,2:收入）',
  transaction_time  bigint(20)      not null                   comment '交易时间戳',
  note              varchar(255)    default null               comment '备注',
  image_path        varchar(255)    default null               comment '图片路径',
  location          varchar(255)    default null               comment '位置',
  sync_state        tinyint(4)      not null default '2'       comment '同步状态（1:本地,2:已同步）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (id),
  key idx_user_id (user_id),
  key idx_category_id (category_id),
  key idx_transaction_time (transaction_time),
  key idx_type (type)
) engine=innodb auto_increment=100 comment = '交易记录表';

-- ----------------------------
-- 5、预算表
-- ----------------------------
drop table if exists fin_budgets;
create table fin_budgets (
  id                bigint(20)      not null auto_increment    comment '预算ID',
  user_id           bigint(20)      not null                   comment '用户ID',
  category_id       bigint(20)      default null               comment '分类ID（NULL表示总预算）',
  year              int(11)         not null                   comment '年份',
  month             int(11)         not null                   comment '月份',
  amount            decimal(12,2)   not null                   comment '预算金额',
  warning_threshold decimal(5,2)    not null default '80.00'   comment '预警阈值（百分比）',
  warned            tinyint(1)      not null default '0'       comment '是否已预警',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (id),
  unique key uk_user_category_year_month (user_id,category_id,year,month),
  key idx_category_id (category_id),
  key idx_year_month (year,month)
) engine=innodb auto_increment=100 comment = '预算表';

-- ----------------------------
-- 6、通知表
-- ----------------------------
drop table if exists fin_notification;
create table fin_notification (
  id                bigint(20)      not null auto_increment    comment '通知ID',
  user_id           bigint(20)      not null                   comment '用户ID',
  title             varchar(100)    not null                   comment '标题',
  content           text            not null                   comment '内容',
  type              varchar(20)     not null                   comment '类型（budget_warning/system_notice）',
  is_read           tinyint(1)      not null default '0'       comment '是否已读',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (id),
  key idx_user_id (user_id),
  key idx_type (type),
  key idx_is_read (is_read)
) engine=innodb auto_increment=100 comment = '通知表';

-- ----------------------------
-- 7、分类规则表
-- ----------------------------
drop table if exists fin_category_rules;
create table fin_category_rules (
  id                bigint(20)      not null auto_increment    comment '规则ID',
  category_id       bigint(20)      not null                   comment '分类ID',
  pattern           text            not null                   comment '匹配模式(关键词，以|分隔)',
  user_id           bigint(20)      default null               comment '用户ID（NULL表示系统预设）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (id),
  key idx_category_id (category_id),
  key idx_user_id (user_id)
) engine=innodb auto_increment=100 comment = '分类规则表';

-- ----------------------------
-- 初始化-分类规则表数据
-- ----------------------------
insert into fin_category_rules values(1, 1, '餐厅|饭店|食堂|外卖|美食|餐饮|吃饭|午餐|晚餐', null, '0', 'admin', sysdate(), '', null, '餐饮分类匹配规则');
insert into fin_category_rules values(2, 2, '购物|超市|商场|淘宝|京东|网购', null, '0', 'admin', sysdate(), '', null, '购物分类匹配规则');
insert into fin_category_rules values(3, 3, '公交|地铁|打车|出租|滴滴|高铁|火车|机票', null, '0', 'admin', sysdate(), '', null, '交通分类匹配规则');
insert into fin_category_rules values(4, 4, '房租|水电|物业|宽带|煤气|有线', null, '0', 'admin', sysdate(), '', null, '住房分类匹配规则');
insert into fin_category_rules values(5, 5, '电影|游戏|KTV|演唱会|旅游|景点', null, '0', 'admin', sysdate(), '', null, '娱乐分类匹配规则');
