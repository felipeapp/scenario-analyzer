/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 26/10/2006
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;

/**
 * Managed Bean para solicitar a renovação de um
 * projeto de monitoria.
 *
 * @author David Ricardo
 *
 */
@Component("solicitarRenovacao")
@Scope("session")
public class SolicitarRenovacaoMBean extends SigaaAbstractController<ProjetoEnsino> {

	public SolicitarRenovacaoMBean() {
		this.obj = new ProjetoEnsino();
	}

	public List<ProjetoEnsino> getProjetos() {
		ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);

		try {
			prepareMovimento(SigaaListaComando.SOLICITAR_RENOVACAO_PROJETO);
			return dao.findProjetosRenovacao(getUsuarioLogado(), CalendarUtils.getAnoAtual());
		} catch(Exception e) {
			e.printStackTrace();
			return new ArrayList<ProjetoEnsino>();
		}
	}
	/**
	 * Confirma solicitação de renovação
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\SolicitarRenovacao\lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public String confirmar() {
		ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);

		try {
			setId();
			obj = dao.findByPrimaryKey(obj.getId(), ProjetoEnsino.class);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(SigaaListaComando.SOLICITAR_RENOVACAO_PROJETO);
			execute(mov, getCurrentRequest());
			addMensagemInformation("Solicitação efetuada com sucesso, aguardando aprovação da renovação.");
		} catch(NegocioException ne) {
			addMensagemErro(ne.getMessage());
			return null;
		} catch(Exception e) {
			e.printStackTrace();
			addMensagemErro(e.getMessage());
		}

		return getSubSistema().getForward();

	}

}
