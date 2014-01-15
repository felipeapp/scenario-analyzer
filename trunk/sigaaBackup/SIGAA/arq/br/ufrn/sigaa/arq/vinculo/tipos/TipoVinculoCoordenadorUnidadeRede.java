package br.ufrn.sigaa.arq.vinculo.tipos;

import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculo;
import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculoCoordenadorUnidadeRede;
import br.ufrn.sigaa.ensino_rede.dominio.CoordenadorUnidade;

@SuppressWarnings("serial")
public class TipoVinculoCoordenadorUnidadeRede extends TipoVinculoAbstract {

	private CoordenadorUnidade coordenacao;
	
	public TipoVinculoCoordenadorUnidadeRede(CoordenadorUnidade coordenacao) {
		this.coordenacao = coordenacao;
	}
	
	@Override
	public String getTipo() {
		return "Coord. da Unidade";
	}

	@Override
	public boolean isAtivo() {
		return true;
	}

	@Override
	public Object getIdentificador() {
		return coordenacao.getPessoa().getCpfCnpjFormatado();
	}

	@Override
	public String getOutrasInformacoes() {
		return coordenacao.getDescricaoDetalhado();
	}

	@Override
	public EstrategiaPopularVinculo getEstrategia() {
		return new EstrategiaPopularVinculoCoordenadorUnidadeRede();
	}

	@Override
	public int getOrdem() {
		return 5;
	}

	public CoordenadorUnidade getCoordenacao() {
		return coordenacao;
	}

	public void setCoordenacao(CoordenadorUnidade coordenacao) {
		this.coordenacao = coordenacao;
	}

	@Override
	public boolean isCoordenadorUnidadeRede() {
		return true;
	}
}
