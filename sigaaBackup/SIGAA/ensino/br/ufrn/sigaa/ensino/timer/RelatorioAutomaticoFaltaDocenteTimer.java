/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ensino.timer;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.Processador;
import br.ufrn.arq.negocio.ProcessadorHome;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Timer que enviará mensalmente um relatório com as faltas para
 * o chefe de departamento e chefe de centro
 * 
 * @author Henrique André
 *
 */
public class RelatorioAutomaticoFaltaDocenteTimer extends TarefaTimer {

	
	
	@Override
	public void run() {
		try {
			enviarRelatorios();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Invoca o processador e envia os relatórios mensal
	 * 
	 * @param tipoRe 
	 * 
	 * @throws NamingException 
	 * @throws CreateException 
	 * @throws RemoteException 
	 */
	private void enviarRelatorios() throws NamingException, RemoteException, CreateException {
		MovimentoCadastro mov = new MovimentoCadastro();
		
		if (isSemanal())
			mov.setCodMovimento(SigaaListaComando.ENVIO_AUTOMATICO_RELATORIO_FALTA_DOCENTE_SEMANAL);
		else if (isMensal())
			mov.setCodMovimento(SigaaListaComando.ENVIO_AUTOMATICO_RELATORIO_FALTA_DOCENTE_MENSAL);
		
		mov.setUsuarioLogado(new Usuario(Usuario.TIMER_SISTEMA));
		mov.setSistema(Sistema.SIGAA);

		InitialContext ic = new InitialContext();

		ProcessadorHome home = (ProcessadorHome) ic.lookup("ejb/SigaaFacade");
		Processador remote = home.create();
		try {
			remote.execute(mov);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isSemanal() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isMensal() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Invoca o processador e envia os relatórios semanais
	 * 
	 * @throws DAOException
	 * @throws NamingException
	 * @throws RemoteException
	 * @throws CreateException
	 */
	public void enviarRelatoriosSemanal() throws DAOException, NamingException, RemoteException, CreateException {
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.ENVIO_AUTOMATICO_RELATORIO_FALTA_DOCENTE_SEMANAL);
		mov.setUsuarioLogado(new Usuario(Usuario.TIMER_SISTEMA));
		mov.setSistema(Sistema.SIGAA);

		InitialContext ic = new InitialContext();

		ProcessadorHome home = (ProcessadorHome) ic.lookup("ejb/SigaaFacade");
		Processador remote = home.create();
		try {
			remote.execute(mov);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Invoca o processador e envia os relatórios semanais
	 * 
	 * @throws DAOException
	 * @throws NamingException
	 * @throws RemoteException
	 * @throws CreateException
	 */
	public void enviarRelatoriosMensal() throws DAOException, NamingException, RemoteException, CreateException {
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.ENVIO_AUTOMATICO_RELATORIO_FALTA_DOCENTE_MENSAL);
		mov.setUsuarioLogado(new Usuario(Usuario.TIMER_SISTEMA));
		mov.setSistema(Sistema.SIGAA);

		InitialContext ic = new InitialContext();

		ProcessadorHome home = (ProcessadorHome) ic.lookup("ejb/SigaaFacade");
		Processador remote = home.create();
		try {
			remote.execute(mov);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
		
	
}
