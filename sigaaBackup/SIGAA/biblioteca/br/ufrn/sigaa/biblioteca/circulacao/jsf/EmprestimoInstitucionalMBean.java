package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TipoEmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoDevolveEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoEstornaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoRealizaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoRenovaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.SituacaoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.integracao.dtos.EmprestimoDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.OperacaoBibliotecaDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.RetornoOperacoesCirculacaoDTO;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;
import br.ufrn.sigaa.biblioteca.util.UsuarioBibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;

/**
 * Classe que gerencia o empréstimo institucionais entre bibliotecas (internas e externas ).
 * @see Emprestimo
 * @author Fred_Castro
 */

@Component("emprestimoInstitucionalMBean")
@Scope("request")
public class EmprestimoInstitucionalMBean extends SigaaAbstractController<Emprestimo> {

	/** Página para realizar empréstimos institucional  */
	public static final String PAGINA_FORMULARIO = "/biblioteca/EmprestimoInstitucional/form.jsp";
	
	/** Página que lista os empréstimos institucionais  */
	public static final String PAGINA_LISTA = "/biblioteca/EmprestimoInstitucional/lista.jsp";
	
	
	/** Guarda a lista de bibliotecas externas para as quais o operador pode fazer emprétimo */
	private Collection<SelectItem> bibliotecasExternas = new ArrayList<SelectItem>();
	
	/** Guarda a lista de bibliotecas internas para as quais o operador pode fazer emprétimo */
	private Collection<SelectItem> bibliotecasInternas = new ArrayList<SelectItem>();
	
	/** Guarda a lista de materiais que o operador selecionou para o empréstimo */
	private List<MaterialInformacional> materiais = new ArrayList <MaterialInformacional>();


	/** Situação dos empréstimos institucionais que o usuário pode buscar */
	public static final int SITUACAO_ATIVA = 1;
	/** Situação dos empréstimos institucionais que o usuário pode buscar */
	public static final int SITUACAO_DEVOLVIDO = 2;
	/** Situação dos empréstimos institucionais que o usuário pode buscar */
	public static final int SITUACAO_AMBAS = 3;
	
	/**
	 * A biblioteca para onde os emprestimos vão ser feitos, também usada no formulário de busca para 
	 * buscar os empréstimo dessa biblioteca
	 */
	private Biblioteca biblioteca;
	
	/**
	 * O tipo de empréstimo que vai ser feito
	 */
	private TipoEmprestimo tipoEmprestimoEscolhido;
	
	/**
	 *  Identifica quando o sistema deve mostrar um campo para o usuário digitar o prazo
	 */
	private boolean tipoEmprestimoPersonalizado = false;
	
	/**
	 * O valor digita para o prazo do empréstimos, caso o empréstimo seja personalizável
	 */
	private Integer diasAEmprestar;
	
	/**
	 * O material qeu vai ser buscado para realizar o empréstimo ou devolução
	 */
	private MaterialInformacional material;
	
	/**
	 * O código de barras digitado pelo usuário na tela para buscar o material.
	 */
	private String codigoBarras;
	
	/** A data de início do período em que se está buscar os empréstimos institucionais */
	private Date dataInicio;
	
	/** A data de fim do período em que se está buscar os empréstimos institucionais */
	private Date dataFim;
	
	/** Indica qual a situação do empréstimos se está buscando */
	private int situacao = SITUACAO_ATIVA;
		
	/**
	 * Data model da lista de materiais que vão ser emprestados
	 */
	private DataModel dmMateriais;

	/**
	 *  Indica se o empréstimo é para biblioteca externa
	 */
	private boolean emprestimoParaBibliotecaExterna;

	
	/** Os tipos de empréstimos que o usuário pode fazer */
	private List<TipoEmprestimo> tiposEmprestimos;
	
	/** As informações do usuário da biblioteca*/
	private InformacoesUsuarioBiblioteca infoUsuario;
	
	/**
	 * O usuário biblioteca da biblioteca
	 */
	private UsuarioBiblioteca usuarioBiblioteca;
	
	/** A situação em que o usuário se encontra, pode emprestar, está suspenso, etc ... */
	private List<SituacaoUsuarioBiblioteca> situacoesUsuario;
	
	/** Se foi feito alguma renovação, habilita a impressão do comprovante */
	private boolean habilidaComprovanteRenovacao = false;
	
	/** Se foi feito alguma devolução, habilita a impressão do comprovante */
	private boolean habilidaComprovanteDevolucao = false;
	
	/** Guarda o emprétimo que foi devolvido para poder emitir o comprovante de devolução. Usa um DTO porque é compartinhado com a devolução desktop*/
	private EmprestimoDto emprestimoDevolvido;
	
