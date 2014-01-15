package br.ufrn.siged.integracao;

import static br.ufrn.arq.util.ReflectionUtils.getProperty;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.comum.dao.ConfiguracaoOperacaoSigedDAO;
import br.ufrn.comum.dominio.ConfiguracaoOperacaoSiged;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.dto.UsuarioDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import br.ufrn.integracao.siged.dto.ArquivoDocumento;
import br.ufrn.integracao.siged.dto.DescritorDTO;
import br.ufrn.integracao.siged.dto.DocumentoDTO;
import br.ufrn.integracao.siged.dto.LinkDocumentoDTO;
import br.ufrn.integracao.siged.dto.PastaDocumentoDTO;
import br.ufrn.integracao.siged.dto.ResultadoBuscaDTO;
import br.ufrn.integracao.siged.service.IntegracaoSigedService;

/**
 * Classe que serve como fachada para acesso as operações de integração
 * com o SIGED. Verifica se o SIGED está ativo e chama o web service
 * de integração.
 * 
 * @author Gleydson Lima
 * @author David Pereira
 *
 */
public class SigedFacade {

	@Autowired
	private IntegracaoSigedService siged;

	/**
	 * Com o Siged e a configuração de operações ativas, realiza o cadastro do documento e cria a pasta automaticamente caso não exista,
	 * caso contrário, insere o arquivo na base de arquivos. 
	 * 
	 * @param arquivo
	 * @param builder
	 * @param params
	 * @param usuario
	 * @return
	 * @throws NegocioException 
	 */
	public DocumentoDTO inserirDocumento(ArquivoDocumento arquivo, DocumentoSigedBuilder builder, Map<String, Object> params, UsuarioDTO usuario) throws NegocioException {
		
		ConfiguracaoOperacaoSiged configuracao = recuperarConfiguracao(builder);
		DocumentoDTO documento = null;
		
		if (configuracao != null) {
			List<DescritorDTO> descritores = siged.buscarDescritores(configuracao.getIdTipoDocumento());
			configuracao.setDescritores(descritores);
			
			documento = builder.build(configuracao, params);
			documento.setArquivo(arquivo);
			documento.setTipoDocumento(configuracao.getIdTipoDocumento());
			
			String pastaId = documento.getPastaId();
			String pastaLabel = documento.getPastaLabel();
			
			try {
				documento = siged.cadastrarDocumento(documento, usuario, true);
			} catch (NegocioRemotoException e) {
				throw new NegocioException(e.getMessage());
			}
			
			documento.setPastaId(pastaId);
			documento.setPastaLabel(pastaLabel);
		} else {
			documento = new DocumentoDTO();
			int identificadorArquivo = cadastrarDocumento(arquivo);
			documento.setIdArquivo(identificadorArquivo);
		}
		
		return documento;
	}
	
	/**
	 * Com o Siged e a configuração de operações ativas, realiza a atualização do documento,
	 * caso contrário, atualiza o arquivo na base de arquivos. 
	 * @param arquivo
	 * @param builder
	 * @param params
	 * @param usuario
	 * @return
	 * @throws NegocioException 
	 */
	public int atualizarDocumento(final ArquivoDocumento arquivo, DocumentoSigedBuilder builder, Map<String, Object> params, UsuarioDTO usuario) throws NegocioException {
		
		ConfiguracaoOperacaoSiged configuracao = recuperarConfiguracao(builder);
		DocumentoDTO documento = (DocumentoDTO) params.get("documento");
		
		if (configuracao != null) {
			documento.setArquivo(arquivo);
			
			try {
				documento = siged.atualizarDocumento(documento, usuario);
			} catch (NegocioRemotoException e) {
				throw new NegocioException(e.getMessage());
			}
			
			return documento.getIdArquivo();
		} else {
			if (documento != null) {
				EnvioArquivoHelper.removeArquivo(documento.getIdArquivo());
			}
			return cadastrarDocumento(arquivo);
		}
		
	}
	
