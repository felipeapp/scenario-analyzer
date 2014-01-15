package br.ufrn.sigaa.apedagogica.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.HashMap;
import java.util.Map;

import br.ufrn.sigaa.apedagogica.dominio.StatusParticipantesAtualizacaoPedagogica;

/**
 * Class que define os managed beans que acessam a busca dos participantes.
 * @author Mário Rizzi
 *
 */
public class OperacaoParticipanteAtividadeAP {

	/** Código da operação para emissão do certificado de participação. */
	public static final int EMITI_CERTIFICADO_PARTICIPACAO = 1;
	
	/** Nome do Managed Bean responsável pela operação */
	private String mBean;
	/** Nome da operação (Para utilização no título da página de busca do discente) */
	private String nome;
	/** Possui os campos que não devem ser exibidos na consulta */
	private String[] camposEscondidos;
	/** Possui os campos que são obrigatório na consulta */
	private String[] camposObrigatorios;
	
	/** Status dos discentes válidos para a busca de discentes */
	private Integer[] statusValidos;
	
	/** HashMap com as Operações Disponíveis */
	private static HashMap<Integer, OperacaoParticipanteAtividadeAP> mapa;
	static {
		mapa = new HashMap<Integer, OperacaoParticipanteAtividadeAP>();
			
		mapa.put(EMITI_CERTIFICADO_PARTICIPACAO, 
				new OperacaoParticipanteAtividadeAP("certificadoParticipacaoAP", 
						"Emitir Certificado de Participação em Atividade de Atualização Pedagógica",
						new Integer[] {StatusParticipantesAtualizacaoPedagogica.CONCLUIDO.getId()},
						new String[]{"filtroSituacao","imprimir"},new String[]{}));
	}
	

	public OperacaoParticipanteAtividadeAP() {
	}

	public OperacaoParticipanteAtividadeAP(String mBean, String nome, Integer[] statusValidos) {
		this.mBean = mBean;
		this.nome = nome;
		this.statusValidos  = statusValidos;
	}
	
	public OperacaoParticipanteAtividadeAP(String mBean, String nome, Integer[] statusValidos, String[] camposEscondidos) {
		this.mBean = mBean;
		this.nome = nome;
		this.statusValidos  = statusValidos;
		this.camposEscondidos = camposEscondidos;
	}
	
	public OperacaoParticipanteAtividadeAP(String mBean, String nome, Integer[] statusValidos, String[] camposEscondidos, String[] camposObrigatorios) {
		this.mBean = mBean;
		this.nome = nome;
		this.statusValidos  = statusValidos;
		this.camposEscondidos = camposEscondidos;
		this.camposObrigatorios = camposObrigatorios;
	}

	/**
	 * Retorna a operação que está em uso.
	 * @param codigoOperacao
	 * @return
	 */
	public static OperacaoParticipanteAtividadeAP getOperacao(int codigoOperacao) {
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
	 * Contem um array com os campos que são obrigatórios
	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/ConsultaParticipanteAPedagogica/busca_participante.jsp</li>
 	 * </ul>
	 * @return
	 */
	public String[] getCamposObrigatorios() {
		return camposObrigatorios;
	}

	public void setCamposObrigatorios(String[] camposObrigatorios) {
		this.camposObrigatorios = camposObrigatorios;
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

	/**
	 * Define os campos que são obrigatórios
	 * @return
	 */
	public Map<String, Boolean> getMapaCamposObrigatorios(){
		Map<String, Boolean> mapaCamposObrigatorios = new HashMap<String, Boolean>();
		
		if(!isEmpty(getCamposObrigatorios())){
			mapaCamposObrigatorios = new HashMap<String, Boolean>();
			for (String cp : getCamposObrigatorios()) {
				mapaCamposObrigatorios.put(cp, true);
			}
		}
		return mapaCamposObrigatorios;
	}
	

}
