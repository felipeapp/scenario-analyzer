package br.ufrn.sigaa.ouvidoria.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Controller para operações gerais em {@link Manifestacao}.
 * 
 * @author Bernardo
 *
 */
@Component(value="manifestacaoGeral") @Scope(value="request")
public class ManifestacaoGeralMBean extends ManifestacaoAbstractController {

	public ManifestacaoGeralMBean() {
    	init();
    }

    /**
     * Inicia os dados necessários do MBean
     */
    protected void init() {
    	obj = new Manifestacao();
    }
    
    @Override
	public String preCadastrar() throws ArqException, NegocioException {
    	if(isDiscente()) {
    		ManifestacaoDiscenteMBean discenteMBean = getMBean("manifestacaoDiscente");
    		return discenteMBean.preCadastrar();
    	}
    	else if(isDocente()) {
    		ManifestacaoDocenteMBean docenteMBean = getMBean("manifestacaoDocente");
    		return docenteMBean.preCadastrar();
    	}
    	else if(isTecnicoAdministrativo()) {
    		ManifestacaoServidorMBean servidorMBean = getMBean("manifestacaoServidor");
    		return servidorMBean.preCadastrar();
    	}
    	
    	return cancelar();
	}
    
    /**
     * Chama o caso de uso de acompanhamento de manifestação de acordo com o tipo de usuário logado.<br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ouvidoria/menu.jsp</li>
     * </ul>
     * 
     * @return
     * @throws ArqException
     * @throws NegocioException
     */
	public String acompanharManifestacoes() throws ArqException, NegocioException {
    	if(isDiscente()) {
    		AnaliseManifestacaoDiscenteMBean discenteMBean = getMBean("analiseManifestacaoDiscente");
    		return discenteMBean.listarManifestacoes();
    	}
    	else if(isDocente()) {
    		AnaliseManifestacaoDocenteMBean docenteMBean = getMBean("analiseManifestacaoDocente");
    		return docenteMBean.listarManifestacoes();
    	}
    	else if(isTecnicoAdministrativo()) {
    		AnaliseManifestacaoServidorMBean servidorMBean = getMBean("analiseManifestacaoServidor");
    		return servidorMBean.listarManifestacoes();
    	}
    	
    	return cancelar();
	}

	/**
	 * Verifica se o usuário logado é um discente.
	 * 
	 * @return
	 */
	public boolean isDiscente() {
		return getUsuarioLogado().getVinculoAtivo().isVinculoDiscente();
    }
    
	/**
	 * Verifica se o usuário logado é um docente.
	 * 
	 * @return
	 */
    public boolean isDocente() {
    	Servidor servidor = getUsuarioLogado().getVinculoAtivo().getServidor();
		return servidor != null && servidor.isDocente();
    }
    
    /**
     * Verifica se o usuário logado é um técnico administrativo.
     * 
     * @return
     */
    public boolean isTecnicoAdministrativo() {
    	Servidor servidor = getUsuarioLogado().getVinculoAtivo().getServidor();
		return servidor != null && !servidor.isDocente();
    }
    
}
