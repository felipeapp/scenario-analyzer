/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 05/06/2008
 *
 */	
package br.ufrn.sigaa.assistencia.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.dominio.ResidenciaUniversitaria;

/**
 * Apenas o CRUD da entidade Residência Universitária
 * 
 * @author agostinho
 *
 */

@Component("residenciaUniversitariaMBean") 
@Scope("session")
public class ResidenciaUniversitariaMBean extends SigaaAbstractController<ResidenciaUniversitaria> {

	/** Atributo que define uma coleção de residências na listagem. */
	private List<ResidenciaUniversitaria> listaResidencias = new ArrayList<ResidenciaUniversitaria>();
	
	public ResidenciaUniversitariaMBean() {
	}
	
	/**
	 * Inicia o caso de uso.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> sigaa.ear/sigaa.war/sae/menu.jsp </ul>
	 * @return
	 */
	public String instanciar() {
		clear();
		return forward("/sae/" + obj.getClass().getSimpleName() + "/form.jsf");
	}
	
	/**
	 * Retorna uma coleção das residências ativas para seleção.  
	 */
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return getAllAtivo(ResidenciaUniversitaria.class, "id","localizacao");
	}
	
	/**
	 * Retorna para página principal
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> sigaa.war/sae/ResidenciaUniversitaria/form.jsp </ul>
	 */
	public String cancelar() {
		return forward("/sae/menu.jsf");
	}
	
	/**
	 * Redireciona após cadastrar
	 * Não invocado por JSP.
	 */
	@Override
	public String forwardCadastrar() {
		return "/sae/index.jsf";
	}
	
	/**
	 * Altera residência existente
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> sigaa.war/sae/ResidenciaUniversitaria/form.jsp </ul>
	 * @return
	 * @throws ArqException
	 */
	public String alterar() throws ArqException {
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		prepareMovimento(ArqListaComando.ALTERAR);
		int id = getParameterInt("id", 0);
		obj = getDAO(GenericDAOImpl.class).findByPrimaryKey(id, ResidenciaUniversitaria.class);
		return forward("/sae/" + obj.getClass().getSimpleName() + "/form.jsf");
	}
	
	/**
	 * Remove residência (ativo = false)
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> sigaa.war/sae/ResidenciaUniversitaria/lista.jsp </ul>
	 */
	@Override
	public String remover() throws ArqException {

		prepareMovimento(ArqListaComando.ALTERAR);
		
		int id = getParameterInt("id", 0);
		obj = getDAO(GenericDAOImpl.class).findByPrimaryKey(id, ResidenciaUniversitaria.class);
		
		if ( obj.isAtivo() ) 
			obj.setAtivo(false);
		else
			obj.setAtivo(true);
	
		try {
			super.cadastrar();
		} catch (NegocioException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Inicializa o objeto gerenciado pelo Managed Bean.
	 */
	private void clear() {
		obj = new ResidenciaUniversitaria();
	}

	/**
	 * Após a remoção. Redireciona para página de listagem.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> Não invocado por JSP. </ul>
	 */
	@Override
	protected String forwardRemover() {
		return forward("/sae/" + obj.getClass().getSimpleName() + "/lista.jsf");
	}
	
	/**
	 * Realiza o cadastro de uma nova Residência
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> sigaa.war/sae/ResidenciaUniversitaria/form.jsp </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		
		if (validarFormularioResidencia()) {
			obj.setAtivo(true);
			super.cadastrar();
			clear();
			return null;
		}
		else 
			return null;
		
	}

	/**
	 * Valida o formulário
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean validarFormularioResidencia() {
		boolean semErros = true;
		boolean semErroDuplicacao = true;
		
		listaResidencias = (List<ResidenciaUniversitaria>) getAllObj(ResidenciaUniversitaria.class);
		for (ResidenciaUniversitaria residencia : listaResidencias) {
			if (residencia.getLocalizacao().equals(obj.getLocalizacao()) && residencia.getSexo().equals(obj.getSexo())) {
				semErroDuplicacao = false;
			}
		}
		
		if (!semErroDuplicacao) { 
			addMensagemErro("Você está tentando cadastrar um residência que já existe!");
		}
		
			if ( obj.getLocalizacao().equals("")) {
				addMensagemErro("Campo Localização é obrigatório!");
				semErros = false;
			}
				
			if ( obj.getSexo() != null) {
				 	if ( obj.getSexo().equals("") ) {
				 		addMensagemErro("É obrigatório escolher um sexo!");
				 		semErros = false; 
				 	}
			}
			else {
				addMensagemErro("É obrigatório escolher um sexo!");
				semErros = false;
			}
			
			return semErros;
	}

	/**
	 * Lista as Residências
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> sigaa.war/sae/ResidenciaUniversitaria/lista.jsp </ul>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String listar() throws ArqException {
		clear();
		listaResidencias.clear();
		List<ResidenciaUniversitaria> listaTemp = (List<ResidenciaUniversitaria>) getAllObj(ResidenciaUniversitaria.class, "localizacao");
		
		for (ResidenciaUniversitaria residenciaUniversitaria : listaTemp) {
			if (residenciaUniversitaria.isAtivo())
				listaResidencias.add(residenciaUniversitaria);
		}		
		
		return forward("/sae/" + obj.getClass().getSimpleName() + "/lista.jsf");
	}

	public List<ResidenciaUniversitaria> getListaResidencias() {
		return listaResidencias;
	}

	public void setListaResidencias(List<ResidenciaUniversitaria> listaResidencias) {
		this.listaResidencias = listaResidencias;
	}

}
