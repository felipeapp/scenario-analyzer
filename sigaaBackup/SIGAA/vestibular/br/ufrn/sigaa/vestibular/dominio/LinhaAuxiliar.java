package br.ufrn.sigaa.vestibular.dominio;

import java.util.ArrayList;
import java.util.List;

/**
 * Linha Auxiliar para a preenchimento da documentação dos discente convocados.
 *  
 * @author guerethes
 */
public class LinhaAuxiliar {

	/**
	 * Atributo que armazena todos as informações necessárias para o impressão dos documentos dos aprovados.
	 */
	private List<LinhaImpressaoDocumentosConvocados> linha = new ArrayList<LinhaImpressaoDocumentosConvocados>();

	
	public List<LinhaImpressaoDocumentosConvocados> getLinha() {
		return linha;
	}

	public void setLinha(List<LinhaImpressaoDocumentosConvocados> linha) {
		this.linha = linha;
	}

}