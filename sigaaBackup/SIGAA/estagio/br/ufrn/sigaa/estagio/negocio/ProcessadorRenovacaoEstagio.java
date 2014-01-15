/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Processador responsável pela a persistência dos dados relacionados 
 * a Renovação de Estágios.
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
			/* Atribui o status de Aguardando Relatório de Estágio, 
			 * pois só será validada a renovação após o preenchimento dos mesmos. */
			renovacao.setStatus(new StatusRenovacaoEstagio(StatusRenovacaoEstagio.AGUARDANDO_RELATORIO));					
			
			dao.create(renovacao);
			
			/* Envia a notificação para o discente, 
			 * informando da renovação do estágio */
			notificarDiscente(renovacao);
			/* Envia a notificação para o supervisor, 
			 * informando da renovação do estágio */
			notificarSupervisor(renovacao);
			/* Envia a notificação para o orientador, 
			 * informando da renovação do estágio */
			notificarOrientador(renovacao);
			
		} finally {
			if (dao != null)
				dao.close();
		}
		

		return null;
	}
	
	/**
	 * Corpo da Mensagem que será notificada aos participantes do estágio
	 * @param r
	 * @param p
	 * @return
	 */
	private String getCorpoMensagem(RenovacaoEstagio r, Pessoa p){
		StringBuffer mensagem = new StringBuffer();
		mensagem.append("Prezado(a) ");
		mensagem.append(p.getNome());
		mensagem.append(", <br/><br/>");		
		mensagem.append("Foi realizada a Renovação do Estágio abaixo:<br/><br/>");
		
		mensagem.append("<b>Estagiário:</b> "+r.getEstagio().getDiscente().getMatriculaNome());
		mensagem.append("<br/><b>Tipo do Estágio:</b> "+r.getEstagio().getTipoEstagio().getDescricao());
		mensagem.append("<br/><b>Concedente:</b> "+r.getEstagio().getConcedente().getPessoa().getNome());
		mensagem.append("<br/><b>Supervisor:</b> "+r.getEstagio().getSupervisor().getNome());
		mensagem.append("<br/><b>Orientador:</b> "+r.getEstagio().getOrientador().getPessoa().getNome());
		mensagem.append("<br/><b>Período do Estágio:</b> "+Formatador.getInstance().formatarData(r.getEstagio().getDataInicio()) +
														" a "+Formatador.getInstance().formatarData(r.getEstagio().getDataFim()));		
		mensagem.append("<br/><br/>");		
		mensagem.append("<b>Data para Renovação:</b> "+Formatador.getInstance().formatarData(r.getDataRenovacao()));
		mensagem.append("<br/><br/>");	
		mensagem.append("Para que seja validado, é necessário o Preenchimento do Relatório Periódico, localizado no ");
		return mensagem.toString();
	}
	
	/**
	 * Notifica o Discente informando da Renovação do Estágio.
	 * @param r
	 */
	private void notificarDiscente(RenovacaoEstagio r){

		StringBuffer mensagem = new StringBuffer();
		mensagem.append(getCorpoMensagem(r, r.getEstagio().getDiscente().getPessoa()));
		mensagem.append("Portal do Discente -> Estágio -> Gerenciar Estágios.");
		
		enviarMensagem(r.getEstagio().getDiscente().getPessoa(), mensagem.toString());
	}
	
	/**
	 * Notifica o Supervisor informando da Renovação do Estágio.
	 * @param r
	 */
	private void notificarSupervisor(RenovacaoEstagio r){

		StringBuffer mensagem = new StringBuffer();
		mensagem.append(getCorpoMensagem(r, r.getEstagio().getSupervisor()));
		mensagem.append("Portal do Concedente de Estágio -> Estágio -> Gerenciar Estágios.");
		
		enviarMensagem(r.getEstagio().getSupervisor(), mensagem.toString());
	}	
	
	/**
	 * Notifica o Orientador informando da Renovação do Estágio.
	 * @param r
	 */
	private void notificarOrientador(RenovacaoEstagio r){

		StringBuffer mensagem = new StringBuffer();
		mensagem.append(getCorpoMensagem(r, r.getEstagio().getOrientador().getPessoa()));
		mensagem.append("Portal do Docente -> Ensino -> Orientação de Estágio -> Gerenciar Estágios.");
		
		enviarMensagem(r.getEstagio().getOrientador().getPessoa(), mensagem.toString());
	}		
	
	/**
	 * Método responsável pela construção e envio do email.
	 * @param p
	 * @param assunto
	 * @param conteudo
	 * @param complemento
	 */
	private void enviarMensagem(Pessoa p, String mensagem) {
		MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setAssunto("SIGAA - Renovação de Estágio - MENSAGEM AUTOMÁTICA");
		mail.setMensagem(mensagem.toString());
		mail.setEmail(p.getEmail());
		mail.setNome(p.getNome());
		Mail.send(mail);
	}	

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {

		
	}

}
