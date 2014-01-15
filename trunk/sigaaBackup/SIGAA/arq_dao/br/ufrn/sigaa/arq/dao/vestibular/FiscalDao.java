/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/08/2009
 *
 */
package br.ufrn.sigaa.arq.dao.vestibular;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.vestibular.dominio.Fiscal;
import br.ufrn.sigaa.vestibular.dominio.JustificativaAusencia;
import br.ufrn.sigaa.vestibular.dominio.LocalAplicacaoProva;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.dominio.ResumoProcessamentoSelecao;

/** DAO responsável por consultas especializadas de Fiscais do Vestibular
 * 
 * @author Édipo Elder F. Melo
 *
 */
public class FiscalDao extends GenericSigaaDAO {

	/** Retorna uma lista de fiscais de uma pessoa, em ordem de participação de processos seletivos.
	 * @param pessoa
	 * @param numProcessosSeletivosAnteriores número de processos seletivos anteriores que participou. Caso seja zero, busca em todos todos processos seletivos. 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Fiscal> findByPessoa(Pessoa pessoa, int numProcessosSeletivosAnteriores) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(Fiscal.class);
			c.add(Restrictions.eq("pessoa.id", pessoa.getId()));
			if (numProcessosSeletivosAnteriores > 0){
				c.setMaxResults(numProcessosSeletivosAnteriores);
			}
			c.addOrder(Order.desc("id"));
			@SuppressWarnings("unchecked")
			Collection<Fiscal> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/** Retorna o fiscal de um processo seletivo.
	 * @param pessoa
	 * @param processoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public Fiscal findByPessoaProcessoSeletivo(Pessoa pessoa,
			ProcessoSeletivoVestibular processoSeletivo) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(Fiscal.class);
			c.add(Restrictions.eq("pessoa.id", pessoa.getId()));
			c.add(Restrictions.eq("processoSeletivoVestibular.id",
					processoSeletivo.getId()));
			return (Fiscal) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/** Retorna a lista de fiscal do processo seletivo. 
	 * @param idProcessoSeletivo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Fiscal> findByProcessoSeletivo(int idProcessoSeletivo)
			throws DAOException {
		List<Fiscal> lista = new ArrayList<Fiscal>();
		// servidores
		Criteria c = getSession().createCriteria(Fiscal.class);
		c.add(Restrictions
				.eq("processoSeletivoVestibular.id", idProcessoSeletivo));
		c.createCriteria("servidor").createCriteria("unidade").addOrder(
				Order.asc("nome"));
		c.createCriteria("pessoa").addOrder(Order.asc("nome"));
		lista.addAll(c.list());
		// discentes
		c = getSession().createCriteria(Fiscal.class);
		c.add(Restrictions
				.eq("processoSeletivoVestibular.id", idProcessoSeletivo));
		c.createCriteria("discente").createCriteria("curso").addOrder(
				Order.asc("nome")).createCriteria("unidade").addOrder(
				Order.asc("nome"));
		c.createCriteria("pessoa").addOrder(Order.asc("nome"));
		lista.addAll(c.list());
		return lista;
	}

	/** Retorna uma lista de fiscais de um local de aplicação de prova.
	 * @param idProcessoSeletivo
	 * @param idLocalAplicacaoProva Caso igual a zero, será ignorado na consulta
	 * @param reserva
	 * @return
	 * @throws DAOException
	 */
	public List<Fiscal> findByProcessoSeletivoLocalAplicacao(int idProcessoSeletivo, int idLocalAplicacaoProva, boolean reserva, boolean excluirAusentes) throws DAOException {
		Criteria c = getSession().createCriteria(Fiscal.class);
		c.add(Restrictions.eq("reserva", reserva));
		if (excluirAusentes) {
			c.add(Restrictions.eq("presenteReuniao", true));
		}
		c.createCriteria("processoSeletivoVestibular").add(Restrictions.eq("id",idProcessoSeletivo));
		c.createCriteria("localAplicacaoProva").add(Restrictions.eq("id", idLocalAplicacaoProva));
		c.setFetchMode("discente", FetchMode.JOIN).setFetchMode("servidor", FetchMode.JOIN);
		c.setFetchMode("pessoa", FetchMode.JOIN).createCriteria("pessoa").addOrder(Order.asc("nomeAscii"));
		@SuppressWarnings("unchecked")
		List<Fiscal> lista = c.list();
		return lista;
	}

