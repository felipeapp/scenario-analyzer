/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 09/04/2010
 */
package br.ufrn.comum.dominio;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Classe com constantes para armazenar os tipos de usuário existentes nos
 * sistemas.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 * 
 */
@Entity @Table(name="tipo_usuario", schema="comum")
public class TipoUsuario implements PersistDB {

	public static final int TIPO_SERVIDOR = 1;

	public static final int TIPO_ALUNO = 2;

	public static final int TIPO_CREDOR = 3;

	public static final int TIPO_OUTROS = 4;

	/**
	 * Usuário consultor de projetos de pesquisa que entram no sistema para
	 * avaliar projetos de pesquisa.
	 */
	public static final int TIPO_CONSULTOR = 5;

	public static final int DOCENTE_EXTERNO = 6;

	/**
	 * Usuário de empresas consignatárias que consultam a margem consignável do
	 * servidor
	 */
	public static final int TIPO_CONSIGNATARIA = 7;

	/**
	 * Usuários de empresas de planos de saúde que confirmam os planos aderidos
	 * pelos servidores.
	 */
	public static final int TIPO_PLANO_SAUDE = 8;

	/**
	 * Usuário das instituições que possuem termo de cooperação com a UFRN.
	 */
	public static final int TIPO_COOPERACAO = 9;

	/** Usuário preceptor de estágio */
	public static final int TIPO_PRECEPTOR_ESTAGIO = 10;
	
	/** Usuário do Familiar do discente */
	public static final int TIPO_FAMILIAR = 11;	
	
	/** Identificador do tipo de usuário. */
	@Id
	private int id;
	
	/** Nome do tipo de usuário */
	private String nome;
	
	/** Se o usuário é interno, ou seja, faz parte da instituição, ou é externo, ou seja, não faz parte da instituição. */
	private boolean interno;
	
	public TipoUsuario() {
		
	}
	
	public TipoUsuario(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public boolean isInterno() {
		return interno;
	}

	public void setInterno(boolean interno) {
		this.interno = interno;
	}

	/**
	 * Retorna a descrição dos tipos de usuário.
	 * @param tipo
	 * @return
	 */
	public static String getTipoUsuarioDesc(int tipo) {
		switch (tipo) {
		case TIPO_SERVIDOR:
			return "Servidor";
		case TIPO_ALUNO:
			return "Aluno";
		case TIPO_CONSULTOR:
			return "Consultor";
		case TIPO_CREDOR:
			return "Credor";
		case TIPO_OUTROS:
			return "Outros";
		case TIPO_COOPERACAO:
			return "Cooperação";
		case TIPO_FAMILIAR:
			return "Familiar";			
		}
		

		return null;
	}
}
