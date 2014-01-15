/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 26/10/2011
 */
package br.ufrn.sigaa.ead.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mobile.utils.JSONProcessor;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ead.negocio.MovimentoDadosEad;
import br.ufrn.sigaa.mobile.resources.SigaaGenericResource;

/**
 * Resource para logon no SIGAA.
 *  
 * @author David Pereira
 *
 */
@Path("/servico")
public class DadosEadResource extends SigaaGenericResource {

	/**
	 * Realiza logon no SIGAA através de uma requisição HTTP
	 * utilizando o método POST. Retorna uma representação em JSON
	 * do usuário logado ou erro 403 (Forbidden) caso o logon
	 * não tenha sido realizado com sucesso.
	 * 
	 * @param login
	 * @param senha
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response execute(@QueryParam("operacao") final String operacao, @QueryParam("login") final String login, @QueryParam("senha") final String senha, @QueryParam("parametros") final String parametros) throws Exception {
		
		Response response = null;
		
		try {
			MovimentoDadosEad mov = new MovimentoDadosEad ();
			mov.setCodMovimento(SigaaListaComando.DADOS_EAD);
			mov.setRequest(request);
			
			mov.setLogin(login);
			mov.setSenha(senha);
			mov.setParametros(parametros);
			mov.setOperacao(operacao);
			
			request.setAttribute("sistema", Sistema.SIGAA);
		
			response = Response.ok(JSONProcessor.toJSON(executarMovimento(mov))).build();
		} catch (NegocioException e) {
			e.printStackTrace();
			response = Response.ok(e.getMessage()).build();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return response;
	}
}