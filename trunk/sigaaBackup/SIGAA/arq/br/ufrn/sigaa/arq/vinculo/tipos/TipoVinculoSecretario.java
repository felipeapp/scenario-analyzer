/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.tipos;

import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculo;
import br.ufrn.sigaa.arq.vinculo.processamento.EstrategiaPopularVinculoSecretaria;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;

/**
 * Representa o tipo de vínculo para o Secretário
 * 
 * @author Henrique André
 *
 */
@SuppressWarnings("serial")
public class TipoVinculoSecretario extends TipoVinculoAbstract {

	public TipoVinculoSecretario(SecretariaUnidade secretaria) {
		this.secretaria = secretaria;
	}
	
	@Override
	public EstrategiaPopularVinculo getEstrategia() {
		return new EstrategiaPopularVinculoSecretaria();
	}	
	
	@Override
	public int getOrdem() {
		return 3;
	}
	
	/**
	 * Referencia para Secretário
	 */
	private SecretariaUnidade secretaria;
	
	@Override
	public String getTipo() {
		return "Secretário";
	}

	@Override
	public boolean isAtivo() {
		return secretaria.isAtivo();
	}

	@Override
	public Object getIdentificador() {
		return secretaria.getUsuario().getPessoa().getCpfCnpjFormatado();
	}

	@Override
	public String getOutrasInformacoes() {
		
		StringBuilder str = new StringBuilder();
		str.append("Secretaria ");
		
		if (secretaria.getUnidade() != null)
			str.append(" da Unidade: " + secretaria.getUnidade().getNome().toUpperCase().trim() );
		else
			str.append(" do Curso: " + secretaria.getCurso().getNome().toUpperCase().trim() );
		
		return str.toString();
	}

	@Override
	public boolean isSecretario() {
		return true;
	}

	public SecretariaUnidade getSecretaria() {
		return secretaria;
	}

}
