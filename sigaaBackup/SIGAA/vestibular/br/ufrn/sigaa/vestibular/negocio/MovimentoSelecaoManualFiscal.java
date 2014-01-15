/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/11/2008
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.vestibular.dominio.InscricaoFiscal;
import br.ufrn.sigaa.vestibular.dominio.ObjectSeletor;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/**
 * Classe que encapsula dados para seleção manual de fiscais.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
public class MovimentoSelecaoManualFiscal extends AbstractMovimentoAdapter {
	
	/** Lista de inscrições de fiscais a processar. */
	private Collection<ObjectSeletor<InscricaoFiscal>> inscricoesFiscais;
	
	/** Processo Seletivo a processar.*/
	private ProcessoSeletivoVestibular processoSeletivo;

	/** Retorna a lista de inscrições de fiscais a processar. 
	 * @return
	 */
	public Collection<ObjectSeletor<InscricaoFiscal>> getInscricoesFiscais() {
		return inscricoesFiscais;
	}

	/** Seta a lista de inscrições de fiscais a processar. 
	 * @param collection
	 */
	public void setInscricoesFiscais(
			Collection<ObjectSeletor<InscricaoFiscal>> collection) {
		this.inscricoesFiscais = collection;
	}

	/** Retorna o processo Seletivo a processar.
	 * @return
	 */
	public ProcessoSeletivoVestibular getProcessoSeletivo() {
		return processoSeletivo;
	}

	/** Seta o processo Seletivo a processar.
	 * @param processoSeletivo
	 */
	public void setProcessoSeletivo(ProcessoSeletivoVestibular processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	/** Construtor padrão. */
	public MovimentoSelecaoManualFiscal() {
	}
}
