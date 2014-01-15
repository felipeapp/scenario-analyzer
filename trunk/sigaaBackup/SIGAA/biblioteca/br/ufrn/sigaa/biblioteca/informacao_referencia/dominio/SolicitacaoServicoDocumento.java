/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Sep 19, 2008
 *
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.dominio;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;

/**
 *	<p>
 *		Representa a abstração de uma solicitação de serviço prestado pela biblioteca ao usuário 
 *		que necessita da apresentação de um documento.
 *	</p>
 *
 *	@author Felipe Rivas
 */
@MappedSuperclass
public abstract class SolicitacaoServicoDocumento extends SolicitacaoServico {
	
	/** Tipo do documento que será catalogado/normalizado. Ex: monografia, tese, etc. */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_documento", nullable = false)
	private TipoDocumentoNormalizacaoCatalogacao tipoDocumento;

	/** Será utilizado caso o tipo de documento for diferentes dos cadastrados.*/
	@Column(name="outro_tipo_documento")
	private String outroTipoDocumento;

	/** Id do arquivo do trabalho na base de arquivos.*/
	@Column(name="id_trabalho_digitalizado")
	private int idTrabalhoDigitalizado;

	/**
	 * Diz se a biblioteca pode descartar o material entregue após realizada a catalogação;
	 * OBS. Todo material é descartado após 3 dias, caso o mesmo não seja recolhido.
	 */
	@Column(name="autoriza_descarte")
	private boolean autorizaDescarte = false;


	/////////////////////////// sets e gets ////////////////////////////
	
	public TipoDocumentoNormalizacaoCatalogacao getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(TipoDocumentoNormalizacaoCatalogacao tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public boolean isAutorizaDescarte() {
		return autorizaDescarte;
	}

	public void setAutorizaDescarte(boolean autorizaDescarte) {
		this.autorizaDescarte = autorizaDescarte;
	}
	
	public String getOutroTipoDocumento() {
		return outroTipoDocumento;
	}

	public void setOutroTipoDocumento(String outroTipoDocumento) {
		this.outroTipoDocumento = outroTipoDocumento;
	}
	
	/**
	 * Valida o preenchimento dos campos do objeto.
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = super.validate();
		
		ValidatorUtil.validateRequired(tipoDocumento, "Tipo de obra", erros);

		if ( tipoDocumento.isTipoDocumentoOutro() )
			ValidatorUtil.validateRequired(outroTipoDocumento, "Especifique o Tipo de Obra", erros);
		
		return erros;
	}

	public int getIdTrabalhoDigitalizado() {
		return idTrabalhoDigitalizado;
	}

	public void setIdTrabalhoDigitalizado(int idTrabalhoDigitalizado) {
		this.idTrabalhoDigitalizado = idTrabalhoDigitalizado;
	}
	
}