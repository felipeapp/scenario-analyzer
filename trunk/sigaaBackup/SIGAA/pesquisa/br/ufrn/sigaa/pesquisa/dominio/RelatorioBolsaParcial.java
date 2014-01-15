/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/01/2007
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

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

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;


/**
 * Classe representativa dos relatórios parciais de bolsas de iniciação científica
 * 
 * @author ricardo
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "relatorio_bolsa_parcial", schema = "pesquisa", uniqueConstraints = {})
public class RelatorioBolsaParcial extends AbstractMovimento implements PersistDB, Validatable {

	// Fields

	/** Chave Primária */
	private int id;

	/** Informa o plano de trabalho do Relatório de Bolsa Parcial */
	private PlanoTrabalho planoTrabalho;

	/** Informa as atividades Realizadas pelo Relatório de Bolsa Parcial */
	private String atividadesRealizadas;

	private String comparacaoOriginalExecutado;

	/** Informa as outras atividades Realizadas pelo Relatório de Bolsa Parcial */
	private String outrasAtividades;

	/** Informa o parecer do Orientador sobre o Relatório de Bolsa Parcial*/
	private String parecerOrientador;

	/** Data de envio do Relatório de Bolsa Parcial */
	private Date dataEnvio;

	/** Data do parecer do orientador */
	private Date dataParecer;

	/** Discentes membros do Relatório de Bolsa Parcial  */
	private MembroProjetoDiscente membroDiscente;
	
	/** Atributo que serve para informar se o Relatório de Bolsa Parcial foi enviado ou não */
	private boolean enviado;

	/** Informa o resultado prelimina do Relatório de Bolsa Parcial */
	private String resultadosPreliminares;
	
	@CampoAtivo
	private boolean ativo;
	
	// Constructors

	/** default constructor */
	public RelatorioBolsaParcial() {
	}

	/** minimal constructor */
	public RelatorioBolsaParcial(int idRelatorioDiscenteParcial) {
		this.id = idRelatorioDiscenteParcial;
	}

	/** full constructor */
	public RelatorioBolsaParcial(int idRelatorioDiscenteParcial,
			PlanoTrabalho planoTrabalho, String atividadesRealizadas,
			String comparacaoOriginalExecutado, String outrasAtividades,
			String parecerOrientador, String resultadosPreliminares, Date dataEnvio, Date dataParecer) {
		this.id = idRelatorioDiscenteParcial;
		this.planoTrabalho = planoTrabalho;
		this.atividadesRealizadas = atividadesRealizadas;
		this.comparacaoOriginalExecutado = comparacaoOriginalExecutado;
		this.outrasAtividades = outrasAtividades;
		this.parecerOrientador = parecerOrientador;
		this.resultadosPreliminares = resultadosPreliminares;
		this.dataEnvio = dataEnvio;
		this.dataParecer = dataParecer;
	}

	// Property accessors
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_relatorio_discente_parcial", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idRelatorioDiscenteParcial) {
		this.id = idRelatorioDiscenteParcial;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_plano_trabalho", unique = false, nullable = true, insertable = true, updatable = true)
	public PlanoTrabalho getPlanoTrabalho() {
		return this.planoTrabalho;
	}

	public void setPlanoTrabalho(
			PlanoTrabalho planoTrabalho) {
		this.planoTrabalho = planoTrabalho;
	}

	@Column(name = "atividades_realizadas", unique = false, nullable = true, insertable = true, updatable = true)
	public String getAtividadesRealizadas() {
		return this.atividadesRealizadas;
	}

	public void setAtividadesRealizadas(String atividades) {
		this.atividadesRealizadas = atividades;
	}

	@Column(name = "comparacao_original_executado", unique = false, nullable = true, insertable = true, updatable = true)
	public String getComparacaoOriginalExecutado() {
		return this.comparacaoOriginalExecutado;
	}

	public void setComparacaoOriginalExecutado(
			String comparacaoOriginalExecutado) {
		this.comparacaoOriginalExecutado = comparacaoOriginalExecutado;
	}

	@Column(name = "outras_atividades", unique = false, nullable = true, insertable = true, updatable = true)
	public String getOutrasAtividades() {
		return this.outrasAtividades;
	}

	public void setOutrasAtividades(String outrasAtividades) {
		this.outrasAtividades = outrasAtividades;
	}

	@Column(name = "parecer_orientador", unique = false, nullable = true, insertable = true, updatable = true)
	public String getParecerOrientador() {
		return this.parecerOrientador;
	}

	public void setParecerOrientador(String parecerOrientador) {
		this.parecerOrientador = parecerOrientador;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_envio", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataEnvio() {
		return this.dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_parecer", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataParecer() {
		return this.dataParecer;
	}

	public void setDataParecer(Date dataParecer) {
		this.dataParecer = dataParecer;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_membro_discente", unique = false, nullable = true, insertable = true, updatable = true)
	public MembroProjetoDiscente getMembroDiscente() {
		return membroDiscente;
	}

	public void setMembroDiscente(MembroProjetoDiscente membroDiscente) {
		this.membroDiscente = membroDiscente;
	}

	@Column(name = "relatorio_enviado", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isEnviado() {
		return enviado;
	}

	public void setEnviado(boolean enviado) {
		this.enviado = enviado;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
	@Column(name = "resultados_preliminares", unique = false, nullable = true, insertable = true, updatable = true)
	public String getResultadosPreliminares() {
		return resultadosPreliminares;
	}

	public void setResultadosPreliminares(String resultadosPreliminares) {
		this.resultadosPreliminares = resultadosPreliminares;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * Realiza as verificações para atender as obrigatoriedades do cadastro.
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();

			ValidatorUtil.validateRequiredId(membroDiscente.getId(), "Discente", erros);
			
			if (getAtividadesRealizadas() == null || "".equals(getAtividadesRealizadas().trim())) {
				ValidatorUtil.validateRequired(null, "Atividades realizadas", erros);
			} else {
				setAtividadesRealizadas( StringUtils.removeCarriageReturn(getAtividadesRealizadas()) );
				if (getAtividadesRealizadas().length() > 5000){
					erros.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Atividades realizadas", 5000);
				}
			}

			if (getComparacaoOriginalExecutado() == null || "".equals(getComparacaoOriginalExecutado().trim())) {
				ValidatorUtil.validateRequired(null, "Comparação entre o plano original e o executado", erros);
			} else {
				setComparacaoOriginalExecutado( StringUtils.removeCarriageReturn(getComparacaoOriginalExecutado()) );
				if (getComparacaoOriginalExecutado().length() > 5000){
					erros.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Comparação entre o plano original e o executado", 5000);
				}
			}

			if (getOutrasAtividades() == null || "".equals(getOutrasAtividades().trim())) {
				ValidatorUtil.validateRequired(null, "Outras atividades", erros);
			} else {
				setOutrasAtividades( StringUtils.removeCarriageReturn(getOutrasAtividades()) );
				if ( getOutrasAtividades().length() > 5000) {
					erros.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Outras atividades", 5000);
				}
			}

			if (getResultadosPreliminares() == null || "".equals(getResultadosPreliminares().trim())) {
				ValidatorUtil.validateRequired(null, "Resultados preliminares", erros);
			} else {
				setResultadosPreliminares(StringUtils.removeCarriageReturn(getResultadosPreliminares()));
				if ( getResultadosPreliminares().length() > 5000) {
					erros.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, "Resultados preliminares", 5000);
				}
			}
			return erros;
	}

}
