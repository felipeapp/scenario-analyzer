package br.ufrn.sigaa.biblioteca.integracao.dtos.inventario_acervo;

import java.io.Serializable;

/**
 * Contem as informacoes relativas a instituicao para utilizacao na inicializacao do coletor.
 * 
 * <strong>OBSERVACAO 1 .:</strong>   A VM do coletor trabalha na versao 1.3 do Java.
 * <strong>OBSERVACAO 2 .:</strong>   Nao usar acentos nos comentarios porque essas classes sao copiadas para o 
 * netbens e dar erro de codificacao se usar.
 * 
 * @author Felipe
 *
 */
public class ParametrosRetornoExecutarColetorDto implements Serializable {
	/** Usado para parametrizar as informacoes institucionais */ 
	public String instituicao;
	/** Usado para parametrizar as informacoes institucionais */ 
	public String siglaSistema;
	/** Usado para parametrizar as informacoes institucionais */
	public String nomeSistema;
	/** Usado para parametrizar as informacoes institucionais */
	public String mensagemSobre;
}
