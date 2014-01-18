/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Interface que lista as entidades nas quais os usu�rios da Turma virtual realizam a��es. 
 * 
 * @author Fred_Castro
 * 
 */
public enum EntidadeRegistroAva {
	
	/** Turma. */
	TURMA (1, "Turma", Turma.class),
	
	/** T�pico de Aula. */
	TOPICO_AULA (2, "T�pico de Aula", TopicoAula.class),
	
	/** Arquivo. */
	ARQUIVO (3, "Arquivo", ArquivoUsuario.class),
	
	/** Tarefa. */
	TAREFA (4, "Tarefa", TarefaTurma.class),
	
	/** Indica��o de Refer�ncia. */
	INDICACAO_REFERENCIA (5, "Indicaca��o de Refer�ncia", IndicacaoReferencia.class),
	
	/** F�rum. */
	FORUM (6, "F�rum", ForumTurma.class),
	
	/** T�pico do F�rum. */
	FORUM_MENSAGEM (7, "T�pico F�rum", ForumMensagem.class),
	
	/** Programa do componente curricular .*/
	PROGRAMA_COMPONENTE_CURRICULAR (8, "Programa", ComponenteCurricular.class),
	
	/** Not�cia. */
	NOTICIA (9, "Not�cia", NoticiaTurma.class),
	
	/** Aula Extra. */
	AULA_EXTRA (10, "Aula Extra", AulaExtra.class),
	
	/** Conte�do. */
	CONTEUDO (11, "Conte�do", ConteudoTurma.class),
	
	/** Enquete. */
	ENQUETE (12, "Enquete", Enquete.class),
	
	/** Notas. */
	NOTAS (13, "Notas", Turma.class),
	
	/** Frequ�ncia. */
	FREQUENCIA (14, "Frequ�ncia", Turma.class),
	
	/** Avalia��o. */
	AVALIACAO (15, "Avalia��o", DataAvaliacao.class),
	
	/** Participantes. */
	PARTICIPANTES (16, "Participantes", Turma.class),
	
	/** Perfil. */
	PERFIL (17, "Perfil", PerfilUsuarioAva.class),
	
	/** V�deo.*/
	VIDEO (18, "V�deo", VideoTurma.class),
	
	/** Question�rio. */
	QUESTIONARIO (19, "Question�rio", QuestionarioTurma.class),
	
	/** R�tulo. */
	ROTULO (20, "R�tulo", RotuloTurma.class),

	/** Chat. */
	CHAT (21, "Chat", ChatTurma.class),

	/** Plano de Curso. */
	PLANO_CURSO (22, "Plano de Curso",Turma.class),

	/** Grupos. */
	GRUPO (23, "Grupos",Turma.class),
	
	/** Resposta de Tarefa. */
	RESPOSTA_TAREFA (24, "Resposta da Tarefa",TarefaTurma.class),
	
	/** Resposta de Question�rio.. */
	RESPOSTA_QUESTIONARIO (25, "Resposta do Question�rio",QuestionarioTurma.class),
		
	/** Twitter. */
	TWITTER (26, "Twitter",TurmaTwitter.class),
	
	/** Alunos Trancados. */
	ALUNOS_TRANCADOS (27, "Alunos Trancados",Turma.class),
	
	/** Aula de Ensino Individual. */
	AULA_ENSINO_INDIVIDUAL (28, "Aula Ensino Individual", AulaExtra.class),
	
	/** Permiss�o. */
	PERMISSAO_AVA (30, "Permiss�o", PermissaoAva.class);
	
	/** Valor da entidade. */
	private int valor;
	
	/** Descri��o da entidade. */
	private String descricao;
	
	/** Descri��o da classe cujo o id ser� persistido. */
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