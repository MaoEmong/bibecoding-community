insert into departments (name) values ('인사');
insert into departments (name) values ('개발');
insert into departments (name) values ('재무');
insert into departments (name) values ('영업');
insert into departments (name) values ('운영');
insert into departments (name) values ('마케팅');
insert into departments (name) values ('고객지원');

insert into positions (name) values ('사원');
insert into positions (name) values ('주임');
insert into positions (name) values ('대리');
insert into positions (name) values ('과장');
insert into positions (name) values ('차장');
insert into positions (name) values ('부장');
insert into positions (name) values ('대표');

insert into users (employee_no, name, email, password, role, department_id, position_id, status, hired_at)
values ('EMP-0001', '홍길동', 'ssar@nate.com', '1234', 'ADMIN',
        (select id from departments where name = '개발'),
        (select id from positions where name = '대표'),
        'EMPLOYED', date '2020-01-01');

insert into users (employee_no, name, email, password, role, department_id, position_id, status, hired_at)
values ('EMP-0002', '김민수', 'minsu@company.com', '1234', 'ADMIN',
        (select id from departments where name = '인사'),
        (select id from positions where name = '부장'),
        'EMPLOYED', date '2021-02-01');

insert into users (employee_no, name, email, password, role, department_id, position_id, status, hired_at)
values ('EMP-0003', '이서연', 'seoyeon@company.com', '1234', 'ADMIN',
        (select id from departments where name = '개발'),
        (select id from positions where name = '대리'),
        'LEAVE', date '2022-03-11');

insert into users (employee_no, name, email, password, role, department_id, position_id, status, hired_at)
values ('EMP-0004', '박지훈', 'jihoon@company.com', '1234', 'ADMIN',
        (select id from departments where name = '영업'),
        (select id from positions where name = '과장'),
        'EMPLOYED', date '2021-07-18');

insert into users (employee_no, name, email, password, role, department_id, position_id, status, hired_at)
values ('EMP-0005', '최유진', 'yujin@company.com', '1234', 'ADMIN',
        (select id from departments where name = '재무'),
        (select id from positions where name = '주임'),
        'RESIGNED', date '2020-09-14');

insert into posts (user_id, title, content, created_at, updated_at)
values ((select id from users where email = 'ssar@nate.com'), '환영합니다', '사내 커뮤니티 시스템을 시작합니다.', current_timestamp, current_timestamp);

insert into posts (user_id, title, content, created_at, updated_at)
values ((select id from users where email = 'minsu@company.com'), '개발팀 공지', '이번 스프린트 목표를 공유합니다.', current_timestamp, current_timestamp);

insert into posts (user_id, title, content, created_at, updated_at)
values ((select id from users where email = 'seoyeon@company.com'), '복지 안내', '사내 복지 제도 업데이트 내용을 확인해 주세요.', current_timestamp, current_timestamp);
