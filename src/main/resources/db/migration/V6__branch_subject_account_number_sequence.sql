create table account_number_sequence (
    branch_code varchar(3) not null references bank_branch(code),
    account_type varchar(20) not null,
    next_sequence integer not null,
    last_issued_at timestamp with time zone,
    version bigint not null default 0,
    primary key (branch_code, account_type),
    check (next_sequence between 1 and 99999)
);

insert into account_number_sequence (branch_code, account_type, next_sequence) values
    ('001', 'ORDINARY', 2), ('001', 'SAVINGS', 1),
    ('002', 'ORDINARY', 10002), ('002', 'SAVINGS', 10001),
    ('003', 'ORDINARY', 20002), ('003', 'SAVINGS', 20001),
    ('004', 'ORDINARY', 30001), ('004', 'SAVINGS', 30001),
    ('005', 'ORDINARY', 40001), ('005', 'SAVINGS', 40001),
    ('006', 'ORDINARY', 50001), ('006', 'SAVINGS', 50001),
    ('007', 'ORDINARY', 60001), ('007', 'SAVINGS', 60001);

update ledger_entry set counterparty_account = case counterparty_account
    when '1000009' then '1000017'
    when '1000017' then '1100015'
    when '2000008' then '1200013'
    else counterparty_account
end;

update bank_account set account_number = '9000019' where account_number = '1000009';
update bank_account set account_number = '9000027' where account_number = '1000017';
update bank_account set account_number = '9000035' where account_number = '2000008';
update bank_account set account_number = '1000017' where account_number = '9000019';
update bank_account set account_number = '1100015' where account_number = '9000027';
update bank_account set account_number = '1200013' where account_number = '9000035';
