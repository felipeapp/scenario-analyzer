/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/10/2006
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import java.util.Set;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;
import br.ufrn.sigaa.ensino.tecnico.dominio.ModuloDisciplina;

/**
 * Movimento para o cadastro de Módulos do Nível Técnico.
 * @author Andre M Dantas
 *
 */
public class ModuloMov extends MovimentoCadastro {

	private Set<ModuloDisciplina> modulosDisciplinasRemovidos;
	
	/**
	 * @param modulo
	 */
	public ModuloMov(Modulo modulo, Comando comando) {
		super();
		setCodMovimento(comando);
		setObjMovimentado(modulo);
	}

	public Set<ModuloDisciplina> getModulosDisciplinasRemovidos() {
		return modulosDisciplinasRemovidos;
	}

	public void setModulosDisciplinasRemovidos(Set<ModuloDisciplina> modulosDisciplinasRemovidos) {
		this.modulosDisciplinasRemovidos = modulosDisciplinasRemovidos;
	}

}
