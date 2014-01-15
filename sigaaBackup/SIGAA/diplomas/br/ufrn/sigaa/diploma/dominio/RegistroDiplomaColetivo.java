/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '25/05/2009'
 *
 */
package br.ufrn.sigaa.diploma.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.Polo;

/** Classe que modela o conceito de registro de diploma coletivo, ou seja, agrupa vários
 *  registros de diplomas de uma turma de colação de grau. 
 * @author Édipo Elder F. Melo
 *
 */
@Entity
@Table(schema = "diploma", name = "registro_diploma_coletivo", uniqueConstraints = {})
public class RegistroDiplomaColetivo  implements Validatable  {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_registro_diploma_coletivo")
	private int id;
	
	/** Registros de diplomas que pertencem a este registro de diploma coletivo. */
	@OneToMany(fetch = FetchType.LAZY, mappedBy="registroDiplomaColetivo")
	@OrderBy("numeroRegistro")
	private Collection<RegistroDiploma> registrosDiplomas;
	
	/** Número do processo do diploma. */
	private String processo;
	
	/** Data de registro do diploma. */
	@Column(name = "data_registro")
	private Date dataRegistro;
	
	/** Data de expedição do diploma. */
	@Column(name = "data_expedicao")
	private Date dataExpedicao;
	
	/** Data de colação. */
	@Column(name = "data_colacao")
	private Date dataColacao;
	
	/** Curso da turma que está registrado. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_curso")
	private Curso curso;
	
	/** Indica se o registro está aberto para alteração. */
	private boolean aberto;
	
	/** Livro utilizado no registro de diploma da turma. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_livro_registro_diploma")
	private LivroRegistroDiploma livroRegistroDiploma;
	
	/** Ano de registro da turma de colação de grau. */
	private Integer ano;
	
	/** Período de registro da turma de colação de grau. */
	private Integer periodo;
	
	/** Polo para o qual foram registrados os diplomas. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_polo")
	private Polo polo;
	
	/** Construtor padrão. */
	public RegistroDiplomaColetivo() {
	}
	
	/** Construtor parametrizado
	 * @param id
	 */
	public RegistroDiplomaColetivo(int id) {
		super();
		this.id = id;
	}

	/**
	 * Retorna a chave primária de Registro Diploma Coletivo
	 * 
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/**
	 * Seta a chave primária de Registro Diploma Coletivo
	 * 
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o número do processo do diploma. 
	 * @return
	 */
	public String getProcesso() {
		return processo;
	}

	/** Seta o número do processo do diploma.
	 * @param processo
	 */
	public void setProcesso(String processo) {
		this.processo = processo;
	}

	/** Retorna a data de registro do diploma. 
	 * @return
	 */
	public Date getDataRegistro() {
		return dataRegistro;
	}

	/** Seta a data de registro do diploma.
	 * @param dataRegistro
	 */
	public void setDataRegistro(Date dataRegistro) {
		this.dataRegistro = dataRegistro;
	}

	/** Retorna a data de expedição do diploma. 
	 * @return
	 */
	public Date getDataExpedicao() {
		return dataExpedicao;
	}

	/** Seta a data de expedição do diploma.
	 * @param dataExpedicao
	 */
	public void setDataExpedicao(Date dataExpedicao) {
		this.dataExpedicao = dataExpedicao;
	}

	/** Retorna a data de colação. 
	 * @return
	 */
	public Date getDataColacao() {
		return dataColacao;
	}

	/** Seta a data de colação.
	 * @param dataColacao
	 */
	public void setDataColacao(Date dataColacao) {
		this.dataColacao = dataColacao;
	}
	
	/** Indica se o registro está aberto para alteração. 
	 * @return
	 */
	public boolean isAberto() {
		return aberto;
	}

	/** Seta se o registro está aberto para alteração. 
	 * @param aberto
	 */
	public void setAberto(boolean aberto) {
		this.aberto = aberto;
	}
	
	/** Retorna o curso da turma que está registrado. 
	 * @return
	 */
	public Curso getCurso() {
		return curso;
	}

	/** Seta o curso da turma que está registrado.
	 * @param curso
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/** Retorna uma coleção de registros de diplomas que pertencem a este registro de diploma coletivo. 
	 * @return
	 */
	public Collection<RegistroDiploma> getRegistrosDiplomas() {
		if (registrosDiplomas != null) {
			Collection<RegistroDiploma> ativos = new ArrayList<RegistroDiploma>();
			for (RegistroDiploma registro : registrosDiplomas)
				if (registro.isAtivo())
					ativos.add(registro);
			registrosDiplomas = ativos;
		} 
		return registrosDiplomas;
	}

	/** Seta a coleção de registros de diplomas que pertencem a este registro de diploma coletivo.
	 * @param registrosDiplomas
	 */
	public void setRegistrosDiplomas(Collection<RegistroDiploma> registrosDiplomas) {
		this.registrosDiplomas = registrosDiplomas;
	}

	/** Retorna o livro utilizado no registro de diploma da turma. 
	 * @return
	 */
	public LivroRegistroDiploma getLivroRegistroDiploma() {
		return livroRegistroDiploma;
	}

	/** Seta o livro utilizado no registro de diploma da turma.
	 * @param livroRegistroDiploma
	 */
	public void setLivroRegistroDiploma(LivroRegistroDiploma livroRegistroDiploma) {
		this.livroRegistroDiploma = livroRegistroDiploma;
	}
	
	/**
	 * Valida os atributos: data de colação, data de expedição, data de
	 * registro, número do processo.
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(ano, "Ano", lista);
		ValidatorUtil.validateRange(periodo, 1, 2, "Período", lista);
		ValidatorUtil.validateRequired(dataColacao, "Data de Colação", lista);
		ValidatorUtil.validateRequired(dataExpedicao, "Data de Expedição", lista);
		ValidatorUtil.validateRequired(dataRegistro, "Data de Regsitro", lista);
		ValidatorUtil.validateRequired(processo, "Número do Processo", lista);
		return lista;
	}

	/**
	 * Retorna uma representação textual do registro do diploma coletivo no
	 * formato: "Processo Nº" seguido do número do protocolo completo.
	 */
	@Override
	public String toString() {
		return "Processo Nº " + processo; 
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Polo getPolo() {
		return polo;
	}

	public void setPolo(Polo polo) {
		this.polo = polo;
	}
}
