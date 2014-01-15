/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 09/12/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.vestibular.negocio;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.vestibular.dominio.Inscrito;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/**
 * @author Rafael Gomes
 *
 */
public class MovimentoImportacaoDadosProcessoSeletivo extends AbstractMovimentoAdapter {

	/** xml de Importação das informações dos candidatos do Processo Seletivo. */
	private UploadedFile xml;
	
	/** Processo Seletivo. */
	private ProcessoSeletivoVestibular processoSeletivo;
	
	private Inscrito inscrito;

	/**
	 * @return the xml
	 */
	public UploadedFile getXml() {
		return xml;
	}

	/**
	 * @param xml the xml to set
	 */
	public void setXml(UploadedFile xml) {
		this.xml = xml;
	}

	/**
	 * @return the processoSeletivo
	 */
	public ProcessoSeletivoVestibular getProcessoSeletivo() {
		return processoSeletivo;
	}

	/**
	 * @param processoSeletivo the processoSeletivo to set
	 */
	public void setProcessoSeletivo(ProcessoSeletivoVestibular processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	/**
	 * @return the inscrito
	 */
	public Inscrito getInscrito() {
		return inscrito;
	}

	/**
	 * @param inscrito the inscrito to set
	 */
	public void setInscrito(Inscrito inscrito) {
		this.inscrito = inscrito;
	}
	
	
	
}
