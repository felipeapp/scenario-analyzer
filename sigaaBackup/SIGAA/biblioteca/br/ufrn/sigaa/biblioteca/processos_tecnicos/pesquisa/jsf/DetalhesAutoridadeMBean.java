/*
 * DetalhesAutoridadeMBean.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;

/**
 *   Mbean que carrega todos os dados de uma autoridade (os campos no formato MARC) para mostrar 
 * ao usu�rio do sistema.
 *
 * @author jadson
 * @since 04/05/2009
 * @version 1.0 criacao da classe
 *
 */
@Component(value="detalhesAutoridadeMBean")
@Scope(value="request")
public class DetalhesAutoridadeMBean extends SigaaAbstractController<Autoridade>{

	
	/**
	 * 
	 *    Carrega as informa��es da completas da autoridade cujo id � passado como par�metro
	 * e verifica a partir de outro par�metro qual p�gina deve ser mostrada.
	 * <br/>
	 * Chamada a partir da p�gina: biblioteca/processos_tecnicos/pesquisa_acervo/detalhesAutoridade.jsp
	 *
	 * @return
	 * @throws DAOException
	 */
	public String getCarregaInformacoesAutoridade() throws DAOException{
		
		GenericDAO dao = getGenericDAO();
		
		// o id do Item � passado como par�metro via javascript
		obj = new Autoridade(getParameterInt("idAutoridade", 0));
	
		obj = dao.findByPrimaryKey(obj.getId(), Autoridade.class);
	
		
		// indica qual das p�ginas dentro da pagide inclu�do com js vai ser mostrada
		
		if(getParameter("mostarPaginaDadosMarc") != null ){
			getCurrentRequest().setAttribute("mostarPaginaDadosMarc", true);
		}
		if(getParameter("mostarPaginaDadosMarcPublico") != null ){
			getCurrentRequest().setAttribute("mostarPaginaDadosMarcPublico", true);
		}
			
		return "";
	}
}
