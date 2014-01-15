/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '20/11/2007'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.dominio;

import java.util.Date;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Entidade que registra as informa��es referentes a orienta��es de resid�ncias m�dicas 
 * por docentes da institui��o
 * 
 * @author Ricardo Wendell
 *
 */
@Entity
@Table(name = "residencia_medica",schema="prodocente")
public class CHDedicadaResidenciaMedica implements Validatable, ViewAtividadeBuilder {

	/** Constante que define o ano incial de cadastro de CH para resid�ncia m�dica. */
	private static final int ANO_INICIAL_CADASTRO_CH = 1950;

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Servidor que dedica a carga hor�ria � resid�ncia m�dica. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_servidor", unique = false, nullable = false, insertable = true, updatable = true)
	private Servidor servidor;

	/** Programa para o qual o servidor dedicar� a carga hor�ria. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_programa_residencia_medica", unique = false, nullable = false, insertable = true, updatable = true)
	private ProgramaResidenciaMedica programaResidenciaMedica;

	/** Ano em que o servidor dedicar� a carga hor�ria. */
	private int ano;

	/** Semestre em que o servidor dedicar� a carga hor�ria. */
	private int semestre;

	/** Carga hor�ria semanal dedicada pelo servidor � resid�ncia m�dica. */
	@Column(name = "ch_semanal")
	private Integer chSemanal;

	/** Observa��es sobre a carga hor�ria dedicada. */
	private String observacoes;

	/** Data de cria��o do registro. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	private Date criadoEm;

	/** Registro de entrada do usu�rio que cadastrou. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_usuario", unique = false, nullable = false, insertable = true, updatable = true)
	private Usuario criadoPor;

	/** Indica se est� ativo ou n�o. */
	private boolean ativo = true;

	public CHDedicadaResidenciaMedica() {
		servidor = new Servidor();
	}

	public CHDedicadaResidenciaMedica(int id) {
		this.id = id;
	}

	public int getAno() {
		return this.ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Usuario getCriadoPor() {
		return this.criadoPor;
	}

	public void setCriadoPor(Usuario criadoPor) {
		this.criadoPor = criadoPor;
	}

	public Integer getChSemanal() {
		return this.chSemanal;
	}

	public void setChSemanal(Integer horas) {
		this.chSemanal = horas;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getObservacoes() {
		return this.observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public ProgramaResidenciaMedica getProgramaResidenciaMedica() {
		return this.programaResidenciaMedica;
	}

	public void setProgramaResidenciaMedica(
			ProgramaResidenciaMedica programaResidenciaMedica) {
		this.programaResidenciaMedica = programaResidenciaMedica;
	}

	public int getSemestre() {
		return this.semestre;
	}

	public void setSemestre(int semestre) {
		this.semestre = semestre;
	}

	public Servidor getServidor() {
		return this.servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public boolean isAtivo() {
		return this.ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequiredId(getProgramaResidenciaMedica().getId(), "Programa", lista);
		ValidatorUtil.validateMinValue(getAno(), ANO_INICIAL_CADASTRO_CH, "Ano", lista);
		ValidatorUtil.validateRequired(getSemestre(), "Semestre", lista);
		ValidatorUtil.validateRequired(getChSemanal(), "Carga Hor�ria Semanal", lista);
		ValidatorUtil.validateRequiredId(servidor.getId(), "Servidor", lista);
		return lista;
	}

	@Override
	public String getItemView() {
		 return "<td>" + programaResidenciaMedica.getDescricao() + "</td>" 
				 	+ "<td nowrap style=\"text-align:center\">"	+ ano + "." + semestre + "</td>" 
					+ "<td style=\"text-align:center\">" + chSemanal + "</td>";
	}

	@Override
	public String getTituloView() {
		return "<td>Programa</td><td style=\"text-align:center\">Ano-per�odo</td><td style=\"text-align:center\">CH Semanal</td>";
	}

	@Override
	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("programaResidenciaMedica", null);
		itens.put("ano", null);
		itens.put("semestre", null);
		itens.put("chSemanal", null);
		return itens;
	}

	@Override
	public float getQtdBase() {
		return 0;
	}

}
