/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.cv.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ava.dominio.ArquivoUsuario;
import br.ufrn.sigaa.cv.dominio.ArquivoComunidade;
import br.ufrn.sigaa.cv.dominio.ComunidadeVirtual;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Movimento que auxilia no cadastro de arquivos para as comunidades virtuais.
 * 
 * @author davidpereira
 *
 */
public class MovimentoArquivoCV extends AbstractMovimentoAdapter {

	private ArquivoComunidade arquivoComunidade;
	
	private ArquivoUsuario arquivo;

	private Usuario usuario;
	
	private ComunidadeVirtual comunidade;
	
	private List<String> cadastrarEm;

	public ArquivoComunidade getArquivoComunidade() {
		return arquivoComunidade;
	}

	public void setArquivoComunidade(ArquivoComunidade arquivoComunidade) {
		this.arquivoComunidade = arquivoComunidade;
	}

	public ArquivoUsuario getArquivo() {
		return arquivo;
	}

	public void setArquivo(ArquivoUsuario arquivo) {
		this.arquivo = arquivo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public ComunidadeVirtual getComunidade() {
		return comunidade;
	}

	public void setComunidade(ComunidadeVirtual comunidade) {
		this.comunidade = comunidade;
	}

	public List<String> getCadastrarEm() {
		return cadastrarEm;
	}

	public void setCadastrarEm(List<String> cadastrarEm) {
		this.cadastrarEm = cadastrarEm;
	}

}
