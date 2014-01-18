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
 * Classe que contém as constantes de análise de uma bolsa.
 * 
 * @author Gleydson
 * 
 */
@Entity
@Table(name="situacao_bolsa_auxilio", schema="sae")
public class SituacaoBolsaAuxilio implements PersistDB {

	/**
	 * O aluno cadastrou a bolsa e aguarda uma análise do serviço social.
	 */
	public static final int EM_ANALISE = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.EM_ANALISE );
	/**
	 * Aluno foi aprovado no questionário social e ganhou a bolsa
	 */
	public static final int BOLSA_DEFERIDA_E_CONTEMPLADA = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.BOLSA_DEFERIDA_E_CONTEMPLADA );
	/**
	 * Aluno foi aprovado no questionário social, mas está na fila de espera por
	 * bolsa
	 */
	public static final int BOLSA_DEFERIDA_FILA_DE_ESPERA = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.BOLSA_DEFERIDA_FILA_DE_ESPERA );

	/**
	 * Bolsa não concedida
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
	 * Bolsa solicitada Atualização
	 */
	public static final int BOLSA_SOLICITADA_RENOVACAO = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.BOLSA_SOLICITADA_RENOVACAO );


	/** Atributo identificador do objeto responsável pelo controle das possíveis situações de bolsa auxílio.*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_situacao_bolsa_auxilio")
	private int id;
	
	/** Denominação da situação de bolsa auxílio*/
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
	
	/** Método responsável por verificar se a situação da bolsa destina-se à auxílio moradia.*/
	public boolean isAuxilioMoradia(){
		if (BOLSA_AUXILIO_MORADIA == id)
			return true;
		else 
			return false;
	}
	
	/** Método responsável por verificar se a situação da bolsa encontra-se conteplada.*/
	public boolean isContemplada() {
		if (BOLSA_DEFERIDA_E_CONTEMPLADA == id) 
			return true;
		else
			return false;
	}

}
