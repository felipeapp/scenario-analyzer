/*
 * MovimentoCatalogaTitulo.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.util.ArrayList;
import java.util.List;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;

/**
 *
 *       Movimento passado ao processador que vai catalogar ou salvar um título.
 *
 * @author jadson
 * @since 18/08/2008
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoCatalogaTitulo extends AbstractMovimentoAdapter{

	/** O título que vai ser catalogado*/
	private TituloCatalografico titulo;

	/** Passa o arquivo digitalizado para ser salvo no processador */
	private UploadedFile arquivoObraDigitalizada = null;
	
	/** Lista de classificações utilizadas no sistema guardadas em memória durante o processo de 
	 * classificação para não precisar consultar novamente no banco.*/
	private List<ClassificacaoBibliografica> classificacoesUtilizadas = new ArrayList<ClassificacaoBibliografica>();
	
	/**
	 *  O usuário vai finalizar a catalogação. então atribui o atributo catalogado para true e 
	 * validar se o título possui os campos obrigatórios
	 */
	private boolean finalizandoCatalogacao;
	
	
	
	
	/**
	 * Construtor padrão
	 * 
	 * @param titulo
	 * @param idLivro
	 * @param atualizarBaseSipac se precisa "sincronizar" as basses do sigaa com sipac ou não. 
	 *                 Essa "sincronização" só ocorre se o título catalogado veio de uma compra, a 
	 *                 maioria dos casos menos artigos de periódicos até agora. 
	 */
	public MovimentoCatalogaTitulo(TituloCatalografico titulo, UploadedFile arquivoObraDigitalizada
			, boolean finalizandoCatalogacao, List<ClassificacaoBibliografica> classificacoesUtilizadas){
		this.titulo = titulo;
		this.arquivoObraDigitalizada = arquivoObraDigitalizada;
		this.finalizandoCatalogacao = finalizandoCatalogacao;
		this.classificacoesUtilizadas = classificacoesUtilizadas;
	}
	
	
	
	public TituloCatalografico getTitulo() {
		return titulo;
	}
	
	public UploadedFile getArquivoObraDigitalizada() {
		return arquivoObraDigitalizada;
	}

	public boolean isFinalizandoCatalogacao() {
		return finalizandoCatalogacao;
	}

	public void setFinalizandoCatalogacao(boolean finalizandoCatalogacao) {
		this.finalizandoCatalogacao = finalizandoCatalogacao;
	}

	public List<ClassificacaoBibliografica> getClassificacoesUtilizadas() {
		return classificacoesUtilizadas;
	}
	
	
	
}
