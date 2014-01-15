/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 15/08/2007
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.CalendarioMonitoria;

/**
 * MBean responsável por cadastro do calendário de monitoria
 * @author Ilueny
 *
 */
@Component("calendarioMonitoria")
@Scope("request")
public class CalendarioMonitoriaMBean extends
		SigaaAbstractController<CalendarioMonitoria> {


	
	public CalendarioMonitoriaMBean() {
		obj = new CalendarioMonitoria();
		obj.setId(CalendarUtils.getAnoAtual());
	}

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
		
			prepareMovimento(SigaaListaComando.CADASTRAR_CALENDARIO_MONITORIA);
			
			GenericDAO dao = getGenericDAO();
			Collection<CalendarioMonitoria> ativos = dao.findAllAtivos(CalendarioMonitoria.class, "id" ); 
			
			if( (ativos != null) && (! ativos.isEmpty()))
				this.obj = ativos.iterator().next();
		
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
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_CALENDARIO_MONITORIA);

			execute(mov, getCurrentRequest());
			
		} catch(Exception e) {
			addMensagemErro(e.getMessage());
			return null;
		}

		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);		
		return forward("/monitoria/index.jsf");
	
	}

	@Override
	public String getDirBase() {
		return "/monitoria/CalendarioMonitoria";
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
	}

}