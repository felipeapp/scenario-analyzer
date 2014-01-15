/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '24/08/2009'
 *
 */
package br.ufrn.sigaa.diploma.dominio;

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

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;

@Entity
@Table(schema = "diploma", name = "observacao_registro_diploma")
public class ObservacaoRegistroDiploma implements Validatable {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_observacao_registro_diploma")
	private int id;
	
	/** Data em que foi adicionado a observa��o ao registro do diploma. */
	@CriadoEm
	@Column(name="data_observacao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date adicionadoEm;
	
	/** Registro de diploma ao qual esta observa��o pertence. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registro_diploma")
	private RegistroDiploma registroDiploma;
	
	/** Observa��o adicionada ao registro de diploma. */
	private String observacao;
	
	/** Indica se esta observa��o � ativa ou n�o. */
	private boolean ativo;

	/** Construtor padr�o. */
	public ObservacaoRegistroDiploma() {
		ativo = true;
		adicionadoEm = new Date();
	}
	
	/** Construtor parametrizado.
	 * @param id Chave prim�ria
	 */
	public ObservacaoRegistroDiploma(int id) {
		this();
		this.id = id;
	}
	
	/** Construtor parametrizado.
	 * @param observacao Observa��o.
	 */
	public ObservacaoRegistroDiploma(String observacao) {
		this();
		this.observacao = observacao;
	}
	
	/** Valida os dados da observa��o: adicionadoEm, observacao, registroDiploma. */
	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(adicionadoEm, "Adicionado Em", lista);
		ValidatorUtil.validateRequired(registroDiploma, "Registro de Diploma", lista);
		ValidatorUtil.validateRequired(observacao, "Observa��o", lista);
		return lista;
	}

	
	/** Retorna a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna a data em que foi adicionado a observa��o ao registro do diploma.
	 * @return Data em que foi adicionado a observa��o ao registro do diploma. 
	 */
	public Date getAdicionadoEm() {
		return adicionadoEm;
	}

	/** Seta a data em que foi adicionado a observa��o ao registro do diploma. 
	 * @param adicionadoEm Data em que foi adicionado a observa��o ao registro do diploma. 
	 */
	public void setAdicionadoEm(Date adicionadoEm) {
		this.adicionadoEm = adicionadoEm;
	}

	/** Retorna o registro de diploma ao qual esta observa��o pertence.
	 * @return Registro de diploma ao qual esta observa��o pertence. 
	 */
	public RegistroDiploma getRegistroDiploma() {
		return registroDiploma;
	}

	/** Seta o registro de diploma ao qual esta observa��o pertence. 
	 * @param registroDiploma Registro de diploma ao qual esta observa��o pertence. 
	 */
	public void setRegistroDiploma(RegistroDiploma registroDiploma) {
		this.registroDiploma = registroDiploma;
	}

	/** Retorna a observa��o adicionada ao registro de diploma.
	 * @return Observa��o adicionada ao registro de diploma. 
	 */
	public String getObservacao() {
		return observacao;
	}

	/** Seta a observa��o adicionada ao registro de diploma. 
	 * @param observacao Observa��o adicionada ao registro de diploma. 
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	/** Indica se esta observa��o � ativa ou n�o. 
	 * @return True se esta observa��o � ativa ou n�o. 
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/** Seta se esta observa��o � ativa ou n�o. 
	 * @param ativo True, se esta observa��o � ativa ou n�o. 
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	/**
	 * Retorna uma representa��o textual desta observa��o no formado Adicionado
	 * em, seguido por dois pontos, seguido pela observa��o.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Formatador.getInstance().formatarData(adicionadoEm) + ": " + observacao;
	}

}
