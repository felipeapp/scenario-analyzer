package br.ufrn.sigaa.apedagogica.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.sigaa.apedagogica.dao.ParticipanteAtividadeAPDAO;
import br.ufrn.sigaa.apedagogica.dominio.NotificacaoParticipanteAtividade;
import br.ufrn.sigaa.apedagogica.dominio.ParticipanteAtividadeAtualizacaoPedagogica;

public class ProcessadorNotificarParticipanteAP extends AbstractProcessador  {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		MovimentoNotificarParticipanteAP notificaMov = (MovimentoNotificarParticipanteAP) mov; 
		NotificacaoParticipanteAtividade notificacao = notificaMov.getNotificacao();
		Collection<ParticipanteAtividadeAtualizacaoPedagogica> participantes = notificaMov.getParticipantes();
		ParticipanteAtividadeAPDAO dao = getDAO(ParticipanteAtividadeAPDAO.class, notificaMov);
		
		for (ParticipanteAtividadeAtualizacaoPedagogica p : participantes){ 

			String nome = p.getDocente().getPessoa().getNome();
			String email = p.getDocente().getPessoa().getEmail();
			
			if( !isEmpty(nome) && !isEmpty(email) && p.isSelecionado() ){
			
				MailBody mail = new MailBody();
				mail.setContentType(MailBody.TEXT_PLAN);
				mail.setAssunto(notificacao.getTitulo());
				mail.setMensagem("Prezado(a) " + nome + ", \n\n" + notificacao.getMensagem()+ ", \n\n Att,\n Programa de Atualização Pedagógica ");
				mail.setEmail( email );
				mail.setNome(RepositorioDadosInstitucionais.get("nomeInstituicao"));
				mail.setFromName("Programa de Atualização Pedagógica - " + RepositorioDadosInstitucionais.get("nomeInstituicao"));
				Mail.send(mail);
			
			}	

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

	}
	
}
