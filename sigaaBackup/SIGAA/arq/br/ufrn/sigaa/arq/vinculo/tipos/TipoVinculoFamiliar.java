/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 26/09/2011
 */
package br.ufrn.sigaa.arq.vinculo.tipos;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculo;
import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculoFamiliar;
import br.ufrn.sigaa.ensino.medio.dominio.UsuarioFamiliar;

/**
 * Representa o tipo de v�nculo para familiares de discentes
 * 
 * @author Arlindo Rodrigues
 *
 */
public class TipoVinculoFamiliar extends TipoVinculoAbstract {
	
	/** Construtor padr�o */
	public TipoVinculoFamiliar(UsuarioFamiliar usuarioFamiliar) {
		this.usuarioFamiliar = usuarioFamiliar;
	}

	/** Usu�rio familiar */
	private UsuarioFamiliar usuarioFamiliar;	
	
	@Override
	public String getTipo() {
		return "Familiar";
	}
	
	@Override
	public int getOrdem() {
		return 10;
	}
	
	@Override
	public boolean isAtivo() {
		return usuarioFamiliar.getDiscenteMedio().getStatus() == StatusDiscente.ATIVO
		 	|| usuarioFamiliar.getDiscenteMedio().getStatus() == StatusDiscente.ATIVO_DEPENDENCIA;
	}

	public UsuarioFamiliar getUsuarioFamiliar() {
		return usuarioFamiliar;
	}

	public void setUsuarioFamiliar(UsuarioFamiliar usuarioFamiliar) {
		this.usuarioFamiliar = usuarioFamiliar;
	}
	
	@Override
	public boolean isFamiliar() {
		return true;
	}

	@Override
	public EstrategiaPopularVinculo getEstrategia() {
		return new EstrategiaPopularVinculoFamiliar();
	}

	@Override
	public Object getIdentificador() {
		return usuarioFamiliar.getDiscenteMedio().getMatricula();
	}

	@Override
	public String getOutrasInformacoes() {
		return "Aluno: " + usuarioFamiliar.getDiscenteMedio().getNome();
	}

}