	/**
	 * Retorna uma lista de fiscais discentes com disponibilidade para trabalhar
	 * em outra cidade que não seja a do funcionamento do curso.
	 * 
	 * @param idProcessoSeletivo
	 * @param incluirAlocados
	 * @return
	 * @throws DAOException
	 */
	public List<Fiscal> findByProcessoSeletivoDisponibilidadeOutraCidade(
			int idProcessoSeletivo, boolean incluirAlocados) throws DAOException {
		// somente discentes podem viajar para outras cidades
		Criteria c = getSession().createCriteria(Fiscal.class);
		c.add(Restrictions
				.eq("processoSeletivoVestibular.id", idProcessoSeletivo));
		c.addOrder(Order.desc("recadastro"));
		c.addOrder(Order.asc("reserva"));
		c.createCriteria("discente").createCriteria("curso").addOrder(
				Order.asc("nome"));
		c.createCriteria("inscricaoFiscal").add(
				Restrictions.eq("disponibilidadeOutrasCidades", true));
		c.createCriteria("pessoa").addOrder(Order.asc("nome"));
		if (incluirAlocados)
			c.add(Restrictions.isNotNull("localAplicacaoProva"));
		else 
			c.add(Restrictions.isNull("localAplicacaoProva"));
		@SuppressWarnings("unchecked")
		List<Fiscal> lista = c.list();
		return lista;
	}

	/** Retorna uma lista de fiscais que tenham uma preferência a trabalhar em um local de aplicação de prova.
	 * @param idProcessoSeletivo
	 * @param idLocalAplicacaoProva
	 * @param preferencia
	 * @param alocado
	 * @return
	 * @throws DAOException
	 */
	public List<Fiscal> findByProcessoSeletivoLocalPreferencia(
			int idProcessoSeletivo, int idLocalAplicacaoProva, int preferencia,
			boolean alocado) throws DAOException {
		Criteria c;
		List<Fiscal> lista = new ArrayList<Fiscal>();
		// filtra por local preferencial
		List <Integer> idS = null;
		if (preferencia > 0) {
			String sql = "select fiscal.id_fiscal as id" +
					" from vestibular.fiscal" +
					" inner join vestibular.inscricao_fiscal using (id_inscricao_fiscal)" +
					" inner join vestibular.local_aplicacao_inscricao_fiscal using (id_inscricao_fiscal)" +
					" where ordem = :preferencia" +
					" and local_aplicacao_inscricao_fiscal.id_local_aplicacao_prova = :idLocalAplicacaoProva";
			Query q = getSession().createSQLQuery(sql).addScalar("id", Hibernate.INTEGER);
			q.setInteger("preferencia", preferencia - 1);
			q.setInteger("idLocalAplicacaoProva", idLocalAplicacaoProva);
			@SuppressWarnings("unchecked")
			List<Integer> listaIDs = q.list();
			idS = listaIDs;
		}
		// no caso dos servidores, o local é sempre o município.
		LocalAplicacaoProva local = findByPrimaryKey(idLocalAplicacaoProva,
				LocalAplicacaoProva.class);
		if (local != null) {
			c = getSession().createCriteria(Fiscal.class);
			c.add(Restrictions.eq("processoSeletivoVestibular.id",
					idProcessoSeletivo));
			c.createCriteria("servidor").createCriteria("unidade")
					.createCriteria("municipio").add(
							Restrictions.eq("id", local.getEndereco()
									.getMunicipio().getId()));
			c.addOrder(Order.desc("recadastro"));
			c.addOrder(Order.asc("reserva"));
			c.add(Restrictions.isNull("localAplicacaoProva"));
			c.createCriteria("pessoa").addOrder(Order.asc("nome"));
			c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			if (idS != null && idS.size() > 0 )
				c.add(Restrictions.in("id", idS));
			@SuppressWarnings("unchecked")
			Collection<Fiscal> listaServidor = c.list();
			lista.addAll(listaServidor);
		}
		// discentes
		c = getSession().createCriteria(Fiscal.class);
		c.add(Restrictions
				.eq("processoSeletivoVestibular.id", idProcessoSeletivo));
		c.createCriteria("inscricaoFiscal").createCriteria(
				"localAplicacaoProvas").add(
				Restrictions.eq("id", idLocalAplicacaoProva));
		c.addOrder(Order.desc("recadastro"));
		c.addOrder(Order.asc("reserva"));
		c.createCriteria("discente").createCriteria("curso").addOrder(
				Order.asc("nome"));
		if (alocado) {
			c.add(Restrictions.isNotNull("localAplicacaoProva"));
		} else {
			c.add(Restrictions.isNull("localAplicacaoProva"));
		}
		c.createCriteria("pessoa").addOrder(Order.asc("nome"));
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		if (idS != null && idS.size() > 0 )
			c.add(Restrictions.in("id", idS));
		@SuppressWarnings("unchecked")
		Collection<Fiscal> listaDiscente = c.list();
		lista.addAll(listaDiscente);
		return lista;
	}

