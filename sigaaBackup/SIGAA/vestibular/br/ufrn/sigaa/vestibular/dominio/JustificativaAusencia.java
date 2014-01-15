/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/08/2009
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Classe que modela o conceito de justificativa de aus�ncia do fiscal na
 * reuni�o, ou na aplica��o.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Entity
@Table(schema = "vestibular", name = "justificativa_ausencia")
public class JustificativaAusencia implements Validatable, StatusJustificativaAusencia {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_justificativa", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Fiscal ao qual esta justificativa pertence.*/
	@ManyToOne
	@JoinColumn(name="id_fiscal")
	private Fiscal fiscal;
	
	/** Texto justificativo da aus�ncia do fiscal. */
	private String justificativa;
	
	/** ID do arquivo que comprova a justificativa do fiscal. */
	@Column(name = "id_arquivo")
	private Integer idArquivo;
	
	/** Status dessa justificativa (N�o Analisada, Deferida, Indeferida)*/
	private char status;
	
	/** Texto justificativo do motivo de indeferimento da aus�ncia. */
	@Column(name = "motivo_indeferimento")
	private String motivoIndeferimento;
	
	/** Registro de Entrada do usu�rio que alterou o status da justificativa. */
	@ManyToOne
	@JoinColumn(name = "id_entrada")
	private RegistroEntrada registroEntrada;
	
	/** Observa��es anotadas pelo coordenador de fiscais sobre a justificativa de aus�ncia. */
	private String observacoes;
	
	/** Construtor padr�o. */
	public JustificativaAusencia() {
		setStatus(StatusJustificativaAusencia.NAO_ANALISADO);
	}
	
	/** Construtor parametrizado.
	 * @param id
	 */
	public JustificativaAusencia(int id) {
		super();
		this.id = id;
	}

	/** Valida os dados:
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(fiscal, "Fiscal", lista);
		ValidatorUtil.validateRequired(justificativa, "Justificativa", lista);
		ValidatorUtil.validateRequired(status, "Status", lista);
		if (isIndeferido())
			ValidatorUtil.validateRequired(motivoIndeferimento, "Motivo de Indeferimento", lista);
		return lista;
	}

	/** Retorna a chave prim�ria
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o fiscal ao qual esta justificativa pertence.
	 * @return Fiscal ao qual esta justificativa pertence.
	 */
	public Fiscal getFiscal() {
		return fiscal;
	}

	/** Seta o fiscal ao qual esta justificativa pertence.
	 * @param fiscal Fiscal ao qual esta justificativa pertence.
	 */
	public void setFiscal(Fiscal fiscal) {
		this.fiscal = fiscal;
	}

	/** Retorna o texto justificativo da aus�ncia do fiscal. 
	 * @return Texto justificativo da aus�ncia do fiscal. 
	 */
	public String getJustificativa() {
		return justificativa;
	}

	/** Seta o texto justificativo da aus�ncia do fiscal. 
	 * @param justificativa Texto justificativo da aus�ncia do fiscal. 
	 */
	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	/** Retorna o ID do arquivo que comprova a justificativa do fiscal. 
	 * @return ID do arquivo que comprova a justificativa do fiscal. 
	 */
	public Integer getIdArquivo() {
		return idArquivo;
	}

	/** Seta o ID do arquivo que comprova a justificativa do fiscal. 
	 * @param idArquivo ID do arquivo que comprova a justificativa do fiscal. 
	 */
	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	/** Retorna o status dessa justificativa (N�o Analisada, Deferida, Indeferida).
	 * @return Status dessa justificativa (N�o Analisada, Deferida, Indeferida)
	 */
	public char getStatus() {
		return status;
	}

	/** Seta o status dessa justificativa (N�o Analisada, Deferida, Indeferida).
	 * @param status Status dessa justificativa (N�o Analisada, Deferida, Indeferida)
	 */
	public void setStatus(char status) {
		this.status = status;
	}

	/** Retorna o Registro de Entrada do usu�rio que alterou o status da justificativa. 
	 * @return Registro de Entrada do usu�rio que alterou o status da justificativa. 
	 */
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	/** Seta o Registro de Entrada do usu�rio que alterou o status da justificativa. 
	 * @param registroEntrada Registro de Entrada do usu�rio que alterou o status da justificativa. 
	 */
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	/** Retorna o texto justificativo do motivo de indeferimento da aus�ncia.
	 * @return Texto justificativo do motivo de indeferimento da aus�ncia. 
	 */
	public String getMotivoIndeferimento() {
		return motivoIndeferimento;
	}

	/** Seta o texto justificativo do motivo de indeferimento da aus�ncia. 
	 * @param motivoIndeferimento Texto justificativo do motivo de indeferimento da aus�ncia. 
	 */
	public void setMotivoIndeferimento(String motivoIndeferimento) {
		this.motivoIndeferimento = motivoIndeferimento;
	}
	
	/** Retorna uma descri��o textual do status da justificativa;
	 * @return
	 */
	public String getDescricaoStatus() {
		switch (status) {
		case NAO_ANALISADO: return "N�o Analisado";
		case DEFERIDO: return "Deferido";
		case INDEFERIDO: return "Indeferido";
		case EM_ANALISE: return "Em An�lise";
		default:return null;
		}
	}
	
	
	/** Indica se a justificativa foi indeferida.
	 * @return
	 */
	public boolean isDeferido () {
		return status == DEFERIDO;
	}
	
	/** Indica se a justificativa foi deferida.
	 * @return
	 */
	public boolean isIndeferido () {
		return status == INDEFERIDO;
	}
	
	/** Indica se a justificativa n�o foi analisada.
	 * @return
	 */
	public boolean isNaoAnalisado () {
		return status == NAO_ANALISADO;
	}

	/** Retorna uma representa��o textual da justificativa da aus�ncia do fiscal, no formato:
	 * matricula ou siape do fiscal, seguido do nome do fiscal, seguido de dois pontos, seguido da justificativa.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (fiscal.getDiscente() != null) {
			return fiscal.getDiscente().getMatriculaNome() + ": " + justificativa; 
		} else {
			return fiscal.getServidor().getSiapeNome() + ": " + justificativa;
		}
	}

	/** Retorna as observa��es anotadas pelo coordenador de fiscais sobre a justificativa de aus�ncia.
	 * @return Observa��es anotadas pelo coordenador de fiscais sobre a justificativa de aus�ncia. 
	 */
	public String getObservacoes() {
		return observacoes;
	}

	/** Seta as observa��es anotadas pelo coordenador de fiscais sobre a justificativa de aus�ncia. 
	 * @param observacoes Observa��es anotadas pelo coordenador de fiscais sobre a justificativa de aus�ncia. 
	 */
	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

}
