/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '09/07/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.isNull;
import static org.hibernate.criterion.Restrictions.or;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.stricto.dominio.TipoCursoStricto;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.TeseOrientada;

/**
 * Classe utilizada para realizar consultas específicas à entidade
 * OrientacaoAcademica. <br/>
 * A orientação acadêmica tem como objetivo facilitar a integração dos alunos à
 * vida universitária, orientando-os quanto às suas atividades acadêmicas. No
 * caso de discentes de pós-graduação, o orientador supervisionará as atividades
 * do discente na organização do seu plano de curso e assisti-lo em sua formação.
 *
 * 
 * @author Victor Hugo
 */
public class OrientacaoAcademicaDao extends GenericSigaaDAO {


	/**
	 * Retorna as OrientacaoAcademica ativas do discente informado se for
	 * discente de PÓS pode ter UM ORIENTADOR E VÁRIOS CO-ORIENTADORES ativos se
	 * for discente de GRADUAÇÃO pode ter APENAS UM ORIENTADOR ACADÊMICO.
	 *
	 * @param idDiscente
	 * @param ativo
	 * @return
	 * @throws DAOException
	 */
	public Collection<OrientacaoAcademica> findAtivosByDiscente(int idDiscente, Character tipo) throws DAOException {
		return findByDiscente(idDiscente, tipo, true);
	}

	/**
	 * Retorna as orientações de um discente.
	 *
	 * @param idDiscente
	 * @param tipo
	 * @param somenteAtivos
	 * @return
	 * @throws DAOException
	 */
	public Collection<OrientacaoAcademica> findByDiscente(int idDiscente, Character tipo, boolean somenteAtivos) throws DAOException {
		Criteria c = getSession().createCriteria(OrientacaoAcademica.class);
		c.add(Restrictions.eq("discente.id", idDiscente));
		c.add(Restrictions.eq("cancelado", false));
		if (somenteAtivos)
			c.add(Restrictions.isNull("fim"));
		if (tipo != null)
			c.add(Restrictions.eq("tipoOrientacao", tipo));
		c.addOrder(Order.desc("tipoOrientacao"));
		c.addOrder(Order.desc("fim"));
		c.addOrder(Order.desc("inicio"));

		@SuppressWarnings("unchecked")
		Collection<OrientacaoAcademica> lista = c.list();
		return lista;
	}

	/**
	 * Localiza os Cursos de Pós onde o docente da instituição possui orientandos
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public List<Curso> findCursosOrientandosByServidor(Servidor servidor) throws DAOException {
		return findCursosOrientandos(servidor, null);
	}

	/**
	 * Localiza os Cursos de Pós onde o docente externo possui orientandos
	 * 
	 * @param docExterno
	 * @return
	 * @throws DAOException
	 */
	public List<Curso> findCursosOrientandosByDocenteExterno(DocenteExterno docExterno) throws DAOException {
		return findCursosOrientandos(null, docExterno);
	}

	/**
	 * Localiza os Cursos de Pós onde o docente externo ou servidor da instituição possui orientandos
	 * 
	 * @param servidor
	 * @param docExterno
	 * @return
	 * @throws DAOException
	 */
	private List<Curso> findCursosOrientandos(Servidor servidor, DocenteExterno docExterno) throws DAOException {
		
		StringBuilder hql = new StringBuilder();
		
		String projecao = " curso.id, curso.nome, curso.nivel ";
		
		hql.append("select distinct " + projecao +
				" from OrientacaoAcademica oa " +
				" 	join oa.discente discente " +
				" 	join discente.curso curso " +
				"	left join oa.servidor servidor " +
				"	left join oa.docenteExterno docenteExterno " +
				" where oa.cancelado = falseValue() ");
		
		if (isNotEmpty(servidor))
			hql.append(" and oa.servidor = " + servidor.getId());
		else
			hql.append(" and oa.docenteExterno = " + docExterno.getId());
		
		hql.append("	and oa.dataFinalizacao is null " +
				"	and discente.nivel in " + gerarStringIn(NivelEnsino.getNiveisStricto()) + 
				"	and oa.tipoOrientacao = :tipoOrientacao " +
				"	and (oa.fim > :dataFim or fim is null)");
		
		Query query = getSession().createQuery(hql.toString());
		query.setCharacter("tipoOrientacao", OrientacaoAcademica.ORIENTADOR);
		query.setDate("dataFim", new Date());
		
		
		return (List<Curso>) HibernateUtils.parseTo(query.list(), projecao, Curso.class, "curso");
	}

	/** Retorna uma lista de unidades do programa orientados pelo servidor
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public List<Unidade> findProgramaOrientandosByServidor(Servidor servidor) throws DAOException {
		return findProgramaOrientandos(servidor, null);
	}

	/** Retorna uma lista de unidades do programa orientados pelo docente externo
	 * @param docExterno
	 * @return
	 * @throws DAOException
	 */
	public List<Unidade> findProgramaOrientandosByDocenteExterno(DocenteExterno docExterno) throws DAOException {
		return findProgramaOrientandos(null, docExterno);
	}	
	
	/** Retorna uma lista de unidades do programa orientados pelo servidor ou docente externo
	 * @param servidor
	 * @param docExterno
	 * @return
	 * @throws DAOException
	 */
	private List<Unidade> findProgramaOrientandos(Servidor servidor, DocenteExterno docExterno) throws DAOException {
		
		StringBuilder hql = new StringBuilder();
		
		String projecao = " unidade.id, unidade.nome ";
		
		hql.append("select distinct " + projecao +
				" from OrientacaoAcademica oa " +
				" 	join oa.discente discente " +
				" 	join discente.gestoraAcademica unidade " +
				"	left join oa.servidor servidor " +
				"	left join oa.docenteExterno docenteExterno " +
				" where oa.cancelado = falseValue() ");
		
		if (isNotEmpty(servidor))
			hql.append(" and oa.servidor = " + servidor.getId());
		else
			hql.append(" and oa.docenteExterno = " + docExterno.getId());
		
		hql.append("	and oa.dataFinalizacao is null " +
				"	and discente.nivel in " + gerarStringIn(NivelEnsino.getNiveisStricto()) + 
				"	and oa.tipoOrientacao = :tipoOrientacao " +
				"	and (oa.fim > :dataFim or fim is null)");
		
		Query query = getSession().createQuery(hql.toString());
		query.setCharacter("tipoOrientacao", OrientacaoAcademica.ORIENTADOR);
		query.setDate("dataFim", new Date());
		
		
		return (List<Unidade>) HibernateUtils.parseTo(query.list(), projecao, Unidade.class, "unidade");
	}
	
