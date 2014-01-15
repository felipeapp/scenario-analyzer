package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

import java.util.Date;

/**
 * Guarda os dados das prorroga��es feitas
 * @author jadson
 *
 */
public class DadosProrrogacaoRelatorioOcorrenciaPerdaMaterial{
	
	/** a data inicial da prorroga��o */
	private Date dataIncial;
	
	/** a data final da prorroga��o */
	private Date dataFinal;
	
	/** quem fez a comunica��o de perda */
	private String usuarioCadastrouProrrogacao;
	
	/** a data que a comunica��o de perda foi realizada */
	private String justificacativa;

	public DadosProrrogacaoRelatorioOcorrenciaPerdaMaterial(Date dataIncial,
			Date dataFinal, String usuarioCadastrouProrrogacao,
			String justificacativa) {
		super();
		this.dataIncial = dataIncial;
		this.dataFinal = dataFinal;
		this.usuarioCadastrouProrrogacao = usuarioCadastrouProrrogacao;
		this.justificacativa = justificacativa;
	}

	public Date getDataIncial() {
		return dataIncial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public String getUsuarioCadastrouProrrogacao() {
		return usuarioCadastrouProrrogacao;
	}

	public String getJustificacativa() {
		return justificacativa;
	}
	
}
