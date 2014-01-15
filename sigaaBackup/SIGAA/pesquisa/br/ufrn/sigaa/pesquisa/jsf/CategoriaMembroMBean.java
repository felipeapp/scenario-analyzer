/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/10/2007
 *
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;

/**
 * Gerado pelo CrudBuilder
 */
public class CategoriaMembroMBean
		extends
		SigaaAbstractController<CategoriaMembro> {
	
	
	public CategoriaMembroMBean() {
		obj = new CategoriaMembro();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(CategoriaMembro.class, "id", "descricao");
	}
	
	public int getDISCENTE(){
		return CategoriaMembro.DISCENTE;
	}
	
	public int getDOCENTE(){
		return CategoriaMembro.DOCENTE;
	}

	public int getEXTERNO(){
		return CategoriaMembro.EXTERNO;
	}
	
	
	public int getSERVIDOR(){
		return CategoriaMembro.SERVIDOR;
	}
	
}
