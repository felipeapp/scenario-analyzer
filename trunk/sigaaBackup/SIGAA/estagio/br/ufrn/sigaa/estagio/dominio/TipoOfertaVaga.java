/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/03/2013
 *
 */
package br.ufrn.sigaa.estagio.dominio;


/**
 * Enumera os tipos de oferta de conv�nio de est�gio, definindo se ser�o
 * ofertados por uma empresa, pela coordena��o do curso ou por ambos.
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
public enum TipoOfertaVaga {
	// N�O ALTERAR A ORDEM!!!
	/** Indica que o conv�nio de est�gio poder� ser ofertado por empresas. */
	OFERTADO_POR_EMPRESA,
	/** Indica que o conv�nio de est�gio poder� ser ofertado pela coordena��o do curso. */
	OFERTADO_PELA_COORDENACAO,
	/** Indica que o conv�nio de est�gio poder� ser ofertado por empresa ou pela coordena��o do curso. */
	OFERTADO_POR_EMPRESA_COORDENACAO;

	/** Indica se este tipo de oferta � dado por empresas.
	 * @return
	 */
	public boolean isOfertadoEmpresa() {
		return this == OFERTADO_POR_EMPRESA || this == OFERTADO_POR_EMPRESA_COORDENACAO;
	}
	
	/** Indica se este tipo de oferta � dado pela coordena��o do curso.
	 * @return
	 */
	public boolean isOfertadoCoordenacaoCurso() {
		return this == OFERTADO_PELA_COORDENACAO || this == OFERTADO_POR_EMPRESA_COORDENACAO;
	}
	
	/** Retorna uma representa��o textual do tipo de ofetra de vagas.
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		switch (this) {
			case OFERTADO_POR_EMPRESA : return "OFERTADO POR EMPRESA";
			case OFERTADO_PELA_COORDENACAO : return "OFERTADO PELA COORDENA��O DO CURSO";
			case OFERTADO_POR_EMPRESA_COORDENACAO : return "OFERTADO POR EMPRESA OU PELA COORDENA��O DO CURSO";
			default : return "";
		}
	}
	
}
