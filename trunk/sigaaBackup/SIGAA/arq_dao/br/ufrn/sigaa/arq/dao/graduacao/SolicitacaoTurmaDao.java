/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '01/02/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricularPrograma;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscentesSolicitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Dao para consultas relativas à solicitação abertura de turmas
 * @author Leonardo
 * @author Victor Hugo
 */
public class SolicitacaoTurmaDao extends GenericSigaaDAO {

	/**
	 * Busca todas as solicitações de abertura de turmas feitas a um departamento
	 * por um dado curso. Para buscar as solicitações de todos os cursos a um departamento,
	 * informar idCurso igual a zero.
	 *
	 * Se o ano e  o período for nulo retorna as solicitações dos próximos períodos (próximo regular e próximo de férias)
	 *
	 * @param idUnidade unidade do componente da solicitação
	 * @param idCurso  curso solicitante
	 * @param ano ano da solicitação
	 * @param periodo período da solicitação
	 * @param tipoTurma tipo de turma da solicitação
	 * @return
	 * @throws DAOException
	 */
	public List<SolicitacaoTurma> filter(Usuario usuario, List<Unidade> unidades, Integer ano, Integer periodo, Integer tipoTurma, Integer idCurso, Integer idComponente, String horario, Servidor docente) throws DAOException {
		try {

			String projecao = " DISTINCT st.id AS ID, st.ano AS ANO, st.periodo AS PERIODO, " 
				+ " cc.codigo AS COD_COMPONENTE, detalhes.nome AS NOME_COMPONENTE, " 
				+ " st.tipo AS TIPO_TURMA, st.situacao AS SITUACAO, st.horario AS HORARIO, st.vagas AS QTD_VAGAS, " 
				+ " unidade.id AS ID_UNID, unidade.nome AS NOME_UNID, " 
				+ " unidade.sigla AS SIGLA_UNID, curso.id AS ID_CURSO, curso.nome AS NOME_CURSO, " 
				+ " st.dataCadastro AS DT_CADASTRO, programa.id as ID_PROGRAMA, " +
				"	reservas.id as ID_RESERVA " ;

			StringBuilder hql = new StringBuilder( "SELECT " + projecao );
			hql.append( " FROM SolicitacaoTurma st " +
						"	INNER JOIN st.componenteCurricular cc " +
						"	INNER JOIN cc.unidade unidade " +
						" 	INNER JOIN st.curso curso " +
						" 	INNER JOIN cc.detalhes detalhes " +
						"	LEFT JOIN st.turmasSolicitacaoTurmas tst " +
						"	LEFT JOIN st.reservas reservas " +
						"	LEFT JOIN tst.turma t " +
						"	LEFT JOIN t.docentesTurmas dt " +
						"	LEFT JOIN cc.programa programa");
			hql.append( " WHERE unidade.id in " + gerarStringIn(unidades) );
			
			if ( idCurso != null ) 
				hql.append( " AND curso.id = :idCurso" );

			if ( ano != null ) 
				hql.append( " AND st.ano = :ano" );
			
			if ( periodo != null ) 
				hql.append( " AND st.periodo = :periodo" );
			
			if ( horario != null ) 
				hql.append( " AND trim(horario) like :horario" );
			
			if ( idComponente != null ) 
				hql.append( " AND cc.id = :idComponente" );
			
			if ( docente != null ) 
				hql.append( " AND (dt.docente.id = :idDocente" + " OR dt.docenteExterno.id = :idDocente)" );
			
			if ( tipoTurma != null ) {
				hql.append( " AND st.tipo = :tipoTurma" );
			}

			/*
			 * se o ano e o período for nulo retorna as solicitações dos próximos períodos (próximo regular e próximo de férias)
			 */
			if( ano == null && periodo == null ){
				CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(usuario);
				hql.append( " AND (st.periodo = " + cal.getProximoAnoPeriodoRegular().getPeriodo()
					+ " OR st.periodo = " + cal.getPeriodoFeriasVigente() + " ) "
					+ " AND ( st.ano = " + cal.getProximoAnoPeriodoRegular().getAno()
					+ " OR st.ano = " + cal.getAnoFeriasVigente() + ") " );
			}

			hql.append( " ORDER BY cc.codigo asc, curso.id asc " );

			Query q = getSession().createQuery( hql.toString() );
			
			if ( idCurso != null ) 
				q.setInteger("idCurso", idCurso );

			if ( ano != null ) 
				q.setInteger("ano", ano );
			
			if ( periodo != null ) 
				q.setInteger("periodo", periodo );
			
			if ( horario != null ) 
				q.setString("horario", "%" + horario.toUpperCase() + "%");
			
			if ( idComponente != null ) 
				q.setInteger("idComponente", idComponente );
			
			if ( docente != null ) 
				q.setInteger("idDocente", docente.getId() );
			
			if ( tipoTurma != null ) {
				q.setInteger("tipoTurma", tipoTurma );
			}
			
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> lista = q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			
			
			List<SolicitacaoTurma> resultado = new ArrayList<SolicitacaoTurma>();
			for( Map<String, Object> map : lista ){
				
				
				SolicitacaoTurma sol = new SolicitacaoTurma();
				sol.setId( (Integer) map.get("ID") );
				
				if (resultado.contains(sol)) {
					sol = resultado.get( resultado.indexOf(sol) );
				} else {
					sol.setAno( (Short) map.get("ANO") );
					sol.setPeriodo( (Byte) map.get("PERIODO") );
					sol.setTipo( (Integer) map.get("TIPO_TURMA") );
					sol.setSituacao( (Integer) map.get("SITUACAO") );
					sol.setVagas( (Short) map.get("QTD_VAGAS") );
					sol.setHorario( (String) map.get("HORARIO") );
					sol.setDataCadastro( (Date) map.get("DT_CADASTRO") );
					
					sol.setComponenteCurricular( new ComponenteCurricular() );
					sol.getComponenteCurricular().setCodigo( (String) map.get("COD_COMPONENTE") );
					sol.getComponenteCurricular().setDetalhes(new ComponenteDetalhes());
					sol.getComponenteCurricular().getDetalhes().setNome( (String) map.get("NOME_COMPONENTE") );
					
					sol.getComponenteCurricular().setUnidade( new Unidade() );
					sol.getComponenteCurricular().getUnidade().setId( (Integer) map.get("ID_UNID") );
					sol.getComponenteCurricular().getUnidade().setNome( (String) map.get("NOME_UNID") );
					sol.getComponenteCurricular().getUnidade().setSigla( (String) map.get("SIGLA_UNID") );
					sol.getComponenteCurricular().setPrograma(new ComponenteCurricularPrograma());
					if (map.get("ID_PROGRAMA") != null)
						sol.getComponenteCurricular().getPrograma().setId((Integer) map.get("ID_PROGRAMA"));
					
					sol.setCurso( new Curso() );
					sol.getCurso().setId( (Integer) map.get("ID_CURSO") );
					sol.getCurso().setNome( (String) map.get("NOME_CURSO") );
				
					resultado.add(sol);
				}

				if ( ValidatorUtil.isNotEmpty(map.get("ID_RESERVA")) ) {
					ReservaCurso r = new ReservaCurso();
					r.setId((Integer) map.get("ID_RESERVA"));
					
					sol.getReservas().add(r);
				}
				
			}
			
			return resultado;

		}catch (Exception e) {
			throw new DAOException(e);
		}

	}

	
	/**
	 * Retorna os cursos que solicitaram disciplinas do departamento informado
	 * @param idUnidade
	 * @param ano
	 * @param periodo
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> findCursosSolicitantesDepartamento (int idUnidade, Integer ano, Integer periodo, Integer tipo) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();

			hql.append( " SELECT DISTINCT sol.curso " );
			hql.append( " FROM SolicitacaoTurma sol, ComponenteCurricular cc " );
			hql.append( " WHERE sol.componenteCurricular.id = cc.id " );
			//hql.append( " AND rc.solicitacao.id = sol.id " );
			hql.append( " AND sol.componenteCurricular.unidade.id = :idUnidade " );
			if( ano != null )
				hql.append( " AND sol.ano = :ano " );
			if( periodo != null )
				hql.append( " AND sol.periodo = :periodo " );
			if( tipo != null )
				hql.append( " AND sol.tipo = :tipo " );

			hql.append( " ORDER BY sol.curso.nome" );

			Query query = getSession().createQuery( hql.toString() );
			query.setInteger("idUnidade", idUnidade);
			if( ano != null )
				query.setInteger("ano", ano);
			if( periodo != null )
				query.setInteger("periodo", periodo);
			if( tipo != null )
				query.setInteger("tipo",  tipo);

			@SuppressWarnings("unchecked")
			Collection<Curso> lista = query.list();
			return lista;
		}catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna os componentes do departamento informado que tem solicitações de turma realizadas
	 * @param idUnidade
	 * @param ano
	 * @param periodo
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findComponentesSolicitantesDepartamento (int idUnidade, Integer ano, Integer periodo, Integer tipo) throws DAOException {
		try {
			String projecao = "sol.componenteCurricular.id as id, sol.componenteCurricular.codigo as codigo, sol.componenteCurricular.detalhes.nome as detalhes.nome";
			StringBuffer hql = new StringBuffer();

			hql.append( " SELECT DISTINCT " ).append(HibernateUtils.removeAliasFromProjecao(projecao));
			hql.append( " FROM SolicitacaoTurma sol " );
			hql.append( " WHERE sol.componenteCurricular.unidade.id = :idUnidade " );
			if( ano != null )
				hql.append( " AND sol.ano = :ano " );
			if( periodo != null )
				hql.append( " AND sol.periodo = :periodo " );
			if( tipo != null )
				hql.append( " AND sol.tipo = :tipo " );

			hql.append( " ORDER BY sol.componenteCurricular.codigo" );

			Query query = getSession().createQuery( hql.toString() );
			query.setInteger("idUnidade", idUnidade);
			if( ano != null )
				query.setInteger("ano", ano);
			if( periodo != null )
				query.setInteger("periodo", periodo);
			if( tipo != null )
				query.setInteger("tipo",  tipo);

			@SuppressWarnings("unchecked")
			Collection<ComponenteCurricular> lista = HibernateUtils.parseTo(query.list(), projecao, ComponenteCurricular.class, "sol");
			return lista;
		}catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna as solicitações pendentes do componente informado
	 * que estão com as seguintes situações:
	 *  ABERTA, ATENDIDA_ALTERACAO, ATENDIDA_PARCIALMENTE
	 * @param idComponente
	 * @return
	 * @throws DAOException
	 */
	public List<SolicitacaoTurma> findPendentesByComponente(List<Integer> idsComponentes, String horario, int ano, int periodo) throws DAOException {
		Criteria c = getSession().createCriteria(SolicitacaoTurma.class);
		
		c.add( Expression.in("componenteCurricular.id", idsComponentes) );
		c.add( Expression.eq("horario", horario) );
		c.add( Expression.eq("ano", new Short((short) ano) ) );
		c.add( Expression.eq("periodo", new Byte( (byte) periodo ) ) );
		
		Collection<Integer> situacoes = new ArrayList<Integer>();
		situacoes.add( SolicitacaoTurma.ABERTA );
		situacoes.add( SolicitacaoTurma.ATENDIDA_ALTERACAO );
		situacoes.add( SolicitacaoTurma.ATENDIDA_PARCIALMENTE );
		situacoes.add( SolicitacaoTurma.SOLICITADA_ALTERACAO );
		c.add( Expression.in("situacao", situacoes) );

		@SuppressWarnings("unchecked")
		List<SolicitacaoTurma> lista = c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		return lista;
	}

