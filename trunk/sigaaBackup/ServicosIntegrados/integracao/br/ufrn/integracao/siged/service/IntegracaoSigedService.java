/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Data de Criação: 16/12/2009
 */
package br.ufrn.integracao.siged.service;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.integracao.dto.UsuarioDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import br.ufrn.integracao.siged.dto.ArquivoDocumento;
import br.ufrn.integracao.siged.dto.DescritorDTO;
import br.ufrn.integracao.siged.dto.DocumentoDTO;
import br.ufrn.integracao.siged.dto.LinkDocumentoDTO;
import br.ufrn.integracao.siged.dto.PastaDocumentoDTO;
import br.ufrn.integracao.siged.dto.ResultadoBuscaDTO;

/**
 * Interface que define os métodos para integração com o SIGED. 
 * 
 * @author David Pereira
 *
 */
@WebService
@Transactional(propagation=Propagation.REQUIRED)
public interface IntegracaoSigedService {

	/**
	 * Busca os descritores associados a um determinado tipo de documento
	 * @param tipoDocumento
	 * @return
	 */
	public List<DescritorDTO> buscarDescritores(int tipoDocumento);
	
	
	/**
	 * Busca uma pasta de documentos pelo nome (exact match) e pela pasta pai.
	 * @param nome
	 * @param pai
	 * @return
	 */
	public PastaDocumentoDTO buscarPasta(String nome, Integer pai);
	
	/**
	 * Busca uma pasta de documentos pelo label (exact match) e pela pasta pai.
	 * @param label
	 * @param pai
	 * @return
	 */
	public PastaDocumentoDTO buscarPastaPorLabel(String label, Integer pai);
	
	/**
	 * Cadastra uma pasta de documentos passando como parâmetros o nome
	 * da pasta e a pasta pai. 
	 * 
	 * @param pasta
	 * @param usuario
	 * @return
	 * @throws NegocioRemotoException 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public PastaDocumentoDTO cadastrarPasta(PastaDocumentoDTO pasta, UsuarioDTO usuario) throws NegocioRemotoException; 
	
	/**
	 * Cadastra um link para um documento passando como parâmetros o 
	 * documento de destino do link. 
	 * 
	 * @param pasta
	 * @param usuario
	 * @return
	 * @throws NegocioRemotoException 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public LinkDocumentoDTO cadastrarLink(LinkDocumentoDTO link, UsuarioDTO usuario) throws NegocioRemotoException;
	
	/** 
	 * Cadastra um documento no SIGED. Deve-se passar como parâmetro o documento
	 * a ser cadastrado, o arquivo associado ao documento e o usuário que está cadastrando.
	 * Caso o usuário não tenha permissão para cadastrar o documento em questão, será 
	 * disparada uma exceção do tipo {@link SegurancaException}.
	 * Método depreciado, o arquivo deve ser informado no próprio documento documento. Utilizar o método
	 * cadastrarPastasDocumento(), indicando o autoCreateFolder como false.
	 * @param documento
	 * @param arquivo
	 * @param usuario
	 * @throws NegocioRemotoException 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	@Deprecated
	@WebMethod(operationName="cadastrarDocumentoDeprecated")
	public DocumentoDTO cadastrarDocumento(DocumentoDTO documento, ArquivoDocumento arquivo, UsuarioDTO usuario) throws NegocioRemotoException;
		
	
	/** 
	 * Atualiza um documento no SIGED. Deve-se passar como parâmetro o documento
	 * a ser atualizado, o arquivo associado ao documento e o usuário que está cadastrando.
	 * Se o arquivo for null, mantém o que está. Se for diferente de null, atualiza o arquivo.
	 * Método depreciado, o arquivo arquivo deve ser informado no próprio documento.Utilizar atualizarDocumento informando
	 * apenas o documento e o usuário.
	 * 
	 * @param documento
	 * @param arquivo
	 * @param usuario
	 * @throws NegocioRemotoException 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	@Deprecated @WebMethod(operationName="atualizarDocumentoDeprecated")
	public DocumentoDTO atualizarDocumento(DocumentoDTO documento, ArquivoDocumento arquivo, UsuarioDTO usuario) throws NegocioRemotoException;
	
	/**
	 * Atualiza um documento no SIGED. Deve-se passar como parâmetro o documento
	 * a ser atualizado e o usuário que está cadastrando.
	 * Se o arquivo for null, mantém o que está. Se for diferente de null, atualiza o arquivo.
	 * @param documento
	 * @param usuario
	 * @return
	 * @throws NegocioRemotoException 
	 */
	@WebMethod(operationName="atualizarDocumento")
	public DocumentoDTO atualizarDocumento(DocumentoDTO documento, UsuarioDTO usuario) throws NegocioRemotoException;
	
