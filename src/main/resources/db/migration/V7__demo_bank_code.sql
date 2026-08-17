-- 9999 is an application-local demo code and is not presented as an assigned bank code.
update bank_branch
set bank_code = '9999'
where bank_code = '0200';
