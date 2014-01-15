/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
* 
* Created on 13/11/2006
*
*/
package br.ufrn.sigaa.monitoria.jsf;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;

/**
 * Managed bean para associar monitores com docentes.
 *
 * @author David Ricardo
 * @author ilueny santos
 *
 */
@Component("associarMonitorDocente")
@Scope("session")
public class AssociarMonitorDocenteMBean extends SigaaAbstractController<Orientacao> {


	public AssociarMonitorDocenteMBean(){
		obj = new Orientacao();
	}


	/**
	 * Escolhe orientação para monitor.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>JSP: sigaa.war\monitoria\AssociarMonitorDocente\lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String escolherOrientacao() throws SegurancaException {
	    	checkRole( SigaaPapeis.GESTOR_MONITORIA );	
		ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);
		try {
		    prepareMovimento(SigaaListaComando.ASSOCIAR_MONITOR_DOCENTE);
		    int id = getParameterInt("id");
		    obj = dao.findByPrimaryKey(id, Orientacao.class);
		} catch (ArqException e) {
		    notifyError(e);
		}			
		return forward("/monitoria/AssociarMonitorDocente/form.jsp");
	}


	/**
	 * Associa monitor ao projeto de ensino.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\AssociarMonitorDocente\form.jsp</li>
	 *  <li>sigaa.war\monitoria\AssociarMonitorDocente\lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String associarMonitor() throws SegurancaException {
	    checkRole( SigaaPapeis.GESTOR_MONITORIA );

		try {

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(SigaaListaComando.ASSOCIAR_MONITOR_DOCENTE);
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);

		} catch (Exception e) {
		    notifyError(e);
		}			

		obj = new Orientacao();
		resetBean();
		return getSubSistema().getForward();
	}

}
