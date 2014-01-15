/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Created on 14/09/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ava.dominio.DominioTurmaVirtual;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Representa um Forum de discussão em uma determinada turma/serie de Ensino médio.
 *
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name = "forum_medio", schema = "medio")
@HumanName(value="Fórum", genero='M')
public class ForumMedio implements DominioTurmaVirtual {

	public static final short TURMA = 1;

	public static final short MURAL = 2;

	public static final short PESQUISA = 3;
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_forum_medio", nullable = false)
	private int id;
	
	private Boolean ativo;

	private String descricao;
	
	private String titulo;

	private short tipo;
	
	private boolean topicos;
	
	private Character nivel;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_turma_serie")
	private TurmaSerie turmaSerie;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turma")
	private Turma turma;

	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	@CriadoEm
	@Column(name = "data_criacao")
	private Date data;

	@OneToMany(mappedBy = "forum", fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	private List<ForumMensagemMedio> mensagens = new ArrayList<ForumMensagemMedio>(0);

	@Transient
	private int totalTopicos;

	@Transient
	private Date dataUltimaMensagem;
	

	public ForumMedio() {
	}
	
	public String getNivelDescricao() {
		return NivelEnsino.getDescricao(nivel);
	}
	
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
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
	public Boolean getAtivo() { 
		return this.ativo; 
	}
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo; 
	}
	public List<ForumMensagemMedio> getMensagens() {
		return mensagens;
	}
	public void setMensagens(List<ForumMensagemMedio> mensagens) {
		this.mensagens = mensagens;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public Turma getTurma() {
		return turma;
	}
	public void setTurma(Turma turma) {
		this.turma = turma;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		if (titulo == null || "".equals(titulo.trim()))
			lista.addErro("O título do fórum não pode ficar em branco.");
		if (descricao == null || "".equals(descricao.trim()))
			lista.addErro("A descrição do fórum não pode ficar em branco.");

		if (titulo != null && !"".equals(titulo.trim()) && titulo.length() < 3)
			lista.addErro("O título deve conter ao menos três caracteres");
		if (descricao != null && !"".equals(descricao.trim()) && descricao.length() < 3)
			lista.addErro("A descrição deve conter ao menos três caracteres");

		return lista;
	}

	public Date getDataUltimaMensagem() {
		return dataUltimaMensagem;
	}
	public int getTotalTopicos() {
		return totalTopicos;
	}
	public void setTotalTopicos(int totalTopicos) {
		this.totalTopicos = totalTopicos;
	}
	public void setDataUltimaMensagem(Date dataUltimaMensagem) {
		this.dataUltimaMensagem = dataUltimaMensagem;
	}
	public short getTipo() {
		return tipo;
	}
	public void setTipo(short tipo) {
		this.tipo = tipo;
	}
	public boolean isTopicos() {
		return topicos;
	}
	public void setTopicos(boolean topicos) {
		this.topicos = topicos;
	}

	public boolean isMural() {
		return this.tipo == MURAL;
	}

	public String getMensagemAtividade() {
		return "Novo fórum cadastrado";
	}

	public Character getNivel() {
		return nivel;
	}

	public void setNivel(Character nivel) {
		this.nivel = nivel;
	}

	public TurmaSerie getTurmaSerie() {
		return turmaSerie;
	}

	public void setTurmaSerie(TurmaSerie turmaSerie) {
		this.turmaSerie = turmaSerie;
	}
	
}