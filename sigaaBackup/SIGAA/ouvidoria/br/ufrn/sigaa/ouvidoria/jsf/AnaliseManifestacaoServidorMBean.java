package br.ufrn.sigaa.ouvidoria.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.ouvidoria.dao.AcompanhamentoManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dao.DelegacaoUsuarioRespostaDao;
import br.ufrn.sigaa.ouvidoria.dao.HistoricoManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dao.ManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;

/**
 * Controller para operações de análise de manifestações feitas por um servidor.
 * 
 * @author Bernardo
 *
 */
@Component(value="analiseManifestacaoServidor") @Scope(value="request")
public class AnaliseManifestacaoServidorMBean extends AnaliseManifestacaoAbstractController {
	
	/** Coleção de manifestações copiadas para a pessoa do servidor. */
	private Collection<Manifestacao> manifestacoesCopiadas;
    
    public AnaliseManifestacaoServidorMBean() {
    	init();
    }
    
    /**
     * Direciona o fluxo para a tela de acompanhar manifestações.<br />
     * Método não invocado por JSPs.
     * 
     * @return
     */
    public String acompanharManifestacao() {
    	return forward(getDirBase() + "/acompanhar.jsp");
    }
    
    /**
     * Lista as manifestações cadastradas e copiadas, e direciona o usuário para a página de acompanhamento.<br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/anifestacao/servidor/detalhes_manifestacao.jsp</li>
     * </ul>
     * 
     * @return
     * @throws DAOException
     */
    public String listarManifestacoes() throws DAOException {
		popularManifestacoesCadastradas();
		popularManifestacoesCopiadas();
		
		return acompanharManifestacao();
    }

    /**
     * Popula a lista com as manifestações cadastradas peo servidor.
     * 
     * @throws DAOException
     */
	private void popularManifestacoesCadastradas() throws DAOException {
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		
		manifestacoes = dao.findManifestacoesByAnoNumeroPessoa(ano, numero, getUsuarioLogado().getPessoa().getId());
	}
	
	/**
	 * Popula a lista de manifestações copiadas para o servidor.
	 * 
	 * @throws DAOException
	 */
	private void popularManifestacoesCopiadas() throws DAOException {
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		
		manifestacoesCopiadas = dao.findManifestacoesCopiadas(ano, numero, getUsuarioLogado().getPessoa().getId(), getAllUnidadesResponsabilidadeUsuario().toArray(new UnidadeGeral[0]));
	}
    
    @Override
    public void detalharManifestacao(ActionEvent evt) throws DAOException {
		Integer id = (Integer) evt.getComponent().getAttributes().get("idManifestacao");
		
		detalharManifestacao(id, true);
    }
    
    /**
     * Detalha a manifestação informada, direcionando o fluxo para a tela de detalhes da manifestação
     * de acordo com o parametro informado.
     * 
     * @param id
     * @param forward
     * @throws DAOException
     */
    private void detalharManifestacao(Integer id, boolean forward) throws DAOException {
		HistoricoManifestacaoDao dao = getDAO(HistoricoManifestacaoDao.class);
		AcompanhamentoManifestacaoDao acompanhamentoDao = getDAO(AcompanhamentoManifestacaoDao.class);
		DelegacaoUsuarioRespostaDao delegacaoDao = getDAO(DelegacaoUsuarioRespostaDao.class);
		
		try {
		    obj = getManifestacaoById(id);
		    
		    obj = dao.refresh(obj);
		    
		    if(!isEmpty(obj)) {
		    	if(manifestacoesCopiadas.contains(obj)) { //Se a manifestação está copiada para a pessoa, permite visualização completa do histórico
		    		historicos = dao.getAllHistoricosByManifestacao(obj.getId());
		    		
		    		copias = acompanhamentoDao.findAllAcompanhamentosByManifestacao(obj.getId());
		    		
		    		delegacoes = delegacaoDao.findAllDelegacoesByManifestacao(obj.getId());
		    	}
		    	else //Caso contrário, a manifestação foi cadastrada pela pessoa, e ela só deve ter acesso ao histórico de resposta da ouvidoria
		    		historicos = dao.getAllHistoricosVisiveisInteressadoByManifestacao(obj.getId());
		    }
		} finally {
		    dao.close();
		    acompanhamentoDao.close();
		    delegacaoDao.close();
		}
		
		if(forward)
		    forward(getDirBase() + JSP_DETALHES_MANIFESTACAO);
    }
    
    /**
     * Recupera uma manifestação previamente listada de acordo com seu id.
     * 
     * @param id
     * @return
     */
    public Manifestacao getManifestacaoById(int id) {
		Manifestacao manifestacao = new Manifestacao();
		
		if(isNotEmpty(getTodasManifestacoes())) {
			for (Manifestacao m : getTodasManifestacoes()) {
			    if(m.getId() == id) {
					manifestacao = m;
					break;
			    }
			}
		}
		
		return manifestacao;
    }
    
    /**
     * Direciona o usuário para a tela de seleção de módulos do sistema.<br />
     * Método não invocado por JSPs.
     * 
     * @return
     */
    public String redirectMenuPrincipal() {
		return redirect("/sigaa/verMenuPrincipal.do");
	}
    
    /**
     * Retorna o total de manifestaçãoes que o usuário acompanha.
     * 
     * @return
     */
    public int getTotalManifestacoes() {
    	int cadastradas = getManifestacoes() != null ? manifestacoes.size() : 0;
    	int copiadas = getManifestacoesCopiadas() != null ? manifestacoesCopiadas.size() : 0;
    	
    	return cadastradas + copiadas;
    }
    
    /**
     * Retorna todas as manifestações cadastradas e copiadas para o servidor.
     * 
     * @return
     */
	protected Collection<Manifestacao> getTodasManifestacoes() {
    	Collection<Manifestacao> todas = new ArrayList<Manifestacao>();
    	
    	todas.addAll(getManifestacoes());
    	todas.addAll(getManifestacoesCopiadas());
    	
		return todas;
	}
    
	/**
	 * Retorna as manifestações copiadas para o servidor.
	 * 
	 * @return
	 */
    public Collection<Manifestacao> getManifestacoesCopiadas() {
    	if(isEmpty(manifestacoesCopiadas)) {
    		try {
    			popularManifestacoesCopiadas();
    		} catch (DAOException ex) {
				addMensagemErro(ex.getMessage());
			}
    	}
    	
		return manifestacoesCopiadas;
	}

	public void setManifestacoesCopiadas(Collection<Manifestacao> manifestacoesCopiadas) {
		this.manifestacoesCopiadas = manifestacoesCopiadas;
	}
	
    @Override
	public String cancelar() {
		resetBean();
		
		return redirectMenuPrincipal();
	}
    
    @Override
    public String getDirBase() {
    	String dirBase = super.getDirBase();
    	
    	if(getSubSistema().getId() != SigaaSubsistemas.OUVIDORIA.getId()) {
    		dirBase += "/ouvidoria/Manifestacao";
    	}
    	dirBase += "/servidor";
    	
		return dirBase;
    }

    @Override
    public Collection<Manifestacao> getManifestacoes() {
    	if(isEmpty(manifestacoes)) {
    		try {
    			popularManifestacoesCadastradas();
    		} catch (DAOException ex) {
				addMensagemErro(ex.getMessage());
			}
    	}
    	
        return manifestacoes;
    }

}
