/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/05/2008
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import java.util.Collection;
import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.vestibular.dominio.Fiscal;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.dominio.QuantidadeFiscalPorMunicipio;
import br.ufrn.sigaa.vestibular.dominio.ResumoProcessamentoSelecao;

/** Classe que encapsula dados para o processamento da sele��o de fiscais.
 * @author �dipo Elder F. Melo
 *
 */
public class MovimentoSelecaoFiscal extends AbstractMovimentoAdapter {
	/** Quantidade de fiscais a selecionar por munic�pio. */
	private Collection<QuantidadeFiscalPorMunicipio> fiscaisPorMunicipio;
	
	/** Processo Seletivo a processar. */
	private ProcessoSeletivoVestibular processoSeletivoVestibular;

	/** Indica se o processamento deve ser simulado ou consolidado. Caso true, 
	 * a sele��o de fiscais � processada, mas n�o gravada no banco. Caso false,
	 * o processamento n�o ser� mais realizado, gravando em banco o processamento
	 * simulado. */
	private boolean simulaProcessamento;
	
	/** Mapa com um resumo quantitativo do processamento. O Mapa � no formato <municipio, <curso, <[quantidadeTitular, quantidadeReserva]>>. */
	private List<ResumoProcessamentoSelecao> resumoProcessamento;
	
	/** Cole��o de fiscais selecionados no processamento. */
	private Collection<Fiscal> fiscaisSelecionados;
	
	/** Construtor padr�o. */
	public MovimentoSelecaoFiscal() {
		simulaProcessamento = false;
	}

	/** Retorna a quantidade de fiscais a selecionar por munic�pio. 
	 * @return
	 */
	public Collection<QuantidadeFiscalPorMunicipio> getFiscaisPorMunicipio() {
		return fiscaisPorMunicipio;
	}

	/** Retorna o Processo Seletivo a processar. 
	 * @return
	 */
	public ProcessoSeletivoVestibular getProcessoSeletivoVestibular() {
		return processoSeletivoVestibular;
	}

	/** Seta a quantidade de fiscais a selecionar por munic�pio. 
	 * @param fiscaisPorMunicipio
	 */
	public void setFiscaisPorMunicipio(
			Collection<QuantidadeFiscalPorMunicipio> fiscaisPorMunicipio) {
		this.fiscaisPorMunicipio = fiscaisPorMunicipio;
	}

	/** Seta o Processo Seletivo a processar.
	 * @param processoSeletivoVestibular
	 */
	public void setProcessoSeletivoVestibular(
			ProcessoSeletivoVestibular processoSeletivoVestibular) {
		this.processoSeletivoVestibular = processoSeletivoVestibular;
	}

	/**
	 * Indica se o processamento deve ser simulado ou consolidado.
	 * 
	 * @return Caso true, a sele��o de fiscais � processada, mas n�o gravada no
	 *         banco. Caso false, o processamento n�o ser� mais realizado,
	 *         gravando em banco o processamento simulado.
	 */
	public boolean isSimulaProcessamento() {
		return simulaProcessamento;
	}

	/** Seta se o processamento deve ser simulado ou consolidado.
	 * @param simulaProcessamento
	 */
	public void setSimulaProcessamento(boolean simulaProcessamento) {
		this.simulaProcessamento = simulaProcessamento;
	}

	/** Retorna a cole��o de fiscais selecionados no processamento. 
	 * @return
	 */
	public Collection<Fiscal> getFiscaisSelecionados() {
		return fiscaisSelecionados;
	}

	/** Seta a cole��o de fiscais selecionados no processamento. 
	 * @param fiscaisSelecionados
	 */
	public void setFiscaisSelecionados(Collection<Fiscal> fiscaisSelecionados) {
		this.fiscaisSelecionados = fiscaisSelecionados;
	}

	/**
	 * Retorna o mapa com um resumo quantitativo do processamento.
	 * 
	 * @return the resumoProcessamento
	 */
	public List<ResumoProcessamentoSelecao> getResumoProcessamento() {
		return resumoProcessamento;
	}

    /** Seta o mapa com um resumo quantitativo do processamento. 
	 * @param resumoProcessamento the resumoProcessamento to set
	 */
	public void setResumoProcessamento(List<ResumoProcessamentoSelecao> resumoProcessamento) {
		this.resumoProcessamento = resumoProcessamento;
	}
	
}
