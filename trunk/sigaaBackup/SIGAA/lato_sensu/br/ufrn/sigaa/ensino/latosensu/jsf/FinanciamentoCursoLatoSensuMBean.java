package br.ufrn.sigaa.ensino.latosensu.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.ensino.latosensu.dominio.FinanciamentoCursoLato;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * Managed Bean de Financiamento dos Cursos Lato-Sensu, que realiza as seguintes operações cadastro, atualização, 
 * remoção de um Financiamento.
 *  
 * @author guerethes
 */
@Component @Scope("request")
public class FinanciamentoCursoLatoSensuMBean extends AbstractControllerAtividades<FinanciamentoCursoLato> {

	public FinanciamentoCursoLatoSensuMBean() {
		clear();
	}
	
	@Override
	public String getDirBase() {
		return "/lato/financiamento";
	}
	
	/**
	 * Instância o Objeto sentando em obj
	 */
	private void clear() {
		obj = new FinanciamentoCursoLato();
	}
	
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		Collection <FinanciamentoCursoLato> finan = 
			getGenericDAO().findAllAtivos(FinanciamentoCursoLato.class, "descricao"); 
		return toSelectItems(finan, "id", "descricao");
	}
	
}