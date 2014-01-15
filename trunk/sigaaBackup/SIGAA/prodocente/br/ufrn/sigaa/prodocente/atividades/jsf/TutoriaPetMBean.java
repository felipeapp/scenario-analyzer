/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '05/11/2008'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.ProgramaEducacaoTutorial;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.ClassificacaoPet;
import br.ufrn.sigaa.prodocente.atividades.dominio.TutoriaPet;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * Gerado pelo CrudBuilder
 */
@Component("tutoriaPet") @Scope("request")
public class TutoriaPetMBean extends AbstractControllerAtividades<TutoriaPet> {
	public TutoriaPetMBean() {
		obj = new TutoriaPet();
		obj.setServidor(new Servidor());
		obj.setClassificacaoPet(new ClassificacaoPet(ClassificacaoPet.TUTOR));
		obj.setPet(new ProgramaEducacaoTutorial());
		setIdServidor(-1);
		setIdUnidade(-1);
	}

	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(TutoriaPet.class, "id", "titulo");
	}
	@Override
	protected void afterCadastrar() {
		obj = new TutoriaPet();
	}
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.CDP);
	}
	
	@Override
	public String getDirBase() {
		return "/prodocente/atividades/TutoriaPet";
	}
}

