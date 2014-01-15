/*
 * RenovacaoAssinatura.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

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
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.StringUtils;

/**
 *     Classe que guarda o hist�rico de renova��es de uma Assinatura.
 *
 * @author jadson
 * @since 16/11/2009
 * @version 1.0 criacao da classe
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "renovacao_assinatura", schema = "biblioteca")
public class RenovacaoAssinatura  implements Validatable{

	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.material_informacional_sequence") })
	@Column(name = "id_renovacao_assinatura")
	private int id;
	
	/** A data inicial do per�odo de renova��o da assinatura */
	@Column(name="data_inicial")
	@Temporal(TemporalType.DATE)
	private Date dataInicial;
	
	
	/** A data final do per�odo de renova��o da assinatura */
	@Column(name="data_final")
	@Temporal(TemporalType.DATE)
	private Date dataFinal;
	
	/**  Caso o usu�rio queira informar algo sobre a renova��o   */
	@Column(name="observacao")
	private String observacao;
	
	
	/** A assinatura a quem essa renova��o pertence  */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_assinatura", referencedColumnName = "id_assinatura")
	private Assinatura assinatura;
	
	
	////////////////////////////  INFORMA��ES AUDITORIA  ///////////////////////////////////////	


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_criacao")
	@CriadoPor
	private RegistroEntrada registroCriacao;

	/**
	 * data de cadastro
	 */
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_criacao")
	private Date dataCriacao;

	/**
	 * registro entrada  do usu�rio que realizou a �ltima atualiza��o
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroUltimaAtualizacao;

	/**
	 * data da �ltima atualizac�o
	 */
	@AtualizadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_ultima_atualizacao")
	private Date dataUltimaAtualizacao;

	//////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();
		
		if(dataInicial == null){
			lista.addErro("Informe a data inicial do per�odo de renova��o.");
			return lista;
		}
		
		if(dataFinal == null){
			lista.addErro("Informe a data final do per�odo de renova��o.");
			return lista;
		}
		
		if(dataFinal.before(dataInicial) || dataFinal.equals(dataInicial))
			lista.addErro("A data final da renova��o precisa ser maior que a data inicial");
		
		if(dataFinal.before(new Date())){
			lista.addErro("A data final da renova��o precisa ser maior que a data atual");
		}
		
		if(StringUtils.notEmpty(observacao) && observacao.length() > 400){
			lista.addErro("O tamanho m�ximo para o campo observa��o � de 200 caracteres.");
		}
		
		return lista;
	}
	
	
	
	
	// sets e gets

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Assinatura getAssinatura() {
		return assinatura;
	}

	public void setAssinatura(Assinatura assinatura) {
		this.assinatura = assinatura;
	}

	public RegistroEntrada getRegistroCriacao() {
		return registroCriacao;
	}

	public void setRegistroCriacao(RegistroEntrada registroCriacao) {
		this.registroCriacao = registroCriacao;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public RegistroEntrada getRegistroUltimaAtualizacao() {
		return registroUltimaAtualizacao;
	}


	public void setRegistroUltimaAtualizacao(
			RegistroEntrada registroUltimaAtualizacao) {
		this.registroUltimaAtualizacao = registroUltimaAtualizacao;
	}

	public Date getDataUltimaAtualizacao() {
		return dataUltimaAtualizacao;
	}

	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {
		this.dataUltimaAtualizacao = dataUltimaAtualizacao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
}
