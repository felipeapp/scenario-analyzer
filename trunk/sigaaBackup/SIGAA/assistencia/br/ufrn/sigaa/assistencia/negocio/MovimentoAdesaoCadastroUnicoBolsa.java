/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 26/03/2009
 *
 */	
package br.ufrn.sigaa.assistencia.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.assistencia.cadunico.dominio.AdesaoCadastroUnicoBolsa;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;

/**
 * Movimento para mandar para o processador o objeto de adesão e resposta do formulário
 * 
 * @author Henrique Andre
 *
 */
public class MovimentoAdesaoCadastroUnicoBolsa extends AbstractMovimentoAdapter {

	private AdesaoCadastroUnicoBolsa adesaoCadatro;
	private QuestionarioRespostas respostas;

	public AdesaoCadastroUnicoBolsa getAdesaoCadatro() {
		return adesaoCadatro;
	}

	public void setAdesaoCadatro(AdesaoCadastroUnicoBolsa adesaoCadatro) {
		this.adesaoCadatro = adesaoCadatro;
	}

	public QuestionarioRespostas getRespostas() {
		return respostas;
	}

	public void setRespostas(QuestionarioRespostas respostas) {
		this.respostas = respostas;
	}

}
