/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/03/2010
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.util.Calendar;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.CalendarioExtensao;

/**
 * 
 * @author Daniel Augusto
 *
 */
@Scope("session")
@Component("calendarioExtensao")
public class CalendarioExtensaoMBean extends SigaaAbstractController<CalendarioExtensao> {
	

	public CalendarioExtensaoMBean(){
		obj = new CalendarioExtensao();
	}
	
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		
		erros.getMensagens().clear();
		removeOperacaoAtiva();
		obj = buscarCalendario(CalendarUtils.getAnoAtual());
		definirAcao(obj);
		
		if (obj != null) {
			checkChangeRole();
			prepareMovimento(ArqListaComando.ALTERAR); 
			setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
			return forward(getFormPage());
		} 
		obj = new CalendarioExtensao(CalendarUtils.getAnoAtual());
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		return super.preCadastrar();
	}
	
	public void alterarAno(ActionEvent event) throws DAOException{
		
		int ano = obj.getAnoReferencia();
		Calendar c = Calendar.getInstance();
		c.setTime(obj.getInicioCadastroBolsa());
		c.set(Calendar.YEAR, ano);
		obj.setInicioCadastroBolsa(c.getTime());
		
		c.setTime(obj.getFimCadastroBolsa());
		c.set(Calendar.YEAR, ano);
		obj.setFimCadastroBolsa(c.getTime());
		CalendarioExtensao cal = buscarCalendario(ano);
		definirAcao(cal);
		if (cal == null) {
			cal = new CalendarioExtensao(ano);
			cal.setFimCadastroBolsa(obj.getFimCadastroBolsa());
			cal.setInicioCadastroBolsa(obj.getInicioCadastroBolsa());
		}
		obj = cal;
	}
	
	private CalendarioExtensao buscarCalendario(int ano) throws DAOException{
		return getGenericDAO().findByExactField(CalendarioExtensao.class, "anoReferencia", ano, true);
	}
	
	private void definirAcao(CalendarioExtensao cal){
		setConfirmButton((cal == null) ? "Cadastrar" : "Alterar");
	}
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
	}
	
	@Override
	public String getDirBase() {
		return "/extensao/CalendarioExtensao";
	}
	
	@Override
	public String forwardCadastrar() {
		return getSubSistema().getLink();
	}
}