	/**
	 * Retorna o orientador ativo relacionado ao programa corrente
	 * do discente informado. 
	 *
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public OrientacaoAcademica findOrientadorAtivoByDiscente(int idDiscente) throws DAOException {
		Criteria c = getSession().createCriteria(OrientacaoAcademica.class);
		c.add(Restrictions.eq("discente.id", idDiscente));
		c.add(Restrictions.eq("tipoOrientacao", OrientacaoAcademica.ORIENTADOR));
		c.add(Restrictions.eq("cancelado", false));
		c.add(Restrictions.isNull("fim"));
		c.addOrder(Order.desc("inicio"));
		@SuppressWarnings("unchecked")
		List<OrientacaoAcademica> orientacoes = c.list();
		if (orientacoes != null && orientacoes.size() > 0)
			return orientacoes.get(0);
		return null;
	}

	/**
	 * Retorna o último orientador (não necessariamente ativo)
	 * do programa mais recente do discente informado.
	 *
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public OrientacaoAcademica findUltimoOrientadorByDiscente(int idDiscente) throws DAOException {
		Criteria c = getSession().createCriteria(OrientacaoAcademica.class);
		c.add(Restrictions.eq("discente.id", idDiscente));
		c.add(Restrictions.eq("tipoOrientacao", OrientacaoAcademica.ORIENTADOR));
		c.add(Restrictions.eq("cancelado", false));
		c.addOrder(Order.desc("inicio"));
		@SuppressWarnings("unchecked")
		List<OrientacaoAcademica> orientacoes = c.list();
		if (orientacoes != null && orientacoes.size() > 0)
			return orientacoes.get(0);
		return null;
	}

	/**
	 * Retorna o OrientadorAcademico ativo do discente informado.
	 *
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public OrientacaoAcademica findOrientadorAcademicoAtivoByDiscente(int idDiscente) throws DAOException {
		Criteria c = getSession().createCriteria(OrientacaoAcademica.class);
		c.add(Restrictions.eq("discente.id", idDiscente));
		c.add(Restrictions.eq("tipoOrientacao", OrientacaoAcademica.ACADEMICO));
		c.add(Restrictions.eq("cancelado", false));
		c.add(Restrictions.or(Restrictions.isNull("fim"), Restrictions.ge("fim", new Date())));
		c.addOrder(Order.desc("inicio"));
		return (OrientacaoAcademica) c.setMaxResults(1).uniqueResult();
	}


	/**
	 * Retorna as orientações acadêmicas ativas do curso ou programa informado.
	 *
	 * @param idCurso
	 *            id do curso
	 * @param idPrograma
	 *            id do programa
	 * @return
	 * @throws DAOException
	 */
	public Collection<OrientacaoAcademica> findAtivoByCurso(Integer idCurso, Integer idPrograma) throws DAOException {
		Criteria c = getSession().createCriteria(OrientacaoAcademica.class);
		Criteria discCriteria = c.createCriteria("discente");
		c.add(Restrictions.eq("cancelado", false));
		c.add(Restrictions.isNull("fim"));
		if (idCurso != null)
			discCriteria.add(Restrictions.eq("curso.id", idCurso));
		if (idPrograma != null)
			discCriteria.add(Restrictions.eq("gestoraAcademica.id", idPrograma));

		Criteria subPessoa = discCriteria.createCriteria("pessoa");
		subPessoa.addOrder(Order.asc("nome"));
		@SuppressWarnings("unchecked")
		Collection<OrientacaoAcademica> lista = c.list();
		return lista;
	}

	/**
	 * Retorna a quantidade de orientandos de um servidor.
	 *
	 * @param idServidor
	 * @param tipoOrientacao
	 * @return
	 * @throws DAOException
	 */
	public int findTotalOrientandosAtivos(int idServidor, Character tipoOrientacao) throws DAOException {

		StringBuilder hql = new StringBuilder(" SELECT count( DISTINCT o.id ) FROM OrientacaoAcademica o  ");
		hql.append(" INNER JOIN o.discente d WHERE o.fim is null AND o.dataFinalizacao is null ");
		hql.append(" AND o.cancelado = falseValue() AND o.servidor = :idServidor AND o.tipoOrientacao = :tipoOrientacao ");
		hql.append(" AND d.status IN " + UFRNUtils.gerarStringIn( StatusDiscente.getAtivos() ));
		
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("idServidor", idServidor);
		q.setCharacter("tipoOrientacao", tipoOrientacao);
		
		return ((Long) q.uniqueResult()).intValue();
	}

	/**
	 * Retorna a quantidade de orientandos de um docente externo.
	 *
	 * @param docenteExterno
	 * @return
	 * @throws DAOException
	 */
	public int findTotalOrientandosAtivos(DocenteExterno docenteExterno) throws DAOException {
		String hql = " SELECT count( DISTINCT o.id ) FROM OrientacaoAcademica o WHERE o.fim is null and o.cancelado = falseValue() "+
				" and o.docenteExterno.id = " + docenteExterno.getId()
				+ " and o.tipoOrientacao = '"+ OrientacaoAcademica.ORIENTADOR + "'";
		Query q = getSession().createQuery(hql);
		return ((Long) q.uniqueResult()).intValue();
	}

	/**
	 * Retorna a quantidade de orientandos de um servidor no nível informado
	 * (considera todos os níveis stricto sensu como um só).
	 *
	 * @param idServidor
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public int findTotalOrientandosAtivosNivel(int idServidor, char nivel)
			throws DAOException {
		String niveis = "('" + nivel + "')";
		if (NivelEnsino.isAlgumNivelStricto(nivel))
			niveis = NivelEnsino.getNiveisStrictoString();

		String hql = " SELECT count( DISTINCT o.id ) FROM OrientacaoAcademica o, Discente d "
				+ " WHERE o.discente.id = d.id "
				+ " AND o.fim IS NULL "
				+ " AND o.cancelado = falseValue() "
				+ " AND o.servidor = "
				+ idServidor + " AND d.nivel in " + niveis;
		Query q = getSession().createQuery(hql);
		return ((Long) q.uniqueResult()).intValue();
	}
	
	/**
	 * Retorna a quantidade de orientandos de um servidor no nível informado
	 * (considera os níveis de mestrado e doutorado do stricto sensu separadamente).
	 *
	 * @param docente
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public int findTotalOrientandosAtivosNivel(Servidor docente, char nivel)
			throws DAOException {
		String hql = " SELECT count( DISTINCT o.id ) FROM OrientacaoAcademica o, Discente d "
				+ " WHERE o.discente.id = d.id "
				+ " AND o.fim IS NULL "
				+ " AND o.cancelado = falseValue() "
				+ " AND o.servidor = "
				+ docente.getId() + " AND d.nivel = '" + nivel +"'";
		Query q = getSession().createQuery(hql);
		return ((Long) q.uniqueResult()).intValue();
	}

	/**
	 * Retorna as orientações acadêmicas ativas de um discente de um determinado
	 * curso ou programa se o discente não pertencer ao curso ou programa
	 * informado não retornara nada.
	 *
	 * @param idCurso
	 *            id do curso
	 * @param idPrograma
	 *            id do programa
	 * @param idDiscente
	 *            id do discente
	 * @return
	 * @throws DAOException
	 */
	public Collection<OrientacaoAcademica> findAtivoByDiscenteCurso(Integer idCurso, Integer idPrograma, int idDiscente) throws DAOException {
		Criteria c = getSession().createCriteria(OrientacaoAcademica.class);
		Criteria discCriteria = c.createCriteria("discente");
		discCriteria.add(Restrictions.eq("id", idDiscente));
		c.add(Restrictions.eq("cancelado", false));

		if (idCurso != null)
			discCriteria.add(Restrictions.eq("curso.id", idCurso));

		if (idPrograma != null)
			discCriteria.add(Restrictions.eq("gestoraAcademica.id", idPrograma));

		c.add(Restrictions.isNull("fim"));
		Criteria subPessoa = discCriteria.createCriteria("pessoa");
		subPessoa.addOrder(Order.asc("nome"));
		@SuppressWarnings("unchecked")
		Collection<OrientacaoAcademica> lista = c.list();
		return lista;
	}

