package br.ufrn.sigaa.apedagogica.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.HashMap;
import java.util.Map;

import br.ufrn.sigaa.apedagogica.dominio.StatusParticipantesAtualizacaoPedagogica;

/**
 * Class que define os managed beans que acessam a busca dos participantes.
 * @author M�rio Rizzi
 *
 */
public class OperacaoParticipanteAtividadeAP {

	/** C�digo da opera��o para emiss�o do certificado de participa��o. */
	public static final int EMITI_CERTIFICADO_PARTICIPACAO = 1;
	
	/** Nome do Managed Bean respons�vel pela opera��o */
	private String mBean;
	/** Nome da opera��o (Para utiliza��o no t�tulo da p�gina de busca do discente) */
	private String nome;
	/** Possui os campos que n�o devem ser exibidos na consulta */
	private String[] camposEscondidos;
	/** Possui os campos que s�o obrigat�rio na consulta */
	private String[] camposObrigatorios;
	
	/** Status dos discentes v�lidos para a busca de discentes */
	private Integer[] statusValidos;
	
	/** HashMap com as Opera��es Dispon�veis */
	private static HashMap<Integer, OperacaoParticipanteAtividadeAP> mapa;
	static {
		mapa = new HashMap<Integer, OperacaoParticipanteAtividadeAP>();
			
		mapa.put(EMITI_CERTIFICADO_PARTICIPACAO, 
				new OperacaoParticipanteAtividadeAP("certificadoParticipacaoAP", 
						"Emitir Certificado de Participa��o em Atividade de Atualiza��o Pedag�gica",
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
	 * Retorna a opera��o que est� em uso.
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
	 * Contem um array com os campos que s�o obrigat�rios
	 * <br /> 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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

	/**
	 * Define os campos que s�o obrigat�rios
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
