package br.ufrn.academico.dominio;

/**
 * Classe que mantém os tipos de projetos acadêmicos que podem ser consultados
 * 
 * @author wendell
 *
 */
public class TipoProjetoAcademico {

	public static final int PROJETO_PESQUISA = 1;
	public static final int PROJETO_EXTENSAO = 2;
	public static final int CURSO_LATO_SENSU = 3;
	public static final int PROJETO_ASSOCIADO = 4;
	
	/**
	 * Retorna a descrição de um determinado tipo de projeto acadêmico
	 * 
	 * @param tipo
	 * @return
	 */
	public static String getDescricao(int tipo) {
		switch(tipo) {
			case PROJETO_PESQUISA: return "PESQUISA";
			case PROJETO_EXTENSAO: return "EXTENSÃO";
			case CURSO_LATO_SENSU: return "LATO SENSU";
			case PROJETO_ASSOCIADO: return "PROJETO ASSOCIADO";
			default: return "TIPO DESCONHECIDO";
		}
	}
	
}
