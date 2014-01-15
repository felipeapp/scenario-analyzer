package br.ufrn.sigaa.apedagogica.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.HashMap;
import java.util.Map;

/**
 * Class que define os managed beans que acessam a busca das atividades.
 * @author M�rio Rizzi
 *
 */
public class OperacaoAtividadeAP {

	/** C�digo da opera��o para confirmar a inscri��o de um participante. */
	public static final int ALTERA_SITUACAO_INSCRICAO_ATIVIDADE = 1;
	/** C�digo da opera��o gera��o da lista de presen�a. */
	public static final int GERA_LISTA_PRESENCA = 2;
	/** C�digo da opera��o gera��o da lista de presen�a. */
	public static final int NOTIFICAR_PARTICIPANTES = 3;
	
	/** Nome do Managed Bean respons�vel pela opera��o */
	private String mBean;
	/** Nome da opera��o (Para utiliza��o no t�tulo da p�gina de busca do discente) */
	private String nome;
	/** Possui os campos que n�o devem ser exibidos na consulta */
	private String[] camposEscondidos;
	
	/** Status dos discentes v�lidos para a busca de discentes */
	private Integer[] statusValidos;
	
	/** HashMap com as Opera��es Dispon�veis */
	private static HashMap<Integer, OperacaoAtividadeAP> mapa;
	static {
		mapa = new HashMap<Integer, OperacaoAtividadeAP>();
		
		mapa.put(ALTERA_SITUACAO_INSCRICAO_ATIVIDADE, 
				new OperacaoAtividadeAP("alteraSituacaoInscricaoAP", 
						"Altera��o da Situa��o da Inscri��o", new Integer[]{},
						new String[]{}));
		
		mapa.put(GERA_LISTA_PRESENCA, 
				new OperacaoAtividadeAP("geracaoListaPresencaAP", 
						"Lista de Presen�a dos Participantes",new Integer[]{},
						new String[]{}));
		
		mapa.put(NOTIFICAR_PARTICIPANTES, 
				new OperacaoAtividadeAP("notificarParticipanteAP", 
						"Notifica��o de Participantes em Atividade",new Integer[]{},
						new String[]{}));
	
	}

	public OperacaoAtividadeAP() {
	}

	public OperacaoAtividadeAP(String mBean, String nome, Integer[] statusValidos) {
		this.mBean = mBean;
		this.nome = nome;
		this.statusValidos  = statusValidos;
	}
	
	public OperacaoAtividadeAP(String mBean, String nome, Integer[] statusValidos, String[] camposEscondidos) {
		this.mBean = mBean;
		this.nome = nome;
		this.statusValidos  = statusValidos;
		this.camposEscondidos = camposEscondidos;
	}

	/**
	 * Retorna a opera��o que est� em uso.
	 * @param codigoOperacao
	 * @return
	 */
	public static OperacaoAtividadeAP getOperacao(int codigoOperacao) {
		return mapa.get(codigoOperacao);
	}

	public String getMBean() {
		return mBean;
	}

	public String getNome() {
		return nome;
	}

	public void setMBean(String bean) {
		mBean = bean;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer[] getStatusValidos() {
		return statusValidos;
	}

	public void setStatusValidos(Integer[] statusValidos) {
		this.statusValidos = statusValidos;
	}

	/**
	 * Contem um array com os campos que n�o devem ser visualizados 
	 * <br /> 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/ConsultaParticipanteAPedagogica/busca_participante.jsp</li>
 	 * </ul>
	 * @return
	 */
	public String[] getCamposEscondidos() {
		return camposEscondidos;
	}

	public void setCamposEscondidos(String[] camposEscondidos) {
		this.camposEscondidos = camposEscondidos;
	}
	
	/**
	 * Define os campos que n�o devem ser exibidos. 
	 * @return
	 */
	public Map<String, Boolean> getMapaCamposEscondidos(){
		Map<String, Boolean> mapaCamposEscondidos = new HashMap<String, Boolean>();
		
		if(!isEmpty(getCamposEscondidos())){
			mapaCamposEscondidos = new HashMap<String, Boolean>();
			for (String cp : getCamposEscondidos()) {
				mapaCamposEscondidos.put(cp, true);
			}
		}
		return mapaCamposEscondidos;
	}
	

}