	/**
	 * Retorna as orientações ativas do servidor informado filtrando somente os
	 * alunos do curso ou programa informado.
	 *
	 * @param idCurso
	 * @param idPrograma
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public Collection<OrientacaoAcademica> findAtivoByServidorCurso(Integer idCurso, Integer idPrograma, int idServidor) throws DAOException {
		return findAtivoByServidorCursoTipo(idCurso, idPrograma, idServidor);
	}

	/**
	 * Retorna as orientações ativas do servidor informado, por curso, programa, e tipos de orientação acadêmica.
	 *
	 * @param idCurso
	 * @param idPrograma
	 * @param idServidor
	 * @param tipos
	 * @return
	 * @throws DAOException
	 */
	public Collection<OrientacaoAcademica> findAtivoByServidorCursoTipo(
			Integer idCurso, Integer idPrograma, int idServidor, Character... tipos) throws DAOException {
		
		
		StringBuilder hql = new StringBuilder(" SELECT DISTINCT orientacao.id_orientacao_academica," 
				+ " s.id_servidor, p_serv.nome, s.siape, "
				+ " d.id_discente, d.matricula, d.nivel, " 
				+ " d.status, p_disc.nome as nome_discente, p_disc.email, " 
				+ " orientacao.inicio, orientacao.fim, orientacao.tipoOrientacao, usr.id_usuario " 
				
				+ " FROM graduacao.orientacao_academica orientacao "
				+ " JOIN discente d using(id_discente) "
				+ " JOIN comum.pessoa p_disc on (p_disc.id_pessoa = d.id_pessoa) "
				+ " JOIN rh.servidor s using(id_servidor) " 
				+ " JOIN comum.pessoa p_serv on(p_serv.id_pessoa = s.id_pessoa) "
				+ " LEFT JOIN comum.usuario usr on(usr.id_pessoa = d.id_pessoa)" );
		
		
		hql.append(" WHERE orientacao.fim is null ");
		hql.append(" AND orientacao.data_finalizacao is null ");
		hql.append(" AND s.id_servidor = :idServidor");
		hql.append(" AND orientacao.cancelado = falseValue() ");
		if (idCurso != null) {
			hql.append(" AND d.id_curso = :idCurso");
			hql.append(" AND orientacao.tipoorientacao = '"
					+ OrientacaoAcademica.ACADEMICO + "'");
		}
		if (idPrograma != null) {
			hql.append(" AND d.id_gestora_academica = :idPrograma ");
			hql.append(" AND orientacao.tipoorientacao in ( '"
					+ OrientacaoAcademica.ACADEMICO + "' ) ");
		}
		if (tipos != null && tipos.length != 0)
			hql.append(" AND orientacao.tipoorientacao in " + gerarStringIn(tipos));

		hql.append("order by p_disc.nome");

		Query q = getSession().createSQLQuery(hql.toString());
		q.setInteger("idServidor", idServidor);
		if (idCurso != null)
			q.setInteger("idCurso", idCurso);
		if (idPrograma != null)
			q.setInteger("idPrograma", idPrograma);

		Collection<OrientacaoAcademica> lista = new ArrayList<OrientacaoAcademica>();// = q.list();
		@SuppressWarnings("unchecked")
		Collection<Object[]> rs = q.list();
		
		int i = 0;
		for( Object[] linha : rs ){
			i = 0;
			
			OrientacaoAcademica oa = new  OrientacaoAcademica();
			oa.setId( (Integer) linha[i++] );
			oa.setServidor( new Servidor((Integer) linha[i++], (String) linha[i++], (Integer) linha[i++]) );
			
			int idDisc = (Integer) linha[i++];
			BigInteger mat = (BigInteger) linha[i++];
			char nivel = (Character) linha[i++];
			short status = (Short) linha[i++];
			String nomeDisc = (String) linha[i++];
			String email = (String) linha[i++];
			
			oa.setDiscente( new Discente(idDisc, mat.longValue(), nomeDisc, status, nivel) );
			oa.getDiscente().getPessoa().setEmail(email);
			oa.setInicio( (Date) linha[i++] ); 
			oa.setFim( (Date) linha[i++] );
			oa.setTipoOrientacao( (Character) linha[i++] );
			
			Integer idUsr = (Integer) linha[i++];
			if( idUsr != null ){
				oa.getDiscente().setUsuario(new Usuario());
				oa.getDiscente().getUsuario().setId(idUsr);
			}
			lista.add( oa );
		}
		
		return lista;
	}

