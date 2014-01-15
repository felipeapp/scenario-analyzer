/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.tipos;

import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculo;
import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculoTutor;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;

/**
 * Representa o tipo de vínculo para tutor
 * 
 * @author Henrique André
 *
 */
@SuppressWarnings("serial")
public class TipoVinculoTutor extends TipoVinculoAbstract {

	public TipoVinculoTutor(TutorOrientador tutor) {
		this.tutor = tutor;
	}

	@Override
	public EstrategiaPopularVinculo getEstrategia() {
		return new EstrategiaPopularVinculoTutor();
	}	
	
	@Override
	public int getOrdem() {
		return 7;
	}
	
	/**
	 * Referência para Tutor;
	 */
	private TutorOrientador tutor;
	
	@Override
	public String getTipo() {
		return "Tutor Orientador";
	}

	@Override
	public boolean isAtivo() {
		return tutor.isAtivo();
	}

	@Override
	public Object getIdentificador() {
		return tutor.getPessoa().getCpfCnpjFormatado();
	}

	@Override
	public String getOutrasInformacoes() {
		String info;
		if (tutor.isPresencial()){
			if ( tutor.getPoloCurso()!=null)
				info = "Pólo: " + tutor.getPoloCurso().getPolo().getDescricao();
			else
				info = "Sem pólo associado";
		}else
			info = "Tutor à Distância";
		return info;
	}

	@Override
	public boolean isTutor() {
		return true;
	}
	
	public TutorOrientador getTutor() {
		return tutor;
	}

	public void setTutor(TutorOrientador tutor) {
		this.tutor = tutor;
	}

}
