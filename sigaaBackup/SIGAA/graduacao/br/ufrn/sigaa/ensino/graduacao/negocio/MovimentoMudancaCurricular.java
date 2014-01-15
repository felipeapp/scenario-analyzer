package br.ufrn.sigaa.ensino.graduacao.negocio;

import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.dominio.MovimentoCadastro;

/** Movimento que encapsula dados da mudan�a curricular de um discente.
 * @author �dipo Elder F. Melo
 *
 */
public class MovimentoMudancaCurricular extends MovimentoCadastro {
	
	/** Stream de resposta, utilizado para retornar o hist�rico do discente numa simula��o de mudan�a curricular. */
	private HttpServletResponse response;

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

}
