/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 15/08/2007
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.CalendarioMonitoria;



/**
 * MBean responsável por cadastro do calendário de monitoria
 * @author Ilueny
 *
 */
@SuppressWarnings("serial")
@Component("calendarioMonitoria")@Scope("request")
public class CalendarioMonitoriaMBean extends SigaaAbstractController<CalendarioMonitoria> {

	private CalendarioMonitoria calendario = new CalendarioMonitoria();
	

	private void initObj() {
		if (obj == null)
			obj = new CalendarioMonitoria();
	}

	/** Lista dos Currículos por série de ensino médio. */
	private List<SelectItem> calendariosAtivos = new ArrayList<SelectItem>(0);
	
	/**
	 * redireciona para página de cadastro do calendário de monitoria.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\index.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarCadastroCalendario() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		try {
			initObj();
			prepareMovimento(SigaaListaComando.ALTERAR_CALENDARIO_MONITORIA);
			setConfirmButton("Cadastrar Calendário");
			calendariosAtivos = toSelectItems(getGenericDAO().findAllAtivos(CalendarioMonitoria.class, "anoReferencia" ), "id", "anoReferencia" );
			return forward(getFormPage());
		} catch (Exception e) {
			notifyError(e);
			return null;
		}
	}
	
	/**
	 * Cadastra novo calendário de monitoria.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\CalendarioMonitoria\form.jsp</li
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		
		try {
			
			erros.addAll(  obj.validate().getMensagens()  );
			
			if (hasErrors())
				return null;
			
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			if(obj.getId() > 0)
				mov.setCodMovimento(SigaaListaComando.ALTERAR_CALENDARIO_MONITORIA);
			else
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_CALENDARIO_MONITORIA);

			execute(mov, getCurrentRequest());
			
		} catch(Exception e) {
			addMensagemErro(e.getMessage());
			return null;
		}

		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);		
		return forward("/monitoria/index.jsf");
	
	}

	public void carregarCalendario(ActionEvent ev) throws ArqException{
		GenericDAO dao = getGenericDAO();
		try {
			if(obj.getId() == 0){
				setConfirmButton("Cadastrar Calendário");
				obj = new CalendarioMonitoria();
				prepareMovimento(SigaaListaComando.CADASTRAR_CALENDARIO_MONITORIA);
			}else{
				setConfirmButton("Alterar Calendário");
				setObj(dao.findByPrimaryKey(obj.getId(), CalendarioMonitoria.class));
			}
		} finally {
			dao.close();
		}
	}
		
	@Override
	public String getDirBase() {
		return "/monitoria/CalendarioMonitoria";
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
	}

	public List<SelectItem> getCalendariosAtivos() {
		return calendariosAtivos;
	}

	public void setCalendariosAtivos(List<SelectItem> calendariosAtivos) {
		this.calendariosAtivos = calendariosAtivos;
	}

	public CalendarioMonitoria getCalendario() {
		return calendario;
	}
	
	public void setCalendario(CalendarioMonitoria calendario) {
		this.calendario = calendario;
	}
}