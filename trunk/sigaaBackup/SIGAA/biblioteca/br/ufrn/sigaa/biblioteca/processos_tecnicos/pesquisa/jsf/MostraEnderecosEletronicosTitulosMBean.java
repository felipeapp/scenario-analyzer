/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 27/10/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;

/**
 *
 * <p>Classe respons�vel por gerar os link HTML para acessar informa��es de obras que est�o dispon�veis na internet</p>
 *    
 * <p>Esse MBean � usado tanto pela parte interna quanto pela parte p�blica do sistema, apenas � chamado de p�gina diferentes</p>
 * 
 * @author jadson
 *
 */
@Component(value="mostraEnderecosEletronicosTitulosMBean")
@Scope(value="request")
public class MostraEnderecosEletronicosTitulosMBean extends SigaaAbstractController<TituloCatalografico>{

	
	/**
	 *   M�todo que retorna os endere�os eletr�nicos de um t�tulo no formato de links html para o 
	 * usu�rio poder clicar e j� abrir.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/enderecosEletronicosTitulo.jsp
	 * @return
	 * @throws DAOException
	 */
	public String getEnderecosEletronicosFormatadosLink() throws DAOException{
		
		GenericDAO dao = null;
		
		try {
			
			dao = getGenericDAO();
			
			StringBuilder retorno = new StringBuilder();
			
			retorno.append("<p><ul>");
			
			CacheEntidadesMarc tituloCache = dao.findByPrimaryKey(getParameterInt("idTituloCache", 0), CacheEntidadesMarc.class, "localizacaoEnderecoEletronico");
			
			if(tituloCache == null){
				retorno.append("<li> N�o foi poss�vel obter o endere�o eletr�nico do t�tulo. </li>");
			}else{
				List<Map<String, String>> enderecos =  tituloCache.getLocalizacaoEnderecoEletronicoFormatados();
				
				String url;
				for (Map<String, String> endereco : enderecos) {
					url = endereco.get("url");
					/*
					 *  para for�ar a ser um link externo, se come�ar apenas com www o navegador tentar abrir como uma p�gina interna do SIGAA
					 */
					if(url != null && ! url.startsWith("http://") && ! url.startsWith("https://") && ! url.startsWith("ftp://")) 
						url  = "http://"+url;
					
					retorno.append("<li>"+"<a href=\""+url+"\"  target=\"_blank\">"+endereco.get("descricao")+"</a>" +"</li>");
				}
			}
			
			retorno.append("</ul></p>");
	
			return retorno.toString();
			
		} finally {
			if (dao != null)
				dao.close();
		}
	
	}
}
