/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.tipos;

import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculo;
import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculoResponsavel;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Representa o tipo de vínculo quando o usuário possui responsabilidade em uma unidade
 * 
 * @author Henrique André
 *
 */
@SuppressWarnings("serial")
public class TipoVinculoResponsavel extends TipoVinculoServidor {

	public TipoVinculoResponsavel(Responsavel responsavel, Servidor servidor) {
		super(servidor);
		this.responsavel = responsavel;
	}
	
	@Override
	public EstrategiaPopularVinculo getEstrategia() {
		return new EstrategiaPopularVinculoResponsavel();
	}
	
	@Override
	public int getOrdem() {
		return 4;
	}
	
	/**
	 * Referencia a responsabilidade do usuário
	 */
	private Responsavel responsavel;
	
	@Override
	public String getTipo() {
		return responsavel.getDescricaoNivelResponsabilidade();
	}

	@Override
	public boolean isAtivo() {
		return getServidor().getAtivo().getId() == Ativo.SERVIDOR_ATIVO;
	}

	@Override
	public Object getIdentificador() {
		return getServidor().getSiape();
	}

	@Override
	public String getOutrasInformacoes() {
		return "Unidade: " + responsavel.getUnidade().getNome();
	}

	
	@Override
	public boolean isResponsavel() {
		return true;
	}
	
	public Responsavel getResponsavel() {
		return responsavel;
	}

	
}
