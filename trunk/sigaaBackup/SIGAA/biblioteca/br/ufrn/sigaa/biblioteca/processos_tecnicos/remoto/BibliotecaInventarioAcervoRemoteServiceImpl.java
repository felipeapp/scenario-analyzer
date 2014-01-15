/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 * 
 * Created on 30/03/2012
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.remoto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jws.WebService;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.PermissaoDAO;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.FacadeDelegate;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.Permissao;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dominio.UsuarioMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.integracao.dtos.inventario_acervo.InventarioDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.inventario_acervo.MaterialDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.inventario_acervo.ParametrosRetornoExecutarColetorDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.inventario_acervo.ParametrosRetornoLogarColetorDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.inventario_acervo.ParametrosRetornoRegistrarMateriaisDto;
import br.ufrn.sigaa.biblioteca.integracao.exceptions.NegocioRemotoBibliotecaColetorException;
import br.ufrn.sigaa.biblioteca.integracao.interfaces.BibliotecaInventarioAcervoRemoteService;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.InventarioAcervoBibliotecaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.InventarioAcervoBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ItemResultadoRegistraMateriaisInventario;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ResultadoRegistraMateriaisInventario;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoRegistraMateriaisInventario;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;


/**
 * Web-service responsável por processar as operações relacionadas ao inventário do acervo do coletor de dados.
 * <br/>
 * (Referenciado pelo projeto: ColetorInventarioAcervoBiblioteca).
 * @author Felipe 
 *
 */
@Component("bibliotecaInventarioAcervoRemoteService")
@Scope("singleton")
@WebService
public class BibliotecaInventarioAcervoRemoteServiceImpl extends AbstractController implements BibliotecaInventarioAcervoRemoteService  {
	
	/** Esse valor tem que ser igual ao valor da tabela "navegador" :   
	 * INSERT INTO navegador(id, nome, expressao_identificadora) VALUES (XX, 'Sistema Coletor Biblioteca', 'Coletor Biblioteca'); */
	static final String  USER_AGENTE = "Coletor Biblioteca";
	
	
	
	public BibliotecaInventarioAcervoRemoteServiceImpl() {
		
	}

	/**
	 * Ver comentário na interface implementada.
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.remoto.BibliotecaInventarioAcervoRemoteService#testarConexao()
	 */
	public String testarConexao() {
		return "Conectou com sucesso.";
	}

