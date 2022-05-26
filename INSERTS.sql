use primeirocontroller;
-- INSERT DAS ROLES
insert into tb_role VALUES (1,'ROLE_ADMIN');
insert into tb_role VALUES (2,'ROLE_USER');
-- CRIAÇÃO DE UM USER DE ROLE ADMIN DE FORMA MANUAL
insert into tb_clientepf VALUES (8,'14578963200','Luiz',2);
insert into tb_usuario VALUES (2,'14578963200','$2a$10$98Yb9AX.GexVZifRO/CsieRwr3Qlv0CMnI.qlueKd.7LKquEkpxt');
insert into tb_usuarios_roles VALUES (2,1);