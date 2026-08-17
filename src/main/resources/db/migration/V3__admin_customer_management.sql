alter table app_user add column customer_number varchar(10);
alter table app_user add column email varchar(160);
alter table app_user add column phone varchar(30);
alter table app_user add column role varchar(20) not null default 'CUSTOMER';
alter table app_user add column status varchar(20) not null default 'ACTIVE';

update app_user set customer_number = 'C' || right('000000' || cast(id as varchar), 6) where role = 'CUSTOMER';
alter table app_user add constraint uk_app_user_customer_number unique (customer_number);

alter table bank_account add column account_type varchar(20) not null default 'ORDINARY';
alter table bank_account add column status varchar(20) not null default 'ACTIVE';
alter table bank_account add column opened_at timestamp with time zone not null default current_timestamp;

create index idx_app_user_role_status on app_user(role, status);
create index idx_bank_account_owner_status on bank_account(owner_id, status);
