/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Processador para cadastro de relatórios finais de monitoria.
 *
 * @author ilueny santos
 *
 */
public class ProcessadorCadastroRelatorioMonitor extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		
		/* @negocio: É permitido ao monitor cadastra o relatório e concluir e enviar o relatório depois. */
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

		/* @negocio: O discente de monitoria conclui o cadastro e envia o relatório para ciência do coordenador e da Pró-Reitoria de Graduação. */
		if ( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.ENVIAR_RELATORIO_MONITOR)){

			RelatorioMonitor rm = (RelatorioMonitor) cMov.getObjMovimentado();
			GenericDAO dao = getGenericDAO(mov);
			try {			
			    getGenericDAO(mov).initialize(rm.getProjetoEnsino());
			    rm.getProjetoEnsino().setEquipeDocentes( getGenericDAO(mov).findByExactField(EquipeDocente.class, "projetoEnsino.id", rm.getProjetoEnsino().getId()));			
			    getGenericDAO(mov).initialize(rm.getDiscenteMonitoria());
			    rm.getDiscenteMonitoria().setOrientacoes(getGenericDAO(mov).findByExactField(Orientacao.class, "discenteMonitoria.id", rm.getDiscenteMonitoria().getId()));
			
			    /* @negocio: Relatórios de desligamento do monitor precisam ser validados pelo coordenador do projeto e pela Pró-Reitoria de Graduação. */
			    if (rm.getTipoRelatorio().getId() == TipoRelatorioMonitoria.RELATORIO_DESLIGAMENTO_MONITOR) {
				rm.setStatus(new StatusRelatorio(StatusRelatorio.AGUARDANDO_AVALIACAO));
					
				/* @negocio: Enviando e-mail para o coordenador avisando do relatório de desligamento do monitor. */
				if (rm.getProjetoEnsino().getCoordenacao().getPessoa().getEmail() != null){
				    enviarEmailCoordenador(rm);						
				}

			    }else {
				/* @negocio: Outros tipos de relatórios são avaliados diretamente (NÃO necessitam de aprovação do coordenador ou da Pró-Reitoria). */
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
		

		/* @negocio: Esta ação permite que o gestor de monitoria devolva os relatório para os monitores que erraram o cadastro. */  
		if ( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.DEVOLVER_RELATORIO_MONITOR)){
			
			GenericDAO dao = getGenericDAO(mov);
			try {
           			RelatorioMonitor rm = (RelatorioMonitor) cMov.getObjMovimentado();
           			rm.setRegistroEntradaDevolucaoReedicao(mov.getUsuarioLogado().getRegistroEntrada());
           			rm.setAtivo(true);
           				
           			//cancelando as últimas validações da coordenação e da prograd
           			rm.setCoordenacaoValidouDesligamento(null);
           			rm.setObservacaoCoordenacaoDesligamento(null);
           			rm.setRegistroValidacaoCoordenacaoDesligamento(null);
           				
           			rm.setProgradValidouDesligamento(null);
           			rm.setObservacaoProgradDesligamento(null);
           			rm.setRegistroValidacaoProgradDesligamento(null);
           				
           			//cancelando o último envio
           			rm.setStatus(new StatusRelatorio(StatusRelatorio.CADASTRO_EM_ANDAMENTO));				
           			rm.setDataEnvio(null);           				
           			dao.update(rm);
        		} finally {
        		    dao.close();			    
        		}
		}
		
		
		/* @negocio: A coordenação do projeto deve validar o relatório de desligamento do monitor. */
		if ( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.COORDENACAO_VALIDAR_RELATORIO_MONITOR)){

		    GenericDAO dao = getGenericDAO(mov);				
		    try {
		    	RelatorioMonitor rm = (RelatorioMonitor) cMov.getObjMovimentado();
		    	/* @negocio: aguardando validação da prograd. */
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
		

		/* @negocio: A Pró-Reitoria de Graduação deve validar o relatório de desligamento do monitor. */
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
				// permite que a prograd execute procedimentos sem realizar validação
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
	 * Método responsável por enviar e-mail para o coordenador do projeto 
	 * informando que o discente solicitou desligamento do projeto.
	 * 
	 * @param rm RelatorioMonitor
	 */
	private void enviarEmailCoordenador(RelatorioMonitor rm) {	    
										
		MailBody email = new MailBody();
		email.setAssunto("[SIGAA] Solicitação de Desligamento de Monitor");
		email.setContentType(MailBody.HTML);
		email.setNome(rm.getProjetoEnsino().getTitulo());
		email.setEmail(rm.getProjetoEnsino().getEmail());			

		StringBuffer msg = new StringBuffer();
			msg.append("Sr(a). Coordenador(a),<br/>");
			msg.append("Um novo Relatório de Desligamento de Monitor foi cadastrado no SIGAA e precisa de sua validação.<br/>");
			msg.append("Para validar o relatório acesse:<br/> <i>Portal Docente -> Monitoria -> Coordenação de Projetos -> Validar Relatórios de Desligamento.</i> <br/><br/>");
					
			msg.append("<b><i>DADOS DO PROJETO:</i> </b><br/>");
			msg.append("<b>Ano do Projeto:</b> " + rm.getProjetoEnsino().getAno() +"<br/>");
			msg.append("<b>Título do Projeto:</b> " + rm.getProjetoEnsino().getTitulo() + "<br/>" );
			msg.append("<b>Coordenador(a):</b> " + rm.getProjetoEnsino().getCoordenacao().getPessoa().getNome() + "<br/><br/>" );
			
			msg.append("<b><i>DADOS DA MONITORIA QUE SOLICITOU O DESLIGAMENTO: </i></b><br/>");
			msg.append("<b>Monitor(a):</b> " + rm.getDiscenteMonitoria().getDiscente().getMatriculaNome() + "<br/>" );
			msg.append("<b>Curso:</b> " + rm.getDiscenteMonitoria().getDiscente().getCurso().getDescricao() + "<br/>" );
			msg.append("<b>Início de Monitoria:</b> " + rm.getDiscenteMonitoria().getDataInicio() + "<br/>" );
			
			StringBuffer orientadores = new StringBuffer();
			for (Orientacao ori : rm.getDiscenteMonitoria().getOrientacoes()) {
				orientadores.append(ori.getEquipeDocente().getServidor().getNome() + ", ");													
			}
			msg.append("<b>Orientações: </b>" + orientadores + "<br/><br/>" );												
			
			msg.append("<hr/>" +
			"Esta mensagem foi gerada automaticamente pelo Sistema Integrado de Gestão de Atividades Acadêmicas.<br/>" +
			"Não é necessário respondê-la.<br/>"+ 
			"SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas.<br/>" +
			"Universidade Federal do Rio Grande do Norte.<br/>");

		email.setMensagem(msg.toString());
		Mail.send(email);
	}
	

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens lista = new ListaMensagens();
		RelatorioMonitor rel = ((RelatorioMonitor)((MovimentoCadastro)mov).getObjMovimentado());
		
		/* @negocio: Somente o gestor de monitoria pode devolver o relatório para que o monitor reenvie-o. */
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
		 * 	Para que o desligamento do monitor seja realizado com sucesso no SIGAA ele precisa da validação do relatório de desligamento.
		 * 	O relatório de desligamento só é validado se o monitor não tiver mais bolsas ativas de monitoria no SIPAC.
		 * 	Isso garante que o monitor só seja finalizado no SIGAA quando for finalizado antes no SIPAC.
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