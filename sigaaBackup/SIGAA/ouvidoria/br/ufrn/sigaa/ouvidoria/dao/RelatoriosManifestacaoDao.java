package br.ufrn.sigaa.ouvidoria.dao;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ouvidoria.dominio.AssuntoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaAssuntoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaSolicitante;
import br.ufrn.sigaa.ouvidoria.dominio.DelegacaoUsuarioResposta;
import br.ufrn.sigaa.ouvidoria.dominio.HistoricoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.InteressadoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.OrigemManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.StatusManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.TipoManifestacao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Dao responsável por consultas de geração de relatórios da Ouvidoria.
 * 
 * @author Bernardo
 *
 */
public class RelatoriosManifestacaoDao extends GenericSigaaDAO {
	
	/**
	 * Retorna o conjunto de históricos de acordo com os dados passados para montagem dos relatórios gerais de manifestações.
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @param categoriaSolicitante
	 * @param origemManifestacao
	 * @param statusManifestacao
	 * @param categoriaAssuntoManifestacao
	 * @param assuntoManifestacao
	 * @param unidade
	 * @param somenteNaoRespondidas
	 * @return
	 * @throws DAOException
	 */
	public Collection<HistoricoManifestacao> getManifestacoesRelatorioGeral(Date dataInicial, Date dataFinal, CategoriaSolicitante categoriaSolicitante,
			OrigemManifestacao origemManifestacao, StatusManifestacao statusManifestacao, CategoriaAssuntoManifestacao categoriaAssuntoManifestacao,
			AssuntoManifestacao assuntoManifestacao, Unidade unidade, boolean somenteNaoRespondidas)
			throws DAOException {
		String projecao = "manifestacao.id, manifestacao.numero, manifestacao.dataCadastro, manifestacao.titulo, manifestacao.statusManifestacao, manifestacao.origemManifestacao, " +
						"manifestacao.assuntoManifestacao, manifestacao.interessadoManifestacao.categoriaSolicitante, h.prazoResposta, h.dataResposta, " +
						"h.unidadeResponsavel.id, h.unidadeResponsavel.nome, delegacoesUsuarioResposta.pessoa.id, delegacoesUsuarioResposta.pessoa.nome ";
		
		String hql = "SELECT " + projecao + " FROM HistoricoManifestacao h " +
							"LEFT JOIN h.delegacoesUsuarioResposta delegacoesUsuarioResposta with delegacoesUsuarioResposta.ativo = " + SQLDialect.TRUE + " " +
							"LEFT JOIN delegacoesUsuarioResposta.pessoa " +
							"LEFT JOIN h.unidadeResponsavel " +
							"RIGHT JOIN h.manifestacao manifestacao " +
							"INNER JOIN manifestacao.interessadoManifestacao " +
					"WHERE 1=1 ";
		
		if(isNotEmpty(categoriaSolicitante))
		    hql += "AND manifestacao.interessadoManifestacao.categoriaSolicitante.id = :categoriaSolicitante ";
		if(isNotEmpty(origemManifestacao))
		    hql += "AND manifestacao.origemManifestacao.id = :origem ";
		if(isNotEmpty(statusManifestacao))
		    hql += "AND manifestacao.statusManifestacao.id = :status ";
		if(isNotEmpty(categoriaAssuntoManifestacao))
		    hql += "AND manifestacao.assuntoManifestacao.categoriaAssuntoManifestacao.id = :categoriaAssunto ";
		if(isNotEmpty(assuntoManifestacao))
		    hql += "AND manifestacao.assuntoManifestacao.id = :assunto ";
		if(isNotEmpty(dataInicial))
		    hql += "AND manifestacao.dataCadastro >= :dataInicial ";
		if(isNotEmpty(dataFinal))
		    hql += " AND manifestacao.dataCadastro <= :dataFinal ";
		if(isNotEmpty(unidade))
		    hql += " AND h.unidadeResponsavel.id = :unidade ";
		if(somenteNaoRespondidas) {
			hql += "AND h.resposta is null ";
		}
				
		hql += " ORDER BY manifestacao.dataCadastro, manifestacao.numero";
		
		Query q = getSession().createQuery(hql);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		
		if(isNotEmpty(categoriaSolicitante))
		    q.setInteger("categoriaSolicitante", categoriaSolicitante.getId());
		if(isNotEmpty(origemManifestacao))
		    q.setInteger("origem", origemManifestacao.getId());
		if(isNotEmpty(statusManifestacao))
		    q.setInteger("status", statusManifestacao.getId());
		if(isNotEmpty(assuntoManifestacao))
		    q.setInteger("assunto", assuntoManifestacao.getId());
		if(isNotEmpty(categoriaAssuntoManifestacao))
		    q.setInteger("categoriaAssunto", categoriaAssuntoManifestacao.getId());
		if(isNotEmpty(dataInicial)) {
		    cal.setTime(dataInicial);
		    q.setDate("dataInicial", cal.getTime());
		}
		if(isNotEmpty(dataFinal)) {
		    cal.setTime(dataFinal);
		    cal.add(Calendar.DATE, 1);
		    q.setDate("dataFinal", cal.getTime());
		}
		if(isNotEmpty(unidade))
			q.setInteger("unidade", unidade.getId());
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		Map<Integer, HistoricoManifestacao> historicos = new HashMap<Integer, HistoricoManifestacao>();
	
		for (Object[] linha : list) {
		    HistoricoManifestacao h = new HistoricoManifestacao();
		    int cont = 0;
		    
		    h.setManifestacao(new Manifestacao());
		    h.getManifestacao().setId((Integer) linha[cont++]);
		    
		    if(historicos.containsKey(h.getManifestacao().getId()))
		    	continue;
		    
		    h.getManifestacao().setNumero((String) linha[cont++]);
		    h.getManifestacao().setDataCadastro((Date) linha[cont++]);
		    h.getManifestacao().setTitulo((String) linha[cont++]);
		    h.getManifestacao().setStatusManifestacao((StatusManifestacao) linha[cont++]);
		    h.getManifestacao().setOrigemManifestacao((OrigemManifestacao) linha[cont++]);
		    h.getManifestacao().setAssuntoManifestacao((AssuntoManifestacao) linha[cont++]);
		    h.getManifestacao().setInteressadoManifestacao(new InteressadoManifestacao());
		    h.getManifestacao().getInteressadoManifestacao().setCategoriaSolicitante((CategoriaSolicitante) linha[cont++]);
		    
		    h.setPrazoResposta((Date) linha[cont++]);
		    h.setDataResposta((Date) linha[cont++]);
		    
		    if(isNotEmpty(linha[cont])) {
		    	h.setUnidadeResponsavel(new Unidade());
		    	h.getUnidadeResponsavel().setId((Integer) linha[cont++]);
		    	h.getUnidadeResponsavel().setNome((String) linha[cont++]);
		    }
		    else {
		    	cont += 2;
		    }
		    
		    if(isNotEmpty(linha[cont])) {
	        	    DelegacaoUsuarioResposta delegacaoUsuarioResposta = new DelegacaoUsuarioResposta();
	        	    delegacaoUsuarioResposta.setPessoa(new Pessoa());
	        	    delegacaoUsuarioResposta.getPessoa().setId((Integer) linha[cont++]);
	        	    delegacaoUsuarioResposta.getPessoa().setNome((String) linha[cont++]);
	        	    
	        	    h.adicionarDelegacaoUsuarioResposta(delegacaoUsuarioResposta);
		    }
		    
			
		    historicos.put(h.getManifestacao().getId(), h);
		}
		
		return historicos.values();
	}

