/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 08/09/2011
 *
 */
package br.ufrn.sigaa.arq.negocio;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;

/**
 * Classe para unificar a implementação de operações gerais utilizadas em todo o SIGAA.
 * Criada para evitar a duplicação de código em Controllers e Validators, por exemplo.
 * 
 * @author Leonardo Campos
 *
 */
public class SigaaHelper {

	/**
	 * Retorna Curso atual manipulado pelo coordenador/secretário logado
	 * 
	 * @return
	 */
	public static Curso getCursoAtualCoordenacao() {
		
		SecretariaUnidade secretariaUnidade = getUsuarioLogado().getVinculoAtivo().getSecretariaUnidade();
		
		if (secretariaUnidade != null && secretariaUnidade.getCurso() != null) {
			return secretariaUnidade.getCurso();
		} else if (getCurrentSession().getAttribute("cursoAtual")!= null)
			return (Curso) getCurrentSession().getAttribute("cursoAtual");
		else
			return null;
		
	}
	
	/**
	 * Retorna Programa atual manipulado pelo coordenador/secretário logado
	 * 
	 * @return
	 */
	public static Unidade getProgramaStricto() {
		
		if (getUsuarioLogado().getVinculoAtivo().isVinculoSecretaria()) {
			return getUsuarioLogado().getVinculoAtivo().getSecretariaUnidade().getUnidade();
		} else if (getCurrentSession().getAttribute("programaAtual")!= null)
			return (Unidade) getCurrentSession().getAttribute("programaAtual");
		else
			return null;
	}	
	
	/**
	 * Retorna o usuário logado.
	 * 
	 * @param <V>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <V extends Usuario> V getUsuarioLogado() {
		return (V) getCurrentRequest().getSession().getAttribute("usuario");
	}

	/**
	 * Possibilita o acesso ao HttpSession.
	 */
	private static HttpSession getCurrentSession() {
		return getCurrentRequest().getSession(true);
	}

	/**
	 * Possibilita o acesso ao HttpServletRequest.
	 */
	private static HttpServletRequest getCurrentRequest() {
		return (HttpServletRequest) getExternalContext().getRequest();
	}
	
	/**
	 * Acessa o external context do JavaServer Faces
	 **/
	private static ExternalContext getExternalContext() {
		return FacesContext.getCurrentInstance().getExternalContext();
	}
}
