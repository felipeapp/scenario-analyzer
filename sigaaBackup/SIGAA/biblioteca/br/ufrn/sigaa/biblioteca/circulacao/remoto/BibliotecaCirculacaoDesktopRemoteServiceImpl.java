/*
 * Universidade Federal do Rio Grande no Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em:  22/09/2008
 */
package br.ufrn.sigaa.biblioteca.circulacao.remoto;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.springframework.context.annotation.Scope;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.FacadeDelegate;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.interfaces.IdentificacaoBiometricaRemoteService;
import br.ufrn.sigaa.arq.dao.biblioteca.BuscaPessoaBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ConsultasEmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TipoEmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.dominio.UsuarioMov;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.remoto.IdentificacaoPessoaRemoteServiceImpl;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.RegistroCheckout;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoDesfazOperacao;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoDevolveEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoRealizaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoRenovaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.SituacaoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.integracao.dtos.BibliotecaDTO;
import br.ufrn.sigaa.biblioteca.integracao.dtos.EmprestimoDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.MaterialInformacionalDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.ParametrosRetornoLogarCirculacaoDTO;
import br.ufrn.sigaa.biblioteca.integracao.dtos.RetornoOperacoesCirculacaoDTO;
import br.ufrn.sigaa.biblioteca.integracao.dtos.TipoEmprestimoDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.TituloCatalograficoDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.UsuarioBibliotecaDto;
import br.ufrn.sigaa.biblioteca.integracao.exceptions.NegocioRemotoBibliotecaDesktopException;
import br.ufrn.sigaa.biblioteca.integracao.interfaces.BibliotecaCirculacaoDesktopRemoteService;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.BuscaUsuarioBibliotecaHelper;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * <p>Classe que implementa os métodos de comunicação entre a parte Desktop do
 * sistema da biblioteca e a parte servidora.</p>
 * 
 * Obs1.: you should use the prototype scope for all beans that are stateful, while the singleton scope should be used for stateless beans
 * Obs2.: Multiple threads calling the same method at the same time will all have their own copies of the method parameters.
 * 
 * @author jadson
 * @since 22/09/2008
 * @version 1.0 criacao da classe
 *
 */
@Component("bibliotecaCirculacaoDesktopRemoteService")
@Scope("singleton") 
@WebService
public class BibliotecaCirculacaoDesktopRemoteServiceImpl extends SigaaAbstractController <EmprestimoDto> implements BibliotecaCirculacaoDesktopRemoteService {

	/**
	 * O serviço utilizado para identificar um usuário pela digital.  Chama a classe <code>IdentificacaoDigital</code> em serviços integrados
	 */
	@Resource(name = "identificacaoBiometricaInvoker")
	private IdentificacaoBiometricaRemoteService identificador;
	
	
	/** Esse valor tem que ser igual ao valor da tabela "navegador" :   
	 * INSERT INTO navegador(id, nome, expressao_identificadora) VALUES (18, 'Sistema Desktop Biblioteca', 'Desktop Biblioteca'); */
	static final String  USER_AGENTE = "Desktop Biblioteca";
	
	/**
	 * Construtor padrão. Como o Service é singleton, só passe parâmetros aqui que sejam constantes e comuns a todos os usuários.
	 */
	public BibliotecaCirculacaoDesktopRemoteServiceImpl () {
		
	}

	
	/**
	 * 1º Método chamado para se logar no sistema
	 * 
	 * @throws NegocioRemotoBibliotecaDesktopException 
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.remoto.BibliotecaCirculacaoDesktopRemoteService#logar(java.lang.String,
	 *      java.lang.String, java.net.InetAddress)
	 */
	public ParametrosRetornoLogarCirculacaoDTO logar (String login, String senha, String hostAddress, String hostName, String operacionalSystem) throws NegocioRemotoBibliotecaDesktopException {

		UsuarioMov userMov = new UsuarioMov();
		userMov.setCodMovimento(SigaaListaComando.LOGON);

		Usuario usuario = new Usuario();
		usuario.setLogin(login);
		usuario.setSenha(senha);
		userMov.setUsuario(usuario);

		GenericDAO dao = null;
		TipoEmprestimoDao daoTipoEmp = null;
		
		Usuario user = null;

		
		try {
			userMov.setIP(hostAddress);
			userMov.setHost(hostName);
			userMov.setUserAgent(USER_AGENTE+" "+operacionalSystem);
			userMov.setAutenticarComHash(false);
			userMov.setCanal(RegistroEntrada.CANAL_DESKTOP);
			user = (Usuario) executeWithoutClosingSession(userMov, new FacadeDelegate("ejb/SigaaFacade"), usuario, Sistema.SIGAA);

			
			// Verifica se o usuário está logado e com permissão de utilizar o sistema.
			if (user != null && user.isAutorizado()){
				if (user.isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_CHECKOUT)) {

					ParametrosRetornoLogarCirculacaoDTO retorno = new ParametrosRetornoLogarCirculacaoDTO();
					
					retorno.idOperador =  user.getId();
					retorno.nomeOperador = user.getNome();
					retorno.loginOperador = user.getLogin();
					retorno.sistemaOrigem = Sistema.COMUM;
					retorno.sistemaDestino = Sistema.SIGAA;

					
					retorno.idRegistroEntrada = user.getRegistroEntrada().getId();
					
					// Usado para parametrizar as informações institucionais  //
					retorno.instituicao = RepositorioDadosInstitucionais.get("nomeInstituicao");
					retorno.siglaSistema = RepositorioDadosInstitucionais.get("siglaSigaa");
					retorno.nomeSistema = RepositorioDadosInstitucionais.get("nomeSigaa");
					retorno.mensagemSobre = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_JANELA_SOBRE_DESKTOP);
				
					// parametriza os links para os manuais do desktop //
					retorno.linkManualEmprestimos = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.LINK_MANUAL_EMPRESTIMOS_DESKTOP);
					retorno.linkManualCheckOut = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.LINK_MANUAL_CHECKOUT_DESKTOP);
					
					dao = DAOFactory.getGeneric(Sistema.SIGAA);
					daoTipoEmp = DAOFactory.getInstance().getDAO(TipoEmprestimoDao.class);
					/*
					 *  Os dados das bibliotecas que vão ser passados para o sitema remoto 
					 */
					List<BibliotecaDTO> bibliotecasPermissaoRemotas = new ArrayList<BibliotecaDTO>();
					
					/* 
					 * <p>Envia os papéis do usuário e o id da biblioteca onde ele tem o papel, para habilitar as abas corretas do móduto de circulacao</p>
					 * 
					 * <p>Enviado no formato IdPapel_idBiblioteca para evitar ter que criar outro DTO para enviar esses dados</p>
					 */
					List<String> papeis = new ArrayList<String>();
					
