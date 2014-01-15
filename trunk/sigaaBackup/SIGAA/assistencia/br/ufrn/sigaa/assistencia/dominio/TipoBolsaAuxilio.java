package br.ufrn.sigaa.assistencia.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;

/**
 * A Secretaria de Apóio ao Estudante definiu os seguintes tipos de Bolsa Auxílio que podem ser concedidas aos 
 * discentes da instituição:
 * 
 * Bolsa Auxílio de Residência (para Graduação de Pós Graduação)
 * Bolsa Auxílio Alimentação 
 * Bolsa Auxílio Transporte
 * 
 * @author Gleydson
 * @author agostinho campos
 *
 */
@Entity
@Table(name="tipo_bolsa_auxilio", schema="sae")
public class TipoBolsaAuxilio implements PersistDB {

	/** Armazena o valor da bolsa de Residência de Graduação */
	public static final int RESIDENCIA_GRADUACAO = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.RESIDENCIA_GRADUACAO );
	/** Armazena o valor da bolsa de Residência de Pós Graduação */
	public static final int RESIDENCIA_POS = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.RESIDENCIA_POS  );
	/** Armazena o valor da bolsa de Alimentação */
	public static final int ALIMENTACAO = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.ALIMENTACAO  );
	/** Armazena o valor da bolsa de Transporte */
	public static final int TRANSPORTE = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.TRANSPORTE  );
	/** Armazena o valor da bolsa do PROMISAES */
	public static final int PROMISAES = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.PROMISAES  );
	/** Armazena o valor da bolsa da CRECHE */
	public static final int CRECHE = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.CRECHE );
	/** Armazena o valor da bolsa da ATLETA */
	public static final int ATLETA = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.ATLETA );
	/** Armazena o valor da bolsa da Óculos */
	public static final int OCULOS = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.OCULOS );
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_tipo_bolsa_auxilio")
	private int id;

	/** Descrição do tipo de Bolsa */
	private String denominacao;

	@Transient
	private SituacaoBolsaAuxilio situacao;
	
	public TipoBolsaAuxilio() {
		// TODO Auto-generated constructor stub
	}

	public TipoBolsaAuxilio( int id ) {
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
	
	public boolean isResidenciaGraduacao() {
		return id == RESIDENCIA_GRADUACAO;
	}
	
	public boolean isResidenciaPos() {
		return id == RESIDENCIA_POS;
	}
	
	public boolean isAlimentacao() {
		return id == ALIMENTACAO;
	}
	
	public boolean isTransporte() {
		return id == TRANSPORTE;		
	}
	
	public boolean isPromisaes() {
		return id == PROMISAES;		
	}

	public boolean isCreche() {
		return id == CRECHE;		
	}
	
	public boolean isAtleta() {
		return id == ATLETA;		
	}

	public boolean isOculos() {
		return id == OCULOS;		
	}
	
	public boolean isBolsaAuxilio() {
		return id == ALIMENTACAO || id == RESIDENCIA_GRADUACAO || id == RESIDENCIA_POS;		
	}

	public static int[] getTiposResidencia() {
		return new int[] {RESIDENCIA_GRADUACAO, RESIDENCIA_POS};
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((denominacao == null) ? 0 : denominacao.hashCode());
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TipoBolsaAuxilio other = (TipoBolsaAuxilio) obj;
		if (denominacao == null) {
			if (other.denominacao != null)
				return false;
		} else if (!denominacao.equals(other.denominacao))
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	/**
	 * Verifica se o tipo da bolsa informada foi criada especificamente pelo SIPAC 
	 * @param idTipoBolsa
	 * @return
	 */
	public static boolean isTipoBolsaSIPAC(int idTipoBolsa) {
		int[] bolsasEspecifcasSIPAC = ParametroHelper.getInstance().getParametroIntArray(ConstantesParametro.BOLSAS_ESPECIFICAS_SIPAC);
		
		for (int id : bolsasEspecifcasSIPAC) {
			if (id == idTipoBolsa)
				return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "TipoBolsaAuxilio [denominacao=" + denominacao + ", id=" + id
				+ "]";
	}

	public SituacaoBolsaAuxilio getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoBolsaAuxilio situacao) {
		this.situacao = situacao;
	}

}