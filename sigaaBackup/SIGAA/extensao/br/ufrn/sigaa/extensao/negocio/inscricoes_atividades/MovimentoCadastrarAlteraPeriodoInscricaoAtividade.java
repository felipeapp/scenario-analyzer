/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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

	/** A inscri��o a ser aberta alterada */
	private InscricaoAtividade inscricao; 
	
	/** Se o usu�rio atualmente logado � coordenador de extens�o */
	private boolean isCoordenadorExtensao = false;
	
	/** Se se est� abrindo ou alterando um per�odo de inscri��o */
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