	/**
	 * Buscar todos os orientandos de pós-graduação de um servidor.
	 *
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public Collection<OrientacaoAcademica> findAllStrictoByServidor(Servidor servidor) throws DAOException {
		String projecao = " id, servidor.id, servidor.siape, servidor.pessoa.nome, "
			+ " discente.id, discente.matricula, discente.nivel, discente.status, discente.pessoa.nome, discente.pessoa.email, discente.tipo, "
			+ "inicio, fim, tipoOrientacao";
		StringBuilder hql = new StringBuilder("SELECT" + projecao);
		hql.append(" FROM OrientacaoAcademica orientacao");
		hql.append(" WHERE servidor.id = :idServidor ");
		hql.append(" AND fim is null ");
		hql.append(" AND cancelado = falseValue() ");
		hql.append(" AND discente.nivel in " + gerarStringIn(NivelEnsino.getNiveisStricto()) );
		hql.append(" AND discente.status not in " + gerarStringIn(StatusDiscente.getAfastamentosPermanentes()) );
		hql.append(" ORDER BY discente.nivel desc, discente.pessoa.nome");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idServidor", servidor.getId());
		try {
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			return HibernateUtils.parseTo(lista, projecao, OrientacaoAcademica.class);
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	
	/**
	 * Buscar o total de orientações de um servidor em um nível especificado.
	 *
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public Integer findTotalOrientacoesServidorNivel(Servidor servidor, DocenteExterno docenteExterno, char nivel, Integer tipoDiscente, Integer idPrograma) throws DAOException {
		String projecao = " count (distinct id) ";
		StringBuilder hql = new StringBuilder(" SELECT " + projecao);
		hql.append(" FROM OrientacaoAcademica orientacao");
		
		if(!ValidatorUtil.isEmpty(servidor)) {
			hql.append(" WHERE servidor.id = " + servidor.getId());
		} else {
			hql.append(" WHERE docenteExterno.id = " + docenteExterno.getId());
		}
		
		hql.append(" AND fim is null ");
		hql.append(" AND cancelado = falseValue() ");
		hql.append(" AND discente.nivel = '" + nivel + "'" );
		hql.append(" AND discente.tipo = " + tipoDiscente );
		hql.append(" AND discente.status not in " + gerarStringIn(StatusDiscente.getAfastamentosPermanentes()) );	
		if (idPrograma != null && idPrograma > 0 )
			hql.append(" AND discente.gestoraAcademica.id = " + idPrograma);

		Query q = getSession().createQuery(hql.toString());
		
		try {			
			return new Integer(q.uniqueResult().toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna a tese orientada de um discente por um determinado docente.
	 * @param discente
	 * @param orientador
	 * @return
	 * @throws DAOException
	 */
	public TeseOrientada findTeseOrientadaByDiscenteOrientador(
			DiscenteAdapter discente, Servidor orientador) throws DAOException {
		if (orientador != null) {
			Criteria c = getSession().createCriteria(TeseOrientada.class);
			c.add(or(eq("ativo", true), isNull("ativo")));
			c.add(isNull("periodoFim"));
			c.add(isNull("dataPublicacao"));
			c.add(eq("servidor.id", orientador.getId()));
			c.add(eq("orientandoDiscente.id", discente.getId()));
			c.setMaxResults(1);
			return (TeseOrientada) c.uniqueResult();
		} else {
			return null;
		}
	}

