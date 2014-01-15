/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 17/10/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;

/**
 * <p>Passa os dados para o processador que vai alterar a situa��o de v�rios materiais ao mesmo tempo.</p>
 *
 * @author felipe
 *
 */
public class MovimentoTransferirMateriaisEntreSetores extends AbstractMovimentoAdapter {

	/**
	 * Os materiais que v�o ser alterados
	 */
	private List<MaterialInformacional> materiais;
	
	/**
	 * A nova situa��o dos materiais
	 */
	private SituacaoMaterialInformacional situacao;
	
	public MovimentoTransferirMateriaisEntreSetores(List<MaterialInformacional> materiais, SituacaoMaterialInformacional situacao) {
		this.materiais = materiais;
		this.situacao = situacao;
	}

	public List<MaterialInformacional> getMateriais() {
		return materiais;
	}

	public void setMateriais(List<MaterialInformacional> materiais) {
		this.materiais = materiais;
	}

	public SituacaoMaterialInformacional getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoMaterialInformacional situacao) {
		this.situacao = situacao;
	}
	
}