	/** Mensagem mostradas no comprovante de devolução, dependem da estratégia de punição: usuário suspenso até... usuário multado em ...*/
	private List<String> mensagensComprovanteDevolucao;
	
	/** Guarda a lista de empréstimo renovados para poder emitir o comprovante de renovação */
	private List<OperacaoBibliotecaDto> emprestimosRenovadosOp;
	
	/**
	 * Construtor padrão
	 * @throws DAOException
	 */
	public EmprestimoInstitucionalMBean() throws DAOException {
		
	}
	
	
	/**
	 * Inicia o caso de uso exibindo a página de listagem
	 * 
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/circulacao.jsp</li>
	 *    <li>/sigaa.war/biblioteca/menus/informacao_referencia.jsp</li>
	 *   </ul>
	 * @throws SegurancaException 
	 */
	@Override
	public String listar() throws DAOException, SegurancaException {
		
		habilidaComprovanteRenovacao = false;
		habilidaComprovanteDevolucao = false;
		
		tiposEmprestimos = getTiposEmprestimosBiblioteca();
		
		iniciarDadosPesquisa();
		return telaListaEmprestimosInstitucionais();
	}

	
	/**
	 * Busca os tipos de empréstios que o usuário pode realizar para uma biblioteca, dependendo do papel que o usuário tenha.
	 *
	 * @return
	 * @throws DAOException
	 */
	private List<TipoEmprestimo> getTiposEmprestimosBiblioteca() throws DAOException{
		if(tiposEmprestimos == null){
			TipoEmprestimoDao dao = null;
			
			try{
				dao = getDAO(TipoEmprestimoDao.class);
				
				if(getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL
						, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO)){
				
					tiposEmprestimos = dao.findTiposEmprestimoParaBiblioteca(true);
					
				}else{ // não pode fazer empréstimos personalizado, somente o INSTITUCIONAL
					
					tiposEmprestimos = dao.findTiposEmprestimoParaBiblioteca(false);
				}
				
			}finally{
				if(dao != null) dao.close();
			}
		}
		return tiposEmprestimos;
	}
	

	/**
	 * Inicia os dados da página de listagem dos emprétimos institucionais
	 * 
	 * @throws DAOException
	 */
	private void iniciarDadosPesquisa() throws DAOException {
		
		if (getParameter("emprestimoParaBibliotecaExterna") != null)
			emprestimoParaBibliotecaExterna = getParameterBoolean("emprestimoParaBibliotecaExterna");

		
		habilidaComprovanteRenovacao = false;
		habilidaComprovanteDevolucao = false;

		zeraDadosBiblioteca();
		
		situacao = SITUACAO_ATIVA;
		dataInicio = null;
		dataFim = null;
		
		materiais = new ArrayList<MaterialInformacional>();
		dmMateriais = new ListDataModel();

		tipoEmprestimoEscolhido = new TipoEmprestimo(-1);
		tipoEmprestimoPersonalizado = false;
		
		
		BibliotecaDao dao = null;
		
		try {
			dao = getDAO(BibliotecaDao.class);

			if (emprestimoParaBibliotecaExterna) {
				bibliotecasExternas = new ArrayList <SelectItem> ();
				Collection<Biblioteca> bEs = dao.findAllBibliotecasExternasAtivas();
				for (Biblioteca b : bEs)
					bibliotecasExternas.add(new SelectItem(b.getId(), b.getDescricaoCompleta()));
			} else {
				bibliotecasInternas = new ArrayList <SelectItem> ();
				Collection<Biblioteca> bIs = dao.findAllBibliotecasInternasAtivas();
				for (Biblioteca b : bIs)
					bibliotecasInternas.add(new SelectItem(b.getId(), b.getDescricaoCompleta()));
			}
		} finally {
			if (dao != null)
				dao.close();
		}	
		
	}

	/**
	 * Valida se o usuário preencheu os dados corretamente.
	 * @return
	 */
	private ListaMensagens validate(){
		ListaMensagens mensagens = new ListaMensagens();
		
		if ((biblioteca == null || biblioteca.getId() == 0))
			mensagens.addErro("Selecione uma biblioteca.");
		
		if (materiais == null || materiais.isEmpty())
			mensagens.addErro("Adicione pelo menos um material a ser emprestado.");
		
		if (tipoEmprestimoEscolhido == null || tipoEmprestimoEscolhido.getId() < 0)
			mensagens.addErro("Selecione o tipo do empréstimo");
		
		if (getIdTipoEmprestimoPersonalizado() == tipoEmprestimoEscolhido.getId()){
			if(diasAEmprestar == null || diasAEmprestar <= 0)
				mensagens.addErro("Informe a quantidade de dias dos empréstimos");
			
			try {
				confirmaSenha();
			} catch (ArqException e) {
				addMensagemErro("Não foi possível confirmar a senha do operado para realizar o empréstimo personalizado");
			}
		}
		
		return mensagens;
	}
	
	/**
	 * Prepara o form de empréstimo institucional.
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmprestimoInstitucional/lista.jsp</li>
	 *   </ul>
	 * 
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {

		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO
				, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		iniciarDadosPesquisa();
		
		zeraDadosBiblioteca();
		
		prepareMovimento(SigaaListaComando.REALIZA_EMPRESTIMO);
		setConfirmButton("Realizar Empréstimos");
		return forward(PAGINA_FORMULARIO);
	}

	
	
	/**
	 * Preenche o usuário de acordo com o id passado.
	 * Chamado no método iniciar(); 
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws SegurancaException 
	 */
	private void verificaSituacaoUsuario (UsuarioBiblioteca usuarioBiblioteca) throws DAOException{
		
		if(usuarioBiblioteca == null){
			addMensagemErro("Nenhum usuário selecionado");
			return;
		} 
			
		situacoesUsuario = new ArrayList<SituacaoUsuarioBiblioteca>();
		
		UsuarioBibliotecaDao daoUsuario = null;
	
		try {
			
			daoUsuario = DAOFactory.getInstance().getDAO(UsuarioBibliotecaDao.class);
			
			String motivoBloqueio = VerificaSituacaoUsuarioBibliotecaUtil.getMotivoBloqueadoUsuario(usuarioBiblioteca);
			
			if(motivoBloqueio != null){ // se está bloqueado não vai conseguir fazer emprétimos, então não precisa verificar o resto
				SituacaoUsuarioBiblioteca situacaoBloqueio = SituacaoUsuarioBiblioteca.ESTA_BLOQUEADO;
				situacaoBloqueio.setDescricaoCompleta(situacaoBloqueio.getDescricaoResumida()+" Motivo: "+motivoBloqueio );
				situacoesUsuario.add(situacaoBloqueio);
			}else{
			
				situacoesUsuario.addAll( VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiPunicoesBiblioteca(
						usuarioBiblioteca.getIdentificadorPessoa()
						, usuarioBiblioteca.getIdentificadorBiblioteca() ));
				situacoesUsuario.addAll( VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiEmprestimosEmAbertoOUAtrasadosBiblioteca(usuarioBiblioteca.getId()) );
			
				if(situacoesUsuario.isEmpty()){
					situacoesUsuario.add(SituacaoUsuarioBiblioteca.SEM_PENDENCIA);
				}
			}
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}finally{
			if (daoUsuario != null)
				daoUsuario.close();
		}

	}
	
	
	
	/**
	 * Realiza os empréstimos para uma biblioteca Chamado na página
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmprestimoInstitucional/form.jsp</li>
	 *   </ul>
	 * 
	 * 
	 * @throws ArqException
	 */
	public String realizarEmprestimos() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO
				, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		addMensagens(validate());
		
		habilidaComprovanteRenovacao = false;
		habilidaComprovanteDevolucao = false;
		
		if (!hasErrors()){
			
			UsuarioBibliotecaDao dao = null;
			
			try {
		
				dao = getDAO(UsuarioBibliotecaDao.class);
				
				MovimentoRealizaEmprestimo mov = null;
				
				biblioteca = dao.refresh(biblioteca);
				
				UsuarioBiblioteca usuarioBiblioteca = UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaBiblioteca(biblioteca.getId(), dao);
				
				Map <Integer, Integer> mapa = new HashMap <Integer, Integer> ();
				
				for (MaterialInformacional m : materiais)
					mapa.put(m.getId(), tipoEmprestimoEscolhido.getId());
				
				// Sempre passa a senha o usuário biblioteca porque biblioteca não precisa confirmar a senha //
				mov = new MovimentoRealizaEmprestimo(mapa, usuarioBiblioteca, "", diasAEmprestar, null);
	
				RetornoOperacoesCirculacaoDTO retorno =  execute(mov);
				
				for (String mensagem : retorno.mensagemAosUsuarios) {
					addMensagemWarning(mensagem);
				}
				
				for (OperacaoBibliotecaDto emprestimos : retorno.operacoesRealizadas) {
					addMensagemInformation(emprestimos.infoMaterial+"<br/> Prazo para Devolução: "+emprestimos.getPrazoFormatado());
				}
				
				addMensagemInformation("Empréstimos realizados com sucesso.");

				zeraDadosBiblioteca();
				all = null;
				
				return forward(PAGINA_LISTA);
				
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				
				// Como o processador pode ter dado refresh em algum material, devem-se montar os formatos de referência.
				for (MaterialInformacional m : materiais){
					m.setInformacao(  BibliotecaUtil.obtemDadosMaterialInformacional(  m.getId() ) );
				}
			} finally {
				if (dao != null)
					dao.close();
			}
		}

		return null;
	}


	/**
	 * Apaga os dados da biblioteca buscada
	 *
	 */
	private void zeraDadosBiblioteca() {
		biblioteca = new Biblioteca();
		infoUsuario = null;
		usuarioBiblioteca = null;
	}

	/**
	 * 
	 * Método que realiza a devolução do emprestimos institucional
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmprestimoInstitucional/lista.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String renovar() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO
				, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		// Pega a id do empréstimo, passada por parâmetro, e popula o obj.
		obj = new Emprestimo();
		populateObj(true);
		
		emprestimosRenovadosOp = null;
		
		habilidaComprovanteRenovacao = false;
		habilidaComprovanteDevolucao = false;
		
		if (obj.getDataDevolucao() != null)
			addMensagemErro("Este Empréstimo já foi devolvido.");
		else{
			
			UsuarioBibliotecaDao dao = null;
			
			try {
				
				prepareMovimento(SigaaListaComando.RENOVA_EMPRESTIMO);
				
				dao = getDAO(UsuarioBibliotecaDao.class);
				
				UsuarioBiblioteca usuarioBiblioteca = UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaBiblioteca(biblioteca.getId(), dao);
				
				
				List<Integer> idMateriais = new ArrayList<Integer>();
				idMateriais.add(obj.getMaterial().getId());
				
				// Sempre passa a senha o usuário biblioteca porque biblioteca não precisa confirmar a senha //
				MovimentoRenovaEmprestimo mov = new MovimentoRenovaEmprestimo(idMateriais, usuarioBiblioteca, "");
			
				mov.setCodMovimento(SigaaListaComando.RENOVA_EMPRESTIMO);
		
				RetornoOperacoesCirculacaoDTO retorno =  execute(mov);
				
				for (String mensagem : retorno.mensagemAosUsuarios) {
					addMensagemWarning(mensagem);
				}
				
				emprestimosRenovadosOp = retorno.operacoesRealizadas;
				
				for (OperacaoBibliotecaDto renovacoes : emprestimosRenovadosOp) {
					addMensagemInformation(renovacoes.infoMaterial+"<br/> Prazo para Devolução: "+renovacoes.getPrazoFormatado());
				}
				
				habilidaComprovanteRenovacao = true;
				
				addMensagemInformation("Empréstimo renovado com sucesso.");
				
			    atualizaEmpretimosIntitucionais();
			    verificaSituacaoUsuario(usuarioBiblioteca);
					
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return null;
			}finally{
				if(dao != null) dao.close();
			}
		}
	
		return forward(PAGINA_LISTA);
	}
	
	
	/**
	 *  Exibe o código de autenticação na página do comprovante.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>>/biblioteca/EmprestimoInstitucional/comprovanteRenovacao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String getCodigoAutenticacaoRenovacao() throws DAOException{
		return CirculacaoUtil.getCodigoAutenticacaoRenovacao(emprestimosRenovadosOp);
	}
	
	/**
	 * Devolve o empréstimo selecionado.
	 * 
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmprestimoInstitucional/lista.jsp</li>
	 *   </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String devolver() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO
				, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		// Pega a id do empréstimo, passada por parâmetro, e popula o obj.
		obj = new Emprestimo();
		populateObj(true);
		
		habilidaComprovanteRenovacao = false;
		habilidaComprovanteDevolucao = false;
		
		if (obj.getDataDevolucao() != null)
			addMensagemErro("Este Empréstimo já foi devolvido.");
		else
			try {
				
				prepareMovimento(SigaaListaComando.DEVOLVE_EMPRESTIMO);
				
				MovimentoDevolveEmprestimo mov = new MovimentoDevolveEmprestimo(obj.getMaterial().getId(), getUsuarioLogado(), null);
			
				mov.setCodMovimento(SigaaListaComando.DEVOLVE_EMPRESTIMO);
		
				RetornoOperacoesCirculacaoDTO retorno = execute(mov);
				
				// Mostra mensagem cadastradas para serem mostradas no momento da devolução por meio de notas de circulação.  //  
				for (String mensagem : retorno.mensagemAosUsuarios) {
					addMensagemWarning(mensagem);
				}
				
				// Mostra detalhes do material devolvido //  
				if(retorno.getOperacoesRealizadas() != null){
					for (OperacaoBibliotecaDto devolucao : retorno.getOperacoesRealizadas()) {
						addMensagemInformation("<strong> Prazo: </strong>"+ devolucao.getPrazoFormatado()
								+ "<br/>"+" <strong> Data da Devolução: </strong>"+ devolucao.getDataRealizacaoFormatada()
								+ "<br/>"+devolucao.getInfoMaterial()+ " ");
					}
				}
				
				// Mostra mensagem de multa e suspensão, se o usuário devolveu com atrazo //  
				for (String mensagem : retorno.mensagensComprovanteDevolucao) {
					addMensagemInformation(mensagem);
				}
				
				addMensagemInformation("Material devolvido com sucesso!");
			
					
				habilidaComprovanteDevolucao = true;
				
			    emprestimoDevolvido  = retorno.emprestimoRetornado;
			    mensagensComprovanteDevolucao = retorno.mensagensComprovanteDevolucao; 
			    
			    atualizaEmpretimosIntitucionais();
			    verificaSituacaoUsuario(usuarioBiblioteca);
					
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
			}
	
		return forward(PAGINA_LISTA);
	}
	
	
	
	/**
	 * 
	 * Redireciona para a página do comprovante de renovação.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmprestimosInstitucional/lista.jsp</li>
	 *   </ul>
	 *
	 *  
	 *
	 * @return
	 */
	public String geraComprovanteRenovacao(){
		getCurrentRequest().setAttribute("liberaEmissaoComprovante", true); // para impedir que o usuário tente acessar diretamente a página
		// vários MBean podem emitir o comprovante de renovação, o nome na página está padronizado
		getCurrentRequest().setAttribute("_mBeanRealizouRenovacao", this); 
		return telaComprovanteRenovacao();
	}
	
	/**
	 * 
	 * Redireciona para a página do comprovante de devolução.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmprestimosInstitucional/lista.jsp</li>
	 *   </ul>
	 *
	 *  
	 *
	 * @return
	 */
	public String geraComprovanteDevolucao(){
		getCurrentRequest().setAttribute("liberaEmissaoComprovante", true); // para impedir que o usuário tente acessar diretamente a página
		// vários MBean podem emitir o comprovante de devolução, o nome na página está padronizado
		getCurrentRequest().setAttribute("_mBeanRealizouDevolucao", this); 
		return telaComprovanteDevolucao();
	}
	
	
	/**
	 *   Método não chamado por nenhuma página jsp.
	 *
	 * @return
	 */
	public String telaComprovanteDevolucao() {
		return forward(ModuloCirculacaoMBean.PAGINA_COMPROVANTE_DEVOLUCAO);
	}
	
	/**
	 * Redireciona para a tela de impressão do comprovante de renovação.
	 * 
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 */
	public String telaComprovanteRenovacao(){
		return forward(ModuloCirculacaoMBean.PAGINA_COMPROVANTE_RENOVACAO);
	}

	
	/**
	 * Estorna o empréstimo selecionado.
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmprestimoInstitucional/lista.jsp</li>
	 *   </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String estornar() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		// Pega a id do empréstimo, passada por parâmetro, e popula o obj.
		obj = new Emprestimo();
		populateObj(true);
		
		if (!obj.isAtivo())
			addMensagemErro("Este empréstimo já foi estornado.");
		else
			if (!hasErrors()){
				try {
			
					prepareMovimento (SigaaListaComando.ESTORNA_EMPRESTIMO);
					
					MovimentoEstornaEmprestimo mov = new MovimentoEstornaEmprestimo(obj.getId());
			
					mov.setCodMovimento(SigaaListaComando.ESTORNA_EMPRESTIMO);
		
					execute(mov);
					addMensagemInformation("Empréstimo estornado com sucesso.");
					
					all = null;
					
				} catch (NegocioException e) {
					addMensagens(e.getListaMensagens());
				}
			}
	
		return forward(PAGINA_LISTA);
	}
	
	/**
	 * Adiciona um material à lista de materiais à emprestar.
	 * 
	 * 
	 */
	private void adicionarMaterial (){
		if (materiais.contains(material))
			addMensagemErro("O material já está na lista de materiais a emprestar.");
		else if (!material.podeSerEmprestado())
			addMensagemErro("Este material não está disponível");
		else {
			materiais.add(material);
			dmMateriais.setWrappedData(materiais);
			material = null;
		}
	}
	
	/**
	 * 
	 * Retorna a quantidade de materiais adicionados
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmprestimoInstitucional/form.jsp</li>
	 *   </ul>
	 *
	 */
	public int getQtdMateriaisEmprestimo(){
		return (materiais != null ?  materiais.size() : 0);
	}
	
	
	/**
	 * Remove um material da lista de materiais à emprestar.
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmprestimoInstitucional/form.jsp</li>
	 *   </ul>
	 * 
	 * @param e
	 */
	public void removerMaterial (ActionEvent e){
		materiais.remove(dmMateriais.getRowData());
		dmMateriais.setWrappedData(materiais);
	}
	
	/**
	 * Consulta por um material, dado seu código de barras.
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmprestimoInstitucional/form.jsp</li>
	 *   </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void buscarMaterialByCodigoBarras (ActionEvent e) throws DAOException {		
		MaterialInformacionalDao dao = null;
		
		try {
			dao = getDAO(MaterialInformacionalDao.class);
			material = dao.findMaterialAtivoByCodigoBarras(codigoBarras);
			
			if (material != null){
				
				material.setInformacao(  BibliotecaUtil.obtemDadosMaterialInformacional(  material.getId() ) );
				
				codigoBarras = "";
				adicionarMaterial();
	
			} else
				addMensagemErro("Material não encontrado.");		
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	

	
	
	
	/**
	 * Método chamdo por ajax para busca e mostrar para o usuário as informações do usuário biblioteca da biblioteca que vai realizar os empréstimos.
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmprestimoInstitucional/form.jsp</li>
	 *   </ul>
	 * @throws  
	 * @throws DAOException 
	 *   
	 * @throws SegurancaException 
	 */
	public void atualizaBibliotecaEmprestimo(ActionEvent evnt) throws DAOException{
		
		if( materiais != null)  materiais.clear();
		
		if(this.biblioteca == null || this.biblioteca.getId() <= 0){
			zeraDadosBiblioteca();
			return;
		}else{
		
			try {
				usuarioBiblioteca = UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaBiblioteca(this.biblioteca.getId(), null);
				infoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(usuarioBiblioteca, null, null);
				verificaSituacaoUsuario(usuarioBiblioteca);
				
			} catch (NegocioException ne) {
				addMensagensAjax(ne.getListaMensagens());
				 zeraDadosBiblioteca();
			}
		}
	}
	
	/**
	 * 
	 * Apaga os resulatados da consulta.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmprestimoInstitucional/lista.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public String apagarEmprestimosInstitucionais(){
		habilidaComprovanteRenovacao = false;
		habilidaComprovanteDevolucao = false;
		all = null;
		zeraDadosBiblioteca();
		return telaListaEmprestimosInstitucionais();
	}
	
	
	
	/**
	 * 
	 * Realiza a busca dos empréstimos institucionais de acordo com os parâmetro digitados pelo usuário na tela de busca.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmprestimoInstitucional/lista.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public String buscarEmprestimosInstitucionais() throws DAOException{
		
		habilidaComprovanteRenovacao = false;
		habilidaComprovanteDevolucao = false;
		
		MaterialInformacionalDao daoMaterial = null;
		UsuarioBibliotecaDao daoUsuario = null;
		
		try {
			
			daoUsuario = getDAO(UsuarioBibliotecaDao.class);
			
			if ((biblioteca == null || biblioteca.getId() <= 0)){
				addMensagemErro("Selecione uma biblioteca.");
				return telaListaEmprestimosInstitucionais();
			}
			
			all = null; // apaga a lista anterior
				
			usuarioBiblioteca = UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaBiblioteca(biblioteca.getId(), daoUsuario);
			
			if(usuarioBiblioteca != null){
				
				// Se é a primeira vez ou mudou de biblioteca, busca as informações para mostrar na tela
				if(infoUsuario == null || infoUsuario.getIdUsuarioBiblioteca() != usuarioBiblioteca.getId())
					infoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(usuarioBiblioteca, null, null);
				verificaSituacaoUsuario(usuarioBiblioteca);
			}
			
			
			atualizaEmpretimosIntitucionais();
			
			
			
			
			return telaListaEmprestimosInstitucionais();
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			zeraDadosBiblioteca();
			return null;
		} finally {
			if (daoMaterial != null) daoMaterial.close();
			if (daoUsuario != null) daoUsuario.close();
		}
	}
	
	
	/**
	 * Atualiza os empréstimos institucionais do usuário
	 *
	 * @throws DAOException
	 */
	private void atualizaEmpretimosIntitucionais() throws DAOException{
		EmprestimoDao dao = null;
		try {
			dao = getDAO(EmprestimoDao.class);
			Boolean auxSituacao = situacao == SITUACAO_AMBAS  ? null : situacao == SITUACAO_ATIVA ? false : true;
			all = dao.findEmprestimosParaBiblioteca(usuarioBiblioteca, auxSituacao, dataInicio, dataFim, ! emprestimoParaBibliotecaExterna);
			
			for (Emprestimo emprestimo : all) {
				emprestimo.getMaterial().setInformacao(BibliotecaUtil.obtemDadosMaterialInformacional(emprestimo.getMaterial().getId()));
			}
			
			if(all.size() == 0){
				addMensagemWarning("Não Foram Encontrados Empréstimos Institucionais com as Características Buscadas");
			}
			
			if( new Integer(EmprestimoDao.LIMITE_BUSCA_EMPRESTIMOS_INSTITUCIONAIS).compareTo( all.size() ) <= 0){
				addMensagemWarning("A busca resultou em um número muito grande de resultados, somente os "+EmprestimoDao.LIMITE_BUSCA_EMPRESTIMOS_INSTITUCIONAIS+" primeiros empréstimos estão sendo mostrados.");
			}
			
		} finally {
			if (dao != null) dao.close();
		}
	}
	
	
	/**
	 *  Redireciona para a página de listagem dos empréstimos institucionais
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmprestimosInstitucional/form.jsp</li>
	 *   </ul>
	 *
	 *
	 */
	public String telaListaEmprestimosInstitucionais() {
		return forward(PAGINA_LISTA);
	}

	
	/**
	 * Retorna a listagem de todos os Empréstimos ativos, de acordo com o filtro
	 * selecionado, para Bibliotecas Externas, ordenadas pelo nome chamado em
	 * /biblioteca/EmprestimoInstitucional/lista.jsp
	 */
	@Override
	public Collection <Emprestimo> getAll() throws DAOException {
		return all;
	}

	
	
