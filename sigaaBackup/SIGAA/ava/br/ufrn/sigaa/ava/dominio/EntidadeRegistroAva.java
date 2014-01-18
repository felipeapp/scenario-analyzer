/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 18/08/2010
 */
package br.ufrn.sigaa.ava.dominio;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.ava.forum.relacionamentos.dominio.ForumTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * 
 * Interface que lista as entidades nas quais os usuários da Turma virtual realizam ações. 
 * 
 * @author Fred_Castro
 * 
 */
public enum EntidadeRegistroAva {
	
	/** Turma. */
	TURMA (1, "Turma", Turma.class),
	
	/** Tópico de Aula. */
	TOPICO_AULA (2, "Tópico de Aula", TopicoAula.class),
	
	/** Arquivo. */
	ARQUIVO (3, "Arquivo", ArquivoUsuario.class),
	
	/** Tarefa. */
	TAREFA (4, "Tarefa", TarefaTurma.class),
	
	/** Indicação de Referência. */
	INDICACAO_REFERENCIA (5, "Indicacação de Referência", IndicacaoReferencia.class),
	
	/** Fórum. */
	FORUM (6, "Fórum", ForumTurma.class),
	
	/** Tópico do Fórum. */
	FORUM_MENSAGEM (7, "Tópico Fórum", ForumMensagem.class),
	
	/** Programa do componente curricular .*/
	PROGRAMA_COMPONENTE_CURRICULAR (8, "Programa", ComponenteCurricular.class),
	
	/** Notícia. */
	NOTICIA (9, "Notícia", NoticiaTurma.class),
	
	/** Aula Extra. */
	AULA_EXTRA (10, "Aula Extra", AulaExtra.class),
	
	/** Conteúdo. */
	CONTEUDO (11, "Conteúdo", ConteudoTurma.class),
	
	/** Enquete. */
	ENQUETE (12, "Enquete", Enquete.class),
	
	/** Notas. */
	NOTAS (13, "Notas", Turma.class),
	
	/** Frequência. */
	FREQUENCIA (14, "Frequência", Turma.class),
	
	/** Avaliação. */
	AVALIACAO (15, "Avaliação", DataAvaliacao.class),
	
	/** Participantes. */
	PARTICIPANTES (16, "Participantes", Turma.class),
	
	/** Perfil. */
	PERFIL (17, "Perfil", PerfilUsuarioAva.class),
	
	/** Vídeo.*/
	VIDEO (18, "Vídeo", VideoTurma.class),
	
	/** Questionário. */
	QUESTIONARIO (19, "Questionário", QuestionarioTurma.class),
	
	/** Rótulo. */
	ROTULO (20, "Rótulo", RotuloTurma.class),

	/** Chat. */
	CHAT (21, "Chat", ChatTurma.class),

	/** Plano de Curso. */
	PLANO_CURSO (22, "Plano de Curso",Turma.class),

	/** Grupos. */
	GRUPO (23, "Grupos",Turma.class),
	
	/** Resposta de Tarefa. */
	RESPOSTA_TAREFA (24, "Resposta da Tarefa",TarefaTurma.class),
	
	/** Resposta de Questionário.. */
	RESPOSTA_QUESTIONARIO (25, "Resposta do Questionário",QuestionarioTurma.class),
		
	/** Twitter. */
	TWITTER (26, "Twitter",TurmaTwitter.class),
	
	/** Alunos Trancados. */
	ALUNOS_TRANCADOS (27, "Alunos Trancados",Turma.class),
	
	/** Aula de Ensino Individual. */
	AULA_ENSINO_INDIVIDUAL (28, "Aula Ensino Individual", AulaExtra.class),
	
	/** Permissão. */
	PERMISSAO_AVA (30, "Permissão", PermissaoAva.class);
	
	/** Valor da entidade. */
	private int valor;
	
	/** Descrição da entidade. */
	private String descricao;
	
	/** Descrição da classe cujo o id será persistido. */
	private Class<? extends PersistDB> classe;
	
	/** Construtor. */
	EntidadeRegistroAva (int valor, String descricao, Class<? extends PersistDB> classe){
		this.valor = valor;
		this.descricao = descricao;
		this.classe = classe;
	}
	
	/** Retorna uma entidade completa com base no valor informado. */
	public static EntidadeRegistroAva getEntidade (int entidade){
		for (EntidadeRegistroAva e : EntidadeRegistroAva.values())
			if (e.getValor() == entidade)
				return e;
		return null;
	}

	public int getValor() {
		return valor;
	}

	public void setValor(int valor) {
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setClasse(Class<? extends PersistDB> classe) {
		this.classe = classe;
	}

	public Class<? extends PersistDB> getClasse() {
		return classe;
	}
	
}