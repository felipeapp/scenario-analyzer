/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Apresenta��es em eventos realizadas por docentes da institui��o
 * @author Gleydson
 */
@Entity
@Table(name = "apresentacao_em_evento", schema = "prodocente")
@PrimaryKeyJoinColumn(name = "id_apresentacao_em_evento")
public class ApresentacaoEmEvento extends Producao {

	@Column(name = "tipo")
	private String tipo;

	@Column(name = "local")
	private String local;

	@Column(name = "data")
	@Temporal(TemporalType.DATE)
	private Date data;

	@Column(name = "evento")
	private String evento;

	@JoinColumn(name = "id_tipo_evento", referencedColumnName = "id_tipo_evento")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoEvento tipoEvento = new TipoEvento();

	@JoinColumn(name = "id_tipo_regiao", referencedColumnName = "id_tipo_regiao")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoRegiao tipoRegiao = new TipoRegiao();


	/** Creates a new instance of ApresentacaoEmEvento */
	public ApresentacaoEmEvento() {
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		setAnoReferencia(CalendarUtils.getAno(data));
		this.data = data;
	}

	public String getEvento() {
		return evento;
	}

	public void setEvento(String evento) {
		this.evento = evento;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public TipoEvento getTipoEvento() {
		return tipoEvento;
	}

	public void setTipoEvento(TipoEvento tipoEvento) {
		this.tipoEvento = tipoEvento;
	}

	public TipoRegiao getTipoRegiao() {
		return tipoRegiao;
	}

	public void setTipoRegiao(TipoRegiao tipoRegiao) {
		this.tipoRegiao = tipoRegiao;
	}

	/*
	 * Campos obrigat�rios: Titulo, Data, Tipo Regi�o, Tipo Evento,
	 * 						Participa��o, Natureza
	 */

	public ListaMensagens validate(){

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getTitulo(),"T�tulo", lista);
		ValidatorUtil.validateRequired(getAnoReferencia(), "Ano de Refer�ncia", lista);
		ValidatorUtil.validateRequiredId(getTipoRegiao().getId(),"Tipo Regi�o", lista);
		ValidatorUtil.validateRequiredId(getTipoEvento().getId(), "Tipo de Evento", lista);
		ValidatorUtil.validateRequired(getArea(), "�rea", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-�rea", lista);
		ValidatorUtil.validateRequired(getEvento(), "Evento", lista);
		ValidatorUtil.validateRequired(getDataProducao(), "Data de Produ��o", lista);
		return lista;
	}

}
