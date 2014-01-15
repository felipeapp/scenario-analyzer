/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.tipos;

import br.ufrn.rh.dominio.Ativo;
import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculo;
import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculoServidor;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Representa o vínculo de Servidor
 * 
 * @author Henrique André
 *
 */
@SuppressWarnings("serial")
public class TipoVinculoServidor extends TipoVinculoAbstract {

	public TipoVinculoServidor(Servidor servidor) {
		this.servidor = servidor;
	}
	
	@Override
	public EstrategiaPopularVinculo getEstrategia() {
		return new EstrategiaPopularVinculoServidor();
	}	
	
	@Override
	public int getOrdem() {
		return 2;
	}
	
	/**
	 * Referência para servidor
	 */
	private Servidor servidor;
	
	@Override
	public String getTipo() {
		return "Servidor";
	}

	@Override
	public boolean isAtivo() {
		if (servidor.getAtivo().getId() == Ativo.SERVIDOR_ATIVO) {
			return true;
		} else {
			return servidor.isColaboradorVoluntario();
		}
	}

	@Override
	public Object getIdentificador() {
		return servidor.getSiape();
	}

	@Override
	public String getOutrasInformacoes() {
		return "Lotação: " + servidor.getUnidade().getNome();
	}

	@Override
	public boolean isServidor() {
		return true;
	}

	public Servidor getServidor() {
		return servidor;
	}
}
