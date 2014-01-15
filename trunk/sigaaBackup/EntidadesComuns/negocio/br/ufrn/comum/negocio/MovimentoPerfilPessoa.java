/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Criado em: 2007/10/05
 */
package br.ufrn.comum.negocio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.comum.dominio.PerfilPessoa;

/**
 * Movimento utilizado para atualizações dos perfis dos usuários
 * 
 * @author David Pereira
 *
 */
public class MovimentoPerfilPessoa extends AbstractMovimentoAdapter {

	private PerfilPessoa perfil;
	
	private Integer idFoto;
	
	private UploadedFile foto;

	public PerfilPessoa getPerfil() {
		return perfil;
	}

	public void setPerfil(PerfilPessoa perfil) {
		this.perfil = perfil;
	}

	public UploadedFile getFoto() {
		return foto;
	}

	public void setFoto(UploadedFile foto) {
		this.foto = foto;
	}

	public Integer getIdFoto() {
		return idFoto;
	}

	public void setIdFoto(Integer idFoto) {
		this.idFoto = idFoto;
	}
	
}
