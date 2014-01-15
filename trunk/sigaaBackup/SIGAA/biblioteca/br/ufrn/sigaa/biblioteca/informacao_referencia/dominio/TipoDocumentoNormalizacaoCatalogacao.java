/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Oct 7, 2008
 *
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 *	Esta entidade representa um tipo de documento que pode ser 
 * solicitado por normalização e/ou catalogação na biblioteca
 * Pode ser: Artigo para publicar em revista da UFRN,artigo para publicar em revista de 0utra editora, 
 * tese/dissertação defendida na UFRN, livro, folheto, separata etc. para publicar na UFRN, livro, folheto, 
 * separata etc. para publicar em outra editora, Monografia apresentada a UFRN, outros.
 * @author Victor Hugo
 */
@Entity
@Table(name = "tipo_documento_normalizacao_catalogacao", schema = "biblioteca")
public class TipoDocumentoNormalizacaoCatalogacao implements Validatable {
	
	/**
	 * Id do tipo de documento
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.resgistro_extras_sequence") })
	@Column(name = "id_tipo_documento_normalizacao_catalogacao", nullable = false)
	private int id;
	
	/**
	 * Descrição do tipo de documento
	 */
	@Column(name="denominacao")
	private String denominacao;

	/** O tipo de documentos outro é fixo do sistema não pode ser alterado pelo usuário*/
	private boolean editavel = true;
	
	/** Quando um tipo de documento é removido pelo usuário ele é desativado no sistema */
	private boolean ativo = true;
	
	
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
	
	public TipoDocumentoNormalizacaoCatalogacao() {
		
	}
	
	public TipoDocumentoNormalizacaoCatalogacao(int id) {
		this.id = id;
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();

		ValidatorUtil.validateRequired(denominacao, "Denominação", erros);
		
		return erros;
	}
	
	/**
	 *   Verifica se o usuário escolheu o tipo de documento "OUTRO" que precisa ser fixo no sistema.
	 *
	 * @return
	 */
	public boolean isTipoDocumentoOutro(){
		return (this.id == getIdTipoDocumentoOutro());
	}
	
	/** Retorna o id do Tipo de documento "outro".  */
	public static int getIdTipoDocumentoOutro(){
		return ParametroHelper.getInstance().getParametroInt(
				ParametrosBiblioteca.SOLICITACAO_NORMALIZACAO_CATALOGACAO_FONTE_TIPO_DOCUMENTO_OUTROS);
	}

	public boolean isEditavel() {
		return editavel;
	}

	public void setEditavel(boolean editavel) {
		this.editavel = editavel;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
}
