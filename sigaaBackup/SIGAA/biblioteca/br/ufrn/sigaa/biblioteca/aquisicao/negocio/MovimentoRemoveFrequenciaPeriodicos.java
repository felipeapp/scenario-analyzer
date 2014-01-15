/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 04/01/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.aquisicao.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FrequenciaPeriodicos;

/**
 * <p>Passa os dados para o processador</p>
 * 
 * @author jadson
 *
 */
public class MovimentoRemoveFrequenciaPeriodicos extends AbstractMovimentoAdapter{

	/** A  FrequenciaPeriodicos que vai ser removida (desativada) */
	private FrequenciaPeriodicos frequenciaASerRemovida; 
	
	/** A nova  FrequenciaPeriodicos que as assinaturas que possuem a coleção antiga vão sem migrados */
	private FrequenciaPeriodicos frequenciaParaMigraAssinaturas;

	
	public MovimentoRemoveFrequenciaPeriodicos(FrequenciaPeriodicos frequenciaASerRemovida, FrequenciaPeriodicos frequenciaParaMigraAssinaturas) {
		super();
		this.frequenciaASerRemovida = frequenciaASerRemovida;
		this.frequenciaParaMigraAssinaturas = frequenciaParaMigraAssinaturas;
	}

	public FrequenciaPeriodicos getFrequenciaASerRemovida() {
		return frequenciaASerRemovida;
	}

	public FrequenciaPeriodicos getFrequenciaParaMigraAssinaturas() {
		return frequenciaParaMigraAssinaturas;
	}
	
}
