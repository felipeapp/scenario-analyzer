/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 18/05/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;


/**
 *
 * <p>Passa os dados para o processador que vai remover a situação</p>
 *
 * @author jadson
 *
 */
public class MovimentoRemoveSituacao extends AbstractMovimentoAdapter{

	/** A coleção que vai ser removido (desativado) */
	private SituacaoMaterialInformacional situacao; 
	
	/** A nova coleção que os materias que possuem a coleção antiga vão sem migrados */
	private SituacaoMaterialInformacional novaSituacao;

	
	public MovimentoRemoveSituacao(SituacaoMaterialInformacional situacao, SituacaoMaterialInformacional novaSituacao) {
		this.situacao = situacao;
		this.novaSituacao = novaSituacao;
	}

	public SituacaoMaterialInformacional getSituacao() {
		return situacao;
	}

	public SituacaoMaterialInformacional getNovaSituacao() {
		return novaSituacao;
	} 
	
}
