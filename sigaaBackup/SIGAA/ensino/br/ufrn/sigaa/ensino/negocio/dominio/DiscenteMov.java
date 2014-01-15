/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.apache.struts.upload.FormFile;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.assistencia.dominio.SituacaoSocioEconomicaDiscente;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;

/**
 * Movimento utilizado para cadastro e atualização de dados dos discentes da instituição
 * 
 * @author Andre M Dantas
 *
 */
public class DiscenteMov extends MovimentoCadastro {

	private boolean discenteAntigo;
	
	/** Arquivo digitalizado do histórico do discente antigo utilizado no cadastro em JSF */
	private UploadedFile arquivo;
	
	/** Arquivo digitalizado do histórico do discente antigo utilizado no cadastro em Struts */
	private FormFile file;
	
	private SituacaoSocioEconomicaDiscente situacaoSocioEconomica;

	public DiscenteMov() {
	}

	public DiscenteMov(Comando com, DiscenteAdapter discente) {
		setCodMovimento(com);
		setObjMovimentado(discente);
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	@Override
	public int getId() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	@Override
	public void setId(int id) {
	}

	public boolean isDiscenteAntigo() {
		return this.discenteAntigo;
	}

	public void setDiscenteAntigo(boolean discenteAntigo) {
		this.discenteAntigo = discenteAntigo;
	}

	public SituacaoSocioEconomicaDiscente getSituacaoSocioEconomica() {
		return situacaoSocioEconomica;
	}

	public void setSituacaoSocioEconomica(
			SituacaoSocioEconomicaDiscente situacaoSocioEconomica) {
		this.situacaoSocioEconomica = situacaoSocioEconomica;
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}
	
}
