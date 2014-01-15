/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.tipos;

import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculo;
import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculoCoordenadorPolo;
import br.ufrn.sigaa.ead.dominio.CoordenacaoPolo;

/**
 * Representa o tipo de v�nculo quando o usua�rio � coordenador de polo
 * 
 * @author Henrique Andr�
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
	 * Refer�ncia a coordena��o de polo
	 */
	private CoordenacaoPolo coordenacao;
	
	@Override
	public String getTipo() {
		return "Coordenador de P�lo";
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
		return "P�lo: " + coordenacao.getPolo().getDescricao();
	}
	
	@Override
	public boolean isCoordenacaoPolo() {
		return true;
	}
	
	public CoordenacaoPolo getCoordenacao() {
		return coordenacao;
	}

}
