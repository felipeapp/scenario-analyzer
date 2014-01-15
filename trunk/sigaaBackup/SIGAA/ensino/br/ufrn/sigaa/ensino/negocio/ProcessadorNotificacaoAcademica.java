package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dao.NotificacaoAcademicaDao;
import br.ufrn.sigaa.ensino.dominio.NotificacaoAcademica;
import br.ufrn.sigaa.ensino.dominio.NotificacaoAcademicaDiscente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processador para notificação acadêmica
 * 
 * @author Diego Jácome
 *
 */
public class ProcessadorNotificacaoAcademica extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,	RemoteException {

		if (mov.getCodMovimento().equals(SigaaListaComando.ENVIAR_NOTIFICACAO_ACADEMICA))
			return enviarNotificacao(mov);	
		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_NOTIFICACAO_ACADEMICA))
			return cadastrarNotificacao(mov);
		return null;
	}

	/**
	 * Envia as notificações para os discentes.
	 * @return
	 * @throws NegocioException 
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	private Object enviarNotificacao(Movimento mov) throws ArqException, NegocioException{
		
		MovimentoCadastro mc = (MovimentoCadastro) mov;
		ArrayList<NotificacaoAcademica> notificacoes = (ArrayList<NotificacaoAcademica>) mc.getColObjMovimentado();
		NotificacaoAcademicaDao dao = null;
		
		try {
			dao = getDAO(NotificacaoAcademicaDao.class, mc);
			
			if (!isEmpty(notificacoes)){
				
				ArrayList<NotificacaoAcademicaDiscente> notificacoesDiscentes = new ArrayList<NotificacaoAcademicaDiscente>();
				
				for ( NotificacaoAcademica notificacao : notificacoes ){
					Date dataCadastro = new Date();
					ArrayList<Discente> discentes = (ArrayList<Discente>) notificacao.getDiscentes();
					if ( discentes == null || (discentes != null && notificacao.isAnoPeriodoReferencia()) )
						discentes = dao.findDiscentesByFiltroNotificacao(notificacao.getSqlFiltrosDiscentes(),notificacao.isAnoPeriodoReferencia(),notificacao.getAnoReferencia(),notificacao.getPeriodoReferencia());
					if ( discentes != null ){
						for ( Discente d : discentes ){
							NotificacaoAcademicaDiscente notificacaoDiscente = new NotificacaoAcademicaDiscente();
							notificacaoDiscente.setDiscente(d);
							notificacaoDiscente.setPendente(true);
							notificacaoDiscente.setNotificacaoAcademica(notificacao);
							notificacaoDiscente.setRegistroNotificacao(mov.getUsuarioLogado().getRegistroEntrada());
							notificacaoDiscente.setDataCadastro(dataCadastro);
							
							// Dados para auditoria
							notificacaoDiscente.setMensagemNotificacao(notificacao.getMensagemNotificacao());
							notificacaoDiscente.setMensagemEmail(notificacao.getMensagemEmail());
							notificacaoDiscente.setExigeConfirmacao(notificacao.isExigeConfirmacao());
							
							notificacoesDiscentes.add(notificacaoDiscente);
						}
					}
				}
				if (notificacoesDiscentes != null && !notificacoesDiscentes.isEmpty() ){
					dao.inserirNotificacoes(notificacoesDiscentes);
					notificarDiscentes(notificacoesDiscentes);
				} else
					throw new NegocioException("Nenhum discente selecionado");
			}
		}finally {
			if ( dao !=  null )
				dao.close();
		}
		
		return null;
	}
		
	/**
	 * Cadastra notificações academicas
	 * @return
	 * @throws SQLException 
	 */
	private Object cadastrarNotificacao(Movimento mov) throws ArqException{
		MovimentoCadastro mc = (MovimentoCadastro) mov;
		NotificacaoAcademica notificacao = mc.getObjMovimentado();
		NotificacaoAcademicaDao dao = null;
		
		try {
			dao = getDAO(NotificacaoAcademicaDao.class, mc);
			if ( notificacao.getId() > 0 )
				dao.updateField(NotificacaoAcademica.class, notificacao.getId(), "ativo", false);
			notificacao.setId(0);
			dao.create(notificacao);
		
		}finally {
			if ( dao !=  null )
				dao.close();
		}
		
		return null;
	}
	
	  /**
     * Envia e-mail para os participantes do fórum.
     * 
     */
    public void notificarDiscentes(ArrayList<NotificacaoAcademicaDiscente> notificacoesDiscente) {
    		
		for (NotificacaoAcademicaDiscente n : notificacoesDiscente) {
			MailBody mail = new MailBody();
			mail.setContentType(MailBody.HTML);
			mail.setAssunto("[SIGAA] Nova Notificação Acadêmica");
			mail.setEmail(n.getDiscente().getPessoa().getEmail());
			mail.setMensagem(n.getNotificacaoAcademica().getMensagemEmail() + "<br/>Para mais informações acesse o SIGAA.");
			Mail.send(mail);
		}

    }
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {	
		
	}

}
