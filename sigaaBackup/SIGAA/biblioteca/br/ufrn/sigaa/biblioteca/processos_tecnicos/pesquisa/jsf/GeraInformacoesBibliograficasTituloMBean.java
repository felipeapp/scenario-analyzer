/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 04/11/2008
 *
 */

package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.FormatosBibliograficosUtil;

/**
 *    <p>Classe responsável por gerar as informações bibliográfica de um Título, detre as quais estão o formato de 
 *    referência e a ficha catalográfica.
 *    </p>
 *    
 *    <p>Esse MBean é usado tanto pela parte interna quanto pela parte pública do sistema, apenas é chamado de página diferentes</p>
 *
 * @author jadson
 * @since 04/09/2008
 * @version 1.0 criacao da classe
 * @version 2.0 20/10/2010 concertando a geração da ficha catalográfica e do formato de referência
 */
@Component(value="geraInformacoesBibliograficasTituloMBean")
@Scope(value="request")
public class GeraInformacoesBibliograficasTituloMBean extends SigaaAbstractController<TituloCatalografico>{
	
	
	/**
	 * 
	 * Carrega as informações completas do título cujo id é passado como parâmetro
	 * e verifica a partir de outro parâmetro qual página deve ser mostrada.
	 * 
	 * Chamada a partir da página: /sigaa/biblioteca/processos_tecnicos/pesquisas_acervo/infomarcoesBibliograficasTitulo.jsp
	 * Chamada a partir da página: /sigaa/public/biblioteca/paginaPublicaVisualizaFormatosBibliograficoTitulo.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getGerarInformacoesTitulo() throws DAOException{
		
		Integer idTituloGerarInformacoes = getParameterInt("idTitulo");
		
		if(idTituloGerarInformacoes == null) // Verifica se o usuário não tentou acessar a página diretamente
			return "";
		
		/**
		 * Ainda continua no mesmo Título, então não precisa carregar e gerar as informações novamente. Continua as que estão na página.
		 */
		if(obj != null &&  idTituloGerarInformacoes.equals(obj.getId())){
			return "";
		}
		
		TituloCatalograficoDao dao = null;
		
		try {
			dao = getDAO(TituloCatalograficoDao.class);
			
			// O id do Título é passado por javascript //
			obj = new TituloCatalografico(idTituloGerarInformacoes);
			
			
			if (getParameter("exibirPaginaDadosMarc") != null){
				
				obj= dao.findAllDadosMARCTituloCatalografico(obj.getId());
				
				getCurrentRequest().setAttribute("exibirPaginaDadosMarc", true);
			}
			
			
			if (getParameter("exibirPaginaDadosMarcPublico") != null){
				
				obj = dao.findAllDadosMARCTituloCatalografico(obj.getId());
				
				getCurrentRequest().setAttribute("exibirPaginaDadosMarcPublico", true);
			}	
			
			
			if (getParameter("exibirPaginaFichaCatalografica") != null || getParameter("exibirPaginaReferencias") != null){
			
				/* ************************************************************************************
				 * Obtém os dados do Títulos passado necessários para gerar o formato de referência.
				 * Object[0] = 245     (campo)
				 * Object[1] = a       (subcampo)
				 * Object[2] = "...."  (dados)
				 * Object[3] = "...."  (id campo dados)
				 * Object[4] = "...."  (posicao campo dados)
				 * Object[5] = "...."  (posicao sub campo)
				 * ************************************************************************************/
				List<Object[]> dadosCampo = dao.findDadosTituloCatalografico(obj.getId());
				
				if (getParameter("exibirPaginaFichaCatalografica") != null){
					
					String [] dadosFichaTemp = new FormatosBibliograficosUtil().gerarFichaCatalografica(obj, dadosCampo, true);
					
					obj.setNumeroChamada(dadosFichaTemp[0]);
					obj.setFichaCatalografica(dadosFichaTemp[1]);
					
					getCurrentRequest().setAttribute("exibirPaginaFichaCatalografica", true);
				}
				
				if (getParameter("exibirPaginaReferencias") != null){
					obj.setFormatoReferencia(new FormatosBibliograficosUtil().gerarFormatoReferencia(obj, dadosCampo, true));
					getCurrentRequest().setAttribute("exibirPaginaReferencias", true);
				}
			
			}
			
			return "";
			
		} finally {
			if (dao != null) dao.close();
		}
	}

}
