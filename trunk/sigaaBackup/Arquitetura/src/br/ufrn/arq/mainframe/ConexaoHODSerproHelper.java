/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 09/05/2012
 * Autor:     Johnny Marçal
 *
 */
package br.ufrn.arq.mainframe;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import br.ufrn.arq.erros.RuntimeNegocioException;

/**
 * 
 * Utilizado para realizar o processo de login no Serviço de Emulação 3270 via Web Browser (HOD) do SERPRO.
 * Esse procedimento é necessário para que seja criado um canal de comunicação registrado através
 * da propriedade "TERMINAL LU" que posteriormente é utilizada para estabelecer comunicação com o mainframe
 * do sistema estruturante.
 * 
 * @author Johnny Marçal
 * 
 */
public class ConexaoHODSerproHelper {

	private final static String URL_SERPRO_LOGIN_HOD_SIAFI = "https://acesso.serpro.gov.br/HOD10/jsp/logonJava.jsp";

	public static String conectar(String cpf, String senha) {

		HttpClient cliente = new HttpClient();
		PostMethod metodoPost = new PostMethod(URL_SERPRO_LOGIN_HOD_SIAFI);

		metodoPost.addParameter("txtNumCpf", cpf);
		metodoPost.addParameter("txtSenha", senha);
		
		int codigoStatus = -1;
		String LU = "";

		try {

			if (SSLHelper.ignorarCerificado(URL_SERPRO_LOGIN_HOD_SIAFI)) {

				codigoStatus = cliente.executeMethod(metodoPost);

				if (codigoStatus != -1) {
					
					String resposta = metodoPost.getResponseBodyAsString();

					metodoPost.releaseConnection();

					if(conexaoEfetuadaComSucesso(resposta, cpf)){						
						
						String[] respostaSplitLU = resposta.split("luid=");														
						
						if (respostaSplitLU.length > 1) {
							LU = respostaSplitLU[1].substring(0, respostaSplitLU[1].indexOf("'"));
							br.ufrn.arq.seguranca.log.Logger.info("TERMINAL LU(SERPRO): " + LU);
						}else{
							br.ufrn.arq.seguranca.log.Logger.info("[ERRO] String \"luid=\" não encontrada no conteúdo de resposta!");
							throw new Exception("Erro no mecanismo de autenticação HOD do Serpro. Entre em contato com a equipe de suporte.");
						}	
					}
				}
			}
			
		}catch (RuntimeNegocioException e) {
			e.printStackTrace();
			throw new RuntimeNegocioException(e.getMessage());
		} 
		catch (Exception e) {
			e.printStackTrace();
			throw new MainFrameConnectionException(e.getMessage());
		}

		return LU;

	}

	private static boolean conexaoEfetuadaComSucesso(String resposta, String cpf){
		
		if(resposta.contains("Erro EW(02):"))
			throw new SenhaIncorretaHODException(String.format("ATENÇÃO: senha incorreta para o CPF \"%s\"! Confira a senha com cautela, pois três erros sucessivos revogam o acesso deste usuário aos sistemas do SERPRO.", cpf));
			
		if(resposta.contains("Erro EW(18):"))
			throw new UsuarioRevogadoHODException(String.format("Usuário de CPF \"%s\" com acesso revogado aos sistemas do SERPRO. Solicitar a troca da sua senha.", cpf));
		
		if(resposta.contains("Erro BW(4):"))
			throw new AcessoNaoPermitidoHODException(String.format("Usuário de CPF \"%s\" com acesso não permitido aos sistemas do SERPRO. Persistindo o erro, entre em contato com a central de atendimento do SERPRO.", cpf));
		
		if(resposta.contains("Erro EW(03):"))
			throw new AcessoNaoPermitidoHODException(String.format("Usuário de CPF \"%s\" não cadastrado nos sistemas do SERPRO. Persistindo o erro, entre em contato com a central de atendimento do SERPRO.", cpf));
		
		return true;
	}		
	
}