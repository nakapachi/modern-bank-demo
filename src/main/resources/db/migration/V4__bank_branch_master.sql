create table bank_branch (
    code varchar(3) primary key,
    bank_code varchar(4) not null,
    name varchar(80) not null,
    location varchar(120) not null,
    connection varchar(120) not null
);

insert into bank_branch (bank_code, code, name, location, connection) values
    ('0200', '001', '本店営業部', '東京都', 'はと銀行本店'),
    ('0200', '002', '井草支店', '東京都杉並区', '井草八幡宮'),
    ('0200', '003', '深川支店', '東京都江東区', '富岡八幡宮'),
    ('0200', '004', '鎌倉支店', '神奈川県鎌倉市', '鶴岡八幡宮'),
    ('0200', '005', '八幡支店', '京都府八幡市', '石清水八幡宮'),
    ('0200', '006', '宇佐支店', '大分県宇佐市', '宇佐神宮'),
    ('0200', '007', '箱崎支店', '福岡県福岡市東区', '筥崎宮');

alter table bank_account add column branch_code varchar(3) not null default '001';
alter table bank_account add constraint fk_bank_account_branch
    foreign key (branch_code) references bank_branch(code);
create index idx_bank_account_branch on bank_account(branch_code);