//	/**
//	 *  <p>Método chamado quando um usuário escolhe o tipo de empréstimo.</p>
//	 *  <p>
//	 *  	<ul>
//	 *    		<li>Ser for institucional: mostra o prazo de acordo com a política de empréstios </li>
//	 *    		<li>Ser for personalizável: mostra um campo para o usuário digitar o prazo para o emprestimos </li>
//	 *   	</ul>
//	 *  </p>
//	 *  
//	 *  <br/>
//	 *  Método chamado pela(s) seguinte(s) JSP(s):
//	 *   <ul>
//	 *    <li>/sigaa.war/biblioteca/EmprestimoInstitucional/form.jsp</li>
//	 *   </ul>
//	 *
//	 * @param evt
//	 */
//	public void verificaTipoEmprestimo(ValueChangeEvent evt){
//		
//		Integer idTipoEmprestimo = (Integer) evt.getNewValue();
//		
//		tipoEmprestimoEscolhido.setId(idTipoEmprestimo );
//		
//		for (TipoEmprestimo tipoEmp : tiposEmprestimos) {
//			
//			if( tipoEmprestimoEscolhido.getId() ==  tipoEmp.getId()  ){
//				
//				if(tipoEmp.isTipoEmprestimoPersonalizavel()){
//					tipoEmprestimoPersonalizado = true;
//					return;
//				}
//			}
//			
//		}
//		
//		tipoEmprestimoPersonalizado = false;
//		
//	}
	
	
	/**
	 * Retorna o id do tipo de empréstimo personalizado no banco.  Para verificar na página se deve ativar o campo para o usuário informar 
	 * a quantidade de dias que o material vai ficar emprestado.
	 *
	 * @return
	 * @throws DAOException 
	 */
	public int getIdTipoEmprestimoPersonalizado() {
		
		if(tiposEmprestimos != null){
		
			for (TipoEmprestimo tipo : tiposEmprestimos) {
				
				if(tipo.isTipoEmprestimoPersonalizavel()){
					
					return tipo.getId();
				}
			}
		}
		
		return -1;
	}
	
	
	/**
	 * 
	 * Retorna para um combobox os tipos de empréstimos que uma biblioteca pode fazer
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmprestimoInstitucional/form.jsp</li>
	 *   </ul>
	 *
	 *
	 * @return
	 * @throws DAOException
	 */
	public List <SelectItem> getTiposEmprestimosComboBox() throws DAOException {
		
		return toSelectItems(tiposEmprestimos, "id", "descricao");
	}
	

	/**
	 * Retorna a descrição do tipo de biblioteca para o qual está sendo realizado o emprétimo institucional (interna ou externa.)
	 *
	 * @return
	 */
	public String getTipoBiblioteca (){
		if (emprestimoParaBibliotecaExterna)
			return "Biblioteca / Unidade Externa";
		
		return "Biblioteca / Unidade Interna";
	}
	
	
	/**
	 * Retorna a quantidade de prazos cadastrados para exibir na
	 * listagem Chamado na página /biblioteca/EmprestimoInstitucional/lista.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public int getSize() throws ArqException {
		
		if(getAll() != null)
			return getAll().size();
		else
			return 0;
	}

	public Collection<SelectItem> getBibliotecasExternas() {
		return bibliotecasExternas;
	}

	public void setBibliotecasExternas(
			Collection<SelectItem> bibliotecasExternas) {
		this.bibliotecasExternas = bibliotecasExternas;
	}

	public Collection<SelectItem> getBibliotecasInternas() {
		return bibliotecasInternas;
	}

	public void setBibliotecasInternas(
			Collection<SelectItem> bibliotecasInternas) {
		this.bibliotecasInternas = bibliotecasInternas;
	}

	public List<MaterialInformacional> getMateriais() {
		return materiais;
	}

	public void setMateriais(List<MaterialInformacional> materiais) {
		this.materiais = materiais;
	}

	public boolean isEmprestimoParaBibliotecaExterna() {
		return emprestimoParaBibliotecaExterna;
	}

	public void setEmprestimoParaBibliotecaExterna(
			boolean emprestimoParaBibliotecaExterna) {
		this.emprestimoParaBibliotecaExterna = emprestimoParaBibliotecaExterna;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	public DataModel getDmMateriais() {
		return dmMateriais;
	}

	public void setDmMateriais(DataModel dmMateriais) {
		this.dmMateriais = dmMateriais;
	}

	public MaterialInformacional getMaterial() {
		return material;
	}

	public void setMaterial(MaterialInformacional material) {
		this.material = material;
	}


	public String getCodigoBarras() {
		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}
	
	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public int getSituacao() {
		return situacao;
	}

	public void setSituacao(int situacao){
		this.situacao = situacao;
	}

	public int getSituacaoAtiva() {
		return SITUACAO_ATIVA;
	}

	public int getSitucaoDevolvido() {
		return SITUACAO_DEVOLVIDO;
	}

	public int getSituacaoAmbas() {
		return SITUACAO_AMBAS;
	}

	public TipoEmprestimo getTipoEmprestimoEscolhido() {
		return tipoEmprestimoEscolhido;
	}

	public void setTipoEmprestimoEscolhido(TipoEmprestimo tipoEmprestimoEscolhido) {
		this.tipoEmprestimoEscolhido = tipoEmprestimoEscolhido;
	}

	public boolean isTipoEmprestimoPersonalizado() {
		return tipoEmprestimoPersonalizado;
	}

	public void setTipoEmprestimoPersonalizado(boolean tipoEmprestimoPersonalizado) {
		this.tipoEmprestimoPersonalizado = tipoEmprestimoPersonalizado;
	}

	public Integer getDiasAEmprestar() {
		return diasAEmprestar;
	}

	/**
	 * 
	 * Configura as quantidade de dis a emprestar.  Utilizando nos empréstimos personalizados onde o operador escolher a quantidade de dias.
	 *  
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/EmprestimoInstitucional/form.jsp</li>
	 *   </ul>
	 *   
	 * @param diasAEmprestar
	 */
	public void setDiasAEmprestar(Integer diasAEmprestar) {
		if(diasAEmprestar == null)
			this.diasAEmprestar = 0;
		else
			this.diasAEmprestar = diasAEmprestar;
	}


	public boolean isHabilidaComprovanteRenovacao() {
		return habilidaComprovanteRenovacao;
	}


	public void setHabilidaComprovanteRenovacao(boolean habilidaComprovanteRenovacao) {
		this.habilidaComprovanteRenovacao = habilidaComprovanteRenovacao;
	}


	public boolean isHabilidaComprovanteDevolucao() {
		return habilidaComprovanteDevolucao;
	}


	public void setHabilidaComprovanteDevolucao(boolean habilidaComprovanteDevolucao) {
		this.habilidaComprovanteDevolucao = habilidaComprovanteDevolucao;
	}


	public EmprestimoDto getEmprestimoDevolvido() {
		return emprestimoDevolvido;
	}

	public void setEmprestimoDevolvido(EmprestimoDto emprestimoDevolvido) {
		this.emprestimoDevolvido = emprestimoDevolvido;
	}

	public List<String> getMensagensComprovanteDevolucao() {
		return mensagensComprovanteDevolucao;
	}

	public void setMensagensComprovanteDevolucao(List<String> mensagensComprovanteDevolucao) {
		this.mensagensComprovanteDevolucao = mensagensComprovanteDevolucao;
	}

	public InformacoesUsuarioBiblioteca getInfoUsuario() {
		return infoUsuario;
	}

	public void setInfoUsuario(InformacoesUsuarioBiblioteca infoUsuario) {
		this.infoUsuario = infoUsuario;
	}

	public List<SituacaoUsuarioBiblioteca> getSituacoesUsuario() {
		return situacoesUsuario;
	}

	public void setSituacoesUsuario(List<SituacaoUsuarioBiblioteca> situacoesUsuario) {
		this.situacoesUsuario = situacoesUsuario;
	}


	public List<OperacaoBibliotecaDto> getEmprestimosRenovadosOp() {
		return emprestimosRenovadosOp;
	}


	public void setEmprestimosRenovadosOp(List<OperacaoBibliotecaDto> emprestimosRenovadosOp) {
		this.emprestimosRenovadosOp = emprestimosRenovadosOp;
	}
	
	
	
	
}