drop table if exists authority;
drop table if exists xjtu_identity;
drop table if exists user;

create table user
(
    id       int auto_increment primary key,
    password varchar(100) not null,
    email    varchar(50) unique,
    phone    varchar(50) unique,
    enabled  bool         not null default true
)
    comment '用户表';

create table xjtu_identity
(
    id         int primary key references user (id),
    net_id     varchar(50) unique,
    student_id varchar(20) unique,
    name       varchar(20) not null
)
    comment '交大身份认证表';

CREATE TABLE authority
(
    id        int         not null references user (id),
    authority varchar(50) not null,
    primary key (id, authority)
)
    comment '权限表';
