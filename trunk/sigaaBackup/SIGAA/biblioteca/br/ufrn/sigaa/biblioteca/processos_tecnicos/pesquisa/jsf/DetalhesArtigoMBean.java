/*
 * DetalhesArtigoMBean.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.biblioteca.ArtigoDePeriodicoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArtigoDePeriodico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *     <p>MBean auxiliar para exibi��o dos dados completos de um artigo no sistema.</p>
 *
 *     <p>Utilizando para mostrar os detalhes do um artigo tanto na pesquisa dos bibliotec�rios, quando na busca interna, quanto na busca externa.</p>
 *
 *     <p>Esse Mbean deve utilizar a p�gina paginaPadraoDetalhesArtigo.jsp, assim qualquer altera��o na exibi��o dos artigos � realizadas 
 *     em apenas 1 lugar do sistema</p>
 *
 * @author jadson
 * @since 05/08/2009
 * @version 1.0 criacao da classe
 *
 */
@Component("detalhesArtigoMBean") 
@Scope("request")
public class DetalhesArtigoMBean extends SigaaAbstractController<CacheEntidadesMarc>{

	/** Os dados da assinatura do artigo */
	private Assinatura assinatura;
	
	/** Os dados do fasc�culo do artigo */
	private Fasciculo fasciculo;
	
	
	public DetalhesArtigoMBean() {
		obj = new CacheEntidadesMarc();
	}
	
	
	
	/**
	 * <p>M�todo que carrega todas as informa��es do artigo selecionado e mostra para o usu�rio na tela padr�o da aplica��o.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>/sigaa.war/biblioteca/processos_tecncios/pesquisa_acervo/pesquisaTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 * @param event
	 * @throws DAOException
	 */
	public void carregarDetalhesArtigoSelecionado(ActionEvent event) throws DAOException{
		
		long tempo = System.currentTimeMillis();
		ArtigoDePeriodicoDao dao = null;
		
		
		try{
			dao = getDAO(ArtigoDePeriodicoDao.class);
			obj = BibliotecaUtil.obtemDadosArtigoCache( getParameterInt("idArtigoMostrarDetalhes", 0) );
			
			// obj != null para os casos onde o cache � removido quando o artigo � removido.
			if (obj != null && obj.getId() > 0) {
				try {
					
					ArtigoDePeriodico artigo =  dao.findByPrimaryKey(obj.getIdArtigoDePeriodico(), ArtigoDePeriodico.class);
					
					fasciculo = artigo.getFasciculo();
					
					if(fasciculo != null)
						assinatura = fasciculo.getAssinatura();
					
				} catch (DAOException e) {
					notifyError(e);
				}
			}
			
		} finally{
			if(dao != null) dao.close();
		}
		
		System.out.println(">>>>>>>>>>> Consultar todas informa��es do artigo demorou: "+ (System.currentTimeMillis()-tempo)+" ms");
		
	}
	

	public Assinatura getAssinatura() {
		return assinatura;
	}

	public Fasciculo getFasciculo() {
		return fasciculo;
	}
	
	
	
}
