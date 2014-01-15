/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 22/09/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RelacionaClassificacaoBibliograficaAreaCNPq;

/**
 *
 * <p>Passa os dados para o processador que vai atualizar o rela��o das grandes �reas CNPq </p>
 * 
 * @author jadson
 *
 */
public class MovimentoAtualizaRelacaoClassificacaoAreasCNPq extends AbstractMovimentoAdapter{

	/** Guarda os relacionamentos entre as �reas CNPq e as classifica��es utilizadas na biblioteca */
	private List<RelacionaClassificacaoBibliograficaAreaCNPq> relacionamentos;
	
	public MovimentoAtualizaRelacaoClassificacaoAreasCNPq(List<RelacionaClassificacaoBibliograficaAreaCNPq> relacionamentos) {
		this.relacionamentos = relacionamentos;
	}

	public List<RelacionaClassificacaoBibliograficaAreaCNPq> getRelacionamentos() {
		return relacionamentos;
	}
	
}