	/**
	 * Retorna o conjunto de manifestações a serem mostradas no quadro geral.
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 * @throws DAOException
	 */
    public Collection<HistoricoManifestacao> getManifestacoesQuadroGeral(Date dataInicial, Date dataFinal) throws DAOException {
		String projecao = "manifestacao.id, manifestacao.numero, manifestacao.dataCadastro, manifestacao.tipoManifestacao, manifestacao.origemManifestacao, " +
						"manifestacao.statusManifestacao, manifestacao.assuntoManifestacao, manifestacao.interessadoManifestacao.categoriaSolicitante, h.id, h.prazoResposta, h.lido, h.resposta, " +
						"h.unidadeResponsavel.id, h.unidadeResponsavel.nome, delegacoesUsuarioResposta.pessoa.id, delegacoesUsuarioResposta.pessoa.nome ";
		
		String hql = "SELECT " + projecao + " FROM HistoricoManifestacao h " +
		    				"LEFT JOIN h.delegacoesUsuarioResposta delegacoesUsuarioResposta WITH (delegacoesUsuarioResposta.ativo = " + SQLDialect.TRUE + ") " +
		    				"LEFT JOIN delegacoesUsuarioResposta.pessoa " +
		    				"LEFT JOIN h.unidadeResponsavel " +
		    				"RIGHT JOIN h.manifestacao manifestacao " +
					"WHERE 1 = 1 ";
		
		if(isNotEmpty(dataInicial))
		    hql += " AND manifestacao.dataCadastro >= :dataInicial ";
		if(isNotEmpty(dataFinal))
		    hql += " AND manifestacao.dataCadastro <= :dataFinal ";
		
		hql += " ORDER BY manifestacao.dataCadastro ";
		
		Query q = getSession().createQuery(hql);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		
		if(isNotEmpty(dataInicial)) {
		    cal.setTime(dataInicial);
		    q.setDate("dataInicial", cal.getTime());
		}
		if(isNotEmpty(dataFinal)) {
		    cal.setTime(dataFinal);
		    cal.add(Calendar.DATE, 1);
		    q.setDate("dataFinal", cal.getTime());
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		Map<Integer, HistoricoManifestacao> historicos = new HashMap<Integer, HistoricoManifestacao>();
		
		for (Object[] linha : list) {
		    HistoricoManifestacao h = new HistoricoManifestacao();
		    int cont = 0;
		    
		    h.setManifestacao(new Manifestacao());
		    h.getManifestacao().setId((Integer) linha[cont++]);
		    
		    if(historicos.containsKey(h.getManifestacao().getId())) {
				h = historicos.get(h.getManifestacao().getId());
				//pula para a posição do atributo 'h.lido'
				cont = 10;
				h.getManifestacao().setLida(h.getManifestacao().isLida() && (Boolean) linha[cont]);
		    }
		    else {
				h.getManifestacao().setNumero((String) linha[cont++]);
				h.getManifestacao().setDataCadastro((Date) linha[cont++]);
				h.getManifestacao().setTipoManifestacao((TipoManifestacao) linha[cont++]);
				h.getManifestacao().setOrigemManifestacao((OrigemManifestacao) linha[cont++]);
				h.getManifestacao().setStatusManifestacao((StatusManifestacao) linha[cont++]);
				h.getManifestacao().setAssuntoManifestacao((AssuntoManifestacao) linha[cont++]);
				h.getManifestacao().setInteressadoManifestacao(new InteressadoManifestacao());
				h.getManifestacao().getInteressadoManifestacao().setCategoriaSolicitante((CategoriaSolicitante) linha[cont++]);
				
				//Verifica se existe histórico cadastrado para a manifestação
				if(isNotEmpty(linha[cont])) {
		        		h.setId((Integer) linha[cont++]);
		        		h.setPrazoResposta((Date) linha[cont++]);
		        		Boolean lido = (Boolean) linha[cont++];
		        		h.getManifestacao().setLida(lido);
		        		h.setLido(lido);
		        		h.setResposta((String) linha[cont++]);
		        		//Verifica se o histórico possui unidade responsável ou foi respondido pela ouvidoria
		        		if(isNotEmpty(linha[cont])) {
		        		    h.setUnidadeResponsavel(new Unidade());
		        		    h.getUnidadeResponsavel().setId((Integer) linha[cont++]);
		        		    h.getUnidadeResponsavel().setNome((String) linha[cont++]);
		        		} else {
		        		    //atualizando o valor do contador
		        		    cont += 2;
		        		    h.setUnidadeResponsavel(getUnidadeOuvidoria());
		        		}
		        		//Verifica se houve alguma delegação de pessoa para resposta da manifestação
		        		if(isNotEmpty(linha[cont])) {
		        		    DelegacaoUsuarioResposta delegacaoUsuarioResposta = new DelegacaoUsuarioResposta();
		        		    delegacaoUsuarioResposta.setPessoa(new Pessoa());
		        		    delegacaoUsuarioResposta.getPessoa().setId((Integer) linha[cont++]);
		        		    delegacaoUsuarioResposta.getPessoa().setNome((String) linha[cont++]);
		        		    
		        		    h.adicionarDelegacaoUsuarioResposta(delegacaoUsuarioResposta);
		        		}
				}
				historicos.put(h.getManifestacao().getId(), h);
		    }
		}
		
		return historicos.values();
    }
    
    /**
     * Retorna a Unidade da Ouvidoria.
     * 
     * @return
     * @throws DAOException
     */
    private Unidade getUnidadeOuvidoria() throws DAOException {
		String projecao = "id, nome";
		String hql = "SELECT " + projecao + " FROM Unidade WHERE nome = 'OUVIDORIA'";
		
		Query q = getSession().createQuery(hql);
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		
		for (Object[] linha : list) {
		    Unidade u = new Unidade();
		    
		    u.setId((Integer) linha[0]);
		    u.setNome((String) linha[1]);
		    
		    return u;
		}
		
		return null;
    }
}
