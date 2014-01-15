/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 02/10/2006
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import java.util.HashSet;
import java.util.Set;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.ensino.tecnico.dominio.DisciplinaComplementar;
import br.ufrn.sigaa.ensino.tecnico.dominio.EstruturaCurricularTecnica;
import br.ufrn.sigaa.ensino.tecnico.dominio.ModuloCurricular;

/**
 * Movimento para a Estrutura Curricular do Ensino Técnico.
 * @author Andre M Dantas
 *
 */
public class EstruturaCurricularTecnicaMov extends MovimentoCadastro {

	private Set<DisciplinaComplementar> discComplemRemovidas = new HashSet<DisciplinaComplementar>(0);

	private Set<ModuloCurricular> modCurrRemovidos = new HashSet<ModuloCurricular>(0);

	
	public EstruturaCurricularTecnicaMov() {
	}
	
	public EstruturaCurricularTecnicaMov(EstruturaCurricularTecnica ect,  Comando comando) {
		setObjMovimentado(ect);
		setCodMovimento(comando);
	}
	
	public Set<DisciplinaComplementar> getDiscComplemRemovidas() {
		return discComplemRemovidas;
	}

	public void setDiscComplemRemovidas(Set<DisciplinaComplementar> discComplemRemovidas) {
		this.discComplemRemovidas = discComplemRemovidas;
	}

	public Set<ModuloCurricular> getModCurrRemovidos() {
		return modCurrRemovidos;
	}

	public void setModCurrRemovidos(Set<ModuloCurricular> modCurrRemovidos) {
		this.modCurrRemovidos = modCurrRemovidos;
	}
	
}
