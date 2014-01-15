/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/06/2010
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;

/**
 * Classe responsável pelo encapsulamento dos objetos para o cadastro de dados
 * pessoais e foto do candidato do Vestibular.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class MovimentoPessoaVestibular extends AbstractMovimentoAdapter {

	/** Arquivo de foto do candidato. */
	private UploadedFile foto;

	/** Dados pessoais do candidato. */
	private PessoaVestibular pessoaVestibular;
	
	/** Senha não criptografada, utilizada no e-mail enviado ao candidato com os dados do cadastro. */
	private String senhaAberta;
	
	/** Indica se deve enviar e-mail para o candidato confirmando o cadastro de dados pessoais. */
	private boolean enviaEmail;

	/** Retorna o arquivo de foto do candidato. 
	 * @return
	 */
	public UploadedFile getFoto() {
		return foto;
	}

	/** Seta o arquivo de foto do candidato.
	 * @param foto
	 */
	public void setFoto(UploadedFile foto) {
		this.foto = foto;
	}

	/** Retorna os dados pessoais do candidato.
	 * @return
	 */
	public PessoaVestibular getPessoaVestibular() {
		return pessoaVestibular;
	}

	/** Seta os dados pessoais do candidato.
	 * @param pessoaVestibular
	 */
	public void setPessoaVestibular(PessoaVestibular pessoaVestibular) {
		this.pessoaVestibular = pessoaVestibular;
	}

	/** Retorna a senha não criptografada, utilizada no e-mail enviado ao candidato com os dados do cadastro. 
	 * @return
	 */
	public String getSenhaAberta() {
		return senhaAberta;
	}

	/** Seta a senha não criptografada, utilizada no e-mail enviado ao candidato com os dados do cadastro.
	 * @param senhaAberta
	 */
	public void setSenhaAberta(String senhaAberta) {
		this.senhaAberta = senhaAberta;
	}

	public boolean isEnviaEmail() {
		return enviaEmail;
	}

	public void setEnviaEmail(boolean enviaEmail) {
		this.enviaEmail = enviaEmail;
	}
}