	/**
	 * Retorna as solicitações de um discente em um ano/período específico
	 * @param idDiscente
	 * @param periodo
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	public List<SolicitacaoTurma> findByDiscenteAnoPeriodo(int idDiscente, byte periodo, short ano, Integer... situacoes) throws DAOException {
		try {

			Criteria c = getSession().createCriteria(DiscentesSolicitacao.class);
			Criteria cSol = c.createCriteria("solicitacaoTurma");
			cSol.add( Expression.eq("ano", ano) );
			cSol.add( Expression.eq("periodo", periodo) );
			c.add( Expression.eq("discenteGraduacao.id", idDiscente) );
			if( situacoes != null )
				cSol.add( Expression.in("situacao", situacoes) );

			c.setProjection(Projections.property("solicitacaoTurma"));

			@SuppressWarnings("unchecked")
			List<SolicitacaoTurma> lista = c.list();
			return lista;

		}catch (Exception e) {
			throw new DAOException(e);
		}
	}


	/**
	 * Retorna as solicitações de um discente em um ano/período especifico
	 * @param idDiscente
	 * @param periodo
	 * @param ano
	 * @return
	 * @throws DAOException
	 * int id, short ano, short periodo, String codigo, String componente, int idTipo,
			int idSituacao, String horario, short vagas, int idDepartamento, String deptoNome, String deptoSigla
	 */
	public List<SolicitacaoTurma> findByCurso(Curso curso, Integer ano, Integer periodo) throws DAOException {
		try {

			String hql = "select new SolicitacaoTurma(id,ano,periodo,componenteCurricular.codigo,componenteCurricular.detalhes.nome, tipo, " +
					" situacao, horario, vagas, componenteCurricular.unidade.id, componenteCurricular.unidade.nome, componenteCurricular.unidade.sigla )" +
					" from SolicitacaoTurma st where st.curso.id = " + curso.getId();

			if ( ano != null ) {
				hql += " and ano = " + ano;
			}

			if ( periodo != null ) {
				hql +=  " and periodo = " + periodo;
			}

			hql += " order by componenteCurricular.codigo asc ";

			Query q = getSession().createQuery(hql);
			@SuppressWarnings("unchecked")
			List<SolicitacaoTurma> lista = q.list();
			return lista;

		}catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna as solicitações de um discente em um ano/período especifico do tipo de turma informado
	 * @param curso
	 * @param ano ano da solicitação
	 * @param periodo período da solicitação
	 * @param tipoTurma tipo da solicitação de turma q deve consultar (regular, ensino individual, ferias)
	 * @return
	 * @throws DAOException
	 */
	public List<SolicitacaoTurma> findByCursoTipo(Curso curso, Integer ano, Integer periodo, int tipoTurma) throws DAOException {
		try {

			String hql = "SELECT new SolicitacaoTurma(id,ano,periodo,componenteCurricular.codigo,componenteCurricular.detalhes.nome, tipo, " +
					" situacao, horario, vagas, componenteCurricular.unidade.id, componenteCurricular.unidade.nome, componenteCurricular.unidade.sigla )" +
					" FROM SolicitacaoTurma st WHERE st.curso.id = " + curso.getId() + " AND st.tipo = " + tipoTurma;

			if ( ano != null ) {
				hql += " AND ano = " + ano;
			}

			if ( periodo != null ) {
				hql +=  " AND periodo = " + periodo;
			}

			hql += " ORDER BY componenteCurricular.codigo asc ";

			Query q = getSession().createQuery(hql);
			@SuppressWarnings("unchecked")
			List<SolicitacaoTurma> lista = q.list();
			return lista;

		}catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna os DiscentesSolicitação de uma solicitação de turma
	 * @param idSolicitacaoTurma
	 * @param ano
	 * @param periodo
	 * @param tipoTurma
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscentesSolicitacao> findDiscentesSolicitacaoBySoliciacao(
			int idSolicitacaoTurma) throws DAOException {
		StringBuffer hql = new StringBuffer();
		hql.append(" SELECT dg FROM DiscentesSolicitacao dg ");
		hql.append(" WHERE dg.solicitacaoTurma.id = :idSolicitacaoTurma ");

		Query q = getSession().createQuery(hql.toString());

		q.setInteger("idSolicitacaoTurma", idSolicitacaoTurma);

		@SuppressWarnings("unchecked")
		Collection<DiscentesSolicitacao> lista = q.list();
		return lista;
	}

	/**
	 * Busca as solicitações de turma do componente/horário/ano/período informado
	 * @param idComponente
	 * @param horario
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoTurma> findByComponenteHorarioAnoPeriodo(ComponenteCurricular componente, String horario, int ano, int periodo) throws DAOException{
		Criteria c = getSession().createCriteria(SolicitacaoTurma.class);
		c.add( Restrictions.eq("componenteCurricular", componente) );
		c.add( Restrictions.eq("horario", horario) );
		c.add( Restrictions.eq("ano", (short) ano) );
		c.add( Restrictions.eq("periodo", (byte) periodo) );
		c.add( Restrictions.ne("situacao", SolicitacaoTurma.REMOVIDA) );
		@SuppressWarnings("unchecked")
		Collection<SolicitacaoTurma> lista = c.list();
		return lista;
	}
	
	/**
	 * Retorna as solicitações de turma que possui algum dos discentes informado na coleção no ano, período e situação informados
	 * porém que é diferente da solicitação informada
	 * @param ano
	 * @param periodo
	 * @param discentes
	 * @param situacoes
	 * @param solicitacao não retorna esta solicitação, caso algum discente esteja nela no mesmo ano período
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoTurma> findSolTurmaFeriasByDiscente(SolicitacaoTurma solicitacao, int ano, int periodo, List<Integer> idsDiscentes, int... situacoes) throws DAOException{
		
		StringBuilder hql = new StringBuilder();
		hql.append( " SELECT st FROM SolicitacaoTurma  st JOIN FETCH st.discentes discentes" );
		hql.append( " WHERE st.ano = :ano " );
		hql.append( " AND st.periodo = :periodo " );
		hql.append( " AND discentes.discenteGraduacao.id in " + gerarStringIn(idsDiscentes) );
		if( !isEmpty( situacoes ) )
			hql.append( " AND st.situacao in " + gerarStringIn(situacoes) );
		if( solicitacao != null )
			hql.append( " AND st.id <> :idSolicitacao" );
		
		Query q = getSession().createQuery( hql.toString() );
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		if( solicitacao != null )
			q.setInteger("idSolicitacao", solicitacao.getId());
		
		@SuppressWarnings("unchecked")
		Collection<SolicitacaoTurma> lista = q.list();
		return lista;
		
	}
	
	
	/** Retorna as solicitações de turma pendentes em um ano-período.
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoTurma> findPendenteByAnoPeriodo(int ano, int periodo) throws DAOException {
		Integer[] situacoes = new Integer[] {SolicitacaoTurma.ABERTA, SolicitacaoTurma.ATENDIDA_ALTERACAO, SolicitacaoTurma.ATENDIDA_PARCIALMENTE};
		Criteria c = getSession().createCriteria(SolicitacaoTurma.class);
		c.add(Restrictions.eq("ano", (short) ano))
			.add(Restrictions.eq("periodo", (byte) periodo))
			.add(Restrictions.in("situacao", situacoes));
		c.createCriteria("componenteCurricular").addOrder(Order.asc("codigo"))
		.createCriteria("unidade").addOrder(Order.asc("nome"));
		@SuppressWarnings("unchecked")
		Collection<SolicitacaoTurma> lista = c.list();
		return lista;
	}
	
	/** Retorna as solicitações de uma turma 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public SolicitacaoTurma findByTurma(int idTurma) throws DAOException {
		Criteria c = getSession().createCriteria(SolicitacaoTurma.class);
		c.createCriteria("turmasSolicitacaoTurmas").createCriteria("turma")
		.add(Restrictions.eq("id", idTurma));
		return (SolicitacaoTurma) c.setMaxResults(1).uniqueResult();
	}

}
