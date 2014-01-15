/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/11/2006
 *
 */
package br.ufrn.sigaa.extensao.relatorio.dominio;

import java.util.Date;

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

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;

/*******************************************************************************
 * Representa o relatório parcial e final apresentado pelo discente de extensão.
 * 
 * @author Victor Hugo
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table( schema = "extensao", name = "relatorio_bolsista_extensao")
public class RelatorioBolsistaExtensao implements Validatable {

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_relatorio_bolsista_extensao", unique = true, nullable = false)
	private int id;

	//Discente que cadastrou o relatório
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente_extensao")
	private DiscenteExtensao discenteExtensao;

	//Data docadastro
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", nullable = false)
	private Date dataCadastro;

	//Data de envio
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_envio", nullable = false)
	private Date dataEnvio;
	
	//Descrição textual da introdução
	@Column(name = "introducao")
	private String introducao;

	//Descrição textual das metodologias para o desenvolvimento da ação
	@Column(name = "metodologia")
	private String metodologia;

	//Descrição das atividades desenvolvidas
	@Column(name = "atividades")
	private String atividades;

	//Descrição dos resultados obtidos
	@Column(name = "resultados")
	private String resultados;

	//Descrição da conclusão
	@Column(name = "conclusoes")
	private String conclusoes;

	//Parecer do Orientador
	@Column(name = "parecer_orientador")
	private String parecerOrientador;

	//Data que foi dado o parecer.
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_parecer")
	private Date dataParecer;

	//Registro de entrada do responsável pelo parecer.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_parecer")
	private RegistroEntrada registroParecer;

	//Registro de entrada do cadastro
	@ManyToOne
	@JoinColumn(name = "id_registro_entrada_cadastro", updatable = false)
	private RegistroEntrada registroEntradaCadastro;

	//Tipo de relatório
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_relatorio", unique = true, updatable = false)
	private TipoRelatorioExtensao tipoRelatorio = new TipoRelatorioExtensao();

	private boolean ativo;
	

	public String getAtividades() {
		return atividades;
	}

	public void setAtividades(String atividades) {
		this.atividades = atividades;
	}

	public String getConclusoes() {
		return conclusoes;
	}

	public void setConclusoes(String conclusoes) {
		this.conclusoes = conclusoes;
	}

	public Date getDataParecer() {
		return dataParecer;
	}

	public void setDataParecer(Date dataParecer) {
		this.dataParecer = dataParecer;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIntroducao() {
		return introducao;
	}

	public void setIntroducao(String introducao) {
		this.introducao = introducao;
	}

	public String getMetodologia() {
		return metodologia;
	}

	public void setMetodologia(String metodologia) {
		this.metodologia = metodologia;
	}

	public String getParecerOrientador() {
		return parecerOrientador;
	}

	public void setParecerOrientador(String parecerOrientador) {
		this.parecerOrientador = parecerOrientador;
	}

	public RegistroEntrada getRegistroParecer() {
		return registroParecer;
	}

	public void setRegistroParecer(RegistroEntrada registroParecer) {
		this.registroParecer = registroParecer;
	}

	public String getResultados() {
		return resultados;
	}

	public void setResultados(String resultados) {
		this.resultados = resultados;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(discenteExtensao, "Discentes de Extensão", lista);
		ValidatorUtil.validateRequired(introducao, "Introdução", lista);
		ValidatorUtil.validateRequired(metodologia, "Metodologia", lista);
		ValidatorUtil.validateRequired(atividades, "Atividades", lista);
		ValidatorUtil.validateRequired(resultados, "Resultados", lista);
		ValidatorUtil.validateRequired(conclusoes, "Conclusões", lista);
		return lista;
	}

	public DiscenteExtensao getDiscenteExtensao() {
		return discenteExtensao;
	}

	public void setDiscenteExtensao(DiscenteExtensao discenteExtensao) {
		this.discenteExtensao = discenteExtensao;
	}

	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public RegistroEntrada getRegistroEntradaCadastro() {
		return registroEntradaCadastro;
	}

	public void setRegistroEntradaCadastro(RegistroEntrada registroEntradaCadastro) {
		this.registroEntradaCadastro = registroEntradaCadastro;
	}

	public TipoRelatorioExtensao getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(TipoRelatorioExtensao tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isEditavel() {
		return dataParecer == null;
	}
	
	public boolean isRelatorioFinal() {
	    return getTipoRelatorio().getId() == TipoRelatorioExtensao.RELATORIO_FINAL;
	}

	public boolean isRelatorioParcial() {
	    return getTipoRelatorio().getId() == TipoRelatorioExtensao.RELATORIO_PARCIAL;
	}

}
