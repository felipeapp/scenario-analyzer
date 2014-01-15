/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 * Created 25/06/2007
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import java.util.ArrayList;

/**
 * Classe para armazenar as constantes dos motivos de mobilidade interna 
 * @author leonardo
 *
 */
public class MotivoMobilidadeInterna {

	// constantes
	public static final int ESTAGIO = 1;
	public static final int TREINAMENTO = 2;
	public static final int TRANSFERIDO_TEMPORARIAMENTE = 3;
	public static final int A_DISPOSICAO_OUTROS_ORGAOS = 4;
	public static final int DEPENDENTE_SERVIDOR_PUBLICO = 5;
	
	private int id;
	private String descricao;
	
	public MotivoMobilidadeInterna(){
		
	}
	
	public MotivoMobilidadeInterna(int id, String descricao){
		this.id = id;
		this.descricao = descricao;
	}
	
	public static ArrayList<MotivoMobilidadeInterna> getAllMotivos(){
		ArrayList<MotivoMobilidadeInterna> lista = new ArrayList<MotivoMobilidadeInterna>();
		
		lista.add(new MotivoMobilidadeInterna(1, "Est�gio"));
		lista.add(new MotivoMobilidadeInterna(2, "Treinamento"));
		lista.add(new MotivoMobilidadeInterna(3, "Transferido temporariamente"));
		lista.add(new MotivoMobilidadeInterna(4, "� disposi��o de outros �rg�os"));
		lista.add(new MotivoMobilidadeInterna(5, "Dependente de Servidor P�blico"));
		
		return lista;
	}
	
	public static String getDescricao(int tipo){
		switch (tipo) {
		case 1:
			return "Est�gio";
		case 2:
			return "Treinamento";
		case 3:
			return "Transferido temporariamente";
		case 4:
			return "� disposi��o de outros �rg�os";
		case 5:
			return "Dependente de Servidor P�blico";
		default:
			return "Desconhecido";
		}
	}
	
	
	
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
}
