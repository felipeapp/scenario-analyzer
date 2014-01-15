/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/09/2010
 *
 */
package br.ufrn.sigaa.avaliacao.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validaOrdemTemporalDatas;
import static br.ufrn.arq.util.ValidatorUtil.validateRange;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

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

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;

/** Calendário de aplicação de questionários de Avaliação Institucional.
 * @author Édipo Elder F. Melo
 *
 */
@Entity 
@Table(name="calendario_avaliacao", schema="avaliacao")
public class CalendarioAvaliacao implements Validatable {

	/** Chave primária. */
	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
    				  parameters={ @Parameter(name="sequence_name", value="avaliacao.avaliacao_seq") })
	@Column(name = "id_calendario")
	private int id;
	
	/** {@link FormularioAvaliacaoInstitucional Formulário} a ser aplicado no período especificado. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_formulario")
	private FormularioAvaliacaoInstitucional formulario;
	
	/** Ano referente à aplicação do formulário. **/
	private int ano;
	/** Período referente à aplicação do formulário. **/
	private int periodo;
	/** Início da aplicação do formulário. **/
	private Date inicio;
	/** Fim da aplicação do formulário. **/
	private Date fim;
	/** Indica se o formulário está ativo ou não. **/
	private boolean ativo;

	/** Construtor padrão. */
	public CalendarioAvaliacao() {
		formulario = new FormularioAvaliacaoInstitucional();
	}
	
	/** Retorna a chave primária
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}
	/** Seta a chave primária
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}
	/** Retorna o Formulário a ser aplicado no período especificado. 
	 * @return
	 */
	public FormularioAvaliacaoInstitucional getFormulario() {
		return formulario;
	}
	/** Seta o Formulário a ser aplicado no período especificado.
	 * @param formulario
	 */
	public void setFormulario(FormularioAvaliacaoInstitucional formulario) {
		this.formulario = formulario;
	}
	/** Retorna o ano referente à aplicação do formulário. 
	 * @return
	 */
	public int getAno() {
		return ano;
	}
	/** Seta o ano referente à aplicação do formulário.
	 * @param ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}
	/** Retorna o período referente à aplicação do formulário. 
	 * @return
	 */
	public int getPeriodo() {
		return periodo;
	}
	/** Seta o período referente à aplicação do formulário.
	 * @param periodo
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}
	/** Retorna o início da aplicação do formulário. 
	 * @return
	 */
	public Date getInicio() {
		return inicio;
	}
	/** Seta o início da aplicação do formulário.
	 * @param inicio
	 */
	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}
	/** Retorna o fim da aplicação do formulário. 
	 * @return
	 */
	public Date getFim() {
		return fim;
	}
	/** Seta o fim da aplicação do formulário.
	 * @param fim
	 */
	public void setFim(Date fim) {
		this.fim = fim;
	}
	/** Valida os dados para persistência: datas de início e fim, ano, período, e formulário.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(inicio, "Data de Início", lista);
		validateRequired(fim, "Data de Fim", lista);
		validateRequired(formulario, "Formulário", lista);
		validateRequiredId(ano, "Ano", lista);
		validateRange(periodo, 1, 2, "Período", lista);
		validaOrdemTemporalDatas(inicio, fim, true, "Início e fim", lista);
		return lista;
	}
	/** Indica se o formulário está ativo ou não. 
	 * @return
	 */
	public boolean isAtivo() {
		return ativo;
	}
	/** Seta se o formulário está ativo ou não. 
	 * @param ativo
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	/** Indica se está no período de preenchimento do formulário.
	 * @return
	 */
	public boolean isPeriodoPreenchimento() {
		return CalendarUtils.isDentroPeriodo(inicio, fim);
	}

}
