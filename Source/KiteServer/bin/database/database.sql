create table usuario (
	-- id integer not null auto_increment,
	-- id integer GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1),
	nome varchar(80) not null,
	nickname varchar (30) not null,
	nick varchar(80) not null,
	status char(1) not null,
	senha varchar(32) not null,
	img varchar(50048) not null,
	dataNasc date not null,
	sexo char(1) not null,
	msg varchar(100),
	flag integer not null,
	-- constraint USUARIO_PK primary key (id)
	constraint USUARIO_PK primary key (nickname)
);

create table friendship (
	requester varchar(30) not null, --nickname de quem adiciona
	requested varchar(30) not null, --nickname de quem foi adicionado
	accept char(1) not null,
	constraint friendship_pk primary key (requester, requested),
	constraint friendship_reqr_fk foreign key (requester) references usuario(nickname),
	constraint friendship_reqd_fk foreign key (requested) references usuario(nickname),
	constraint friendship_itself check(requester != requested) 
);