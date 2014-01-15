/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 20/05/2011
 *
 */
package br.ufrn.sigaa.ouvidoria.dao;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.Unidade;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ouvidoria.dominio.AssuntoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaAssuntoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaSolicitante;
import br.ufrn.sigaa.ouvidoria.dominio.DadosInteressadoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.DelegacaoUsuarioResposta;
import br.ufrn.sigaa.ouvidoria.dominio.HistoricoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.InteressadoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.InteressadoNaoAutenticado;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.NotificacaoManifestacaoPendente;
import br.ufrn.sigaa.ouvidoria.dominio.OrigemManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.StatusManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.TipoHistoricoManifestacao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Dao responsável por realizar consultas de {@link Manifestacao}.
 * 
 * @author bernardo
 * 
 */
public class ManifestacaoDao extends GenericSigaaDAO {

	/**
	 * Verifica se existe alguma {@link Manifestacao} designada à {@link PessoaGeral}
	 * passada. Utilizado no controle de acesso ao módulo de Ouvidoria.
	 * 
	 * @param pessoa
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public boolean possuiManifestacoesDesignadas(PessoaGeral pessoa) throws HibernateException, DAOException {
		String count = "SELECT count(*) " + "FROM DelegacaoUsuarioResposta d " +
						"WHERE d.pessoa.id = :idPessoa and d.ativo = :ativo";

		final Query q = getSession().createQuery(count);

		q.setInteger("idPessoa", pessoa.getId());
		q.setBoolean("ativo", true);

		return (Long) q.uniqueResult() > 0;
	}

	/**
	 * Retorna todas as manifestações cadastradas de acordo com a lista de status passada.
	 * 
	 * @param status
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Manifestacao> getAllManifestacoesByStatus(int... status) throws HibernateException, DAOException {
		String projecao = "m.id, m.numero, m.dataCadastro, m.titulo, m.anonima, m.origemManifestacao, m.statusManifestacao, m.assuntoManifestacao, i.categoriaSolicitante, p.id, p.nome, ina.id, ina.nome, u.nome ";

		String hql = "SELECT distinct " + projecao + 
						" FROM HistoricoManifestacao h " +
								"LEFT JOIN h.unidadeResponsavel u " +
								"RIGHT JOIN h.manifestacao m " +
								"INNER JOIN m.interessadoManifestacao i " +
								"INNER JOIN i.dadosInteressadoManifestacao di " +
								"LEFT JOIN di.pessoa p " +
								"LEFT JOIN di.interessadoNaoAutenticado ina " +
						"WHERE m.statusManifestacao.id in " + UFRNUtils.gerarStringIn(status) +
						" ORDER BY m.dataCadastro desc, m.numero";

		Query q = getSession().createQuery(hql);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		ArrayList<Manifestacao> manifestacoes = new ArrayList<Manifestacao>();
		
		// Prioridade para a última alteração no histórico
		Integer idUltimaManifestacao = 0;
		Integer idNovaManifestacao = 0;
		
		for (Object[] linha : list) {
			Manifestacao m = new Manifestacao();
			m.setInteressadoManifestacao(new InteressadoManifestacao());

			int cont = 0;
			idNovaManifestacao = (Integer) linha[cont++];
			
			if (idNovaManifestacao.intValue() != idUltimaManifestacao.intValue()) {
				m.setId(idNovaManifestacao);
				m.setNumero((String) linha[cont++]);
				m.setDataCadastro((Date) linha[cont++]);
				m.setTitulo((String) linha[cont++]);
				m.setAnonima((Boolean) linha[cont++]);
				m.setOrigemManifestacao((OrigemManifestacao) linha[cont++]);
				m.setStatusManifestacao((StatusManifestacao) linha[cont++]);
				m.setAssuntoManifestacao((AssuntoManifestacao) linha[cont++]);
				m.getInteressadoManifestacao().setCategoriaSolicitante((CategoriaSolicitante) linha[cont++]);
				m.getInteressadoManifestacao().setDadosInteressadoManifestacao(new DadosInteressadoManifestacao());
				Integer idPessoa = (Integer) linha[cont++];
				if(isNotEmpty(idPessoa)) {
					m.getInteressadoManifestacao().getDadosInteressadoManifestacao().setPessoa(new Pessoa(idPessoa));
					m.getInteressadoManifestacao().getDadosInteressadoManifestacao().getPessoa().setNome((String) linha[cont++]);
				}
				else {
					cont++;
				}
				Integer idInteressado = (Integer) linha[cont++];
				if(isNotEmpty(idInteressado)) {
					m.getInteressadoManifestacao().getDadosInteressadoManifestacao().setInteressadoNaoAutenticado(new InteressadoNaoAutenticado(idInteressado));
					m.getInteressadoManifestacao().getDadosInteressadoManifestacao().getInteressadoNaoAutenticado().setNome((String) linha[cont++]);
				}
				else {
					cont++;
				}
				String nomeUnidade = (String) linha[cont++];
				if(isNotEmpty(nomeUnidade)) {
					m.setUnidadeResponsavel(new Unidade());
					m.getUnidadeResponsavel().setNome(nomeUnidade);
				}
	
				manifestacoes.add(m);
			}
			
			idUltimaManifestacao = idNovaManifestacao;
		}

		if (manifestacoes != null && manifestacoes.size() > 0){
			Collections.sort(manifestacoes, new Comparator<Manifestacao>(){
				public int compare(Manifestacao m1, Manifestacao m2) {
					int retorno = 0;
					retorno = m1.getDataCadastro().compareTo(m2.getDataCadastro());
					if( retorno == 0 )
						retorno = m2.getNumero().compareToIgnoreCase(m1.getNumero());
					return retorno;
				}
			});
		}
		
		return manifestacoes;
	}

	/**
	 * Retorna todas as manifestações pendentes para a Ouvidoria
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Manifestacao> getAllManifestacoesPendentes() throws HibernateException, DAOException {
		return getAllManifestacoesByStatus(StatusManifestacao.SOLICITADA, StatusManifestacao.ESCLARECIDO_OUVIDORIA);
	}

	/**
	 * Retorna todas as manifestações já encaminhadas pela ouvidoria que ainda não foram respondidas.
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Manifestacao> getAllManifestacoesEncaminhadas() throws HibernateException, DAOException {
		return getAllManifestacoesByStatus(StatusManifestacao.ENCAMINHADA_UNIDADE, StatusManifestacao.DESIGNADA_RESPONSAVEL, StatusManifestacao.AGUARDANDO_PARECER, StatusManifestacao.ESCLARECIDO_RESPONSAVEL);
	}
	
	/**
	 * Retorna todas as manifestações já encaminhadas pela ouvidoria que já foram respondidas pela unidade.
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Manifestacao> getAllManifestacoesRespondidas() throws HibernateException, DAOException {
		return getAllManifestacoesByStatus(StatusManifestacao.PARECER_CADASTRADO, StatusManifestacao.RESPONDIDA);
	}

	/**
	 * Retorna todas as manifestações pendentes de acordo com a unidade responsável por elas.
	 * 
	 * @param unidade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Manifestacao> getAllManifestacoesPendentesByUnidade(UnidadeGeral ...unidade) throws HibernateException, DAOException {
		String projecao = "m.id, m.numero, m.dataCadastro, m.titulo, m.anonima, m.origemManifestacao, m.statusManifestacao, m.assuntoManifestacao, i.categoriaSolicitante, p.id, p.nome, ina.id, ina.nome, h.lido";

		String hql = "SELECT " + projecao + " FROM HistoricoManifestacao h " +
							"INNER JOIN h.manifestacao m " +
							"INNER JOIN m.interessadoManifestacao i " +
							"INNER JOIN i.dadosInteressadoManifestacao di " +
							"LEFT JOIN di.pessoa p " +
							"LEFT JOIN di.interessadoNaoAutenticado ina " +
						"WHERE h.unidadeResponsavel.id in " + UFRNUtils.gerarStringIn(unidade) +
							"AND m.statusManifestacao.id in " + UFRNUtils.gerarStringIn(new int[] {StatusManifestacao.ENCAMINHADA_UNIDADE, StatusManifestacao.DESIGNADA_RESPONSAVEL, StatusManifestacao.AGUARDANDO_PARECER, StatusManifestacao.ESCLARECIDO_RESPONSAVEL}) +
						" ORDER BY m.dataCadastro, m.numero";

		Query q = getSession().createQuery(hql);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		Map<Integer, Manifestacao> manifestacoes = new HashMap<Integer, Manifestacao>();

		for (Object[] linha : list) {
			Manifestacao m = new Manifestacao();
			m.setInteressadoManifestacao(new InteressadoManifestacao());

			int cont = 0;

			m.setId((Integer) linha[cont++]);
			if (manifestacoes.containsKey(m.getId())) {
				m = manifestacoes.get(m.getId());
				// pula para a posição do atributo 'h.lido'
				cont = 13;
				m.setLida(m.isLida() && (Boolean) linha[cont]);
			} else {
				m.setNumero((String) linha[cont++]);
				m.setDataCadastro((Date) linha[cont++]);
				m.setTitulo((String) linha[cont++]);
				m.setAnonima((Boolean) linha[cont++]);
				m.setOrigemManifestacao((OrigemManifestacao) linha[cont++]);
				m.setStatusManifestacao((StatusManifestacao) linha[cont++]);
				m.setAssuntoManifestacao((AssuntoManifestacao) linha[cont++]);
				m.getInteressadoManifestacao().setCategoriaSolicitante((CategoriaSolicitante) linha[cont++]);
				m.getInteressadoManifestacao().setDadosInteressadoManifestacao(new DadosInteressadoManifestacao());
				Integer idPessoa = (Integer) linha[cont++];
				if(isNotEmpty(idPessoa)) {
					m.getInteressadoManifestacao().getDadosInteressadoManifestacao().setPessoa(new Pessoa(idPessoa));
					m.getInteressadoManifestacao().getDadosInteressadoManifestacao().getPessoa().setNome((String) linha[cont++]);
				}
				else {
					cont++;
				}
				Integer idInteressado = (Integer) linha[cont++];
				if(isNotEmpty(idInteressado)) {
					m.getInteressadoManifestacao().getDadosInteressadoManifestacao().setInteressadoNaoAutenticado(new InteressadoNaoAutenticado(idInteressado));
					m.getInteressadoManifestacao().getDadosInteressadoManifestacao().getInteressadoNaoAutenticado().setNome((String) linha[cont++]);
				}
				else {
					cont++;
				}
				m.setLida((Boolean) linha[cont++]);
				
				manifestacoes.put(m.getId(), m);
			}
		}

		return manifestacoes.values();
	}

	/**
	 * Retorna todas as manifestações pendentes de acordo com a pessoa designada a respondê-las.
	 * 
	 * @param idPessoa
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Manifestacao> getAllManifestacoesPendentesByDesignado(int idPessoa) throws HibernateException, DAOException {
		String projecao = "m.id, m.numero, m.dataCadastro, m.titulo, m.anonima, m.origemManifestacao, m.assuntoManifestacao, i.categoriaSolicitante, p.id, p.nome, ina.id, ina.nome, h.lido";

		String hql = "SELECT " + projecao + " FROM DelegacaoUsuarioResposta d " +
							" INNER JOIN d.historicoManifestacao h " +
							" INNER JOIN h.manifestacao m " +
							" INNER JOIN m.interessadoManifestacao i " +
							" INNER JOIN i.dadosInteressadoManifestacao di " +
							" LEFT JOIN di.pessoa p " +
							" LEFT JOIN di.interessadoNaoAutenticado ina " +
							" JOIN m.interessadoManifestacao interessadoManifestacao " +
						" WHERE d.pessoa.id = :pessoa" +
							" AND m.statusManifestacao.id in ("+StatusManifestacao.DESIGNADA_RESPONSAVEL+","+StatusManifestacao.ESCLARECIDO_RESPONSAVEL+")" +
							" AND d.ativo = " + SQLDialect.TRUE +
						" ORDER BY m.dataCadastro, m.numero";

		Query q = getSession().createQuery(hql);

		q.setInteger("pessoa", idPessoa);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();

		Map<Integer, Manifestacao> manifestacoes = new HashMap<Integer, Manifestacao>();

		for (Object[] linha : list) {
			Manifestacao m = new Manifestacao();
			m.setInteressadoManifestacao(new InteressadoManifestacao());

			int cont = 0;

			m.setId((Integer) linha[cont++]);
			if (manifestacoes.containsKey(m.getId())) {
				m = manifestacoes.get(m.getId());
				// pula para a posição do atributo 'h.lido'
				cont = 11;
				m.setLida(m.isLida() && (Boolean) linha[cont]);
			} else {
				m.setNumero((String) linha[cont++]);
				m.setDataCadastro((Date) linha[cont++]);
				m.setTitulo((String) linha[cont++]);
				m.setAnonima((Boolean) linha[cont++]);
				m.setOrigemManifestacao((OrigemManifestacao) linha[cont++]);
				m.setAssuntoManifestacao((AssuntoManifestacao) linha[cont++]);
				m.getInteressadoManifestacao().setCategoriaSolicitante(
						(CategoriaSolicitante) linha[cont++]);
				m.getInteressadoManifestacao().setDadosInteressadoManifestacao(new DadosInteressadoManifestacao());
				Integer idPessoaInteressada = (Integer) linha[cont++];
				if(isNotEmpty(idPessoaInteressada)) {
					m.getInteressadoManifestacao().getDadosInteressadoManifestacao().setPessoa(new Pessoa(idPessoaInteressada));
					m.getInteressadoManifestacao().getDadosInteressadoManifestacao().getPessoa().setNome((String) linha[cont++]);
				}
				else {
					cont++;
				}
				Integer idInteressado = (Integer) linha[cont++];
				if(isNotEmpty(idInteressado)) {
					m.getInteressadoManifestacao().getDadosInteressadoManifestacao().setInteressadoNaoAutenticado(new InteressadoNaoAutenticado(idInteressado));
					m.getInteressadoManifestacao().getDadosInteressadoManifestacao().getInteressadoNaoAutenticado().setNome((String) linha[cont++]);
				}
				else {
					cont++;
				}
				m.setLida((Boolean) linha[cont++]);

				manifestacoes.put(m.getId(), m);
			}
		}

		return manifestacoes.values();
	}

	/**
	 * Retorna a lista de manifestações para acompanhamento da ouvidoria.
	 * 
	 * @param ano
	 * @param numero
	 * @param dataInicial
	 * @param dataFinal
	 * @param categoriaAssuntoManifestacao
	 * @param paging
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Manifestacao> findManifestacoesAcompanhamentoOuvidoria(Integer ano, String numero, Date dataInicial, Date dataFinal, CategoriaAssuntoManifestacao categoriaAssuntoManifestacao, String nome, Long matriculaSiape, Long cpf, PagingInformation paging) throws HibernateException, DAOException {
		String projecao = "m.id_manifestacao, m.numero, m.data_cadastro, m.titulo, m.id_origem_manifestacao, m.id_assunto_manifestacao, sm.id_status_manifestacao, sm.descricao, cs.id_categoria_solicitante, cs.descricao as categoria ";

		String hql = "SELECT DISTINCT " + projecao;
						
		String consulta = "FROM ouvidoria.manifestacao m " +
								"INNER JOIN ouvidoria.assunto_manifestacao USING (id_assunto_manifestacao) " +
								"INNER JOIN ouvidoria.categoria_assunto_manifestacao ca USING (id_categoria_assunto_manifestacao) " +
								"INNER JOIN ouvidoria.status_manifestacao sm USING (id_status_manifestacao) " +
								"INNER JOIN ouvidoria.interessado_manifestacao i USING (id_interessado_manifestacao) " + 
								"INNER JOIN ouvidoria.categoria_solicitante cs USING (id_categoria_solicitante) " +
								"INNER JOIN ouvidoria.dados_interessado_manifestacao di USING (id_dados_interessado_manifestacao) " +
								"LEFT JOIN comum.pessoa p USING (id_pessoa) " +
								"LEFT JOIN ouvidoria.interessado_nao_autenticado ina USING (id_interessado_nao_autenticado) " + 
								"LEFT JOIN rh.servidor s USING (id_pessoa) " +
								"LEFT JOIN discente d USING (id_pessoa) " +
							"WHERE sm.id_status_manifestacao != "+StatusManifestacao.CANCELADA;

		if (isNotEmpty(ano))
			consulta += "AND " + SQLDialect.extractDate("m.data_cadastro", "year") + " = :ano ";
		if (isNotEmpty(numero))
			consulta += "AND m.numero like :numero ";
		
		if(isNotEmpty(dataInicial))
			consulta += " AND m.data_cadastro >= :dataInicial ";
		if(isNotEmpty(dataFinal))
			consulta += " AND m.data_cadastro <= :dataFinal ";
		
		if(isNotEmpty(categoriaAssuntoManifestacao))
			consulta += "AND ca.id_categoria_assunto_manifestacao = :categoriaAssunto ";
		
		if(isNotEmpty(nome)) {
			consulta += "AND p.nome_ascii ilike :nome OR ina.nome_ascii ilike :nome ";
		}
		
		if(isNotEmpty(matriculaSiape))
			consulta += "AND d.matricula = :matriculaSiape OR s.siape = :matriculaSiape ";
		
		if(isNotEmpty(cpf))
			consulta += "AND p.cpf_cnpj = :cpf ";

		hql += consulta + "ORDER BY m.data_cadastro desc";

		Query q = getSession().createSQLQuery(hql);

		if (isNotEmpty(ano))
			q.setInteger("ano", ano);
		if (isNotEmpty(numero))
			q.setString("numero", numero);
		if(isNotEmpty(dataInicial))
			q.setDate("dataInicial", dataInicial);
		if(isNotEmpty(dataFinal))
			q.setDate("dataFinal", dataFinal);
		if(isNotEmpty(categoriaAssuntoManifestacao))
			q.setInteger("categoriaAssunto", categoriaAssuntoManifestacao.getId());
		if(isNotEmpty(nome)) {
			nome = StringUtils.toAscii(nome + "%");
			q.setString("nome", nome);
		}
		if(isNotEmpty(matriculaSiape))
			q.setLong("matriculaSiape", matriculaSiape);
		if(isNotEmpty(cpf))
			q.setLong("cpf", cpf);
		
		if(paging != null){
			String countHql = "SELECT count(*) " + consulta;
			Query count = getSession().createSQLQuery(countHql );
			
			if (isNotEmpty(ano))
				count.setInteger("ano", ano);
			if (isNotEmpty(numero))
				count.setString("numero", numero + "%");
			if(isNotEmpty(dataInicial))
				count.setDate("dataInicial", dataInicial);
			if(isNotEmpty(dataFinal))
				count.setDate("dataFinal", dataFinal);
			if(isNotEmpty(categoriaAssuntoManifestacao))
				count.setInteger("categoriaAssunto", categoriaAssuntoManifestacao.getId());
			if(isNotEmpty(nome))
				count.setString("nome", nome);
			if(isNotEmpty(matriculaSiape))
				count.setLong("matriculaSiape", matriculaSiape);
			if(isNotEmpty(cpf))
				count.setLong("cpf", cpf);
	 
			paging.setTotalRegistros(((BigInteger) count.uniqueResult()).intValue());
			q.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			q.setMaxResults(paging.getTamanhoPagina());
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		Collection<Manifestacao> manifestacoes = new ArrayList<Manifestacao>();

		for (Object[] linha : list) {
			Manifestacao m = new Manifestacao();
			m.setInteressadoManifestacao(new InteressadoManifestacao());

			int cont = 0;

			m.setId((Integer) linha[cont++]);
			m.setNumero((String) linha[cont++]);
			m.setDataCadastro((Date) linha[cont++]);
			m.setTitulo((String) linha[cont++]);
			m.setOrigemManifestacao(new OrigemManifestacao((Integer) linha[cont++]));
			m.setAssuntoManifestacao(new AssuntoManifestacao((Integer) linha[cont++]));
			m.setStatusManifestacao(new StatusManifestacao((Integer) linha[cont++]));
			m.getStatusManifestacao().setDescricao((String) linha[cont++]);
			m.getInteressadoManifestacao().setCategoriaSolicitante(new CategoriaSolicitante((Integer) linha[cont++]));
			m.getInteressadoManifestacao().getCategoriaSolicitante().setDescricao((String) linha[cont++]);

			manifestacoes.add(m);
		}

		return manifestacoes;
	}

	/**
	 * Retorna a lista de manifestações filtradas que foram encaminhas pela ouvidoria.
	 * 
	 * @param ano
	 * @param numero
	 * @param dataInicial
	 * @param dataFinal
	 * @param categoriaAssuntoManifestacao
	 * @param paging
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Manifestacao> findManifestacoesEncaminhadasOuvidoria(Integer ano, String numero, Date dataInicial, Date dataFinal, CategoriaAssuntoManifestacao categoriaAssuntoManifestacao, String nome, Long matriculaSiape, Long cpf, PagingInformation paging) throws HibernateException, DAOException {
		String projecao = "m.id_manifestacao, m.numero, m.data_cadastro, m.titulo, am.id_assunto_manifestacao, am.descricao amDescricao, ca.descricao caDescricao, om.id_origem_manifestacao, om.descricao omDescricao, sm.id_status_manifestacao, sm.descricao smDescricao, u.nome , cs.id_categoria_solicitante, cs.descricao as categoria ";

		String hql = "SELECT DISTINCT " + projecao;
						
		String consulta = "FROM ouvidoria.historico_manifestacao h " +
								"LEFT JOIN comum.unidade u USING (id_unidade) " +
								"INNER JOIN ouvidoria.manifestacao m USING (id_manifestacao) " +
								"INNER JOIN ouvidoria.assunto_manifestacao am USING (id_assunto_manifestacao) " +
								"INNER JOIN ouvidoria.categoria_assunto_manifestacao ca USING (id_categoria_assunto_manifestacao) " +
								"INNER JOIN ouvidoria.origem_manifestacao om USING (id_origem_manifestacao) " +
								"INNER JOIN ouvidoria.status_manifestacao sm USING (id_status_manifestacao) " +
								"INNER JOIN ouvidoria.interessado_manifestacao i USING (id_interessado_manifestacao) " + 
								"INNER JOIN ouvidoria.categoria_solicitante cs USING (id_categoria_solicitante) " +
								"INNER JOIN ouvidoria.dados_interessado_manifestacao di USING (id_dados_interessado_manifestacao) " +
								"LEFT JOIN comum.pessoa p USING (id_pessoa) " +
								"LEFT JOIN ouvidoria.interessado_nao_autenticado ina USING (id_interessado_nao_autenticado) " + 
								"LEFT JOIN rh.servidor s USING (id_pessoa) " +
								"LEFT JOIN discente d USING (id_pessoa) " +
							"WHERE sm.id_status_manifestacao in " + UFRNUtils.gerarStringIn(StatusManifestacao.getAllStatusSemResposta())+
								"AND h.id_tipo_historico_manifestacao not in "+ UFRNUtils.gerarStringIn(TipoHistoricoManifestacao.getAllTiposEsclarecimento());

		if (isNotEmpty(ano))
			consulta += "AND " + SQLDialect.extractDate("m.data_cadastro", "year") + " = :ano ";
		if (isNotEmpty(numero))
			consulta += "AND m.numero like :numero ";
		
		if(isNotEmpty(dataInicial))
			consulta += " AND m.data_cadastro >= :dataInicial ";
		if(isNotEmpty(dataFinal))
			consulta += " AND m.data_cadastro <= :dataFinal ";
		
		if(isNotEmpty(categoriaAssuntoManifestacao))
			consulta += "AND ca.id_categoria_assunto_manifestacao = :categoriaAssunto ";
		
		if(isNotEmpty(nome)) {
			consulta += "AND p.nome_ascii ilike :nome OR ina.nome_ascii ilike :nome ";
		}
		
		if(isNotEmpty(matriculaSiape))
			consulta += "AND d.matricula = :matriculaSiape OR s.siape = :matriculaSiape ";
		
		if(isNotEmpty(cpf))
			consulta += "AND p.cpf_cnpj = :cpf ";

		hql += consulta + "ORDER BY m.data_cadastro asc, m.numero";

		Query q = getSession().createSQLQuery(hql);

		if (isNotEmpty(ano))
			q.setInteger("ano", ano);
		if (isNotEmpty(numero))
			q.setString("numero", numero);
		if(isNotEmpty(dataInicial))
			q.setDate("dataInicial", dataInicial);
		if(isNotEmpty(dataFinal))
			q.setDate("dataFinal", dataFinal);
		if(isNotEmpty(categoriaAssuntoManifestacao))
			q.setInteger("categoriaAssunto", categoriaAssuntoManifestacao.getId());
		if(isNotEmpty(nome)) {
			nome = StringUtils.toAscii(nome + "%");
			q.setString("nome", nome);
		}
		if(isNotEmpty(matriculaSiape))
			q.setLong("matriculaSiape", matriculaSiape);
		if(isNotEmpty(cpf))
			q.setLong("cpf", cpf);
		
		if(paging != null){
			String countHql = "SELECT count(distinct m.id_manifestacao) " + consulta;
			Query count = getSession().createSQLQuery(countHql );
			
			if (isNotEmpty(ano))
				count.setInteger("ano", ano);
			if (isNotEmpty(numero))
				count.setString("numero", numero + "%");
			if(isNotEmpty(dataInicial))
				count.setDate("dataInicial", dataInicial);
			if(isNotEmpty(dataFinal))
				count.setDate("dataFinal", dataFinal);
			if(isNotEmpty(categoriaAssuntoManifestacao))
				count.setInteger("categoriaAssunto", categoriaAssuntoManifestacao.getId());
			if(isNotEmpty(nome))
				count.setString("nome", nome);
			if(isNotEmpty(matriculaSiape))
				count.setLong("matriculaSiape", matriculaSiape);
			if(isNotEmpty(cpf))
				count.setLong("cpf", cpf);
	 
			paging.setTotalRegistros(((BigInteger) count.uniqueResult()).intValue());
			q.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			q.setMaxResults(paging.getTamanhoPagina());
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		Collection<Manifestacao> manifestacoes = new ArrayList<Manifestacao>();

		// Prioridade para a última alteração no histórico
		Integer idUltimaManifestacao = 0;
		Integer idNovaManifestacao = 0;
		
		for (Object[] linha : list) {
			Manifestacao m = new Manifestacao();
			m.setInteressadoManifestacao(new InteressadoManifestacao());

			int cont = 0;
			idNovaManifestacao = (Integer) linha[cont++];
			
			if (idNovaManifestacao.intValue() != idUltimaManifestacao.intValue()) {				
				m.setId(idNovaManifestacao);
				m.setNumero((String) linha[cont++]);
				m.setDataCadastro((Date) linha[cont++]);
				m.setTitulo((String) linha[cont++]);
				m.setAssuntoManifestacao(new AssuntoManifestacao((Integer) linha[cont++]));
				m.getAssuntoManifestacao().setDescricao((String) linha[cont++]);
				m.getAssuntoManifestacao().setCategoriaAssuntoManifestacao(new CategoriaAssuntoManifestacao());
				m.getAssuntoManifestacao().getCategoriaAssuntoManifestacao().setDescricao((String) linha[cont++]);
				m.setOrigemManifestacao(new OrigemManifestacao((Integer) linha[cont++]));
				m.getOrigemManifestacao().setDescricao((String) linha[cont++]);
				m.setStatusManifestacao(new StatusManifestacao((Integer) linha[cont++]));
				m.getStatusManifestacao().setDescricao((String) linha[cont++]);
				m.setUnidadeResponsavel(new Unidade());
				m.getUnidadeResponsavel().setNome((String) linha[cont++]);
				m.getInteressadoManifestacao().setCategoriaSolicitante(new CategoriaSolicitante((Integer) linha[cont++]));
				m.getInteressadoManifestacao().getCategoriaSolicitante().setDescricao((String) linha[cont++]);
				
				manifestacoes.add(m);
			}
			
			idUltimaManifestacao = idNovaManifestacao;
		}

		return manifestacoes;
	}
	
	/**
	 * Retorna a lista de manifestações para acompanhamento da unidade.
	 * 
	 * @param ano
	 * @param numero
	 * @param dataInicial
	 * @param dataFinal
	 * @param categoriaAssuntoManifestacao
	 * @param unidade
	 * @param paging
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Manifestacao> findManifestacoesAcompanhamentoUnidade(Integer ano, String numero, Date dataInicial, Date dataFinal, CategoriaAssuntoManifestacao categoriaAssuntoManifestacao, PagingInformation paging, UnidadeGeral ...unidade) throws HibernateException, DAOException {
		String projecao = "m.id, m.numero, m.dataCadastro, m.titulo, m.anonima, m.origemManifestacao, m.assuntoManifestacao, m.statusManifestacao, interessadoManifestacao.categoriaSolicitante";

		String hql = "SELECT " + projecao;
						
		String consulta = " FROM HistoricoManifestacao h " +
							"JOIN h.manifestacao m " +
							"JOIN m.interessadoManifestacao interessadoManifestacao " +
						" WHERE h.unidadeResponsavel.id in " + UFRNUtils.gerarStringIn(unidade);

		if (isNotEmpty(ano))
			consulta += "AND " + SQLDialect.extractDate("m.dataCadastro", "year")
					+ " = :ano ";
		if (isNotEmpty(numero))
			consulta += "AND m.numero like :numero ";
		
		if(isNotEmpty(dataInicial))
			consulta += " AND h.manifestacao.dataCadastro >= :dataInicial ";
		if(isNotEmpty(dataFinal))
			consulta += " AND h.manifestacao.dataCadastro <= :dataFinal ";
		
		if(isNotEmpty(categoriaAssuntoManifestacao))
			consulta += "AND m.assuntoManifestacao.categoriaAssuntoManifestacao.id = :categoriaAssunto ";

		hql += consulta + "ORDER BY m.dataCadastro desc";

		Query q = getSession().createQuery(hql);

		if (isNotEmpty(ano))
			q.setInteger("ano", ano);
		if (isNotEmpty(numero))
			q.setString("numero", numero + "%");
		if(isNotEmpty(dataInicial))
			q.setDate("dataInicial", dataInicial);
		if(isNotEmpty(dataFinal))
			q.setDate("dataFinal", dataFinal);
		if(isNotEmpty(categoriaAssuntoManifestacao))
			q.setInteger("categoriaAssunto", categoriaAssuntoManifestacao.getId());
		
		if(paging != null){
			String countHql = "SELECT count(*) " + consulta;
			Query count = getSession().createQuery(countHql );
			
			if (isNotEmpty(ano))
				count.setInteger("ano", ano);
			if (isNotEmpty(numero))
				count.setString("numero", numero + "%");
			if(isNotEmpty(dataInicial))
				count.setDate("dataInicial", dataInicial);
			if(isNotEmpty(dataFinal))
				count.setDate("dataFinal", dataFinal);
			if(isNotEmpty(categoriaAssuntoManifestacao))
				count.setInteger("categoriaAssunto", categoriaAssuntoManifestacao.getId());
	 
			paging.setTotalRegistros(((Long) count.uniqueResult()).intValue());
			q.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			q.setMaxResults(paging.getTamanhoPagina());
		}

		@SuppressWarnings("unchecked")
		List<Object[]> manifestacoes = q.list();

		return HibernateUtils.parseTo(manifestacoes, projecao, Manifestacao.class, "m");
	}

	/**
	 * Retorna uma lista de manifestações para acompanhamento de um designado.
	 * 
	 * @param ano
	 * @param numero
	 * @param dataInicial
	 * @param dataFinal
	 * @param categoriaAssuntoManifestacao
	 * @param pessoa
	 * @param paging
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Manifestacao> findManifestacoesAcompanhamentoDesignado(Integer ano, String numero, Date dataInicial, Date dataFinal, CategoriaAssuntoManifestacao categoriaAssuntoManifestacao, Integer pessoa, PagingInformation paging) throws HibernateException, DAOException {
		String projecao = "m.id, m.numero, m.dataCadastro, m.titulo, m.anonima, m.origemManifestacao, m.assuntoManifestacao, m.statusManifestacao, interessadoManifestacao.categoriaSolicitante";

		String hql = "SELECT " + projecao;
		
		String consulta = " FROM DelegacaoUsuarioResposta d " +
							" INNER JOIN d.historicoManifestacao h " +
							" INNER JOIN h.manifestacao m " +
							" JOIN m.interessadoManifestacao interessadoManifestacao " +
						" WHERE d.ativo = " + SQLDialect.TRUE +
						" AND d.pessoa.id = :pessoa ";

		if (isNotEmpty(ano))
			consulta += "AND " + SQLDialect.extractDate("m.dataCadastro", "year")
					+ " = :ano ";
		if (isNotEmpty(numero))
			consulta += "AND m.numero like :numero ";
		
		if(isNotEmpty(dataInicial))
			consulta += " AND h.manifestacao.dataCadastro >= :dataInicial ";
		if(isNotEmpty(dataFinal))
			consulta += " AND h.manifestacao.dataCadastro <= :dataFinal ";
		
		if(isNotEmpty(categoriaAssuntoManifestacao))
			consulta += "AND m.assuntoManifestacao.categoriaAssuntoManifestacao.id = :categoriaAssunto ";

		hql += consulta + "ORDER BY m.dataCadastro desc";

		Query q = getSession().createQuery(hql);

		q.setInteger("pessoa", pessoa);

		if (isNotEmpty(ano))
			q.setInteger("ano", ano);
		if (isNotEmpty(numero))
			q.setString("numero", numero + "%");
		if(isNotEmpty(dataInicial))
			q.setDate("dataInicial", dataInicial);
		if(isNotEmpty(dataFinal))
			q.setDate("dataFinal", dataFinal);
		if(isNotEmpty(categoriaAssuntoManifestacao))
			q.setInteger("categoriaAssunto", categoriaAssuntoManifestacao.getId());
		
		if(paging != null){
			String countHql = "SELECT count(*) " + consulta;
			Query count = getSession().createQuery(countHql );
			
			count.setInteger("pessoa", pessoa);

			if (isNotEmpty(ano))
				count.setInteger("ano", ano);
			if (isNotEmpty(numero))
				count.setString("numero", numero + "%");
			if(isNotEmpty(dataInicial))
				count.setDate("dataInicial", dataInicial);
			if(isNotEmpty(dataFinal))
				count.setDate("dataFinal", dataFinal);
			if(isNotEmpty(categoriaAssuntoManifestacao))
				count.setInteger("categoriaAssunto", categoriaAssuntoManifestacao.getId());
	 
			paging.setTotalRegistros(((Long) count.uniqueResult()).intValue());
			q.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			q.setMaxResults(paging.getTamanhoPagina());
		}

		@SuppressWarnings("unchecked")
		List<Object[]> manifestacoes = q.list();

		return HibernateUtils.parseTo(manifestacoes, projecao, Manifestacao.class, "m");
	}

	/**
	 * Retorna as manifestações cadastradas pela pessoa passada de acordo com seu ano e número.
	 * 
	 * @param ano
	 * @param numero
	 * @param pessoa
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Manifestacao> findManifestacoesByAnoNumeroPessoa(Integer ano, String numero, Integer pessoa) throws HibernateException, DAOException {
		String projecao = "m.id, m.numero, m.dataCadastro, m.titulo, m.origemManifestacao, m.assuntoManifestacao, m.statusManifestacao, interessadoManifestacao.categoriaSolicitante ";
		String hql = "SELECT " + projecao;
		
		hql += 		" FROM Manifestacao m " +
						"JOIN m.interessadoManifestacao interessadoManifestacao " +
						"JOIN interessadoManifestacao.dadosInteressadoManifestacao dadosInteressadoManifestacao " +
						"JOIN dadosInteressadoManifestacao.pessoa pessoa " +
					"WHERE pessoa.id = :pessoa ";

		if (isNotEmpty(ano))
			hql += "AND " + SQLDialect.extractDate("m.dataCadastro", "year") + " = :ano ";
		if (isNotEmpty(numero))
			hql += "AND m.numero like :numero ";

		hql += "ORDER BY m.dataCadastro desc";

		Query q = getSession().createQuery(hql);

		q.setInteger("pessoa", pessoa);

		if (isNotEmpty(ano))
			q.setInteger("ano", ano);
		if (isNotEmpty(numero))
			q.setString("numero", numero + "%");

		@SuppressWarnings("unchecked")
		List<Object[]> manifestacoes = q.list();

		return HibernateUtils.parseTo(manifestacoes, projecao, Manifestacao.class, "m");
	}
	
	/**
	 * Retorna uma lista de manifestações copiadas para a pessoa ou unidade passadas.
	 * 
	 * @param ano
	 * @param numero
	 * @param pessoa
	 * @param unidade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Manifestacao> findManifestacoesCopiadas(Integer ano, String numero, int pessoa, UnidadeGeral ...unidades) throws HibernateException, DAOException {
		String projecao = "m.id, m.numero, m.dataCadastro, m.titulo, m.anonima, m.origemManifestacao, m.assuntoManifestacao, m.statusManifestacao, interessadoManifestacao.categoriaSolicitante ";
		
		String hql = "SELECT " + projecao;
		
		hql += " FROM AcompanhamentoManifestacao am " +
					"JOIN am.manifestacao m " +
					"JOIN m.interessadoManifestacao interessadoManifestacao " +
					"LEFT JOIN am.pessoa pessoa " +
					"LEFT JOIN am.unidadeResponsabilidade unidade " +
				"WHERE (pessoa.id = :pessoa";
		
		if(isNotEmpty(unidades))
			hql += " OR unidade.id in " + UFRNUtils.gerarStringIn(unidades);
		
		hql += ") ";
		
		if (isNotEmpty(ano))
			hql += "AND " + SQLDialect.extractDate("m.dataCadastro", "year") + " = :ano ";
		if (isNotEmpty(numero))
			hql += "AND m.numero like :numero ";

		hql += "ORDER BY m.dataCadastro desc";

		Query q = getSession().createQuery(hql);

		q.setInteger("pessoa", pessoa);

		if (isNotEmpty(ano))
			q.setInteger("ano", ano);
		if (isNotEmpty(numero))
			q.setString("numero", numero + "%");

		@SuppressWarnings("unchecked")
		List<Object[]> manifestacoes = q.list();

		return HibernateUtils.parseTo(manifestacoes, projecao, Manifestacao.class, "m");
	}
	
	/**
	 * Método que retonar todas as manifestações pendentes de análise que estão ou estarão expirados e que foram designadas,
	 * contando do dia atual até o número de dias passado como parâmetro.
	 * @return List(Map(String, Object))
	 * @throws DAOException
	 */
	public List<NotificacaoManifestacaoPendente> findAllManifestacoesPendentes(int diasRestantes) throws DAOException {
		
		String projecao = "pc.id_pessoa as idResponsavel, pc.nome as nome_responsavel, pc.email as email_responsavel, p.id_pessoa as idDesignado, p.nome, p.email, h.prazo_resposta, m.numero, m.data_cadastro, m.titulo, m.id_status_manifestacao , a.descricao "; 
		
		String sql = "SELECT "+projecao+" FROM ouvidoria.historico_manifestacao h " + 
					 "LEFT JOIN ouvidoria.delegacao_usuario_resposta d using (id_historico_manifestacao) " +
					 "LEFT JOIN comum.pessoa p using (id_pessoa) " +
					 "INNER JOIN ouvidoria.manifestacao m using (id_manifestacao) " +
					 "INNER JOIN ouvidoria.assunto_manifestacao a using (id_assunto_manifestacao) "+
					 "INNER JOIN ouvidoria.status_manifestacao using (id_status_manifestacao) " +
					 "INNER JOIN comum.responsavel_unidade ru using (id_unidade) " +
					 "INNER JOIN rh.servidor s using  (id_servidor) " +
					 "INNER JOIN comum.pessoa pc on s.id_pessoa = pc.id_pessoa " +
					 "WHERE id_status_manifestacao in " + UFRNUtils.gerarStringIn(new int[] {StatusManifestacao.ENCAMINHADA_UNIDADE, StatusManifestacao.DESIGNADA_RESPONSAVEL, StatusManifestacao.AGUARDANDO_PARECER}) +
						 " AND h.prazo_resposta <= :vencimento " +
						 "AND ru.id_registro_entrada_exclusao is null " +
						 "AND ru.data_inicio <= :hoje " +
						 "AND (ru.data_fim IS NULL OR ru.data_fim >= :hoje) " +
						 "AND ru.nivel_responsabilidade = 'C' " +
						 "AND h.data_resposta is null " +
						 "ORDER BY pc.id_pessoa , p.nome nulls first , m.data_cadastro ";
		
		Query q = getSession().createSQLQuery(sql);
		
		Calendar dataVencimento = new GregorianCalendar();
		dataVencimento.add(Calendar.DAY_OF_MONTH, diasRestantes);
		
		q.setDate("vencimento", dataVencimento.getTime());
		q.setDate("hoje", new Date());
				
		@SuppressWarnings("unchecked")
		List<Object[]> linhas = q.list();
		
		List<NotificacaoManifestacaoPendente> notificacoes = new ArrayList<NotificacaoManifestacaoPendente>();
		List<DelegacaoUsuarioResposta> listaManifestacoes = new ArrayList<DelegacaoUsuarioResposta>();
		Integer oldIdResponsavel = 0;
		Integer newIdResponsavel = 0;
		
		for (Object[] linha: linhas) {
			
			Integer i = 0;
			newIdResponsavel = (Integer)linha[i++];
			
			if ( newIdResponsavel.intValue() != oldIdResponsavel.intValue() ){
				
				DelegacaoUsuarioResposta dUR = new DelegacaoUsuarioResposta();

				Pessoa r = new Pessoa();
				r.setId(newIdResponsavel);
				r.setNome((String)linha[i++]);
				r.setEmail((String)linha[i++]);
				
				Pessoa d = null;
				Integer idDesignado = (Integer)linha[i++];
				String nomeDesignado = (String)linha[i++];
				String emailDesignado = (String)linha[i++];

				if (idDesignado!=null){
					d = new Pessoa();
					d.setId(idDesignado);
					d.setNome(nomeDesignado);
					d.setEmail(emailDesignado);
					dUR.setPessoa(d);
				}
				
				HistoricoManifestacao h = new HistoricoManifestacao();
				h.setPrazoResposta((Date)linha[i++]);
				
				Manifestacao m = new Manifestacao();
				m.setNumero((String)linha[i++]);
				m.setDataCadastro((Date)linha[i++]);
				m.setTitulo((String)linha[i++]);
				
				StatusManifestacao s = new StatusManifestacao();
				s.setId((Integer)linha[i++]);
				
				AssuntoManifestacao a = new AssuntoManifestacao();
				a.setDescricao((String)linha[i++]);
				
				listaManifestacoes = new ArrayList<DelegacaoUsuarioResposta>();
				m.setStatusManifestacao(s);
				m.setAssuntoManifestacao(a);
				h.setManifestacao(m);
				dUR.setHistoricoManifestacao(h);
				listaManifestacoes.add(dUR);
				
				NotificacaoManifestacaoPendente n = new NotificacaoManifestacaoPendente();
				n.setResponsavel(r);
				n.setManifestacoes(listaManifestacoes);
				
				notificacoes.add(n);
			} else if ( newIdResponsavel.intValue() == oldIdResponsavel.intValue() ) {
				
				i = 3;
				DelegacaoUsuarioResposta dUR = new DelegacaoUsuarioResposta();
				
				Pessoa d = null;
				Integer idDesignado = (Integer)linha[i++];
				String nomeDesignado = (String)linha[i++];
				String emailDesignado = (String)linha[i++];

				if (idDesignado!=null){
					d = new Pessoa();
					d.setId(idDesignado);
					d.setNome(nomeDesignado);
					d.setEmail(emailDesignado);
					dUR.setPessoa(d);
				}
				
				HistoricoManifestacao h = new HistoricoManifestacao();
				h.setPrazoResposta((Date)linha[i++]);
				
				Manifestacao m = new Manifestacao();
				m.setNumero((String)linha[i++]);
				m.setDataCadastro((Date)linha[i++]);
				m.setTitulo((String)linha[i++]);
				
				StatusManifestacao s = new StatusManifestacao();
				s.setId((Integer)linha[i++]);
				
				AssuntoManifestacao a = new AssuntoManifestacao();
				a.setDescricao((String)linha[i++]);
				
				m.setStatusManifestacao(s);
				m.setAssuntoManifestacao(a);
				h.setManifestacao(m);
				dUR.setHistoricoManifestacao(h);
				listaManifestacoes.add(dUR);
				
			}

			oldIdResponsavel = newIdResponsavel;
			
		}
		return notificacoes;
		
	}

