/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/04/2012
 */
package fundacao.integracao.projetos;

import java.io.Serializable;


/**
 * Data Transfer Object para informações dos orgãos financiadores do projeto.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class ProjetoOrigemRecursoDTO implements Serializable {
	
	/** Representa o nome do orgão financiador.*/
	private String nomeOrgaoFinanciador;

	/** Representa o CPFCNPJ do orgão financiador.*/
	private Long cpfCnpjOrgaoFinanciador;

	public void setNomeOrgaoFinanciador(String nomeOrgaoFinanciador) {
		this.nomeOrgaoFinanciador = nomeOrgaoFinanciador;
	}

	public String getNomeOrgaoFinanciador() {
		return nomeOrgaoFinanciador;
	}

	public void setCpfCnpjOrgaoFinanciador(Long cpfCnpjOrgaoFinanciador) {
		this.cpfCnpjOrgaoFinanciador = cpfCnpjOrgaoFinanciador;
	}

	public Long getCpfCnpjOrgaoFinanciador() {
		return cpfCnpjOrgaoFinanciador;
	}

}
