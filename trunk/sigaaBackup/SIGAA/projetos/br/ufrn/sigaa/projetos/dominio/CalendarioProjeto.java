package br.ufrn.sigaa.projetos.dominio;

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

/***
 * Representa um calendário onde são postas regras de validação mantidas por gestores de projetos acadêmicos.
 * 
 * @author ilueny
 *
 */
@Entity
@Table(name = "calendario_projeto", schema = "projetos")
public class CalendarioProjeto implements Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_calendario_projeto", nullable = false)
	private int id;
	
	@Column(name = "ano_referencia")
	private int anoReferencia;

	private boolean ativo;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "inicio_cadastro_bolsa")
	private Date inicioCadastroBolsa;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "fim_cadastro_bolsa")
	private Date fimCadastroBolsa;

	//Registro de entrada do responsável pelo cadastro do calendario.
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroEntrada;
	
	//Data de cadastro
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	private Date dataCadastro;
	
	public CalendarioProjeto(){}
	
	public CalendarioProjeto(int anoReferencia){
		this.anoReferencia = anoReferencia;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAnoReferencia() {
		return anoReferencia;
	}

	public void setAnoReferencia(int anoReferencia) {
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

	public String periodoCadastroBolsas(){
		return "Data deve estar entre o período de " 
				+ CalendarUtils.getDiaByData(this.inicioCadastroBolsa) + " à "
				+ CalendarUtils.getDiaByData(this.fimCadastroBolsa);
	}

	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validaInt(this.anoReferencia, "Ano Referência", lista);
		if (this.inicioCadastroBolsa == null)
			lista.addErro("Data inicial do cadastro dos Planos de Trabalho: Campo de preenchimento obrigatório não informado ou data inválida. ");
		if (this.fimCadastroBolsa == null)
			lista.addErro("Data final do cadastro dos Planos de Trabalho: Campo de preenchimento obrigatório não informado ou data inválida.");
		if (CalendarUtils.calculoMeses(inicioCadastroBolsa, fimCadastroBolsa) != 0)
			lista.addErro("As Datas inicial e final do cadastro de bolsas devem ter um período máximo de 30 dias.");
		ValidatorUtil.validaInicioFim(this.inicioCadastroBolsa, this.fimCadastroBolsa, "Período de efetivação das bolsas de projetos", lista);
		return lista;
	}
	

}