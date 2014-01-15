/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 27/03/2009
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.util.ArrayList;
import java.util.List;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;

/**
 *
 *    Movimento com as informacoes para atualizar um titulo.
 *
 * @author jadson
 * @since 27/03/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoAtualizaTitulo extends AbstractMovimentoAdapter{

	/** O título que vai ser atualizado. */
	private TituloCatalografico titulo;
	
	/** Passa o arquivo digitalizado para substituir o arquivo existente */
	private UploadedFile arquivoObraDigitalizada = null;
	
	/**  indica se é para apagar o artigo anterior */
	private boolean apagarArquivoDigitalSalvo;
	
	/** o sistema está atualizando automaticamente o título */
	private boolean alteracaoAutomatica; 
	
	/**
	 *  Dao passado entre dois processadores.
	 * Usado porque percorrendo os campos do titulo nos dois processadores, ao abrir outra
	 * sessao ocorre o erro de associar 1 objeto a 2 sessões diferentes.
	 */
	private TituloCatalograficoDao tituloDao;
	
	/** Lista de classificações utilizadas no sistema guardadas em memória durante o processo de 
	 * classificação para não precisar consultar novamente no banco.*/
	private List<ClassificacaoBibliografica> classificacoesUtilizadas = new ArrayList<ClassificacaoBibliografica>();
	
	
	/**
	 *   Usado na atualização do título.
	 * 
	 * @param titulo
	 * @param validarCamposPadraoMarc
	 * @param arquivoObraDigitalizada
	 */
	public MovimentoAtualizaTitulo(TituloCatalografico titulo, UploadedFile arquivoObraDigitalizada
			, boolean apagarArquivoDigitalSalvo, boolean alteracaoAutomatica
			, List<ClassificacaoBibliografica> classificacoesUtilizadas){
		this.titulo = titulo;
		this.apagarArquivoDigitalSalvo = apagarArquivoDigitalSalvo;
		this.arquivoObraDigitalizada = arquivoObraDigitalizada;
		this.alteracaoAutomatica = alteracaoAutomatica;
		this.classificacoesUtilizadas = classificacoesUtilizadas;
	}
	
	
	public TituloCatalografico getTitulo() {
		return titulo;
	}


	public TituloCatalograficoDao getTituloDao() {
		return tituloDao;
	}

	public UploadedFile getArquivoObraDigitalizada() {
		return arquivoObraDigitalizada;
	}

	public boolean isApagarArquivoDigitalSalvo() {
		return apagarArquivoDigitalSalvo;
	}

	public boolean isAlteracaoAutomatica() {
		return alteracaoAutomatica;
	}
	
	public List<ClassificacaoBibliografica> getClassificacoesUtilizadas() {
		return classificacoesUtilizadas;
	}
	
	
}
