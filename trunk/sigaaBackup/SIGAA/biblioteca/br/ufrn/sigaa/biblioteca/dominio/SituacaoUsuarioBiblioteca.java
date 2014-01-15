/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 02/03/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.dominio;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * <p> Guardas as situações dos usuários biblioteca com relação aos emprestimos, </p>
 * 
 * @author jadson
 *
 */
public enum SituacaoUsuarioBiblioteca implements Serializable{

	
	
	/** 
	 * <p>SEM_PENDENCIA = O usuário está sem nenhum pedência na biblioteca.</p>
	 * <p>POSSUI_EMPRESTIMOS_ATIVOS =  Situação quando o usuário possui empréstimos mas não estão atrasados nem o usuario está suspenso.</p>
	 * <p>ESTA_SUSPENSO =  Situação quando o usuário está suspenso.</p>
	 * <p>ESTA_MULTADO =  Situação quando o usuário está multado.</p>
	 * <p>POSSUI_EMPRESTIMOS_ATRASADOS = Situação quando o usuário está com empréstimo atrasados.</p>
	 * <p>ESTA_BLOQUEADO = Não pode realizar empréstimo pois seu cadastro foi bloqueado</p>
	 */ 
	SEM_PENDENCIA(1, "Usuário Sem Pendências"), POSSUI_EMPRESTIMOS_ATIVOS(2, "Usuário Possui Empréstimos Ativos"), ESTA_SUSPENSO(3, "Usuário está Suspenso")
			, ESTA_MULTADO(4, "Usuário está Multado"), POSSUI_EMPRESTIMOS_ATRASADOS(5, "Usuário Possui Empréstimos Atrasados"), ESTA_BLOQUEADO(6, "Usuário está  Bloqueado")
			, ESTA_INATIVO(7, "Usuário está Inativo");
	
	
	
	/** 
	 * O valor que representa a pendência
	 */
	private int valor;
	
	/**
	 * A descrição resumida da situação, que é passada no construtor e não pode ser alterada
	 */
	private String descricaoResumida;
	
	/**
	 * <p>A descrição completa do problema, contém informações específicas da punição que o usuário sofreu</p>
	 * 
	 * <p>Geralmente só utilizada para as situações do usuário que geram punição, caso contrário será igual à descricaoResumida.</p>
	 * 
	 * <p><strong>Importante:</strong> Essa informação não é acessada no lado do desktop</p>
	 */
	private String descricaoCompleta;
	
	
	
	private SituacaoUsuarioBiblioteca(int valor, String descricaoResumida){
		this.valor = valor;
		this.descricaoResumida = descricaoResumida;
		this.descricaoCompleta = descricaoResumida;
	}

	
	public int getValor() {
		return valor;
	}
	
	public String getDescricaoResumida() {
		return descricaoResumida;
	}
	
	public String getDescricaoCompleta() {
			return descricaoCompleta;
	}

	public void setDescricaoCompleta(String descricaoCompleta) {
		this.descricaoCompleta = descricaoCompleta;
	}

	@Override
	public String toString() {
		return String.valueOf(valor);
	}
	
	/**
	 *  Verifica as situações que não permitem ao usuário realizar empréstimo
	 *
	 * @return
	 */
	public boolean isSituacaoImpedeEmprestimos(){
		if(this.equals(ESTA_SUSPENSO) || this.equals(ESTA_MULTADO)
				|| this.equals(POSSUI_EMPRESTIMOS_ATRASADOS) || this.equals(ESTA_BLOQUEADO) || this.equals(ESTA_INATIVO) )
			return true;
		return false;
	}
	
	/**
	 *  Verifica as situações que não permitem emitir o documento de quitação para o usuário.
	 *
	 * @return
	 */
	public boolean isSituacaoImpedeEmissaoDocumentoQuitacao(){
		if(this.equals(ESTA_MULTADO) )
			return true;
		return false;
	}
	
	
	/**
	 * Usado os relatórios
	 */
	public static final Set<SituacaoUsuarioBiblioteca> POSSIVEIS_SITUACAO_DO_USUARIO =
		new HashSet<SituacaoUsuarioBiblioteca>( Arrays.asList( SituacaoUsuarioBiblioteca.SEM_PENDENCIA,
			SituacaoUsuarioBiblioteca.ESTA_SUSPENSO,
			SituacaoUsuarioBiblioteca.POSSUI_EMPRESTIMOS_ATIVOS,
			SituacaoUsuarioBiblioteca.POSSUI_EMPRESTIMOS_ATRASADOS)
		);
	
	
	/**
	 * Retorna a situação correspondente ao valor passado. Usado no lado cliente. Já que o enum não é 
	 * mais passados no DTOs. 
	 *  
	 *
	 * @param valorVinculoSelecionado
	 * @return
	 */
	public static SituacaoUsuarioBiblioteca getSituacao(final Integer valorSituacao) {
		
		
		if( valorSituacao.equals(SEM_PENDENCIA.valor))
			return SEM_PENDENCIA;
			
		if( valorSituacao.equals(POSSUI_EMPRESTIMOS_ATIVOS.valor))
			return POSSUI_EMPRESTIMOS_ATIVOS;
		
		if( valorSituacao.equals(ESTA_SUSPENSO.valor))
			return ESTA_SUSPENSO;
		
		if( valorSituacao.equals(ESTA_MULTADO.valor))
			return ESTA_MULTADO;
		
		if( valorSituacao.equals(POSSUI_EMPRESTIMOS_ATRASADOS.valor))
			return POSSUI_EMPRESTIMOS_ATRASADOS;
		
		if( valorSituacao.equals(ESTA_BLOQUEADO.valor))
			return ESTA_BLOQUEADO;
		
		if( valorSituacao.equals(ESTA_INATIVO.valor))
			return ESTA_INATIVO;
		
		throw new IllegalArgumentException("O valor passado não é válido para a situação do usuário");
	}

}