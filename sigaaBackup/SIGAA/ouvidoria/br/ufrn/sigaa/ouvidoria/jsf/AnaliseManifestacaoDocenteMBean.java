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
 * Controller respons�vel por opera��es de an�lise de manifesta��es por docentes.
 * 
 * @author Bernardo
 *
 */
@Component(value="analiseManifestacaoDocente") @Scope(value="request")
public class AnaliseManifestacaoDocenteMBean extends AnaliseManifestacaoAbstractController {
	
	/** Lista de manifesta��es copiadas para o docente logado. */
	private Collection<Manifestacao> manifestacoesCopiadas;
    
    public AnaliseManifestacaoDocenteMBean() {
    	init();
    }

    /**
     * Direciona o usu�rio para a p�gina de acompanhamento de suas manifesta��es.<br />
     * M�todo n�o invocado por JSPs.
     * 
     * @return
     */
    public String acompanharManifestacao() {
    	return forward(getDirBase() + "/acompanhar.jsp");
    }
    
    /**
     * Lista as manifesta��es cadastradas e copiadas para o docente logado.<br />
     * M�todo invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/docente/detalhes_manifestacao.jsp</li>
     * <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
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
     * Popula as manifesta��es cadastradas pelo docente logado.
     * 
     * @throws DAOException
     */
    private void popularManifestacoesCadastradas() throws DAOException {
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		
		manifestacoes = dao.findManifestacoesByAnoNumeroPessoa(ano, numero, getUsuarioLogado().getPessoa().getId());
	}
    
    /**
     * Popula as manifesta��es copiadas para o docente logado.
     * 
     * @throws DAOException
     */
    private void popularManifestacoesCopiadas() throws DAOException {
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class);
		
		manifestacoesCopiadas = dao.findManifestacoesCopiadas(ano, numero, getUsuarioLogado().getPessoa().getId(), getAllUnidadesResponsabilidadeUsuario().toArray(new UnidadeGeral[0]));
	}
    
    /**
     * Recupera o id da manifesta��o selecionada pelo usu�rio e inicia o detalhamento da manifesta��o.<br />
     * M�todo invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/Manifestacao/docente/acompanhar.jsp</li>
     * </ul>
     */
    @Override
    public void detalharManifestacao(ActionEvent evt) throws DAOException {
		Integer id = (Integer) evt.getComponent().getAttributes().get("idManifestacao");
		
		detalharManifestacao(id, true);
    }
    
    /**
     * Detalha a manifesta��o selecionada e direciona o usu�rio para a 
     * p�gina de detalhamento se assim for especificado no par�metro passado.
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
		    	if(manifestacoesCopiadas.contains(obj)) { //Se a manifesta��o est� copiada para a pessoa, permite visualiza��o completa do hist�rico
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
     * Retorna o total de manifesta��es cadastradas e copiadas para o docente.
     * 
     * @return
     */
    public int getTotalManifestacoes() {
    	int cadastradas = getManifestacoes() != null ? manifestacoes.size() : 0;
    	int copiadas = getManifestacoesCopiadas() != null ? manifestacoesCopiadas.size() : 0;
    	
    	return cadastradas + copiadas;
    }
    
    /**
     * Retorna todas as manifesta��es cadastradas e copiadas para o docente.
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
	 * Popula e retorna as manifesta��es cadastradas pelo docente.
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
     * Retorna as manifesta��es copiadas para o docente.
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
    	dirBase += "/docente";
    	
		return dirBase;
    }

}
