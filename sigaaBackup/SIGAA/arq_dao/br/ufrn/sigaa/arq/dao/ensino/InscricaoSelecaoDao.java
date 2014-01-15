/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 14/12/2007
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino;


import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ensino.dominio.EditalProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;
import br.ufrn.sigaa.ensino.dominio.InscricaoSelecao;
import br.ufrn.sigaa.ensino.dominio.PessoaInscricao;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.StatusInscricaoSelecao;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.negocio.InscricaoSelecaoValidator;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Identidade;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO responsável pela consulta de inscrições em processos
 * seletivos do programa de pós-graduação, técnico, lato e transferência voluntária.
 *
 * @author Ricardo Wendell
 *
 */
public class InscricaoSelecaoDao extends GenericSigaaDAO {


	/**
	 * Retorna o status de uma inscrição de um processo seletivo
	 *
	 * @param cpf
	 * @param processoSeletivo
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public InscricaoSelecao findByCpfAndProcessoEdital(Long cpf, String passaporte, ProcessoSeletivo processoSeletivo,
			EditalProcessoSeletivo edital) throws DAOException {
	
		StringBuilder hql = new StringBuilder();
		
		hql.append(" FROM InscricaoSelecao i WHERE 1=1 ");

		if( !isEmpty(cpf) )
			hql.append(" AND i.pessoaInscricao.cpf = :cpf ");

		if( !isEmpty(passaporte) )
			hql.append(" AND i.pessoaInscricao.passaporte = :passaporte ");
		 
		//Se for processo seletivo PÓS, LATUS, TÉCNICO
		if(!isEmpty(processoSeletivo) && isEmpty(edital))
			hql.append(" AND i.processoSeletivo.id = :idProcesso ");
		else if(!isEmpty(edital))
			hql.append(" AND i.processoSeletivo.editalProcessoSeletivo.id = :idEdital ");
		
		hql.append(" AND i.status <> " + StatusInscricaoSelecao.CANCELADA);

		Query q = getSession().createQuery(hql.toString());
		
		if( !isEmpty(cpf) )
			q.setLong("cpf", cpf);
		if( !isEmpty(passaporte) )
			q.setString("passaporte", passaporte);
		
		//Se for processo seletivo PÓS, LATUS, TÉCNICO
		if(!isEmpty(processoSeletivo) && isEmpty(edital))
			q.setInteger("idProcesso", processoSeletivo.getId());
		else if(!isEmpty(edital))
			q.setInteger("idEdital", edital.getId());

		return (InscricaoSelecao) q.setMaxResults(1).uniqueResult();
	}
	

	/**
	 * Busca as inscrições de uma determinada pessoa, pelo seu CPF
	 * 
	 * @param cpf
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<InscricaoSelecao> findByCpf(Long cpf,String passaporte) throws DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM InscricaoSelecao i WHERE 1 = 1 ");
		if( !isEmpty(cpf) )
			hql.append(" AND i.pessoaInscricao.cpf = :cpf ");
		if( !isEmpty(passaporte) )
			hql.append(" AND i.pessoaInscricao.passaporte = :passaporte ");
		hql.append(" AND i.status <> " + StatusInscricaoSelecao.CANCELADA);
		hql.append(" AND i.processoSeletivo.ativo = trueValue() ");
		hql.append(" ORDER BY i.dataInscricao DESC");
		
		Query q = getSession().createQuery(hql.toString());
		if( !isEmpty(cpf) )
			q.setLong("cpf", cpf);
		if( !isEmpty(passaporte) )
			q.setString("passaporte", passaporte);

		Collection<InscricaoSelecao> lista =  q.list();
		
		if( !isEmpty(lista) )
			for (InscricaoSelecao i : lista) 
				i.getPessoaInscricao().setEstrangeiro(
							!isEmpty(i.getPessoaInscricao().getPassaporte()) && !i.getPessoaInscricao().getPais().isBrasil()
							);
		
		return q.list();
	}

	/**
	 * Utilizado somente na transferência voluntária
	 * Busca as inscrições por período, data, agendamento e status da inscrição
	 * 
	 * @return
	 * @throws DAOException
	 */
	