	/** Retorna o total de fiscais reservas e titulares de um município.
	 * @param processoSeletivo
	 * @param municipio
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public long findTotalFiscaisMunicipio(ProcessoSeletivoVestibular processoSeletivo, Municipio municipio)	throws HibernateException, DAOException {
		return findTotalFiscaisMunicipio(processoSeletivo, municipio, true)
				+ findTotalFiscaisMunicipio(processoSeletivo, municipio, false);
	}

	/** Retorna o total de fiscais reservas ou titulares de um município.
	 * @param processoSeletivo
	 * @param municipio
	 * @param reserva
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public long findTotalFiscaisMunicipio(
			ProcessoSeletivoVestibular processoSeletivo, Municipio municipio,
			boolean reserva) throws HibernateException, DAOException {
		String hql;
		Query q;
		long subTotal = 0;
		if (municipio != null && municipio.getId() != 0) {
			// conta discentes
			hql = "select count(*)" + " from Fiscal fiscal"
					+ " where id_processo_seletivo = :idProcessoSeletivo"
					+ " and reserva = :reserva"
					+ " and fiscal.discente.curso.municipio.id = :idMunicipio";
			q = getSession().createQuery(hql);
			q.setInteger("idProcessoSeletivo", processoSeletivo.getId());
			q.setInteger("idMunicipio", municipio.getId());
			q.setBoolean("reserva", reserva);
			subTotal += (Long) q.uniqueResult();
			// conta servidores
			hql = "select count(*)"
					+ " from Fiscal fiscal"
					+ " where id_processo_seletivo = :idProcessoSeletivo"
					+ " and reserva = :reserva"
					+ " and fiscal.servidor.unidade.municipio.id = :idMunicipio";
			q = getSession().createQuery(hql);
			q.setInteger("idProcessoSeletivo", processoSeletivo.getId());
			q.setInteger("idMunicipio", municipio.getId());
			q.setBoolean("reserva", reserva);
			subTotal += (Long) q.uniqueResult();
			return subTotal;
		} else
			return 0;
	}

	/** Retorna o ira mínimo de um curso na seleção de fiscais.
	 * @param processoSeletivo
	 * @param curso
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public double findIraMinSelecao(
			ProcessoSeletivoVestibular processoSeletivo, Curso curso)
			throws HibernateException, DAOException {
		String sql;
		sql = "select min(discente_graduacao.ira)"
				+ " from vestibular.fiscal"
				+ " inner join graduacao.discente_graduacao on (id_discente = id_discente_graduacao)"
				+ " inner join discente d using (id_discente)"
				+ " inner join curso using (id_curso)"
				+ " where id_processo_seletivo = :idProcessoSeletivo"
				+ " and (curso.id_curso = :idCurso)"
				+ " and fiscal.recadastro = falseValue()"
				+ " and inclusao_manual = falseValue()"
				// Desconsiderar os IRAs dos residentes
				+ " and not exists "
						+ " (select id_discente "
						+ " from sae.bolsa_auxilio "
						+ " where id_discente = d.id_discente "
						+ " and id_tipo_bolsa_auxilio in " + gerarStringIn( TipoBolsaAuxilio.getTiposResidencia() )
						+ " and id_situacao_bolsa = " + SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA
						+ ")";
		
		Query q = getSession().createSQLQuery(sql);
		q.setInteger("idProcessoSeletivo", processoSeletivo.getId());
		q.setInteger("idCurso", curso.getId());
		BigDecimal tmp = (BigDecimal) q.uniqueResult();
		if (tmp == null)
			return -1;
		else
			return tmp.doubleValue();
	}

	/** Retorna a média geral mínima de um curso de pós na seleção de fiscais. 
	 * @param processoSeletivo
	 * @param curso
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public long findMediaGeralMinSelecao(
			ProcessoSeletivoVestibular processoSeletivo, Curso curso)
			throws HibernateException, DAOException {
		String hql;
		hql = "select min(discente_stricto.mediageral)"
				+ " from vestibular.fiscal"
				+ " inner join stricto_sensu.discente_stricto using (id_discente)"
				+ " inner join discente using (id_discente)"
				+ " inner join curso using (id_curso)"
				+ " where fiscal.id_processo_seletivo = :idProcessoSeletivo"
				+ " and curso.id_curso = :idCurso";
		Query q = getSession().createSQLQuery(hql);
		q.setInteger("idProcessoSeletivo", processoSeletivo.getId());
		q.setInteger("idCurso", curso.getId());
		return ((BigDecimal) q.uniqueResult()).longValue();
	}

	/** Retorna o total de fiscais de um local de aplicação de prova.
	 * @param idProcessoSeletivo
	 * @param idLocalAplicacaoDestino
	 * @param reserva
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public long findTotalFiscaisLocalAplicacao(int idProcessoSeletivo,
			int idLocalAplicacaoDestino, boolean reserva)
			throws HibernateException, DAOException {
		String hql;
		hql = "select count(*)" + " from Fiscal fiscal"
				+ " where id_processo_seletivo = :idProcessoSeletivo"
				+ " and id_local_aplicacao_prova = :idLocalAplicacaoProva"
				+ " and reserva = :reserva";
		Query q = getSession().createQuery(hql);
		q.setInteger("idProcessoSeletivo", idProcessoSeletivo);
		q.setInteger("idLocalAplicacaoProva", idLocalAplicacaoDestino);
		q.setBoolean("reserva", reserva);
		return (Long) q.uniqueResult();
	}

	/** Retorna a justificativa dada pelo fiscal por sua ausência. 
	 * @param fiscal
	 * @return null, caso não tenha justificativa cadastrada.
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public JustificativaAusencia findJustificativaByFiscal(Fiscal fiscal) throws HibernateException, DAOException {
		if (fiscal == null) return null;
		Criteria c = getSession().createCriteria(JustificativaAusencia.class)
		.createCriteria("fiscal").add(Restrictions.eq("id", fiscal.getId()));
		return (JustificativaAusencia) c.uniqueResult();
	}

	/** Retorna uma coleção de justificativas de ausência de fiscais dadas em um determinado processo seletivo.
	 * @param idProcessoSeletivo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<JustificativaAusencia> findJustificativaByProcessoSeletivo(int idProcessoSeletivo) throws HibernateException, DAOException {
		Criteria c = getSession().createCriteria(JustificativaAusencia.class)
		.createCriteria("fiscal");
		c.createCriteria("pessoa").addOrder(Order.asc("nome"));
		c.createCriteria("processoSeletivoVestibular")
		.add(Restrictions.eq("id", idProcessoSeletivo));
		@SuppressWarnings("unchecked")
		Collection<JustificativaAusencia> lista = c.list();
		return lista;
	}
	
	/** Retorna uma coleção de fiscais de acordo com os parâmetros passados.
	 * @param idProcessoSeletivo <b>obrigatório</b> 
	 * @param idLocalAplicacao caso seja igual a zero, não será considerado na busca.
	 * @param nome caso seja nulo ou vazio, não será considerado na busca
	 * @param matricula caso seja nulo ou vazio, não será considerado na busca
	 * @param siape caso seja nulo ou vazio, não será considerado na busca
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<Fiscal> findByNomeMatricula(int idProcessoSeletivo, int idLocalAplicacao, String nome, Long matricula, Integer siape) throws DAOException, LimiteResultadosException {
		Integer contagem = 0;
		// no caso de não especificar nome do fiscal ou o local de aplicação, a consulta pode ser demorada
		if (matricula == null && siape == null && nome == null && idLocalAplicacao == 0)
			contagem = findQtdByProcessoSeletivoLocal(idProcessoSeletivo, idLocalAplicacao);
		if (contagem > 256)
			throw new LimiteResultadosException();
		Criteria c = getSession().createCriteria(Fiscal.class);
		if (idProcessoSeletivo != 0)
			c.createCriteria("processoSeletivoVestibular").add(Restrictions.eq("id", idProcessoSeletivo));
		c.setFetchMode("discente", FetchMode.JOIN);
		c.setFetchMode("servidor", FetchMode.JOIN);
		if (idLocalAplicacao != 0)
			c.createCriteria("localAplicacaoProva").add(Restrictions.eq("id", idLocalAplicacao));
		if (nome != null && nome.length() > 0)
			c.createCriteria("pessoa").add(Restrictions.ilike("nomeAscii", nome+"%"));
		if (matricula != null && matricula > 0) {
			c.createCriteria("discente").add(Restrictions.eq("matricula", matricula));
		}
		if (siape != null && siape > 0) {
			c.createCriteria("servidor").add(Restrictions.eq("siape", siape));
		}
		@SuppressWarnings("unchecked")
		Collection<Fiscal> lista = c.list();
		return lista;
	}
	
	/** Retorna a quantidade de fiscais em um processo seletivo e local de aplicação de prova
	 * @param idProcessoSeletivo caso 0, não limita os fiscais ao processo seletivo
	 * @param idLocalAplicacao caso 0, não limita os fiscais ao local de aplicação de prova.
	 * @return
	 * @throws DAOException
	 */
	public Integer findQtdByProcessoSeletivoLocal(int idProcessoSeletivo, int idLocalAplicacao) throws DAOException {
		Criteria c = getSession().createCriteria(Fiscal.class);
		if (idProcessoSeletivo != 0)
			c.createCriteria("processoSeletivoVestibular").add(Restrictions.eq("id", idProcessoSeletivo));
		c.setFetchMode("discente", FetchMode.JOIN);
		c.setFetchMode("servidor", FetchMode.JOIN);
		if (idLocalAplicacao != 0)
			c.createCriteria("localAplicacaoProva").add(Restrictions.eq("id", idLocalAplicacao));
		return (Integer) c.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	/** Retorna uma coleção de fiscais ausentes na reunião ou na aplicação, para um processo seletivo especificado.
	 * @param idProcessoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public Collection<Fiscal> findAllAusentesByProcessoSeletivo(int idProcessoSeletivo) throws DAOException {
		Criteria c = getSession().createCriteria(Fiscal.class);
		c.setFetchMode("discente", FetchMode.JOIN);
		c.setFetchMode("servidor", FetchMode.JOIN);
		c.createCriteria("processoSeletivoVestibular").add(Restrictions.eq("id", idProcessoSeletivo));
		c.add(Restrictions.or(Restrictions.eq("presenteReuniao", false), Restrictions.eq("presenteAplicacao", false)));
		c.createCriteria("pessoa").addOrder(Order.asc("nomeAscii"));
		@SuppressWarnings("unchecked")
		Collection<Fiscal> lista = c.list();
		return lista;
	}
	
	/**
	 * Retorna uma lista de fiscais que não foram alocados para trabalhar em um
	 * local de aplicação de prova.
	 * 
	 * @param idProcessoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public Collection<Fiscal> findNaoAlocadosByProcessoSeletivo(int idProcessoSeletivo) throws DAOException {
		Collection<Fiscal> lista = new ArrayList<Fiscal>();
		// discentes
		Criteria c = getSession().createCriteria(Fiscal.class);
		c.add(Restrictions.isNull("localAplicacaoProva"));
		c.createCriteria("processoSeletivoVestibular").add(Restrictions.eq("id", idProcessoSeletivo));
		c.setFetchMode("discente", FetchMode.JOIN).createCriteria("discente");
		c.setFetchMode("pessoa", FetchMode.JOIN).createCriteria("pessoa").addOrder(Order.asc("nomeAscii"));
		@SuppressWarnings("unchecked")
		Collection<Fiscal> listaDiscente = c.list();
		// servidores
		c = getSession().createCriteria(Fiscal.class);
		c.add(Restrictions.isNull("localAplicacaoProva"));
		c.createCriteria("processoSeletivoVestibular").add(Restrictions.eq("id", idProcessoSeletivo));
		c.setFetchMode("servidor", FetchMode.JOIN).createCriteria("servidor");
		c.setFetchMode("pessoa", FetchMode.JOIN).createCriteria("pessoa").addOrder(Order.asc("nomeAscii"));
		@SuppressWarnings("unchecked")
		Collection<Fiscal> listaServidor = c.list();
		lista.addAll(listaServidor);
		lista.addAll(listaDiscente);
		return lista;
	}
	
	/** Retorna uma lista contendo os dados de contato de fiscais reservas.
	 * @param idProcessoSeletivo
	 * @param idLocalAplicaoProva lista os fiscais associados ao local de prova em que foi alocado.
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findContatosFiscais(int idProcessoSeletivo, int idLocalAplicaoProva, Boolean reserva) throws DAOException {
		// consulta para fiscais alocados no local de aplicação de prova
		StringBuilder sqlAlocados = new StringBuilder("select f.id_fiscal," +
			" f.reserva as reserva," +
			" d.matricula," +
			" p.nome," +
			" p.telefone_fixo," +
			" p.telefone_celular," +
			" c.nome as curso," +
			" case when me.id_modalidade_educacao = "+ModalidadeEducacao.A_DISTANCIA+ " then 'EAD' else '' end as modalidade_educacao," +
			" ga.descricao as grau_academico," +
			" t.sigla as turno," +
			" m.nome as municipio," +
			" e.bairro as bairro," +
			" 'FORAM ALOCADOS NO LOCAL DE APLICAÇÃO' as tipo_alocacao," +
			" la.nome as local_aplicacao," +
			" min(lf.ordem) + 1 as ordem_preferencial" +
			" from vestibular.fiscal f" +
			" inner join vestibular.local_aplicacao_prova la on (f.id_local_aplicacao_prova = la.id_local_aplicacao_prova)" +
			" left join vestibular.local_aplicacao_inscricao_fiscal lf on (f.id_local_aplicacao_prova = lf.id_local_aplicacao_prova)" +
			" inner join comum.pessoa p using (id_pessoa)" +
			" inner join discente d using (id_discente)" +
			" left join comum.endereco e on (p.id_endereco_contato = e.id_endereco)" +
			" inner join curso c using (id_curso)" +
			" inner join comum.modalidade_educacao me using (id_modalidade_educacao)" +
			" inner join comum.municipio m on (m.id_municipio = c.id_municipio)" +
			" left join graduacao.curriculo cur using (id_curriculo)" +
			" left join graduacao.matriz_curricular mc on (id_matriz = id_matriz_curricular)" +
			" left join graduacao.habilitacao h using (id_habilitacao)" +
			" left join ensino.grau_academico ga on (mc.id_grau_academico = ga.id_grau_academico)" +
			" left join ensino.turno t on (t.id_turno = mc.id_turno)" +
			" where f.id_processo_seletivo = "+ idProcessoSeletivo +
			(reserva != null ? (reserva ? " and reserva = trueValue()" : " and reserva = falseValue()") : "") +
			" and la.id_local_aplicacao_prova = " + idLocalAplicaoProva +
			" and f.presente_reuniao = trueValue()" + 
			" group by f.id_fiscal, f.reserva, d.matricula, p.nome, p.telefone_fixo, p.telefone_celular, c.nome, me.id_modalidade_educacao," +
			" ga.descricao, t.sigla ,  m.nome, e.bairro, tipo_alocacao, la.nome");
		// consulta para fiscais alocados que optaram pelo local de aplicação de prova durante a inscrição
		StringBuilder sqlPreferencia = new StringBuilder("select distinct f.id_fiscal," +
				" f.reserva," +
				" d.matricula," +
				" p.nome," +
				" p.telefone_fixo," +
				" p.telefone_celular," +
				" c.nome as curso," +
				" case when me.id_modalidade_educacao = "+ModalidadeEducacao.A_DISTANCIA+ " then 'EAD' else '' end as modalidade_educacao," +
				" ga.descricao as grau_academico," +
				" t.sigla as turno," +
				" m.nome as municipio," +
				" e.bairro as bairro," +
				" 'OPTARAM PELO LOCAL DE APLICAÇÃO' as tipo_alocacao," +
				" lp.nome as local_aplicacao," +
				" lf.ordem + 1 as ordem_preferencial" +
				" from vestibular.fiscal f" +
				" inner join vestibular.local_aplicacao_inscricao_fiscal lf using (id_inscricao_fiscal)" +
				" inner join vestibular.local_aplicacao_prova lp on (lf.id_local_aplicacao_prova = lp.id_local_aplicacao_prova)" +
				" inner join comum.pessoa p using (id_pessoa)" +
				" inner join discente d using (id_discente)" +
				" left join comum.endereco e on (p.id_endereco_contato = e.id_endereco)" +
				" inner join curso c using (id_curso)" +
				" inner join comum.modalidade_educacao me using (id_modalidade_educacao)" +
				" inner join comum.municipio m on (m.id_municipio = c.id_municipio)" +
				" left join graduacao.curriculo cur using (id_curriculo)" +
				" left join graduacao.matriz_curricular mc on (id_matriz = id_matriz_curricular)" +
				" left join graduacao.habilitacao h using (id_habilitacao)" +
				" left join ensino.grau_academico ga on (mc.id_grau_academico = ga.id_grau_academico)" +
				" left join ensino.turno t on (t.id_turno = mc.id_turno)" +
				" where f.id_processo_seletivo = "+ idProcessoSeletivo +
				(reserva != null ? (reserva ? " and reserva = trueValue()" : " and reserva = falseValue()") : "") +
				" and lp.id_local_aplicacao_prova = " + idLocalAplicaoProva +
				" and f.id_local_aplicacao_prova != "  + idLocalAplicaoProva);
		sqlAlocados.append(" union ");
		sqlAlocados.append(sqlPreferencia);
		sqlAlocados.append(" order by reserva, tipo_alocacao, local_aplicacao, ordem_preferencial, nome, modalidade_educacao");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> lista = getJdbcTemplate().queryForList(sqlAlocados.toString());
		return lista;
	}
	
	/** Retorna dados para o relatório de IRA mínimo e máximo, por curso, dos fiscais selecionados em um determinado processo seletivo.
	 * @param idProcessoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public List<ResumoProcessamentoSelecao> findIraMinimoMaximoFiscaisSelecionados(int idProcessoSeletivo) throws DAOException {
		Criteria c = getSession().createCriteria(ResumoProcessamentoSelecao.class);
		c.createCriteria("processoSeletivoVestibular").add(Restrictions.eq("id", idProcessoSeletivo));
		c.addOrder(Order.asc("grupoSelecao")).addOrder(Order.asc("subgrupoSelecao"));
		@SuppressWarnings("unchecked")
		List<ResumoProcessamentoSelecao> lista = c.list();
		return lista;
	}
	
	/**
	 * Gera os dados necessários para a geração da frenquência dos fiscais.
	 * @param id
	 * @return
	 * @throws ArqException
	 */
	public String exportaRelatorioFrequenciaFiscais(int id) throws ArqException {
		Connection con = null;
		StringBuilder dados = new StringBuilder();
		try {
			con = Database.getInstance().getSigaaConnection();		
		
			// dados do candidato
			String sqlCandidato = "select vestibular.local_aplicacao_prova.nome as local," +
					" pessoa.nome as nome, pessoa.cpf_cnpj as cpf, discente.matricula as \"Matrícula/Siape\"," +
					" fiscal.frequencia as \"dias trabalhados\", 'Discente' as \"categoria\" from vestibular.processo_seletivo " +
					" inner join vestibular.fiscal using (id_processo_seletivo)" +
					" inner join vestibular.local_aplicacao_prova using (id_local_aplicacao_prova)" +
					" inner join comum.pessoa using (id_pessoa) inner join discente using (id_discente)" +
					" where fiscal.id_servidor is null and  fiscal.id_processo_seletivo = " + id + " and reserva = false" +
					" and presente_reuniao = true " +
					" union" +
					" select vestibular.local_aplicacao_prova.nome as local, pessoa.nome as nome, pessoa.cpf_cnpj as cpf, " +
					" rh.servidor.siape as matricula, fiscal.frequencia, 'Servidor' as categoria" +
					" from vestibular.processo_seletivo inner join vestibular.fiscal using (id_processo_seletivo) " +
					" inner join vestibular.local_aplicacao_prova using (id_local_aplicacao_prova)" +
					" inner join comum.pessoa using (id_pessoa) " +
					" inner join rh.servidor using (id_servidor)" +
					" where fiscal.id_discente is null and fiscal.id_processo_seletivo = " + id + 
					" and reserva = false and presente_reuniao = true order by local, nome;"; 
				
			PreparedStatement st = con.prepareStatement(sqlCandidato);
			ResultSet rs = st.executeQuery();
			if ( !rs.next() )
				return null;
			dados.append("\nFREQUÊNCIA DOS FISCAIS PARA SIMPLES CONFERÊNCIA\n");
			UFRNUtils.toAsciiUTF8(dados.toString());
			dados.append(UFRNUtils.resultSetToCSV(rs));
			return dados.toString();			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			Database.getInstance().close(con);
		}
	}
	
}