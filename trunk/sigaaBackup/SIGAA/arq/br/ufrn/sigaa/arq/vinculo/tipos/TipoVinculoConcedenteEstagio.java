/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.tipos;

import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculo;
import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculoConcedenteEstagio;
import br.ufrn.sigaa.estagio.dominio.ConcedenteEstagioPessoa;

/**
 * Representa o tipo de v�nculo para concedente de est�gio
 * 
 * @author Henrique Andr�
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
	 * Refer�ncia para o Concedente
	 */
	private ConcedenteEstagioPessoa concedente;
	
	@Override
	public String getTipo() {
		return "Concedente de Est�gio";
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
