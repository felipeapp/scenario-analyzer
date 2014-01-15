/*
 * Created on 01/06/2007
 *
 */
package br.ufrn.sigaa.monitoria.dominio;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.time.DateUtils;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * <p>
 * Representa uma autoriza��o da Pr�-Reitoria de Gradua��o para envio de 
 * relat�rios de atividades dos monitores (frequ�ncia).
 * Os monitores s� enviam freq��ncias do m�s que a Pr�-Reitoria autorizar.
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "envio_frequencia", schema = "monitoria")
public class EnvioFrequencia implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_envio_frequencia", unique = true, nullable = false)
	private int id;

	@Column(name = "mes", nullable = false)
	private Integer mes;

	@Column(name = "ano", nullable = false)
	private Integer ano;

	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio_recebimento", nullable = false)
	private Date dataInicioRecebimento;

	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim_recebimento", nullable = false)
	private Date dataFimRecebimento;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio_entrada_monitor_permitido")
	private Date dataInicioEntradaMonitorPermitido;

	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim_entrada_monitor_permitido")
	private Date dataFimEntradaMonitorPermitido;
	
	@Column(name = "ano_inicio_projetos_permitidos")
	private Integer anoInicioProjetosPermitidos;

	@Column(name = "ano_fim_projetos_permitidos")
	private Integer anoFimProjetosPermitidos;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", nullable = false)
	@CriadoEm
	private Date dataCadastro;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	@Column(name = "ativo", nullable = false)
	private boolean ativo;

	/**
	 * @return id
	 */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Ano referente ao recebimento das freq��ncias.
	 * 
	 * @return ano
	 */
	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	/**
	 * M�s referente ao recebimento das freq��ncias.
	 * 
	 * @return m�s
	 */
	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	/**
	 * Data do cadastro da regra de recebimento de freq��ncias.
	 * 
	 * @return data
	 */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/**
	 * Registro de entrada do usu�rio que cadastrou a regra.
	 * 
	 * @return {@link RegistroEntrada}
	 */
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
	
	/**
	 * Verifica se a regra pode ser cadastrada no banco.
	 * 
	 * @return {@link ListaMensagens} com lista de mensagens de erro de valida��o.
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(mes, "M�s Refer�ncia", lista);
		ValidatorUtil.validateRequired(ano, "Ano Refer�ncia", lista);
		ValidatorUtil.validateRequired(dataInicioEntradaMonitorPermitido, "Monitores cadastrados no per�odo: Data In�cio", lista);
		ValidatorUtil.validateRequired(dataFimEntradaMonitorPermitido, "Monitores cadastrados no per�odo: Data Fim", lista);
		ValidatorUtil.validateRequired(dataInicioRecebimento, "Data In�cio Recebimento", lista);
		ValidatorUtil.validateRequired(dataFimRecebimento, "Data Fim Recebimento", lista);
		ValidatorUtil.validateRequired(anoInicioProjetosPermitidos, "Ano In�cio Projeto Permitido", lista);
		ValidatorUtil.validateRequired(anoFimProjetosPermitidos, "Ano Fim Projeto Permitido", lista);

		if (anoInicioProjetosPermitidos != null) {
		    ValidatorUtil.validateMinValue(anoInicioProjetosPermitidos, 1900, "Ano In�cio Projeto Permitido", lista);
		}
		if (anoFimProjetosPermitidos != null) {
		    ValidatorUtil.validateMinValue(anoFimProjetosPermitidos, 1900, "Ano Fim Projeto Permitido", lista);
		}
		if (ano != null) {
		    ValidatorUtil.validateMinValue(ano, 1900, "Ano Refer�ncia", lista);
		}
		return lista;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * Data final para recebimento da frequ�ncia.
	 * 
	 * @return data
	 */
	public Date getDataFimRecebimento() {
		return dataFimRecebimento;
	}

	public void setDataFimRecebimento(Date dataFimRecebimento) {
		this.dataFimRecebimento = dataFimRecebimento;
	}

	/**
	 * Data inicial para recebimento da frequ�ncia.
	 * 
	 * @return data
	 */
	public Date getDataInicioRecebimento() {
		return dataInicioRecebimento;
	}

	public void setDataInicioRecebimento(Date dataInicioRecebimento) {
		this.dataInicioRecebimento = dataInicioRecebimento;
	}

	/**
	 * Informa o ano inicial de projetos que est�o autorizados para envio de
	 * frequ�ncia. Somente projetos dentro do per�odo
	 * (anoInicioProjetosPermitidos - anoFimProjetosPermitidos) podem enviar
	 * freq��ncia.
	 * 
	 * @return ano
	 */
	public Integer getAnoInicioProjetosPermitidos() {
		return anoInicioProjetosPermitidos;
	}

	public void setAnoInicioProjetosPermitidos(Integer anoInicioProjetosPermitidos) {
		this.anoInicioProjetosPermitidos = anoInicioProjetosPermitidos;
	}

	/**
	 * Informa o ano inicial de projetos que est�o autorizados para envio de
	 * frequ�ncia. Somente projetos dentro do per�odo
	 * (anoInicioProjetosPermitidos - anoFimProjetosPermitidos) podem enviar
	 * freq��ncia.
	 * 
	 * @return ano
	 */
	public Integer getAnoFimProjetosPermitidos() {
		return anoFimProjetosPermitidos;
	}

	public void setAnoFimProjetosPermitidos(Integer anoFimProjetosPermitidos) {
		this.anoFimProjetosPermitidos = anoFimProjetosPermitidos;
	}

	/**
	 * Verifica se est� no per�odo de recebimento e freq��ncias.
	 * dataInicioRecebimento <= Data atual <= dataFimRecebimento
	 * 
	 * @return <code>true</code> se a data atual est� no per�odo de recebimento.
	 */
	public boolean isEnvioLiberado() {
		return ((this.getDataInicioRecebimento().compareTo(
				DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) <= 0) && (this
				.getDataFimRecebimento().compareTo(
						DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) >= 0));

	}
	
	
	/**
	 * Utilizado para validar monitores no cadastro de freq��ncias
	 * de per�odos anteriores a data de in�cio da bolsa.
	 * 
	 * @return data
	 */
	public Date getDataInicioEntradaMonitorPermitido() {
	    return dataInicioEntradaMonitorPermitido;
	}

	public void setDataInicioEntradaMonitorPermitido(Date dataInicioEntradaMonitorPermitido) {
	    this.dataInicioEntradaMonitorPermitido = dataInicioEntradaMonitorPermitido;
	}

	/**
	 * Utilizado para validar monitores no cadastro de freq��ncias
	 * de per�odos anteriores a data de in�cio da bolsa.
	 * 
	 * @return data
	 */
	public Date getDataFimEntradaMonitorPermitido() {
	    return dataFimEntradaMonitorPermitido;
	}

	public void setDataFimEntradaMonitorPermitido(Date dataFimEntradaMonitorPermitido) {
	    this.dataFimEntradaMonitorPermitido = dataFimEntradaMonitorPermitido;
	}
	
}
