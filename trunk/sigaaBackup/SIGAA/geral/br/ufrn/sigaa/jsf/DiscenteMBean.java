/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 17/05/2007
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Managed Bean responsável por trazer os status dos discentes e popular os SelectItens.
 * @author Victor Hugo
 *
 */
public class DiscenteMBean extends SigaaAbstractController<Discente> {

	public DiscenteMBean() {
		obj = new Discente();
	}

	/** Lista dos componentes da combo de tipo de discente. */
	private static List<SelectItem> tipoCombo = new ArrayList<SelectItem>();
	/** Lista dos componentes da combo de status de discente. */
	private static List<SelectItem> statusCombo = new ArrayList<SelectItem>();
	/** Lista dos componentes da combo de status ativos de discente. */
	private static List<SelectItem> statusAtivosCombo = new ArrayList<SelectItem>();

	static {
		tipoCombo.add(new SelectItem("1", "REGULAR"));
		tipoCombo.add(new SelectItem("2", "ESPECIAL"));
		//tipoCombo.add(new SelectItem("3", "EM ASSOCIAÇÃO"));
	}

	static {
		statusCombo.add(new SelectItem("1", "ATIVO"));
		statusCombo.add(new SelectItem("2", "CADASTRADO"));
		statusCombo.add(new SelectItem("3", "CONCLUIDO"));
		statusCombo.add(new SelectItem("4", "AFASTADO"));
		statusCombo.add(new SelectItem("5", "TRANCADO"));
		statusCombo.add(new SelectItem("6", "CANCELADO"));
		statusCombo.add(new SelectItem("7", "JUBILADO"));
		statusCombo.add(new SelectItem("8", "FORMANDO"));
		statusCombo.add(new SelectItem("9", "GRADUANDO"));
	}
	
	static {
		statusAtivosCombo.add(new SelectItem(StatusDiscente.ATIVO, StatusDiscente.getDescricao(StatusDiscente.ATIVO)));
		statusAtivosCombo.add(new SelectItem(StatusDiscente.FORMANDO, StatusDiscente.getDescricao(StatusDiscente.FORMANDO)));
		statusAtivosCombo.add(new SelectItem(StatusDiscente.GRADUANDO, StatusDiscente.getDescricao(StatusDiscente.GRADUANDO)));
	}	

	public List<SelectItem> getStatusCombo() {
		return statusCombo;
	}

	public void setStatusCombo(List<SelectItem> statusCombo) {
		DiscenteMBean.statusCombo = statusCombo;
	}

	public List<SelectItem> getTipoCombo() {
		return tipoCombo;
	}

	public void setTipoCombo(List<SelectItem> tipoCombo) {
		DiscenteMBean.tipoCombo = tipoCombo;
	}

	public List<SelectItem> getStatusAtivosCombo() {
		return statusAtivosCombo;
	}

	public void setStatusAtivosCombo(List<SelectItem> statusAtivosCombo) {
		DiscenteMBean.statusAtivosCombo = statusAtivosCombo;
	}


}
