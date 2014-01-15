/*
 * MovimentoSenhaUsuarioBiblioteca.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TermoAdesaoSistemaBibliotecas;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;

/**
 *
 *    Guarda os dados que vao ser passados ao precessador para criar/atualizar a senha do usuario
 * da biblioteca.
 *
 * @author jadson
 * @since 06/10/2008
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoSenhaUsuarioBiblioteca extends AbstractMovimentoAdapter{

	
	/** O usuário biblioteca que vai ser criado ou atualizado */
	private UsuarioBiblioteca usuarioBiblioteca;
	
	/** O termo de adesão ao sistema de bibiotecas */
	private TermoAdesaoSistemaBibliotecas termoAdesao;
	
	/** Usado nos casos em que o usuário biblioteca já existia, mas sem a informação do vínculo, neste caso vai ter que atualizar além da senha o vínculo */
	private boolean atualizaVinculo;
	
	/** Todas as contas que o usuário já teve na biblioteca, para não deixar criar uma nova se tiver punição em alguma anterior.*/
	private List<UsuarioBiblioteca> contasUsuarioBiblioteca;
	
	
	public void setUsuarioBiblioteca(UsuarioBiblioteca usuarioBiblioteca) {
		this.usuarioBiblioteca = usuarioBiblioteca;
	}

	public UsuarioBiblioteca getUsuarioBiblioteca() {
		return usuarioBiblioteca;
	}

	public boolean isAtualizaVinculo() {
		return atualizaVinculo;
	}

	public void setAtualizaVinculo(boolean atualizaVinculo) {
		this.atualizaVinculo = atualizaVinculo;
	}

	public List<UsuarioBiblioteca> getContasUsuarioBiblioteca() {
		return contasUsuarioBiblioteca;
	}

	public void setContasUsuarioBiblioteca(List<UsuarioBiblioteca> contasUsuarioBiblioteca) {
		this.contasUsuarioBiblioteca = contasUsuarioBiblioteca;
	}

	public TermoAdesaoSistemaBibliotecas getTermoAdesao() {
		return termoAdesao;
	}

	public void setTermoAdesao(TermoAdesaoSistemaBibliotecas termoAdesao) {
		this.termoAdesao = termoAdesao;
	}
	
	
	
}
