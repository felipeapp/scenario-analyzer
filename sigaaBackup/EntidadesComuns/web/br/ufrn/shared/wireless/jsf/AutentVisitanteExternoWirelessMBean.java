package br.ufrn.shared.wireless.jsf;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.jsf.ComumAbstractController;
import br.ufrn.comum.negocio.ComumListaComando;
import br.ufrn.comum.wireless.AutenticacaoUsersExt;
import br.ufrn.comum.wireless.AutorizacaoUsersExt;
import br.ufrn.comum.wireless.ConexaoWAS;
import br.ufrn.shared.wireless.dao.VisitanteExternoWirelessDAO;

/**
 * 
 * MBean responsavel por verificar se o Visitante Externo
 * ja esta cadastrado no sistema. 
 * 
 * Caso esteja e seja o primeiro acesso do visitante, ele vai fornecer 
 * alguns dados pessoais complementares ao sistema e criar uma senha.
 * 
 * Do segundo acesso em diante o visitante so precisa informar sua senha. 
 *   
 * JSPs:
 * shared/wireless/index.jsp
 * shared/wireless/visitante_ext.jsp
 * 
 * @author agostinho
 *
 */
@Component("autentVisitExternoMBean")
@Scope("session")
public class AutentVisitanteExternoWirelessMBean extends ComumAbstractController<AutenticacaoUsersExt> {

	private boolean primeiroAcessoUsuario;
	private boolean segundoAcessoUsuario;
	private Long cpf;
	private String passaporte;
	private String senha1, senha2;
	
	/**
	 * Construtor
	 * @throws ArqException
	 */
	public AutentVisitanteExternoWirelessMBean() throws ArqException {
		reset();
	}
	
	/**
	 * Metodo para iniciar o obj
	 * @throws ArqException
	 */
	public void reset() throws ArqException {
		obj = new AutenticacaoUsersExt();
		cpf = null;
		passaporte = null;
	}
	
	/**
	 * Redireciona para a pagina de autenticacao
	 * @return
	 * @throws ArqException
	 */
	public String pageAutenticacao() throws ArqException {
		
		reset();
		primeiroAcessoUsuario = false;
		segundoAcessoUsuario = false;
		
		return forward("/wireless/visitante_ext.jsp");
	}
	
