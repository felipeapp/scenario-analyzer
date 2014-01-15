/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/11/2006
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.mensagens.MensagensMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;
import br.ufrn.sigaa.monitoria.dominio.RelatorioMonitor;
import br.ufrn.sigaa.monitoria.dominio.StatusRelatorio;
import br.ufrn.sigaa.monitoria.dominio.TipoRelatorioMonitoria;


/**
 * Processador para cadastro de relat�rios finais de monitoria.
 *
 * @author ilueny santos
 *
 */
public class ProcessadorCadastroRelatorioMonitor extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		
		/* @negocio: � permitido ao monitor cadastra o relat�rio e concluir e enviar o relat�rio depois. */
		if ( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.CADASTRAR_RELATORIO_MONITOR)){

			GenericDAO dao = getGenericDAO(mov);
			try {
			    RelatorioMonitor rm = (RelatorioMonitor) cMov.getObjMovimentado();
			    rm.setStatus(new StatusRelatorio(StatusRelatorio.CADASTRO_EM_ANDAMENTO));				
			    rm.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			    rm.setAtivo(true);				
			    dao.createOrUpdate(rm);
			    return rm;
			} finally {
			    dao.close();			    
			}
		}

		/* @negocio: O discente de monitoria conclui o cadastro e envia o relat�rio para ci�ncia do coordenador e da Pr�-Reitoria de Gradua��o. */
		if ( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.ENVIAR_RELATORIO_MONITOR)){

			RelatorioMonitor rm = (RelatorioMonitor) cMov.getObjMovimentado();
			GenericDAO dao = getGenericDAO(mov);
			try {			
			    getGenericDAO(mov).initialize(rm.getProjetoEnsino());
			    rm.getProjetoEnsino().setEquipeDocentes( getGenericDAO(mov).findByExactField(EquipeDocente.class, "projetoEnsino.id", rm.getProjetoEnsino().getId()));			
			    getGenericDAO(mov).initialize(rm.getDiscenteMonitoria());
			    rm.getDiscenteMonitoria().setOrientacoes(getGenericDAO(mov).findByExactField(Orientacao.class, "discenteMonitoria.id", rm.getDiscenteMonitoria().getId()));
			
			    /* @negocio: Relat�rios de desligamento do monitor precisam ser validados pelo coordenador do projeto e pela Pr�-Reitoria de Gradua��o. */
			    if (rm.getTipoRelatorio().getId() == TipoRelatorioMonitoria.RELATORIO_DESLIGAMENTO_MONITOR) {
				rm.setStatus(new StatusRelatorio(StatusRelatorio.AGUARDANDO_AVALIACAO));
					
				/* @negocio: Enviando e-mail para o coordenador avisando do relat�rio de desligamento do monitor. */
				if (rm.getProjetoEnsino().getCoordenacao().getPessoa().getEmail() != null){
				    enviarEmailCoordenador(rm);						
				}

			    }else {
				/* @negocio: Outros tipos de relat�rios s�o avaliados diretamente (N�O necessitam de aprova��o do coordenador ou da Pr�-Reitoria). */
				rm.setStatus(new StatusRelatorio(StatusRelatorio.AVALIADO));
			    }
				
			    if (rm.getDataCadastro() == null) {
				rm.setDataCadastro(new Date());
			    }				
			    rm.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			    rm.setAtivo(true);
			    rm.setDataEnvio(new Date());			    
			    dao.createOrUpdate(rm);
			    return rm;
			} finally {
			    dao.close();			    
			}
		}
		

		/* @negocio: Esta a��o permite que o gestor de monitoria devolva os relat�rio para os monitores que erraram o cadastro. */  
		if ( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.DEVOLVER_RELATORIO_MONITOR)){
			
			GenericDAO dao = getGenericDAO(mov);
			try {
           			RelatorioMonitor rm = (RelatorioMonitor) cMov.getObjMovimentado();
           			rm.setRegistroEntradaDevolucaoReedicao(mov.getUsuarioLogado().getRegistroEntrada());
           			rm.setAtivo(true);
           				
           			//cancelando as �ltimas valida��es da coordena��o e da prograd
           			rm.setCoordenacaoValidouDesligamento(null);
           			rm.setObservacaoCoordenacaoDesligamento(null);
           			rm.setRegistroValidacaoCoordenacaoDesligamento(null);
           				
           			rm.setProgradValidouDesligamento(null);
           			rm.setObservacaoProgradDesligamento(null);
           			rm.setRegistroValidacaoProgradDesligamento(null);
           				
           			//cancelando o �ltimo envio
           			rm.setStatus(new StatusRelatorio(StatusRelatorio.CADASTRO_EM_ANDAMENTO));				
           			rm.setDataEnvio(null);           				
           			dao.update(rm);
        		} finally {
        		    dao.close();			    
        		}
		}
		
		
		/* @negocio: A coordena��o do projeto deve validar o relat�rio de desligamento do monitor. */
		if ( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.COORDENACAO_VALIDAR_RELATORIO_MONITOR)){

		    GenericDAO dao = getGenericDAO(mov);				
		    try {
		    	RelatorioMonitor rm = (RelatorioMonitor) cMov.getObjMovimentado();
		    	/* @negocio: aguardando valida��o da prograd. */
		    	if ( rm.getCoordenacaoValidouDesligamento() != null && !rm.getCoordenacaoValidouDesligamento() )
		    		rm.setStatus(new StatusRelatorio(StatusRelatorio.AVALIADO));
		    	else
		    		rm.setStatus(new StatusRelatorio(StatusRelatorio.AGUARDANDO_AVALIACAO));
		    	rm.setRegistroValidacaoCoordenacaoDesligamento(mov.getUsuarioLogado().getRegistroEntrada());
		    	dao.update(rm);
		    	return rm;
		    } finally {
		    	dao.close();			    
		    }
		}
		

		/* @negocio: A Pr�-Reitoria de Gradua��o deve validar o relat�rio de desligamento do monitor. */
		if ( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.PROGRAD_VALIDAR_RELATORIO_MONITOR)){

		    GenericDAO dao = getGenericDAO(mov);				
		    try {
			RelatorioMonitor rm = (RelatorioMonitor) cMov.getObjMovimentado();
			rm.setStatus(new StatusRelatorio(StatusRelatorio.AVALIADO));
			rm.setRegistroValidacaoProgradDesligamento(mov.getUsuarioLogado().getRegistroEntrada());
			dao.update(rm);
			
			if( rm.getProgradValidouDesligamento() ) {
				
				//Finalizar				
				DiscenteMonitoriaMov movFinalizar = new DiscenteMonitoriaMov();
				movFinalizar.setUsuarioLogado(mov.getUsuarioLogado());
				movFinalizar.setDiscenteMonitoria(rm.getDiscenteMonitoria());
				movFinalizar.setCodMovimento(SigaaListaComando.FINALIZAR_DISCENTEMONITORIA);
				// permite que a prograd execute procedimentos sem realizar valida��o
				movFinalizar.setValidar(true);
				movFinalizar.setValidarRelatorios(false);
				ProcessadorDiscenteMonitoria processador = new ProcessadorDiscenteMonitoria();
				processador.execute(movFinalizar);
				
			}
			
			
			return rm;
		    } finally {
			dao.close();			    
		    }
		}
		
		return null;
	}

	/**
	 * M�todo respons�vel por enviar e-mail para o coordenador do projeto 
	 * informando que o discente solicitou desligamento do projeto.
	 * 
	 * @param rm RelatorioMonitor
	 */
	private void enviarEmailCoordenador(RelatorioMonitor rm) {	    
										
		MailBody email = new MailBody();
		email.setAssunto("[SIGAA] Solicita��o de Desligamento de Monitor");
		email.setContentType(MailBody.HTML);
		email.setNome(rm.getProjetoEnsino().getTitulo());
		email.setEmail(rm.getProjetoEnsino().getEmail());			

		StringBuffer msg = new StringBuffer();
			msg.append("Sr(a). Coordenador(a),<br/>");
			msg.append("Um novo Relat�rio de Desligamento de Monitor foi cadastrado no SIGAA e precisa de sua valida��o.<br/>");
			msg.append("Para validar o relat�rio acesse:<br/> <i>Portal Docente -> Monitoria -> Coordena��o de Projetos -> Validar Relat�rios de Desligamento.</i> <br/><br/>");
					
			msg.append("<b><i>DADOS DO PROJETO:</i> </b><br/>");
			msg.append("<b>Ano do Projeto:</b> " + rm.getProjetoEnsino().getAno() +"<br/>");
			msg.append("<b>T�tulo do Projeto:</b> " + rm.getProjetoEnsino().getTitulo() + "<br/>" );
			msg.append("<b>Coordenador(a):</b> " + rm.getProjetoEnsino().getCoordenacao().getPessoa().getNome() + "<br/><br/>" );
			
			msg.append("<b><i>DADOS DA MONITORIA QUE SOLICITOU O DESLIGAMENTO: </i></b><br/>");
			msg.append("<b>Monitor(a):</b> " + rm.getDiscenteMonitoria().getDiscente().getMatriculaNome() + "<br/>" );
			msg.append("<b>Curso:</b> " + rm.getDiscenteMonitoria().getDiscente().getCurso().getDescricao() + "<br/>" );
			msg.append("<b>In�cio de Monitoria:</b> " + rm.getDiscenteMonitoria().getDataInicio() + "<br/>" );
			
			StringBuffer orientadores = new StringBuffer();
			for (Orientacao ori : rm.getDiscenteMonitoria().getOrientacoes()) {
				orientadores.append(ori.getEquipeDocente().getServidor().getNome() + ", ");													
			}
			msg.append("<b>Orienta��es: </b>" + orientadores + "<br/><br/>" );												
			
			msg.append("<hr/>" +
			"Esta mensagem foi gerada automaticamente pelo Sistema Integrado de Gest�o de Atividades Acad�micas.<br/>" +
			"N�o � necess�rio respond�-la.<br/>"+ 
			"SIGAA - Sistema Integrado de Gest�o de Atividades Acad�micas.<br/>" +
			"Universidade Federal do Rio Grande do Norte.<br/>");

		email.setMensagem(msg.toString());
		Mail.send(email);
	}
	

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens lista = new ListaMensagens();
		RelatorioMonitor rel = ((RelatorioMonitor)((MovimentoCadastro)mov).getObjMovimentado());
		
		/* @negocio: Somente o gestor de monitoria pode devolver o relat�rio para que o monitor reenvie-o. */
		if ( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.DEVOLVER_RELATORIO_MONITOR)){
			checkRole(SigaaPapeis.GESTOR_MONITORIA, mov);
		}
		
		if ( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.CADASTRAR_RELATORIO_MONITOR)){
			if (rel.getDataEnvio() != null) {
			    lista.addMensagem(new MensagemAviso(MensagensMonitoria.RELATORIO_JA_ENVIADO_PROGRAD, TipoMensagemUFRN.ERROR));
			}
		}
		
		if ( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.ENVIAR_RELATORIO_MONITOR)){			
			if (rel.getTipoRelatorio().getId() == TipoRelatorioMonitoria.RELATORIO_PARCIAL) {
			    RelatorioMonitoriaValidator.validaEnvioRelatorioParcialMonitor(rel.getDiscenteMonitoria(), lista);
			}
			
			if (rel.getTipoRelatorio().getId() == TipoRelatorioMonitoria.RELATORIO_FINAL) {
				RelatorioMonitoriaValidator.validaEnvioRelatorioFinalMonitor(rel.getDiscenteMonitoria(), lista);
			}
		}

		/* @negocio: 
		 * 	Para que o desligamento do monitor seja realizado com sucesso no SIGAA ele precisa da valida��o do relat�rio de desligamento.
		 * 	O relat�rio de desligamento s� � validado se o monitor n�o tiver mais bolsas ativas de monitoria no SIPAC.
		 * 	Isso garante que o monitor s� seja finalizado no SIGAA quando for finalizado antes no SIPAC.
		 * 
		 */
		if ( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.PROGRAD_VALIDAR_RELATORIO_MONITOR)){
			checkRole(SigaaPapeis.GESTOR_MONITORIA, mov);			
			if ((rel.getProgradValidouDesligamento() != null) && (rel.getProgradValidouDesligamento())){
				RelatorioMonitoriaValidator.validaDiscenteComBolsaMonitoriaAtivaSIPAC(rel.getDiscenteMonitoria(), lista );
			}			
		}
		checkValidation(lista);
	}

}