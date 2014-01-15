package br.ufrn.sigaa.arq.vinculo.tipos;

import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculo;
import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculoCoordenacaoGeralRede;
import br.ufrn.sigaa.ensino_rede.dominio.CoordenacaoGeralRede;

@SuppressWarnings("serial")
public class TipoVinculoCoordenacaoGeralRede extends TipoVinculoAbstract {

	private CoordenacaoGeralRede coordenacao;
	
	public TipoVinculoCoordenacaoGeralRede(CoordenacaoGeralRede coordenacao) {
		this.coordenacao = coordenacao;
	}

	@Override
	public String getTipo() {
		return "Coord. Geral";
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
		return coordenacao.getProgramaRede().getDescricao();
	}

	@Override
	public EstrategiaPopularVinculo getEstrategia() {
		return new EstrategiaPopularVinculoCoordenacaoGeralRede();
	}

	@Override
	public int getOrdem() {
		return 5;
	}
	
	public CoordenacaoGeralRede getCoordenacao() {
		return coordenacao;
	}
	
	@Override
	public boolean isCoordenadorGeralRede() {
		return true;
	}
	
}
