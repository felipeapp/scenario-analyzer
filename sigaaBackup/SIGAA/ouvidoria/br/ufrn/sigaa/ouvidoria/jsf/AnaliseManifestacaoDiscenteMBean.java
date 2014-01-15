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
 * Controller respons�vel pelas opera��es de an�lise de manifesta��es por um discente.
 * 
 * @author Bernardo
 *
 */
@Component(value="analiseManifestacaoDiscente") @Scope(value="request")
public class AnaliseManifestacaoDiscenteMBean extends AnaliseManifestacaoAbstractController {
	
	/** Armazena as manifesta��es copiadas para a pessoa logada. */
	private Collection<Manifestacao> manifestacoesCopiadas;
    
    public AnaliseManifestacaoDiscenteMBean() {
    	init();
    }
    
    /**
     * Direciona o usu�rio para a p�gina de acompanhamento de suas manifesta��es cadastradas e copiadas.<br />
     * M�todo n�o invocado por JSPs.
     * 
     * @return
     */
    public String acompanharManifestacao() {
    	return forward(getDirBase() + "/acompanhar.jsp");
    }
    
    /**
     * Lista as manifesta��es cadastradas e copiadas, e direciona o usu�rio para a tela de acompanhamento.<br />
     * M�todo invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/discente/detalhes_manifestacao.jsp</li>
     * <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
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
     * Popula a lista de manifesta��es cadastradas pelo usu�rio.
     * 
     * @throws DAOException
     */
	private void popularManifestacoesCadastradas() throws DAOException {
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		
		manifestacoes = dao.findManifestacoesByAnoNumeroPessoa(ano, numero, getUsuarioLogado().getPessoa().getId());
	}
	
	/**
	 * Popula a lista de manifesta��es copiadas para o usu�rio logado.
	 * 
	 * @throws DAOException
	 */
	private void popularManifestacoesCopiadas() throws DAOException {
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		
		manifestacoesCopiadas = dao.findManifestacoesCopiadas(ano, numero, getUsuarioLogado().getPessoa().getId(), getAllUnidadesResponsabilidadeUsuario().toArray(new UnidadeGeral[0]));
	}
    
	/**
	 * Recupera o id da manifesta��o selecionada e inicia o processo de detalhamento da mesma.<br />
     * M�todo n�o invocado por JSPs.
	 */
    @Override
    public void detalharManifestacao(ActionEvent evt) throws DAOException {
		Integer id = (Integer) evt.getComponent().getAttributes().get("idManifestacao");
		
		detalharManifestacao(id, true);
    }
    
    /**
     * Detalha a manifesta��o de acordo com o id passado.
     * Direciona o usu�rio para a tela de detalhamento da manifesta��o de acordo com o par�metro #forward passado.
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
		    	if(manifestacoesCopiadas != null && manifestacoesCopiadas.contains(obj)) { //Se a manifesta��o est� copiada para a pessoa, permite visualiza��o completa do hist�rico
		    		historicos = dao.getAllHistoricosByManifestacao(obj.getId());
		    		
		    		copias = acompanhamentoDao.findAllAcompanhamentosByManifestacao(obj.getId());
		    		
		    		delegacoes = delegacaoDao.findAllDelegacoesByManifestacao(obj.getId());
		    	}
		    	else //Caso contr�rio, a manifesta��o foi cadastrada pela pessoa, e ela s� deve ter acesso ao hist�rico de resposta da ouvidoria
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
     * Recupera uma manifesta��o previamente listada de acordo com seu id.
     * 
     * @param id
     * @return
     */
    @Override
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
     * Recupera a quantidade total de manifesta��es cadastradas e copiadas para o usu�rio.
     * 
     * @return
     */
    public int getTotalManifestacoes() {
    	int cadastradas = getManifestacoes() != null ? manifestacoes.size() : 0;
    	int copiadas = getManifestacoesCopiadas() != null ? manifestacoesCopiadas.size() : 0;
    	
    	return cadastradas + copiadas;
    }
    
    /**
     * Lista todas as manifesta��es cadastradas e copiadas para o usu�rio.
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
	 * Recupera a cole��o de manifesta��es cadastradas pelo usu�rio.
	 */
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
    
	/**
	 * Recupera a cole��o de manifesta��es copiadas para o usu�rio.
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
    public String getDirBase() {
    	String dirBase = super.getDirBase();
    	
    	if(getSubSistema().getId() != SigaaSubsistemas.OUVIDORIA.getId()) {
    		dirBase += "/ouvidoria/Manifestacao";
    	}
    	dirBase += "/discente";
    	
		return dirBase;
    }

}
