package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;
import java.util.Map;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.sigaa.ensino.dominio.NotificacaoProcessoSeletivo;
import br.ufrn.sigaa.ensino.negocio.dominio.MovimentoNotificarProcessoSeletivo;

/**
 * Processador responsável pelo cadastro das notificações dos inscritos no processo seletivo.
 * @author Mário Rizzi
 */
public class ProcessadorNotificarInscritos extends AbstractProcessador  {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		MovimentoNotificarProcessoSeletivo notificaMov = (MovimentoNotificarProcessoSeletivo) mov; 
		NotificacaoProcessoSeletivo notificacao = notificaMov.getNotificacao();
		Map<String, String> destinatarios = notificaMov.getDestinatarios();
		
		GenericDAO dao = getGenericDAO(notificaMov);

		for (Map.Entry<String, String> d : destinatarios.entrySet()){ 

			String nome = d.getValue();
			String email = d.getKey();
			
			MailBody mail = new MailBody();
			mail.setContentType(MailBody.TEXT_PLAN);
			mail.setAssunto(notificacao.getTitulo());
			mail.setMensagem("Prezado(a) " + nome + ", \n\n" + notificacao.getMensagem());
			mail.setEmail(email);
			mail.setNome(RepositorioDadosInstitucionais.get("nomeInstituicao"));

			if(email != null)
				Mail.send(mail);

		}
		
		try{
			dao.create(notificacao);
		}finally{
			dao.close();
		}
		
		return notificacao;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoNotificarProcessoSeletivo notificaMov = (MovimentoNotificarProcessoSeletivo) mov; 
		NotificacaoProcessoSeletivo notificacao = notificaMov.getNotificacao();
		
		ListaMensagens erros = new ListaMensagens();
		erros.addAll( notificacao.validate() );

		checkValidation(erros);
		
	}

}
