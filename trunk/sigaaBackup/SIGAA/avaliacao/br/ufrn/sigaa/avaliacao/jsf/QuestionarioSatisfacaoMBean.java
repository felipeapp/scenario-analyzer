/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 09/06/2008 
 */
package br.ufrn.sigaa.avaliacao.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.avaliacao.dominio.QuestionarioSatisfacao;

/**
 * Managed Bean para pegar os dados do questionário
 * de satisfação da avaliação institucional. 
 * 
 * @author David Pereira
 *
 */
@Component @Scope("session")
public class QuestionarioSatisfacaoMBean extends SigaaAbstractController<QuestionarioSatisfacao> {

	public QuestionarioSatisfacaoMBean() {
		obj = new QuestionarioSatisfacao();
	}

	public String cadastrar() throws ArqException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.QUESTIONARIO_SATISFACAO);
		
		try {
			execute(mov);
			addMensagemInformation("Avaliação preenchida com sucesso!");
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		
		return cancelar();
	}
	
}
