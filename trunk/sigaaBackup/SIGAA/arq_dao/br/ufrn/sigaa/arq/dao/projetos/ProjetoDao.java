/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/02/2009
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.Avaliacao;
import br.ufrn.sigaa.projetos.dominio.DistribuicaoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.FotoProjeto;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.ModeloAvaliacao;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.QuestionarioAvaliacao;
import br.ufrn.sigaa.projetos.dominio.SolicitacaoReconsideracao;
import br.ufrn.sigaa.projetos.dominio.TipoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.TipoAvaliador;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * DAO para consultas relacionadas a projetos institucionais
 * 
 * @author wendell
 *
 */
public class ProjetoDao extends GenericSigaaDAO {

	/** Sequência que identifica projetos institucionais(pesquisa, extensão e monitoria) */
	private static final String PREFIXO_SEQUENCIA_NUMERO_INSTITUCIONAL = "projetos.seq_numero_projeto_";
	/** Limita resultados a busca por propostas de projetos associados */
	private static final Integer LIMITE_RESULTADOS = 1000;
	
	/**
	 * Busca o próximo número na sequência do ano informado
	 * 
	 * @param ano
	 * @return
	 * @throws DAOException 
	 */
	public Integer findNextNumeroInstitucional(int ano) throws DAOException {
		String nomeSequencia = PREFIXO_SEQUENCIA_NUMERO_INSTITUCIONAL + ano;
		return getNextSeq(nomeSequencia);
	}
	
	/**
	 * Retorna o id do TipoSituacaoProjeto da última situação gravada no
	 * histórico do projeto.
	 * 
	 * listaSituacoesDesconsideradas
	 * 
	 * @param idProjeto
	 * @param listaSituacoesDesconsideradas Utilizada para retornar projeto para uma situação anterior a informada na lista.
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public int findLastSituacaoProjeto(int idProjeto, Integer[] listaSituacoesDesconsideradas) throws HibernateException, DAOException {

		try {
			String sql = "SELECT h.situacaoProjeto.id FROM HistoricoSituacaoProjeto h " +
					"WHERE h.projeto.id = :idProjeto ";

			if (listaSituacoesDesconsideradas != null) {
				sql += " AND h.situacaoProjeto.id NOT IN (:listaSituacoesDesconsideradas) ";
			}
			
			sql += " ORDER BY h.data DESC";
			Query q = getSession().createQuery(sql);
			q.setInteger("idProjeto", idProjeto);

			if (listaSituacoesDesconsideradas != null) {
				q.setParameterList("listaSituacoesDesconsideradas", listaSituacoesDesconsideradas);
			}
			q.setMaxResults(1);
			return (Integer) (q.uniqueResult() == null ? 0 : q.uniqueResult());

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	
	
	/**
	 * Retorna projetos com cadastro em andamento gravados pelo
	 * usuário informado.
	 * 
	 * @param usuario usuário informado
	 * @param idTipoProjeto tipo de projeto. @see projetos.TipoProjeto
	 * @return Lista de projetos encontrados
	 * @throws DAOException quando ocorre erro na busca.
	 */
	public Collection<Projeto> findGravadosByUsuario(Usuario usuario, int idTipoProjeto) throws DAOException {

		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT projeto.id, projeto.ano, projeto.titulo, sit.id, sit.descricao "
							+ " FROM Projeto projeto "
							+ " INNER JOIN projeto.situacaoProjeto sit "
							+ " INNER JOIN projeto.registroEntrada reg  "
							+ " INNER JOIN reg.usuario usu  "
							+ " INNER JOIN usu.pessoa pe  "
							+ " WHERE pe.id = :idPessoa " +
									"AND sit.id = :idSituacao AND projeto.ativo = trueValue() " +
									"AND projeto.tipoProjeto.id = :idTipoProjeto");

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idPessoa", usuario.getPessoa().getId());			
			query.setInteger("idSituacao",	TipoSituacaoProjeto.PROJETO_BASE_CADASTRO_EM_ANDAMENTO);
			query.setInteger("idTipoProjeto", idTipoProjeto);
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = query.list();

