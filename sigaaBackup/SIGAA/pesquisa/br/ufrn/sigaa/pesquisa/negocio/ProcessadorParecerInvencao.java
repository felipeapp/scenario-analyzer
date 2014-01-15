/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/10/2008
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.pesquisa.dominio.Invencao;
import br.ufrn.sigaa.pesquisa.dominio.ParecerInvencao;

/**
 * Processador responsável pela emissão dos pareceres sobre as invenções
 * 
 * @author leonardo
 *
 */
public class ProcessadorParecerInvencao extends AbstractProcessador {

	
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		validate(mov);
		
		GenericDAO dao = getGenericDAO(mov);
		ParecerInvencao parecer = (ParecerInvencao) ((MovimentoCadastro) mov).getObjMovimentado();

		// Persiste o parecer e atualiza o status da Invenção
		dao.create(parecer);
		if(parecer.getStatus() != null)
			dao.updateField(Invencao.class, parecer.getInvencao().getId(), "status", parecer.getStatus());
		
		if(parecer.isNotificarEmail()){
			MailBody mail = new MailBody();
			mail.setAssunto("SIGAA - Notificação de Invenção - Parecer Emitido");
			mail.setEmail(parecer.getInvencao().getCriadoPor().getUsuario().getEmail());
			mail.setNome(parecer.getInvencao().getCriadoPor().getUsuario().getPessoa().getNome());
			mail.setMensagem(parecer.getTexto());
			
			Mail.send(mail);
		}
		
		return parecer;
	}

	
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ParecerInvencao parecer = (ParecerInvencao) ((MovimentoCadastro) mov).getObjMovimentado();
		checkValidation(parecer.validate());
	}

}
