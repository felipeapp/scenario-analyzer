/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.prodocente.AtividadesProdocenteDao;
import br.ufrn.sigaa.prodocente.atividades.dominio.DisciplinaQualificacao;
import br.ufrn.sigaa.prodocente.atividades.dominio.QualificacaoDocente;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * MBean responsável por carregar as informações das páginas que mostram as informações 
 * sobre Disciplina.
 * 
 * @author Gleydson
 */
@Scope("request")
@Component("disciplinaQualificacao")
public class DisciplinaQualificacaoMBean extends
		AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.DisciplinaQualificacao> {
	
	
	private ArrayList<QualificacaoDocente> atividadesQ;
	
	public DisciplinaQualificacaoMBean() {
		obj = new DisciplinaQualificacao();
		obj.setQualificacaoDocente(new QualificacaoDocente());
	}

	/**
	 * Método não é invocado por jsp
	 */
	public Collection<SelectItem> getAllCombo() {
		return getAll(DisciplinaQualificacao.class, "id", "disciplina");
	}

	/**
	 * Método não é invocado por jsp
	 */
	@Override
	protected void afterCadastrar() {
		obj = new DisciplinaQualificacao();
	}

	/**
	 * Método não é invocado por jsp
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO);
	}

	/**
	 * A partir de um evento pode-se fazer uma busca pelo servidor ou pela unidade, ou pelos dois.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/prodocente/atividades/DisciplinaQualificacao/lista.jsp
	 * 
	 * @param e
	 * @throws DAOException
	 */
	@Override
	public void buscar(ActionEvent e) throws DAOException {
		
		atividadesQ = new ArrayList<QualificacaoDocente>();
		
		if (!isBuscaServidor()) 
			setIdServidor(-1);
		
		if (!isBuscaUnidade()) 
			setIdUnidade(-1);

		AtividadesProdocenteDao dao = (AtividadesProdocenteDao) getDAO(AtividadesProdocenteDao.class);
		atividadesQ = (ArrayList<QualificacaoDocente>) dao.findByServidorDep(QualificacaoDocente.class, 
				getIdServidor(), getIdUnidade());
	}

	public ArrayList<QualificacaoDocente> getAtividadesQ() {
		return atividadesQ;
	}

	public void setAtividadesQ(ArrayList<QualificacaoDocente> atividades) {
		this.atividadesQ = atividades;
	}
}
