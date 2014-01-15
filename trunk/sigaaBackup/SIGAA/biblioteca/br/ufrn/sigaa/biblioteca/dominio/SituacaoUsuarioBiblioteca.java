/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p> Guardas as situa��es dos usu�rios biblioteca com rela��o aos emprestimos, </p>
 * 
 * @author jadson
 *
 */
public enum SituacaoUsuarioBiblioteca implements Serializable{

	
	
	/** 
	 * <p>SEM_PENDENCIA = O usu�rio est� sem nenhum ped�ncia na biblioteca.</p>
	 * <p>POSSUI_EMPRESTIMOS_ATIVOS =  Situa��o quando o usu�rio possui empr�stimos mas n�o est�o atrasados nem o usuario est� suspenso.</p>
	 * <p>ESTA_SUSPENSO =  Situa��o quando o usu�rio est� suspenso.</p>
	 * <p>ESTA_MULTADO =  Situa��o quando o usu�rio est� multado.</p>
	 * <p>POSSUI_EMPRESTIMOS_ATRASADOS = Situa��o quando o usu�rio est� com empr�stimo atrasados.</p>
	 * <p>ESTA_BLOQUEADO = N�o pode realizar empr�stimo pois seu cadastro foi bloqueado</p>
	 */ 
	SEM_PENDENCIA(1, "Usu�rio Sem Pend�ncias"), POSSUI_EMPRESTIMOS_ATIVOS(2, "Usu�rio Possui Empr�stimos Ativos"), ESTA_SUSPENSO(3, "Usu�rio est� Suspenso")
			, ESTA_MULTADO(4, "Usu�rio est� Multado"), POSSUI_EMPRESTIMOS_ATRASADOS(5, "Usu�rio Possui Empr�stimos Atrasados"), ESTA_BLOQUEADO(6, "Usu�rio est�  Bloqueado")
			, ESTA_INATIVO(7, "Usu�rio est� Inativo");
	
	
	
	/** 
	 * O valor que representa a pend�ncia
	 */
	private int valor;
	
	/**
	 * A descri��o resumida da situa��o, que � passada no construtor e n�o pode ser alterada
	 */
	private String descricaoResumida;
	
	/**
	 * <p>A descri��o completa do problema, cont�m informa��es espec�ficas da puni��o que o usu�rio sofreu</p>
	 * 
	 * <p>Geralmente s� utilizada para as situa��es do usu�rio que geram puni��o, caso contr�rio ser� igual � descricaoResumida.</p>
	 * 
	 * <p><strong>Importante:</strong> Essa informa��o n�o � acessada no lado do desktop</p>
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
	 *  Verifica as situa��es que n�o permitem ao usu�rio realizar empr�stimo
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
	 *  Verifica as situa��es que n�o permitem emitir o documento de quita��o para o usu�rio.
	 *
	 * @return
	 */
	public boolean isSituacaoImpedeEmissaoDocumentoQuitacao(){
		if(this.equals(ESTA_MULTADO) )
			return true;
		return false;
	}
	
	
	/**
	 * Usado os relat�rios
	 */
	public static final Set<SituacaoUsuarioBiblioteca> POSSIVEIS_SITUACAO_DO_USUARIO =
		new HashSet<SituacaoUsuarioBiblioteca>( Arrays.asList( SituacaoUsuarioBiblioteca.SEM_PENDENCIA,
			SituacaoUsuarioBiblioteca.ESTA_SUSPENSO,
			SituacaoUsuarioBiblioteca.POSSUI_EMPRESTIMOS_ATIVOS,
			SituacaoUsuarioBiblioteca.POSSUI_EMPRESTIMOS_ATRASADOS)
		);
	
	
	/**
	 * Retorna a situa��o correspondente ao valor passado. Usado no lado cliente. J� que o enum n�o � 
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
		
		throw new IllegalArgumentException("O valor passado n�o � v�lido para a situa��o do usu�rio");
	}

}