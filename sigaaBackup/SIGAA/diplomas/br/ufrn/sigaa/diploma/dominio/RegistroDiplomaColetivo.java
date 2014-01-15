/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
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

/** Classe que modela o conceito de registro de diploma coletivo, ou seja, agrupa v�rios
 *  registros de diplomas de uma turma de cola��o de grau. 
 * @author �dipo Elder F. Melo
 *
 */
@Entity
@Table(schema = "diploma", name = "registro_diploma_coletivo", uniqueConstraints = {})
public class RegistroDiplomaColetivo  implements Validatable  {

	/** Chave prim�ria. */
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
	
	/** N�mero do processo do diploma. */
	private String processo;
	
	/** Data de registro do diploma. */
	@Column(name = "data_registro")
	private Date dataRegistro;
	
	/** Data de expedi��o do diploma. */
	@Column(name = "data_expedicao")
	private Date dataExpedicao;
	
	/** Data de cola��o. */
	@Column(name = "data_colacao")
	private Date dataColacao;
	
	/** Curso da turma que est� registrado. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_curso")
	private Curso curso;
	
	/** Indica se o registro est� aberto para altera��o. */
	private boolean aberto;
	
	/** Livro utilizado no registro de diploma da turma. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_livro_registro_diploma")
	private LivroRegistroDiploma livroRegistroDiploma;
	
	/** Ano de registro da turma de cola��o de grau. */
	private Integer ano;
	
	/** Per�odo de registro da turma de cola��o de grau. */
	private Integer periodo;
	
	/** Polo para o qual foram registrados os diplomas. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_polo")
	private Polo polo;
	
	/** Construtor padr�o. */
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
	 * Retorna a chave prim�ria de Registro Diploma Coletivo
	 * 
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/**
	 * Seta a chave prim�ria de Registro Diploma Coletivo
	 * 
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o n�mero do processo do diploma. 
	 * @return
	 */
	public String getProcesso() {
		return processo;
	}

	/** Seta o n�mero do processo do diploma.
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

	/** Retorna a data de expedi��o do diploma. 
	 * @return
	 */
	public Date getDataExpedicao() {
		return dataExpedicao;
	}

	/** Seta a data de expedi��o do diploma.
	 * @param dataExpedicao
	 */
	public void setDataExpedicao(Date dataExpedicao) {
		this.dataExpedicao = dataExpedicao;
	}

	/** Retorna a data de cola��o. 
	 * @return
	 */
	public Date getDataColacao() {
		return dataColacao;
	}

	/** Seta a data de cola��o.
	 * @param dataColacao
	 */
	public void setDataColacao(Date dataColacao) {
		this.dataColacao = dataColacao;
	}
	
	/** Indica se o registro est� aberto para altera��o. 
	 * @return
	 */
	public boolean isAberto() {
		return aberto;
	}

	/** Seta se o registro est� aberto para altera��o. 
	 * @param aberto
	 */
	public void setAberto(boolean aberto) {
		this.aberto = aberto;
	}
	
	/** Retorna o curso da turma que est� registrado. 
	 * @return
	 */
	public Curso getCurso() {
		return curso;
	}

	/** Seta o curso da turma que est� registrado.
	 * @param curso
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/** Retorna uma cole��o de registros de diplomas que pertencem a este registro de diploma coletivo. 
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

	/** Seta a cole��o de registros de diplomas que pertencem a este registro de diploma coletivo.
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
	 * Valida os atributos: data de cola��o, data de expedi��o, data de
	 * registro, n�mero do processo.
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(ano, "Ano", lista);
		ValidatorUtil.validateRange(periodo, 1, 2, "Per�odo", lista);
		ValidatorUtil.validateRequired(dataColacao, "Data de Cola��o", lista);
		ValidatorUtil.validateRequired(dataExpedicao, "Data de Expedi��o", lista);
		ValidatorUtil.validateRequired(dataRegistro, "Data de Regsitro", lista);
		ValidatorUtil.validateRequired(processo, "N�mero do Processo", lista);
		return lista;
	}

	/**
	 * Retorna uma representa��o textual do registro do diploma coletivo no
	 * formato: "Processo N�" seguido do n�mero do protocolo completo.
	 */
	@Override
	public String toString() {
		return "Processo N� " + processo; 
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
