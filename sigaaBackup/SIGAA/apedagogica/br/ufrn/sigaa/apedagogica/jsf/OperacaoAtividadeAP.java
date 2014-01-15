package br.ufrn.sigaa.apedagogica.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.HashMap;
import java.util.Map;

/**
 * Class que define os managed beans que acessam a busca das atividades.
 * @author Mário Rizzi
 *
 */
public class OperacaoAtividadeAP {

	/** Código da operação para confirmar a inscrição de um participante. */
	public static final int ALTERA_SITUACAO_INSCRICAO_ATIVIDADE = 1;
	/** Código da operação geração da lista de presença. */
	public static final int GERA_LISTA_PRESENCA = 2;
	/** Código da operação geração da lista de presença. */
	public static final int NOTIFICAR_PARTICIPANTES = 3;
	
	/** Nome do Managed Bean responsável pela operação */
	private String mBean;
	/** Nome da operação (Para utilização no título da página de busca do discente) */
	private String nome;
	/** Possui os campos que não devem ser exibidos na consulta */
	private String[] camposEscondidos;
	
	/** Status dos discentes válidos para a busca de discentes */
	private Integer[] statusValidos;
	
	/** HashMap com as Operações Disponíveis */
	private static HashMap<Integer, OperacaoAtividadeAP> mapa;
	static {
		mapa = new HashMap<Integer, OperacaoAtividadeAP>();
		
		mapa.put(ALTERA_SITUACAO_INSCRICAO_ATIVIDADE, 
				new OperacaoAtividadeAP("alteraSituacaoInscricaoAP", 
						"Alteração da Situação da Inscrição", new Integer[]{},
						new String[]{}));
		
		mapa.put(GERA_LISTA_PRESENCA, 
				new OperacaoAtividadeAP("geracaoListaPresencaAP", 
						"Lista de Presença dos Participantes",new Integer[]{},
						new String[]{}));
		
		mapa.put(NOTIFICAR_PARTICIPANTES, 
				new OperacaoAtividadeAP("notificarParticipanteAP", 
						"Notificação de Participantes em Atividade",new Integer[]{},
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
	 * Retorna a operação que está em uso.
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
	 * Contem um array com os campos que não devem ser visualizados 
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Define os campos que não devem ser exibidos. 
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
