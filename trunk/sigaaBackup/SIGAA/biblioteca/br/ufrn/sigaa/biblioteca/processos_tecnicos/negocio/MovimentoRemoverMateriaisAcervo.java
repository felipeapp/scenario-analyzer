/*
 * MovimentoRemoverMateriaisAcervo.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 18/02/2010
 * Autor: jadson
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;

/**
 *   Passa os dados ao processador que remove os materiais do acervo das bibliotecas.
 *
 * @author jadson
 *
 */
public class MovimentoRemoverMateriaisAcervo  extends AbstractMovimentoAdapter{

	private List<MaterialInformacional> materiais; /** Os materiais que vão ser removidos */

	public MovimentoRemoverMateriaisAcervo( List<MaterialInformacional> materiais) {
		this.materiais = materiais;
	}

	public MovimentoRemoverMateriaisAcervo(MaterialInformacional material) {
		if(materiais == null)
			materiais = new ArrayList<MaterialInformacional>();
		
		materiais.add(material);
	}
	
	public List<MaterialInformacional> getMateriais() {
		return materiais;
	}	
	
}