	/**
	 * Retorna todas as orientações acadêmicas de um programa ou curso de pós-graduação.
	 * @param idPrograma
	 * @param idCurso
	 * @param ativo
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	public List<OrientacaoAcademica> findTotalOrientacoesByProgramaOrCursoDiscente(Integer idPrograma, Integer idCurso,
			Boolean ativo, StatusDiscente status) throws DAOException {
		
		StringBuilder hqlServidor = new StringBuilder("SELECT p.id AS ID_PESSOA, p.nome AS NOME, COUNT(distinct oa.id) AS TOTAL, " +
				" s.id AS ID_SERVIDOR, s.siape AS SIAPE FROM OrientacaoAcademica oa JOIN oa.servidor s JOIN s.pessoa p ");
		
		StringBuilder hqlDocenteExterno = new StringBuilder("SELECT p.id AS ID_PESSOA, p.cpf_cnpj AS CPF, p.nome AS NOME, COUNT(distinct oa.id) AS TOTAL, " +
		" de.id AS ID_DOCENTE_EXTERNO, de.matricula AS MATRICULA FROM OrientacaoAcademica oa JOIN oa.docenteExterno de JOIN de.pessoa p ");

		
		StringBuilder hql = new StringBuilder();
		hql.append(" WHERE oa.cancelado = falseValue() ");
		if (isEmpty(status.getId()))
			hql.append(" AND oa.discente.status in " + gerarStringIn( StatusDiscente.getValidos() ));
		else
			hql.append(" AND oa.discente.status = " + status.getId() );
		
		if (idPrograma != null && idPrograma > 0)
			hql.append(" AND oa.discente.gestoraAcademica.id = :idPrograma ");
		if( idCurso != null )
			hql.append(" AND oa.discente.curso.id = :idCurso");
		
		if (ativo == null) {
		} else if (ativo)
			hql.append(" AND ( oa.fim is null OR oa.fim > :hoje )");
		else if (!ativo)
			hql.append(" AND ( oa.fim is not null OR oa.fim < :hoje  )");

		List<OrientacaoAcademica> orientacoes = new ArrayList<OrientacaoAcademica>();
		
		Query qServidor = getSession().createQuery( hqlServidor.toString() + hql.toString() + " GROUP BY p.id, p.nome, s.id, s.siape ");
		if (idPrograma != null && idPrograma > 0)
			qServidor.setInteger("idPrograma", idPrograma);
		if( idCurso != null )
			qServidor.setInteger("idCurso", idCurso);
		
		if (ativo != null)
			qServidor.setDate("hoje", new Date());
		qServidor.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> resultadoServidor = qServidor.list();
		for ( Map<String, Object> mapa : resultadoServidor ) {
			OrientacaoAcademica orientacao = new OrientacaoAcademica();
			
			orientacao.setServidor(new Servidor());
			orientacao.getServidor().setId( (Integer) mapa.get( "ID_SERVIDOR" ) );
			orientacao.getServidor().setSiape( (Integer) mapa.get( "SIAPE" ) );
			orientacao.getServidor().setPessoa(new Pessoa());
			orientacao.getServidor().getPessoa().setId( (Integer) mapa.get( "ID_PESSOA" ) );
			orientacao.getServidor().getPessoa().setNome( (String) mapa.get( "NOME" ) );
			orientacao.setTotal( ((Long) mapa.get( "TOTAL" )).intValue() );

			orientacoes.add(orientacao);
		}
		
		Query qDocenteExterno = getSession().createQuery( hqlDocenteExterno.toString() + hql.toString() + " GROUP BY p.id, p.cpf_cnpj, p.nome, de.id, de.matricula ");
		if (idPrograma != null && idPrograma > 0)
			qDocenteExterno.setInteger("idPrograma", idPrograma);
		if( idCurso != null )
			qDocenteExterno.setInteger("idCurso", idCurso);
		
		if (ativo != null)
			qDocenteExterno.setDate("hoje", new Date());
		qDocenteExterno.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> resultadoDE = qDocenteExterno.list();
		for ( Map<String, Object> mapa : resultadoDE ) {
			OrientacaoAcademica orientacao = new OrientacaoAcademica();
			
			orientacao.setDocenteExterno( new DocenteExterno() );
			orientacao.getDocenteExterno().setId( (Integer) mapa.get( "ID_DOCENTE_EXTERNO" ) );
			orientacao.getDocenteExterno().setMatricula( (String) mapa.get( "MATARICULA" ) );
			orientacao.getDocenteExterno().setPessoa(new Pessoa());
			orientacao.getDocenteExterno().getPessoa().setNome( (String) mapa.get( "NOME" ) );
			orientacao.getDocenteExterno().getPessoa().setCpf_cnpj( (Long) mapa.get( "CPF" ) );
			orientacao.setTotal( ((Long) mapa.get( "TOTAL" )).intValue() );

			orientacoes.add(orientacao);
		}
		
		return orientacoes;
		
	}
	
	/**
	 * Retorna todos os discentes de um determinado programa de pós-graduação.
	 * 
	 * @param programa
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<OrientacaoAcademica> findByProgramas(int programa,
			NivelEnsino nivel) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();

			hql.append(" SELECT o.id, ds.id, ds.matricula, ds.status, ds.nivel, ds.anoIngresso, pdi.id, pdi.nome, pdi.email,  ");
			hql.append(" s.siape, ps.nome, pd.nome, o.tipoOrientacao, ds.nivel, o.fim, o.cancelado, c.id, c.nome, c.nivel, ");
			hql.append(" tcs.id, tcs.descricao FROM OrientacaoAcademica o RIGHT JOIN o.discente ds LEFT JOIN o.docenteExterno de ");
			hql.append(" LEFT JOIN o.servidor s LEFT JOIN s.pessoa ps LEFT JOIN de.pessoa pd LEFT JOIN ds.pessoa pdi ");
			hql.append(" INNER JOIN ds.curso c LEFT JOIN c.tipoCursoStricto tcs WHERE c.unidade.id = :programa ");
			hql.append(" AND ds.status IN ( "+StatusDiscente.ATIVO+","+StatusDiscente.EM_HOMOLOGACAO+" ) ");
			
			if (!isEmpty(nivel))
				hql.append("ds.nivel = :nivel");
			
			hql.append(" order by ds.nivel DESC, c.nome ASC, ds.pessoa.nome ASC, o.fim DESC, o.inicio DESC, o.tipoOrientacao DESC ");
			  
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("programa", programa);

			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			List<OrientacaoAcademica> result = new ArrayList<OrientacaoAcademica>();

			for (int a = 0; a < lista.size(); a++) {
				OrientacaoAcademica orientacaoAcademica = new OrientacaoAcademica();
				Object[] colunas = lista.get(a);
				
				orientacaoAcademica.setId(!isEmpty(colunas[0])?(Integer) colunas[0]:a);

				orientacaoAcademica.setDiscente(new Discente());
				orientacaoAcademica.getDiscente().setId((Integer) colunas[1]);
				orientacaoAcademica.getDiscente().setMatricula((Long) colunas[2]);
				orientacaoAcademica.getDiscente().setStatus((Integer) colunas[3]);
				orientacaoAcademica.getDiscente().setNivel((Character) colunas[4]);
				orientacaoAcademica.getDiscente().setAnoIngresso((Integer) colunas[5]);
				orientacaoAcademica.setFim((Date) colunas[14]);
				if(!isEmpty(colunas[15]))
				orientacaoAcademica.setCancelado((Boolean) colunas[15]);


				orientacaoAcademica.getDiscente().setPessoa(new Pessoa());
				orientacaoAcademica.getDiscente().getPessoa().setId((Integer) colunas[6]);
				orientacaoAcademica.getDiscente().getPessoa().setNome((String) colunas[7]);
				orientacaoAcademica.getDiscente().getPessoa().setEmail((String) colunas[8]);
				if(!isEmpty(colunas[13]))
					orientacaoAcademica.getDiscente().setNivel((Character) colunas[13]);
				
				if(!isEmpty(colunas[9])){
					orientacaoAcademica.setServidor(new Servidor());
					orientacaoAcademica.getServidor().setSiape((Integer) colunas[9]);
					orientacaoAcademica.getServidor().setPessoa(new Pessoa());
					orientacaoAcademica.getServidor().getPessoa().setNome((String) colunas[10]);
				
				}
				if(!isEmpty(colunas[11])){
				orientacaoAcademica.setDocenteExterno(new DocenteExterno());
				orientacaoAcademica.getDocenteExterno().setPessoa(new Pessoa());
				orientacaoAcademica.getDocenteExterno().getPessoa().setNome((String) colunas[11]);
				}
				
				if(!isEmpty(colunas[12]))
					orientacaoAcademica.setTipoOrientacao((Character) colunas[12]);
				
				orientacaoAcademica.getDiscente().setCurso(new Curso((Integer) colunas[16]));
				orientacaoAcademica.getDiscente().getCurso().setNome((String) colunas[17]);
				orientacaoAcademica.getDiscente().getCurso().setNivel((Character) colunas[18]);
				
				orientacaoAcademica.getDiscente().getCurso().setTipoCursoStricto(new TipoCursoStricto((Integer) colunas[16], (String) colunas[20]));
				
				
				if(!result.contains(orientacaoAcademica))
					result.add(orientacaoAcademica);

			}
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}
	
	
	/**
	 * Retorna as orientações de discentes de um dado programa.
	 * Ativo indica a situação das OrientacoesAcademicas que devem ser
	 * retornadas.
	 *
	 * @param idPrograma
	 * @param idCurso
	 * @param ativo
	 *            NULL para TODAS, TRUE para ATIVAS, FALSE para INATIVAS
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	public List<OrientacaoAcademica> findByProgramaOrCursoDiscente(Integer idPrograma, Integer idCurso,
			Boolean ativo, StatusDiscente status) throws DAOException {

		StringBuilder hql = new StringBuilder("SELECT oa.id AS ID, oa.inicio AS INICIO, oa.fim AS FIM, oa.tipoOrientacao AS TIPO, "
				+ " s.id as ID_SERVIDOR, s.siape AS SIAPE, p2.nome AS NOME_SERVIDOR, " +
				  " oa.discente.id AS ID_DISCENTE, oa.discente.tipo AS TIPO_DISCENTE, oa.discente.matricula as MAT_DISCENTE, oa.discente.pessoa.nome AS NOME_DISCENTE, "
				+ " oa.discente.status AS STATUS, de.id AS ID_DEXTERNO, de.matricula AS MAT_DEXTERNO, p1.nome AS NOME_DEXTERNO, "
				+ " p1.cpf_cnpj AS CPF, oa.discente.nivel AS NIVEL ");
		hql.append(" FROM OrientacaoAcademica oa " +
				" LEFT JOIN oa.docenteExterno de LEFT JOIN de.pessoa p1 " +
				" LEFT JOIN oa.servidor s LEFT JOIN s.pessoa p2");

		hql.append(" WHERE oa.cancelado = falseValue() ");
		
		if (isEmpty(status.getId()))
			hql.append(" AND oa.discente.status in " + gerarStringIn( StatusDiscente.getValidos() ));
		else
			hql.append(" AND oa.discente.status = " + status.getId());

		if (idPrograma != null && idPrograma > 0 )
			hql.append(" AND oa.discente.gestoraAcademica.id = :idPrograma ");
		if( idCurso != null )
			hql.append(" AND oa.discente.curso.id = :idCurso");
		
		if (ativo == null) {
		} else if (ativo) 
			hql.append(" AND ( oa.fim is null OR oa.fim > :hoje )");
		else if (!ativo)
			hql.append(" AND ( oa.fim is not null OR oa.fim < :hoje  )");

		Query q = getSession().createQuery(hql.toString());
		if (idPrograma != null && idPrograma > 0 )
			q.setInteger("idPrograma", idPrograma);
		if( idCurso != null )
			q.setInteger("idCurso", idCurso);
		
		if (ativo != null)
			q.setDate("hoje", new Date());

		List<OrientacaoAcademica> orientacoes = new ArrayList<OrientacaoAcademica>();

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> resultado = q.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP ).list();
		for ( Map<String, Object> mapa : resultado ) {
			OrientacaoAcademica orientacao = new OrientacaoAcademica();
			orientacao.setId( (Integer) mapa.get("ID") );
			orientacao.setInicio( (Date) mapa.get("INICIO") );
			orientacao.setFim( (Date) mapa.get("FIM") );
			orientacao.setTipoOrientacao( (Character) mapa.get("TIPO") );
			orientacao.setDiscente(new Discente() );
			orientacao.getDiscente().setId( (Integer) mapa.get( "ID_DISCENTE" ) );
			orientacao.getDiscente().setMatricula( (Long) mapa.get("MAT_DISCENTE") );
			orientacao.getDiscente().setStatus( (Integer) mapa.get("STATUS") );
			orientacao.getDiscente().setTipo( (Integer) mapa.get("TIPO_DISCENTE") );
			orientacao.getDiscente().setNivel( (Character) mapa.get("NIVEL") );
			orientacao.getDiscente().setPessoa(new Pessoa());
			orientacao.getDiscente().getPessoa().setNome( (String) mapa.get( "NOME_DISCENTE") );
			if( mapa.get( "ID_SERVIDOR" ) != null ){
				orientacao.setServidor(new Servidor());
				orientacao.getServidor().setId( (Integer) mapa.get( "ID_SERVIDOR" ) );
				orientacao.getServidor().setSiape( (Integer) mapa.get( "SIAPE" ) );
				orientacao.getServidor().setPessoa(new Pessoa());
				orientacao.getServidor().getPessoa().setNome( (String) mapa.get( "NOME_SERVIDOR" ) );
			}else if( mapa.get("ID_DEXTERNO") != null ){
				orientacao.setDocenteExterno( new DocenteExterno() );
				orientacao.getDocenteExterno().setId( (Integer) mapa.get( "ID_DEXTERNO" ) );
				orientacao.getDocenteExterno().setMatricula( (String) mapa.get( "MAT_DEXTERNO" ) );
				orientacao.getDocenteExterno().setPessoa(new Pessoa());
				orientacao.getDocenteExterno().getPessoa().setNome( (String) mapa.get( "NOME_DEXTERNO" ) );
				orientacao.getDocenteExterno().getPessoa().setCpf_cnpj( (Long) mapa.get( "CPF" ) );
			}

			orientacoes.add(orientacao);
		}

		return orientacoes;
	}

	/**
	 * Retorna o co-orientador atual de um discente.
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public OrientacaoAcademica findCoOrientadorAtivoByDiscente(int idDiscente)
			throws DAOException {
		Criteria c = getSession().createCriteria(OrientacaoAcademica.class);
		c.add(Restrictions.eq("discente.id", idDiscente));
		c
				.add(Restrictions.eq("tipoOrientacao",
						OrientacaoAcademica.CoORIENTADOR));
		c.add(Restrictions.eq("cancelado", false));
		c.add(Restrictions.isNull("fim"));
		c.addOrder(Order.desc("inicio"));
		@SuppressWarnings("unchecked")
		List<OrientacaoAcademica> orientacoes = c.list();
		if (orientacoes != null && orientacoes.size() > 0)
			return orientacoes.get(0);
		return null;
	}

	/**
	 * Retorna o último co-orientador atual de um discente.
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public OrientacaoAcademica findUltimoCoOrientadorByDiscente(int idDiscente)
			throws DAOException {
		Criteria c = getSession().createCriteria(OrientacaoAcademica.class);
		c.add(Restrictions.eq("discente.id", idDiscente));
		c.add(Restrictions.eq("tipoOrientacao", OrientacaoAcademica.CoORIENTADOR));
		c.add(Restrictions.eq("cancelado", false));
		c.addOrder(Order.desc("inicio"));
		@SuppressWarnings("unchecked")
		List<OrientacaoAcademica> orientacoes = c.list();
		if (orientacoes != null && orientacoes.size() > 0)
			return orientacoes.get(0);
		return null;
	}	
	
	/**
	 * Busca as orientações acadêmicas de um discente por unidade e nível de ensino.
	 * @param idDiscente
	 * @param unidadeGestora
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<OrientacaoAcademica> findAtivoByDiscenteUnidadeNivel(
			int idDiscente, int unidadeGestora, char nivel) throws DAOException {

		Criteria c = getSession().createCriteria(OrientacaoAcademica.class);
		c.add(Restrictions.eq("cancelado", false));
		c.add(Restrictions.isNull("fim"));

		Criteria discCriteria = c.createCriteria("discente");
		discCriteria.add(Restrictions.eq("id", idDiscente));

		Criteria curso = discCriteria.createCriteria("curso");
		curso.add(Restrictions.eq("unidade.id", unidadeGestora));

		Criteria subPessoa = discCriteria.createCriteria("pessoa");
		subPessoa.addOrder(Order.asc("nome"));
		@SuppressWarnings("unchecked")
		Collection<OrientacaoAcademica> lista = c.list();
		return lista;
	}

	/**
	 * Busca as orientações acadêmicas de um docente por unidade e nível de ensino.
	 * @param unidadeGestora
	 * @param idServidor
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<OrientacaoAcademica> findAtivoByServidorUnidadeNivel( int unidadeGestora, int idServidor, char nivel ) throws DAOException {
		
		String construtor = " select new OrientacaoAcademica( orientacao.id, orientacao.servidor.id, orientacao.servidor.siape, orientacao.servidor.pessoa.nome, "
			+ " orientacao.discente.id, orientacao.discente.matricula, orientacao.discente.nivel, orientacao.discente.pessoa.nome, orientacao.discente.pessoa.email, orientacao.inicio, orientacao.fim, orientacao.tipoOrientacao ) FROM OrientacaoAcademica orientacao ";
		
		StringBuilder hql = new StringBuilder(construtor);
		hql.append(" WHERE orientacao.fim is null ");
		hql.append(" AND orientacao.dataFinalizacao is null ");
		hql.append(" AND orientacao.servidor.id = :idServidor");
		hql.append(" AND orientacao.cancelado = falseValue()");
		hql.append(" AND orientacao.discente.curso.unidade.id = :unidade");
		hql.append(" AND orientacao.discente.nivel = :nivel");
		hql.append(" AND orientacao.tipoOrientacao = '" + OrientacaoAcademica.ACADEMICO
				+ "'");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idServidor", idServidor);
		q.setInteger("unidade", unidadeGestora);
		q.setCharacter("nivel", nivel);

		@SuppressWarnings("unchecked")
		Collection<OrientacaoAcademica> lista = q.list();
		return lista;
	}

	/**
	 * Busca as orientações acadêmicas ativas de uma unidade por nível.
	 * @param unidadeGestora
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<OrientacaoAcademica> findAtivoByUnidadeNivel(int unidadeGestora, char nivel) throws DAOException {

		Criteria c = getSession().createCriteria(OrientacaoAcademica.class);
		c.add(Restrictions.eq("cancelado", false));
		c.add(Restrictions.isNull("fim"));

		Criteria discCriteria = c.createCriteria("discente");
		discCriteria.add(Restrictions.eq("nivel", nivel));

		Criteria curso = discCriteria.createCriteria("curso");
		curso.add(Restrictions.eq("unidade.id", unidadeGestora));

		Criteria subPessoa = discCriteria.createCriteria("pessoa");
		subPessoa.addOrder(Order.asc("nome"));
		@SuppressWarnings("unchecked")
		Collection<OrientacaoAcademica> lista = c.list();
		return lista;
	}

	/**
	 * Retorna a última ORIENTAÇÃO do discente informado,
	 * sendo ela ativa ou inativa.
	 * @param idDiscente
	 * @param tipo
	 * @param somenteAtivos
	 * @return
	 * @throws DAOException
	 */
	public OrientacaoAcademica findUltimaOrientacaoByDiscente(int idDiscente) throws DAOException {
		Criteria c = getSession().createCriteria(OrientacaoAcademica.class);
		c.add(Restrictions.eq("discente.id", idDiscente));
		c.add(Restrictions.eq("cancelado", false));
		c.add(Restrictions.eq("tipoOrientacao", OrientacaoAcademica.ORIENTADOR));
		c.addOrder(Order.desc("inicio"));

		c.setMaxResults(1);

		return (OrientacaoAcademica) c.uniqueResult();
	}

