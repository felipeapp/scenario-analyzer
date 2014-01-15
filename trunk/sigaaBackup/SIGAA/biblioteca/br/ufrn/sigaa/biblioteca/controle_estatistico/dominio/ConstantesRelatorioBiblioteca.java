package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

/** 
 * Classe criada para agrupar as constantes utilizadas nos relatórios do módulo de bibliotecas.
 * 
 * @author Felipe
 */
public class ConstantesRelatorioBiblioteca {
	
	// Turnos
	
	public enum Turnos{
		
		TODOS(0, "Todos"), MANHA(1, "Manhã"), TARDE(2, "Tarde"), NOITE(3, "Noite");
		
		private final int valor;
		
		private final String descricao;
		
		
		private Turnos(int valor, String descricao){
			this.valor = valor;
			this.descricao = descricao;
		}

		public int getValor() {
			return valor;
		}
		
		public String getDescricao() {
			return descricao;
		}
		
		public String getDescricaoCompleta() {
			return valor+" - "+descricao;
		}
		
		public static String getDescricao(int valor) {
			if( valor == TODOS.getValor()){
				return TODOS.getDescricao();
			}
			if( valor == MANHA.getValor()){
				return MANHA.getDescricao();
			}
			if( valor == TARDE.getValor()){
				return TARDE.getDescricao();
			}
			if( valor == NOITE.getValor()){
				return NOITE.getDescricao();
			}
			return "";
		}
		
	}
	
	
	// Tipos de operação sobre o acervo a serem visualizados
	
	/** Tipo de operação que engloba catalogações e inserções de itens. */
	public static final int TIPO_ACERVO_TODOS = 0;
	/** Tipo de Operação catalogação de novo título. */
	public static final int TIPO_ACERVO_TITULOS = 1;
	/** Tipo de operação inserção de itens (materiais) em títulos já catalogados. */
	public static final int TIPO_ACERVO_MATERIAIS = 2;
	
}
