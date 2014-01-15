/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>Classe responsável por gerar os link HTML para acessar informações de obras que estão disponíveis na internet</p>
 *    
 * <p>Esse MBean é usado tanto pela parte interna quanto pela parte pública do sistema, apenas é chamado de página diferentes</p>
 * 
 * @author jadson
 *
 */
@Component(value="mostraEnderecosEletronicosTitulosMBean")
@Scope(value="request")
public class MostraEnderecosEletronicosTitulosMBean extends SigaaAbstractController<TituloCatalografico>{

	
	/**
	 *   Método que retorna os endereços eletrônicos de um título no formato de links html para o 
	 * usuário poder clicar e já abrir.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/enderecosEletronicosTitulo.jsp
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
				retorno.append("<li> Não foi possível obter o endereço eletrônico do título. </li>");
			}else{
				List<Map<String, String>> enderecos =  tituloCache.getLocalizacaoEnderecoEletronicoFormatados();
				
				String url;
				for (Map<String, String> endereco : enderecos) {
					url = endereco.get("url");
					/*
					 *  para forçar a ser um link externo, se começar apenas com www o navegador tentar abrir como uma página interna do SIGAA
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
