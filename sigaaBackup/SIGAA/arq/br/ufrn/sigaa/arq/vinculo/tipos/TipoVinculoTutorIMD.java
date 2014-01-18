/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.tipos;

import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculo;
import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculoTutorIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutoriaIMD;

/**
 * Representa o tipo de vínculo para tutor do IMD, que diferere do Tutor normal
 * da SEDIS
 * 
 * @author Gleydson Lima
 * 
 */
@SuppressWarnings("serial")
public class TipoVinculoTutorIMD extends TipoVinculoAbstract {

	public TipoVinculoTutorIMD(TutoriaIMD tutoria) {
		this.tutoria = tutoria;
	}

	@Override
	public EstrategiaPopularVinculo getEstrategia() {
		return new EstrategiaPopularVinculoTutorIMD();
	}

	@Override
	public int getOrdem() {
		return 7;
	}

	/**
	 * Referência para Tutor;
	 */
	private TutoriaIMD tutoria;

	@Override
	public String getTipo() {
		return "Tutor IMD";
	}

	@Override
	public boolean isAtivo() {
		return tutoria.isAtivo();
	}

	@Override
	public Object getIdentificador() {
		return tutoria.getTutor().getPessoa().getCpf_cnpj();
	}

	@Override
	public String getOutrasInformacoes() {
		return "Unidade: " + tutoria.getTurmaEntrada().getUnidade().getNome();
	}

	public TutoriaIMD getTutoria() {
		return tutoria;
	}

	public void setTutoria(TutoriaIMD tutoria) {
		this.tutoria = tutoria;
	}
	
	@Override
	public boolean isTutorIMD() {
		return true;
	}

}
