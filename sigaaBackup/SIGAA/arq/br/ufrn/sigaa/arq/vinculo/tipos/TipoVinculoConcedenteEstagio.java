/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.tipos;

import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculo;
import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculoConcedenteEstagio;
import br.ufrn.sigaa.estagio.dominio.ConcedenteEstagioPessoa;

/**
 * Representa o tipo de vínculo para concedente de estágio
 * 
 * @author Henrique André
 *
 */
@SuppressWarnings("serial")
public class TipoVinculoConcedenteEstagio extends TipoVinculoAbstract {

	public TipoVinculoConcedenteEstagio(ConcedenteEstagioPessoa concedente) {
		this.concedente = concedente;
	}
	
	@Override
	public EstrategiaPopularVinculo getEstrategia() {
		return new EstrategiaPopularVinculoConcedenteEstagio();
	}	
	
	@Override
	public int getOrdem() {
		return 8;
	}
	
	/**
	 * Referência para o Concedente
	 */
	private ConcedenteEstagioPessoa concedente;
	
	@Override
	public String getTipo() {
		return "Concedente de Estágio";
	}

	@Override
	public boolean isAtivo() {
		return concedente.getConcedente().isAtivo();
	}

	@Override
	public Object getIdentificador() {
		return concedente.getPessoa().getCpfCnpjFormatado();
	}

	@Override
	public String getOutrasInformacoes() {
		return "Concedente: " + concedente.getConcedente().getPessoa().getNome();
	}

	public ConcedenteEstagioPessoa getConcedente() {
		return concedente;
	}
	
	@Override
	public boolean isConcedenteEstagio() {
		return true;
	}
	
}