	/**
	 * Busca todos os orientandos de pós-graduação de um docente externo.
	 *
	 * @param docenteExterno
	 * @return
	 * @throws DAOException
	 */
	public Collection<OrientacaoAcademica> findAllStrictoByDocenteExterno(DocenteExterno docenteExterno) throws DAOException {
		
		String projecao = " id, docenteExterno.id, docenteExterno.pessoa.nome, "
			+ " discente.id, discente.matricula, discente.nivel, discente.status, discente.pessoa.nome, discente.pessoa.email, discente.tipo, "
			+ "inicio, fim, tipoOrientacao";
		
		StringBuilder hql = new StringBuilder("SELECT" + projecao);
		hql.append(" FROM OrientacaoAcademica orientacao");
		hql.append(" WHERE docenteExterno.id = :idDocenteExterno ");
		hql.append(" AND fim is null ");
		hql.append(" AND cancelado = falseValue() ");
		hql.append(" AND discente.nivel in " + gerarStringIn(NivelEnsino.getNiveisStricto()) );
		hql.append(" ORDER BY discente.nivel desc, discente.pessoa.nome");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idDocenteExterno", docenteExterno.getId());
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return HibernateUtils.parseTo(lista, projecao, OrientacaoAcademica.class);
	}
	
	/**
	 * Busca todas as orientações de um docente num dado período e nível de ensino.
	 * @param servidor
	 * @param anoInicio
	 * @param anoFim
	 * @param cancelado
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<OrientacaoAcademica> findAllByServidorPeriodo(Servidor servidor, Integer anoInicio, Integer anoFim, Boolean cancelado, Character... nivel) throws DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append("select orientacao FROM OrientacaoAcademica orientacao" +
				" inner join fetch orientacao.discente discente " +
				" left join orientacao.docenteExterno de" +
			" WHERE (orientacao.servidor.id = :idServidor " +
			"  or de.servidor.id = :idServidor)"+
			" AND year(inicio) <= :anoInicio "+
			" AND (fim is null OR year(fim) >= :anoFim)"+
			" AND orientacao.discente.tipo = "+Discente.REGULAR+
			" AND orientacao.discente.status <> "+StatusDiscente.DEFENDIDO);
		if(nivel != null) {
			hql.append(" and discente.nivel in " + UFRNUtils.gerarStringIn(nivel));
		}
		if (cancelado != null) {
			hql.append(" and orientacao.cancelado = :cancelado ");
		}
		
		hql.append(" ORDER BY orientacao.discente.curso.unidade.nome, orientacao.discente.pessoa.nome");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idServidor", servidor.getId());
		q.setInteger("anoInicio", anoInicio);
		q.setInteger("anoFim", anoFim);
		if (cancelado != null) {
			q.setBoolean("cancelado", cancelado);
		}
		@SuppressWarnings("unchecked")
		Collection<OrientacaoAcademica> lista = q.list();
		return lista;
	}
	
	/**
	 * Retorna uma coleção contendo todos as orientações acadêmicas do servidor 
	 * passado como parâmetro.
	 * 
	 * @param servidor
	 * @param nome
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<OrientacaoAcademica> findAllOrientandosByServidor(Servidor servidor) throws HibernateException, DAOException {
		StringBuffer hql = new StringBuffer();
		String projecao = "o.id, o.cancelado, o.fim, o.discente.id, o.discente.matricula, o.discente.pessoa.nome";
		
		hql.append("select " + projecao + " from OrientacaoAcademica o JOIN o.discente d ");
		hql.append(" WHERE o.servidor.id = " + servidor.getId());
		hql.append(" order by d.pessoa.nome asc");
		
		Query q = getSession().createQuery(hql.toString());
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		return HibernateUtils.parseTo(lista, projecao, OrientacaoAcademica.class, "o");
	}
	
	/**
	 * Busca todas as orientações de um docente num dado período, otimizando a consulta
	 * através de uma projeção.
	 * @param servidor
	 * @param anoInicio
	 * @param anoFim
	 * @return
	 * @throws DAOException
	 */
	public Collection<OrientacaoAcademica> findAllByServidorPeriodoOtimizado(Servidor servidor, Date dataInicial, Date dataFinal) throws DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT orientacao.inicio, orientacao.fim, orientacao.discente.pessoa.nome, orientacao.discente.curso.nome");
		hql.append(" FROM OrientacaoAcademica orientacao");
		hql.append(" WHERE orientacao.servidor.id = :idServidor AND ");
		hql.append(String.format("(%1$s = %3$s or (%1$s > %3$s and %1$s <= %4$s) or (%1$s < %3$s and (%3$s <= %2$s or %2$s is null)))", "inicio", "fim", ":dataInicio", ":dataFim"));
		hql.append(" AND (orientacao.cancelado is null OR orientacao.cancelado = falseValue())");
		hql.append(" AND orientacao.tipoOrientacao = '"+OrientacaoAcademica.ACADEMICO+"'");
		hql.append(" AND orientacao.discente.nivel = '"+NivelEnsino.GRADUACAO+"'");
		hql.append(" order by orientacao.discente.pessoa.nome");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idServidor", servidor.getId());
		q.setDate("dataInicio", dataInicial);
		q.setDate("dataFim", dataFinal);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		ArrayList<OrientacaoAcademica> result = new ArrayList<OrientacaoAcademica>();
		for (int i = 0; i < lista.size(); i++) {
			int col = 0;
			Object[] colunas = lista.get(i);
			
			OrientacaoAcademica orientacao = new OrientacaoAcademica();
			orientacao.setInicio((Date) colunas[col++]);
			orientacao.setFim((Date) colunas[col++]);
			
			Discente discente = new Discente();
			discente.getPessoa().setNome((String) colunas[col++]);
			discente.getCurso().setNome((String) colunas[col++]);
			
			orientacao.setDiscente(discente);
			
			result.add(orientacao);
		}
		return result;
	}
	
	/**
	 * Traz as orientações ativas que um discente possui com um professor.
	 * 
	 * @param idDiscente
	 * @param idOrientador
	 * @return
	 * @throws DAOException
	 */
	public List<OrientacaoAcademica> findByDiscenteAndOrientador(int idDiscente, int idOrientador) throws DAOException {
		StringBuilder sb = new StringBuilder();
		
		sb.append("select orientacao from OrientacaoAcademica orientacao");
		sb.append(" where orientacao.discente.id = :idDiscente");
		sb.append(" and orientacao.servidor.id = :idOrientador");
		sb.append(" and orientacao.fim is null");
		sb.append(" and orientacao.cancelado = falseValue()");
		
		Query query = getSession().createQuery(sb.toString());
		query.setInteger("idDiscente", idDiscente);
		query.setInteger("idOrientador", idOrientador);
		
		@SuppressWarnings("unchecked")
		List<OrientacaoAcademica> lista = query.list();
		return lista;
	}
	
	/**
	 * Método que realiza a consulta sql para um relatório, e retorna uma Lista
	 * das linhas da consulta.
	 *
	 * @param consultaSql
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 * @throws SQLException
	 */
	public List<Map<String, Object>> executeSql(String consultaSql)
			throws SQLException, HibernateException, DAOException {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> lista = getJdbcTemplate().queryForList(consultaSql);
		result.addAll(lista);
		return result;
	}


	/**
	 * Retorna dados referente ao Orientando. Tais como: matrícula, nome, data do inicio da orientação.
	 * 
	 * @param idDiscente
	 * @param ativa
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> findDadosOrientacaoByDiscente(int idDiscente, Boolean ativa ) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder("select d.matricula," +
				" p.nome," +
				" ori.inicio," +
				" ori.fim," +
				" ps.nome as orientador," +
				" case when fim is not null and cancelado = falseValue() then trueValue() else falseValue()  end as ativa" +
				" from graduacao.orientacao_academica as ori" +
				" inner join discente as d using (id_discente)" +
				" inner join comum.pessoa as p using (id_pessoa)" +
				" left join ensino.docente_externo de using (id_docente_externo)" +
				" left join rh.servidor s on (ori.id_servidor = s.id_servidor or de.id_servidor = s.id_servidor)" +
				" left join comum.pessoa ps on (s.id_pessoa = ps.id_pessoa)" +
				" where ori.id_discente = " + idDiscente);
		if (ativa != null) {
			if (ativa) {
			sqlconsulta.append(" and fim is not null" +
							   " and cancelado = falseValue()");
			} else {
				sqlconsulta.append(" and (fim < current_date" +
				   " or cancelado = trueValue())");
			}
		}
		sqlconsulta.append(" order by p.nome, ori.inicio desc");
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return result;
	}
	
	
	/** Busca os dados para a geração do relatório de orientações concluídas por orientador.
	 * @param idUnidade (opcional) caso seja zero, ou menor que zero, lista todas unidades
	 * @param ano (obrigatório) ano de publicação da tese/dissertação
	 * @return
	 */
	public List<Map<String, Object>> dadosRelatorioOrientacoesConcluidas(int idUnidade, int ano, Collection<Character> niveisEnsino) {
		String sql = " select "
			+ " u.id_unidade as id_unidade,"
			+ " u.nome as nome_unidade,"
			+ " s.id_servidor as id_servidor,"
			+ " s.siape as siape,"
			+ " p.nome as nome_docente,"
			+ " teo.titulo as titulo,"
			+ " tor.descricao as tipo_orientacao,"
			+ " coalesce (pd.nome, teo.orientando, 'NÃO INFORMADO') as orientando,"
			+ " teo.data_publicacao as data_publicacao"
			+ " from prodocente.tese_orientada teo"
			+ " inner join prodocente.tipo_orientacao tor using (id_tipo_orientacao)"
			+ " inner join rh.servidor s on (s.id_servidor=teo.id_servidor)"
			+ " inner join comum.pessoa p on (p.id_pessoa=s.id_pessoa)"
			+ " inner join comum.unidade u on (u.id_unidade=teo.id_programa_pos)"
			+ " left join discente d on (d.id_discente = teo.id_discente)"
			+ " left join comum.pessoa pd on (d.id_pessoa = pd.id_pessoa)"
			+ " where tor.nivelensino in " + gerarStringIn(niveisEnsino) 
			+ " and teo.orientacao = ?"
			+ " and extract(year from teo.data_publicacao) = ?"
			+ " and (teo.id_programa_pos = ? or 0 >= ?)"
			+ " order by u.nome, p.nome, pd.nome";
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> dadosRelatorio = getJdbcTemplate().queryForList(sql, new Object[] {"O", ano, idUnidade, idUnidade});
		return dadosRelatorio;
	}


	/**
	 * Retorna o total de orientações do servidor
	 * 
	 * @param idServidor
	 * @param idDocenteExterno
	 * @param nivel
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public int findTotalOrientacoesNivelTipoServidor(Servidor idServidor, DocenteExterno idDocenteExterno, Character nivel, int tipo ) throws DAOException {
		
		StringBuilder sql = 
			new StringBuilder("select count(*) from graduacao.orientacao_academica as oa inner join discente d using (id_discente)");
		
		if (idServidor != null) 
			sql.append(" where oa.id_servidor = " + idServidor.getId());
		else
			sql.append(" where oa.id_docente_externo = " + idDocenteExterno.getId());

		sql.append(" and tipoOrientacao = '" + OrientacaoAcademica.ORIENTADOR + "'");
		sql.append(" and d.nivel = '" + nivel + "'");
		sql.append(" and d.tipo = " + tipo);
		sql.append("and (oa.fim is null or now() <= oa.fim)");
		
		return getJdbcTemplate().queryForInt(sql.toString());
	}

	/**
	 * Busca tipos de orientação acadêmica de um docente cujo id servidor foi passado como parâmetro.
	 * @param id
	 * @return
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public List<Character> findTiposOrientacao(int idServidor) throws DAOException {
		return getSession().createQuery("select distinct o.tipoOrientacao from OrientacaoAcademica o where (o.fim is null or o.fim >= now()) and o.servidor.id = ?").setInteger(0, idServidor).list();
	}
	
}