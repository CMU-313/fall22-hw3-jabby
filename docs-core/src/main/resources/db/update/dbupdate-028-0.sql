alter table T_DOCUMENT add column DOC_AVG_TECH_C varchar(100);
alter table T_DOCUMENT add column DOC_AVG_INTERPERSONAL_C varchar(100);
alter table T_DOCUMENT add column DOC_AVG_FIT_C varchar(100);
alter table T_DOCUMENT add column DOC_NUM_REVIEWS_N int(100);
update T_CONFIG set CFG_VALUE_C = '28' where CFG_ID_C = 'DB_VERSION';