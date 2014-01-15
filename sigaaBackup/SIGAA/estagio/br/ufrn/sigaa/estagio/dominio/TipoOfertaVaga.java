/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/03/2013
 *
 */
package br.ufrn.sigaa.estagio.dominio;


/**
 * Enumera os tipos de oferta de convênio de estágio, definindo se serão
 * ofertados por uma empresa, pela coordenação do curso ou por ambos.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
public enum TipoOfertaVaga {
	// NÃO ALTERAR A ORDEM!!!
	/** Indica que o convênio de estágio poderá ser ofertado por empresas. */
	OFERTADO_POR_EMPRESA,
	/** Indica que o convênio de estágio poderá ser ofertado pela coordenação do curso. */
	OFERTADO_PELA_COORDENACAO,
	/** Indica que o convênio de estágio poderá ser ofertado por empresa ou pela coordenação do curso. */
	OFERTADO_POR_EMPRESA_COORDENACAO;

	/** Indica se este tipo de oferta é dado por empresas.
	 * @return
	 */
	public boolean isOfertadoEmpresa() {
		return this == OFERTADO_POR_EMPRESA || this == OFERTADO_POR_EMPRESA_COORDENACAO;
	}
	
	/** Indica se este tipo de oferta é dado pela coordenação do curso.
	 * @return
	 */
	public boolean isOfertadoCoordenacaoCurso() {
		return this == OFERTADO_PELA_COORDENACAO || this == OFERTADO_POR_EMPRESA_COORDENACAO;
	}
	
	/** Retorna uma representação textual do tipo de ofetra de vagas.
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		switch (this) {
			case OFERTADO_POR_EMPRESA : return "OFERTADO POR EMPRESA";
			case OFERTADO_PELA_COORDENACAO : return "OFERTADO PELA COORDENAÇÃO DO CURSO";
			case OFERTADO_POR_EMPRESA_COORDENACAO : return "OFERTADO POR EMPRESA OU PELA COORDENAÇÃO DO CURSO";
			default : return "";
		}
	}
	
}
