/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 08/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.dominio;



/**
 *
 * <p> Guardas os tipos de v�nculos que o usu�rio da biblioteca pode ter no sistema. </p>
 * 
 * @author jadson
 *
 */
public enum VinculoUsuarioBiblioteca {

	/** Aluno de gradua��o */
	ALUNO_GRADUACAO(0, "ALUNO DE GRADUA��O"),
	
	/** Aluno de n�vem m�dio t�cnico */
	ALUNO_TECNICO_MEDIO(1, "ALUNO M�DIO/T�CNICO"),

	/** Aluno de p�s-gradua��o */
	ALUNO_POS_GRADUCACAO(2, "ALUNO DE P�S-GRADUA��O"),
	
	/** Servidor t�cnico-administrativo */
	SERVIDOR_TECNO_ADMINISTRATIVO(3, "SERVIDOR T�CNICO-ADMINISTRATIVO"), 

	/** Professor da Institui��o */
	DOCENTE(4, "DOCENTE"),
	
	/** Biblioteca da Institui��o */
	BIBLIOTECA(5, "BIBLIOTECA"), 
	
	 /** Biblioteca externa � Institui��o */
	BIBLIOTECA_EXTERNA(6, "BIBLIOTECA EXTERNA"),
	
	/** Usu�rio externo � Institui��o */
	USUARIO_EXTERNO(7, "USU�RIO EXTERNO"),
	
	/** Docente externo � Institui��o */
	DOCENTE_EXTERNO(8, "DOCENTE EXTERNO"),
	
	/** V�nculo que n�o permite a reailza��o de empr�stimos */
	INATIVO(9, "INATIVO ( sem v�nculos ativos para realizar empr�stimos )"),  
	
	/** Aluno do ensino infantil */
	ALUNO_INFANTIL(10, "ALUNO INFANTIL");
	
	
	/** 
	 * O valor que representa o v�nculo
	 */
	private int valor;
	
	/**
	 * A descri��o do v�nculo mostrada as usu�rios
	 */
	private String descricao;
	
	
	
	private VinculoUsuarioBiblioteca(int valor, String descricao){
		this.valor = valor;
		this.descricao = descricao;
	}
	
	/**
	 *  Verifica se n�o est� inativo e pode realizar empr�stimos no sistema
	 *
	 * @return
	 */
	public boolean isPodeRealizarEmprestimos(){
		if(! this.equals(INATIVO) )
			return true;
		else
			return false;
	}
	
	/**
	 * verifica se o v�nculo � destinado a alunos
	 *
	 * @return
	 */
	public boolean isVinculoAluno(){
		if( this.equals(ALUNO_INFANTIL) ||  this.equals(ALUNO_TECNICO_MEDIO) || this.equals(ALUNO_GRADUACAO) ||this.equals(ALUNO_POS_GRADUCACAO) )
			return true;
		else
			return false;
	}
	
	/**
	 * retorna os v�nculos de alunos
	 *
	 * @return
	 */
	public static VinculoUsuarioBiblioteca[] getVinculosAluno(){
		return new VinculoUsuarioBiblioteca[]{ALUNO_INFANTIL, ALUNO_TECNICO_MEDIO, ALUNO_GRADUACAO, ALUNO_POS_GRADUCACAO};
	}
	
	/**
	 * retorna os v�nculos de alunos
	 *
	 * @return
	 */
	public static VinculoUsuarioBiblioteca[] getVinculosBibliotecas(){
		return new VinculoUsuarioBiblioteca[]{BIBLIOTECA, BIBLIOTECA_EXTERNA};
	}
	
	/**
	 *  verifica se o v�nculo � destinado a servidores
	 *
	 * @return
	 */
	public boolean isVinculoServidor(){
		if( this.equals(SERVIDOR_TECNO_ADMINISTRATIVO) || this.equals(DOCENTE) )
			return true;
		else
			return false;
	}
	
	/**
	 *  retorna os v�nculos de servidores
	 *
	 * @return
	 */
	public static VinculoUsuarioBiblioteca[] getVinculosServidor(){
		return new VinculoUsuarioBiblioteca[]{SERVIDOR_TECNO_ADMINISTRATIVO, DOCENTE};
	}
	
