package br.ufrn.sigaa.ava.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/**
 * Representa uma denuncia de mensagem em um Forum. Uma mensagem é um tópico ou
 * uma resposta de um tópico de um fórum, usada para comunicação entre os
 * participantes de uma turma virtual ou fórum de curso. Podem criar e
 * visualizar a mensagem qualquer docente ou discente que possua acesso ao fórum
 * ou a turma virtual.
 * 
 * @author Leandro Oliveira
 * 
 */
@Entity
@Table(name = "denuncia_mensagem", schema = "ava")
public class DenunciaMensagem implements Validatable {
	private static final long serialVersionUID = 1L;

	/** Chave primaria */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "hibernate_sequence") })
	@Column(name = "id_denuncia_mensagem", nullable = false)
	private int id;

	/** Data em que a mensagem foi cadastrada **/
	@CriadoEm
	private Date data;

	/** Registro entrada de cadastro da denúncia */
	@CriadoPor
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registro_cadastro")
	private RegistroEntrada registroCadastro;

	/** Guarda o motivo da denúncia **/
	@Column(name = "motivo_denuncia")
	private String motivoDenuncia;

	/** Mensagem associada a denúncia **/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_forum_mensagem")
	private ForumMensagem forumMensagem;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMotivoDenuncia() {
		return motivoDenuncia;
	}

	public void setMotivoDenuncia(String motivoDenuncia) {
		this.motivoDenuncia = motivoDenuncia;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	@Override
	public boolean equals(Object o) {
		return EqualsUtil.testTransientEquals(this, o, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public ForumMensagem getForumMensagem() {
		return forumMensagem;
	}

	public void setForumMensagem(ForumMensagem forumMensagem) {
		this.forumMensagem = forumMensagem;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();

		ValidatorUtil.validateRequired(getMotivoDenuncia(), "Motivo", erros);

		return erros;
	}
}