	/**
	 * Ver comentário na interface implementada.
	 * 
	 * @throws NegocioRemotoBibliotecaColetorException 
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.remoto.BibliotecaInventarioAcervoRemoteService#registrarMateriais(int, java.util.List<java.lang.String>, int)
	 */
	@SuppressWarnings("unchecked")
	public ParametrosRetornoRegistrarMateriaisDto registrarMateriais(int idInventario, List<String> codigosBarras, int idOperador) throws NegocioRemotoBibliotecaColetorException {		
		PermissaoDAO permissaoDAO = null;
		
		try {			
			validarCampos(idInventario, codigosBarras);
			
			permissaoDAO = DAOFactory.getInstance().getDAO(PermissaoDAO.class);
			
			Usuario operador = new Usuario(idOperador);
			
			operador.setPermissoes(permissaoDAO.findPermissoesByUsuario(operador));
			operador.setPapeis(new ArrayList<Papel>());
			
			for (Permissao p : operador.getPermissoes()) {
				operador.getPapeis().add(p.getPapel());
			}
			
			MovimentoRegistraMateriaisInventario mov = new MovimentoRegistraMateriaisInventario(codigosBarras, new InventarioAcervoBiblioteca(idInventario));
			mov.setCodMovimento(SigaaListaComando.REGISTRAR_MATERIAIS_INVENTARIO);

			ResultadoRegistraMateriaisInventario resultado = (ResultadoRegistraMateriaisInventario) executeWithoutClosingSession(mov, new FacadeDelegate("ejb/SigaaFacade"), operador, Sistema.SIGAA);
			
			return converterParaDTO(resultado);
		} catch (NegocioException ne){ // Erros de negócio do empréstimo.			
			throw new NegocioRemotoBibliotecaColetorException(ne.getMessage());			
		} catch (Exception e) {      // Erros que não eram para ocorrer.
			throw new NegocioRemotoBibliotecaColetorException(ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_REMOTA_ERRO_PADRAO));
		} finally {
			if (permissaoDAO != null) permissaoDAO.close();
		}
	}

	/**
	 * Converte o resultado do processamento do registro em um DTO para ser enviado ao software cliente.
	 * 
	 * @param resultado
	 * @return
	 */
	private ParametrosRetornoRegistrarMateriaisDto converterParaDTO(ResultadoRegistraMateriaisInventario resultado) {
		ParametrosRetornoRegistrarMateriaisDto retorno = new ParametrosRetornoRegistrarMateriaisDto();
		List<String> itensConcluidos = new ArrayList<String>();
		
		retorno.mensagens = resultado.getStringMensagens();
		
		for (ItemResultadoRegistraMateriaisInventario item : resultado.getItensConcluidos()) {
			itensConcluidos.add(item.getCodigoBarras());
		}
		
		retorno.itensConcluidos = itensConcluidos;
		
		return retorno;
	}

	/**
	 * Valida os campos antes de prosseguir com o processamento do registro.
	 * 
	 * @param inventario
	 * @param codigosBarras
	 * @throws NegocioException
	 */
	private void validarCampos(int inventario, List<String> codigosBarras) throws NegocioException {
		ListaMensagens erros = new ListaMensagens();

		if (inventario == -1) {
			erros.addErro("Selecione uma inventário.");
		}
		
		if (codigosBarras == null || codigosBarras.isEmpty()) {
			erros.addErro("Não há materiais na lista para registrar consulta.");
		}
		
		if (erros.size() > 0) {			
			throw new NegocioException(erros);
		}		
	}

	/**
	 * Ver comentário na interface implementada.
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.remoto.BibliotecaInventarioAcervoRemoteService#obterParametrosInstitucionais()
	 */
	public ParametrosRetornoExecutarColetorDto obterParametrosInstitucionais() {

		ParametrosRetornoExecutarColetorDto retorno = new ParametrosRetornoExecutarColetorDto();
		
		// Usado para parametrizar as informações institucionais  //
		retorno.instituicao = RepositorioDadosInstitucionais.get("nomeInstituicao");
		retorno.siglaSistema = RepositorioDadosInstitucionais.get("siglaSigaa");
		retorno.nomeSistema = RepositorioDadosInstitucionais.get("nomeSigaa");
//		retorno.mensagemSobre = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_JANELA_SOBRE_DESKTOP);
		
		return retorno;
		
	}

	/**
	 * Ver comentário na interface implementada.
	 * 
	 * @throws NegocioRemotoBibliotecaColetorException 
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.remoto.BibliotecaInventarioAcervoRemoteService#logar(java.lang.String, java.lang.String, java.lang.String, 
	 * java.lang.String, java.lang.String)
	 */
	public ParametrosRetornoLogarColetorDto logar(String login, String senha, String hostAddress, String hostName, String operacionalSystem) throws NegocioRemotoBibliotecaColetorException {

		UsuarioMov userMov = new UsuarioMov();
		userMov.setCodMovimento(SigaaListaComando.LOGON);

		Usuario usuario = new Usuario();
		usuario.setLogin(login);
		usuario.setSenha(senha);
		userMov.setUsuario(usuario);

		GenericDAO dao = null;
		InventarioAcervoBibliotecaDao inventarioDao = null;
		
		Usuario user = null;
		
		try {
			userMov.setIP(hostAddress);
			userMov.setHost(hostName);
			userMov.setUserAgent(USER_AGENTE+" "+operacionalSystem);
			userMov.setAutenticarComHash(false);
			userMov.setCanal(RegistroEntrada.CANAL_DEVICE); //confirmar com gleydson
			user = (Usuario) executeWithoutClosingSession(userMov, new FacadeDelegate("ejb/SigaaFacade"), usuario, Sistema.SIGAA);

			// Verifica se o usuário está logado e com permissão de utilizar o sistema.
			if (user != null && user.isAutorizado()){
				if (user.isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_CHECKOUT)) {

					ParametrosRetornoLogarColetorDto retorno = new ParametrosRetornoLogarColetorDto();
					
					retorno.idOperador =  user.getId();
					retorno.nomeOperador = user.getNome();
					retorno.loginOperador = user.getLogin();
					retorno.sistemaOrigem = Sistema.COMUM;
					retorno.sistemaDestino = Sistema.SIGAA;
					
					dao = DAOFactory.getGeneric(Sistema.SIGAA);
					inventarioDao = DAOFactory.getInstance().getDAO(InventarioAcervoBibliotecaDao.class);
					
					/*
					 *  Os dados dos inventários que vão ser passados para o sitema remoto 
					 */
					List<InventarioDto> inventariosPermissaoRemota = new ArrayList<InventarioDto>();
					
					// O conjunto de bibliotecas onde o usuário tem permissão de circulação, no login do desktop ele vai ter que escolher uma para trabalhar //
					Set<InventarioAcervoBiblioteca> inventariosPermissao = new HashSet<InventarioAcervoBiblioteca>();

					if(user.isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
						List<InventarioAcervoBiblioteca> inventariosTemp = inventarioDao.findAllAbertos();
						
						inventariosPermissao.addAll(inventariosTemp);
					} else {
						if(user.isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL)){
							
							List<InventarioAcervoBiblioteca> inventariosTemp =  BibliotecaUtil.obtemInventariosPermissaoUsuario(user, new Papel(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL));
							
							inventariosPermissao.addAll(inventariosTemp);
						}
						
						if(user.isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO)){
							
							List<InventarioAcervoBiblioteca> inventariosTemp =  BibliotecaUtil.obtemInventariosPermissaoUsuario(user, new Papel(SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO));
							
							inventariosPermissao.addAll(inventariosTemp);
						}
						
						if(user.isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF)){
							
							List<InventarioAcervoBiblioteca> inventariosTemp =  BibliotecaUtil.obtemInventariosPermissaoUsuario(user, new Papel(SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF));
							
							inventariosPermissao.addAll(inventariosTemp);
						}
						
						if(user.isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO)){
							
							List<InventarioAcervoBiblioteca> inventariosTemp =  BibliotecaUtil.obtemInventariosPermissaoUsuario(user, new Papel(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO));
							
							inventariosPermissao.addAll(inventariosTemp);
						}
						
						if(user.isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO)){
							
							List<InventarioAcervoBiblioteca> inventariosTemp =  BibliotecaUtil.obtemInventariosPermissaoUsuario(user, new Papel(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO));
							
							inventariosPermissao.addAll(inventariosTemp);
						}
					}

					if (inventariosPermissao.size() == 0) {
						throw new NegocioException("Usuário não tem permissão para utilizar o módulo de coletor ou não existem inventários abertos disponíveis para o usuário.");
					} else{
						for (InventarioAcervoBiblioteca inventario : inventariosPermissao) {
							inventariosPermissaoRemota.add(new InventarioDto(inventario.getId(), inventario.getBiblioteca().getIdentificador() + " - " + inventario.getDescricao()));
						}
						
						retorno.inventariosPermissaoRemota = inventariosPermissaoRemota;
					}
					
					return retorno;
				}
			}
		} catch (NegocioException ne) {
			throw new NegocioRemotoBibliotecaColetorException(ne.getMessage());
		} catch (ArqException ae) {
			throw new NegocioRemotoBibliotecaColetorException(ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_REMOTA_ERRO_LOGON_DESKTOP));
		} finally {
			if (dao != null) dao.close();
			if (inventarioDao != null) inventarioDao.close();
		}

		return null;
	}

	/**
	 * Ver comentário na interface implementada.
	 * 
	 * @throws NegocioRemotoBibliotecaColetorException 
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.remoto.BibliotecaInventarioAcervoRemoteService#obterInformacoesMaterial(java.lang.String)
	 */
	public MaterialDto obterInformacoesMaterial(String codigoBarras) throws NegocioRemotoBibliotecaColetorException {
		InventarioAcervoBibliotecaDao inventarioDao = null;
		MaterialInformacionalDao materialDao = null;
		
		try {
			inventarioDao = DAOFactory.getInstance().getDAO(InventarioAcervoBibliotecaDao.class);
			materialDao = DAOFactory.getInstance().getDAO(MaterialInformacionalDao.class);

			Integer idMaterial = inventarioDao.findIdMaterialAtivoByCodigoBarras(codigoBarras);
			
			if (idMaterial != null) {
				Object[] objMaterial = (Object[]) materialDao.findInformacoesDoMaterial(idMaterial);
				
				MaterialDto material = new MaterialDto();

				material.id = idMaterial;
				material.codigoBarras = codigoBarras;
				material.titulo = (String) objMaterial[1];
				
				return material;
			} else {
				throw new NegocioException("Material com código de barras \"" + codigoBarras + "\" não existe na base de dados.");
			}
		} catch (NegocioException ne) {
			throw new NegocioRemotoBibliotecaColetorException(ne.getMessage());
		} catch (ArqException ae) {
			throw new NegocioRemotoBibliotecaColetorException(ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_REMOTA_ERRO_LOGON_DESKTOP));
		} finally {
			if (inventarioDao != null) inventarioDao.close();
			if (materialDao != null) materialDao.close();
		}
	}

}
