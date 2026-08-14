update ledger_entry
set counterparty_account = case counterparty_account
    when '100000000001' then '1000001'
    when '100000000002' then '1000002'
    when '200000000001' then '2000001'
    else counterparty_account
end
where counterparty_account in ('100000000001', '100000000002', '200000000001');

update bank_account
set account_number = case account_number
    when '100000000001' then '1000001'
    when '100000000002' then '1000002'
    when '200000000001' then '2000001'
    else account_number
end
where account_number in ('100000000001', '100000000002', '200000000001');

alter table bank_account alter column account_number type varchar(7);
alter table ledger_entry alter column counterparty_account type varchar(7);
