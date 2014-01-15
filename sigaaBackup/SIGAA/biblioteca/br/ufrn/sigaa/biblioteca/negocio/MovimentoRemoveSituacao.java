/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>Passa os dados para o processador que vai remover a situa��o</p>
 *
 * @author jadson
 *
 */
public class MovimentoRemoveSituacao extends AbstractMovimentoAdapter{

	/** A cole��o que vai ser removido (desativado) */
	private SituacaoMaterialInformacional situacao; 
	
	/** A nova cole��o que os materias que possuem a cole��o antiga v�o sem migrados */
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
