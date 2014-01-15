/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 17/10/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;

/**
 * <p>Passa os dados para o processador que vai alterar o motivo de baixa de vários materiais ao mesmo tempo.</p>
 *
 * @author felipe
 *
 */
public class MovimentoAlterarMotivoBaixaVariosMateriais extends AbstractMovimentoAdapter {

	/**
	 * Os materiais que vão ser alterados
	 */
	private List<MaterialInformacional> materiaisSelecionados;
	
	public MovimentoAlterarMotivoBaixaVariosMateriais(List<MaterialInformacional> materiaisSelecionados){
		this.materiaisSelecionados = materiaisSelecionados;
	}

	public List<MaterialInformacional> getMateriaisAlteracao() {
		return materiaisSelecionados;
	}

	public void setMateriaisAlteracao(List<MaterialInformacional> materiaisAlteracao) {
		this.materiaisSelecionados = materiaisAlteracao;
	}
	
}
