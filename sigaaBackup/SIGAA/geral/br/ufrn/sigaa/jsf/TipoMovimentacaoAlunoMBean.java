/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '30/05/2007'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.sigaa.arq.dao.ensino.TipoMovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;

public class TipoMovimentacaoAlunoMBean extends SigaaAbstractController<TipoMovimentacaoAluno> {
	public TipoMovimentacaoAlunoMBean() {
		obj = new TipoMovimentacaoAluno();
	}

	public Collection<SelectItem> getAllAtivosCombo() {
		TipoMovimentacaoAlunoDao dao = null;
		try {
			dao = getDAO(TipoMovimentacaoAlunoDao.class);
			return toSelectItems(dao.findAtivos(), "id", "descricao");
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<SelectItem>();
		} finally {
			dao.close();
		}
	}

	public Collection<SelectItem> getAllPermanentes() {
		TipoMovimentacaoAlunoDao dao = null;
		try {
			dao = getDAO(TipoMovimentacaoAlunoDao.class);
			return toSelectItems(dao.findAtivos(true), "id", "descricao");
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<SelectItem>();
		} finally {
			dao.close();
		}
	}

	public Collection<SelectItem> getAllProvisorios() {
		TipoMovimentacaoAlunoDao dao = null;
		try {
			dao = getDAO(TipoMovimentacaoAlunoDao.class);
			return toSelectItems(dao.findAtivos(false), "id", "descricao");
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<SelectItem>();
		} finally {
			dao.close();
		}
	}
}
