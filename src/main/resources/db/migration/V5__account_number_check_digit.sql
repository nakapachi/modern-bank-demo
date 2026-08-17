update ledger_entry set counterparty_account = case counterparty_account
    when '1000001' then '1000009'
    when '1000002' then '1000017'
    when '2000001' then '2000008'
    else counterparty_account
end;

update bank_account set account_number = case account_number
    when '1000001' then '1000009'
    when '1000002' then '1000017'
    when '2000001' then '2000008'
    else account_number
end;
