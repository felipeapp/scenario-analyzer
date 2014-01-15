/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 09/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio.inscricoes_atividades;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividade;

/**
 * <p>Passa dos dados para o processador</p>
 * 
 * @author jadson
 *
 */
public class MovimentoCadastrarAlteraPeriodoInscricaoAtividade extends AbstractMovimentoAdapter{

	/** A inscrição a ser aberta alterada */
	private InscricaoAtividade inscricao; 
	
	/** Se o usuário atualmente logado é coordenador de extensão */
	private boolean isCoordenadorExtensao = false;
	
	/** Se se está abrindo ou alterando um período de inscrição */
	private boolean isAlterado = false;

	
	public MovimentoCadastrarAlteraPeriodoInscricaoAtividade(InscricaoAtividade inscricao, boolean isCoordenadorExtensao, boolean isAlterado) {
		this.inscricao = inscricao;
		this.isCoordenadorExtensao = isCoordenadorExtensao;
		this.isAlterado = isAlterado;
		setCodMovimento(SigaaListaComando.CADASTRA_ALTERA_PERIODO_INSCRICAO_ATIVIDADE);
	}

	public InscricaoAtividade getInscricao() {
		return inscricao;
	}

	public boolean isCoordenadorExtensao() {
		return isCoordenadorExtensao;
	}

	public boolean isAlterado() {
		return isAlterado;
	}
	
}