	/**
	 * M�todo responsavel por verificar se o CPF informado
	 * est� autorizado para acessar a rede Wireless.
	 * 
	 * Primeiro: � verificado se o CPF est� em um prazo v�lido
	 * Segundo: se o usu�rio
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws SQLException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String autenticarVisitanteExterno() throws HibernateException, SQLException, NegocioException, ArqException {
		
		VisitanteExternoWirelessDAO dao = new VisitanteExternoWirelessDAO();
		
		if ( cpf != 0 && passaporte.equals("") ) { // se informar CPF
			
				AutorizacaoUsersExt autorizacao = dao.findAutorizacaoUserExtByCPF(cpf);
				if (autorizacao != null) {
					
					if ( verificarPrazoAcessoWireless( autorizacao )) { // verifica prazo de acesso p/ esse usuario
					
						if ( dao.findAutorizacaoUserExtByCPF(cpf) == null ) {
							addMensagemWarning("Esse CPF n�o est� cadastrado no sistema. Entre em contato com o departamento ao qual est� associado.");
							dao.close();
							return redirectMesmaPagina();
						}
						if ( dao.findAutorizacaoUserExtByCPF(cpf).getAutenticacao() == null ) { 
							// se for primeiro acesso do usuario, exibe tela para criar senha de acesso
							primeiroAcessoUsuario = true;
							dao.close();
							return null;
						}
						if ( dao.findAutorizacaoUserExtByCPF(cpf).getAutenticacao() != null ) {
							// se o usuario ja tiver criado sua senha, exibe tela p/ se autenticar
							segundoAcessoUsuario = true;
							dao.close();
							return null;
						}
					} else {
							addMensagemWarning("Caro visitante, seu prazo para acessar a Wireless expirou. Entre em contato com o departamento ao qual est� associado.");
							return redirectMesmaPagina();
					}
				} 
				else {
					addMensagemWarning("Esse CPF n�o est� cadastrado no sistema. Entre em contato com o departamento ao qual est� associado.");
					dao.close();
					return redirectMesmaPagina();
				}
		}
		
		if ( !passaporte.equals("") && cpf == 0) { // se informar passaporte
		
			AutorizacaoUsersExt autorizacao = dao.findAutorizacaoUserExtByPassaporte(passaporte);
			if (autorizacao != null) {
				
				if ( verificarPrazoAcessoWireless( autorizacao )) { // verifica prazo de acesso p/ esse usuario
				
					if ( dao.findAutorizacaoUserExtByPassaporte(passaporte) == null ) {
						addMensagemWarning("Esse passaporte n�o est� cadastrado no sistema. Entre em contato com o departamento ao qual est� associado.");
						dao.close();
						return redirectMesmaPagina();
					}
					if ( dao.findAutorizacaoUserExtByPassaporte(passaporte).getAutenticacao() == null ) { 
						// se for primeiro acesso do usuario, exibe tela para criar senha de acesso
						primeiroAcessoUsuario = true;
						dao.close();
						return null;
					}
					if ( dao.findAutorizacaoUserExtByPassaporte(passaporte).getAutenticacao() != null ) {
						// se o usuario ja tiver criado sua senha, exibe tela p/ se autenticar
						segundoAcessoUsuario = true;
						dao.close();
						return null;
					}
				} else {
						addMensagemWarning("Caro visitante, seu prazo para acessar a Wireless expirou. Entre em contato com o departamento ao qual est� associado.");
						return redirectMesmaPagina();
				}
			} 
			else {
				addMensagemWarning("Esse passaporte n�o est� cadastrado no sistema. Entre em contato com o departamento ao qual est� associado.");
				dao.close();
				return redirectMesmaPagina();
			}
			
		}
		
		if ( !passaporte.equals("") && cpf != 0)
			addMensagemWarning("Voc� deve informar o CPF OU Passaporte e n�o ambos.");
		
		if ( passaporte.equals("") && passaporte == null)
			addMensagemWarning("Voc� deve informar: CPF ou Passaporte e a quantidade de dias.");
		
		dao.close();
		return null;
	}
	
	/**
	 * Cada CPF tem um n�mero X de dias permitidos.
	 * Esse m�todo verifica se o CPF ainda est� autorizado
	 * para se autenticar.
	 * 
	 * @param aut
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private boolean verificarPrazoAcessoWireless(AutorizacaoUsersExt aut) throws NegocioException, ArqException {
		
			Date dataHoje = new Date();
			Date dataIncrementadaDiasAcesso = new Date();
			
			Calendar calendar = Calendar.getInstance();  
			calendar.setTime(aut.getDataCadastro());  
			calendar.add(Calendar.DAY_OF_MONTH, aut.getDiasAutorizados()); //incrementa diasAcesso
			
			dataIncrementadaDiasAcesso = calendar.getTime();
			
			if ( dataHoje.after(dataIncrementadaDiasAcesso) )	
				return false;
			else
				return true;
	}

	/**
	 * Cadastra uma autentica��o (o form apresentado para o usuario)
	 * na primeira vez que ele acessa.
	 */
	public String cadastrar() throws ArqException, NegocioException {
		
//		prepareMovimento(ComumListaComando.CADASTRAR_AUTENTICACAO_WIRELESS);
		
		VisitanteExternoWirelessDAO dao = new VisitanteExternoWirelessDAO();
		AutorizacaoUsersExt autorizacao = dao.findAutorizacaoUserExtByCPF(cpf);
		ArrayList<AutorizacaoUsersExt> list = new ArrayList<AutorizacaoUsersExt>();
		
		if ( !validarSenhasDigitadas(senha1, senha2) ) {
			obj.setSenha( UFRNUtils.toMD5(senha1, ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.SALT_SENHAS_USUARIOS)) );
			autorizacao.setAutenticacao(obj);
			list.add(autorizacao);
			obj.setAutorizacoes(list);
			
			MovimentoCadastro movCad = new MovimentoCadastro();
			movCad.setObjMovimentado(obj);
			movCad.setCodMovimento( ComumListaComando.CADASTRAR_AUTENTICACAO_WIRELESS );
			
			executeWithoutClosingSession(movCad, getCurrentRequest());
			
		}
		else 
			return redirectMesmaPagina();
		
