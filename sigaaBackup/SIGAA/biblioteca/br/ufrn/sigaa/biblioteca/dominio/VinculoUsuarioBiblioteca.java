/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 08/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.dominio;



/**
 *
 * <p> Guardas os tipos de vínculos que o usuário da biblioteca pode ter no sistema. </p>
 * 
 * @author jadson
 *
 */
public enum VinculoUsuarioBiblioteca {

	/** Aluno de graduação */
	ALUNO_GRADUACAO(0, "ALUNO DE GRADUAÇÃO"),
	
	/** Aluno de nívem médio técnico */
	ALUNO_TECNICO_MEDIO(1, "ALUNO MÉDIO/TÉCNICO"),

	/** Aluno de pós-graduação */
	ALUNO_POS_GRADUCACAO(2, "ALUNO DE PÓS-GRADUAÇÃO"),
	
	/** Servidor técnico-administrativo */
	SERVIDOR_TECNO_ADMINISTRATIVO(3, "SERVIDOR TÉCNICO-ADMINISTRATIVO"), 

	/** Professor da Instituição */
	DOCENTE(4, "DOCENTE"),
	
	/** Biblioteca da Instituição */
	BIBLIOTECA(5, "BIBLIOTECA"), 
	
	 /** Biblioteca externa à Instituição */
	BIBLIOTECA_EXTERNA(6, "BIBLIOTECA EXTERNA"),
	
	/** Usuário externo à Instituição */
	USUARIO_EXTERNO(7, "USUÁRIO EXTERNO"),
	
	/** Docente externo à Instituição */
	DOCENTE_EXTERNO(8, "DOCENTE EXTERNO"),
	
	/** Vínculo que não permite a reailzação de empréstimos */
	INATIVO(9, "INATIVO ( sem vínculos ativos para realizar empréstimos )"),  
	
	/** Aluno do ensino infantil */
	ALUNO_INFANTIL(10, "ALUNO INFANTIL");
	
	
	/** 
	 * O valor que representa o vínculo
	 */
	private int valor;
	
	/**
	 * A descrição do vínculo mostrada as usuários
	 */
	private String descricao;
	
	
	
	private VinculoUsuarioBiblioteca(int valor, String descricao){
		this.valor = valor;
		this.descricao = descricao;
	}
	
	/**
	 *  Verifica se não está inativo e pode realizar empréstimos no sistema
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
	 * verifica se o vínculo é destinado a alunos
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
	 * retorna os vínculos de alunos
	 *
	 * @return
	 */
	public static VinculoUsuarioBiblioteca[] getVinculosAluno(){
		return new VinculoUsuarioBiblioteca[]{ALUNO_INFANTIL, ALUNO_TECNICO_MEDIO, ALUNO_GRADUACAO, ALUNO_POS_GRADUCACAO};
	}
	
	/**
	 * retorna os vínculos de alunos
	 *
	 * @return
	 */
	public static VinculoUsuarioBiblioteca[] getVinculosBibliotecas(){
		return new VinculoUsuarioBiblioteca[]{BIBLIOTECA, BIBLIOTECA_EXTERNA};
	}
	
	/**
	 *  verifica se o vínculo é destinado a servidores
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
	 *  retorna os vínculos de servidores
	 *
	 * @return
	 */
	public static VinculoUsuarioBiblioteca[] getVinculosServidor(){
		return new VinculoUsuarioBiblioteca[]{SERVIDOR_TECNO_ADMINISTRATIVO, DOCENTE};
	}
	
	/**
	 * retorna todos os vínculos menos o vínculo inativo
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
	 *  erifica se o vínculo é destinado a comunidade externa
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
	 *  Verifica se o vínculo é destinado a bibliotecas
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
	 * Representa os usuário que fezem parte de comunidade externa.  Usado os relatórios
	 */
	public static final VinculoUsuarioBiblioteca[] COMUNIDADE_EXTERNA = {BIBLIOTECA_EXTERNA, USUARIO_EXTERNO, DOCENTE_EXTERNO };
		
	/**
	 * Representa os usuário que fezem parte de comunidade interna. Usado os relatórios
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
	 * Retorna o vínculo correspondente ao valor passado
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