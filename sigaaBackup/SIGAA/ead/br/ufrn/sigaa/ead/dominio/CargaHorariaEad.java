/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 14/06/2011
 *
 */
package br.ufrn.sigaa.ead.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRange;
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
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Registra a carga horária dedicada pelo docente no ensino de turmas a
 * distância.<br/>
 * A carga horária é totalizada por grupo de componente curricular lecionado.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Entity
@Table(name = "carga_horaria_ead", schema = "ead", uniqueConstraints = {})
public class CargaHorariaEad implements Validatable, Comparable<CargaHorariaEad> {

	/** Constante que define o ano inicial do cadastro da carga horária dedicada ao ensino do EAD. */
	private static final Integer ANO_INICIO_CADASTRO_CARGA_HORARIA = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.ANO_INICIO_CADASTRO_CH_DEDICADA_EAD);

	/** CH Semanal máxima dedicada por servidor que tem regime de Dedicação Exclusiva. */
	private static final Integer CH_MAX_DEDICACAO_EXCLUSIVA = 40;

	/** Chave primária. */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "hibernate_sequence") })
	@Column(name = "id_carga_horaria_ead", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Ano das turmas lecionadas para o componente curricular. */ 
	private int ano;
	
	/** Período das turmas lecionadas para o componente curricular. */
	private int periodo;
	
	/** Componente curricular lecionado nas turmas. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_componente_curricular")
	private ComponenteCurricular componenteCurricular;

	/** Total de turmas lecionadas para o componente curricular. */
	@Column(name = "qtd_turmas")
	private long qtdTurmas;

	/** Total de discentes matriculados nas turmas do componente curricular. */
	@Column(name = "qtd_discentes")
	private long qtdDiscentes;

	/**
	 * Carga horária dedicada pelo docente as turmas de EAD do componente
	 * curricular.
	 */
	@Column(name = "ch_dedicada")
	private int chDedicada;

	/** Data de cadastro desta carga horária dedicada. */
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date datacadastro;

	/** Registro de Entrada do usuário que cadastrou a carga horária. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/**
	 * Define o servidor associado à carga horária.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_servidor")
	private Servidor servidor;
	
	/** Para fins de registro da CH dedicada ao ensino a distÂncia de docentes externos. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_docente_externo")
	private DocenteExterno docenteExterno;

	public CargaHorariaEad() {
		super();
	}

	/** Valida os dados obrigatórios: servidor, componenteCurricular, ano, período.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		if (isEmpty(docenteExterno))
			validateRequired(servidor, "Docente", erros);
		else
			validateRequired(docenteExterno, "Docente Externo", erros);
		validateRequired(componenteCurricular, "Componente Curricular", erros);
		validateMinValue(ano, ANO_INICIO_CADASTRO_CARGA_HORARIA, "Ano", erros);
		validateRange(periodo, 1, 4, "Período", erros);
		int maxCH = 40;
		String nome = isEmpty(docenteExterno) ? servidor.getNome() : docenteExterno.getNome();
		if (servidor != null && servidor.getRegimeTrabalho() != null)
			maxCH = servidor.getRegimeTrabalho() > CH_MAX_DEDICACAO_EXCLUSIVA ? CH_MAX_DEDICACAO_EXCLUSIVA : servidor.getRegimeTrabalho();
		if (chDedicada > maxCH) {
			erros.addErro("CH Semanal Dedicada do docente "+nome+": O valor deve ser menor ou igual a "+maxCH+"h");
		}
		return erros;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ComponenteCurricular getComponenteCurricular() {
		return componenteCurricular;
	}

	public void setComponenteCurricular(
			ComponenteCurricular componenteCurricular) {
		this.componenteCurricular = componenteCurricular;
	}

	public long getQtdTurmas() {
		return qtdTurmas;
	}

	public void setQtdTurmas(long qtdTurmas) {
		this.qtdTurmas = qtdTurmas;
	}

	public long getQtdDiscentes() {
		return qtdDiscentes;
	}

	public void setQtdDiscentes(long qtdDiscentes) {
		this.qtdDiscentes = qtdDiscentes;
	}

	public int getChDedicada() {
		return chDedicada;
	}

	public void setChDedicada(int chDedicada) {
		this.chDedicada = chDedicada;
	}

	public Date getDatacadastro() {
		return datacadastro;
	}

	public void setDatacadastro(Date datacadastro) {
		this.datacadastro = datacadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	/**
	 * Retorna uma representação textual deste objeto no formato: Codigo - Nome
	 * do Componente Curricular: CH dedicada.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return componenteCurricular.getCodigoNome() + ": " + chDedicada;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}
	
	/** Compara este objeto com outro, comparando o nome do servidor e o nome do componente curricular.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CargaHorariaEad outro) {
		String nome = isEmpty(this.docenteExterno) ? servidor.getNome() : this.docenteExterno.getNome();
		String outroNome = isEmpty(outro.docenteExterno) ? outro.servidor.getNome() : outro.docenteExterno.getNome();
		int comp = nome.compareTo(outroNome);
		if (comp == 0)
			comp = this.getComponenteCurricular().getNome().compareTo(outro.getComponenteCurricular().getNome());
		return comp;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CargaHorariaEad) {
			CargaHorariaEad other = (CargaHorariaEad) obj;
			if (other.getId() == this.getId()) 
				return true;
			else if ( (isEmpty(this.docenteExterno) && isEmpty(other.getDocenteExterno())
					   && other.getServidor().getId() == this.getServidor().getId()
					   || !isEmpty(this.docenteExterno) && !isEmpty(other.getDocenteExterno())
					   && other.docenteExterno.getId() == this.getDocenteExterno().getId())
					&& 
					other.getComponenteCurricular().getId() == this.getComponenteCurricular().getId()) 
				return true;
			else
				return false;
		} else
			return false;
	}
	
	@Override
	public int hashCode() {
		int id = 0;
		if (isEmpty(docenteExterno))
			id = servidor.getId();
		else
			id = docenteExterno.getId();
		return HashCodeUtil.hashAll(id, componenteCurricular.getId());
	}

	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}
	
}
