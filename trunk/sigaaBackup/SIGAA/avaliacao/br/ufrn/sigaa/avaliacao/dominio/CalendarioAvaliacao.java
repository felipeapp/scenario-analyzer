/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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

/** Calend�rio de aplica��o de question�rios de Avalia��o Institucional.
 * @author �dipo Elder F. Melo
 *
 */
@Entity 
@Table(name="calendario_avaliacao", schema="avaliacao")
public class CalendarioAvaliacao implements Validatable {

	/** Chave prim�ria. */
	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
    				  parameters={ @Parameter(name="sequence_name", value="avaliacao.avaliacao_seq") })
	@Column(name = "id_calendario")
	private int id;
	
	/** {@link FormularioAvaliacaoInstitucional Formul�rio} a ser aplicado no per�odo especificado. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_formulario")
	private FormularioAvaliacaoInstitucional formulario;
	
	/** Ano referente � aplica��o do formul�rio. **/
	private int ano;
	/** Per�odo referente � aplica��o do formul�rio. **/
	private int periodo;
	/** In�cio da aplica��o do formul�rio. **/
	private Date inicio;
	/** Fim da aplica��o do formul�rio. **/
	private Date fim;
	/** Indica se o formul�rio est� ativo ou n�o. **/
	private boolean ativo;

	/** Construtor padr�o. */
	public CalendarioAvaliacao() {
		formulario = new FormularioAvaliacaoInstitucional();
	}
	
	/** Retorna a chave prim�ria
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}
	/** Seta a chave prim�ria
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}
	/** Retorna o Formul�rio a ser aplicado no per�odo especificado. 
	 * @return
	 */
	public FormularioAvaliacaoInstitucional getFormulario() {
		return formulario;
	}
	/** Seta o Formul�rio a ser aplicado no per�odo especificado.
	 * @param formulario
	 */
	public void setFormulario(FormularioAvaliacaoInstitucional formulario) {
		this.formulario = formulario;
	}
	/** Retorna o ano referente � aplica��o do formul�rio. 
	 * @return
	 */
	public int getAno() {
		return ano;
	}
	/** Seta o ano referente � aplica��o do formul�rio.
	 * @param ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}
	/** Retorna o per�odo referente � aplica��o do formul�rio. 
	 * @return
	 */
	public int getPeriodo() {
		return periodo;
	}
	/** Seta o per�odo referente � aplica��o do formul�rio.
	 * @param periodo
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}
	/** Retorna o in�cio da aplica��o do formul�rio. 
	 * @return
	 */
	public Date getInicio() {
		return inicio;
	}
	/** Seta o in�cio da aplica��o do formul�rio.
	 * @param inicio
	 */
	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}
	/** Retorna o fim da aplica��o do formul�rio. 
	 * @return
	 */
	public Date getFim() {
		return fim;
	}
	/** Seta o fim da aplica��o do formul�rio.
	 * @param fim
	 */
	public void setFim(Date fim) {
		this.fim = fim;
	}
	/** Valida os dados para persist�ncia: datas de in�cio e fim, ano, per�odo, e formul�rio.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(inicio, "Data de In�cio", lista);
		validateRequired(fim, "Data de Fim", lista);
		validateRequired(formulario, "Formul�rio", lista);
		validateRequiredId(ano, "Ano", lista);
		validateRange(periodo, 1, 2, "Per�odo", lista);
		validaOrdemTemporalDatas(inicio, fim, true, "In�cio e fim", lista);
		return lista;
	}
	/** Indica se o formul�rio est� ativo ou n�o. 
	 * @return
	 */
	public boolean isAtivo() {
		return ativo;
	}
	/** Seta se o formul�rio est� ativo ou n�o. 
	 * @param ativo
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	/** Indica se est� no per�odo de preenchimento do formul�rio.
	 * @return
	 */
	public boolean isPeriodoPreenchimento() {
		return CalendarUtils.isDentroPeriodo(inicio, fim);
	}

}
