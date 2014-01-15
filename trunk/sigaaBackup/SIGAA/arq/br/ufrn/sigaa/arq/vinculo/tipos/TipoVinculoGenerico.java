/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.tipos;

import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculo;
import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculoGenerico;

/**
 * Vinculo Generico usado quando o usuário não se enquadra em nenhum outro
 * 
 * @author Henrique André
 *
 */
@SuppressWarnings("serial")
public class TipoVinculoGenerico extends TipoVinculoAbstract {

	@Override
	public String getTipo() {
		return "Outros";
	}

	@Override
	public EstrategiaPopularVinculo getEstrategia() {
		return new EstrategiaPopularVinculoGenerico();
	}

	@Override
	public int getOrdem() {
		return 9;
	}
	
	@Override
	public boolean isAtivo() {
		return true;
	}

	@Override
	public Object getIdentificador() {
		return "";
	}

	@Override
	public String getOutrasInformacoes() {
		return "Outros";
	}

}