			ArrayList<Projeto> result = new ArrayList<Projeto>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = lista.get(a);
				Projeto projeto = new Projeto();
				projeto.setId((Integer) colunas[col++]);
				projeto.setAno((Integer) colunas[col++]);
				projeto.setTitulo((String) colunas[col++]);
				TipoSituacaoProjeto sit = new TipoSituacaoProjeto((Integer) colunas[col++]);
				String desc = (String) colunas[col++];
				if (desc != null) {
					sit.setDescricao(desc);
				}
				projeto.setSituacaoProjeto(sit);
				result.add(projeto);

			}

			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}
	
	
	
	
	/**
	 * Retorna projetos que o servidor faz parte da equipe organizadora.
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public Collection<Projeto> findByServidor(Servidor servidor, int idTipoProjeto) throws DAOException {

		try {

			String hql = null;
			hql = "SELECT distinct projeto.id, projeto.ano, projeto.titulo, s.descricao, s.id  "
					+ "FROM Projeto projeto "
					+ "INNER JOIN projeto.equipe m "
					+ "INNER JOIN projeto.situacaoProjeto s "
					+ "WHERE m.servidor.id = :idservidor "
					+ "AND projeto.tipoProjeto.id = :idTipoProjeto " 
					+ "AND projeto.ativo = trueValue() "
					+ "ORDER BY projeto.ano desc";

			Query query = getSession().createQuery(hql);
			query.setInteger("idservidor", servidor.getId());
			query.setInteger("idTipoProjeto", idTipoProjeto);

			@SuppressWarnings("unchecked")
			List<Object[]> lista = query.list();

			ArrayList<Projeto> result = new ArrayList<Projeto>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = lista.get(a);

				Projeto projeto = new Projeto();
				projeto.setId((Integer) colunas[col++]);
				projeto.setAno((Integer) colunas[col++]);
				projeto.setTitulo((String) colunas[col++]);

				TipoSituacaoProjeto sit = new TipoSituacaoProjeto();
				String desc = (String) colunas[col++];
				if (desc != null) {
				    sit.setDescricao(desc);
				    sit.setId((Integer) colunas[col++]);
				}
				projeto.setSituacaoProjeto(sit);
				result.add(projeto);

			}

			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}
	
	/**
	 * Retorna projetos que o servidor faz parte com membro da equipe organizadora.
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public Collection<Projeto> findByServidor(Servidor servidor,  Integer[] idsSituacoes) throws DAOException {

		try {

			StringBuffer hql = new StringBuffer();
			hql.append(" SELECT distinct projeto.id, projeto.ano, projeto.titulo, s.descricao, s.id  "
					+ "FROM Projeto projeto "
					+ "INNER JOIN projeto.equipe m "
					+ "INNER JOIN projeto.situacaoProjeto s "
					+ "INNER JOIN projeto.coordenador coordenador " 
					+ "WHERE coordenador.servidor.id = :idServidor AND coordenador.ativo = trueValue() " 
					+ "AND projeto.tipoProjeto.id = :idTipoProjeto " 
					+ "AND projeto.ativo = trueValue() ");
					if (idsSituacoes != null) {
					    hql.append(" AND projeto.situacaoProjeto.id in (:idsSituacoes) ");
					}
					hql.append(" ORDER BY projeto.ano desc ");

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idServidor", servidor.getId());
			query.setParameterList("idsSituacoes", idsSituacoes);
			query.setInteger("idTipoProjeto", TipoProjeto.ASSOCIADO);

			@SuppressWarnings("unchecked")
			List<Object[]> lista = query.list();

			ArrayList<Projeto> result = new ArrayList<Projeto>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = lista.get(a);

				Projeto projeto = new Projeto();
				projeto.setId((Integer) colunas[col++]);
				projeto.setAno((Integer) colunas[col++]);
				projeto.setTitulo((String) colunas[col++]);

				TipoSituacaoProjeto sit = new TipoSituacaoProjeto();
				String desc = (String) colunas[col++];
				if (desc != null) {
				    sit.setDescricao(desc);
				    sit.setId((Integer) colunas[col++]);
				}
				projeto.setSituacaoProjeto(sit);
				result.add(projeto);

			}

			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}
	
	/**
	 * Verifica se o tipo de ação de extensão está cadastrada. 
	 * 
	 * @param id
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public boolean isProjetoExtensaoSubmetido(int idProjeto, int idTipoAtividade ) {
	    	JdbcTemplate jt = getJdbcTemplate();
	    	int i = 0;
	    	i = jt.queryForInt("select count(id_projeto) from extensao.atividade a where a.id_projeto = " 
	    		+ idProjeto + " and (a.data_envio is not null) and a.id_tipo_atividade_extensao = " + idTipoAtividade);
	    	return i > 0;
	}

	/**
	 * Verifica se o projeto de pesquisa informado já está cadastrado. 
	 * 
	 * @param id
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public boolean isProjetoPesquisaSubmetido(int idProjeto) {
	    	JdbcTemplate jt = getJdbcTemplate();
	    	int i = 0;
	    	i = jt.queryForInt("select count(id_projeto) from pesquisa.projeto_pesquisa p where p.id_projeto = " + idProjeto);
	    	return i > 0;
	}

	/**
	 * Verifica se o projeto de ensino está cadastrado.
	 * 
	 * @param id
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public boolean isProjetoEnsinoSubmetido(int idProjeto, int idTipoProjeto) {
	    	JdbcTemplate jt = getJdbcTemplate();
	    	int i = 0;
	    	i = jt.queryForInt("select count(id_projeto) from monitoria.projeto_monitoria m where m.id_projeto = " + idProjeto 
	    		+ " and (m.data_envio is not null) and m.id_tipo_projeto_ensino = " + idTipoProjeto);
	    	return i > 0;
	}
	
	/**
	 * É a busca geral para ações acadêmicas busca por propostas de projetos submetidos com os 
	 * parâmetros informados. 
	 */
	public List<Projeto> filter ( 
		Integer idEdital,
		Date inicio,
		Date fim,
		Integer ano,
		String titulo,
		Integer[] idsSituacoesProjeto,
		Integer idUnidadeProponente, Integer idCentro, Integer idAreaCNPq,
		Integer idServidor, 
		Boolean financiamentoInterno, Boolean financiamentoExterno,
		Boolean autoFinanciamento, Boolean solicitacaoRenovacao, 
		Boolean ensino, Boolean extensao, Boolean pesquisa) throws DAOException,LimiteResultadosException {

	    try {

		StringBuilder hqlCount = new StringBuilder();
		hqlCount.append(" SELECT  count(distinct p.id) "
			+ "FROM Projeto p "
			+ "LEFT JOIN p.edital edital "		
			+ "LEFT JOIN p.coordenador as coord "
			+ "LEFT JOIN coord.pessoa as pessoa "
		);
		hqlCount.append(" WHERE p.ativo = trueValue() AND p.tipoProjeto.id = :idTipoProjeto "); //Ações Associadas

		StringBuilder hqlConsulta = new StringBuilder();
		hqlConsulta
		.append(" SELECT distinct p.id, p.numeroInstitucional, p.dataInicio, p.dataFim, p.ano, p.financiamentoInterno, "
			+ "p.financiamentoExterno, p.autoFinanciado, p.renovacao, edital, p.titulo, p.media, p.classificacao, "
			+ "p.ensino, p.extensao, p.pesquisa, "
			+ "p.unidade.id, p.unidade.sigla, p.unidade.nome, uo.id, uo.sigla, uo.nome, " 
			+ "p.situacaoProjeto.id, p.situacaoProjeto.descricao, "
			+ "coord.id, pessoa.id, pessoa.nome, pessoa.email, coord.funcaoMembro.id, coord.ativo, coord.dataInicio, coord.dataFim, "
			+ "servCoordUnidade.id, servCoordUnidade.nome, servCoordUnidade.sigla, "
			+ "docenCoordUnidade.id, docenCoordUnidade.nome, docenCoordUnidade.sigla, "
			+ "ava.id, ava.ativo, ava.dataAvaliacao, ava.nota  "
			+ "FROM Projeto p "
			+ "LEFT JOIN p.coordenador as coord "
			+ "LEFT JOIN coord.servidor.unidade as servCoordUnidade "
			+ "LEFT JOIN coord.docenteExterno.unidade as docenCoordUnidade "
			+ "LEFT JOIN p.edital edital "
			+ "LEFT JOIN coord.pessoa as pessoa "
			+ "LEFT JOIN p.unidadeOrcamentaria as uo "
			+ "LEFT JOIN p.avaliacoes as ava "
		);

		hqlConsulta.append(" WHERE p.ativo = trueValue() AND p.tipoProjeto.id = :idTipoProjeto  "); //Ações Associadas.

		StringBuilder hqlFiltros = new StringBuilder();

		// Filtros para a busca
		if (titulo != null) {
		    hqlFiltros.append(" AND "
			    + UFRNUtils.toAsciiUpperUTF8("p.titulo") + " like "
			    + UFRNUtils.toAsciiUTF8(":titulo"));
		}

		if (idEdital != null) {
		    hqlFiltros.append(" AND edital.id = :idEdital ");
		}

		/* localiza projetos que passaram pelo período informado */
		if ((inicio != null) && (fim != null)) {
		    hqlFiltros.append(" AND " + HibernateUtils.generateDateIntersection("p.dataInicio", "p.dataFim", ":inicio", ":fim"));
		}

		// A busca pode ser feita por mais de uma Situação ao mesmo
		// tempo, exemplo: 103, 105, 110 
		if ((idsSituacoesProjeto != null) && (idsSituacoesProjeto.length > 0)) {
		    hqlFiltros.append(" AND p.situacaoProjeto.id IN ( :situacoes ) ");
		}

		if (idUnidadeProponente != null) {
		    hqlFiltros.append(" AND p.unidade.id = :idUnidadeProponente");
		}

		if (idCentro != null) {
		    // adaptação para unidades especializadas
		    hqlFiltros.append(" AND (p.unidade.gestora.id = :idCentro or p.unidade.id = :idCentro)");
		}

		if (idAreaCNPq != null) {
		    hqlFiltros.append(" AND p.areaConhecimentoCnpq.id = :idAreaCNPq");
		}

		if (idServidor != null) {
		    hqlFiltros.append(" AND coord.servidor.id = :idServidor");
		}

		if (ano != null) {
		    // Na busca serão trazidas todas as ações que estejam em execução durante ano informado.
		    hqlFiltros.append(" AND (extract(year from p.dataInicio) <= :ano and extract(year from p.dataFim) >= :ano) ");
		}

		if (financiamentoInterno != null) {
		    hqlFiltros.append(" AND p.financiamentoInterno = :financiamentoInterno");
		}

		if (financiamentoExterno != null) {
		    hqlFiltros.append(" AND p.financiamentoExterno = :financiamentoExterno");
		}

		if (autoFinanciamento != null) {
		    hqlFiltros.append(" AND p.autoFinanciado = :autoFinanciamento");
		}

		if (solicitacaoRenovacao != null) {
		    hqlFiltros.append(" AND p.renovacao = :solicitacaoRenovacao");
		}
		
		if (ensino != null){
			if(ensino){
				hqlFiltros.append(" AND p.ensino = :ensino");
			}
		}
		
		if (extensao != null){
			if(extensao){
				hqlFiltros.append(" AND p.extensao = :extensao");
			}
		}
		
		if (pesquisa != null){
			if(pesquisa){
				hqlFiltros.append(" AND p.pesquisa = :pesquisa");
			}
		}

		hqlCount.append(hqlFiltros.toString());
		hqlConsulta.append(hqlFiltros.toString());
		hqlConsulta.append(" ORDER BY p.ano DESC ");

		// Criando consulta
		Query queryCount = getSession().createQuery(hqlCount.toString());
		Query queryConsulta = getSession().createQuery(hqlConsulta.toString());

		//Busca realizada somente para ações associadas
	    queryCount.setInteger("idTipoProjeto", TipoProjeto.ASSOCIADO);
	    queryConsulta.setInteger("idTipoProjeto", TipoProjeto.ASSOCIADO);


		// Populando os valores dos filtros
		if (titulo != null) {
		    queryCount.setString("titulo", "%" + titulo.toUpperCase() + "%");
		    queryConsulta.setString("titulo", "%" + titulo.toUpperCase() + "%");
		}

		if (idEdital != null) {
		    queryCount.setInteger("idEdital", idEdital);
		    queryConsulta.setInteger("idEdital", idEdital);
		}

		if ((inicio != null) && (fim != null)) {
		    queryConsulta.setDate("inicio", inicio);
		    queryConsulta.setDate("fim", fim);

		    queryCount.setDate("inicio", inicio);
		    queryCount.setDate("fim", fim);
		}

		if (idUnidadeProponente != null) {
		    queryCount.setInteger("idUnidadeProponente",   idUnidadeProponente);
		    queryConsulta.setInteger("idUnidadeProponente",   idUnidadeProponente);
		}

		if (idCentro != null) {
		    queryCount.setInteger("idCentro", idCentro);
		    queryConsulta.setInteger("idCentro", idCentro);
		}

		if (idAreaCNPq != null) {
		    queryCount.setInteger("idAreaCNPq", idAreaCNPq);
		    queryConsulta.setInteger("idAreaCNPq", idAreaCNPq);
		}

		if (idServidor != null) {
		    queryCount.setInteger("idServidor", idServidor);
		    queryConsulta.setInteger("idServidor", idServidor);
		}

		if (ano != null) {
		    queryCount.setInteger("ano", ano);
		    queryConsulta.setInteger("ano", ano);
		}

		if (financiamentoInterno != null) {
		    queryCount.setBoolean("financiamentoInterno", financiamentoInterno);
		    queryConsulta.setBoolean("financiamentoInterno", financiamentoInterno);
		}

		if (financiamentoExterno != null) {
		    queryCount.setBoolean("financiamentoExterno", financiamentoExterno);
		    queryConsulta.setBoolean("financiamentoExterno", financiamentoExterno);
		}

		if (autoFinanciamento != null) {
		    queryCount.setBoolean("autoFinanciamento", autoFinanciamento);
		    queryConsulta.setBoolean("autoFinanciamento", autoFinanciamento);
		}

		if (solicitacaoRenovacao != null) {
		    queryCount.setBoolean("solicitacaoRenovacao", solicitacaoRenovacao);
		    queryConsulta.setBoolean("solicitacaoRenovacao", solicitacaoRenovacao);
		}
		
		if (ensino != null){
			if(ensino){
				queryCount.setBoolean("ensino", ensino);
				queryConsulta.setBoolean("ensino", ensino);
			}
		}
		
		if (extensao != null){
			if(extensao){
				queryCount.setBoolean("extensao", extensao);
				queryConsulta.setBoolean("extensao", extensao);
			}
		}
		
		if (pesquisa != null){
			if(pesquisa){
				queryCount.setBoolean("pesquisa", pesquisa);
				queryConsulta.setBoolean("pesquisa", pesquisa);
			}
		}
		
		if ((idsSituacoesProjeto != null) && (idsSituacoesProjeto.length > 0)) {
		    queryCount.setParameterList("situacoes", idsSituacoesProjeto);
		    queryConsulta.setParameterList("situacoes", idsSituacoesProjeto);
		}

		Long total = (Long) queryCount.uniqueResult();

		if (total > LIMITE_RESULTADOS) {
		    throw new LimiteResultadosException("A consulta retornou " + total + " resultados. Por favor, restrinja mais a busca.");
		}

		@SuppressWarnings("unchecked")
		List<Object> lista = queryConsulta.list();

		ArrayList<Projeto> result = new ArrayList<Projeto>();

		int idOld = 0;
		for (int a = 0; a < lista.size(); a++) {

		    int col = 0;
		    Object[] colunas = (Object[]) lista.get(a);
		    Projeto pj = new Projeto((Integer) colunas[col++]);

		    if (idOld != pj.getId()) {
				idOld = pj.getId();
				col = 0;
				pj.setId(((Integer) colunas[col++]));					
				pj.setNumeroInstitucional(((Integer) colunas[col++]));
				pj.setDataInicio((Date) colunas[col++]);
				pj.setDataFim((Date) colunas[col++]);
				pj.setAno((Integer) colunas[col++]);
				pj.setFinanciamentoInterno((Boolean) colunas[col++]);
				pj.setFinanciamentoExterno((Boolean) colunas[col++]);
				pj.setAutoFinanciado((Boolean) colunas[col++]);
				pj.setRenovacao((Boolean) colunas[col++]);
				pj.setEdital((Edital) colunas[col++]);
				pj.setTitulo((String) colunas[col++]);
				pj.setMedia((Double) colunas[col++]);
				pj.setClassificacao((Integer) colunas[col++]);
				pj.setEnsino((Boolean) colunas[col++]);
				pj.setExtensao((Boolean) colunas[col++]);
				pj.setPesquisa((Boolean) colunas[col++]);
	
				Unidade unidade = new Unidade();
				unidade.setId((Integer) colunas[col++]);
				unidade.setSigla((String) colunas[col++]);
				unidade.setNome((String) colunas[col++]);
				pj.setUnidade(unidade);
	
				if (colunas[19] != null) {
	        			Unidade unidadeOrcamentaria = new Unidade();
	        			unidadeOrcamentaria.setId((Integer) colunas[col++]);
	        			unidadeOrcamentaria.setSigla((String) colunas[col++]);
	        			unidadeOrcamentaria.setNome((String) colunas[col++]);
	        			pj.setUnidadeOrcamentaria(unidadeOrcamentaria);
				}
				
				col = 22;
				TipoSituacaoProjeto situacao = new TipoSituacaoProjeto();
				situacao.setId((Integer) colunas[col++]);
				situacao.setDescricao((String) colunas[col++]);
				pj.setSituacaoProjeto(situacao);
				
				// Adicionando o coordenador na ação encontrada
				if (colunas[24] != null) {
					MembroProjeto m = new MembroProjeto();
					m.setId((Integer) colunas[col++]);
					Pessoa p = new Pessoa();
					p.setId((Integer) colunas[col++]);
					p.setNome((String) colunas[col++]);
					p.setEmail((String) colunas[col++]);
					m.setPessoa(p);
					m.setFuncaoMembro(new FuncaoMembro((Integer) colunas[col++]));
					m.setAtivo((Boolean) colunas[col++]);
					m.setDataInicio((Date) colunas[col++]);
					m.setDataFim((Date) colunas[col++]);
					
					// setando a unidade do coordenador seja ele servidor ou docente externo
					Unidade u = new Unidade();
					if(colunas[32] != null){
						u.setId((Integer) colunas[col++]);
						u.setNome((String) colunas[col++]);
						u.setSigla((String) colunas[col++]);
						m.getServidor().setUnidade(u);
					}else if(colunas[34] != null){
						u.setId((Integer) colunas[col++]);
						u.setNome((String) colunas[col++]);
						u.setSigla((String) colunas[col++]);
						m.getDocenteExterno().setUnidade(u);
					}
				
					pj.getEquipe().add(m);
					pj.setCoordenador(m);
				}
				
				result.add(pj);
		    }

		    col = 38;
		    if (colunas[col] != null) {
			    Avaliacao ava = new Avaliacao();
			    ava.setId((Integer) colunas[col++]);
			    ava.setAtivo((Boolean) colunas[col++]);
			    ava.setDataAvaliacao((Date) colunas[col++]);
			    ava.setNota((Double) colunas[col++]);
			    result.get(result.indexOf(pj)).getAvaliacoes().add(ava);
		    }
		    
		}

		return result;

	    } catch (LimiteResultadosException ex) {
		throw new LimiteResultadosException(ex.getMessage(), ex);
	    }		
	    catch (Exception ex) {
		throw new DAOException(ex.getMessage(), ex);
	    }
	}

	/**
	 * Método que realiza a consulta SQL para um relatório, e retorna uma Lista
	 * das linhas da consulta
	 *
	 * @param consultaSql
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> executeSql(String consultaSql) throws DAOException {

		SQLQuery q = getSession().createSQLQuery(consultaSql);
		q.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
		return q.list();
	}
	
	/**
	 * Retorna um Quantitativo de Projetos Submetidos para o Edital REUNI  
	 * 
	 * @param idEdital
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQuantitativoTurmaDepartamento(Integer idEdital) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder("SELECT "+
				" (select count(*) from projetos.projeto where ativo = trueValue() and id_edital = "+idEdital+" and id_tipo_projeto = "+TipoProjeto.PESQUISA+") AS ISOLADOS_PESQUISA, "+
				" (select count(*) from projetos.projeto where ativo = trueValue() and id_edital = "+idEdital+" and id_tipo_projeto = "+TipoProjeto.ENSINO+") AS ISOLADOS_ENSINO, "+
				" (select count(*) from projetos.projeto where ativo = trueValue() and id_edital = "+idEdital+" and id_tipo_projeto = "+TipoProjeto.EXTENSAO+") AS ISOLADOS_EXTENSAO, "+
				" (select count(*) from projetos.projeto where ativo = trueValue() and id_edital = "+idEdital+" and id_tipo_projeto != "+TipoProjeto.ASSOCIADO+") AS TOTAL_ISOLADOS, "+
				" (select count(*) from projetos.projeto where ativo = trueValue() and id_edital = "+idEdital+" and id_tipo_projeto = "+TipoProjeto.ASSOCIADO+" and ensino = trueValue() and pesquisa = trueValue() and extensao = trueValue()) AS ASSOCIADOS_TRES_DIMENSOES, "+
				" (select count(*) from projetos.projeto where ativo = trueValue() and id_edital = "+idEdital+" and id_tipo_projeto = "+TipoProjeto.ASSOCIADO+" and ensino = trueValue() and pesquisa = trueValue() and extensao = falseValue()) AS ASSOCIADOS_ENSINO_PESQUISA, "+
				" (select count(*) from projetos.projeto where ativo = trueValue() and id_edital = "+idEdital+" and id_tipo_projeto = "+TipoProjeto.ASSOCIADO+" and ensino = trueValue() and pesquisa = falseValue() and extensao = trueValue()) AS ASSOCIADOS_ENSINO_EXTENSAO, "+
				" (select count(*) from projetos.projeto where ativo = trueValue() and id_edital = "+idEdital+" and id_tipo_projeto = "+TipoProjeto.ASSOCIADO+" and ensino = falseValue() and pesquisa = trueValue() and extensao = trueValue()) AS ASSOCIADOS_PESQUISA_EXTENSAO, "+
				" (select count(*) from projetos.projeto where ativo = trueValue() and id_edital = "+idEdital+" and id_tipo_projeto = "+TipoProjeto.ASSOCIADO+") AS TOTAL_ASSOCIADOS, "+
				" (select count(*) from projetos.projeto where ativo = trueValue() and id_edital = "+idEdital+") AS TOTAL_GERAL");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}
	

	/**
	 * Retorna todos os projetos que podem solicitar reconsideração
	 * para um determinado servidor que dever ser o coordenador do
	 * projeto.
	 * 
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Projeto> findByPassiveisReconsideracao(int idServidor) throws DAOException {
		if (idServidor > 0) {
			String projecao = "pj.id, pj.ano, pj.titulo, pj.situacaoProjeto.id, pj.situacaoProjeto.descricao, pj.coordenador.id, " +
					" pj.edital.id, pj.edital.descricao, pj.edital.dataFimReconsideracao, pj.edital.dataInicioReconsideracao ";
			String hql = " SELECT " + projecao + " FROM Projeto pj "
					+ " JOIN pj.coordenador coord "
					+ " LEFT JOIN pj.edital edital"
					+ " WHERE pj.ativo = trueValue() " 
					+ " AND pj.tipoProjeto = :tipoProjeto " 
					+ " AND coord.servidor.id = :idServidor AND coord.ativo = trueValue() "
					+ " AND pj.situacaoProjeto.id IN (:GRUPO_SOLICITAR_RECONSIDERACAO, :ANALISANDO_PEDIDO_RECONSIDERACAO) ";

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idServidor", idServidor);
			query.setParameterList("GRUPO_SOLICITAR_RECONSIDERACAO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_SOLICITAR_RECONSIDERACAO);
			query.setInteger("ANALISANDO_PEDIDO_RECONSIDERACAO", TipoSituacaoProjeto.PROJETO_BASE_ANALISANDO_PEDIDO_RECONSIDERACAO);
			query.setInteger("tipoProjeto", TipoProjeto.ASSOCIADO);

			return HibernateUtils.parseTo(query.list(), projecao, Projeto.class, "pj");

		} else {
			return null;
		}
	}	

	/**
	 * Verifica com base no histórico de situações se a execução da ação foi realizada em algum momento.
	 * 
	 * @param idProjeto
	 * @param idTipoProjeto
	 * @return
	 */
	public boolean isProjetoPassouPorSituacao(int idProjeto, Integer[] idsSituacoes) {
		JdbcTemplate jt = getJdbcTemplate();
		int i = 0;
		i = jt.queryForInt("select count(h.id_projeto) from projetos.historico_situacao_projeto h where h.id_projeto = "
				+ idProjeto
				+ " and h.id_tipo_situacao_projeto in "
				+ UFRNUtils
						.gerarStringIn(idsSituacoes));
		return i > 0;
	}
	
	/**
	 * Retorna o total de projetos coordenados pela pessoa informada
	 * no edital do projeto.  
	 * 
	 * @param edital
	 * @return
	 * @throws DAOException
	 */
	public Long countProjetosCoordenador(Pessoa pessoa, Projeto projeto) throws DAOException {
	    try {
		StringBuilder hql = new StringBuilder();
		hql.append(" select count(pj.id) FROM Projeto pj " +
			" JOIN pj.coordenador coordenador " +
			" JOIN pj.situacaoProjeto sitPro " +
			" WHERE pj.ativo = trueValue() " +
			" AND pj.id <> :idProjeto " +
			" AND pj.edital.id = :idEdital " +
			" AND sitPro.id NOT IN (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO) " +
			" AND coordenador.ativo = trueValue() " +
			" AND coordenador.servidor.pessoa.id = :idPessoa "
		);
		Query query = getSession().createQuery(hql.toString());

		query.setInteger("idPessoa", pessoa.getId());
		query.setInteger("idProjeto", projeto.getId());
		query.setInteger("idEdital", projeto.getEdital().getId());
		query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
		query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);
		return (Long) query.uniqueResult();
		
	    } catch (Exception e) {
		throw new DAOException(e.getMessage(), e);
	    }
	}
	
	/**
	 * Retorna o total de projetos já classificados para o edital informado.
	 * 
	 * @param idEdital
	 * @return
	 */
	public int getTotalProjetosClassificadosParaEdital(int idEdital) {
	    JdbcTemplate jt = getJdbcTemplate();
	    int i = 0;
	    i = jt.queryForInt("select count(id_projeto) from projetos.projeto p where p.id_edital = " + idEdital + " and p.media is not null");
	    return i;
	}
	
    /**
     * Retorna a lista de projetos com avaliações de acordo com os parâmetros passados.
     * 
     * @param avaliador
     * @param situacao
     * @param dataAvaliacao
     * @param titulo
     * @param ano
     * @return
     * @throws DAOException
     */
    public List<Projeto> findByAvaliacoes(Usuario avaliador, TipoSituacaoAvaliacao situacao, String titulo, Integer ano, Unidade unidadeProponente, int tipoProjeto) throws DAOException {
    	try {
    		StringBuffer hql = new StringBuffer(
    				"select projeto.id, projeto.ano, projeto.titulo, projeto.media, unidade.id, unidade.nome, unidade.sigla, " +
    				"aval.id, aval.dataAvaliacao, aval.nota, aval.ativo, " +
    				"distribuicao.id, tipoAvaliador, " +
    				"modelo.id, modelo.tipoAvaliacao.id, modelo.tipoAvaliacao.descricao, " +
    				"situacao.id, situacao.descricao, avaliador.id, pessoa.id, pessoa.nome " +
    				"from br.ufrn.sigaa.projetos.dominio.Avaliacao aval " +
    				"join aval.projeto projeto " +
    				"join projeto.unidade unidade " +    				
    				"join aval.situacao situacao " +
    				"join aval.avaliador avaliador " +
    				"join aval.distribuicao distribuicao " +
    				"join distribuicao.tipoAvaliador tipoAvaliador " +
    				"join distribuicao.modeloAvaliacao modelo " +
    				"join avaliador.pessoa pessoa " +
    				"where aval.ativo = trueValue() and projeto.ativo = trueValue() and projeto.tipoProjeto.id = " + tipoProjeto);

    		if (avaliador != null) {
    			hql.append(" and avaliador.id = :idAvaliador");
    		}
    		
    		if (situacao != null) {
    			hql.append(" and situacao.id = :idSituacao");
    		}
    		
    		if (titulo != null) {
    		    hql.append(" and "
    			    + UFRNUtils.toAsciiUpperUTF8("projeto.titulo") + " like "
    			    + UFRNUtils.toAsciiUTF8(":titulo"));
    		}
    		
    		if (ano != null) {    		
    			hql.append(" and projeto.ano = :ano");
    		}

    		if (unidadeProponente != null) {    		
    			hql.append(" and projeto.unidade.id = :idUnidade");
    		}

    		
    		hql.append(" order by projeto.id, situacao.id");    		
    		Query query = getSession().createQuery(hql.toString());
    		
    		if (avaliador != null) {
    			query.setInteger("idAvaliador", avaliador.getId());
    		}
    		
    		if (situacao != null) {
    			query.setInteger("idSituacao", situacao.getId());
    		}
    		
    		if (titulo != null) {
    			query.setString("titulo", "%" + titulo.toUpperCase() + "%");
    		}
    		
    		if (ano != null) {    		
    			query.setInteger("ano", ano);
    		}

    		if (unidadeProponente != null) {    		
    			query.setInteger("idUnidade", unidadeProponente.getId());
    		}
    		
    		@SuppressWarnings("unchecked")
    		List<Object> lista = query.list();
    		ArrayList<Projeto> result = new ArrayList<Projeto>();

    		int idOld = 0;
    		for (int a = 0; a < lista.size(); a++) {
    		    int col = 0;
    		    Object[] colunas = (Object[]) lista.get(a);
    		    Projeto pj = new Projeto((Integer) colunas[col++]);

    		    if (idOld != pj.getId()) {
    		    	idOld = pj.getId();
    		    	col = 0;    		    	
        		    pj = new Projeto((Integer) colunas[col++]);
        			pj.setAno((Integer) colunas[col++]);
        			pj.setTitulo((String) colunas[col++]);
        			pj.setMedia((Double) colunas[col++]);
        			Unidade u = new Unidade((Integer) colunas[col++]);
        			u.setNome((String) colunas[col++]);
        			u.setSigla((String) colunas[col++]);
        			pj.setUnidade(u);
        			result.add(pj);
    		    }

    		    col = 7;
    		    Avaliacao avaliacao = new Avaliacao();
    		    avaliacao.setId((Integer) colunas[col++]);
    		    avaliacao.setDataAvaliacao((Date) colunas[col++]);
    		    avaliacao.setNota((Double) colunas[col++]);
    		    avaliacao.setAtivo((Boolean) colunas[col++]);
    		    DistribuicaoAvaliacao dis = new DistribuicaoAvaliacao();
    		    dis.setId((Integer) colunas[col++]);
    		    dis.setTipoAvaliador((TipoAvaliador) colunas[col++]);
    		    ModeloAvaliacao mod = new ModeloAvaliacao();
    		    mod.setId((Integer) colunas[col++]);
    		    TipoAvaliacao tipo = new TipoAvaliacao();
    		    tipo.setId((Integer) colunas[col++]);
    		    tipo.setDescricao((String) colunas[col++]);
    		    mod.setTipoAvaliacao(tipo);
    		    dis.setModeloAvaliacao(mod);
    		    avaliacao.setDistribuicao(dis);
    		    
    		    TipoSituacaoAvaliacao sit = new TipoSituacaoAvaliacao();
    		    sit.setId((Integer) colunas[col++]);
    		    sit.setDescricao((String) colunas[col++]);
    		    avaliacao.setSituacao(sit);
    		    
    		    Usuario av = new Usuario();
    		    av.setId((Integer) colunas[col++]);
    		    Pessoa p = new Pessoa ((Integer) colunas[col++]);
    		    p.setNome((String) colunas[col++]);
    		    av.setPessoa(p);
    		    avaliacao.setAvaliador(av);
    		    
    		    result.get(result.indexOf(pj)).getAvaliacoes().add(avaliacao);
    		}
    		
    		return result;

    	} catch (Exception e) {
    		throw new DAOException(e);
    	}
    }  
     
    /**
     * Retorna uma lista de projetos que estão aguardando avaliação.
     * Ou seja, que já foram distribuídos, mas que as avaliações ainda não foram consolidadas.
     * 
     * @param edital
     * @return
     * @throws DAOException
     */
    public List<Projeto> findProjetosParaAvaliar(DistribuicaoAvaliacao distribuicao) throws DAOException {
    	return findProjetosParaDistribuicaoOuAvaliacao(distribuicao, new Integer[] { TipoSituacaoProjeto.PROJETO_BASE_AGUARDANDO_AVALIACAO, TipoSituacaoProjeto.PROJETO_BASE_AVALIADO });
    }

    /**
     * Retorna uma lista de projetos que estão aguardando distribuição ou aguardando avaliação.
     * Ou seja que já foram distribuídos, mas as avaliações ainda não foram consolidadas ou que ainda não 
     * foram distribuídos.
     * 
     * @param edital
     * @return
     * @throws DAOException
     */
    public List<Projeto> findProjetosParaDistribuir(DistribuicaoAvaliacao distribuicao) throws DAOException {
    	return findProjetosParaDistribuicaoOuAvaliacao(distribuicao, new Integer[] { TipoSituacaoProjeto.PROJETO_BASE_AGUARDANDO_AVALIACAO, TipoSituacaoProjeto.PROJETO_BASE_SUBMETIDO });
    }

    
    /**
     * Retorna a lista de projetos que podem ser avaliados ou que estão em processo de avaliação conforme os parâmetros informados.
     * 
     * @param edital edital do projeto
     * @param idsSituacoesProjeto determina se os projetos retornados são os avaliados e/ou aptos a avaliação(submetidos).
     * @return
     * @throws DAOException
     */
    private List<Projeto> findProjetosParaDistribuicaoOuAvaliacao(DistribuicaoAvaliacao distribuicao, Integer[] idsSituacoesProjeto) throws DAOException {    	
    	try {
    		StringBuffer hql = new StringBuffer(
    				"select projeto.id, projeto.ano, projeto.titulo, situacaoProjeto, projeto.media, unidade.id, unidade.nome, unidade.sigla, " +
    				"area.id, area.nome, " +
    				"aval.id, aval.dataAvaliacao, aval.nota, aval.ativo, " +
    				"distribuicao.id, tipoAvaliador, " +
    				"modelo.id, tipoAvaliacao.id, tipoAvaliacao.descricao, questionario.id, avaliador.id, pessoa.id, pessoa.nome, situacaoAvaliacao, " +
    				"eqp.id, eqp.pessoa.id, sol.id " +
    				"from Projeto projeto " +
    				"left join projeto.equipe eqp " +
    				"left join projeto.unidade unidade " +
    				"left join projeto.areaConhecimentoCnpq area " +
    				"left join projeto.situacaoProjeto situacaoProjeto " +
    				"left join projeto.avaliacoes aval " +
    				"left join projeto.solicitacoesReconsideracao sol " +
    				"left join aval.situacao situacaoAvaliacao " +
    				"left join aval.avaliador avaliador " +
    				"left join avaliador.pessoa pessoa " +
    				"left join aval.distribuicao distribuicao " +
    				"left join distribuicao.tipoAvaliador tipoAvaliador " +
    				"left join distribuicao.modeloAvaliacao modelo " +
    				"left join modelo.tipoAvaliacao tipoAvaliacao " +
    				"left join modelo.questionario questionario " +
    				
    		"where projeto.ativo = trueValue() ");

    		if ( distribuicao.getModeloAvaliacao().getEdital() != null ) 
    			hql.append("and projeto.edital.id = :idEdital ");
    		else
    			hql.append("and projeto.edital.id is null ");
    		
    		hql.append("and situacaoProjeto.id in (:situacoesProjeto) ");
    		hql.append(" order by projeto.titulo, aval.id, eqp.id ");
    		
    		Query query = getSession().createQuery(hql.toString());
    		if ( distribuicao.getModeloAvaliacao().getEdital() != null ) {
    			query.setInteger("idEdital", distribuicao.getModeloAvaliacao().getEdital().getId());
			}
   			query.setParameterList("situacoesProjeto", idsSituacoesProjeto);
   			   			
    		@SuppressWarnings("unchecked")
			List<Object> lista = query.list();
    		ArrayList<Projeto> result = new ArrayList<Projeto>();

    		int idOld = 0;
    		int idAvaOld = 0;
    		for (int a = 0; a < lista.size(); a++) {
    			int col = 0;
    			Object[] colunas = (Object[]) lista.get(a);
    			Projeto pj = new Projeto((Integer) colunas[col++]);

    			if (idOld != pj.getId()) {
    				idOld = pj.getId();
    				col = 0;    		    	
    				pj = new Projeto((Integer) colunas[col++]);
    				pj.setAno((Integer) colunas[col++]);
    				pj.setTitulo((String) colunas[col++]);
    				pj.setSituacaoProjeto((TipoSituacaoProjeto) colunas[col++]);
    				pj.setMedia((Double) colunas[col++]);
    				Unidade u = new Unidade((Integer) colunas[col++]);
    				u.setNome((String) colunas[col++]);
    				u.setSigla((String) colunas[col++]);
    				pj.setUnidade(u);
    				Integer idArea = (Integer) colunas[col++];
    				if(idArea != null) {
    					AreaConhecimentoCnpq area = new AreaConhecimentoCnpq(idArea);
    					area.setNome((String) colunas[col++]);
    					pj.setAreaConhecimentoCnpq(area);
    				}
    				result.add(pj);    				
    			}

    			col = 10;
    			if (colunas[col] != null && idAvaOld != ((Integer) colunas[10]).intValue() && colunas[12] != null) {
    				idAvaOld =(Integer) colunas[10];
	    			Avaliacao avaliacao = new Avaliacao();
	    			avaliacao.setId((Integer) colunas[col++]);
	    			avaliacao.setDataAvaliacao((Date) colunas[col++]);
	    			avaliacao.setNota((Double) colunas[col++]);
	    			avaliacao.setAtivo((Boolean) colunas[col++]);
	    			DistribuicaoAvaliacao dis = new DistribuicaoAvaliacao();
	    			dis.setId((Integer) colunas[col++]);
	    			dis.setTipoAvaliador((TipoAvaliador) colunas[col++]);
	    			ModeloAvaliacao mod = new ModeloAvaliacao();
	    			mod.setId((Integer) colunas[col++]);
	    			TipoAvaliacao tipo = new TipoAvaliacao();
	    			tipo.setId((Integer) colunas[col++]);
	    			tipo.setDescricao((String) colunas[col++]);
	    			mod.setTipoAvaliacao(tipo);
	    			QuestionarioAvaliacao q = new QuestionarioAvaliacao((Integer) colunas[col++]);
	    			mod.setQuestionario(q);
	    			dis.setModeloAvaliacao(mod);
	    			avaliacao.setDistribuicao(dis);
	    			Usuario u = new Usuario((Integer) colunas[col++]);
	    			Pessoa p = new Pessoa((Integer) colunas[col++]);
	    			p.setNome((String) colunas[col++]);
	    			u.setPessoa(p);
	    			avaliacao.setAvaliador(u);
	    			avaliacao.setSituacao((TipoSituacaoAvaliacao) colunas[col++]);
	    			avaliacao.setProjeto(pj);
	
	    			result.get(result.indexOf(pj)).getAvaliacoes().add(avaliacao);
    			}

    			col = 24;
    			if (colunas[col] != null) {
    				MembroProjeto mp = new MembroProjeto(); 
    				mp.setId((Integer) colunas[col++]);
    				mp.setPessoa(new Pessoa((Integer) colunas[col++]));
    				result.get(result.indexOf(pj)).getEquipe().add(mp);
    			}
    			
    			col = 26;
    			if (colunas[col] != null) {
	    			SolicitacaoReconsideracao sol = new SolicitacaoReconsideracao();
	    			sol.setId((Integer) colunas[col]);
	    			pj.getSolicitacoesReconsideracao().add(sol);
    			}
    				
    		}

    		return result;

    	} catch (Exception e) {
    		throw new DAOException(e);
    	}

    }

    /**
     * Retorna todos os projetos submetidos ao edital informado que podem ser distribuídos para
     * avaliação.
     * 
     * @param edital
     * @return
     * @throws LimiteResultadosException
     * @throws DAOException
     */
    public List<Projeto> findAptosParaDistribuirByEdital(Edital edital) throws LimiteResultadosException, DAOException {
    	return filter(edital.getId(), null, null, null, null, 
    			new Integer[] {TipoSituacaoProjeto.PROJETO_BASE_SUBMETIDO, TipoSituacaoProjeto.PROJETO_BASE_AGUARDANDO_AVALIACAO}, 
    			null, null, null, null, null, null, null, null, null, null, null);
    }
 
    /***
     * Retorna a quantidade de planos de trabalho ativos do projeto.
     * 
     * @param idProjeto
     * @param idTipoVinculo
     * @return
     */
    public Integer quantidadePlanosTrabalhoAtivos(int idProjeto, int idTipoVinculo){
    	return count("from projetos.plano_trabalho_projeto where ativo = trueValue() " +
    			"and id_projeto = "+idProjeto+" and id_tipo_vinculo_discente = "+idTipoVinculo);
    }
    
    /**
	 * Retorna todas os projetos coordenados pelo servidor informado
	 * que possuem relatórios cadastrados ou não.
	 * 
	 * @param servidor
	 * @param idSituacaoProjeto
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Projeto> findProjetosComRelatorioByCoordenador(Servidor servidor, Integer[] idSituacaoProjeto) throws DAOException {

        	try {
        
        		StringBuilder hql = new StringBuilder();
        		hql.append(" select distinct projeto " +
        				   	" from Projeto projeto " +
        					" inner join projeto.coordenador coord" +
        					" left join projeto.relatorios rel " +
        					" where projeto.ativo = trueValue() and coord.servidor.id = :idServidor and coord.ativo = trueValue() " +
        				   	" and projeto.situacaoProjeto.id not in ( :PROJETO_BASE_GRUPO_INVALIDO ) " +
        				   	" and (projeto.tipoProjeto.id = :idTipoProjeto)");
        		
        		if (idSituacaoProjeto != null) {
        			hql.append(" and projeto.situacaoProjeto.id in (:idSituacaoProjeto) ");
        		}
        		
        		hql.append(" order by projeto.titulo ");			
        		Query query = getSession().createQuery(hql.toString());
        		query.setInteger("idServidor", servidor.getId());
        		query.setInteger("idTipoProjeto", TipoProjeto.ASSOCIADO);
        		query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);
        
        		if (idSituacaoProjeto != null) {
        		    query.setParameterList("idSituacaoProjeto", idSituacaoProjeto);
        		}
        
        		return query.list();
        
        	} catch (Exception e) {
        		throw new DAOException(e.getMessage(), e);
        	}

	}
	
	/**
	 * Retorna todas as fotos ativas do projeto passado por parâmetro
	 * 
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<FotoProjeto> findFotosByProjeto(int idProjeto) throws DAOException {
		try{
			StringBuilder hql = new StringBuilder();
			
			hql.append(" SELECT fotos FROM FotoProjeto fotos");
			hql.append(" WHERE fotos.projeto = :idProjeto AND fotos.ativo = trueValue() ");
			
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idProjeto", idProjeto);
			
			return q.list();
		}catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		
	}
	
	
	/**
	 * Verifica se já existem outros Projetos do mesmo Tipo, com o mesmo Título do projeto informado.
	 * São considerados apenas os projetos com o mesmo Ano de referência do projeto informado. 
	 * Utilizado na validação de dados gerais, proibindo o cadastro de projetos com mesmo título no mesmo ano.
	 * 
	 * @param projeto
	 * @return
	 * @throws DAOException
	 */
	public boolean existeProjetoComMesmoTitulo(Projeto projeto) throws DAOException {
		try {
			Query query = getSession().createQuery("select count(pj.id) from Projeto pj "
					+ "where pj.ativo = trueValue() and (pj.id != :id) " 
					+ "and (pj.ano = :ano) "
					+ "and (pj.tipoProjeto.id = :idTipoProjeto) "
					+ "and " + UFRNUtils.toAsciiUpperUTF8("pj.titulo") + " = " + UFRNUtils.toAsciiUTF8(":titulo"));

			query.setInteger("id", projeto.getId());
			query.setInteger("ano", projeto.getAno());
			query.setInteger("idTipoProjeto", projeto.getTipoProjeto().getId());
			query.setString("titulo", projeto.getTitulo().toUpperCase());
			query.setMaxResults(1);

			return Integer.parseInt(query.uniqueResult().toString()) > 0;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
}
