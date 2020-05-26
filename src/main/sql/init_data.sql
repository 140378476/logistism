use logistism;

# delete
# from manager;

replace into user(username, password, nickname)
values ('lyc', '$2a$10$BH3Z4Vb3DMDGHv3RK9lU6.XbG5IuX/97eIXyUZWMAsfDjpaQQDOKa', 'llyc'),  # pass
       ('kdy', '$2a$10$BH3Z4Vb3DMDGHv3RK9lU6.XbG5IuX/97eIXyUZWMAsfDjpaQQDOKa', 'kdy00'), # pass
       ('user', '$2a$10$j1mwm4/zbhRScT6pqYqFLeKprWYx0TJ4rhDuqF3SWxmJo.yy/QSPS', '演示用户'); # qwert

replace into manager(username, level)
values ('lyc', 0);

replace into customer(username)
values ('lyc'),
       ('user');

replace into postman(username, real_name, phone)
values ('kdy', '小呆', '11110');

replace into postman_repo(postman, repository)
values ('kdy', 1),
       ('kdy', 4),
       ('kdy', 5);

replace into location(id, name, province, city, district, street, address)
values (1, '家', '上海', '上海市', '浦东新区', 'XX街道', 'XXX路XXX弄XXX号 XX室XX'),
       (2, '上海市浦东新区配送站', '上海', '上海市', '浦东新区', 'XX街道', 'XXX路XXX弄XXX号'),
       (3, '虹桥机场集运点', '上海', '上海市', '长宁区', '街道', '地址'),
       (4, '首都国际机场集运点', '北京', '北京市', '朝阳区', '街道', '地址'),
       (5, '海淀区配送站', '北京', '北京市', '海淀区', '中关村街道', '地址'),
       (6, '学校', '北京', '北京市', '海淀区', '清华街道', '紫荆XX号楼'),
       (7, '杭州铁路物流中心', '浙江省', '杭州市', '上城区', '环城东路街道', '环城东路1号'),
       (8, '买家', '浙江省', '杭州市', '上城区', 'XXX街道', 'XXX'),
       (9, '上海火车站物流集散点', '上海', '上海市', '黄浦区', ' ', ' ');
# update locations set name = '学校' where id = 6;
# select * from locations;
replace into repository(id, name, location_id)
values (1, '上海市浦东新区配送站', 2),
       (2, '虹桥机场集运点', 3),
       (3, '首都国际机场集运点', 4),
       (4, '海淀区配送站', 5),
       (5, '杭州铁路物流中心', 7),
       (6, '上海火车站物流集散点', 9);


replace into endpoint(id, location, name, phone, description)
values (1, 1, 'lyc', '12300', '家'),
       (2, 6, 'fs', '32100', '学校'),
       (3, 8, '买家', '99999', '买家地址'),
       (4, 1, 'my_name', '186XXXXXXXX', '家');

replace into customer_endpoint(customer, endpoint_id)
values ('lyc', 1),
       ('lyc', 2),
       ('lyc', 3),
       ('user', 4),
       ('user', 2),
       ('user', 3)
;


replace into `order`(id, item_name, customer, source, destination, creation_date, state, remark)
values (1, '书', 'lyc', 1, 2, utc_timestamp(), 0, '测试');


replace into shipment_schedule(shipment_id, description, transportation, source_id, destination_id, time_cost,
                               repetition_type, repetition_time)
values (1, '上海到北京直飞路线', '空运', 2, 3, 60, 'minute', maketime(0, 0, 24)),
       (2, '上海集运', '货车', 1, 2, 30, 'minute', maketime(0, 0, 10)),
       (3, '北京分送', '货车', 3, 4, 30, 'minute', maketime(0, 0, 10)),
       (4, '上海火车站集运', '货车', 2, 6, 43, 'minute', maketime(0, 0, 13)),
       (5, '沪杭铁路', '火车', 6, 5, 30, 'minute', maketime(0, 0, 21)),
       (6, '北京到上海直飞路线', '空运', 3, 2, 43 * 60, 'hour', maketime(0, 5, 24)),
       (7, '上海分送', '货车', 2, 1, 10 * 60, 'hour', maketime(0, 50, 14)),
       (8, '北京集运', '货车', 4, 3, 7 * 60, 'hour', maketime(0, 0, 13));

# insert into posting_log(order_id, postman, time, type)
# VALUES (1, 'kdy', utc_timestamp(), 0);
# insert into transfer_log(repository, shipment, order_id, type, time)
# VALUES (1, null, 1, 0, utc_timestamp());


# insert into