	/**
	 * Exclui um documento do SIGED de acordo com o id passado
	 * como parâmetro.
	 * 
	 * @param idDocumento
	 * @throws NegocioRemotoException 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public void excluirDocumento(UsuarioDTO usuario, int idDocumento, String securityToken) throws NegocioRemotoException;
	
	/**
	 * Exclui um documento do SIGED de acordo com o id do arquivo
	 * associado ao documento.
	 * @param usuario
	 * @param idArquivo
	 * @throws NegocioRemotoException 
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public void excluirDocumentoPorIdArquivo(UsuarioDTO usuario, int idArquivo, String securityToken) throws NegocioRemotoException;
	
	/**
	 * Dado o ID de um documento, retorna as suas principais informações.
	 * @param id
	 * @return
	 */
	public DocumentoDTO buscarPorId(UsuarioDTO usuario, int id);
	
	/**
	 * Busca um documento pelo identificador do arquivo associado ao mesmo.
	 * @param id
	 * @return
	 */
	public DocumentoDTO buscarPorIdArquivo(UsuarioDTO usuario, int idArquivo);
	
	/**
	 * Método para busca de documentos por um critério. Busca dados
	 * no conteúdo dos documentos.
	 * 
	 * @param criterio
	 * @return
	 */
	public List<ResultadoBuscaDTO> buscarDocumentos(UsuarioDTO usuario, String criterio);

	/**
	 * Método para busca avançada de documentos, podendo restringir a
	 * busca por tipo de documento, valores de descritores e um determinado
	 * texto no seu conteúdo.
	 * 
	 * @param tipoDocumento
	 * @param descritores
	 * @param criterio
	 * @return
	 */
	public List<ResultadoBuscaDTO> buscaAvancada(UsuarioDTO usuario, Integer tipoDocumento, List<DescritorDTO> descritores, String criterio);

	/**
	 * Cadastra todas as pastas informadas no caminho especificado pela String diretorio.
	 * @param usuario
	 * @param diretorio
	 * @param idPasta
	 * @return
	 * @throws NegocioRemotoException 
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public PastaDocumentoDTO cadastrarPastas(UsuarioDTO usuario, String pastaId, String pastaLabel, Integer idPasta) throws NegocioRemotoException;
	
	/**
	 * Cadastra um documento e sua árvore de pastas no SIGED.
	 * 
	 * @see IntegracaoSigedService#cadastrarDocumento(DocumentoDTO, ArquivoDocumento, UsuarioDTO)
	 * @param documento
	 * @param arquivo
	 * @param usuario
	 * @param diretorio
	 * @param idPastaPai
	 * @return
	 * @throws NegocioRemotoException 
	 * @throws NegocioException
	 * @throws ArqException
	 */
	@Deprecated
	public DocumentoDTO cadastrarPastasDocumento(DocumentoDTO documento, ArquivoDocumento arquivo, UsuarioDTO usuario, String pastaId, String pastaLabel, Integer idPastaPai) throws NegocioRemotoException;
		
	
	/** 
	 * Cadastra um documento no SIGED. Deve-se passar como parâmetro o documento
	 * a ser cadastrado e o usuário que está cadastrando.
	 * Caso o usuário não tenha permissão para cadastrar o documento em questão, será 
	 * disparada uma exceção do tipo {@link SegurancaException}.
	 * @param documento
	 * @param usuario
	 * @param autoCreateFolder - Cria a pasta automaticamente se ela não existir
	 * @throws NegocioRemotoException 

	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	@WebMethod(operationName="cadastrarDocumento")
	public DocumentoDTO cadastrarDocumento(DocumentoDTO documento, UsuarioDTO usuario, boolean criarPastas) throws NegocioRemotoException;
	
	
}