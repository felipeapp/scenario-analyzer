/*
 * DetalhesMARCArtigoMBean
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
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArtigoDePeriodico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;

/**
 *
 *   Mbean que carrega os dados MARC de uma entidade do acervo.
 *
 * @author jadson
 * @since 21/07/2009
 * @version 1.0 criacao da classe
 *
 */
@Component(value="detalhesMARCMBean")
@Scope(value="request")
public class DetalhesMARCMBean extends SigaaAbstractController<Object>{

	
	
	/**
	 * Carregar as informa��es marc do t�tulo passado.
	 * 
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/infoMarc.jsp</li>
	 *   </ul>
	 *   
	 *
	 * @return
	 * @throws DAOException
	 */
	public String getCarregaInformacoesMarcTitulo() throws DAOException{
		
		if(getParameterInt("idTitulo") != null){ 
		
			GenericDAO dao = null;
			
			try{
				
				dao = getGenericDAO();
				
				// o id do Item � passado como parametro via javascript
				TituloCatalografico titulo = new TituloCatalografico(getParameterInt("idTitulo", 0));
			
				obj = dao.findByPrimaryKey(titulo.getId(), TituloCatalografico.class);
			
				
				// indica qual das p�ginas dentro da pagide inclu�do com js vai ser mostrada
				
				if(getParameter("mostarPaginaDadosMarc") != null ){
					getCurrentRequest().setAttribute("mostarPaginaDadosMarc", true);
				}
				if(getParameter("mostarPaginaDadosMarcPublico") != null ){
					getCurrentRequest().setAttribute("mostarPaginaDadosMarcPublico", true);
				}
			
			}finally{
				if(dao != null) dao.close();
			}
		
		}
		
		return "";
	}
	
	/**
	 * 
	 *    Carregar as informa��es marc da autoridade passado.
	 * 
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/infoMarc.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public String getCarregaInformacoesMarcAutoridade() throws DAOException{
		
		if(getParameterInt("idAutoridade") != null){ 
		
		
			GenericDAO dao = null;
			
			try{
				
				dao = getGenericDAO();
			
				// o id do Item � passado como parametro via javascript
				Autoridade autoridade = new Autoridade(getParameterInt("idAutoridade", 0));
			
				obj = dao.findByPrimaryKey(autoridade.getId(), Autoridade.class);
			
				
				// indica qual das p�ginas dentro da pagide inclu�do com js vai ser mostrada
				
				if(getParameter("mostarPaginaDadosMarc") != null ){
					getCurrentRequest().setAttribute("mostarPaginaDadosMarc", true);
				}
				if(getParameter("mostarPaginaDadosMarcPublico") != null ){
					getCurrentRequest().setAttribute("mostarPaginaDadosMarcPublico", true);
				}
			
			}finally{
				if(dao != null) dao.close();
			}
			
		}
		return "";
	}
	
	
	
	/**
	 * Carrega as informa��es MARC do artigo passado
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/detalhesArtigoFormatoMarc.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/detalhesArtigoFormatoMarcPublico.jsp	/</li>
	 *   </ul>
	 *   
	 *
	 * @return
	 * @throws DAOException
	 */
	public String getCarregaInformacoesMarcArtigo() throws DAOException{
		
		if(getParameterInt("idArtigo") != null){ 
		
			GenericDAO dao = null;
			
			try{
				
				dao = getGenericDAO();
			
			// o id do Item � passado como parametro via javascript
				ArtigoDePeriodico artigo = new ArtigoDePeriodico(getParameterInt("idArtigo", 0));
			
				obj = dao.findByPrimaryKey(artigo.getId(), ArtigoDePeriodico.class);
			
				
				// indica qual das p�ginas dentro da pagide inclu�do com js vai ser mostrada
				
				if(getParameter("mostarPaginaDadosMarc") != null ){
					getCurrentRequest().setAttribute("mostarPaginaDadosMarc", true);
				}
				if(getParameter("mostarPaginaDadosMarcPublico") != null ){
					getCurrentRequest().setAttribute("mostarPaginaDadosMarcPublico", true);
				}
			}finally{
				if(dao != null) dao.close();
			}
			
		}
		
		return "";
	}
	
}
