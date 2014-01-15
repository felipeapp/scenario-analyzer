/*
 * MovimentoCatalogaTitulo.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
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
 *       Movimento passado ao processador que vai catalogar ou salvar um t�tulo.
 *
 * @author jadson
 * @since 18/08/2008
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoCatalogaTitulo extends AbstractMovimentoAdapter{

	/** O t�tulo que vai ser catalogado*/
	private TituloCatalografico titulo;

	/** Passa o arquivo digitalizado para ser salvo no processador */
	private UploadedFile arquivoObraDigitalizada = null;
	
	/** Lista de classifica��es utilizadas no sistema guardadas em mem�ria durante o processo de 
	 * classifica��o para n�o precisar consultar novamente no banco.*/
	private List<ClassificacaoBibliografica> classificacoesUtilizadas = new ArrayList<ClassificacaoBibliografica>();
	
	/**
	 *  O usu�rio vai finalizar a cataloga��o. ent�o atribui o atributo catalogado para true e 
	 * validar se o t�tulo possui os campos obrigat�rios
	 */
	private boolean finalizandoCatalogacao;
	
	
	
	
	/**
	 * Construtor padr�o
	 * 
	 * @param titulo
	 * @param idLivro
	 * @param atualizarBaseSipac se precisa "sincronizar" as basses do sigaa com sipac ou n�o. 
	 *                 Essa "sincroniza��o" s� ocorre se o t�tulo catalogado veio de uma compra, a 
	 *                 maioria dos casos menos artigos de peri�dicos at� agora. 
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
