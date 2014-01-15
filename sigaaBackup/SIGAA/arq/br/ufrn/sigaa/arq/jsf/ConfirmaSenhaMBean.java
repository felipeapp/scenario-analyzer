/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jun 13, 2007
 *
 */
package br.ufrn.sigaa.arq.jsf;

import java.util.Date;

/**
 * MBean utilizado para casos de uso em que precisa de uma confirmação da senha
 * do usuário para a efetivação do mesmo.
 *
 * @author Victor Hugo
 *
 */
public class ConfirmaSenhaMBean extends SigaaAbstractController {

	public static final int SHOW_DT_NASCIMENTO = 1;

	public static final int SHOW_IDENTIDADE = 2;

	public static final int APENAS_SENHA = 3;

	private String senha;

	private Date dataNascimento;

	private String identidade;

	private int opcaoExibir;

	private boolean apenasSenha = false;

	public ConfirmaSenhaMBean() {
		opcaoExibir = (int) ( Math.random() * 2 + 1  );
	}

	public String getExibirSenha(){

		/*String param = (String) getCurrentRequest().getAttribute("exibirApenasSenha");
		if( !StringUtils.isEmpty(param) )
			apenasSenha = Boolean.valueOf(param);

		if( apenasSenha )
			opcaoExibir = APENAS_SENHA;*/

		return "";
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getIdentidade() {
		return identidade;
	}

	public void setIdentidade(String identidade) {
		this.identidade = identidade;
	}

	public int getOpcaoExibir() {
		return opcaoExibir;
	}

	public void setOpcaoExibir(int opcaoExibir) {
		this.opcaoExibir = opcaoExibir;
	}

	public boolean isShowDataNascimento(){
		return opcaoExibir == SHOW_DT_NASCIMENTO;
	}

	public boolean isShowIdentidade(){
		return opcaoExibir == SHOW_IDENTIDADE;
	}

	public boolean isApenasSenha() {
		return apenasSenha;
	}

	public void setApenasSenha(boolean apenasSenha) {
		this.apenasSenha = apenasSenha;
	}

}
