package br.ufrn.sigaa.assistencia.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;

/**
 * Classe que cont�m as constantes de an�lise de uma bolsa.
 * 
 * @author Gleydson
 * 
 */
@Entity
@Table(name="situacao_bolsa_auxilio", schema="sae")
public class SituacaoBolsaAuxilio implements PersistDB {

	/**
	 * O aluno cadastrou a bolsa e aguarda uma an�lise do servi�o social.
	 */
	public static final int EM_ANALISE = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.EM_ANALISE );
	/**
	 * Aluno foi aprovado no question�rio social e ganhou a bolsa
	 */
	public static final int BOLSA_DEFERIDA_E_CONTEMPLADA = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.BOLSA_DEFERIDA_E_CONTEMPLADA );
	/**
	 * Aluno foi aprovado no question�rio social, mas est� na fila de espera por
	 * bolsa
	 */
	public static final int BOLSA_DEFERIDA_FILA_DE_ESPERA = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.BOLSA_DEFERIDA_FILA_DE_ESPERA );

	/**
	 * Bolsa n�o concedida
	 */
	public static final int BOLSA_INDEFERIDA = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.BOLSA_INDEFERIDA );

	/**
	 * Bolsa cancelada
	 */
	public static final int BOLSA_CANCELADA = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.BOLSA_CANCELADA );

	/**
	 * Bolsa de Auxilio Moradia
	 */
	public static final int BOLSA_AUXILIO_MORADIA = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.BOLSA_ALIMENTACAO_DEFERIDA_CONTEMP_BOLSA_MORADIA_EM_ESPERA );

	/**
	 * Bolsa de Finalizada
	 */
	public static final int BOLSA_FINALIZADA = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.BOLSA_FINALIZADA );
	
	/**
	 * Bolsa solicitada Atualiza��o
	 */
	public static final int BOLSA_SOLICITADA_RENOVACAO = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.BOLSA_SOLICITADA_RENOVACAO );


	/** Atributo identificador do objeto respons�vel pelo controle das poss�veis situa��es de bolsa aux�lio.*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_situacao_bolsa_auxilio")
	private int id;
	
	/** Denomina��o da situa��o de bolsa aux�lio*/
	private String denominacao;
	
	public SituacaoBolsaAuxilio() {
	}
	
	public SituacaoBolsaAuxilio(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}
	
	/** M�todo respons�vel por verificar se a situa��o da bolsa destina-se � aux�lio moradia.*/
	public boolean isAuxilioMoradia(){
		if (BOLSA_AUXILIO_MORADIA == id)
			return true;
		else 
			return false;
	}
	
	/** M�todo respons�vel por verificar se a situa��o da bolsa encontra-se conteplada.*/
	public boolean isContemplada() {
		if (BOLSA_DEFERIDA_E_CONTEMPLADA == id) 
			return true;
		else
			return false;
	}

}
