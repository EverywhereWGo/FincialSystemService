-- 创建通知表
create table if not exists fin_notification (
  id                bigint(20)      not null auto_increment    comment '通知ID',
  user_id           bigint(20)      not null                   comment '用户ID',
  title             varchar(100)    not null                   comment '标题',
  content           varchar(500)    not null                   comment '内容',
  type              varchar(50)     not null                   comment '类型（budget_warning/system_notice）',
  is_read           tinyint(1)      default 0                  comment '是否已读（0否 1是）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (id)
) engine=innodb auto_increment=200 comment = '财务通知表';

-- 注意：fin_menu.sql已包含通知管理相关菜单，无需重复添加