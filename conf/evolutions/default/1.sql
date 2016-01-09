# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table system_document (
  system_document_id        bigserial not null,
  document_name             varchar(255),
  published_date            timestamp,
  document_content          text,
  constraint pk_system_document primary key (system_document_id))
;




# --- !Downs

drop table if exists system_document cascade;

