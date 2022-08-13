insert into user(id, email, password)
values (1, 'user@email.com', '$2a$10$j0MLshsEbRQ2YREDd9DvEuQHo//pZ6/VbsCKvMiWrcfo.Rbp8M42K');
# password;

insert into user(id, email, password)
values (2, 'hamlet@email.com', '$2a$10$o.jCMmA799B8l/oFo37nhuqqRMJT7uqhub6stzSo0K5pXKAlGjSrO');
# 123456;

insert into user(id, phone, password)
values (3, '13200000000', '$2a$10$o.jCMmA799B8l/oFo37nhuqqRMJT7uqhub6stzSo0K5pXKAlGjSrO');

insert into xjtu_identity(id, net_id, name)
values (2, 'hamlet', '小张');

insert into authority(id, authority)
values (1, 'ROLE_USER');

insert into authority(id, authority)
values (2, 'ROLE_USER');

insert into authority(id, authority)
values (2, 'ROLE_ADMIN');