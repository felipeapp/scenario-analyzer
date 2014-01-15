/*
 * Universidade Federal do Rio Grande no Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 19/01/2009
 *
 */
package br.ufrn.sigaa.biblioteca.aquisicao.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;

/**
 *
 *   Passa os dados para o processador
 *
 * @author jadson
 * @since 07/10/2009
 * @version 1.0 criação da classe
 *
 */
public class MovimentoCadastraAlteraAssinaturaDePeriodico extends AbstractMovimentoAdapter{

	/** A assinatura que vai ser criada/editada */
	private Assinatura assinatura;
	
	/** Se está no caso de uso de editar ou criar assinatura*/
	private boolean editando;
	
	/** Se é para o sistema gerar o código de assinatura, ou usar o que o usuário digitou*/
	private boolean gerarCodigoAssinatuaCompra;
	
	
	public MovimentoCadastraAlteraAssinaturaDePeriodico(Assinatura assinatura, boolean editando
			, boolean gerarCodigoAssinatuaCompra) {
		this.assinatura = assinatura;
		this.editando = editando;
		this.gerarCodigoAssinatuaCompra = gerarCodigoAssinatuaCompra;
	}

	public Assinatura getAssinatura() {
		return assinatura;
	}

	public boolean isEditando() {
		return editando;
	}

	public boolean isGerarCodigoAssinatuaCompra() {
		return gerarCodigoAssinatuaCompra;
	}
	
}
