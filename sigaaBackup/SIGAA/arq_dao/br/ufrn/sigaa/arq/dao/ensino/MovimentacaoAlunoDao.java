/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/09/2007
 *
 */
package br.ufrn.sigaa.arq.dao.ensino;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.GrupoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Dao responsável por consultas específicas à movimentação de discente.
 * @see MovimentacaoAluno
 *
 * @author eric
 *
 */
public class MovimentacaoAlunoDao extends GenericSigaaDAO {

	/** Construtor padrão. */
	public MovimentacaoAlunoDao() {
	}

	/**
	 * Método para se obter uma lista de Afastamentos por nome do aluno ou tipo
	 * afastamento
	 */
	public Collection<MovimentacaoAluno> findByDiscenteOrTipoMovimentacao(int idDiscente,
			int tipoMovimentacaoAluno, boolean todos, int unidadeId, char nivel, PagingInformation paging)
			throws DAOException {
		try {
			Criteria c = getSession().createCriteria(MovimentacaoAluno.class);
			c.add(Restrictions.eq("ativo", Boolean.TRUE));
			if (tipoMovimentacaoAluno != 0)
				c.add(Restrictions.eq("tipoMovimentacaoAluno", new TipoMovimentacaoAluno(tipoMovimentacaoAluno)));
			if (!todos)
				c.add(Restrictions.isNull("dataCadastroRetorno"));
			if (idDiscente != 0)
				c.add(Restrictions.eq("discente", new Discente(idDiscente)));
			Criteria cDis = c.createCriteria("discente");
			if (unidadeId != 0)
				cDis.add(Restrictions.eq("gestoraAcademica", new Unidade(unidadeId)));
			if (nivel != ' ')
				cDis.add(Restrictions.eq("nivel", nivel));
			c.addOrder(Order.asc("dataOcorrencia"));
			preparePaging(paging, c);
			@SuppressWarnings("unchecked")
			Collection<MovimentacaoAluno> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/** Retorna uma coleção de afastamentos do discente.
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public Collection<MovimentacaoAluno> findAfastamentosByDiscente(int idDiscente) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(MovimentacaoAluno.class);
			c.add(Restrictions.eq("ativo", Boolean.TRUE));
			c.add(Restrictions.isNull("dataRetorno"));
			c.add(Restrictions.eq("discente", new Discente(idDiscente)));
			c.addOrder(Order.desc("dataOcorrencia"));
			c.createCriteria("tipoMovimentacaoAluno").add(Restrictions.in("statusDiscente", StatusDiscente.getAfastamentos()));
			@SuppressWarnings("unchecked")
			Collection<MovimentacaoAluno> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/** Retorna uma coleção de afastamentos do discente, posteriores ao ano-período informados.
	 * @param idDiscente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<MovimentacaoAluno> findAfastamentosFuturosByDiscente(int idDiscente, int ano, int periodo) throws DAOException {
		try {
			String hql = "SELECT new MovimentacaoAluno(id)" +
					" FROM MovimentacaoAluno" +
					" WHERE ativo=trueValue()" +
					" and tipoMovimentacaoAluno.statusDiscente in "+ gerarStringIn(StatusDiscente.getAfastamentos()) +
					" and discente.id="+idDiscente +
					" and (anoReferencia > "+ ano+" or (anoReferencia="+ano+" and periodoReferencia>"+periodo+"))";
			@SuppressWarnings("unchecked")
			Collection<MovimentacaoAluno> lista = getSession().createQuery(hql).list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/** Retorna uma coleção de trancamentos do discente, posteriores ao ano-período informados.
	 * @param idDiscente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<MovimentacaoAluno> findTrancamentosFuturosByDiscente(int idDiscente, int ano, int periodo) throws DAOException {
		try {
			String hql = "FROM MovimentacaoAluno" +
					" WHERE ativo=trueValue()" +
					" and tipoMovimentacaoAluno.id = "+ TipoMovimentacaoAluno.TRANCAMENTO +
					" and discente.id="+idDiscente+" and (anoReferencia > "+ ano+" or (anoReferencia="+ano+" and periodoReferencia>"+periodo+"))";
			@SuppressWarnings("unchecked")
			Collection<MovimentacaoAluno> lista = getSession().createQuery(hql).list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/** Retorna uma coleção de afastamentos ativos do discente.
	 * @param idDiscente
	 * @param somenteAtivo
	 * @return
	 * @throws DAOException
	 */
	public Collection<MovimentacaoAluno> findByDiscente(int idDiscente, boolean somenteAtivo) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(MovimentacaoAluno.class);
			c.add(Restrictions.eq("ativo", somenteAtivo));
			c.add(Restrictions.eq("discente.id", idDiscente));
			c.addOrder(Order.desc("anoReferencia"));
			c.addOrder(Order.desc("periodoReferencia"));
			c.addOrder(Order.desc("dataOcorrencia"));
			@SuppressWarnings("unchecked")
			Collection<MovimentacaoAluno> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}


	/**
	 * Retorna o trancamento do discente informado no ano.periodo informado.
	 * @param idDiscente
	 * @param ano
	 * @param periodo
	 * @param semRetorno TRUE caso deva retornar apenas os trancamentos que não possuem retorno
	 * @return
	 */
	public MovimentacaoAluno findTrancamentosByDiscente(int idDiscente, int ano, int periodo, boolean semRetorno) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(MovimentacaoAluno.class);
			c.add(Restrictions.eq("ativo", Boolean.TRUE));
			c.add(Restrictions.eq("discente", new Discente(idDiscente)));
			c.add(Restrictions.eq("anoReferencia", ano));
			c.add(Restrictions.eq("periodoReferencia", periodo));
			c.add(Restrictions.eq("tipoMovimentacaoAluno.id", TipoMovimentacaoAluno.TRANCAMENTO));
			if( semRetorno )
				c.add(Restrictions.isNull("dataCadastroRetorno"));
			c.addOrder(Order.desc("dataOcorrencia"));
			@SuppressWarnings("unchecked")
			Collection<MovimentacaoAluno> trancs = c.list();
			if (trancs != null && trancs.size()  > 0)
				return trancs.iterator().next();
			else
				return null;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/** Retorna o trancamento do discente, no ano-período informado.
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @param semRetorno caso true, retorna tracamento que não possui data de cadastro de retorno.
	 * @return
	 * @throws DAOException
	 */
	public MovimentacaoAluno findTracamentoByDiscente(DiscenteAdapter discente, int ano, int periodo, boolean semRetorno) throws DAOException {
		Criteria c = getSession().createCriteria(MovimentacaoAluno.class);
		c.add(Restrictions.eq("ativo", Boolean.TRUE));
		if( semRetorno )
			c.add(Restrictions.isNull("dataCadastroRetorno"));
		c.add(Restrictions.eq("anoReferencia", ano));
		c.add(Restrictions.eq("periodoReferencia", periodo));
		c.add(Restrictions.eq("tipoMovimentacaoAluno.id", TipoMovimentacaoAluno.TRANCAMENTO));
		c.add(Restrictions.eq("discente.id", discente.getId()));
		c.setMaxResults(1);
		return (MovimentacaoAluno) c.uniqueResult();
	}

	/** Retorna uma lista de trancamentos de discentes (para operações em lotes).
	 * @param idsDiscentes
	 * @param ano
	 * @param periodo
	 * @param semRetorno
	 * @return
	 * @throws DAOException
	 */
	public Collection<MovimentacaoAluno> findTrancamentosByDiscentes(Collection<Integer> idsDiscentes, int ano, int periodo, boolean semRetorno) throws DAOException {

		String hql = "SELECT id AS ID," +
				" tipoMovimentacaoAluno.id as ID_TIPO," +
				" discente.id AS ID_DISCENTE," +
				" discente.matricula AS MATRICULA," +
				" discente.status AS STATUS," +
				" discente.nivel AS NIVEL, " +
				" dataOcorrencia AS DATA_OCORRENCIA," +
				" usuarioCadastro.id AS ID_USUARIO_CADASTRO," +
				" usuarioRetorno.id AS ID_USUARIO_RETORNO," +
				" dataCadastroRetorno AS DATA_CADASTRO_RETORNO," +
				" anoReferencia AS ANO_REFERENCIA," +
				" periodoReferencia AS PERIODO_REFERENCIA, " +
				" valorMovimentacao AS VALOR_MOVIMENTACAO," +
				" ativo AS ATIVO, " +
				" usuarioCancelamento.id AS ID_USUARIO_CANCELAMENTO," +
				" dataEstorno AS DATA_ESTORNO," +
				" apostilamento AS APOSTILAMENTO," +
				" inicioAfastamento AS INICIO_AFASTAMENTO " +
				" FROM MovimentacaoAluno " +
				" WHERE discente.id in " + gerarStringIn(idsDiscentes) +
				" AND anoReferencia = " + ano +
				" AND periodoReferencia = " + periodo +
				" AND tipoMovimentacaoAluno.id = " + TipoMovimentacaoAluno.TRANCAMENTO +
				" AND ativo = trueValue()" ;
		if( semRetorno ){
			hql += " AND dataCadastroRetorno is null ";
		}

		Query q = getSession().createQuery( hql );

		List<MovimentacaoAluno> movimentacoes = new ArrayList<MovimentacaoAluno>();

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> lista =  q.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP ).list();

		for ( Map<String, Object> mapa : lista ) {
			MovimentacaoAluno mov = new MovimentacaoAluno();
			mov.setId( (Integer) mapa.get("ID") );
			mov.setTipoMovimentacaoAluno( new TipoMovimentacaoAluno((Integer) mapa.get("ID_TIPO") ));

			mov.setDiscente( new Discente( (Integer) mapa.get("ID_DISCENTE") ) );
			mov.getDiscente().setMatricula( (Long) mapa.get("MATRICULA") )  ;
			mov.getDiscente().setStatus( (Integer) mapa.get("STATUS") )  ;
			mov.getDiscente().setNivel( (Character) mapa.get("NIVEL") )  ;

			mov.setDataOcorrencia( (Date) mapa.get("DATA_OCORRENCIA") );
			if( mapa.get("ID_USUARIO_CADASTRO") != null )
				mov.setUsuarioCadastro( new Usuario( (Integer) mapa.get("ID_USUARIO_CADASTRO")) );
			if( mapa.get("ID_USUARIO_RETORNO") != null )
				mov.setUsuarioRetorno( new Usuario( (Integer) mapa.get("ID_USUARIO_RETORNO") ) );
			mov.setDataCadastroRetorno( (Date) mapa.get("DATA_CADASTRO_RETORNO") );
			mov.setAnoReferencia( (Integer) mapa.get("ANO_REFERENCIA") );
			mov.setPeriodoReferencia( (Integer) mapa.get("PERIODO_REFERENCIA") );
			mov.setValorMovimentacao( (Integer) mapa.get("VALOR_MOVIMENTACAO") );
			mov.setAtivo( (Boolean) mapa.get("ATIVO") );
			if( mapa.get("ID_USUARIO_CANCELAMENTO") != null )
				mov.setUsuarioCancelamento( new Usuario( (Integer) mapa.get("ID_USUARIO_CANCELAMENTO") ) );
			mov.setDataEstorno( (Date) mapa.get("DATA_ESTORNO") );
			mov.setApostilamento( (Boolean) mapa.get("APOSTILAMENTO") );
			mov.setInicioAfastamento( (Date) mapa.get("INICIO_AFASTAMENTO") );

			movimentacoes.add( mov );
		}

		return movimentacoes;
	}

