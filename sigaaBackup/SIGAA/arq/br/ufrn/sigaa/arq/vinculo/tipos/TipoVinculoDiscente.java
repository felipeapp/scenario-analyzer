/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.tipos;

import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculo;
import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculoDiscente;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;

/**
 * Representa o tipo de vínculo de discente
 * 
 * @author Henrique André
 *
 */
@SuppressWarnings("serial")
public class TipoVinculoDiscente extends TipoVinculoAbstract {

	public TipoVinculoDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}
	
	@Override
	public int getOrdem() {
		return 1;
	}
	
	@Override
	public EstrategiaPopularVinculo getEstrategia() {
		return new EstrategiaPopularVinculoDiscente();
	}
	
	/**
	 * Referência para Discente
	 */
	private DiscenteAdapter discente;

	@Override
	public String getTipo() {
		return "Discente";
	}

	@Override
	public boolean isAtivo() {
		return discente.isAtivo();
	}

	@Override
	public Object getIdentificador() {
		return discente.getMatricula();
	}

	@Override
	public String getOutrasInformacoes() {
		
		StringBuilder sb = new StringBuilder();
		
		if (discente.isRegular())
			sb.append("Curso: " + discente.getCurso().getDescricao());
		else
			sb.append("ALUNO ESPECIAL (" + discente.getNivelDesc() + ")");
		
		return sb.toString();
	}

	@Override
	public boolean isDiscente() {
		return true;
	}
	
	public DiscenteAdapter getDiscente() {
		return discente;
	}

}
