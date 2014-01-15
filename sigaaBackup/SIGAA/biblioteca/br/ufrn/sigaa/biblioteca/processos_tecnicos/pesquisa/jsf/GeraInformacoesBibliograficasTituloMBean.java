/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 *    <p>Classe respons�vel por gerar as informa��es bibliogr�fica de um T�tulo, detre as quais est�o o formato de 
 *    refer�ncia e a ficha catalogr�fica.
 *    </p>
 *    
 *    <p>Esse MBean � usado tanto pela parte interna quanto pela parte p�blica do sistema, apenas � chamado de p�gina diferentes</p>
 *
 * @author jadson
 * @since 04/09/2008
 * @version 1.0 criacao da classe
 * @version 2.0 20/10/2010 concertando a gera��o da ficha catalogr�fica e do formato de refer�ncia
 */
@Component(value="geraInformacoesBibliograficasTituloMBean")
@Scope(value="request")
public class GeraInformacoesBibliograficasTituloMBean extends SigaaAbstractController<TituloCatalografico>{
	
	
	/**
	 * 
	 * Carrega as informa��es completas do t�tulo cujo id � passado como par�metro
	 * e verifica a partir de outro par�metro qual p�gina deve ser mostrada.
	 * 
	 * Chamada a partir da p�gina: /sigaa/biblioteca/processos_tecnicos/pesquisas_acervo/infomarcoesBibliograficasTitulo.jsp
	 * Chamada a partir da p�gina: /sigaa/public/biblioteca/paginaPublicaVisualizaFormatosBibliograficoTitulo.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getGerarInformacoesTitulo() throws DAOException{
		
		Integer idTituloGerarInformacoes = getParameterInt("idTitulo");
		
		if(idTituloGerarInformacoes == null) // Verifica se o usu�rio n�o tentou acessar a p�gina diretamente
			return "";
		
		/**
		 * Ainda continua no mesmo T�tulo, ent�o n�o precisa carregar e gerar as informa��es novamente. Continua as que est�o na p�gina.
		 */
		if(obj != null &&  idTituloGerarInformacoes.equals(obj.getId())){
			return "";
		}
		
		TituloCatalograficoDao dao = null;
		
		try {
			dao = getDAO(TituloCatalograficoDao.class);
			
			// O id do T�tulo � passado por javascript //
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
				 * Obt�m os dados do T�tulos passado necess�rios para gerar o formato de refer�ncia.
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
