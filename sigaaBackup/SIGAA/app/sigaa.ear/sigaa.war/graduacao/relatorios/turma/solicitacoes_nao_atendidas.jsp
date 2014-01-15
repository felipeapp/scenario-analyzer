--select u.id_unidade, u.nome,
select cc.id_disciplina, cc.codigo, ccd.nome,
sum(case when st.situacao = 6 then 1 else 0 end) as "turmas negadas",
sum(case when st.situacao = 1 then 1 else 0 end) as "turmas não atendidas totalmente",
sum(case when st.situacao in (1,2,3) then 1 else 0 end) as "turmas não atendidas"
from graduacao.solicitacao_turma st 
join ensino.componente_curricular cc on cc.id_disciplina = st.id_componente_curricular
join ensino.componente_curricular_detalhes ccd on cc.id_disciplina = ccd.id_componente
where st.ano = 2008 and st.periodo = 1
group by cc.id_disciplina, cc.codigo, ccd.nome
having sum(case when st.situacao in (1,2,3,6) then 1 else 0 end) > 0
order by cc.codigo

--select u.id_unidade, u.nome,
select cc.id_disciplina, cc.codigo, ccd.nome,
sum(case when st.situacao = 6 then 1 else 0 end) as "turmas negadas",
sum(case when st.situacao = 1 then 1 else 0 end) as "turmas não atendidas totalmente",
sum(case when st.situacao in (1,2,3) then 1 else 0 end) as "turmas não atendidas"
from graduacao.solicitacao_turma st 
join ensino.componente_curricular cc on cc.id_disciplina = st.id_componente_curricular
join ensino.componente_curricular_detalhes ccd on cc.id_disciplina = ccd.id_componente
where st.ano = 2008 and st.periodo = 2
group by cc.id_disciplina, cc.codigo, ccd.nome
having sum(case when st.situacao in (1,2,3,6) then 1 else 0 end) > 0
order by cc.codigo