	/**
	 * Retorna as manifestações cadastradas pela pessoa passada de acordo com seu ano e número.
	 * 
	 * @param ano
	 * @param numero
	 * @param pessoa
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Manifestacao> findManifestacoesByEmailCodigoAcesso(String email, String senha) throws HibernateException, DAOException {
		
		String projecao = "m.id, m.numero, m.dataCadastro, m.titulo, m.origemManifestacao, m.assuntoManifestacao, m.statusManifestacao, interessadoManifestacao.categoriaSolicitante ";
		String hql = "SELECT " + projecao;
		
		hql += 		" FROM Manifestacao m " +
						"JOIN m.interessadoManifestacao interessadoManifestacao " +
						"JOIN interessadoManifestacao.dadosInteressadoManifestacao dadosInteressadoManifestacao " +
						"JOIN dadosInteressadoManifestacao.interessadoNaoAutenticado interessadoNaoAutenticado " +
					"WHERE upper(interessadoNaoAutenticado.email) = :email AND interessadoNaoAutenticado.senha = :senha "+
					"ORDER BY m.dataCadastro desc";

		Query q = getSession().createQuery(hql);
		q.setString("email", StringUtils.toAscii(email.toUpperCase()));
		q.setString("senha", senha);

		@SuppressWarnings("unchecked")
		List<Object[]> manifestacoes = q.list();

		return HibernateUtils.parseTo(manifestacoes, projecao, Manifestacao.class, "m");
	}
	
}
