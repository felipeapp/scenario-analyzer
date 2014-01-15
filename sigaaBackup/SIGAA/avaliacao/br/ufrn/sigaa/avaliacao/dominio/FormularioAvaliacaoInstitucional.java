/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/09/2010
 *
 */
package br.ufrn.sigaa.avaliacao.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/** Formulário de Aplicação da Avaliação Institucional.
 * @author Édipo Elder F. Melo
 *
 */
@Entity 
@Table(name="formulario_avaliacao", schema="avaliacao")
public class FormularioAvaliacaoInstitucional implements Validatable, TipoAvaliacaoInstitucional {

	/** Chave primária. */
	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="avaliacao.avaliacao_seq") })
	@Column(name = "id_formulario_avaliacao")
	private int id;
	
	/** Título do formulário. */
	private String titulo;
	
	/** Grupo de perguntas a serem aplicadas. */
	@ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="grupo_perguntas_formulario_avaliacao", schema="avaliacao",
			joinColumns=@JoinColumn(name="id_formulario_avaliacao"),  
			inverseJoinColumns=@JoinColumn(name="id_grupo_pergunta"))
	private List<GrupoPerguntas> grupoPerguntas;
	
	/** Tipo de Avaliação. Por exemplo: Avaliação da Docência Assistida. */
	@Column(name="tipo_avaliacao")
	private int tipoAvaliacao = INDEFINIDO;
	
	/** Indica se a Avaliação será para o Ensino à Distância. */
	private boolean ead;
	
	/** Indica se as turmas serão agrupadas por disciplina. */
	@Column(name = "agrupa_turmas")
	private boolean agrupaTurmas;
	
	/** Instruções gerais que serão informadas ao usuário, no início do formulário da Avaliação Institucional. */
	@Column(name = "instrucoes_gerais")
	private String instrucoesGerais;
	
	/** Construtor padrão. */
	public FormularioAvaliacaoInstitucional() {
		grupoPerguntas = new ArrayList<GrupoPerguntas>();
	}
	
	/** Construtor parametrizado. */
	public FormularioAvaliacaoInstitucional(int id) {
		this();
		this.id = id;
	}
	
	/** Retorna a chave primária. 
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}
	/** Seta a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}
	/** Retorna o tipo de Avaliação. Por exemplo: Avaliação da Docência Assistida. 
	 * @return
	 */
	public int getTipoAvaliacao() {
		return tipoAvaliacao;
	}
	/** Seta o tipo de Avaliação. Por exemplo: Avaliação da Docência Assistida.
	 * @param tipoAvaliacao
	 */
	public void setTipoAvaliacao(int tipoAvaliacao) {
		this.tipoAvaliacao = tipoAvaliacao;
	}
	/** Retorna o grupo de perguntas do discente.
	 * @return
	 */
	public Collection<GrupoPerguntas> getGrupoPerguntasDiscente() {
		Collection<GrupoPerguntas> subGrupo = new ArrayList<GrupoPerguntas>();
		for (GrupoPerguntas grupo : grupoPerguntas) 
			if (grupo.isDiscente())
				subGrupo.add(grupo);
		return subGrupo;
	}
	/** Retorna o grupo de perguntas do docente.
	 * @return
	 */
	public Collection<GrupoPerguntas> getGrupoPerguntasDocente() {
		Collection<GrupoPerguntas> subGrupo = new ArrayList<GrupoPerguntas>();
		for (GrupoPerguntas grupo : grupoPerguntas) 
			if (!grupo.isDiscente())
				subGrupo.add(grupo);
		return subGrupo;
	}
	/** Indica se o formulário é de avaliação da docência assistida.
	 * @return
	 */
	public boolean isAvaliacaoDocenciaAssistida() {
		return tipoAvaliacao == TipoAvaliacaoInstitucional.AVALIACAO_DOCENCIA_ASSISTIDA;
	}
	/** Indica se o formulário será preenchido pelo discente.
	 * @return
	 */
	public boolean isAvaliacaoDiscente() {
		return tipoAvaliacao == TipoAvaliacaoInstitucional.AVALIACAO_DISCENTE_GRADUACAO;
	}
	/** Retorna o título do formulário. 
	 * @return
	 */
	public String getTitulo() {
		return titulo;
	}
	/** Seta o título do formulário.
	 * @param titulo
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	/** Valida os dados para persistência: tipo de avaliação, título, grupo de perguntas.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequiredId(tipoAvaliacao, "Tipo de Avaliação", lista);
		validateRequired(titulo, "Título", lista);
		validateRequired(grupoPerguntas, "Grupo de Perguntas", lista);
		return lista;
	}
	/** Retorna o grupo de perguntas a serem aplicadas. 
	 * @return
	 */
	public List<GrupoPerguntas> getGrupoPerguntas() {
		return grupoPerguntas;
	}
	/** Seta o grupo de perguntas a serem aplicadas.
	 * @param grupoPerguntas
	 */
	public void setGrupoPerguntas(List<GrupoPerguntas> grupoPerguntas) {
		this.grupoPerguntas = grupoPerguntas;
	}
	
	/** Compara este objeto com outro, comparando se o ID é igual.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	/** Retorna um código hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
	/** Retorna uma descrição textual deste objeto.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.titulo;
	}
	
	/** Adiciona um grupo de pergunta ao formulário.
	 * @param grupo
	 */
	public void adicionaGrupoPergunta(GrupoPerguntas grupo) {
		if (grupoPerguntas == null)
			grupoPerguntas = new ArrayList<GrupoPerguntas>();
		grupoPerguntas.add(grupo);
	}
	
	/** Retorna uma descrição textual do perfil do usário que preencherá a Avaliação Institucional.
	 * @return
	 */
	public String getDescricaoTipoAvaliacao() {
		switch (tipoAvaliacao) {
		case AVALIACAO_DISCENTE_GRADUACAO: return "DISCENTE DE GRADUAÇÃO";
		case AVALIACAO_DOCENCIA_ASSISTIDA: return "DOCÊNCIA ASSISTIDA";
		case AVALIACAO_DOCENTE_GRADUACAO: return "DOCENTE DE GRADUAÇÃO";
		default:
			return null;
		}
	}

	public boolean isEad() {
		return ead;
	}

	public void setEad(boolean ead) {
		this.ead = ead;
	}

	public boolean isAgrupaTurmas() {
		return agrupaTurmas;
	}

	public void setAgrupaTurmas(boolean agrupaTurmas) {
		this.agrupaTurmas = agrupaTurmas;
	}

	public String getInstrucoesGerais() {
		return instrucoesGerais;
	}

	public void setInstrucoesGerais(String instrucoesGerais) {
		this.instrucoesGerais = instrucoesGerais;
	}

}
