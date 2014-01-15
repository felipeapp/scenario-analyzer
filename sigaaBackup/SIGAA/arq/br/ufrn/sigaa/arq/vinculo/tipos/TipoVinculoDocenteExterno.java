/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.tipos;

import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculo;
import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculoDocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;

/**
 * Representa o tipo de vínculo para Docente Externo
 * 
 * @author Henrique André
 *
 */
@SuppressWarnings("serial")
public class TipoVinculoDocenteExterno extends TipoVinculoAbstract {

	public TipoVinculoDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
		
	}

	@Override
	public EstrategiaPopularVinculo getEstrategia() {
		return new EstrategiaPopularVinculoDocenteExterno();
	}	
	
	@Override
	public int getOrdem() {
		return 6;
	}
	
	/**
	 * Referência ao Docente Externo
	 */
	private DocenteExterno docenteExterno;
	
	@Override
	public String getTipo() {
		return "Docente Externo";
	}

	@Override
	public boolean isAtivo() {
		return docenteExterno.isAtivo();
	}

	@Override
	public Object getIdentificador() {
		return docenteExterno.getMatricula();
	}

	@Override
	public String getOutrasInformacoes() {
		StringBuilder sb  = new StringBuilder();
		sb.append("Instituição: " + docenteExterno.getNomeInstituicaoEUnidade());
		
		return sb.toString();
	}

	@Override
	public boolean isDocenteExterno() {
		return true;
	}

	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}
	
}
