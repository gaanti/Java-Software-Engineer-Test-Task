create table account
(
    id bigint not null auto_increment,
    primary key (id)
) engine = InnoDB;
create table attendance
(
    id          bigint not null auto_increment,
    is_attended bit    not null,
    lesson_id   bigint,
    student_id  bigint,
    primary key (id)
) engine = InnoDB;
create table bug_report
(
    id            bigint not null auto_increment,
    creation_time datetime,
    description   varchar(255),
    title         varchar(255),
    primary key (id)
) engine = InnoDB;
create table canceled_lesson
(
    id                bigint not null auto_increment,
    cancellation_date datetime,
    reason            TEXT,
    attendance_id     bigint,
    primary key (id)
) engine = InnoDB;
create table company
(
    id              bigint not null auto_increment,
    logo_key        varchar(255),
    name            varchar(255),
    preferred_color varchar(255),
    owner_id        bigint,
    primary key (id)
) engine = InnoDB;
create table hibernate_sequences
(
    sequence_name varchar(255) not null,
    next_val      bigint,
    primary key (sequence_name)
) engine = InnoDB;
insert into hibernate_sequences(sequence_name, next_val)
values ('default', 0);
create table jwt_user
(
    id           bigint auto_increment primary key,
    email        varchar(255) unique not null,
    position     enum ( 'Owner',
        'Receptionist',
        'Teacher',
        'Student'
        ),
    enabled      bit(1),
    password     varchar(255)        not null,
    first_name   varchar(100),
    last_name    varchar(100),
    birthday     date,
    address      varchar(255),
    uuid         varchar(255),
    phone_number varchar(255),
    registered   datetime,
    state        varchar(255),
    username     varchar(255)        not null,
    zip_code     varchar(255)
);
CREATE TABLE jwt_user_role
(
    jwt_user_id bigint       NOT NULL,
    role        varchar(100) NOT NULL,
    PRIMARY KEY (jwt_user_id, role),
    FOREIGN KEY (jwt_user_id) REFERENCES jwt_user (id)
);
create table lesson_credit
(
    id            bigint                                  not null auto_increment,
    amount        integer                                 not null,
    price         double precision                        not null,
    purchase_time datetime,
    status        ENUM ('USED', 'AVAILABLE', 'CANCELLED') not null not null,
    accepter_id   bigint,
    student_id    bigint,
    primary key (id)
) engine = InnoDB;
create table lesson_type
(
    id   bigint not null auto_increment,
    type varchar(255),
    primary key (id)
) engine = InnoDB;
create table owner
(
    id         bigint not null,
    company_id bigint,
    primary key (id)
) engine = InnoDB;
create table plain_lesson
(
    id          bigint not null,
    comment     varchar(255),
    date        date,
    lesson_type varchar(255),
    time        time,
    studio_id   bigint,
    teacher_id  bigint,
    primary key (id)
) engine = InnoDB;
create table receptionist
(
    id bigint not null,
    primary key (id)
) engine = InnoDB;
create table receptionist_studios
(
    receptionist_id bigint not null,
    studios_id      bigint not null,
    primary key (receptionist_id, studios_id)
) engine = InnoDB;
create table registration_voucher
(
    id            bigint                                               not null auto_increment,
    account_type  enum ('Owner', 'Receptionist', 'Teacher', 'Student') not null,
    code          varchar(255),
    date          datetime,
    is_executed   bit                                                  not null,
    issuer_id     bigint,
    registerer_id bigint,
    primary key (id)
) engine = InnoDB;
create table registration_voucher_studios
(
    registration_voucher_id bigint not null,
    studios_id              bigint not null,
    primary key (registration_voucher_id, studios_id)
) engine = InnoDB;
create table schedule_entity
(
    id        bigint not null auto_increment,
    comment   varchar(255),
    date      date,
    time      time,
    studio_id bigint,
    primary key (id)
) engine = InnoDB;
create table schedule_entity_participants
(
    schedule_entity_id bigint not null,
    participants_id    bigint not null,
    primary key (schedule_entity_id, participants_id)
) engine = InnoDB;
create table schedule_slot
(
    id        bigint not null auto_increment,
    time      time,
    studio_id bigint,
    primary key (id)
) engine = InnoDB;
create table standing_appointment
(
    id          bigint not null,
    comment     varchar(255),
    date        date,
    lesson_type varchar(255),
    time        time,
    studio_id   bigint,
    teacher_id  bigint,
    primary key (id)
) engine = InnoDB;
create table student_teacher
(
    student_id bigint not null,
    teacher_id bigint not null,
    primary key (student_id, teacher_id)
) engine = InnoDB;
create table students
(
    comment      varchar(255),
    dance_level  varchar(255),
    home_address varchar(255),
    telegram     varchar(255),
    work_address varchar(255),
    id           bigint not null,
    account_id   bigint,
    studio_id    bigint,
    primary key (id)
) engine = InnoDB;
create table studios
(
    id           bigint not null auto_increment,
    abbreviation varchar(255),
    address      varchar(255),
    studio_name  varchar(255),
    company_id   bigint,
    primary key (id)
) engine = InnoDB;
create table studios_lesson_types
(
    studio_id       bigint not null,
    lesson_types_id bigint not null,
    primary key (studio_id, lesson_types_id)
) engine = InnoDB;
create table teachers
(
    id bigint not null,
    primary key (id)
) engine = InnoDB;
create table teachers_studios
(
    teacher_id bigint not null,
    studios_id bigint not null,
    primary key (teacher_id, studios_id)
) engine = InnoDB;
create table transaction
(
    id               bigint                                    not null auto_increment,
    date             datetime,
    description      VARCHAR(255),
    payment_type     ENUM ('Cash', 'Check', 'MC', 'Visa', 'Discover', 'Amex', 'GiftCert', 'Special'),
    transaction_type ENUM ('PayOwed', 'Owed', 'BalanceChange') not null,
    value            float                                     not null,
    accepter_id      bigint                                    not null,
    account_id       bigint,
    primary key (id)
) engine = InnoDB;
alter table jwt_user
    add constraint UK_59ttfuwf4270m0put2i9fdgjy unique (email);
