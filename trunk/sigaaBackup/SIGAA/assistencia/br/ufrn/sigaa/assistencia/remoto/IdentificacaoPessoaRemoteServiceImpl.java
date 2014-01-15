package br.ufrn.sigaa.assistencia.remoto;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.FacadeDelegate;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.dto.ParametrosRetornoLogarCadastroPessoaDTO;
import br.ufrn.integracao.dto.PessoaDto;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import br.ufrn.integracao.interfaces.IdentificacaoBiometricaRemoteService;
import br.ufrn.integracao.interfaces.IdentificacaoPessoaRemoteService;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dominio.UsuarioMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Implementa os métodos da interface utilizada pelo Spring Remote para comunicação
 * remota com o cliente desktop que realiza o cadastramento de digitais das pessoas.
 * das UFRN (alunos, servidores, etc.)  
 * 
 * Cadastro de Pessoas (aplicativo que faz o cadastramento de digitais de usuários)
 * 
 * @author agostinho campos
 * 
 */
@WebService
@Component("identificacaoPessoaController")
public class IdentificacaoPessoaRemoteServiceImpl extends AbstractController implements IdentificacaoPessoaRemoteService {

	@Resource(name = "identificacaoBiometricaInvoker")
	private IdentificacaoBiometricaRemoteService identificador;
	
	/**
	 * Método invocado pelo aplicativo desktop para autenticar o usuário, caso o mesmo possua o papel
	 * correspondente  
	 */
	public ParametrosRetornoLogarCadastroPessoaDTO logarSistemaCadastroPessoa(String login,
			String senha, String hostName, String hostAddress) {

		PessoaDao pessoaDao = null;
		try {
				pessoaDao = DAOFactory.getInstance().getDAO(PessoaDao.class);
		
				Usuario user = logar(login, senha, hostName, hostAddress);
				String permissaoCadastroPessoa = pessoaDao.findTipoPermissaoCadastroDigitalPessoa(user.getId());
		
				if (user != null && user.isAutorizado()) {
					if (user.isUserInRole(SigaaPapeis.GESTOR_CADASTRO_IDENTIFICACAO_PESSOA)) {
						ParametrosRetornoLogarCadastroPessoaDTO parametrosRetorno = new ParametrosRetornoLogarCadastroPessoaDTO();
						parametrosRetorno.idUsuario = user.getId();
						parametrosRetorno.permissaoCadastroPessoa = permissaoCadastroPessoa;
						return parametrosRetorno;
					}
				}
		} finally {
			if (pessoaDao != null)
				pessoaDao.close();
		}
		
		return null;
	}
	

	/**
	 * Método que busca pessoas pelo nome. Para cada pessoa localizada, verifica se as mesmas possuem digital, 
	 * retornando um DTO para o aplicativo. 
	 */
	public List<PessoaDto> findPessoa(String nome, int tipoPessoa) {
		PessoaDao daoSigaa = null;
		PessoaDao daoComum = null;
		
		try {
			daoSigaa = DAOFactory.getInstance().getDAO(PessoaDao.class);
			daoComum = DAOFactory.getInstance().getDAO(PessoaDao.class);
			
			List<PessoaDto> pessoas = daoSigaa.findPessoaByNome(nome, tipoPessoa);
			return daoComum.verificarExistenciaDigital(pessoas);
			
		} finally {
			if (daoSigaa!=null)
				daoSigaa.close();
			if (daoComum!=null)
				daoComum.close();
		}
	}

	/**
	 * Após coletar os dados da pessoa, o usuário do aplicativo manda cadastrar/atualizar
	 * os dados como  CPF, digital, foto e o dedo coletado
	 * @throws SQLException 
	 */
	public boolean gravarOuAtualizarIdentificacao(Long cpf, byte[] imagem, byte[] digital, String tipoDedo, int idUsuario,  byte[] imagemDigital) {

		PessoaDao pDao = null;
		try {
			pDao = DAOFactory.getInstance().getDAO(PessoaDao.class);
			if ( pDao.findTipoDedoCadastradoByCPF(cpf, tipoDedo) == null)
				return pDao.gravarIdentificacao(cpf, imagem, digital, tipoDedo, idUsuario, imagemDigital);
			else	
				return pDao.updateIdentificacao(cpf, imagem, digital, tipoDedo, idUsuario, imagemDigital);
		} catch (Exception e) {
			notifyError(e);
			return false;
		} finally {
			if (pDao!=null)
				pDao.close();
		}
	}

	/**
	 * Recebe um fluxo de bytes que representa a digital de uma pessoa e verifica e retorna um DTO caso
	 * a identificação seja localizada. 
	 * @throws NegocioRemotoException 
	 */
	public PessoaDto identificarPessoa(byte[] digital, String tipoDedo, Long cpf) throws NegocioRemotoException {

		long cpfLocalizado = identificador.identificar(digital);
		
		if (cpfLocalizado == 0)
			throw new NegocioRemotoException("Pessoa não identificada ou digital não cadastrada!");
		else {
			PessoaDao dao = null;
			
			try {
				
				dao = DAOFactory.getInstance().getDAO(PessoaDao.class);
				Pessoa p = dao.findByCpf(cpfLocalizado, true);
			
				PessoaDto dto = new PessoaDto();
					dto.setCpf(cpfLocalizado);
					dto.setNome(p.getNome());
					dto.setTipoDedo( dao.findTipoDedoCadastradoByCPF(cpfLocalizado, tipoDedo) );
				
				return dto;
				
			} catch(DAOException e) {
				notifyError(e);
			} catch (SQLException e) {
				notifyError(e);
			} finally {
				if (dao!=null)
					dao.close();
			}
		}
		return new PessoaDto();
		
	}
	
	/**
	 * Método que invoca o EJB da arquitetura para fazer o logon e retorna o usuário.
	 * 
	 * A senha recebida do aplicativo desktop é um hash MD5
	 * 
	 * @param login
	 * @param senha
	 * @param inetAdd
	 * @return
	 */
	private Usuario logar(String login, String senha, String hostName, String hostAddress) {
		UsuarioMov userMov = new UsuarioMov();
		userMov.setCodMovimento(SigaaListaComando.LOGON);

		Usuario usuario = new Usuario();
		usuario.setLogin(login);
		usuario.setSenha(senha);
		userMov.setUsuario(usuario);
		userMov.setAutenticarComHash(false);

		Usuario user = null;
		try {
			userMov.setIP(hostName);
			userMov.setHost(hostAddress);
			userMov.setUserAgent("Sistema Restaurante Universitario");
			user = (Usuario) executeWithoutClosingSession(userMov, new FacadeDelegate("ejb/SigaaFacade"), usuario, Sistema.SIGAA);			
		} catch (NegocioException e) {
			notifyError(e);
		} catch (ArqException e) {
			notifyError(e);
		}
		return user;
	}
}