		dao.close();
		segundoAcessoUsuario = true;
		primeiroAcessoUsuario = false;
		addMensagemInformation("Cadastro realizado com sucesso. Digite sua senha para liberar seu acesso!");
		
		return forward("/wireless/visitante_ext.jsp");
	}
	
	/**
	 * Limpa os campos e redireciona para a p�gina de autentica��o
	 * @return
	 * @throws HibernateException
	 * @throws ArqException
	 */
	public String limpar() throws HibernateException, ArqException {
		return pageAutenticacao();
	}
	
	/**
	 * Verifica se a senha informada para aquele CPF est� correta.
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 * @throws IOException
	 * @throws SQLException 
	 */
	public String liberarAcesso() throws HibernateException, DAOException, IOException, SQLException {
		
		VisitanteExternoWirelessDAO dao = new VisitanteExternoWirelessDAO();
		
		if (senha1 != null && cpf != null 
				&& dao.findAutorizacaoUserExtByCPF( cpf ).getAutenticacao() != null 
				&& dao.findAutorizacaoUserExtByCPF( cpf ).getAutenticacao().getSenha() != null) {
			
			String senhaHash = UFRNUtils.toMD5( senha1, ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.SALT_SENHAS_USUARIOS) );
			String hashDB = dao.findAutorizacaoUserExtByCPF( cpf ).getAutenticacao().getSenha();
				
			if ( senhaHash.equals(hashDB) ) {
				if ( ConexaoWAS.autenticarVisitanteExterno(  getCurrentRequest().getRemoteAddr(), 0 ))
					return redirect("/shared/wireless/autenticado.jsp");
			}
			else {
				addMensagemErro("Usu�rio ou senha inv�lida.");
				return redirectMesmaPagina();
			}
		}
		else {
			addMensagemWarning("Esse CPF n�o est� cadastrado no sistema. Entre em contato com o departamento ao qual est� associado.");
			redirectMesmaPagina();
		}
			
		dao.close();
		return redirectMesmaPagina();
	}

	/**
	 * Valida se as senhas digitas s�o iguais]
	 * 
	 * @param senha1
	 * @param senha2
	 * @return
	 */
	private boolean validarSenhasDigitadas(String senha1, String senha2) {
		boolean temErros = false;
		
		if (!senha1.equals( senha2) ) {
			addMensagemErro("As senhas digitadas N�O s�o iguais!");
			temErros = true;
		}
		if (senha1.length() < 6 || senha2.length() < 6) {
			addMensagemErro("A senha precisa ter no m�nimo 6 dig�tos");
			temErros = true;
		}
		
		return temErros;
	}
	
	/**
	 * GETS SETS
	 * @return
	 */
	
	public String getSenha1() {
		return senha1;
	}

	public void setSenha1(String senha1) {
		this.senha1 = senha1;
	}

	public String getSenha2() {
		return senha2;
	}

	public void setSenha2(String senha2) {
		this.senha2 = senha2;
	}

	public boolean isPrimeiroAcessoUsuario() {
		return primeiroAcessoUsuario;
	}

	public void setPrimeiroAcessoUsuario(boolean primeiroAcessoUsuario) {
		this.primeiroAcessoUsuario = primeiroAcessoUsuario;
	}

	public boolean isSegundoAcessoUsuario() {
		return segundoAcessoUsuario;
	}

	public void setSegundoAcessoUsuario(boolean segundoAcessoUsuario) {
		this.segundoAcessoUsuario = segundoAcessoUsuario;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public String getPassaporte() {
		return passaporte;
	}

	public void setPassaporte(String passaporte) {
		this.passaporte = passaporte;
	}
	
}