	@SuppressWarnings("unchecked")
	public Collection<InscricaoSelecao> findInscritosByPeriodoAgendamento
	(EditalProcessoSeletivo edital,Date agenda, Integer status, boolean orderAgenda) throws DAOException {
		
					StringBuilder sql =  new  StringBuilder(" SELECT DISTINCT pii.nome as nome, pii.data_nascimento as data, pii.cpf as cpf, ");
					sql.append(" pii.numero_identidade as identidade, pii.orgao_expedicao_identidade as orgao,ufi.sigla, mu.nome as cidade, ");
					sql.append(" c.nome as curso, g.descricao as modalidade, h.nome as habilitacao,t.sigla as turno, ");
					sql.append(" i.numero_inscricao as numeroInscricao, ag.data_agendada,i.id FROM ensino.inscricao_selecao i  ");
					sql.append(" INNER JOIN ensino.processo_seletivo ps ON i.id_processo_seletivo = ps.id_processo_seletivo  ");
					sql.append(" INNER JOIN ensino.pessoa_inscricao pii ON i.id_pessoa_inscricao = pii.id  ");
					sql.append(" INNER JOIN comum.unidade_federativa ufi ON ufi.id_unidade_federativa = pii.id_uf_identidade	 ");
					sql.append(" LEFT JOIN ensino.agenda_processo_seletivo ag  ON i.id_agenda_processo_seletivo = ag.id_agenda_processo_seletivo  ");
					sql.append(" INNER JOIN graduacao.matriz_curricular m ON ps.id_matriz_curricular = m.id_matriz_curricular  ");
					sql.append(" LEFT JOIN graduacao.habilitacao h ON h.id_habilitacao = m.id_habilitacao  ");
					sql.append(" LEFT JOIN ensino.grau_academico g ON g.id_grau_academico = m.id_grau_academico  ");
					sql.append(" LEFT JOIN ensino.turno t ON t.id_turno = m.id_turno  ");
					sql.append(" INNER JOIN curso c ON m.id_curso = c.id_curso  ");
					sql.append(" INNER JOIN comum.municipio mu ON mu.id_municipio = c.id_municipio  ");
					sql.append(" WHERE ps.id_edital_processo_seletivo = ?  ");
		
					ArrayList<Object> parametros = new ArrayList<Object>();	
					parametros.add(edital.getId());

					
					if(!isEmpty(status)){
						sql.append(" 	AND i.status = ? ");
						 parametros.add(status);
					}	 
					if(!isEmpty(agenda)){
						sql.append(" 	AND ag.data_agendada = ? ");
						parametros.add(agenda);
					}
					
					Object param[] = new Object[parametros.size()];
					param = parametros.toArray(param);
					
					sql.append(" ORDER BY ");
						
					if(orderAgenda)
						sql.append(" ag.data_agendada, ");
					sql.append(" mu.nome, c.nome, pii.nome ");
					
		return getJdbcTemplate().query(sql.toString() , param , new RowMapper(){
 			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				InscricaoSelecao i = new InscricaoSelecao();
				i.setId(rs.getInt("id"));
				i.setPessoaInscricao(new PessoaInscricao());
				i.getPessoaInscricao().setNome(rs.getString("nome"));
				i.getPessoaInscricao().setDataNascimento(rs.getDate("data"));
				i.getPessoaInscricao().setCpf(rs.getLong("cpf"));
				i.getPessoaInscricao().setIdentidade(new Identidade());
				i.getPessoaInscricao().getIdentidade().setNumero(rs.getString("identidade"));
				i.getPessoaInscricao().getIdentidade().setOrgaoExpedicao(rs.getString("orgao"));
				i.getPessoaInscricao().getIdentidade().setUnidadeFederativa(new UnidadeFederativa());
				i.getPessoaInscricao().getIdentidade().getUnidadeFederativa().setSigla(rs.getString("sigla"));
				i.setProcessoSeletivo(new ProcessoSeletivo());
				i.getProcessoSeletivo().setMatrizCurricular(new MatrizCurricular());
				i.getProcessoSeletivo().getMatrizCurricular().setCurso(new Curso());
				i.getProcessoSeletivo().getMatrizCurricular().getCurso().setNome(rs.getString("curso"));
				i.getProcessoSeletivo().getMatrizCurricular().setHabilitacao(new Habilitacao());
				i.getProcessoSeletivo().getMatrizCurricular().getHabilitacao().setNome(rs.getString("habilitacao"));
				i.getProcessoSeletivo().getMatrizCurricular().setTurno(new Turno());
				i.getProcessoSeletivo().getMatrizCurricular().getTurno().setSigla(rs.getString("turno"));
				i.getProcessoSeletivo().getMatrizCurricular().setGrauAcademico(new GrauAcademico());
				i.getProcessoSeletivo().getMatrizCurricular().getGrauAcademico().setDescricao(rs.getString("modalidade"));

				i.getProcessoSeletivo().getMatrizCurricular().getCurso().setMunicipio(new Municipio());
				i.getProcessoSeletivo().getMatrizCurricular().getCurso().getMunicipio().setNome(rs.getString("cidade"));
				i.setDataAgendada(rs.getDate("data_agendada"));
				

			
				if(rs.getDate("data_agendada") != null)
					i.setDataAgendada(rs.getDate("data_agendada"));
				if(rs.getLong("numeroInscricao") > 0)
					i.setNumeroInscricao(rs.getLong("numeroInscricao"));
			
				return i;
			}
			
		});
	
	}
	
	/**
	 * Utilizado somente na transferência voluntária
	 * Busca as inscrições por período, data, agendamento e status da inscrição
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findQtdInscritosDiaByEdital(EditalProcessoSeletivo edital) throws DAOException {
		
		String sql = " 	SELECT DATE(i.data_inscricao) as data, COUNT(i.id) as qtd, SUM(1) as numero FROM ensino.inscricao_selecao i " +
					"	INNER JOIN ensino.processo_seletivo ps ON i.id_processo_seletivo = ps.id_processo_seletivo " +
					"   WHERE ps.id_edital_processo_seletivo = ? GROUP BY  data ";
		
					ArrayList<Object> parametros = new ArrayList<Object>();	
					parametros.add(edital.getId());
					
					Object param[] = new Object[parametros.size()];
					param = parametros.toArray(param);
					
			 
		return getJdbcTemplate().queryForList(sql, param);
	
	}
	
	/**
	 * Utilizado somente na transferência voluntária
	 * Busca a quantidade de inscritos por curso para um edital do processo seletivo.
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findQtdInscritosCursoByEdital(EditalProcessoSeletivo edital) throws DAOException {
		
		StringBuilder 	sql = new StringBuilder(" SELECT (c.nome || ' - ' || g.descricao || ' - ' || t.sigla) as curso, mu.nome as municipio, h.nome as habilitacao");
						sql.append(" , COUNT(i.id) as qtd FROM ensino.inscricao_selecao i ");
						sql.append(" INNER JOIN ensino.processo_seletivo ps ON i.id_processo_seletivo = ps.id_processo_seletivo ");
						sql.append(" INNER JOIN graduacao.matriz_curricular m ON m.id_matriz_curricular = ps.id_matriz_curricular ");
						sql.append(" INNER JOIN curso c ON c.id_curso = m.id_curso ");
						sql.append(" INNER JOIN comum.municipio mu ON mu.id_municipio = c.id_municipio  ");
						sql.append(" LEFT JOIN graduacao.habilitacao h ON h.id_habilitacao = m.id_habilitacao  ");
						sql.append(" LEFT JOIN ensino.grau_academico g ON g.id_grau_academico = m.id_grau_academico  ");
						sql.append(" LEFT JOIN ensino.turno t ON t.id_turno = m.id_turno  ");
						sql.append(" WHERE ps.id_edital_processo_seletivo = ? ");
						sql.append(" GROUP BY curso, habilitacao, municipio ORDER BY qtd DESC, municipio, curso, habilitacao ");
		
					ArrayList<Object> parametros = new ArrayList<Object>();	
					parametros.add(edital.getId());
					
					Object param[] = new Object[parametros.size()];
					param = parametros.toArray(param);
					
			 
		return getJdbcTemplate().queryForList(sql.toString(), param);
	
	}
	
	
	/**
	 * Retorna a quantidade de inscritos de um processo seletivo ativos e que estejam aberto.
	 * 
	 * @param adesao
	 * @return
	 * @throws DAOException
	 */
	public int findQtdInscritosByQuestionario(Integer idQuestionario) throws DAOException {
		
		Date dataAtual = new Date(System.currentTimeMillis());
		return getJdbcTemplate().queryForInt(
				" SELECT COUNT(i.id) FROM ensino.inscricao_selecao i " +
				" INNER JOIN ensino.processo_seletivo ps USING(id_processo_seletivo) " +
				" INNER JOIN ensino.edital_processo_seletivo e USING(id_edital_processo_seletivo)  " +
				" WHERE e.inicio_inscricoes <= '" + dataAtual + "' AND e.fim_inscricoes >= '" + dataAtual + "'" +
				" AND ps.ativo = trueValue() AND ps.id_questionario = " + idQuestionario);

	}
	
	/**
	 * Busca todos os inscritos em um processo seletivo.
	 *
	 * @param processo
	 * @return
	 * @throws DAOException
	 */
	public Collection<InscricaoSelecao> findInscritos(ProcessoSeletivo processo, 
			boolean withProjection) throws DAOException{
		
		List<Integer> listaStatus = new ArrayList<Integer>();
		listaStatus.add(StatusInscricaoSelecao.DEFERIDA);
		listaStatus.add(StatusInscricaoSelecao.APROVADO_SELECAO);
		listaStatus.add(StatusInscricaoSelecao.SUBMETIDA);
		listaStatus.add(StatusInscricaoSelecao.SUPLENTE);
		
		return findInscritosByProcessoStatus(processo, null, withProjection);
	}
	
	/**
	 * Busca todos os inscritos selecionando oo processo seletivo e
	 * os status serem buscados
	 *
	 * @param processo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<InscricaoSelecao> findInscritosByProcessoStatus(ProcessoSeletivo processo, 
			List<Integer> statusInscricao, boolean withProjection) throws DAOException {
		StringBuilder hql = new StringBuilder();
		Collection<InscricaoSelecao> inscritos = new ArrayList<InscricaoSelecao>();

		try {
			StringBuilder projecao = new StringBuilder();
			if (withProjection) {
				projecao.append( " id,pessoaInscricao.id, pessoaInscricao.nome,pessoaInscricao.id, pessoaInscricao.cpf, pessoaInscricao.passaporte, " );
				projecao.append( " numeroInscricao, dataInscricao, status, pessoaInscricao.email, processoSeletivo.id, idGRU " );
				if(processo.getMatrizCurricular() != null)
					projecao.append( " , processoSeletivo.matrizCurricular.id " );
				hql.append( " SELECT " );
				hql.append( projecao );
			}

			hql.append( " FROM InscricaoSelecao i " );
			hql.append( " WHERE i.processoSeletivo = :idProcesso " );
			
			if( !isEmpty(statusInscricao) )
			hql.append( " AND i.status IN " + UFRNUtils.gerarStringIn(statusInscricao) );
			
			hql.append( " ORDER BY i.pessoaInscricao.nome " );
			Query q = getSession().createQuery( hql.toString() );
			q.setInteger("idProcesso", processo.getId());

			if (withProjection) {
				List<Object[]> lista = q.list();
				inscritos = HibernateUtils.parseTo(lista, projecao.toString(), InscricaoSelecao.class);
			} else {
				inscritos = q.list();
			}
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

		return inscritos;
	}

	
	/** Retorna uma coleção de objetos de inscrições a partir de uma lista de números de referência de GRU. 
	 * @param listaNumInscricao
	 * @return
	 * @throws DAOException
	 */
	public Collection<InscricaoSelecao> findByListaNumReferenciaGRU(Collection<Long> listaNumReferencia) throws DAOException{
		if (ValidatorUtil.isEmpty(listaNumReferencia)) return null;
		Criteria c = getSession().createCriteria(InscricaoSelecao.class);
		c.add(Restrictions.in("numeroReferenciaGRU", listaNumReferencia));
		@SuppressWarnings("unchecked")
		Collection<InscricaoSelecao> lista = c.list();
		return lista;
	}

	/**
	 * Retorna todas inscrições pertencentes a um determinado edital de processo seletivo 
	 * e que possua um dos níveis passados como parâmetro.
	 * @param idEdital
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	public Collection<InscricaoSelecao> findByEditalStatus(int idEdital, Integer... status) throws DAOException {
		Criteria c = getSession().createCriteria(InscricaoSelecao.class);
		c.createCriteria("processoSeletivo").createCriteria("editalProcessoSeletivo").add(Restrictions.eq("id", idEdital));
		c.add(Restrictions.in("status", status));
		@SuppressWarnings("unchecked")
		Collection<InscricaoSelecao> lista = c.list();
		return lista;
	}
	
	/**
	 * Retorna todos o quantitativo os inscritos por processo seletivo, informando o curso desejado.
	 */
	public HashMap<String, Collection<Integer>>	findByQuantInscricoes(int idCurso) throws HibernateException, DAOException{
			HashMap<String, Collection<Integer>> result = new HashMap<String, Collection<Integer>>();
		
			String sql = "SELECT x.nome, count(submetidos) as submetidos, count(aprovados) as aprovados, count(cancelada) as cancelada, count(deferida) as deferida, " +
					     "count(indeferida) as indeferida, count(suplente) as suplente " +
						 "FROM ( " +
								"SELECT eps.nome," +
								"CASE WHEN i.status = " + StatusInscricaoSelecao.SUBMETIDA + " THEN 1 END AS submetidos," +
								"CASE WHEN i.status = " + StatusInscricaoSelecao.APROVADO_SELECAO + " THEN 1 END AS aprovados, " +
								"CASE WHEN i.status = " + StatusInscricaoSelecao.CANCELADA + " THEN 1 END AS cancelada, " +
								"CASE WHEN i.status = " + StatusInscricaoSelecao.DEFERIDA + " THEN 1 END AS deferida, " +
								"CASE WHEN i.status = " + StatusInscricaoSelecao.INDEFERIDA + " THEN 1 END AS indeferida, " +
								"CASE WHEN i.status = " + StatusInscricaoSelecao.ELIMINADO + " THEN 1 END AS eliminado, " +
								"CASE WHEN i.status = " + StatusInscricaoSelecao.SUPLENTE + " THEN 1 END AS suplente " +
								"FROM ensino.inscricao_selecao i " +
								"JOIN ensino.processo_seletivo ps using (id_processo_seletivo) " +
								"JOIN ensino.edital_processo_seletivo eps using (id_edital_processo_seletivo) " +
							"WHERE id_curso =:idCurso " +
						 ") x GROUP BY x.nome";
		
			Query q = getSession().createSQLQuery(sql);	
			q.setInteger("idCurso", idCurso);
			
			@SuppressWarnings("unchecked")
			Collection<Object[]> bulk = q.list();
			
			for (Object[] objects : bulk) {
				
				Collection<Integer> quant = new ArrayList<Integer>();
				quant.add(((BigInteger) objects[1]).intValue());
				quant.add(((BigInteger) objects[2]).intValue());
				quant.add(((BigInteger) objects[3]).intValue());
				quant.add(((BigInteger) objects[4]).intValue());
				quant.add(((BigInteger) objects[5]).intValue());
				quant.add(((BigInteger) objects[6]).intValue());
				
				result.put((String) objects[0], quant);
			}
			
		return result;
	}
	
	/**
	 * Retorna a quantidade de inscritos
	 * @param cpf
	 * @param passaporte
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Integer getQtdInscritos(ProcessoSeletivo processo)
			throws DAOException{
		
		String sql = "SELECT CAST(COUNT(DISTINCT id) AS INTEGER) FROM ensino.inscricao_selecao "
				+ " WHERE id_processo_seletivo = " + processo.getId()  
				+ " AND status IN " + gerarStringIn( StatusInscricaoSelecao.getStatusAtivos() );
	  
	    Query q = getSession().createSQLQuery(sql);
	    Integer qtd = (Integer) q.uniqueResult();
	    return qtd;
		
	}

	/** Retorna uma coleção de inscrições que possuem GRU quitadas, verificando nos dados comuns aos sistemas.
	 * @param idProcessoSeletivo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<InscricaoSelecao> verificaInscricaoGRUPaga(int idProcessoSeletivo) throws HibernateException, DAOException {
		String projecao = "inscricao.id," +
				" inscricao.numeroInscricao," +
				" inscricao.pessoaInscricao.id," +
				" inscricao.pessoaInscricao.nome," +
				" inscricao.pessoaInscricao.cpf," +
				" inscricao.idGRU";
		String hql = "select " + projecao +
				" from InscricaoSelecao inscricao" +
				" where inscricao.processoSeletivo.id = :idProcessoSeletivo" +
				" and (inscricao.gruQuitada is null or inscricao.gruQuitada = false)";
		Query q = getSession().createQuery(hql);
		q.setInteger("idProcessoSeletivo", idProcessoSeletivo);
		@SuppressWarnings("unchecked")
		Collection<InscricaoSelecao> lista = HibernateUtils.parseTo(q.list(), projecao, InscricaoSelecao.class, "inscricao");
		if (!isEmpty(lista)) {
			Collection<Integer> idsGRUsPagas = new ArrayList<Integer>();
			for (InscricaoSelecao inscricao : lista)
				if (inscricao.getIdGRU() != null)
					idsGRUsPagas.add(inscricao.getIdGRU());
			idsGRUsPagas = GuiaRecolhimentoUniaoHelper.isGRUQuitada(idsGRUsPagas);
			if (!isEmpty(idsGRUsPagas)) {
				Iterator<InscricaoSelecao> iterator = lista.iterator();
				while (iterator.hasNext()) {
					if (!idsGRUsPagas.contains(iterator.next().getIdGRU()))
						iterator.remove();
				}
				return lista;
			}
		}
		return null;
	}

	/** Serve para verificar se o cpf informado atende a todas as necessidades para efetuar a inscrição no processo seletivo. */
	public Map<Integer, Collection<Discente>> permiteInscricaoSelecao(long cpf, int ano, int periodo) throws DAOException {
		String sql = "select " + InscricaoSelecaoValidator.INCRICAO_SUBMETIDA + " as tipo_bloqueio, p.nome, c.nome as curso "  +
				" from ensino.pessoa_inscricao p join ensino.inscricao_selecao insc on ( p.id = insc.id_pessoa_inscricao )" +
				" join ensino.processo_seletivo ps on ( insc.id_processo_seletivo = ps.id_processo_seletivo )" +
				" join ensino.edital_processo_seletivo eps on ( ps.id_edital_processo_seletivo = eps.id_edital_processo_seletivo )" +
				" join curso c on ( ps.id_curso = c.id_curso )" +
				" where insc.status in " + UFRNUtils.gerarStringIn(new int[] {StatusInscricaoSelecao.SUBMETIDA}) + 
 				" and ps.ativo = trueValue() and c.nivel = '" + NivelEnsino.FORMACAO_COMPLEMENTAR + "' and p.cpf = " + cpf +
				
				" union " +
				
				" select " + InscricaoSelecaoValidator.DISCENTE_ATIVO + " as tipo_bloqueio, p.nome, c.nome as curso" +
				" from discente d join comum.pessoa p using (id_pessoa) " +
				" left join curso c on ( d.id_curso = c.id_curso ) " +
				" where d.status in " + UFRNUtils.gerarStringIn(new int[] {StatusDiscente.ATIVO, StatusDiscente.CADASTRADO}) +
				" and d.nivel = '" + NivelEnsino.FORMACAO_COMPLEMENTAR + "' and p.cpf_cnpj = " + cpf +

				" union " +
				
				" select " + InscricaoSelecaoValidator.DISCENTE_BLOQUEADO + " as tipo_bloqueio, p.nome, c.nome as curso" + 
				" from discente d" +
				" join comum.alteracao_status_aluno asa using ( id_discente ) " +
				" join comum.pessoa p on ( d.id_pessoa = p.id_pessoa )" +
				" left join curso c on ( d.id_curso = c.id_curso ) " +
				" where d.nivel = '" + NivelEnsino.FORMACAO_COMPLEMENTAR + "' and d.status <> " + StatusDiscente.ATIVO +
				" and d.observacao ilike '%PROAE%'" +
				" and p.cpf_cnpj = " + cpf +
				" and asa.ano >= " + ano +
				" and asa.periodo > " + periodo +
		
				"group by p.nome, c.nome";
		
	    Query q = getSession().createSQLQuery(sql);
	    Collection<Object[]> bulk = q.list();
	    Map<Integer, Collection<Discente>> result = new TreeMap<Integer, Collection<Discente>>();
	    int count = 0;
	    for (Object[] objects : bulk) {
	    	count = 0;
	    	if ( (Integer) objects[count] == InscricaoSelecaoValidator.INCRICAO_SUBMETIDA ) {
	    		Collection<Discente> discentes = result.get(InscricaoSelecaoValidator.INCRICAO_SUBMETIDA);
	    		discentes = addDiscente(++count, objects, discentes);
	    		result.put(InscricaoSelecaoValidator.INCRICAO_SUBMETIDA, discentes);
			} else if ( (Integer) objects[count] == InscricaoSelecaoValidator.DISCENTE_ATIVO ) {
	    		Collection<Discente> discentes = result.get(InscricaoSelecaoValidator.DISCENTE_ATIVO);
	    		discentes = addDiscente(++count, objects, discentes);
	    		result.put(InscricaoSelecaoValidator.DISCENTE_ATIVO, discentes);
			} else if ( (Integer) objects[count] == InscricaoSelecaoValidator.DISCENTE_BLOQUEADO ) {
	    		Collection<Discente> discentes = result.get(InscricaoSelecaoValidator.DISCENTE_BLOQUEADO);
	    		discentes = addDiscente(++count, objects, discentes);
	    		result.put(InscricaoSelecaoValidator.DISCENTE_BLOQUEADO, discentes);
			} 
		}	    
	    return result;
	}

	private Collection<Discente> addDiscente(int count, Object[] objects, Collection<Discente> discentes) {
		if ( discentes == null )
			discentes = new ArrayList<Discente>();
		Discente d = new Discente();
		d.setPessoa(new Pessoa());
		d.getPessoa().setNome((String) objects[count++]);
		d.setCurso(new Curso());
		d.getCurso().setNome((String) objects[count++]);
		discentes.add(d);
		return discentes;
	}
	
}