/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/10/2007
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.monitoria.AtividadeMonitorDao;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.CancelamentoBolsa;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.parametros.dominio.ParametrosMonitoria;
import br.ufrn.sigaa.pessoa.dominio.Discente;


/**
 * 
 * Classe utilizada no processo de cancelamento de bolsas de monitores.
 * 
 * @author Ilueny Santos
 *
 */
@Component("cancelarBolsaMonitoria")
@Scope("session")
public class CancelarBolsaMonitoriaMBean extends SigaaAbstractController<CancelamentoBolsa> {

	/** Discentes listados que terão a bolsa cancelada. */
	private Collection<DiscenteMonitoria> discentes =  new ArrayList<DiscenteMonitoria>();	
	/** Tipo do vínculo do discente monitoria. */
	private Integer tipoVinculo = null;	
	/** mensagem utilizada na notificação de alunos inadimplentes */
	private String mensagem;
	
	
	
	/**
	 * Construtor Padrão.
	 */
	public CancelarBolsaMonitoriaMBean() {		
		obj = new CancelamentoBolsa();
		obj.setMesInicio(CalendarUtils.getMesAtual()-3);
		obj.setMesFim(CalendarUtils.getMesAtual()-1);
		obj.setAno(CalendarUtils.getAnoAtual());		
	}

	
	/**
	 * Usado para enviar e-mails para alunos inadimplentes.
	 * 
	 * JSP: sigaa.war/monitoria/CancelarBolsas/form.jsp
	 */
	public String notificarMonitores() {
	    try {
		if( ValidatorUtil.isEmpty(discentes)) {
		    setMensagem("");
		    addMensagemErro("Não foram selecionados discentes para envio da notificação.");
		    return null;
		}		

		for (DiscenteMonitoria discenteMonitoria : discentes) {			
		    if( !discenteMonitoria.getDiscente().isSelecionado()) {
			continue;
		    }			
		    MailBody mail = new MailBody();
		    mail.setAssunto("Notificação - Relatório de Frequência de Monitores");
		    mail.setFromName("Gestor de Monitoria");			
		    mail.setEmail( discenteMonitoria.getDiscente().getPessoa().getEmail() );
		    mail.setMensagem(getMensagem() + "<br /><br /><br />" + "Ano Projeto: " + discenteMonitoria.getProjetoEnsino().getAno() + "<br />" + "Projeto: " + discenteMonitoria.getProjetoEnsino().getTitulo()  );
		    Mail.send(mail);			
		}	
		setMensagem("");
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);	
		return iniciarCancelamentoBolsas();
	    }catch (Exception e) {
		tratamentoErroPadrao(e);
		return null;
	    }
	}
	

	/**
	 * Prepara movimento de cancelamento de bolsas.<br />
	 * Chamado por:
	 * <ul>
	 *  <li>JSP: sigaa.war/monitoria/index.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws ArqException 
	 */
	public String iniciarCancelamentoBolsas() throws SegurancaException  {
	    checkChangeRole();
	    try {
		discentes = null;
		prepareMovimento(SigaaListaComando.CANCELAR_BOLSAS_PROJETO_MONITORIA);
		return forward(getFormPage());
	    } catch (ArqException e) {
		tratamentoErroPadrao(e);
		return null;
	    }
	}
	
	/**
	 * Lista as atividades do Monitor
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/monitoria/CancelarBolsas/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws SegurancaException 
	 */
	public String listarAtividadesMonitor() throws SegurancaException {
	    checkChangeRole();			
	    try {
		AtividadeMonitorMBean atividadeMonitor = (AtividadeMonitorMBean) getMBean("atividadeMonitor");
		atividadeMonitor.setCheckBuscaDiscente(true);
		int idDiscente = getParameterInt("idDiscente");
		atividadeMonitor.setDiscente(new Discente(idDiscente));
		atividadeMonitor.buscar();
		return forward(getListPage());
	    } catch (DAOException e) {
		tratamentoErroPadrao(e);
		return null;
	    }
	}
	
	
	
	/**
	 * Preenche coleção de discentes com discentes inadimplentes.
	 * <br /> 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>JSP: sigaa.war/monitoria/CancelarBolsas/form.jsp<li>
	 * </ul>
	 * @return
	 */
	public String buscar() {
	    ListaMensagens mensagens = obj.validate();	    
	    if (!mensagens.isEmpty()) {
		addMensagens(mensagens);
		return null;
	    }
	    
	    try {
	   
		    if (ParametroHelper.getInstance().getParametroBoolean(ParametrosMonitoria.FREQUENCIA_MONITORIA)) 	
		    	discentes =  getDAO(AtividadeMonitorDao.class).findByDiscentesSemAtividadeNoPeriodo(obj.getMesInicio(), obj.getMesFim(), obj.getAno(), tipoVinculo);
		    else
		    	discentes = getDAO(DiscenteMonitoriaDao.class).findByDiscentesACancelar(obj.getAno(), tipoVinculo);
	    
	    } catch (DAOException e) {
		tratamentoErroPadrao(e);
	    }		
	    return null;
	}
	
	/**
	 * 
	 * Cancela bolsas de monitores.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/monitoria/CancelarBolsas/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String cancelarBolsaMonitoria() throws SegurancaException  {
		checkChangeRole();			
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.CANCELAR_BOLSAS_PROJETO_MONITORIA);
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		return forward("/monitoria/index.jsf");
	}	
	
	@Override
	public String getDirBase() {
		return "/monitoria/CancelarBolsas";
	}
	

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
	}

	public Collection<DiscenteMonitoria> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<DiscenteMonitoria> discentes) {
		this.discentes = discentes;
	}
	
	public Integer getTipoVinculo() {
		return tipoVinculo;
	}

	public void setTipoVinculo(Integer tipoVinculo) {
		this.tipoVinculo = tipoVinculo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	
}