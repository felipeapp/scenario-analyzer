/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Apenas o CRUD da entidade Resid�ncia Universit�ria
 * 
 * @author agostinho
 *
 */

@Component("residenciaUniversitariaMBean") 
@Scope("session")
public class ResidenciaUniversitariaMBean extends SigaaAbstractController<ResidenciaUniversitaria> {

	/** Atributo que define uma cole��o de resid�ncias na listagem. */
	private List<ResidenciaUniversitaria> listaResidencias = new ArrayList<ResidenciaUniversitaria>();
	
	public ResidenciaUniversitariaMBean() {
	}
	
	/**
	 * Inicia o caso de uso.
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> sigaa.ear/sigaa.war/sae/menu.jsp </ul>
	 * @return
	 */
	public String instanciar() {
		clear();
		return forward("/sae/" + obj.getClass().getSimpleName() + "/form.jsf");
	}
	
	/**
	 * Retorna uma cole��o das resid�ncias ativas para sele��o.  
	 */
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return getAllAtivo(ResidenciaUniversitaria.class, "id","localizacao");
	}
	
	/**
	 * Retorna para p�gina principal
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> sigaa.war/sae/ResidenciaUniversitaria/form.jsp </ul>
	 */
	public String cancelar() {
		return forward("/sae/menu.jsf");
	}
	
	/**
	 * Redireciona ap�s cadastrar
	 * N�o invocado por JSP.
	 */
	@Override
	public String forwardCadastrar() {
		return "/sae/index.jsf";
	}
	
	/**
	 * Altera resid�ncia existente
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Remove resid�ncia (ativo = false)
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Ap�s a remo��o. Redireciona para p�gina de listagem.
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> N�o invocado por JSP. </ul>
	 */
	@Override
	protected String forwardRemover() {
		return forward("/sae/" + obj.getClass().getSimpleName() + "/lista.jsf");
	}
	
	/**
	 * Realiza o cadastro de uma nova Resid�ncia
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Valida o formul�rio
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
			addMensagemErro("Voc� est� tentando cadastrar um resid�ncia que j� existe!");
		}
		
			if ( obj.getLocalizacao().equals("")) {
				addMensagemErro("Campo Localiza��o � obrigat�rio!");
				semErros = false;
			}
				
			if ( obj.getSexo() != null) {
				 	if ( obj.getSexo().equals("") ) {
				 		addMensagemErro("� obrigat�rio escolher um sexo!");
				 		semErros = false; 
				 	}
			}
			else {
				addMensagemErro("� obrigat�rio escolher um sexo!");
				semErros = false;
			}
			
			return semErros;
	}

	/**
	 * Lista as Resid�ncias
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
