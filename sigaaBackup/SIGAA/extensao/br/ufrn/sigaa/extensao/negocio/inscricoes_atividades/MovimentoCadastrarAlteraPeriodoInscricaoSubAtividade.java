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
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividade;

/**
 * <p>Passa dos dados para o processador</p>
 * 
 * @author jadson
 */
public class MovimentoCadastrarAlteraPeriodoInscricaoSubAtividade extends AbstractMovimentoAdapter{

	/** A inscri��o a ser aberta alterada */
	private InscricaoAtividade inscricao; 

	/** A atividade de extens�o da sub atividade, usada para validar os dados */
	private AtividadeExtensao atividade; 
	
	/** Se o usu�rio atualmente logado � coordenador de extens�o */
	private boolean isCoordenadorExtensao = false;
	
	/** Se se est� abrindo ou alterando um per�odo de inscri��o */
	private boolean isAlterado = false;

	
	public MovimentoCadastrarAlteraPeriodoInscricaoSubAtividade(InscricaoAtividade inscricao, AtividadeExtensao atividade,
			boolean isCoordenadorExtensao, boolean isAlterado) {
		this.inscricao = inscricao;
		this.atividade = atividade;
		this.isCoordenadorExtensao = isCoordenadorExtensao;
		this.isAlterado = isAlterado;
		setCodMovimento(SigaaListaComando.CADASTRA_ALTERA_PERIODO_INSCRICAO_SUB_ATIVIDADE);
	}

	public InscricaoAtividade getInscricao() {
		return inscricao;
	}

	public AtividadeExtensao getAtividade() {
		return atividade;
	}

	public boolean isCoordenadorExtensao() {
		return isCoordenadorExtensao;
	}

	public boolean isAlterado() {
		return isAlterado;
	}
	
	
	
}