alter table jwt_user
    add constraint UK_oy60o28lsxes9523p97mkp3b4 unique (username);
alter table studios_lesson_types
    add constraint UK_qixlxkjjqh5emdibxqpgbuqrj unique (lesson_types_id);
alter table attendance
    add constraint FK7121lveuhtmu9wa6m90ayd5yg foreign key (student_id) references students (id);
alter table canceled_lesson
    add constraint FKlpj06xltvq5ih8k8lr0ujbptg foreign key (attendance_id) references attendance (id);
alter table company
    add constraint FK5tt9vbqugth6ump43o241yk92 foreign key (owner_id) references owner (id);
alter table jwt_user_role
    add constraint FKej3nxk0eic7easkle77eir422 foreign key (jwt_user_id) references jwt_user (id);
alter table lesson_credit
    add constraint FKii4ja7qm4fwvilpgh8vnuwh2x foreign key (accepter_id) references jwt_user (id);
alter table lesson_credit
    add constraint FKqp8povbnepkvos8wg18r3c19c foreign key (student_id) references students (id);
alter table owner
    add constraint FK135bc9907ly3om0729el1pl34 foreign key (company_id) references company (id);
alter table owner
    add constraint FK6cvrscw2di82q2yp6443nhlrf foreign key (id) references jwt_user (id);
alter table plain_lesson
    add constraint FK_kt6d9l4p2uh91lq80xhylp1dx foreign key (studio_id) references studios (id);
alter table plain_lesson
    add constraint FK_92w39gdgwslumurwh8nem3qf2 foreign key (teacher_id) references teachers (id);
alter table receptionist
    add constraint FK9emxj5jo60w9k97bkqjev9ce7 foreign key (id) references jwt_user (id);
alter table receptionist_studios
    add constraint FK48kjamal6mmebweqdivt8xhq7 foreign key (studios_id) references studios (id);
alter table receptionist_studios
    add constraint FKhti1mt4uklqtnnuay2lfle0fc foreign key (receptionist_id) references receptionist (id);
alter table registration_voucher
    add constraint FKfmcj5wy1h6jmvds5aw25geupg foreign key (issuer_id) references jwt_user (id);
alter table registration_voucher
    add constraint FK8qhvoxlcl8vqdtjcxkwcinye4 foreign key (registerer_id) references jwt_user (id);
alter table registration_voucher_studios
    add constraint FKglw2xe9t7f055q0c715eptwim foreign key (studios_id) references studios (id);
alter table registration_voucher_studios
    add constraint FKc2xn4hiyolamn3msrx4msgu1d foreign key (registration_voucher_id) references registration_voucher (id);
alter table schedule_entity
    add constraint FKs81jeuk8tx7i421nt6sen9m25 foreign key (studio_id) references studios (id);
alter table schedule_entity_participants
    add constraint FKacvga4p9chy1ogi54jh63wkei foreign key (participants_id) references jwt_user (id);
alter table schedule_entity_participants
    add constraint FKl9q77bjajqg8aui9jqpujx3dn foreign key (schedule_entity_id) references schedule_entity (id);
alter table schedule_slot
    add constraint FKasbn5jua8wibi9f9t76a1tj9d foreign key (studio_id) references studios (id);
alter table standing_appointment
    add constraint FK_thybc1gfya42rlxb2p2wov00k foreign key (studio_id) references studios (id);
alter table standing_appointment
    add constraint FK_tomgc3pivfk8kytouq82jhbcm foreign key (teacher_id) references teachers (id);
alter table student_teacher
    add constraint FKiloytcycb3gg01anmvtrjsl6v foreign key (teacher_id) references teachers (id);
alter table student_teacher
    add constraint FK627e2hwcyo6k3ijlfg9iyl8ca foreign key (student_id) references students (id);
alter table students
    add constraint FK7o4n10f0l5utl8fyyjjr9tpax foreign key (account_id) references account (id);
alter table students
    add constraint FK7kbke3n7ejfdiwv8arh31a4y5 foreign key (studio_id) references studios (id);
alter table students
    add constraint FK4l5g82bttv6yfdif7bnd6w42n foreign key (id) references jwt_user (id);
alter table studios
    add constraint FKp7vkr3qms1a315t7oxqd9me99 foreign key (company_id) references company (id);
alter table studios_lesson_types
    add constraint FK71ef1bshtruskl4fqgv5kde0g foreign key (lesson_types_id) references lesson_type (id);
alter table studios_lesson_types
    add constraint FKchj7ptfqutr2hjf806m23qmud foreign key (studio_id) references studios (id);
alter table teachers
    add constraint FKowwjmv9n2ba7wbksvp0clp9vf foreign key (id) references jwt_user (id);
alter table teachers_studios
    add constraint FK2ycw2j2ldxc51n01jpvc3131c foreign key (studios_id) references studios (id);
alter table teachers_studios
    add constraint FK3lvg5h7405wxwwgxpxhw70y7p foreign key (teacher_id) references teachers (id);
alter table transaction
    add constraint FKaj0g8ik8sc00vimdvocq3slau foreign key (accepter_id) references jwt_user (id);
alter table transaction
    add constraint FK6g20fcr3bhr6bihgy24rq1r1b foreign key (account_id) references account (id);