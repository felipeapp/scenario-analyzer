/*
 * DetalhesAutoridadeMBean.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
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
 * ao usuário do sistema.
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
	 *    Carrega as informações da completas da autoridade cujo id é passado como parâmetro
	 * e verifica a partir de outro parâmetro qual página deve ser mostrada.
	 * <br/>
	 * Chamada a partir da página: biblioteca/processos_tecnicos/pesquisa_acervo/detalhesAutoridade.jsp
	 *
	 * @return
	 * @throws DAOException
	 */
	public String getCarregaInformacoesAutoridade() throws DAOException{
		
		GenericDAO dao = getGenericDAO();
		
		// o id do Item é passado como parâmetro via javascript
		obj = new Autoridade(getParameterInt("idAutoridade", 0));
	
		obj = dao.findByPrimaryKey(obj.getId(), Autoridade.class);
	
		
		// indica qual das páginas dentro da pagide incluído com js vai ser mostrada
		
		if(getParameter("mostarPaginaDadosMarc") != null ){
			getCurrentRequest().setAttribute("mostarPaginaDadosMarc", true);
		}
		if(getParameter("mostarPaginaDadosMarcPublico") != null ){
			getCurrentRequest().setAttribute("mostarPaginaDadosMarcPublico", true);
		}
			
		return "";
	}
}