					// O conjunto de bibliotecas onde o usuário tem permissão de circulação, no login do desktop ele vai ter que escolher uma para trabalhar //
					Set<Biblioteca> bibliotecasPermisao = new HashSet<Biblioteca>();
					
					
					if(user.isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_CHECKOUT )){
						
						List<Biblioteca> bibliotecasTemp =  BibliotecaUtil.obtemBibliotecasPermisaoUsuario(user, new Papel(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_CHECKOUT));
						
						for (Biblioteca biblioteca : bibliotecasTemp) {
							papeis.add(UsuarioBibliotecaDto.BIBLIOTECA_SETOR_CIRCULACAO_CHECKOUT+"_"+biblioteca.getId());
						}
						
						bibliotecasPermisao.addAll(bibliotecasTemp);
					}
					
					if(user.isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO )){
						
						List<Biblioteca> bibliotecasTemp =  BibliotecaUtil.obtemBibliotecasPermisaoUsuario(user, new Papel(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO));
						
						for (Biblioteca biblioteca : bibliotecasTemp) {
							papeis.add(UsuarioBibliotecaDto.BIBLIOTECA_SETOR_CIRCULACAO+"_"+biblioteca.getId());
						}
						
						bibliotecasPermisao.addAll(bibliotecasTemp);
					}
					
					if(user.isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO )){
						
						List<Biblioteca> bibliotecasTemp =  BibliotecaUtil.obtemBibliotecasPermisaoUsuario(user, new Papel(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO));
						
						for (Biblioteca biblioteca : bibliotecasTemp) {
							papeis.add(UsuarioBibliotecaDto.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO+"_"+biblioteca.getId());
						}
						
						bibliotecasPermisao.addAll(bibliotecasTemp);
					}
					
					retorno.papeis  = papeis;
					
					
					if (bibliotecasPermisao == null || bibliotecasPermisao.size() == 0) {
						throw new NegocioException ("Usuário não tem permissão para utilizar o módulo de circulação");
					} else{
						
						
						
						for (Biblioteca biblioteca : bibliotecasPermisao) {
							bibliotecasPermissaoRemotas.add(new BibliotecaDTO(biblioteca.getId(), biblioteca.getDescricao()));
						}
						
						retorno.bibliotecasPermissaoRemotas  = bibliotecasPermissaoRemotas;
						
					}
					
					
					List <TipoEmprestimoDto> allTiposEmprestimo = new ArrayList <TipoEmprestimoDto> ();
					List <TipoEmprestimo> tes = daoTipoEmp.findTipoEmprestimosAtivos();

					for (TipoEmprestimo te : tes){
						TipoEmprestimoDto t = new TipoEmprestimoDto();
						t.descricao = te.getDescricao();
						t.idTipoEmprestimo = te.getId();
						
						allTiposEmprestimo.add(t);
					}
					
					retorno.allTiposEmprestimo = allTiposEmprestimo;
					
					return retorno;
				}
			}

		} catch (NegocioException ne) {
			throw new NegocioRemotoBibliotecaDesktopException(ne.getMessage());
		} catch (ArqException ae) {
			throw new NegocioRemotoBibliotecaDesktopException (ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_REMOTA_ERRO_LOGON_DESKTOP));
		} finally {
			if (dao != null)dao.close();
			if (daoTipoEmp != null) daoTipoEmp.close();
			
		}

		return null;
	}
	
	
	/**
	 * 2º  método chamado,  buscas os usuários da biblioteca por cpf. matricula, nome, etc
	 * 
	 * 
	 * @See {@link br.ufrn.sigaa.biblioteca.circulacao.remoto.BibliotecaCirculacaoDesktopRemoteService#buscaUsuariosCirculacaoDesktop(String)}
	 */
	public List <UsuarioBibliotecaDto> buscaUsuariosCirculacaoDesktop (String consulta, int tipoCampo, boolean buscarUsuariosExternos) throws NegocioRemotoBibliotecaDesktopException {

		UsuarioBibliotecaDao dao = null;
		
		try {
			
			boolean buscarUsuarioComum = ! buscarUsuariosExternos;
			
			String cpfConsulta = tipoCampo == TIPO_CAMPO_BUSCA_USUARIO_CPF ? consulta : null;
			String passaporteConsulta = tipoCampo == TIPO_CAMPO_BUSCA_USUARIO_PASSAPORTE ? consulta : null;
			String matriculaConsulta = tipoCampo == TIPO_CAMPO_BUSCA_USUARIO_MATRICULA ? consulta : null;
			String siapeConsulta = tipoCampo == TIPO_CAMPO_BUSCA_USUARIO_SIAPE ? consulta : null;
			String nomeUsuarioConsulta = tipoCampo == TIPO_CAMPO_BUSCA_USUARIO_NOME ? consulta : null;
			
			/*
			 *  Chama a busca padrão de usuários da biblioteca
			 *  
			 *  Não suporta o tipo de usuário biblioteca, pois empréstimos institucionais são realizados apenas no módulo web
			 */
			List <Object []> pessoas = BuscaUsuarioBibliotecaHelper.buscaPadraoUsuarioBiblioteca(buscarUsuarioComum, buscarUsuariosExternos, false
					, cpfConsulta , passaporteConsulta, matriculaConsulta , siapeConsulta, nomeUsuarioConsulta, -1 ,
					false, false, false);
			
			List <UsuarioBibliotecaDto> usuariosRetorno = new ArrayList <UsuarioBibliotecaDto> ();
			
			for (Object [] pessoa : pessoas){
				
				UsuarioBibliotecaDto u = new UsuarioBibliotecaDto();
				
				if (pessoa[BuscaPessoaBibliotecaDao.POSICAO_CPF] != null){
					String aux = ""+ pessoa[BuscaPessoaBibliotecaDao.POSICAO_CPF];
					if (!StringUtils.isEmpty(aux))
						u.cpf = Long.parseLong(""+pessoa[BuscaPessoaBibliotecaDao.POSICAO_CPF]);
				}
				
				if (pessoa[BuscaPessoaBibliotecaDao.POSICAO_PASSAPORTE] != null){
					String aux = ""+ pessoa[BuscaPessoaBibliotecaDao.POSICAO_PASSAPORTE];
					if (!StringUtils.isEmpty(aux))
						u.passaporte = ""+ pessoa[BuscaPessoaBibliotecaDao.POSICAO_PASSAPORTE];
				}
				
				u.nome = (String) pessoa[BuscaPessoaBibliotecaDao.POSICAO_NOME];
				u.dataNascimento = (Date) pessoa[BuscaPessoaBibliotecaDao.POSICAO_DATA_NASCIMENTO];
				u.idUsuarioBiblioteca = ((Number) pessoa[BuscaPessoaBibliotecaDao.POSICAO_ID_USUARIO_BIBLIOTECA]).intValue();
				
				usuariosRetorno.add(u);
			}
			
			return usuariosRetorno;
			
		} catch (Exception e){
			e.printStackTrace();
			throw new NegocioRemotoBibliotecaDesktopException(ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_REMOTA_ERRO_PADRAO)); // encapsula o DAOException 
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	
	
	/**
	 * 3º Método chamado, configuras as informações do usuário biblioteca selecionado (Situação para realizar Emprestimos e  os Emprestimos ativos)
	 * 
	 * 
	 * @throws DAOException 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.remoto.BibliotecaCirculacaoDesktopRemoteService#populaDadosUsuario(UsuarioBibliotecaDto)
	 */
	public UsuarioBibliotecaDto populaDadosUsuario (UsuarioBibliotecaDto dto) throws NegocioRemotoBibliotecaDesktopException {
		
		long tempo = System.currentTimeMillis();
		
		if ( dto != null ){
			
			UsuarioBibliotecaDao daoUsuario = null;
			ConsultasEmprestimoDao daoConsultasEmprestimos = null;
			
			try {
				
				daoUsuario = DAOFactory.getInstance().getDAO(UsuarioBibliotecaDao.class);
				daoConsultasEmprestimos = DAOFactory.getInstance().getDAO(ConsultasEmprestimoDao.class);
				
				UsuarioBiblioteca usuarioBiblioteca = daoConsultasEmprestimos.findInformacoesUsuarioBibliotecaNaoQuitado(dto.idUsuarioBiblioteca);
				
				if (usuarioBiblioteca != null){

					InformacoesUsuarioBiblioteca infoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(usuarioBiblioteca, null, null);
					
					String motivoBloqueio = VerificaSituacaoUsuarioBibliotecaUtil.getMotivoBloqueadoUsuario(usuarioBiblioteca);
					
					if(StringUtils.notEmpty(motivoBloqueio)){
						SituacaoUsuarioBiblioteca situacaoBloqueio = SituacaoUsuarioBiblioteca.ESTA_BLOQUEADO;
						dto.descricoesDetalhadasSituacoesUsuario.add(situacaoBloqueio.getDescricaoResumida()+" Motivo: "+motivoBloqueio );
						dto.valoresSituacoesUsuario.add(situacaoBloqueio.getValor());
						
					}else{
					
						/* **************************************************************************************
						 * IMPORTANTE:  Verificar a situação do usuário com relação as punições na biblioteca   *
						 * **************************************************************************************/
						
						List<SituacaoUsuarioBiblioteca> situacoesUsuario = VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiPunicoesBiblioteca(
								usuarioBiblioteca.getIdentificadorPessoa()
								, usuarioBiblioteca.getIdentificadorBiblioteca() );
						
						for (SituacaoUsuarioBiblioteca situacao : situacoesUsuario) {
							dto.descricoesDetalhadasSituacoesUsuario.add(situacao.getDescricaoCompleta());
							dto.valoresSituacoesUsuario.add( situacao.getValor());
						}
						
						
						dto.emprestimos = daoConsultasEmprestimos.findEmprestimosDtoAtivoByUsuarioBiblioteca(usuarioBiblioteca);
						
						
						/* *********************************************************************************************************
						 * IMPORTANTE:  Verificar se tem empréstimos atrazados se ainda não tem punição que impessa o empréstimos  *
						 * *********************************************************************************************************/
						if (dto.valoresSituacoesUsuario.isEmpty()){  //  
	
							for (EmprestimoDto empDto : dto.emprestimos) {
								if(empDto.status == EmprestimoDto.STATUS_ATRASADO){
									dto.valoresSituacoesUsuario.add(SituacaoUsuarioBiblioteca.POSSUI_EMPRESTIMOS_ATRASADOS.getValor());
									dto.descricoesDetalhadasSituacoesUsuario.add(SituacaoUsuarioBiblioteca.POSSUI_EMPRESTIMOS_ATRASADOS.getDescricaoCompleta());
									break;
								}
									
							}
						}	
						
						/* *********************************************************************************************************
						 * IMPORTANTE:  Se não tem nenhum pedencia até agora, seta a situção de estar sem predencias               *
						 * *********************************************************************************************************/
						if (dto.valoresSituacoesUsuario.isEmpty()){ 
							dto.valoresSituacoesUsuario.add(SituacaoUsuarioBiblioteca.SEM_PENDENCIA.getValor());
							dto.descricoesDetalhadasSituacoesUsuario.add(SituacaoUsuarioBiblioteca.SEM_PENDENCIA.getDescricaoCompleta());
						}
						
						/* **************************************************************************************
						 *                          Verificando o identificador do usuário                      *
						 *  MATRICULA se for aluno, ou SIAPE se for servidor ou CPF para os demais casos        * 
						 * **************************************************************************************/
						
						if(infoUsuario.isContemMatricula()){
							dto.matricula = infoUsuario.getMatricula() != null ? Long.parseLong(infoUsuario.getMatricula()) : null;
							dto.siape = null;
							dto.cpf = null;
						}else{
							if(infoUsuario.isContemSIAPE()){
								dto.matricula = null;
								dto.siape = infoUsuario.getSiape() != null ? Integer.parseInt(infoUsuario.getSiape()) : null;
								dto.cpf = null;
							}else{
								dto.matricula = null;
								dto.siape = null;
								
								if( infoUsuario.isContemCPF())
									dto.cpf = infoUsuario.getCPF() != null ? Long.parseLong(infoUsuario.getCPF()) : null;
								else
									dto.passaporte = infoUsuario.getPassaporte();
							}
						}
						
					} // else, se usuário não está bloqueado

					
					try {
						VerificaSituacaoUsuarioBibliotecaUtil.verificaVinculoUtilizadoAtivo(usuarioBiblioteca);
					} catch (NegocioException e) {
						dto.valoresSituacoesUsuario.add(SituacaoUsuarioBiblioteca.ESTA_INATIVO.getValor());
						dto.descricoesDetalhadasSituacoesUsuario.add("<br>"+e.getMessage());
					}
					
					dto.nome = infoUsuario.getNomeUsuario();
					dto.identificacaoVinculo = infoUsuario.getIdentificacaoVinculo();
					dto.cargo = infoUsuario.getCargo();
					dto.centro =  infoUsuario.getCentro();
					dto.curso = infoUsuario.getCurso();
					dto.idFoto = infoUsuario.getIdFoto();
					dto.lotacao = infoUsuario.getLotacao();
					dto.senha = infoUsuario.getSenha();
					
					dto.valorVinculoUsuario = infoUsuario.getVinculo().getValor();
					dto.tipoDiscente = infoUsuario.getTipoDiscente();
					dto.mobilidadeEstudantil = infoUsuario.isMobilidadeEstudantil();
					dto.iniciacaoCientifica = infoUsuario.isIniciacaoCientifica();
					
					
					if(usuarioBiblioteca.getPessoa() != null){ // se o usuário é uma pessoa
						Long cpfBusca = 0L;
						
						if(infoUsuario.isContemCPF())
							cpfBusca = Long.parseLong(infoUsuario.getCPF());
						else
							cpfBusca =  daoConsultasEmprestimos.findByPrimaryKey(usuarioBiblioteca.getPessoa().getId(), Pessoa.class, "cpf_cnpj").getCpf_cnpj();
						
						if(cpfBusca != null && cpfBusca.compareTo(0L) > 0){
							byte[][] digitais = daoConsultasEmprestimos.findDigitalUsuario(cpfBusca);
							dto.digitalDedoDireito = digitais[0];
							dto.digitalDedoEsquerdo = digitais[1];
						}
					}
					
					
				}else{ // Se usuarioBiblioteca != null
					throw new NegocioException ("Usuário não possui conta na biblioteca ou todas as suas contas estão quitadas");
				}
				
				System.out.println("Popula dados usuário demorou: " + (System.currentTimeMillis() - tempo) + "ms");
				
				return dto;
				
			}  catch (NegocioException e) {
				e.printStackTrace();
				throw new NegocioRemotoBibliotecaDesktopException (e.getMessage());
				
			} catch (Exception e) {
				
				e.printStackTrace();
					
			} finally {
				
				if (daoUsuario != null)
					daoUsuario.close();
				
				if(daoConsultasEmprestimos != null){
					daoConsultasEmprestimos.close();
				}
				
			}
			
			throw new NegocioRemotoBibliotecaDesktopException ("Erro ao recuperar as informações do usuário");
			
		} else {
			return null;
		}
	}
	
	
	
	/**
	 * 4º Método chamado no fluxo padrão, para buscar o material que vai ser emprestado
	 * 
	 * @throws Exception 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.remoto.BibliotecaCirculacaoDesktopRemoteService#findMaterialByCodigoBarras(String codigoBarras, int tipoUsuario)
	 */
	public MaterialInformacionalDto findMaterialByCodigoBarras (String codigoBarras, Integer valorVinculoUsuaro) throws NegocioRemotoBibliotecaDesktopException{

		ConsultasEmprestimoDao dao = null;
		MaterialInformacionalDto materialDto = null;

		try {
			dao = DAOFactory.getInstance().getDAO(ConsultasEmprestimoDao.class);

			MaterialInformacional material = dao.findDadosMaterialByCodigoBarras(codigoBarras);
			
			if (material == null)
				throw new NegocioException("O material com o código de barras: "+codigoBarras+" não foi encontrado.");

			materialDto = configuraInformacoesDoMaterial(material, valorVinculoUsuaro, true);
			
			return materialDto;

		} catch (NegocioException ne){
			ne.printStackTrace();
			throw new NegocioRemotoBibliotecaDesktopException(ne.getMessage());
		} catch (DAOException de) {  // encapsula o DAOException para o sistema remoto
			de.printStackTrace();
			throw new NegocioRemotoBibliotecaDesktopException(ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_REMOTA_ERRO_PADRAO));
		} catch (Exception e) {  // encapsula o DAOException para o sistema remoto
			e.printStackTrace();
			throw new NegocioRemotoBibliotecaDesktopException(ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_REMOTA_ERRO_PADRAO));
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	
	
	
	/**
	 * 5º  Método chamado para realizar os empréstimos e renovações
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.remoto.BibliotecaCirculacaoDesktopRemoteService#realizarOperacoes(java.util.ArrayList,
	 *      int, int, java.lang.String, int)
	 */
	public RetornoOperacoesCirculacaoDTO realizarOperacoes (List<EmprestimoDto> emprestimos, int idUsuario, String senhaDigitada, int idOperador, int idRegistroEntrada, int idBibliotecaOperacao) throws NegocioRemotoBibliotecaDesktopException{
		// Separa as operações que são empréstimos e as que são renovações e chama os seus respectivos
		// processadores, isso para mandar todas as operações de uma vez e não ficar executando várias
		// chamadas remotas. Como o sistema aqui é remoto, tem que otimizar um pouquinho.

		RetornoOperacoesCirculacaoDTO retorno = new RetornoOperacoesCirculacaoDTO();
		
		if (emprestimos == null || emprestimos.size() == 0)
			throw new NegocioRemotoBibliotecaDesktopException("Não há operações para serem realizadas");

		ArrayList <Integer> idsAEmprestar = new ArrayList <Integer> ();
		ArrayList <Integer> idTiposEmprestimos = new ArrayList <Integer> ();
		
		ArrayList <Integer> idsARenovar = new ArrayList <Integer> ();

		// Verifica se a operação passada foi emprestar ou renovar e pega a informação de cada material.
		for (EmprestimoDto e : emprestimos){
			switch (e.acao){
			case EmprestimoDto.ACAO_EMPRESTAR:
				idsAEmprestar.add(e.materialDto.idMaterial);
				idTiposEmprestimos.add(e.idTipoEmprestimo);
				break;
			case EmprestimoDto.ACAO_RENOVAR:
				idsARenovar.add(e.materialDto.idMaterial);
				break;
			}
		}

		// Se tem algum empréstimo a realizar, monta o mapa, que associa a id do material com a id do tipo do empréstimo.
		if (!idsAEmprestar.isEmpty()){
			Map <Integer, Integer> itensAEmprestar = new HashMap <Integer, Integer> ();
			
			for (int i = 0; i < idsAEmprestar.size(); i++)
				itensAEmprestar.put(idsAEmprestar.get(i), idTiposEmprestimos.get(i));

			RetornoOperacoesCirculacaoDTO retornoTemp = realizarEmprestimos(itensAEmprestar, idUsuario, senhaDigitada, idOperador, idRegistroEntrada, idBibliotecaOperacao);
			retorno.addOperacoesRealizadas(retornoTemp.operacoesRealizadas);
			retorno.addMensagensAosUsuarios(retornoTemp.mensagemAosUsuarios);
		}

		// Se tiver algum empréstimo a renovar
		if (!idsARenovar.isEmpty()){
			RetornoOperacoesCirculacaoDTO retornoTemp = renovarEmprestimos(idsARenovar, idUsuario, senhaDigitada, idOperador, idRegistroEntrada);
			retorno.addOperacoesRealizadas(retornoTemp.operacoesRealizadas);
			retorno.addMensagensAosUsuarios(retornoTemp.mensagemAosUsuarios);
		}

		return retorno;
	}
	
	
	/**
	 * 6º  Método chamado para devolver os materiais
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.remoto.BibliotecaCirculacaoDesktopRemoteService#devolverMaterial(MaterialInformacionalDto material, boolean permitirMulta, int idUsuario)
	 */
	public RetornoOperacoesCirculacaoDTO devolverMaterial (MaterialInformacionalDto material, int idOperador, int idBibliotecaOperacao) throws NegocioRemotoBibliotecaDesktopException{

		ConsultasEmprestimoDao dao = null;
		
		try {
			// Acessando um processador da arquitetura sem estar em um cliente web

			dao = DAOFactory.getInstance().getDAO(ConsultasEmprestimoDao.class);
			
			Usuario operador = new Usuario(idOperador);

			prepareMovimento(SigaaListaComando.DEVOLVE_EMPRESTIMO, new FacadeDelegate("ejb/SigaaFacade"), operador, Sistema.SIGAA);

			MovimentoDevolveEmprestimo mov = new MovimentoDevolveEmprestimo(material.idMaterial, operador, idBibliotecaOperacao);

			mov.setCodMovimento(SigaaListaComando.DEVOLVE_EMPRESTIMO);

			// Retorna a mensagem do processador para o sistema remoto

			RetornoOperacoesCirculacaoDTO retorno = (RetornoOperacoesCirculacaoDTO) executeWithoutClosingSession(mov, new FacadeDelegate("ejb/SigaaFacade"), operador, Sistema.SIGAA);
			
			return retorno;

		} catch (NegocioException ne){ // erros do negócio do empréstimo
			throw new NegocioRemotoBibliotecaDesktopException(ne.getMessage());
		} catch (Exception e) {      // erros que não eram para ocorrer
			e.printStackTrace();
			notifyError(e);
			throw new NegocioRemotoBibliotecaDesktopException("Ocorreu um erro ao tentar devolver o material: "+material.codigoBarras);
		} finally {
			if (dao != null)  dao.close();
		}
	}
	
	
	/**
	 * 7º Método que atualiza os empréstimos ativos do usuário depois que as operações são realizadas
	 * 
	 * @throws NegocioRemotoBibliotecaDesktopException 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.remoto.BibliotecaCirculacaoDesktopRemoteService#findEmprestimosAtivosUsuario(int)
	 */
	public List<EmprestimoDto> findEmprestimosAtivosUsuario(int idUsuario) throws NegocioRemotoBibliotecaDesktopException {
		try {
			// passa um UsuarioBibliotecaDto vazio para aproveitar o método que já existia.
			return configuraInformacoesEmprestimosAtivosUsuario(new UsuarioBiblioteca(idUsuario), new UsuarioBibliotecaDto());
		} catch (NegocioException e){
			throw new NegocioRemotoBibliotecaDesktopException(e.getMessage());
		}catch (ArqException e) {  // encapsula o DAOException
			e.printStackTrace();
			throw new NegocioRemotoBibliotecaDesktopException(ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_REMOTA_ERRO_PADRAO));
		}
	}
	
	
	/**
	 * 8º Método a ser chamado par realizar check out
	 * 
	 * @throws Exception 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.remoto.BibliotecaCirculacaoDesktopRemoteService#realizaCheckout(java.lang.String)
	 */
	public RetornoOperacoesCirculacaoDTO realizaCheckout (String codigoBarras, int idOperador) throws NegocioRemotoBibliotecaDesktopException {

		long tempo = System.currentTimeMillis();
		
		ConsultasEmprestimoDao dao = null;

		// Remove espaços.
		codigoBarras = codigoBarras.trim();

		RetornoOperacoesCirculacaoDTO retorno = new RetornoOperacoesCirculacaoDTO();
		
		
		try {
			dao = DAOFactory.getInstance().getDAO(ConsultasEmprestimoDao.class);
			
			RegistroCheckout rc = new RegistroCheckout();
			
			MaterialInformacionalDto materialDto = dao.findInformacoesCheckout(codigoBarras);
			
			if (materialDto != null){ // material buscado existe
				
				rc.setIdMaterialInformacional( materialDto.idMaterial);
				
				Usuario operador = new Usuario(idOperador);
				rc.setUsuario(operador);
				
				if (materialDto.emprestimoAtivoMaterial != null){ // material está emprestadado
					
					if (materialDto.emprestimoAtivoMaterial.status == EmprestimoDto.STATUS_ATRASADO){
						rc.setResultado(RegistroCheckout.MATERIAL_EMPRESTADO_MAS_ATRASADO);
					}else{
						rc.setResultado(RegistroCheckout.MATERIAL_EMPRESTADO);
					}
				}else{
					rc.setResultado(RegistroCheckout.MATERIAL_NAO_EMPRESTADO);
				}
	
				prepareMovimento(ArqListaComando.CADASTRAR, new FacadeDelegate("ejb/SigaaFacade"), operador, Sistema.SIGAA);
				
				/////////////    Registra o checkout no Banco   ///////////////
				MovimentoCadastro mov = new MovimentoCadastro ();
				mov.setCodMovimento(ArqListaComando.CADASTRAR);
				mov.setUsuarioLogado(operador);
				mov.setUsuario(operador);
				mov.setObjMovimentado(rc);
				
				// Registra a consulta.
				executeWithoutClosingSession(mov, new FacadeDelegate("ejb/SigaaFacade"), operador, Sistema.SIGAA);
			
			}
			
			retorno.materialRetornado = materialDto;
			
			System.out.println("Realizar Checkout Demorou: "+(System.currentTimeMillis()-tempo)+" ms");
			
			return retorno;

		} catch (NegocioException e){
			throw new NegocioRemotoBibliotecaDesktopException (e.getMessage());
		} catch (ArqException e) {  // Encapsula as exceptions para o sistema remoto.
			e.printStackTrace();
			throw new NegocioRemotoBibliotecaDesktopException(ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_REMOTA_ERRO_PADRAO));
		} finally {
			if (dao != null) dao.close();
		}
	}

	/**
	 * 9º  método a ser chamdo desfaz operações erradas
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.remoto.BibliotecaCirculacaoDesktopRemoteService#desfazOperacao(int, int, java.lang.String, java.lang.String)
	 */
	public void desfazOperacao (int idEmprestimo, int tipoOpecacao, String loginChefe, String senhaChefe, int idUsuarioOperador, String operacionalSystem) throws NegocioRemotoBibliotecaDesktopException {

		// Verifica se o login e a senha digitadas foram de algum usuário que possui o papel de chefe
		// da seção de circulação, se sim, pode desfazer a operação.

		UsuarioMov userMov = new UsuarioMov();
		userMov.setCodMovimento(SigaaListaComando.LOGON);

		Usuario usuario = new Usuario();
		usuario.setLogin(loginChefe);
		usuario.setSenha(senhaChefe);
		userMov.setUsuario(usuario);
		userMov.setUserAgent(USER_AGENTE+" "+operacionalSystem);
		userMov.setIP("127.0.0.1");  // Não precisa de ip porque aqui é só autenticar o usuário, não faz logon no sistema.

		Usuario user = null;
		
		try {
			userMov.setHost(InetAddress.getLocalHost().getHostName());
			user = (Usuario) executeWithoutClosingSession(userMov, new FacadeDelegate("ejb/SigaaFacade"), usuario, Sistema.SIGAA);
		} catch (NegocioException e){
			throw new NegocioRemotoBibliotecaDesktopException (e.getMessage());
		} catch (Exception e){
			throw new NegocioRemotoBibliotecaDesktopException (ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_REMOTA_ERRO_PADRAO));
		}

		if (user != null && user.isAutorizado()){
			if(user.isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO)) {

				// Autorizado a desfazer a operação.

				try{

					prepareMovimento(SigaaListaComando.DESFAZ_OPERACAO, new FacadeDelegate("ejb/SigaaFacade"), user, Sistema.SIGAA);

					MovimentoDesfazOperacao mov = new MovimentoDesfazOperacao(tipoOpecacao, idEmprestimo, user.getId(), idUsuarioOperador);

					mov.setCodMovimento(SigaaListaComando.DESFAZ_OPERACAO);

					executeWithoutClosingSession(mov, new FacadeDelegate("ejb/SigaaFacade"), user, Sistema.SIGAA);

				} catch (NegocioException ne){
					throw new NegocioRemotoBibliotecaDesktopException(ne.getMessage());
				} catch (Exception e){
					throw new NegocioRemotoBibliotecaDesktopException(ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_REMOTA_ERRO_PADRAO));
				}

			} else
				throw new NegocioRemotoBibliotecaDesktopException(ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_REMOTA_ERRO_SEM_AUTORIZACAO_DESFAZER_OPERACAO));
		} else
			throw new NegocioRemotoBibliotecaDesktopException("Usuário não autorizado a realizar esta operação");
	}

	
	
	
	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.remoto.BibliotecaCirculacaoDesktopRemoteService#generateFotoKey(int)
	 */
	public String generateFotoKey(int idFoto) {
		return UFRNUtils.generateArquivoKey(idFoto);
	}
	
	
	
	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.remoto.BibliotecaCirculacaoDesktopRemoteService#populaDadosUsuarioUsandoDigital(byte[], int)
	 */
	public UsuarioBibliotecaDto populaDadosUsuarioUsandoDigital (byte [] digital) throws NegocioRemotoBibliotecaDesktopException {

		// verifica se a digital está cadastrada
		Long cpf = 0L;

		ConsultasEmprestimoDao dao = null;
		
		try {
			
			cpf = identificador.identificar(digital); // Encontra o cpf da pessoa que tem a digital cadastrada	
			
			if (cpf != null && cpf > 0) {  // Se encontrou o usuário.

				
				
				dao = DAOFactory.getInstance().getDAO(ConsultasEmprestimoDao.class);
				
				Integer idUsuarioBiblioteca = dao.findIdUsuarioBibliotecaAtivoNaoQuitadoByCpf(cpf);
				
				if(idUsuarioBiblioteca == null || idUsuarioBiblioteca.equals(0)){
					UsuarioBibliotecaDto usuarioDto = new UsuarioBibliotecaDto();
					usuarioDto.idUsuarioBiblioteca = 0; // null quer dizer: usuário não cadastradado para utilizar a biblioteca ou todas as contas quitadas
					return usuarioDto;
				}else{
					UsuarioBibliotecaDto usuarioDto = new UsuarioBibliotecaDto();
					usuarioDto.idUsuarioBiblioteca = idUsuarioBiblioteca;
					return  populaDadosUsuario( usuarioDto );
				}
				
				
			} else{
				// throw new NegocioRemotoBibliotecaException("Digital do usuário não está cadastrada ou não foi identificada corretamente."); // Não pode usar porque dá erro no webservice no lado do cliente
				return null; // null quer dizer: Digital do usuário não está cadastrada ou não foi identificada corretamente
			}
	
		} catch (RemoteAccessException e) {
			throw new NegocioRemotoBibliotecaDesktopException(ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_REMOTA_ERRO_PADRAO));
		} catch (DAOException e) {
			throw new NegocioRemotoBibliotecaDesktopException(ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_REMOTA_ERRO_PADRAO));
		} finally {
			if (dao != null) dao.close();
		}
	}
	
	

	
	/**
	 * Ver comentários da classe pai.<br/>
	 * @throws  
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.remoto.BibliotecaCirculacaoDesktopRemoteService#populaDadosUsuarioUsandoDigital(byte[], int)
	 */
	public boolean cadastraDigitalUsuario (int idUsuarioBiblioteca, byte [] digital, String tipoDedo, int idOperador) throws NegocioRemotoBibliotecaDesktopException {

		UsuarioBibliotecaDao dao = null;
		
		if(digital == null){
			throw new NegocioRemotoBibliotecaDesktopException("A digital do usuário não foi capturada corretamente.");
		}
		
		try{
			dao = DAOFactory.getInstance().getDAO(UsuarioBibliotecaDao.class);
	
			Long cpf = dao.findCPFPessoaByUsuarioBibliotecaq(idUsuarioBiblioteca);
			
			if(cpf == null)
				throw new NegocioRemotoBibliotecaDesktopException("Não é possível cadastrar a digital do usuário porque ele não possui CPF.");
			
			IdentificacaoPessoaRemoteServiceImpl identificacao = new IdentificacaoPessoaRemoteServiceImpl();
			return identificacao.gravarOuAtualizarIdentificacao(cpf, null, digital, tipoDedo, idOperador, null);
			
		} catch (DAOException e) {
			throw new NegocioRemotoBibliotecaDesktopException(ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_REMOTA_ERRO_PADRAO));
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	
	
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////  Método privados //////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	 * Realiza o empréstimo depois que a situação do usuário foi
	 * validada e o material foi verificado.
	 * 
	 * IMPORTANTE: Era bom já passar a senha validada no cliente para evitar de
	 * realizar várias chamadas caso o usuário digite a senha inválida, isso
	 * porque aqui as chamadas são remotas e isso tem um custo alto.
	 *
	 * @param idsTiposEmprestimos
	 *            uma array com os Códigos de Barras dos materiais que se
	 *            desejam emprestar[0][n] e seus repectivos tipos de empréstimos [1][m]
	 *  // na posição 0 tem sempre o id do Material e na posição 1
	 * sempre o tipo de empréstimo // [0][0] código de barras do primeiro item, [0][1]
	 * tipo de empréstimo do primeiro item // [1][0] código de barras do segundo item,
	 * [1][1] tipo de empréstimo do segundo item, etc...
	 *              
	 * @param idUsuario
	 *            indica qual o usuário que vai realizar o empréstimo
	 * @param tipoUsuario
	 *            o tipo do usuário para validar as suas informações sobre o
	 *            empréstimo
	 * @param senhaUsuario
	 *            para liberar o empréstimo, um int porque a senha da biblioteca
	 *            é diferente da senha do sistema e é numérica.
	 *            
	 * @param identificacaoUsuario que o usuário usou para fazer o emprétimo (idDiscente ou idServidor ou idDocenteExterno ou idUsuarioExterno ou idBiblioteca)
	 * @param idBibliotecaOperacao  o id da biblioteca que o usuário está operando no dekstop             
	 *              
	 */
	private RetornoOperacoesCirculacaoDTO realizarEmprestimos (Map <Integer, Integer> idsTiposEmprestimos, int idUsuarioBiblioteca, String senhaUsuario, int idOperador, int idRegistroEntrada, int idBibliotecaOperacao)  throws NegocioRemotoBibliotecaDesktopException{

		UsuarioBibliotecaDao dao = null;
		MovimentoRealizaEmprestimo mov = null;
		ConsultasEmprestimoDao daoConsultasEmprestimos = null;
		
		try {

			// Acessando um processador da arquitetura sem estar em um cliente web.
			// Por enquanto o usuário que vai ser usado é o mesmo que está realizando o empréstimo,
			// mas no futuro deve ser o operador do sistema, para logar as suas ações.

			dao = DAOFactory.getInstance().getDAO(UsuarioBibliotecaDao.class);
			daoConsultasEmprestimos = DAOFactory.getInstance().getDAO(ConsultasEmprestimoDao.class);
			
			Usuario operador = new Usuario(idOperador);
			
			RegistroEntrada registro = daoConsultasEmprestimos.findByPrimaryKey(idRegistroEntrada, RegistroEntrada.class, "id");
			
			if(operador != null)
				operador.setRegistroEntrada(registro);  // Para poder usar a auditoria do sistema.
			
			// Prepara o movimento.
			prepareMovimento(SigaaListaComando.REALIZA_EMPRESTIMO, new FacadeDelegate("ejb/SigaaFacade"), operador, Sistema.SIGAA);

			/* *************************************************************
			 *  Busca o usuário da biblioteca.
			 *  
			 *  Só precisa das seguintes informaçoes para realizar o empréstimo:
			 *  
			 *  id
			 *  senha
			 *  bloqueado
			 *  Pessoa (id)
			 *  Biblioteca (id , (Unidade (id) )  )
			 *  
			 */
			UsuarioBiblioteca usuarioBiblioteca = daoConsultasEmprestimos.findInformacoesUsuarioBibliotecaNaoQuitado(idUsuarioBiblioteca);
			
			mov = new MovimentoRealizaEmprestimo(idsTiposEmprestimos, usuarioBiblioteca, senhaUsuario, null, idBibliotecaOperacao);
			mov.setUsuarioLogado(operador);
			
			// Retorna a mensagem do processador para o sistema remoto.
			return (RetornoOperacoesCirculacaoDTO) executeWithoutClosingSession(mov, new FacadeDelegate("ejb/SigaaFacade"), operador, Sistema.SIGAA);
		

		} catch (NegocioException ne){ // Erros do negócio do empréstimo.
			
			throw new NegocioRemotoBibliotecaDesktopException(ne.getMessage(), mov.getIdMaterialProcessando());
			
		} catch (Exception e) {      // Erros que não eram para ocorrer.
			throw new NegocioRemotoBibliotecaDesktopException(ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_REMOTA_ERRO_PADRAO));
		} finally {
			if (dao != null)
				dao.close();
			
			if (daoConsultasEmprestimos != null)
				daoConsultasEmprestimos.close();
		}
	}

	/**
	 * Realiza a renovação de um empréstimo.
	 * 
	 * @param idsMateriais
	 *            o código de barras dos itens cujos empréstimos vão ser
	 *            renovados.
	 * @param idUsuario
	 *            indica qual o usuário que vai renovar o empréstimo
	 * @param tipoUsuario
	 *            o tipo do usuário para validar as suas informações sobre o
	 *            empréstimo
	 * @param senhaDigitada
	 *            para liberar o empréstimo, um int porque a senha da biblioteca
	 *            é diferente da senha do sistema e é numérica.
	 *            
	 * @param identificacaoUsuario que o usuário usou para fazer o emprétimo (idDiscente ou idServidor ou idDocenteExterno ou idUsuarioExterno ou idBiblioteca)           
	 *        
	 * @return uma String com a resposta da solicitação para ser mostrada ao
	 *         usuário no sistema desktop. exemplo: "Renovação realizada",
	 *         "Senha inválida"
	 */
	private RetornoOperacoesCirculacaoDTO renovarEmprestimos (List <Integer> idsMateriais, int idUsuarioBiblioteca, String senhaDigitada, int idOperador, int idRegistroEntrada) throws NegocioRemotoBibliotecaDesktopException{

		
		MovimentoRenovaEmprestimo mov = null;
		ConsultasEmprestimoDao daoConsultasEmprestimos = null;
		try {

			// Acessando um processador da arquitetura sem estar em um cliente web.
			// Por enquanto o usuário que vai ser usado é o mesmo que está
			// realizando a renovação, mas no futuro deve ser o operador do sistema,
			// para logar as suas ações.

			daoConsultasEmprestimos = DAOFactory.getInstance().getDAO(ConsultasEmprestimoDao.class);

			Usuario operador = new Usuario(idOperador);
			RegistroEntrada registro = daoConsultasEmprestimos.findByPrimaryKey(idRegistroEntrada, RegistroEntrada.class, "id");
			
			if(operador != null)
				operador.setRegistroEntrada(registro);  // Para poder usar a auditoria do sistema.
			
			// Busca o usuário da biblioteca.
			UsuarioBiblioteca usuarioBiblioteca = daoConsultasEmprestimos.findInformacoesUsuarioBibliotecaNaoQuitado(idUsuarioBiblioteca); 
			

			prepareMovimento(SigaaListaComando.RENOVA_EMPRESTIMO, new FacadeDelegate("ejb/SigaaFacade"), operador, Sistema.SIGAA);

			mov = new MovimentoRenovaEmprestimo(idsMateriais, usuarioBiblioteca, senhaDigitada);
			mov.setUsuarioLogado(operador);

			mov.setCodMovimento(SigaaListaComando.RENOVA_EMPRESTIMO);

			
			return (RetornoOperacoesCirculacaoDTO) executeWithoutClosingSession(mov, new FacadeDelegate("ejb/SigaaFacade"), operador, Sistema.SIGAA);

		} catch (NegocioException ne){ // Erros de negócio do empréstimo.
			
			throw new NegocioRemotoBibliotecaDesktopException(ne.getMessage(), mov.getIdMaterialProcessando());
			
		} catch (Exception e) {      // Erros que não eram para ocorrer.
			throw new NegocioRemotoBibliotecaDesktopException(ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_REMOTA_ERRO_PADRAO));
		} finally {
			if (daoConsultasEmprestimos != null)
				daoConsultasEmprestimos.close();
		}
	}
	
	

	
	
	/**
	 * Encontra os empréstimos ativos e cria objetos EmprestimoDto para passar
	 * ao cliente desktop
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	private List <EmprestimoDto> configuraInformacoesEmprestimosAtivosUsuario(UsuarioBiblioteca usuarioBiblioteca, UsuarioBibliotecaDto uDto) throws ArqException, NegocioException {
		//List <EmprestimoDto> emprestimosDto =  new ArrayList <EmprestimoDto> ();
		
		if (usuarioBiblioteca == null)
			usuarioBiblioteca = new UsuarioBiblioteca(uDto.idUsuarioBiblioteca);

		ConsultasEmprestimoDao dao = null;
		
		try {
			dao = DAOFactory.getInstance().getDAO(ConsultasEmprestimoDao.class);
			return dao.findEmprestimosDtoAtivoByUsuarioBiblioteca(usuarioBiblioteca);
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	
	/**
	 *  Encontra as informações do título do material para mostrar ao usuário.
	 *  
	 * @throws NegocioException 
	 */
	private MaterialInformacionalDto configuraInformacoesDoMaterial (MaterialInformacional material, Integer valorVinculoUsuaro, boolean buscarEmprestimo) throws DAOException, NegocioException {

		if (material == null)
			return null;

		MaterialInformacionalDto mDto = new MaterialInformacionalDto();

		mDto.idMaterial = material.getId();

		mDto.codigoBarras = material.getCodigoBarras();

		mDto.situacao = material.getSituacao().getDescricao();
		
		EmprestimoDao dao = null;
		
		
		// Busca com o usuário definido no desktop, então busca os tipos de empréstimos que pode fazer.
		if(valorVinculoUsuaro != null){
		
			VinculoUsuarioBiblioteca vinculo = VinculoUsuarioBiblioteca.getVinculo(valorVinculoUsuaro);
		
			if(vinculo != null){ // O usuário pode não existir se está buscando o material para devolução.
				mDto.tiposEmprestimos = configuraTiposDeEmprestimos (material.getBiblioteca().getId(), vinculo, material.getStatus().getId(), material.getTipoMaterial().getId());		
			}
		}
		
		
		
		try {
			dao = DAOFactory.getInstance().getDAO(EmprestimoDao.class);
			
			SituacaoMaterialInformacional situacaoDisponivel = dao.findByExactField(SituacaoMaterialInformacional.class, "situacaoDisponivel", true, true);
			SituacaoMaterialInformacional situacaoEmprestado = dao.findByExactField(SituacaoMaterialInformacional.class, "situacaoEmprestado", true, true);
		
		if (material.getSituacao().getId() == situacaoDisponivel.getId()){
			mDto.estaDisponivel = true;
			
			
			
			// Monta as informações sobre as políticas de empréstimos possíveis
			if(mDto.tiposEmprestimos != null && mDto.tiposEmprestimos.size() > 0){
				
				Integer[] idsTiposEmprestimos = new Integer[mDto.tiposEmprestimos.size()];
				for (int index = 0 ; index < mDto.tiposEmprestimos.size() ; index++) {
					idsTiposEmprestimos[index] = mDto.tiposEmprestimos.get(index).idTipoEmprestimo;
				}
				
				mDto.informacoesPoliticasEmprestimos = 
					CirculacaoUtil.retornaDescricaoPoliticaEmpretimoASerUtilizada( 
							material.getBiblioteca().getId(), 
							VinculoUsuarioBiblioteca.getVinculo(valorVinculoUsuaro), 
							material.getStatus().getId(),
							material.getTipoMaterial().getId(),
							idsTiposEmprestimos);
			}
			
		}else 
			if (material.getSituacao().getId() == situacaoEmprestado.getId()){
				
				mDto.estaEmprestado = true;
				
				if (buscarEmprestimo){
					
					Emprestimo e = dao.findEmprestimoCirculacao(material.getId(), false, true);
					
					if (e == null)
						throw new NegocioException ("Houve uma inconsistência no banco. Apesar de o material estar emprestado, não foi encontrado um empréstimo ativo para ele.");
						
					SimpleDateFormat sdf = null;
					if (e.getPoliticaEmprestimo().isPrazoContadoEmHoras())
						sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					else
						sdf = new SimpleDateFormat("dd/MM/yyyy");
					
					mDto.situacao += " ("+e.getPoliticaEmprestimo().getTipoEmprestimo().getDescricao()+") Prazo: " + sdf.format(e.getPrazo());
					
					if(e.getUsuarioBiblioteca().getPessoa() != null){
						mDto.nomeUsuarioEmpretimo = e.getUsuarioBiblioteca().getPessoa().getNome();
					}else{
						mDto.nomeUsuarioEmpretimo = e.getUsuarioBiblioteca().getBiblioteca().getDescricao();
					}
					
					
					mDto.informacoesPoliticasEmprestimos = CirculacaoUtil.retornaDescricaoPoliticaEmpretimoASerUtilizada( 
							material.getBiblioteca().getId(), 
							e.getUsuarioBiblioteca().getId(), 
							material.getStatus().getId(),
							e.getPoliticaEmprestimo().getTipoEmprestimo().getId());
				}
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		mDto.biblioteca = material.getBiblioteca().getDescricao();
		mDto.tituloDto = montaTituloCatalograficoDto(material);
		mDto.status = material.getStatus().getDescricao();
		mDto.colecao = material.getColecao().getDescricao();
		
		
		
		// Não estava sendo usado
//		if (material instanceof Exemplar){
//			Exemplar pai = ((Exemplar) material).getExemplarDeQuemSouAnexo();
//			if (pai != null)
//				mDto.codigoBarrasPai = pai.getCodigoBarras();
//		}
		
		return mDto;
	}

	
	
	/**
	 * Retorna os tipos de empréstimos, que o usuário pode realizar com o status do material informado, na biblioteca informada.
	 * 
	 * @param tipoUsuario
	 * @param idStatusMaterial
	 * @param idBiblioteca
	 * @return
	 * @throws DAOException
	 */
	private List <TipoEmprestimoDto> configuraTiposDeEmprestimos(int idBiblioteca, VinculoUsuarioBiblioteca vinculo, int idStatusMaterial, int idTipoMaterial) throws DAOException {

		TipoEmprestimoDao dao = null;
		
		try {
			dao = DAOFactory.getInstance().getDAO(TipoEmprestimoDao.class);
		
			// Adiciona os tipos de empréstimo existentes para o desktop saber quais
			// exibir para que o operador não tente realizar uma operação que não pode.
			Collection <TipoEmprestimo> tes = dao.findTiposEmprestimoByInformacoesPoliticas(idBiblioteca, vinculo, idStatusMaterial, idTipoMaterial, false, false);
	
			ArrayList <TipoEmprestimoDto> tiposEmprestimos = new ArrayList <TipoEmprestimoDto>();
	
			for (TipoEmprestimo t : tes){
				TipoEmprestimoDto te = new TipoEmprestimoDto();
				te.idTipoEmprestimo = t.getId();
				te.descricao = t.getDescricao();
				tiposEmprestimos.add(te);
			}
	
			return tiposEmprestimos;
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	
	/**
	 * Monta e retorna o TítuloCatalograficoDto do título do material passado.
	 * 
	 * @param material
	 * @param iDto
	 * @return
	 * @throws DAOException
	 */
	private TituloCatalograficoDto montaTituloCatalograficoDto(MaterialInformacional material) throws DAOException {
		GenericDAO dao = null;
		try {
			dao = DAOFactory.getGeneric(Sistema.SIGAA);
			TituloCatalografico t = material.getTituloCatalografico();
			
			TituloCatalograficoDto titulo = new TituloCatalograficoDto();
			
			CacheEntidadesMarc c = dao.findByExactField(CacheEntidadesMarc.class, "idTituloCatalografico", t.getId(), true);

			titulo.autor = c.getAutor();
			titulo.ano = c.getAno();
			titulo.edicao = c.getEdicao();
			titulo.titulo = c.getTitulo();
			titulo.idTituloCatalografico = c.getIdTituloCatalografico();
			
			return titulo;
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	
}