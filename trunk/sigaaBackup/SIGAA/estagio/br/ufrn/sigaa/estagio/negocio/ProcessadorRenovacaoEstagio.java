/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 18/11/2010
 */
package br.ufrn.sigaa.estagio.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.dao.estagio.RenovacaoEstagioDao;
import br.ufrn.sigaa.estagio.dominio.RenovacaoEstagio;
import br.ufrn.sigaa.estagio.dominio.StatusRenovacaoEstagio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador respons�vel pela a persist�ncia dos dados relacionados 
 * a Renova��o de Est�gios.
 * 
 * @author Arlindo Rodrigues
 *
 */
public class ProcessadorRenovacaoEstagio extends AbstractProcessador  {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		RenovacaoEstagio renovacao = ((MovimentoCadastro)mov).getObjMovimentado();
		RenovacaoEstagioDao dao = getDAO(RenovacaoEstagioDao.class, mov);
		try {
			/* Atribui o status de Aguardando Relat�rio de Est�gio, 
			 * pois s� ser� validada a renova��o ap�s o preenchimento dos mesmos. */
			renovacao.setStatus(new StatusRenovacaoEstagio(StatusRenovacaoEstagio.AGUARDANDO_RELATORIO));					
			
			dao.create(renovacao);
			
			/* Envia a notifica��o para o discente, 
			 * informando da renova��o do est�gio */
			notificarDiscente(renovacao);
			/* Envia a notifica��o para o supervisor, 
			 * informando da renova��o do est�gio */
			notificarSupervisor(renovacao);
			/* Envia a notifica��o para o orientador, 
			 * informando da renova��o do est�gio */
			notificarOrientador(renovacao);
			
		} finally {
			if (dao != null)
				dao.close();
		}
		

		return null;
	}
	
	/**
	 * Corpo da Mensagem que ser� notificada aos participantes do est�gio
	 * @param r
	 * @param p
	 * @return
	 */
	private String getCorpoMensagem(RenovacaoEstagio r, Pessoa p){
		StringBuffer mensagem = new StringBuffer();
		mensagem.append("Prezado(a) ");
		mensagem.append(p.getNome());
		mensagem.append(", <br/><br/>");		
		mensagem.append("Foi realizada a Renova��o do Est�gio abaixo:<br/><br/>");
		
		mensagem.append("<b>Estagi�rio:</b> "+r.getEstagio().getDiscente().getMatriculaNome());
		mensagem.append("<br/><b>Tipo do Est�gio:</b> "+r.getEstagio().getTipoEstagio().getDescricao());
		mensagem.append("<br/><b>Concedente:</b> "+r.getEstagio().getConcedente().getPessoa().getNome());
		mensagem.append("<br/><b>Supervisor:</b> "+r.getEstagio().getSupervisor().getNome());
		mensagem.append("<br/><b>Orientador:</b> "+r.getEstagio().getOrientador().getPessoa().getNome());
		mensagem.append("<br/><b>Per�odo do Est�gio:</b> "+Formatador.getInstance().formatarData(r.getEstagio().getDataInicio()) +
														" a "+Formatador.getInstance().formatarData(r.getEstagio().getDataFim()));		
		mensagem.append("<br/><br/>");		
		mensagem.append("<b>Data para Renova��o:</b> "+Formatador.getInstance().formatarData(r.getDataRenovacao()));
		mensagem.append("<br/><br/>");	
		mensagem.append("Para que seja validado, � necess�rio o Preenchimento do Relat�rio Peri�dico, localizado no ");
		return mensagem.toString();
	}
	
	/**
	 * Notifica o Discente informando da Renova��o do Est�gio.
	 * @param r
	 */
	private void notificarDiscente(RenovacaoEstagio r){

		StringBuffer mensagem = new StringBuffer();
		mensagem.append(getCorpoMensagem(r, r.getEstagio().getDiscente().getPessoa()));
		mensagem.append("Portal do Discente -> Est�gio -> Gerenciar Est�gios.");
		
		enviarMensagem(r.getEstagio().getDiscente().getPessoa(), mensagem.toString());
	}
	
	/**
	 * Notifica o Supervisor informando da Renova��o do Est�gio.
	 * @param r
	 */
	private void notificarSupervisor(RenovacaoEstagio r){

		StringBuffer mensagem = new StringBuffer();
		mensagem.append(getCorpoMensagem(r, r.getEstagio().getSupervisor()));
		mensagem.append("Portal do Concedente de Est�gio -> Est�gio -> Gerenciar Est�gios.");
		
		enviarMensagem(r.getEstagio().getSupervisor(), mensagem.toString());
	}	
	
	/**
	 * Notifica o Orientador informando da Renova��o do Est�gio.
	 * @param r
	 */
	private void notificarOrientador(RenovacaoEstagio r){

		StringBuffer mensagem = new StringBuffer();
		mensagem.append(getCorpoMensagem(r, r.getEstagio().getOrientador().getPessoa()));
		mensagem.append("Portal do Docente -> Ensino -> Orienta��o de Est�gio -> Gerenciar Est�gios.");
		
		enviarMensagem(r.getEstagio().getOrientador().getPessoa(), mensagem.toString());
	}		
	
	/**
	 * M�todo respons�vel pela constru��o e envio do email.
	 * @param p
	 * @param assunto
	 * @param conteudo
	 * @param complemento
	 */
	private void enviarMensagem(Pessoa p, String mensagem) {
		MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setAssunto("SIGAA - Renova��o de Est�gio - MENSAGEM AUTOM�TICA");
		mail.setMensagem(mensagem.toString());
		mail.setEmail(p.getEmail());
		mail.setNome(p.getNome());
		Mail.send(mail);
	}	

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {

		
	}

}
