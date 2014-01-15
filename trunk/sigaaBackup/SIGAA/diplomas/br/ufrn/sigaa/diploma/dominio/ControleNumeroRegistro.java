/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '26/01/2007'
 *
 */
package br.ufrn.sigaa.diploma.dominio;

import java.text.SimpleDateFormat;
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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;

/** Classe de controle do uso de números de registro de diplomas.
 * @author Édipo Elder F. Melo
 *
 */
@Entity
@Table(schema = "diploma", name = "controle_numero_registro")
public class ControleNumeroRegistro implements PersistDB {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
    @Column(name = "id_controle_numero_registro")
    private int id;
	
	/** Número de Registro de Diploma Utilizado. */
	@Column(name = "numero_registro")
	private int numeroRegistro;
	
	/** Data/hora da solicitação do número. */
	@Column(name = "solicitado_em")
	private Date solicitadoEm;
	
	/** Registro de entrada do usuário que solicitou o número.*/
	@ManyToOne
	@JoinColumn(name = "id_entrada")
	private RegistroEntrada registroEntrada;
	
	/** Indica se o número foi utilizado para registrar um diploma emitido por outra instituição. */
	@Column(name = "registro_externo")
	private Boolean registroExterno;
	
	/** Indica se o número de registro de diploma foi utilizado em um registro anterior ao registro no SIGAA. */
	@Column(name = "registro_antigo")
	private Boolean registroAntigo;

	/** Nível de ensino para o qual o número de registro foi utilizado. */
	private char nivel;
	
	/** Livro o qual foi usado o número de registro. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_livro_registro_diploma")
	private LivroRegistroDiploma livro;

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	public Date getSolicitadoEm() {
		return solicitadoEm;
	}

	public void setSolicitadoEm(Date solicitadoEm) {
		this.solicitadoEm = solicitadoEm;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Boolean getRegistroExterno() {
		return registroExterno;
	}

	public void setRegistroExterno(Boolean registroExterno) {
		this.registroExterno = registroExterno;
	}

	public Boolean getRegistroAntigo() {
		return registroAntigo;
	}

	public void setRegistroAntigo(Boolean registroAntigo) {
		this.registroAntigo = registroAntigo;
	}
	
	public int getNumeroRegistro() {
		return numeroRegistro;
	}
	
	public void setNumeroRegistro(int numeroRegistro) {
		this.numeroRegistro = numeroRegistro;
	}
	
	/**
	 * Retorna uma representação textual da requisição do número de registro de diploma no
	 * seguinte formato: "Nº", seguido do número do registro de diploma, seguido de
	 * vírgula, seguido do nome do usuário que requisitou, seguido de vírgula,
	 * seguido da data e hora da requisição.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return "Nº " + id + ", "
				+ registroEntrada.getUsuario().getNome() + ", " 
				+ dateFormatter.format(solicitadoEm);
	}

	public char getNivel() {
		return nivel;
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	public LivroRegistroDiploma getLivro() {
		return livro;
	}

	public void setLivro(LivroRegistroDiploma livro) {
		this.livro = livro;
	}
}