	/** Retorna a quantidade de trancamentos de um discente.
	 * @param idDiscente
	 * @param unidadeId
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public int findTrancamentosByDiscente(int idDiscente, int unidadeId, char nivel) throws DAOException {
		Collection<MovimentacaoAluno> trancamentos = findByDiscenteOrTipoMovimentacao(idDiscente,
				TipoMovimentacaoAluno.TRANCAMENTO, true, unidadeId, nivel, null);
		return (trancamentos != null) ? trancamentos.size() : 0;
	}

	/**
	 * Retorna o último afastamento que o aluno informado possui cadastrado
	 * @param idDiscente
	 * @param permanente diz se o afastamento deve ser permanente ou não
	 * @param incluirRetorno TRUE caso deva considerar os afastamentos que possuem retorno. FALSE caso deva considerar apenas os que NAO POSSUEM RETORNO
	 * @return
	 * @throws DAOException
	 */
	public MovimentacaoAluno findUltimoAfastamentoByDiscente(int idDiscente, Boolean permanente, boolean incluirRetorno) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(MovimentacaoAluno.class);
			c.add(Restrictions.eq("ativo", Boolean.TRUE));

			// Alunos ativos não tem afastamento permanente
			// O estorno considera o último afastamento existente, inclusive os retornados.
			// Alunos com afastamentos retornados estão ativos. Deve considerar o parâmetro incluirRetorno para excluir estas movimentações da busca.
			Criteria cDiscente = c.createCriteria("discente").add(Restrictions.eq("id", idDiscente));
			if( !incluirRetorno )
				cDiscente.add(Restrictions.not(Restrictions.in("status", StatusDiscente.getAtivos())));

