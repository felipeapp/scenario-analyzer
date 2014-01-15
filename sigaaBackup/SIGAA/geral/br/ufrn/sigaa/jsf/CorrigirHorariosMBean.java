package br.ufrn.sigaa.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;

@Component @Scope("request")
public class CorrigirHorariosMBean extends SigaaAbstractController {

	public String iniciar() throws ArqException {
		
		prepareMovimento(SigaaListaComando.CORRIGIR_HORARIO);
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.CORRIGIR_HORARIO);

		try {
			Object qtd = execute(mov);
			addMensagemInformation("Analisou " + qtd + " solicitações de turmas.");
			addMensagemInformation("Executado com sucesso.");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}		
		
		return null;
	}
	
}