	/**
	 * retorna todos os v�nculos menos o v�nculo inativo
	 *
	 * @return
	 */
	public static VinculoUsuarioBiblioteca[] getVinculosPodeRealizarEmprestimos(){
		return new VinculoUsuarioBiblioteca[]{ALUNO_INFANTIL, ALUNO_TECNICO_MEDIO, ALUNO_GRADUACAO, ALUNO_POS_GRADUCACAO
				, SERVIDOR_TECNO_ADMINISTRATIVO
				, DOCENTE, DOCENTE_EXTERNO
				, BIBLIOTECA, BIBLIOTECA_EXTERNA
				, USUARIO_EXTERNO};
	}
	
	/**
	 *  erifica se o v�nculo � destinado a comunidade externa
	 *
	 * @return
	 */
	public boolean isVinculoExterno(){
		if( this.equals(DOCENTE_EXTERNO) || this.equals(USUARIO_EXTERNO) || this.equals(BIBLIOTECA_EXTERNA))
			return true;
		else
			return false;
	}
	
	/**
	 *  Verifica se o v�nculo � destinado a bibliotecas
	 *
	 * @return
	 */
	public boolean isVinculoBiblioteca(){
		if( this.equals(BIBLIOTECA) || this.equals(BIBLIOTECA_EXTERNA) )
			return true;
		else
			return false;
	}
	
	
	/**
	 * Representa os usu�rio que fezem parte de comunidade externa.  Usado os relat�rios
	 */
	public static final VinculoUsuarioBiblioteca[] COMUNIDADE_EXTERNA = {BIBLIOTECA_EXTERNA, USUARIO_EXTERNO, DOCENTE_EXTERNO };
		
	/**
	 * Representa os usu�rio que fezem parte de comunidade interna. Usado os relat�rios
	 */
	public static final VinculoUsuarioBiblioteca[] COMUNIDADE_INTERNA = {ALUNO_INFANTIL, ALUNO_GRADUACAO, ALUNO_TECNICO_MEDIO, ALUNO_POS_GRADUCACAO, BIBLIOTECA, DOCENTE, SERVIDOR_TECNO_ADMINISTRATIVO };

	public int getValor() {
		return valor;
	}

	public String getDescricao() {
		return descricao;
	}

	@Override
	public String toString() {
		return String.valueOf(valor);
	}

	/**
	 * Retorna o v�nculo correspondente ao valor passado
	 *
	 * @param valorVinculoSelecionado
	 * @return
	 */
	public static VinculoUsuarioBiblioteca getVinculo(final Integer valorVinculoSelecionado) {
		

		if( valorVinculoSelecionado.equals(ALUNO_INFANTIL.valor))
			return ALUNO_INFANTIL;
		
		if( valorVinculoSelecionado.equals(ALUNO_TECNICO_MEDIO.valor))
			return ALUNO_TECNICO_MEDIO;
			
		if( valorVinculoSelecionado.equals(ALUNO_GRADUACAO.valor))
			return ALUNO_GRADUACAO;
		
		if( valorVinculoSelecionado.equals(ALUNO_POS_GRADUCACAO.valor))
			return ALUNO_POS_GRADUCACAO;
		
		if( valorVinculoSelecionado.equals(SERVIDOR_TECNO_ADMINISTRATIVO.valor))
			return SERVIDOR_TECNO_ADMINISTRATIVO;
		
		if( valorVinculoSelecionado.equals(DOCENTE.valor))
			return DOCENTE;
		
		if( valorVinculoSelecionado.equals(BIBLIOTECA.valor))
			return BIBLIOTECA;
		
		if( valorVinculoSelecionado.equals(BIBLIOTECA_EXTERNA.valor))
			return BIBLIOTECA_EXTERNA;
		
		if( valorVinculoSelecionado.equals(USUARIO_EXTERNO.valor))
			return USUARIO_EXTERNO;
		
		if( valorVinculoSelecionado.equals(DOCENTE_EXTERNO.valor))
			return DOCENTE_EXTERNO;
		
		return INATIVO;
	}
	
	
	
}