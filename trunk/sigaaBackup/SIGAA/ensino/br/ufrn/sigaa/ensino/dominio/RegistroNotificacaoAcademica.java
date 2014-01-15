package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe que efetua o registro de visualizações de notificações acadêmicas
 * 
 * @author Diego Jácome
 *
 */
@Entity
@Table(name = "registro_notificacao_academica", schema = "ensino", uniqueConstraints = {})
public class RegistroNotificacaoAcademica implements PersistDB {

	/**Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="ensino.registro_notificacao_academica_sequence") })
	@Column(name = "id_registro_notificacao_academica", unique = true, nullable = false)
	private int id;
	
	/**Notificação acadêmica do discente que foi visualizada. */
	@ManyToOne
	@JoinColumn(name = "id_notificacao_academica_discente")
	private NotificacaoAcademicaDiscente notificacaoDiscente;
	
	/** Discente que visualizou a notificação */
	@ManyToOne
	@JoinColumn(name = "id_discente")
	private Discente discente;
	
	/** Registro de entrada da visualização */
	@ManyToOne
	@JoinColumn(name = "id_registro_entrada_visualizacao")
	private RegistroEntrada registroEntrada;
	
	/** Data de visualização da notificação */
	@Column(name="data_visualizacao")
	private Date dataVisualizacao;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public NotificacaoAcademicaDiscente getNotificacaoDiscente() {
		return notificacaoDiscente;
	}

	public void setNotificacaoDiscente(
			NotificacaoAcademicaDiscente notificacaoDiscente) {
		this.notificacaoDiscente = notificacaoDiscente;
	}
	
	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setDataVisualizacao(Date dataVisualizacao) {
		this.dataVisualizacao = dataVisualizacao;
	}

	public Date getDataVisualizacao() {
		return dataVisualizacao;
	}
	
}
