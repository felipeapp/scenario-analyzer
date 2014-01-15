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

import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.MiniCurso;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * Gerado pelo CrudBuilder
 */
public class MiniCursoMBean
		extends
		AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.MiniCurso> {

	private Boolean habilitaDepartamento = true;

	public MiniCursoMBean() {
		obj = new MiniCurso();
		obj.setServidor(new Servidor());
		obj.setDepartamento(new Unidade());
		setOrder(true);
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(MiniCurso.class, "id", "descricao");
	}


	@Override
	public void beforeCadastrarAndValidate() {
		if(!habilitaDepartamento)
			obj.setDepartamento(new Unidade());
		obj.setServidor(getServidorUsuario());
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		super.checkDocenteRole();
	}
	@Override
	protected void afterCadastrar() {
		obj = new MiniCurso();
		obj.setServidor(new Servidor());
		obj.setDepartamento(new Unidade());
	}

	public Collection<SelectItem> getTipoCH(){
		ArrayList<SelectItem> tipos= new ArrayList<SelectItem>();
		SelectItem sl1 = new SelectItem("Horas","Horas");
		SelectItem sl2 = new SelectItem("Semanas","Semanas");
		SelectItem sl3 = new SelectItem("Dias","Dias");
		tipos.add(sl1);
		tipos.add(sl3);
		tipos.add(sl2);

		return tipos;
	}

	public Boolean getHabilitaDepartamento() {
		return habilitaDepartamento;
	}

	public void setHabilitaDepartamento(Boolean habilitaDepartamento) {
		this.habilitaDepartamento = habilitaDepartamento;
	}

	@Override
	public String getUrlRedirecRemover()
	{
		return "/sigaa/prodocente/atividades/MiniCurso/lista.jsf";
	}

}
