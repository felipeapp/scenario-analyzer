/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '22/05/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoEnsinoIndividual;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;



/** 
 * DAO responsável por consultas personalizadas de turmas. Essas Consultas tem fins específicos como o controle de turmas abertas sem solicitação, por exemplo. 
 * @author Eric Moura
 *
 */
public class RelatorioTurmaSqlDao extends AbstractRelatorioSqlDao {

	/**
	 * Método que retorna o Relatório de Turmas
	 * @param centro
	 * @param departamento
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String,Object>> findListaTurma(Integer ano, Integer periodo, Unidade unidade, Curso curso, SituacaoTurma situacaoTurma, HashMap<Integer,Boolean> filtros) throws DAOException{
		//sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				" select t.id_turma," +
				" t.id_situacao_turma," +
				" t.codigo as turma_codigo," +
				" t.ano," +
				" t.periodo, " +
				" un_centro.id_unidade as id_centro," +
				" un_centro.sigla as centro_depto, " + 
				" un_dep.id_unidade as id_departamento," +
				" un_dep.nome as departamento, " +
				" st.descricao as situacao_turma_desc, " +
				" ccd.codigo as componente_codigo," +
				" ccd.nome as componente_nome," +
				" ccd.ch_total, " + 
				" array_to_string(array(" + 
				" select pessoa.nome||' ('||ch_dedicada_periodo||'h)'" + 
				" from ensino.turma " + 
				" inner join ensino.docente_turma using (id_turma) " + 
				" left join rh.servidor sin on (id_docente = id_servidor) " + 
				" left join ensino.docente_externo dex using (id_docente_externo)" + 
				" left join comum.pessoa on (sin.id_pessoa = pessoa.id_pessoa or dex.id_pessoa = pessoa.id_pessoa)" + 
				" where id_turma = t.id_turma), ', ') as docentes_turma," +
				" un_curso.sigla as unidade_curso," +
				" c.nome||coalesce('-'||r_habilitacao.nome,'')||coalesce('-'||r_turno.sigla,'')||coalesce('-'||r_grau.descricao,'') as curso_reserva, " + 
				" rc.numero_vagas," +
				" rc.vagas_atendidas," +
				" rc.vagas_ocupadas,  " +
				" cast(sum( case when mc.id_situacao_matricula = "+SituacaoMatricula.APROVADO.getId()       +" then 1 else 0 end) as double precision) as aprovados, " + 
				" cast(sum( case when mc.id_situacao_matricula = "+SituacaoMatricula.REPROVADO.getId()      +" then 1 else 0 end) as double precision) as reprovados_nota, " +       
				" cast(sum( case when mc.id_situacao_matricula = "+SituacaoMatricula.REPROVADO_FALTA.getId()+" then 1 else 0 end) as double precision) as reprovados_falta, " +       
				" cast(sum( case when mc.id_situacao_matricula = "+SituacaoMatricula.TRANCADO.getId()       +" then 1 else 0 end) as double precision) as trancados, " +       
				" cast(sum( case when mc.id_situacao_matricula = "+SituacaoMatricula.CANCELADO.getId()      +" then 1 else 0 end) as double precision) as cancelados, " +       
				" cast(sum( case when mc.id_situacao_matricula = "+SituacaoMatricula.EM_ESPERA.getId()      +" then 1 else 0 end) as double precision) as espera, " +
				" cast(sum( case when mc.id_situacao_matricula = "+SituacaoMatricula.MATRICULADO.getId()    +" then 1 else 0 end) as double precision) as matriculados, " +       
				" cast(sum( case when mc.id_situacao_matricula = "+SituacaoMatricula.EXCLUIDA.getId()       +" then 1 else 0 end) as double precision) as excluidos " +
				" from ensino.turma t " +
				" join ensino.situacao_turma st on t.id_situacao_turma = st.id_situacao_turma " + 
				" join ensino.componente_curricular cc on t.id_disciplina = cc.id_disciplina " +
				" join ensino.componente_curricular_detalhes ccd on cc.id_detalhe = ccd.id_componente_detalhes " +  
				" join comum.unidade un_dep on cc.id_unidade = un_dep.id_unidade   " +
				" join comum.unidade un_centro on un_dep.id_gestora = un_centro.id_unidade " + 
				" left join graduacao.reserva_curso rc on rc.id_turma = t.id_turma " +
				" left join graduacao.matriz_curricular matriz on rc.id_matriz_curricular = matriz.id_matriz_curricular " +
				" left join curso c_reserva on c_reserva.id_curso = rc.id_curso " +
				" left join graduacao.habilitacao r_habilitacao on matriz.id_habilitacao = r_habilitacao.id_habilitacao " +
				" left join ensino.turno r_turno on r_turno.id_turno = matriz.id_turno " +
				" left join ensino.grau_academico r_grau on r_grau.id_grau_academico = matriz.id_grau_academico" + 
				" left join curso c on matriz.id_curso = c.id_curso " +
				" left join comum.municipio m on c.id_municipio = m.id_municipio " + 
				" left join comum.unidade un_curso on c.id_unidade = un_curso.id_unidade " +   
				" left join ensino.matricula_componente mc on mc.id_turma = t.id_turma " +
				" where cc.nivel =  '" + NivelEnsino.GRADUACAO + "' " + 
				" and t.id_situacao_turma not in " + gerarStringIn( SituacaoTurma.getSituacoesInvalidas() ) );
		
		if(filtros.get(DEPARTAMENTO))
			sqlconsulta.append(" and  un_dep.id_unidade = " + unidade.getId());
		else if(filtros.get(CENTRO)){
			if (unidade.getTipoAcademica() != null && unidade.getTipoAcademica() == TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA){
				sqlconsulta.append(" and  un_dep.id_unidade = " + unidade.getId());
			} else {
				sqlconsulta.append(" and  un_centro.id_unidade = " + unidade.getId());				
			}			
		}			

		if(filtros.get(SITUACAO_TURMA)){
			sqlconsulta.append(" and  st.id_situacao_turma = " + situacaoTurma.getId());
		}

		if(filtros.get(RESERVA_CURSO)){
			sqlconsulta.append(" and  (c.id_curso = " + curso.getId() + " or c_reserva.id_curso = " + curso.getId() +  " ) ");
		}

		if(filtros.get(ANO_PERIODO)){
			sqlconsulta.append("     and t.ano = "+ano);
			sqlconsulta.append("     and t.periodo = "+periodo);
		}

		sqlconsulta.append(" group by t.id_turma," +
				" t.id_situacao_turma," +
				" t.codigo," +
				" t.ano," +
				" t.periodo, " +
				" un_centro.id_unidade," +
				" un_centro.sigla," +
				" un_centro.nome, " +
				" un_dep.id_unidade," +
				" un_dep.nome," +
				" st.descricao,  " +
				" ccd.codigo," +
				" ccd.nome," +
				" ccd.ch_total," +
				" un_curso.sigla," +
				" c.nome,  " +
				" rc.numero_vagas," +
				" rc.vagas_atendidas," +
				" rc.vagas_ocupadas," +
				" r_habilitacao.nome," +
				" r_turno.sigla," +
				" r_grau.descricao");
		sqlconsulta.append(" order by un_centro.nome," +
				" un_dep.nome," +
				" un_dep.id_unidade," +
				" t.ano," +
				" t.periodo," +
				" ccd.nome," +
				" ccd.codigo," +
				" t.id_turma," +
				" curso_reserva," +
				" c.nome");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Método que retorna o Relatório Turmas por quantidade de docentes
	 * @param centro
	 * @param departamento
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String,Object>> findListaTurmasDocentes(Unidade depto, int ano, int periodo) throws DAOException{
		//sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(" select t.ano, t.periodo, u.nome as depto, ccd.nome as disciplina, ccd.codigo, " +
				" ccd.ch_total, count (dt.id_docente) as qtd_docentes " +
				" from ensino.turma t, ensino.componente_curricular cc, ensino.docente_turma dt, " +
				"        comum.unidade u, ensino.componente_curricular_detalhes ccd " +
				" where cc.nivel = 'G' " +
				" and u.id_unidade = cc.id_unidade " +
				" and cc.id_detalhe = ccd.id_componente_detalhes " +
				" and cc.id_disciplina = t.id_disciplina " +
				" and t.id_turma = dt.id_turma ");

		if(ano !=0 && periodo !=0){
			sqlconsulta.append("     and t.ano = "+ano);
			sqlconsulta.append("     and t.periodo = "+periodo);
		}
		if(depto !=null && depto.getId()!=0)
			sqlconsulta.append(" and cc.id_unidade = "+depto.getId());

		sqlconsulta.append(" group by t.ano, t.periodo, u.nome, ccd.nome, ccd.codigo, ccd.ch_total " +
				" order by t.ano, t.periodo, u.nome, ccd.nome, ccd.codigo  ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}

		return result;
	}


	/**
	 * Método que retorna uma Relação por departamento das disciplinas de estágio com a
	 * carga horária e o número de professores envolvidos
	 * @param centro
	 * @param departamento
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String,Object>> findListaTurmasChEstagio(Unidade depto, int ano, int periodo) throws DAOException{
		//sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(" select u.nome as depto_nome, ccd.nome as componente_nome, t.ano,t.periodo, count(s.id_servidor) as qtd_docente " +
				" from comum.unidade u, ensino.turma t, ensino.componente_curricular cc, " +
				" ensino.componente_curricular_detalhes ccd, ensino.docente_turma dt, rh.servidor s " +
				" where u.id_unidade = cc.id_unidade " +
				" and   cc.id_detalhe = ccd.id_componente_detalhes " +
				" and   cc.id_disciplina = t.id_disciplina " +
				" and   t.id_turma = dt.id_turma " +
				" and   dt.id_docente = s.id_servidor " +
				" and   ccd.ch_estagio > 0 ");

		if(ano !=0 && periodo !=0){
			sqlconsulta.append("     and t.ano = "+ano);
			sqlconsulta.append("     and t.periodo = "+periodo);
		}
		if(depto !=null && depto.getId()!=0)
			sqlconsulta.append(" and u.id_unidade = "+depto.getId());

		sqlconsulta.append(" group by u.nome, ccd.nome, t.ano,t.periodo " +
				" order by u.nome, ccd.nome, t.ano,t.periodo ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}

		return result;
	}

	/** Retorna uma lista de turmas ofertadas no curso.
	 * @param curso
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Turma> findListaTurmasOfertadasCurso(Curso curso, int ano, int periodo) throws DAOException{
		try {
			//sql de consulta
			StringBuilder sqlconsulta = new StringBuilder(" select t.id_turma, s.id_servidor, p.nome as docente_nome, s2.id_docente_externo, p2.nome as docente_externo_nome, " +
					" dt.ch_dedicada_periodo, t.ano, t.periodo, t.codigo as codigo_turma, t.local, t.descricao_horario, t.capacidade_aluno, " +
					" c.id_disciplina, c.codigo, c.nivel, d.nome, u.id_unidade, u.nome as unidade_nome" +
					" from graduacao.reserva_curso r, " +
					" ensino.turma t " +
					"	left outer join ensino.docente_turma dt on (t.id_turma = dt.id_turma)" +
					"	left outer join rh.servidor s on (dt.id_docente = s.id_servidor)" +
					"	left outer join comum.pessoa p on (s.id_pessoa = p.id_pessoa)," +
					" ensino.turma t2" +
					"	left outer join ensino.docente_turma dt2 on (t2.id_turma = dt2.id_turma)" +
					"	left outer join ensino.docente_externo s2 on (dt2.id_docente_externo = s2.id_docente_externo)" +
					"	left outer join comum.pessoa p2 on (s2.id_pessoa = p2.id_pessoa)," +
					" ensino.componente_curricular c, ensino.componente_curricular_detalhes d, comum.unidade u" +
					" where t.id_turma = t2.id_turma" +
					" and r.id_turma = t.id_turma" +
					" and t.id_disciplina = c.id_disciplina" +
					" and c.id_disciplina = d.id_componente" +
					" and c.id_unidade = u.id_unidade" +
					" and c.nivel = 'G'" +
					" and t.ano = " +ano+
					" and t.periodo = " +periodo+
					" and (r.id_matriz_curricular in" +
					"        (" +
					"	select id_matriz_curricular" +
					"	from graduacao.matriz_curricular" +
					"	where id_curso = " +curso.getId()+
					"        )" +
					"    or r.id_curso = " +curso.getId()+
					"    )" +
					" group by t.id_turma, s.id_servidor, docente_nome, s2.id_docente_externo, docente_externo_nome, " +
					" dt.ch_dedicada_periodo, t.ano, t.periodo, codigo_turma, t.local, t.descricao_horario, t.capacidade_aluno, " +
					" c.id_disciplina, c.codigo, c.nivel, d.nome, u.id_unidade, unidade_nome " +
					" order by unidade_nome, d.nome");
			
			Query q = getSession().createSQLQuery(sqlconsulta.toString());
	
			List<Object[]> lista = q.list();
			ArrayList<Turma> result = new ArrayList<Turma>();
	
			int idAtual = 0;
			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = lista.get(a);
	
				Turma t = new Turma();
				t.setId((Integer) colunas[col++]);
				if (idAtual == t.getId() && idAtual > 0)
					t = result.get(result.size()-1);
				DocenteTurma dt = new DocenteTurma();
				Integer idDocente = (Integer) colunas[col++];
				String nDocente = (String) colunas[col++];
				Integer idDocenteExt = (Integer) colunas[col++];
				String nDocenteExt = (String) colunas[col++];
				Integer ch = (Integer) colunas[col++];
				if(idDocente != null){
					dt.getDocente().setId(idDocente);
					dt.setChDedicadaPeriodo(ch);
					if (dt.getDocente().getPessoa() != null)
						dt.getDocente().getPessoa().setNome(nDocente);
					t.getDocentesTurmas().add(dt);
				}
				if(idDocenteExt != null){
					dt.setDocenteExterno(new DocenteExterno(idDocenteExt));
					dt.setChDedicadaPeriodo(ch);
					if(dt.getDocenteExterno().getPessoa() != null)
						dt.getDocenteExterno().getPessoa().setNome(nDocenteExt);
					t.getDocentesTurmas().add(dt);
				}
				if (idAtual == t.getId() && idAtual > 0)
					continue;
				t.setAno((Integer) colunas[col++]);
				t.setPeriodo((Integer) colunas[col++]);
				t.setCodigo((String) colunas[col++]);
				t.setLocal((String) colunas[col++]);
				t.setDescricaoHorario((String) colunas[col++]);
				t.setCapacidadeAluno((Integer) colunas[col++]);
				t.setDisciplina(new ComponenteCurricular((Integer) colunas[col++]));
				t.getDisciplina().setCodigo((String) colunas[col++]);
				t.getDisciplina().setNivel((Character) colunas[col++]);
				t.getDisciplina().setNome((String) colunas[col++]);
//				t.setSituacaoTurma(new SituacaoTurma());
//				t.getSituacaoTurma().setDescricao((String) colunas[col++]);
				t.getDisciplina().setUnidade(new Unidade((Integer) colunas[col++]));
				t.getDisciplina().getUnidade().setNome((String) colunas[col++]);
				
				result.add(t);
				idAtual = t.getId();
			}
			return result;			
		} catch (HibernateException e) {
			e.printStackTrace();
			throw new HibernateException(e);
		} catch (DAOException e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	/**
	 * Gera um relatório quantitativo dos alunos reprovados por falta ou por nota no ano e período informado.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> relatorioSituacaoTurma(Integer ano, Integer periodo) throws DAOException {
		String sqlconsulta = "select count(id_turma), st.descricao, cc.nivel from ensino.turma t join ensino.situacao_turma st on t.id_situacao_turma = st.id_situacao_turma"+
		" join ensino.componente_curricular cc on cc.id_disciplina = t.id_disciplina"+
		" where ano = "+ano+" and periodo = "+periodo+""+
		" and id_turma in ( select id_turma from ensino.matricula_componente )"+
		" group by st.descricao, cc.nivel"+
		" order	by cc.nivel, st.descricao";
		
		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}

	/**
	 * Gera um relatório com todos os Discentes que Solicitaram Disciplinas de férias, bem como o
	 * status que a mesma se encontra, se foi aprovada, cancelada, negada, atendida.
	 *
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> relatorioSolicitacaoDisciplinaFerias(Integer ano, Integer periodo) throws DAOException {
		String sqlconsulta = "select c.nome as curso, ccd.codigo, ccd.nome as componente_curricular,"+
					" case when situacao = "+SolicitacaoEnsinoIndividual.SOLICITADA+" then 'SOLICITADA'"+
					" when situacao = "+SolicitacaoEnsinoIndividual.CANCELADA+" then 'CANCELADA'"+
					" when situacao = "+SolicitacaoEnsinoIndividual.ATENDIDA+" then 'ATENDIDA'"+
					" when situacao = "+SolicitacaoEnsinoIndividual.NEGADA+" then 'NEGADA'"+
					" when situacao = "+SolicitacaoEnsinoIndividual.CANCELADA_POR_MATRICULA+" then 'CANCELADA POR MATRÍCULA'"+
					" else 'DESCONHECIDA' end as situacao, d.matricula, p.nome as aluno"+
					" from graduacao.solicitacao_ensino_individual sei"+
					" join discente d using(id_discente) join curso c using (id_curso)"+
					" join comum.pessoa p using(id_pessoa)"+
					" join ensino.componente_curricular cc on cc.id_disciplina = sei.id_componente_curricular"+
					" join ensino.componente_curricular_detalhes ccd on ccd.id_componente_detalhes = cc.id_detalhe"+
					" where ano = "+ano+" and periodo = "+periodo+" and sei.tipo = 2"+
					" order by c.nome, ccd.codigo, situacao, p.nome";
		
		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}

	/**
	 * Gera um relatório com todas as Turma que não foram consolidadas, tendo com base o ano, o período e se deve ser incluída
	 * as turma de Ead.
	 *
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> relatorioTurmaNaoConsolidada(Integer ano, Integer periodo, boolean ead) throws DAOException {

		String strSituacoesMatricula = gerarStringIn( SituacaoMatricula.getSituacoesComVinculoTurma() );
		String strSituacoesTurma = gerarStringIn( SituacaoTurma.getSituacoesAbertas() );
		
		String sqlConsultaMatricula = "ensino.matricula_componente " +
				"		where id_turma = t.id_turma " +
				"		and id_situacao_matricula in " + strSituacoesMatricula;
		
		String sqlConsultaGeral = 
				"	select DISTINCT t.codigo as turma, cc.codigo, ccd.nome_ascii as disciplina, " +
				"	u.nome_ascii as departamento," + 
				"	( select  count( id_matricula_componente ) from " + sqlConsultaMatricula + " ) " +
				"	as matriculados, u.nome, cc.codigo " +
				"	from ensino.turma t " +
				"	inner join ensino.componente_curricular cc on cc.id_disciplina = t.id_disciplina " +
				"	inner join ensino.componente_curricular_detalhes ccd on cc.id_detalhe = ccd.id_componente_detalhes " + 
				"	inner join comum.unidade u on cc.id_unidade = u.id_unidade " +
				"	where t.ano = :ano  " +
				"	and t.periodo = :periodo  " +
				"	and t.agrupadora = falseValue()  " +
				"	and exists (  " +
				"		select id_turma from " + sqlConsultaMatricula + 
				"	) " +
				"	and cc.nivel = :nivel " +
				"	and t.id_situacao_turma in " + strSituacoesTurma;
		if (!ead) {
			sqlConsultaGeral += " and t.id_polo is null ";
		}
		sqlConsultaGeral += "	order by u.nome, cc.codigo";
		
		SQLQuery q = getSession().createSQLQuery(sqlConsultaGeral);
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setCharacter("nivel", NivelEnsino.GRADUACAO);
		q.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
		
		List<Map<String, Object>> result;
		try {
			result = q.list();		
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}
	/**
	 * Gera um relatório de turmas que foram abertas sem solicitação.
	 * @param ano
	 * @param periodo
	 * @param situacaoTurma
	 * @param countEnsinoDistancia
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> relatorioTurmaAbertaSemSolicitacao(Integer ano, Integer periodo, Integer situacaoTurma, boolean countEnsinoDistancia) throws DAOException {
		
		StringBuilder sqlconsulta = new StringBuilder(" select cc.codigo as codigo_componente, " +
				" ccd.nome as nome_componente, t.codigo as codigo_turma, u.nome as departamento, " +
				" t.capacidade_aluno as vagas, " +
				
				" (select count(mc.id_turma) from  ensino.turma t1 " +
				"right join ensino.matricula_componente mc on mc.id_turma = t.id_turma  " +
				"where mc.id_situacao_matricula in ( 4,6,7,1,2,5 )  and t1.id_turma = t.id_turma " +
				"group by t.id_turma ) as numero_matriculados, " +
				
				" t.data_cadastro as data_criacao_turma, s.descricao as situacao_turma, ");
		
		sqlconsulta.append(" (select distinct u.login from   comum.usuario u " +
				"inner join comum.registro_entrada re on (u.id_usuario = re.id_usuario) " +
				"where re.id_entrada = t.id_registro_cadastro) as usuario ");
		
		sqlconsulta.append(" from ensino.turma t inner join ensino.componente_curricular cc on t.id_disciplina = cc.id_disciplina " +
				" inner join ensino.componente_curricular_detalhes ccd on ccd.id_componente_detalhes = cc.id_detalhe" +
				" inner join ensino.situacao_turma s on s.id_situacao_turma = t.id_situacao_turma " +
				" inner join comum.unidade u on cc.id_unidade = u.id_unidade ");
				
		sqlconsulta.append(" where cc.nivel = 'G' " +
				" and t.id_turma not in ( select id_turma from graduacao.turma_solicitacao_turma ) ");
		
		sqlconsulta.append(" and t.ano = " + ano + "");
	
		sqlconsulta.append(" and t.periodo = " + periodo +" ");
		
		if(situacaoTurma > 0){
			sqlconsulta.append(" and s.id_situacao_turma = "+ situacaoTurma +" ");
		}
		
		if(!countEnsinoDistancia){
			sqlconsulta.append(" and t.id_polo is null ");
		}
				
		sqlconsulta.append(" order by u.nome, t.data_cadastro ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}
	
	/**
	 * Geração do relatório tomando com base o ano, período e a forma de busca das turma não solicitadas pelos coordenadores.
	 *
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> relatorioCursoTurma(Integer ano, Integer periodo, boolean filtro) throws DAOException {

		StringBuilder sqlconsulta = new StringBuilder();
			sqlconsulta.append("select c.nome as curso, u.nome as centro,"+ 
				" sum (case when situacao = 1 then 1 else 0 end) as aberta,"+
				" sum (case when situacao = 2 then 1 else 0 end) as solicitacao_alteracao,"+
				" sum (case when situacao = 3 then 1 else 0 end) as atendida_alteracao,"+
				" sum (case when situacao = 4 then 1 else 0 end) as atendida_parcialmente,"+
				" sum (case when situacao = 5 then 1 else 0 end) as atendida,"+
				" sum (case when situacao = 6 then 1 else 0 end) as negada,"+
				" sum (case when situacao = 7 then 1 else 0 end) as removida"+
				" from curso as c left join graduacao.solicitacao_turma st on"+
				" (c.id_curso = st.id_curso  and st.ano = "+ano+" and periodo = "+periodo+"), comum.unidade as u"+  
				" where c.nivel = '"+NivelEnsino.GRADUACAO+"' and c.id_unidade = u.id_unidade  and c.id_curso in (select id_curso from discente where status = "+Discente.REGULAR+")"+
				" group by c.id_curso, c.codigo, c.nome, c.nivel,u.nome"+ 
				" order by u.nome, c.codigo");
		
		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}
	
	/**
	 * Geração do relatório quantitativo de turmas e disciplinas por departamento tomando com base o ano, período.
	 *
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> relatorioQuantTurmaDisciplinaPorDepto(Integer ano, Integer periodo, boolean filtro) throws DAOException {

		StringBuilder sqlconsulta = new StringBuilder();
			sqlconsulta.append(
				"SELECT auxTable.ano, auxTable.periodo, auxTable.ca as centro, auxTable.depto as departamento, " +
				" count(auxTable.disciplina) AS qt_disciplina , sum(auxTable.qt_turma) AS qt_turma " +
				"FROM (SELECT t.ano, t.periodo, un1.nome AS depto, un2.nome AS ca , cc.codigo AS disciplina, " +
				"		count(t.id_turma) AS qt_turma" +
				"		FROM ensino.turma t " +
				"		JOIN ensino.componente_curricular cc ON cc.id_disciplina = t.id_disciplina" +
				"		JOIN comum.unidade un1 ON un1.id_unidade = cc.id_unidade" +
				"		JOIN comum.unidade un2 ON un2.id_unidade = un1.id_gestora" +
				"		WHERE t.ano = " + ano +
				"		AND t.periodo = " + periodo +
				"		AND cc.nivel = '"+NivelEnsino.GRADUACAO+"'" +
				"		AND t.id_situacao_turma IN " + gerarStringIn(SituacaoTurma.getSituacoesValidas()) +
				"       AND t.agrupadora = falseValue() " +
				"		GROUP BY cc.codigo, un1.nome, un2.nome, t.ano, t.periodo) AS auxTable " +
				"GROUP BY auxTable.ano, auxTable.periodo, auxTable.depto, auxTable.ca " +
				"ORDER BY auxTable.ano, auxTable.periodo, auxTable.ca ASC, auxTable.depto ASC");
			
		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}

}