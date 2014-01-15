/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.dominio;

import java.util.HashMap;
import java.util.Map;

/**
 * Armazena os tipos de ícones usados na turma virtual agrupados
 * por tipo de arquivo
 *
 * @author David Pereira
 *
 */
public class RepositorioIcones {

	public static final Integer TAREFA = 1;

	public static final Integer CONTEUDO = 2;

	public static final Integer INDICACAO = 3;

	public static final Integer ARQUIVO_DOC = 4;

	public static final Integer ARQUIVO_PDF = 5;

	public static final Integer ARQUIVO_XLS = 6;

	public static final Integer ARQUIVO_PPT = 7;

	public static final Integer ARQUIVO_ZIP = 8;

	public static final Integer ARQUIVO_HTML = 9;

	public static final Integer ARQUIVO_XML = 10;

	public static final Integer ARQUIVO_IMAGEM = 11;

	public static final Integer ARQUIVO_TXT = 12;

	public static final Integer DESCONHECIDO = 13;

	public static final Integer FILME = 14;
	
	public static final Integer QUESTIONARIO = 15;
	
	public static final Integer FORUM = 16;
	
	public static final Integer CHAT = 17;
	
	public static final Integer ENQUETE = 18;
	
	public static final Integer VIDEO = 19;	

	private static Map<Integer, String> iconesRecursos = new HashMap<Integer, String>();

	private static Map<String, String> arquivos = new HashMap<String, String>();
	
	static {
		iconesRecursos.put(TAREFA, "/img/porta_arquivos/icones/tarefa.png");
		iconesRecursos.put(CONTEUDO, "/img/porta_arquivos/icones/conteudo.png");
		iconesRecursos.put(INDICACAO, "/img/portal_turma/site_add.png");
		iconesRecursos.put(DESCONHECIDO, "/img/porta_arquivos/icones/desconhecido.png");
		iconesRecursos.put(QUESTIONARIO, "/ava/img/questionario.png");
		iconesRecursos.put(FORUM, "/ava/img/forumava.png");		
		iconesRecursos.put(CHAT, "/ava/img/user_comment.png");
		iconesRecursos.put(ENQUETE, "/ava/img/enquete.png");
		iconesRecursos.put(VIDEO, "/img/portal_turma/video.png");
		
		
		
		arquivos.put("doc", "/img/porta_arquivos/icones/doc.png");
		arquivos.put("docx", "/img/porta_arquivos/icones/doc.png");
		arquivos.put("pdf", "/img/porta_arquivos/icones/pdf.png");
		arquivos.put("xls", "/img/porta_arquivos/icones/xls.png");
		arquivos.put("xlsx", "/img/porta_arquivos/icones/xls.png");
		arquivos.put("ppt", "/img/porta_arquivos/icones/ppt.png");
		arquivos.put("pptx", "/img/porta_arquivos/icones/ppt.png");
		arquivos.put("zip", "/img/porta_arquivos/icones/zip.png");
		arquivos.put("rar", "/img/porta_arquivos/icones/zip.png");
				
		arquivos.put("html", "/img/porta_arquivos/icones/html.png");
		arquivos.put("htm", "/img/porta_arquivos/icones/html.png");
		arquivos.put("xml", "/img/porta_arquivos/icones/xml.png");

		arquivos.put("bmp", "/img/porta_arquivos/icones/imagem.png");
		arquivos.put("jpg", "/img/porta_arquivos/icones/imagem.png");
		arquivos.put("gif", "/img/porta_arquivos/icones/imagem.png");
		arquivos.put("png", "/img/porta_arquivos/icones/imagem.png");
		
		arquivos.put("txt", "/img/porta_arquivos/icones/txt.png");
		
		arquivos.put("mov", "/img/porta_arquivos/icones/film.png");
		arquivos.put("wmv", "/img/porta_arquivos/icones/film.png");
		arquivos.put("avi", "/img/porta_arquivos/icones/film.png");
		arquivos.put("fla", "/img/porta_arquivos/icones/film.png");
		arquivos.put("mpeg", "/img/porta_arquivos/icones/film.png");
		
	}

	public static String getRecurso(int tipo) {
		return iconesRecursos.get(tipo);
	}
	
	public static String getArquivo(String extensao) {
		return arquivos.get(extensao);
	}

}