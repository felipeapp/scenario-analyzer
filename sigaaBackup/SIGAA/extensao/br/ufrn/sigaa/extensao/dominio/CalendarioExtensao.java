package br.ufrn.sigaa.extensao.dominio;

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
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Classe de dom�nio para o calend�rio de extens�o
 *
 */
@Entity
@Table(name = "calendario_extensao", schema = "extensao")
public class CalendarioExtensao implements Validatable {

	/** Atributo utilizado para representar o numero id do calend�rio */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_calendario_extensao", nullable = false)
	private int id;
	
	/** Atributo utilizado para representar o ano de referencia */
	@Column(name = "ano_referencia")
	private Integer anoReferencia;

	/** Atributo utilizado para representar se o calend�rio est� ou n�o ativo */
	private boolean ativo;
	
	/** Atributo utilizado para representar a data de in�cio de cadastro de bolsas */
	@Temporal(TemporalType.DATE)
	@Column(name = "inicio_cadastro_bolsa")
	private Date inicioCadastroBolsa;
	
	/** Atributo utilizado para representar a data de fim de cadastro de bolsas */
	@Temporal(TemporalType.DATE)
	@Column(name = "fim_cadastro_bolsa")
	private Date fimCadastroBolsa;

	/** Atributo utilizado para o registro de entrada do respons�vel pelo cadastro do calendario */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroEntrada;
	
	/** Atributo utilizado para representar a data de caadstro do calend�rio */
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	private Date dataCadastro;

	public CalendarioExtensao(){}
	
	public CalendarioExtensao(int anoReferencia){
		this.anoReferencia = anoReferencia;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Integer getAnoReferencia() {
		return anoReferencia;
	}

	public void setAnoReferencia(Integer anoReferencia) {
		this.anoReferencia = anoReferencia;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getInicioCadastroBolsa() {
		return inicioCadastroBolsa;
	}

	public void setInicioCadastroBolsa(Date inicioCadastroBolsa) {
		this.inicioCadastroBolsa = inicioCadastroBolsa;
	}

	public Date getFimCadastroBolsa() {
		return fimCadastroBolsa;
	}

	public void setFimCadastroBolsa(Date fimCadastroBolsa) {
		this.fimCadastroBolsa = fimCadastroBolsa;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
	
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	/**
	 * M�todo utilizado para informar o per�odo de cadastro de bolsas
	 * @return
	 */
	public String periodoCadastroBolsas(){
		return "Data deve estar entre o per�odo de " 
				+ CalendarUtils.getDiaByData(this.inicioCadastroBolsa) + " � "
				+ CalendarUtils.getDiaByData(this.fimCadastroBolsa);
	}

	/**
	 * M�todo utilizado para validar os dados do Calend�rio de Extens�o
	 */
	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validaInt(this.anoReferencia, "Ano Refer�ncia", lista);
		if (this.inicioCadastroBolsa == null)
			lista.addErro("Data inicial do per�odo de efetiva��o das bolsas de extens�o: Campo obrigat�rio n�o informado ou data inv�lida.");
		if (this.fimCadastroBolsa == null)
			lista.addErro("Data final do per�odo de efetiva��o das bolsas de extens�o: Campo obrigat�rio n�o informado ou data inv�lida.");
		if ( inicioCadastroBolsa != null && fimCadastroBolsa != null ) {
			if ( !( CalendarUtils.calculoDias(inicioCadastroBolsa, fimCadastroBolsa) > 0 && CalendarUtils.calculoDias(inicioCadastroBolsa, fimCadastroBolsa) <= 15) ) {
				lista.addErro("O per�odo m�ximo para a defini��o da efetiva��o das bolsas s�o 15 dias.");
			}
		}
		ValidatorUtil.validaInicioFim(this.inicioCadastroBolsa, this.fimCadastroBolsa, "Per�odo de efetiva��o das bolsas de extens�o", lista);
		return lista;
	}

}