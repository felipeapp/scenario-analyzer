/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 * 
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import br.ufrn.arq.dominio.MovimentoCadastro;

/**
 * Movimento para encapsular as operações de cadastro de componentes
 * curriculares
 *
 * @author amdantas
 *
 */
public class ComponenteCurricularMov extends MovimentoCadastro {

	public static final int ACAO_SOLICITAR_CADASTRO = 1;

	public static final int ACAO_NEGAR_CADASTRO = 2;

	public static final int ACAO_DESATIVAR = 3;

	private String observacaoCadastro;

	private boolean processarExpressoes = true;

	public boolean isProcessarExpressoes() {
		return processarExpressoes;
	}

	public void setProcessarExpressoes(boolean processarExpressoes) {
		this.processarExpressoes = processarExpressoes;
	}

	public String getObservacaoCadastro() {
		return observacaoCadastro;
	}

	public void setObservacaoCadastro(String observacaoCadastro) {
		this.observacaoCadastro = observacaoCadastro;
	}

}