			c.addOrder(Order.desc("anoReferencia"));
			c.addOrder(Order.desc("periodoReferencia"));
			c.addOrder(Order.desc("dataOcorrencia"));
			if (permanente != null && permanente) {
				c.add(Restrictions.isNull("dataCadastroRetorno"));
				c.createCriteria("tipoMovimentacaoAluno").add(Restrictions.in("statusDiscente", StatusDiscente.getAfastamentosPermanentes()));
			} else if (permanente != null && !permanente) {
				c.createCriteria("tipoMovimentacaoAluno").add(Restrictions.in("statusDiscente", StatusDiscente.getAfastamentosNaoPermanentes()));
			} else if (permanente == null) {
				c.createCriteria("tipoMovimentacaoAluno").add(Restrictions.in("statusDiscente", StatusDiscente.getAfastamentos()));
			}
			c.add(Restrictions.ne("tipoMovimentacaoAluno.id", TipoMovimentacaoAluno.PRORROGACAO_TRANCAMENTO_PROGRAMA));
			c.setMaxResults(1);
			return (MovimentacaoAluno) c.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna a soma do valor total das movimentações de aluno do tipo prorrogação de prazo.
	 * Soma apenas as ativas, considerando também as prorrogações por trancamento de programa.
	 * Utilizada para alunos do ensino técnico e formação complementar.
	 * @param d
	 * @return
	 * @throws DAOException
	 */
	public int countProrrogacoesByDiscenteTecnico(DiscenteAdapter d) throws DAOException {
		try {
			String hql = " SELECT sum( valorMovimentacao )" +
			" FROM MovimentacaoAluno" +
			" WHERE discente.id = :discente and" +
			" tipoMovimentacaoAluno.grupo = :grupo" +
			" and ativo = trueValue()";
			Query q = getSession().createQuery(hql);
			q.setInteger("discente", d.getId());
			q.setString("grupo", GrupoMovimentacaoAluno.PRORROGACAO_PRAZO);
			Long soma = (Long) q.uniqueResult();
			if (soma != null)
				return soma.intValue();
			else
				return 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna a soma do valor total das movimentações de aluno do tipo prorrogação de prazo.
	 * Soma apenas as ativas e não considera as prorrogações por trancamento de programa.
	 * @param d
	 * @return
	 * @throws DAOException
	 */
	public int countProrrogacoesByDiscente(DiscenteAdapter d) throws DAOException {
		try {
			String hql = " SELECT sum( valorMovimentacao )" +
					" FROM MovimentacaoAluno" +
					" WHERE discente.id = :discente and" +
					" tipoMovimentacaoAluno.grupo = :grupo" +
					" and tipoMovimentacaoAluno.id != :tipo" +
					" and ativo = trueValue()";
			Query q = getSession().createQuery(hql);
			q.setInteger("discente", d.getId());
			q.setString("grupo", GrupoMovimentacaoAluno.PRORROGACAO_PRAZO);
			q.setInteger("tipo", TipoMovimentacaoAluno.PRORROGACAO_TRANCAMENTO_PROGRAMA);
			Long soma = (Long) q.uniqueResult();
			if (soma != null)
				return soma.intValue();
			else
				return 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	/** Retorna uma lista de prorrogações do discente.
	 * @param d
	 * @return
	 * @throws DAOException
	 */
	public Collection<MovimentacaoAluno> findProrrogacoesByDiscente(DiscenteAdapter d) throws DAOException {
		try {
			String hql = "FROM MovimentacaoAluno WHERE discente.id = :discente and ativo = trueValue() and " +
					" tipoMovimentacaoAluno.grupo IN " + gerarStringIn(new String[]{ GrupoMovimentacaoAluno.PRORROGACAO_PRAZO,GrupoMovimentacaoAluno.ANTECIPACAO_PRAZO }) +
					" ORDER BY anoReferencia desc, periodoReferencia desc, dataOcorrencia desc";
			Query q = getSession().createQuery(hql);
			q.setInteger("discente", d.getId());
			@SuppressWarnings("unchecked")
			Collection<MovimentacaoAluno> lista = q.list();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}


	/** Retorna uma lista de movimentações de um discente de acordo com os parâmetros informados.
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Collection<MovimentacaoAluno> findByDiscente(DiscenteAdapter discente, int ano, int periodo, TipoMovimentacaoAluno tipo) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(MovimentacaoAluno.class);
			c.add(Restrictions.eq("ativo", Boolean.TRUE));
			c.add(Restrictions.eq("discente.id", discente.getId()));
			c.add(Restrictions.eq("tipoMovimentacaoAluno.id", tipo.getId()));
			c.add(Restrictions.eq("anoReferencia", ano));
			c.add(Restrictions.eq("periodoReferencia", periodo));
			@SuppressWarnings("unchecked")
			Collection<MovimentacaoAluno> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/** Retorna a movimentação do tipo CONCLUSÃO do discente informado.
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public MovimentacaoAluno findConclusaoByDiscente(int idDiscente, boolean isProjecao) throws DAOException {
		
		String projecao = "id, anoReferencia, periodoReferencia, anoOcorrencia, periodoOcorrencia";
		
		StringBuilder hql = new StringBuilder();
		hql.append(" select ");
		
		if (isProjecao)
			hql.append(projecao);
		else 
			hql.append(" ma" );
		
		hql.append(" from MovimentacaoAluno ma where ");
		hql.append(" ma.discente.id = :idDiscente" );
		hql.append(" and ma.ativo = trueValue() ");
		hql.append(" and ma.tipoMovimentacaoAluno.id = :tipoMovimentacao ");

		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("idDiscente", idDiscente);
		q.setInteger("tipoMovimentacao", TipoMovimentacaoAluno.CONCLUSAO);
		q.setMaxResults(1);
		
		MovimentacaoAluno resultado = null;
		if (isProjecao) {
			Collection col = HibernateUtils.parseTo(q.list(), projecao, MovimentacaoAluno.class);
			if (!isEmpty(col))
				resultado = (MovimentacaoAluno) col.iterator().next();
		} else 
			resultado = (MovimentacaoAluno) q.uniqueResult();
		
		return resultado;
	}
	
	/** Retorna a movimentação do tipo CONCLUSÃO do discente informado. SEM PROJEÇÃO!!!!
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public MovimentacaoAluno findConclusaoByDiscente(int idDiscente) throws DAOException {
		return findConclusaoByDiscente(idDiscente, false);
	}

	
	/**
	 * Retorna a soma do valor total das movimentações de aluno do tipo antecipação de prazo.
	 * soma apenas as ativas
	 * @param d
	 * @return
	 * @throws DAOException
	 */
	public int countAntecipacoesByDiscente(DiscenteAdapter d) throws DAOException {
		try {
			String hql = " SELECT sum( valorMovimentacao )" +
					" FROM MovimentacaoAluno" +
					" WHERE discente.id = :discente and" +
					" tipoMovimentacaoAluno.grupo = :grupo" +
					" and ativo = trueValue()";
			Query q = getSession().createQuery(hql);
			q.setInteger("discente", d.getId());
			q.setString("grupo", GrupoMovimentacaoAluno.ANTECIPACAO_PRAZO);
			Long soma = (Long) q.uniqueResult();
			if (soma != null)
				return soma.intValue();
			else
				return 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	/** Retorna uma lista de movimentações de um discente de acordo com os parâmetros informados.
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Collection<MovimentacaoAluno> findByDiscenteAndTipo(DiscenteAdapter discente, int tipo) throws DAOException {
		Criteria c = getSession().createCriteria(MovimentacaoAluno.class);
		c.add(Restrictions.eq("ativo", Boolean.TRUE));
		c.add(Restrictions.eq("discente.id", discente.getId()));
		c.add(Restrictions.eq("tipoMovimentacaoAluno.id", tipo));
		@SuppressWarnings("unchecked")
		Collection<MovimentacaoAluno> lista = c.list();
		return lista;
	}
	
	/** Retorna uma lista de movimentações de um discente de acordo com os parâmetros informados.
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public int countMovimentacaoByDiscenteAndTipo(DiscenteAdapter discente, int tipo) throws DAOException {
		Criteria c = getSession().createCriteria(MovimentacaoAluno.class);
		c.setProjection(Projections.rowCount());
		c.add(Restrictions.eq("ativo", Boolean.TRUE));
		c.add(Restrictions.eq("discente.id", discente.getId()));
		c.add(Restrictions.eq("tipoMovimentacaoAluno.id", tipo));
		
		return ((Integer)c.list().get(0)).intValue();
	}	
	
	/**
	 * Retorna todos os trancamentos dos discentes.
	 * @param discentes
	 * @return
	 * @throws DAOException
	 */
	public HashMap<Discente,Collection<MovimentacaoAluno>> findTrancamentosByDiscentes (Collection<Discente> discentes) throws DAOException {
	
		try{
			
			String sql = " select m.id_discente , m.id_movimentacao_aluno , m.valor_movimentacao , m.ano_referencia , m.periodo_referencia "+
							" from ensino.movimentacao_aluno m  "+
							" where m.id_discente in "+UFRNUtils.gerarStringIn(discentes)+" "+
							" and m.id_tipo_movimentacao_aluno = "+TipoMovimentacaoAluno.TRANCAMENTO+" and ativo = trueValue() "+
							" order by id_discente , ano_referencia , periodo_referencia ";
			
			Query  q = getSession().createSQLQuery(sql);
			Integer oldIdDiscente = null;
			Integer newIdDiscente = null;
			HashMap<Discente,Collection<MovimentacaoAluno>> map = new HashMap<Discente,Collection<MovimentacaoAluno>>();
			Collection<MovimentacaoAluno> movimentacoes = new ArrayList<MovimentacaoAluno>();
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();		
					
			if ( result != null )
			{	
				for (Object[] linha : result) {
				
					Integer i = 0;
					MovimentacaoAluno m = new MovimentacaoAluno();
					newIdDiscente = (Integer)linha[i++];
					
					if ( !newIdDiscente.equals(oldIdDiscente) ){
						
						Discente d = new Discente();
						movimentacoes = new ArrayList<MovimentacaoAluno>();
						
						d.setId(newIdDiscente);
						m.setId((Integer)linha[i++]);
						m.setValorMovimentacao((Integer)linha[i++]);
						m.setAnoReferencia((Integer)linha[i++]);
						m.setPeriodoReferencia((Integer)linha[i++]);
						movimentacoes.add(m);
						map.put(d,movimentacoes);
					} else if (newIdDiscente.equals(oldIdDiscente)){
						m.setId((Integer)linha[i++]);
						m.setValorMovimentacao((Integer)linha[i++]);
						m.setAnoReferencia((Integer)linha[i++]);
						m.setPeriodoReferencia((Integer)linha[i++]);
						movimentacoes.add(m);
					}
				
					oldIdDiscente = newIdDiscente;
				}	
			}
			
			return map;

		}catch (Exception e) {
			throw new DAOException(e);
		}		
	}
	
	/**
	 * Retorna todas as prorrogações de prazo dos discentes.
	 * @param discentes
	 * @return
	 * @throws DAOException
	 */
	public HashMap<Discente,Collection<MovimentacaoAluno>> findProrrogacoesByDiscentes (Collection<Discente> discentes) throws DAOException {
			
		try{
			
			String sql = " select m.id_discente , m.id_movimentacao_aluno , m.valor_movimentacao , m.ano_referencia , m.periodo_referencia "+
							" from ensino.movimentacao_aluno m  "+
							" join ensino.tipo_movimentacao_aluno t on t.id_tipo_movimentacao_aluno = m.id_tipo_movimentacao_aluno "+
							" where m.id_discente in "+UFRNUtils.gerarStringIn(discentes)+" "+
							" and m.id_tipo_movimentacao_aluno = "+TipoMovimentacaoAluno.PRORROGACAO_TRANCAMENTO_PROGRAMA+" " +
							" and t.grupo = '"+GrupoMovimentacaoAluno.PRORROGACAO_PRAZO+"' and m.ativo = trueValue() "+
							" order by id_discente , ano_referencia , periodo_referencia ";
			
			Query  q = getSession().createSQLQuery(sql);
			Integer oldIdDiscente = null;
			Integer newIdDiscente = null;
			HashMap<Discente,Collection<MovimentacaoAluno>> map = new HashMap<Discente,Collection<MovimentacaoAluno>>();
			Collection<MovimentacaoAluno> movimentacoes = new ArrayList<MovimentacaoAluno>();
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();		
					
			if ( result != null )
			{	
				for (Object[] linha : result) {
				
					Integer i = 0;
					MovimentacaoAluno m = new MovimentacaoAluno();
					newIdDiscente = (Integer)linha[i++];
					
					if ( !newIdDiscente.equals(oldIdDiscente) ){
						
						Discente d = new Discente();
						movimentacoes = new ArrayList<MovimentacaoAluno>();
						
						d.setId(newIdDiscente);
						m.setId((Integer)linha[i++]);
						m.setValorMovimentacao((Integer)linha[i++]);
						m.setAnoReferencia((Integer)linha[i++]);
						m.setPeriodoReferencia((Integer)linha[i++]);
						movimentacoes.add(m);
						map.put(d,movimentacoes);
					} else if (newIdDiscente.equals(oldIdDiscente)){
						m.setId((Integer)linha[i++]);
						m.setValorMovimentacao((Integer)linha[i++]);
						m.setAnoReferencia((Integer)linha[i++]);
						m.setPeriodoReferencia((Integer)linha[i++]);
						movimentacoes.add(m);
					}
				
					oldIdDiscente = newIdDiscente;
				}	
			}
			
			return map;

		}catch (Exception e) {
			throw new DAOException(e);
		}		
	}

	/**
	 * Retorna os afastamentos que o aluno informado possui cadastrado
	 * @param discentes
	 * @return
	 * @throws DAOException
	 */
	public HashMap<Discente,Collection<MovimentacaoAluno>> findAfastamentosByDiscentes (Collection<Discente> discentes) throws DAOException {
			
		try{
			
			String sql = " select m.id_discente , m.id_movimentacao_aluno , m.valor_movimentacao , m.ano_referencia , m.periodo_referencia , m.data_ocorrencia "+
							" from ensino.movimentacao_aluno m  "+
							" join ensino.tipo_movimentacao_aluno t on t.id_tipo_movimentacao_aluno = m.id_tipo_movimentacao_aluno "+
							" join discente d on d.id_discente = m.id_discente "+
							" where m.id_discente in "+UFRNUtils.gerarStringIn(discentes)+" "+
							" and d.status not in "+UFRNUtils.gerarStringIn(StatusDiscente.getAtivos())+" "+
							" and m.id_tipo_movimentacao_aluno = "+TipoMovimentacaoAluno.PRORROGACAO_TRANCAMENTO_PROGRAMA+" " +
							" and t.statusDiscente in "+UFRNUtils.gerarStringIn(StatusDiscente.getAfastamentosPermanentes())+" and m.ativo = trueValue() "+
							" and m.data_cadastro_retorno is null "+
							" order by id_discente , ano_referencia , periodo_referencia ";
			
			Query  q = getSession().createSQLQuery(sql);
			Integer oldIdDiscente = null;
			Integer newIdDiscente = null;
			HashMap<Discente,Collection<MovimentacaoAluno>> map = new HashMap<Discente,Collection<MovimentacaoAluno>>();
			Collection<MovimentacaoAluno> movimentacoes = new ArrayList<MovimentacaoAluno>();
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();		
					
			if ( result != null )
			{	
				for (Object[] linha : result) {
				
					Integer i = 0;
					MovimentacaoAluno m = new MovimentacaoAluno();
					newIdDiscente = (Integer)linha[i++];
					
					if ( !newIdDiscente.equals(oldIdDiscente) ){
						
						Discente d = new Discente();
						movimentacoes = new ArrayList<MovimentacaoAluno>();
						
						d.setId(newIdDiscente);
						m.setId((Integer)linha[i++]);
						m.setValorMovimentacao((Integer)linha[i++]);
						m.setAnoReferencia((Integer)linha[i++]);
						m.setPeriodoReferencia((Integer)linha[i++]);
						m.setDataOcorrencia((Date)linha[i++]);
						movimentacoes.add(m);
						map.put(d,movimentacoes);
					} else if (newIdDiscente.equals(oldIdDiscente)){
						m.setId((Integer)linha[i++]);
						m.setValorMovimentacao((Integer)linha[i++]);
						m.setAnoReferencia((Integer)linha[i++]);
						m.setPeriodoReferencia((Integer)linha[i++]);
						m.setDataOcorrencia((Date)linha[i++]);
						movimentacoes.add(m);
					}
				
					oldIdDiscente = newIdDiscente;
				}	
			}
			
			return map;

		}catch (Exception e) {
			throw new DAOException(e);
		}		
	}
}
