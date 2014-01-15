package br.ufrn.sigaa.ensino.graduacao.negocio;

import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.dominio.MovimentoCadastro;

/** Movimento que encapsula dados da mudança curricular de um discente.
 * @author Édipo Elder F. Melo
 *
 */
public class MovimentoMudancaCurricular extends MovimentoCadastro {
	
	/** Stream de resposta, utilizado para retornar o histórico do discente numa simulação de mudança curricular. */
	private HttpServletResponse response;

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

}
