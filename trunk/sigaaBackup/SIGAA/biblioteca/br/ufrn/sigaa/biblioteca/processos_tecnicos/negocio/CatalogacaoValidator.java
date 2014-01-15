/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 02/06/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoVariavel;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoMaterial;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;

/**
 *
 * <p> Interface das regras de valida��o de t�tulos e autoridade no sistema. </p>
 * 
 * <p> <i> Essa interface representa um ponto de extens�o para implementar regras de valiza��o diferentes das usadas na UFRN. 
 * Apesar de se ter procurado deixar as regras o mais gen�rico poss�vel, cada biblioteca tem liberdade administrativa
 * para inventar as regras que desejar. </i> </p>
 * 
 * @author jadson
 *
 */
public interface CatalogacaoValidator {

	
	
	/**
	 *     Verifica os campos obrigat�rios que uma cataloga��o de T�tulo deve possuir de acordo com o que for implementado.
	 *
	 * @param tituloCatalogafico
	 * @param listaErros
	 * @return
	 * @throws DAOException
	 */
	public ListaMensagens verificaCamposObrigatoriosTitulo(TituloCatalografico tituloCatalogafico, List<ClassificacaoBibliografica> classificacoesUtilizada, ListaMensagens listaErros) throws DAOException;
	
	
	/**
	 * 	   Verifica os campos obrigat�rios que uma cataloga��o de Autoridade deve possuir de acordo com o que for implementado.
	 *
	 * @param tituloCatalogafico
	 * @param listaErros
	 * @return
	 * @throws DAOException
	 */
	public  ListaMensagens verificaCamposObrigatoriosAutoridade(Autoridade autoridade,  ListaMensagens listaErros) throws DAOException;
	
	
	/**
	 *     <p>Verifica se j� existem outro T�tulo catalogado com as mesmas informa��o na base de acordo com o que for implementado.</p
	 *   
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	public ListaMensagens verificaExisteTituloIgual(TituloCatalografico tituloCatalogafico, ListaMensagens listaErros) throws DAOException;
	
	
	
	/** 
	 *    Verifica a exist�ncia de outra autoridade com e mesmo nome pessoal. N�o pode deixar a inser��o
	 * de autoridades duplicadas.
	 *
	 * @param autoridade
	 * @param listaErros
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public ListaMensagens verificaExiteAutoridadeIgual(Autoridade autoridade, ListaMensagens listaErros) throws DAOException;
	
	
	
	
	/**
	 *      M�todo que valida apenas 1 campo MARC. Usado nas telas de edi��o de campos de controle.
	 *
	 * @param campoVariavel
	 * @param formatoMaterial
	 * @return
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public ListaMensagens validaCampoMarcBibliografico(CampoVariavel campoVariavel, FormatoMaterial formatoMaterial) throws NegocioException, DAOException;
	
	
	/**
	 *      M�todo que valida apenas 1 campo MARC de autoridade. Usado nas telas de edi��o de 
	 *  campos de controle de autoridades.
	 *
	 * @param campoVariavel
	 * @param formatoMaterial
	 * @return
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public ListaMensagens validaCampoMarcAutoridade(CampoVariavel campoVariavel) throws NegocioException, DAOException;

	
	/**
	 * 
	 *  M�todo que valida as informa��es MARC de todos os campos do t�tulo passado.
	 *
	 * @return
	 * @throws NegocioException 
	 * @throws DAOException 
	 */
	public  ListaMensagens validaCamposMarcTitulo(TituloCatalografico titulo, FormatoMaterial formatoMaterial, ListaMensagens listaErros) throws DAOException, NegocioException;
	
	
	/**
	 * 
	 *   M�todo que valida as informa��es MARC de todos os campos da autoridade passada.
	 *
	 * @return
	 * @throws NegocioException 
	 * @throws DAOException 
	 */
	public ListaMensagens validaCamposMarcAutoridade(Autoridade autoridade, ListaMensagens listaErros) throws DAOException, NegocioException;
	
}
