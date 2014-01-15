/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.tipos;

import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculo;
import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculoCoordenadorPolo;
import br.ufrn.sigaa.ead.dominio.CoordenacaoPolo;

/**
 * Representa o tipo de vínculo quando o usuaário é coordenador de polo
 * 
 * @author Henrique André
 *
 */
@SuppressWarnings("serial")
public class TipoVinculoCoordenadorPolo extends TipoVinculoAbstract {

	public TipoVinculoCoordenadorPolo(CoordenacaoPolo coordenacao) {
		this.coordenacao = coordenacao;
	}
	
	@Override
	public EstrategiaPopularVinculo getEstrategia() {
		return new EstrategiaPopularVinculoCoordenadorPolo();
	}	
	
	@Override
	public int getOrdem() {
		return 5;
	}
	
	/**
	 * Referência a coordenação de polo
	 */
	private CoordenacaoPolo coordenacao;
	
	@Override
	public String getTipo() {
		return "Coordenador de Pólo";
	}

	@Override
	public boolean isAtivo() {
		return coordenacao.isAtivo();
	}

	@Override
	public Object getIdentificador() {
		return coordenacao.getPessoa().getCpfCnpjFormatado();
	}

	@Override
	public String getOutrasInformacoes() {
		return "Pólo: " + coordenacao.getPolo().getDescricao();
	}
	
	@Override
	public boolean isCoordenacaoPolo() {
		return true;
	}
	
	public CoordenacaoPolo getCoordenacao() {
		return coordenacao;
	}

}
