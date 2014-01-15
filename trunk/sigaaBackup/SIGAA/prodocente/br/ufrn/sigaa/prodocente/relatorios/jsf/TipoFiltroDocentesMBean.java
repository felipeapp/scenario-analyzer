/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Fev 28, 2011
 */
package br.ufrn.sigaa.prodocente.relatorios.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.prodocente.relatorios.dominio.TipoFiltroDocentes;

/**
 * 
 * @author Victor Hugo
 */
@Component("tipoFiltroDocentesMBean") @Scope("request")
public class TipoFiltroDocentesMBean extends SigaaAbstractController<TipoFiltroDocentes> {

	/** Construtor padrão. */
	public TipoFiltroDocentesMBean() {
		obj = new TipoFiltroDocentes();
	}
	
	/** Retornar combo com todos os tipos de Curso Lato */
	public Collection<SelectItem> getAllAtivoCombo() throws ArqException {
		return getAllAtivo(TipoFiltroDocentes.class, "id", "descricao");
	}
	
}