	/**
	 * Recupera as informações de configuração de acordo com a operação realizada.
	 * Retorna nulo, caso o siged ou a operação estejam inativos.  
	 * 
	 * @param builder
	 * @param params 
	 * @return
	 */
	public ConfiguracaoOperacaoSiged recuperarConfiguracao(DocumentoSigedBuilder builder) {
		if (Sistema.isSigedAtivo()) {
			ConfiguracaoOperacaoSigedDAO dao = new ConfiguracaoOperacaoSigedDAO();
			
			try {
				ConfiguracaoOperacaoSiged configuracao = dao.findConfiguracoesByOperacao(builder.getClass().getName());
				
				if (configuracao != null && configuracao.isAtivo())
					return configuracao;
				
			} catch (DAOException e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			
		}
		
		return null;
	}
	
	/**
	 * Utilizado caso o Siged e a configuração de operação não estejam ativos.
	 * Realiza o cadastro do arquivo na base arquivos.
	 * 
	 * @param arquivo
	 * @return 
	 */
	private int cadastrarDocumento(Object arquivo) {
		int idArquivo =	EnvioArquivoHelper.getNextIdArquivo();
		EnvioArquivoHelper.inserirArquivo(idArquivo, (byte[]) getProperty(arquivo, "bytes"), (String) getProperty(arquivo, "contentType"), (String) getProperty(arquivo, "name"));
		return idArquivo;
	}
	
	/**
	 * Exclui um documento do SIGED de acordo com o id do arquivo
	 * associado ao documento. 
	 * 
	 * @param deUsuarioParaDTO
	 * @param idArquivo
	 * @param securityToken
	 * @throws NegocioException 
	 */
	public void excluirDocumentoPorIdArquivo(UsuarioDTO usuario, int idArquivo, String securityToken) throws NegocioException {
		DocumentoDTO documento = null;
			  
		if (Sistema.isSigedAtivo()) {
			documento = buscarDocumentoPorIdArquivo(usuario, idArquivo);   
		}
			  
		if(documento == null){
			EnvioArquivoHelper.removeArquivo(idArquivo);
		}else{
			try {
				siged.excluirDocumentoPorIdArquivo(usuario, idArquivo, securityToken);
			} catch (NegocioRemotoException e) {
				throw new NegocioException(e.getMessage());
			}
		}
	}

	/**
	 * Busca um documento pelo identificador do arquivo associado ao mesmo.
	 * 
	 * @param idArquivo
	 * @return
	 */
	public DocumentoDTO buscarDocumentoPorIdArquivo(UsuarioDTO usuario, Integer idArquivo) {
		if (Sistema.isSigedAtivo()) {
			return siged.buscarPorIdArquivo(usuario, idArquivo);
		} else {
			return null;
		}
	}
	
	/**
	 * Busca uma pasta de documentos pelo nome (exact match) e pela pasta pai.
	 * 
	 * @param nome
	 * @param pai
	 * @return
	 */
	public PastaDocumentoDTO buscarPasta(String nome, Integer pai) {
		if (Sistema.isSigedAtivo()) {
			return siged.buscarPasta(nome, pai);
		} else {
			return null;
		}
	}
	
	/**
	 * Cadastra todas as pastas informadas no caminho especificado pelas String pastaId e pastaLabel.
	 * 
	 * @param usuario
	 * @param pastaId
	 * @param pastaLabel
	 * @param idPasta
	 * @return
	 * @throws NegocioException 
	 */
	public PastaDocumentoDTO cadastrarPastas(UsuarioDTO usuario, String pastaId, String pastaLabel, Integer idPasta) throws NegocioException {
		if (Sistema.isSigedAtivo()) {
			try {
				return siged.cadastrarPastas(usuario, pastaId, pastaLabel, idPasta);
			} catch (NegocioRemotoException e) {
				throw new NegocioException(e.getMessage());
			}
		} else {
			return null;
		}
	}

	/**
	 * Cadastra um link para um documento passando como parâmetros o 
	 * documento de destino do link. 
	 * 
	 * @param id
	 * @param localizacao
	 * @throws NegocioException 
	 */
	public void criarLink(UsuarioDTO usuario, int documento, int localizacao) throws NegocioException {
		if (Sistema.isSigedAtivo()) {
			DocumentoDTO dto = siged.buscarPorId(usuario, documento);
			if (!isEmpty(dto)) {
				LinkDocumentoDTO link = new LinkDocumentoDTO();
				link.setDestino(documento);
				link.setTipoDocumento(dto.getTipoDocumento());
				link.setLocalizacao(localizacao);
				link.setNome(dto.getNome());
				try {
					siged.cadastrarLink(link, usuario);
				} catch (NegocioRemotoException e) {
					throw new NegocioException(e.getMessage());
				}
			}
		}
	}

	/**
	 * Busca os descritores associados a um determinado tipo de documento
	 * 
	 * @param tipoDocumento
	 * @return
	 */
	public List<DescritorDTO> buscarDescritores(Integer tipoDocumento) {
		if (Sistema.isSigedAtivo()) {
			return siged.buscarDescritores(tipoDocumento);
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * Método para busca avançada de documentos, podendo restringir a
	 * busca por tipo de documento, valores de descritores e um determinado
	 * texto no seu conteúdo.
	 * 
	 * @param object
	 * @param tipoDocumento
	 * @param descritores
	 * @param textoConsultado
	 * @return
	 */
	public List<ResultadoBuscaDTO> buscaAvancada(UsuarioDTO usuario, Integer tipoDocumento, List<DescritorDTO> descritores, String criterio) {
		if (Sistema.isSigedAtivo()) {
			return siged.buscaAvancada(usuario, tipoDocumento, descritores, criterio);
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * Cadastra uma pasta de documentos passando como parâmetros o nome
	 * da pasta e a pasta pai. 
	 * 
	 * @param pasta
	 * @param deUsuarioParaDTO
	 * @return
	 * @throws NegocioException 
	 */
	@Deprecated
	public PastaDocumentoDTO cadastrarPasta(PastaDocumentoDTO pasta, UsuarioDTO usuario) throws NegocioException {
		if (Sistema.isSigedAtivo()) {
			try {
				return siged.cadastrarPasta(pasta, usuario);
			} catch (NegocioRemotoException e) {
				throw new NegocioException(e.getMessage());
			}
		} else {
			return null;
		}
	}

	/**
	 * Cadastra um documento no SIGED. Deve-se passar como parâmetro o documento
	 * a ser cadastrado, o arquivo associado ao documento e o usuário que está cadastrando.
	 * Caso o usuário não tenha permissão para cadastrar o documento em questão, será 
	 * disparada uma exceção do tipo {@link SegurancaException}.
	 * Método deprecated, o arquivo deve ser informado no próprio documento documento. Utilizar o método
	 * inserirDocumento().
	 * 
	 * @param documento
	 * @param arquivo
	 * @param deUsuarioParaDTO
	 * @return
	 * @throws NegocioException 
	 */
	@Deprecated
	public DocumentoDTO cadastrarDocumento(DocumentoDTO documento, ArquivoDocumento arquivo, UsuarioDTO usuario) throws NegocioException {
		if (Sistema.isSigedAtivo()) {
			try {
				return siged.cadastrarDocumento(documento, arquivo, usuario);
			} catch (NegocioRemotoException e) {
				throw new NegocioException(e.getMessage());
			}
		} else {
			return null;
		}
	}

	/**
	 * Atualiza um documento no SIGED. Deve-se passar como parâmetro o documento
	 * a ser atualizado, o arquivo associado ao documento e o usuário que está cadastrando.
	 * Se o arquivo for null, mantém o que está. Se for diferente de null, atualiza o arquivo.
	 * Método deprecated, o arquivo arquivo deve ser informado no próprio documento.Utilizar atualizarDocumento informando
	 * o SigedBuilder.
	 * 
	 * @param documento
	 * @param arquivo
	 * @param deUsuarioParaDTO
	 * @return
	 * @throws NegocioException 
	 */
	@Deprecated
	public DocumentoDTO atualizarDocumento(DocumentoDTO documento, ArquivoDocumento arquivo, UsuarioDTO usuario) throws NegocioException {
		if (Sistema.isSigedAtivo()) {
			try {
				return siged.atualizarDocumento(documento, arquivo, usuario);
			} catch (NegocioRemotoException e) {
				throw new NegocioException(e.getMessage());
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Com o Siged e a configuração de operação ativos, realiza o cadastro do documento e cria a pasta automaticamente caso não exista,
	 * caso contrário, insere o arquivo na base de arquivos. 
	 * 
	 * @param arquivo
	 * @param builder
	 * @param params
	 * @param usuario
	 * @return
	 * @throws NegocioException 
	 */
	@Deprecated
	public DocumentoDTO inserirPastaDocumento(final ArquivoDocumento arquivo, DocumentoSigedBuilder builder, Map<String, Object> params, UsuarioDTO usuario) throws NegocioException {
		return inserirDocumento(arquivo, builder, params, usuario);
	}

}
