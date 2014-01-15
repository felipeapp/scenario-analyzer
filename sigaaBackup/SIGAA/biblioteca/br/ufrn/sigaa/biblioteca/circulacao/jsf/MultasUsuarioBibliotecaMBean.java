/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 15/02/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MultaUsuariosBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.MultaUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.MultaUsuarioBiblioteca.StatusMulta;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PoliticaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoCriaGRUMultaBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoEstornarMultaUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoQuitaMultaUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MultaStrategyDefault;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.PunicaoAtrasoEmprestimoStrategyFactory;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.jsf.BuscaUsuarioBibliotecaMBean;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;
import br.ufrn.sigaa.biblioteca.util.MultaUsuarioBibliotecaUtil;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * <p>MBean responsável por realizar as operações sobre multa de um usuário.  
 *   Por exemplo, receber pagamento, estornas multa ou criar uma nova multa para o usuário </p>
 *
 * 
 * @author jadson
 * @version 2.0 - jadson - Alterando para que os bibliotecário possam estornar multas 
 * de qualquer conta da biblioteca, mesmo as já quitadas. Apesar que, ao contrário das suspensões, essa situação nunca deve ocorrer. 
 * Porque usuários devendo dinheiro não podem ser quitados.
 */
@Component("multasUsuarioBibliotecaMBean")
@Scope ("request")
public class MultasUsuarioBibliotecaMBean  extends SigaaAbstractController<MultaUsuarioBiblioteca>  implements PesquisarUsuarioBiblioteca{

	
	/** Página que lista as multas ativas de um usuário. */
	public static final String PAGINA_LISTA_MULTAS_USUARIO = "/biblioteca/circulacao/multas/listaMultasUsuario.jsp";
	
	/** Página para criar uma nova multa para o usuário. */
	public static final String PAGINA_CRIAR_MULTA_MANUAL_USUARIO = "/biblioteca/circulacao/multas/criaMultaManualUsuario.jsp";
	
	/** Página para confirmar o pagamento de uma multa de um usuário. */
	public static final String PAGINA_CONFIRMAR_PAGAMENTO = "/biblioteca/circulacao/multas/confirmarPagamentoMulta.jsp";
	
	/** Página para confirmar o pagamento de uma multa de um usuário. */
	public static final String PAGINA_CONFIRMA_ESTORNA_MULTA = "/biblioteca/circulacao/multas/confirmaEstornoMulta.jsp";
	
	/** Página na qual o operador pode emitir uma única GRU para várias multas do usuário. */
	public static final String PAGINA_EMITE_GRU_VARIAS_MULTAS_USUARIO = "/biblioteca/circulacao/multas/emiteGRUUnicaVariasMultasUsuario.jsp";
	
	
	
	/** Guarda a informações sobre a pessoa selecionada da busca padrão.
	 * Isso porque esse caso de uso deve poder retirar suspensões, mesmo que o vínculo do usuário esteja quitado. 
	 */
	private Pessoa pessoaSelecioandaBuscaPadrao;
	
	/** Guarda a informações sobre a biblioteca selecionada da busca padrão.
	 * Isso porque esse caso de uso deve poder retirar suspensões, mesmo que o vínculo do usuário esteja quitado. 
	 */
	private Biblioteca bibliotecaSelecioandaBuscaPadrao;
	
	/**
	 * Informações sobre todas as contas que a pessoa já tive na biblioteca
	 */
	private List<UsuarioBiblioteca> contasUsuarioBiblioteca = new ArrayList<UsuarioBiblioteca>(); 
	
	
	/** Armazena as informações de conta do usuário ATUAL na biblioteca (Não quitada)
	 *
	 * É para esse conta que são cadastrada as novas suspensões, por esse caso de uso.
	 */
	private UsuarioBiblioteca usuarioBiblioteca = new UsuarioBiblioteca();
	
	
	/** Armazena as informações do usuário da Biblioteca selecionado na pesquias */
	private InformacoesUsuarioBiblioteca informacaoUsuario;
	
	/** Guarda a lista de multas ativas do usuário.  */
	private List<MultaUsuarioBiblioteca> multasAtivasUsuario;
	
	/** Guarda o motivo do estorno das multas selecionadas.  */
	private String motivoEstorno;
	
	/**
	 * O valor total das multas do usuário
	 */
	private BigDecimal valorTotalMultas;
	
	/**
	 * <p>O numero de referência da GRU selecionada para emitir o pagamento.</p>
	 * 
	 * <p>Esse número serve para o usuário conferir qual o pagamento ele está dando baixa.</p>
	 */
	private String numeroReferencia;
	
	
	/** Guarda a GRU na emissão de várias multa numa única GRU. */
	private ByteArrayOutputStream outputSteam;
	
	
	////////////// Dados para o calculo manual do valor da multa realizada pelo sistema  /////////
	
	/** Contém o prazo da devolução para o calculo da data de suspensão manual pelo sistema. */
	private Date prazoDevolucaoCalculoManual;
	
	/** Contém a data de devolução para o calculo da data de suspensão manual pelo sistema. */
	private Date dataDevolucaoCalculoManual;
	
	/** Contém a quantidade de empréstimos feitos para o calculo da data de suspensão manual pelo sistema. */
	private Integer qtdEmprestimosCalculoManual = 1;
	
	/** Contém o tipo de prazo dos empréstimos para o calculo da data de suspensão manual pelo sistema. */
	private Short tipoPrazoCalculoManual = PoliticaEmprestimo.TIPO_PRAZO_EM_DIAS;  
	
	/** A quantidade de dias que o usuário atrazou calculado pelo sistema. */
	private Integer qtdDiasEmAtrasoCalculoManual;
	
	/** A data até quando o usuário vai ficar suspenso calculado pelo sistema. */
	private BigDecimal valorMultaCalculoManual;
	
	
	
	/**
	 * <p>Inicia caso de uso de ver as multas dos usuários da biblitoeca</p>
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/circulacao.jsp</li>
	 *   </ul> 
	 */
	public String iniciarGerenciarMultas () throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		BuscaUsuarioBibliotecaMBean pBean = getMBean ("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, true, true, false, true, "Gerenciar Multas dos Usuários", null);
	}
	
	
	/**
	 * <p>Inicia caso de uso de ver as multas dos usuários da biblitoeca</p>
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/informacao_referencia.jsp</li>
	 *   </ul> 
	 */
	public String iniciarGerenciarMultasBibliotecas () throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		BuscaUsuarioBibliotecaMBean pBean = getMBean ("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, false, false, true, true, "Gerenciar Multas dos Usuários", null);
	}
	
	
	/**
	 * <p> Exibe a lista de multas ativas do usuário. </p>
	 * 
	 * <p>Usado em /sigaa.war/biblioteca/buscaUsuarioBiblioteca.jsp </p>
	 */
	public String listarMultasDoUsuarioSelecionado() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.CRIA_GRU_MULTA_BIBLIOTECA);
		
		UsuarioBibliotecaDao dao = null;
		MultaUsuariosBibliotecaDao multaDao = null;
		
		try {
			
			dao = getDAO(UsuarioBibliotecaDao.class);
			multaDao = getDAO(MultaUsuariosBibliotecaDao.class);
		
			if(pessoaSelecioandaBuscaPadrao != null){
				contasUsuarioBiblioteca = dao.findUsuarioBibliotecaAtivoByPessoa(pessoaSelecioandaBuscaPadrao.getId() );
				informacaoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(null, pessoaSelecioandaBuscaPadrao.getId(), null);
			}
			
			if(bibliotecaSelecioandaBuscaPadrao != null){
				contasUsuarioBiblioteca = dao.findUsuarioBibliotecaAtivoByBiblioteca(bibliotecaSelecioandaBuscaPadrao.getId() );
				informacaoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(null, null, bibliotecaSelecioandaBuscaPadrao.getId() );
			}
			
			
			atualizaMultasAtivasUsuario(multaDao, usuarioBiblioteca.getId());
			
			return telaListaMultasUsuario();
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} finally {
			if (dao != null)  dao.close();
			if (multaDao != null)  multaDao.close();
		}
	}


	/**
	 * Método que busca as multas ativas do usuário e calcula o valor total delas
	 *
	 * @param multaDao
	 * @throws DAOException
	 */
	private void atualizaMultasAtivasUsuario(MultaUsuariosBibliotecaDao multaDao, int idUsuarioBiblioteca)throws DAOException {
		
		valorTotalMultas = new BigDecimal(0);
		
		// Seta as multas existente em qualquer conta do usuário, mesma as já quitadas //
	
		multasAtivasUsuario = multaDao.findMultasAtivasDoUsuario(pessoaSelecioandaBuscaPadrao != null ? pessoaSelecioandaBuscaPadrao.getId() : null
					, bibliotecaSelecioandaBuscaPadrao != null ? bibliotecaSelecioandaBuscaPadrao.getId() : null) ;
		
		
		
		if (!isEmpty(multasAtivasUsuario)) { 
			// verifica o pagamento das GRUs
			Collection<Integer> idsGruQuitadas = new ArrayList<Integer>();
			
			for (MultaUsuarioBiblioteca multa : multasAtivasUsuario){
				if (multa.getIdGRUQuitacao() != null)
					idsGruQuitadas.add(multa.getIdGRUQuitacao());
			}
			
			idsGruQuitadas = GuiaRecolhimentoUniaoHelper.isGRUQuitada(idsGruQuitadas);
			
			if (!isEmpty(idsGruQuitadas)){
				for (MultaUsuarioBiblioteca multa : multasAtivasUsuario){
					multa.setGruQuitada(idsGruQuitadas.contains(multa.getIdGRUQuitacao()));
				}
			}
		}
		
		for (MultaUsuarioBiblioteca multa : multasAtivasUsuario) {
			valorTotalMultas = valorTotalMultas.add(multa.getValor());
		}
	}

	
	/**
	 * <p>Redireciona o usuário para a formulário onde ele vai criar uma multa manual para o usuáiro da biblioteca selecionado</p>
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/circulacao/listaMultasUsuario.jsp</li>
	 *   </ul> 
	 * @throws ArqException 
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	public String preCadastrar() throws ArqException{
		
		prepareMovimento(ArqListaComando.CADASTRAR);
		
		apagaDadosCalculoManual();
		
		boolean possuiContaNaoQuitada = false;
		
		for (UsuarioBiblioteca contaBiblioteca : contasUsuarioBiblioteca) {
			if(! contaBiblioteca.isQuitado()){
				usuarioBiblioteca = contaBiblioteca;
				possuiContaNaoQuitada = true;
			}
		}
		
		if(! possuiContaNaoQuitada ){
			addMensagemErro("Não é possível cadastrar uma nova multa para o usuário, porque todas as suas contas na biblioteca estão quitadas.");
			return null;
		}
		
		
		obj = new MultaUsuarioBiblioteca();
		obj.setBibliotecaRecolhimento(new Biblioteca(-1)); // Multas manuais o usuário devem informar a biblioteca do recolhimento, caso nãos seja informada, irá para a unidade da biblioteca central.
		return telaCadastraNovaMulta();
	}

	
	
	/**
	 * <p>Cria a multa manual</p>
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/circulacao/criaMultaManualUsuario.jsp</li>
	 *   </ul> 
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	public String cadastrar() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		// Set as informações de uma multa manual, o motivo e o valor são digitados pelo usuário //
		obj.setManual(true);
		obj.setUsuarioBiblioteca(usuarioBiblioteca);
		obj.setDataCadastro(new Date());
		obj.setUsuarioCadastro( getUsuarioLogado());
		obj.setStatus(StatusMulta.EM_ABERTO);
		
		MovimentoCadastro mov = new MovimentoCadastro (obj);
		mov.setCodMovimento(ArqListaComando.CADASTRAR);
		
		ListaMensagens lista =  obj.validate();   // Valida os dados digitados pelo usuário //
		
		if(lista.isErrorPresent()){
			addMensagens(lista);
			return null;
		}
		
		MultaUsuariosBibliotecaDao multaDao = null;
		
		try {
		
			multaDao = getDAO(MultaUsuariosBibliotecaDao.class);
		
			execute(mov);
			
			addMensagemInformation("Multa cadastrada com sucesso.");
			
			atualizaMultasAtivasUsuario(multaDao, usuarioBiblioteca.getId());
			
			return telaListaMultasUsuario();
			
		} catch (NegocioException e){
			addMensagens(e.getListaMensagens());
			return null;
		}finally {
			if (multaDao != null)  multaDao.close();
		}
		
	}
	
	
	
	
	/**
	 * <p> Redireciona para a página onde o usuário vai confirmar o pagamento da multa do usuário no sistema, mediante apresentação do comprovante </p>
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/circulacao/listaMultasUsuario.jsp</li>
	 *   </ul> 
	 */
	public String preConfirmarPagamento () throws ArqException{
		
		prepareMovimento(SigaaListaComando.QUITA_MULTA_USUARIO_BIBLIOTECA);
		
		int idMultaSelecionada = getParameterInt("idMultaSelecionada");
		
		
		for (MultaUsuarioBiblioteca multa : multasAtivasUsuario) {
			
			if(multa.getId() == idMultaSelecionada){
				obj = multa;
			}
		}
		
		if(obj == null){
			addMensagemErro("Erro ao selecionar a multa para ser paga. Por favor, reinicie o processo! "); // Mesagem que não era para ser mostrada
			return null;
		}
		
		GenericDAO  daoComum = null;

		try{
			daoComum = DAOFactory.getGeneric(Sistema.COMUM);
		
			if(obj.isgruJaFoiGerada()){
				GuiaRecolhimentoUniao gru =  daoComum.findByPrimaryKey(obj.getIdGRUQuitacao(), GuiaRecolhimentoUniao.class, "numeroReferenciaNossoNumero");
				
				if(gru != null)
					numeroReferencia = String.valueOf(gru.getNumeroReferenciaNossoNumero());
				else{
					mostraMensagemGRUNaoGerada();
				}
			}else{
				mostraMensagemGRUNaoGerada();
			}
			
			// Usado no envio do email para o usuário informando a quitação //
			obj.setInfoIdentificacaoMultaGerada( MultaUsuarioBibliotecaUtil.montaInformacoesMultaComprovante(obj, true));
		
		}finally{
			if(daoComum != null ) daoComum.close();
		}
		
		return telaConfirmaPagamento();
	}
	
	
	/**
	 * Mostra mensagem GRU não gerada.
	 *
	 * @void
	 */
	private void mostraMensagemGRUNaoGerada(){
		numeroReferencia = null;
		numeroReferencia = " GRU PARA PAGAMENTO NÃO GERADA ";
		addMensagemWarning("A GRU para o pagamento dessa multa ainda não foi gerada, provavelmente ela não foi paga!");
	}
	
	
	/**
	 * <p> Confirma o pagamento da multa no sistema manualmente (mediante apresentação do comprovante).</p>
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/circulacao/listaMultasUsuario.jsp</li>
	 *   </ul> 
	 */
	public String confirmarPagamento () throws ArqException{
		
		MovimentoQuitaMultaUsuarioBiblioteca mov = new MovimentoQuitaMultaUsuarioBiblioteca(obj, usuarioBiblioteca, numeroReferencia);
		mov.setCodMovimento(SigaaListaComando.QUITA_MULTA_USUARIO_BIBLIOTECA);
		
		MultaUsuariosBibliotecaDao multaDao = null;
		
		try {
			execute(mov);
			
			multaDao = getDAO(MultaUsuariosBibliotecaDao.class);
			
			atualizaMultasAtivasUsuario(multaDao, usuarioBiblioteca.getId());
			
			addMensagemInformation("Pagamento da multa confirmado com sucesso! ");
			
			return telaListaMultasUsuario();  // Retorna para página que lista as demais multas do usuário, caso ele queira pagar outra
			
		} catch (NegocioException e){
			addMensagens(e.getListaMensagens());
			return null;
		}finally{
			if(multaDao != null ) multaDao.close();
		}
		
	}
	
	
	/**
	 *  Emite a GRU para o usuário pagar a multa, se o bibliotecário quiser, o próprio usuário pode emitir
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/listaMultasUsuario.jsp</li>
	 *   </ul>
	 *
	 * @throws ArqException 
	 * @throws NegocioException 
	 *
	 */
	public String emitirGRU() throws ArqException{
		
		int idMultaSelecionada = getParameterInt("idMultaSelecionada");
		
		
		for (MultaUsuarioBiblioteca multa : multasAtivasUsuario) {
			
			if(multa.getId() == idMultaSelecionada){
				obj = multa;
			}
		}
		
		if(obj == null){
			addMensagemErro("Erro ao selecionar a multa para ser paga. Por favor, reinicie o processo! "); // Mesagem que não era para ser mostrada
			return null;
		}
			
		// Não precisa, não é mais impresso na GRU
		//obj.setInfoIdentificacaoMultaGerada( MultaUsuarioBibliotecaUtil.montaInformacoesMultaComprovante(obj, false));
		
	
		
		if(obj.getIdGRUQuitacao() == null){  // A GRU não foi gerada ainda
			
			try {
			
				MovimentoCriaGRUMultaBiblioteca movGRU = new MovimentoCriaGRUMultaBiblioteca(obj);
				movGRU.setCodMovimento(SigaaListaComando.CRIA_GRU_MULTA_BIBLIOTECA);
				obj = (MultaUsuarioBiblioteca) execute(movGRU);    // A multa com o id da GRU gerada
			
			} catch (NegocioException ne) {
				addMensagens(ne.getListaMensagens());
				return null;
			} finally{
				prepareMovimento(SigaaListaComando.CRIA_GRU_MULTA_BIBLIOTECA);  // prepara o próximo, caso o usuário tente reimprimir
			}  
		}
			
		try{
			
			outputSteam = new ByteArrayOutputStream();
			
			GuiaRecolhimentoUniaoHelper.gerarPDF(outputSteam, obj.getIdGRUQuitacao());
			
			
			outputSteam.flush();
			outputSteam.close();
			
		} catch (IOException e) {
			addMensagemErro("Erro ao tentar gerar a GRU, por favor tente novamente, caso o problema persista, contacte o suporte.");
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
		}
		
		return forward(PAGINA_LISTA_MULTAS_USUARIO);
		
	}
	
	
	/**
	 *  <p>Redireciona para a página na qual o bibliotecário vai escolher várias multas e emitir 1 única  GRU para pagar todas eles e economizar papel, agilizando o processo.</p>
	 *  
	 *  <p>O operador vai escolher a unidade de recebimento da GRU e selecionar mas multas.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/multas/listaMultasUsuario.jsp</li>
	 *   </ul>
	 * @throws DAOException 
	 *
	 * @throws ArqException 
	 * @throws NegocioException 
	 *
	 */
	public String preEmitirGRUUnicaParaMultasAbertas() throws DAOException{
		
		Biblioteca bibliotecaCentral = BibliotecaUtil.getBibliotecaCentral(new String[]{"id", "descricao", "unidade.id"});
		
		// Caso alguma multa não tenha unidade de recolhimento, coloca a unidade de recolhimento da biblioteca central  //
		// que é obrigatória no sistema                                                                                 //
		for (MultaUsuarioBiblioteca multa : multasAtivasUsuario){
			
			if(multa.getIdGRUQuitacao() == null)
				multa.setSelecionado(true);  // Por padrão seleciona todas cuja GRU não foi emitada ainda !
			else
				multa.setSelecionado(false);
			
			if ( multa.isManual() ){
				if( multa.getBibliotecaRecolhimento() == null ){
					multa.setBibliotecaRecolhimento(bibliotecaCentral);
				}
			}else{
				if( multa.getEmprestimo().getMaterial().getBiblioteca() == null ){
					multa.getEmprestimo().getMaterial().setBiblioteca(bibliotecaCentral);
				}
			}
		}
		
		return forward(PAGINA_EMITE_GRU_VARIAS_MULTAS_USUARIO);
	}
	
	
	
	
	/**
	 *  <p>Realiza a ação de emitir uma única GRU para as multas selecionados no passo anterior.</p>
	 *  
	 *  <p>O operador tem que selecionar as multas porque caso o sistema trabalhe com várias unidade de recolhimento o operador tem que escolher a unidade de recolhimento da GRU.</p>
	 * @throws ArqException 
	 *
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/multas/emiteGRUUnicaVariasMultasUsuario.jsp</li>
	 *   </ul>
	 *
	 * @String
	 */
	public String emitirGRUUnicaParaMultasAbertas() throws ArqException{
		
		List<MultaUsuarioBiblioteca> multasASeremPagas =  new ArrayList<MultaUsuarioBiblioteca>();
		
		for (MultaUsuarioBiblioteca multa : multasAtivasUsuario){	
			if(multa.isSelecionado()){
				multasASeremPagas.add(multa);
			}
		}
		
		
		try {
			
			MovimentoCriaGRUMultaBiblioteca movGRU = new MovimentoCriaGRUMultaBiblioteca(multasASeremPagas);
			movGRU.setCodMovimento(SigaaListaComando.CRIA_GRU_MULTA_BIBLIOTECA);
			
			@SuppressWarnings("unchecked")
			List<MultaUsuarioBiblioteca> retorno = (List<MultaUsuarioBiblioteca>) execute(movGRU);
			
			multasASeremPagas = retorno;    // As multas com o id da GRU gerada
		
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} finally{
			prepareMovimento(SigaaListaComando.CRIA_GRU_MULTA_BIBLIOTECA);  // prepara o próximo, caso o usuário tente reimprimir
		}  
	
		
		try{
			
			outputSteam = new ByteArrayOutputStream();
			
			// Pega apenas o id para primeira GRU que a mesma GRU das outras multas.
			GuiaRecolhimentoUniaoHelper.gerarPDF(outputSteam, multasASeremPagas.get(0).getIdGRUQuitacao()); 
			
			outputSteam.flush();
			outputSteam.close();
			
		} catch (IOException e) {
			addMensagemErro("Erro ao tentar gerar a GRU, por favor tente novamente, caso o problema persista, contacte o suporte.");
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
		}finally{
			
		}
	
	
		
		return forward(PAGINA_LISTA_MULTAS_USUARIO);
	}
	
	
	/**
	 * <p>Envia o PDF para a saída do usuário.</p>
	 * 
	 * Utilizado na JSP: /sigaa.war/biblioteca/circulacao/multas/emiteGRUUnicaVariasMultasUsuario.jsp
	 * 
	 * @return
	 * @throws ArqException
	 * @throws IOException 
	 */
	public void visualizarGRUUnicaGerada(ActionEvent evt) {
		if(outputSteam != null){
			
			try {
				
				DataOutputStream dos = new DataOutputStream(getCurrentResponse().getOutputStream());
			
				dos.write(outputSteam.toByteArray());
				getCurrentResponse().setContentType("Content-Type: application/pdf;");
				getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=GRU.pdf");
				FacesContext.getCurrentInstance().responseComplete();
				
				outputSteam = null;
				
			} catch (IOException e) {
				e.printStackTrace();
				addMensagemErro("Erro ao tentar gerar a GRU, por favor tente novamente, caso o problema persista, contacte o suporte.");
			}
		}
		
		
	}
	
	
	/**
	 * <p>Estorna as multas selecionadas. </p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/listaMultasUsuario.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String preEstornar () throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.ESTORNA_MULTA_USUARIO_BIBLIOTECA);
		
		int idMultaSelecionada = getParameterInt("idMultaSelecionada");
		
		
		for (MultaUsuarioBiblioteca multa : multasAtivasUsuario) {
			
			if(multa.getId() == idMultaSelecionada){
				obj = multa;
			}
		}
		
		if(obj == null){
			addMensagemErro("Erro ao selecionar a multa para ser estornada. Por favor, reinicie o processo! "); // Mesagem que não era para ser mostrada
			return null;
		}
			
		
		obj.setInfoIdentificacaoMultaGerada( MultaUsuarioBibliotecaUtil.montaInformacoesMultaComprovante(obj, true));
		
		if(obj.gruJaFoiGerada()){
			addMensagemWarning("A GRU para o pagamento dessa multa já foi gerada, talvez ela já tenha sido paga!");
		}
		
		return telaConfirmaEstorno();
	}
	
	/**
	 * <p>Estorna as multas selecionadas. </p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/confirmaEstornoMulta.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String estornar () throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		if(!confirmaSenha())
			return telaConfirmaEstorno();
		
		MovimentoEstornarMultaUsuarioBiblioteca mov = new MovimentoEstornarMultaUsuarioBiblioteca(obj, usuarioBiblioteca);
		mov.setCodMovimento(SigaaListaComando.ESTORNA_MULTA_USUARIO_BIBLIOTECA);
		
		MultaUsuariosBibliotecaDao multaDao = null;
		
		try {
			
			execute(mov);
			
			multaDao = getDAO(MultaUsuariosBibliotecaDao.class);
			
			atualizaMultasAtivasUsuario(multaDao, usuarioBiblioteca.getId());
			
			addMensagemInformation("Multa estornada com sucesso! ");
			
			return telaListaMultasUsuario();  // Retorna para página que lista as demais multas do usuário, caso ele queira pagar outra
			
		} catch (NegocioException e){
			addMensagens(e.getListaMensagens());
			return null;
		}finally{
			if(multaDao != null ) multaDao.close();
		}
		
	}
	
	
	/**
	 * Realiza os calculos manuais da suspensão para o usuário.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/formSuspensaoUsuario.jsp</li>
	 *   </ul>
	 *
	 * @param evnt
	 */
	public void calcularValorMultaManual(ActionEvent evnt){
		
		PunicaoAtrasoEmprestimoStrategyFactory p = new PunicaoAtrasoEmprestimoStrategyFactory();
		MultaStrategyDefault estrategia =   p.getEstrategiaMulta();
		
		if(prazoDevolucaoCalculoManual != null && dataDevolucaoCalculoManual != null ){
			qtdDiasEmAtrasoCalculoManual = CirculacaoUtil.calculaDiasEmAtrasoBiblioteca(prazoDevolucaoCalculoManual, dataDevolucaoCalculoManual);
			
			Emprestimo e = new Emprestimo();
			e.setPoliticaEmprestimo(new PoliticaEmprestimo(tipoPrazoCalculoManual));
			valorMultaCalculoManual = estrategia.calculaValorMulta(e, prazoDevolucaoCalculoManual, dataDevolucaoCalculoManual);
			
			if(qtdEmprestimosCalculoManual != null)
				valorMultaCalculoManual = valorMultaCalculoManual.multiply( new BigDecimal( qtdEmprestimosCalculoManual) ) ;
			
		}else{
			qtdDiasEmAtrasoCalculoManual = 0;
			valorMultaCalculoManual = new BigDecimal(0);
		}
		
		
	}
	
	
	/**
	 * Apara os dados do calculo manual
	 *
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 */
	private void apagaDadosCalculoManual(){
		prazoDevolucaoCalculoManual = null;
		dataDevolucaoCalculoManual = null;
		qtdEmprestimosCalculoManual = 1;
		tipoPrazoCalculoManual = PoliticaEmprestimo.TIPO_PRAZO_EM_DIAS;  
		qtdDiasEmAtrasoCalculoManual = null;
		valorMultaCalculoManual = null;
	}
	
	
	
	/////////////////////////////////////  Métodos da interface de pesquisa  ////////////////////////////////////////
	
	/**
	 *  Ver comentários da classe pai.<br/>
	 *  <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
		return listarMultasDoUsuarioSelecionado();
	}

	/**
	 *  Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		bibliotecaSelecioandaBuscaPadrao = biblioteca;
	}
	
	/**
	 *  Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setParametrosExtra(boolean, java.lang.String[])
	 */
	@Override
	public void setParametrosExtra(boolean parametroDePessoa, String... parametros) {
		
	}

	/**
	 *  Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		pessoaSelecioandaBuscaPadrao = p;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	

	/**
	 * Retorna todas as biblioteca ativas do sistema para as quais o usuário tem permissão de circulação, em forma de combox.
	 * 
	 * <br><br>
	 * Usado na página /biblioteca/biblioteca/lista.jsp
	 * @throws SegurancaException 
	 */
	public Collection<SelectItem> getAllBibliotecasInternasAtivasComboBox() throws DAOException, SegurancaException{
		
		Collection <Biblioteca> bs = new ArrayList<Biblioteca>();
		
		if(! isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			List<Integer> idUnidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
						getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
										  , SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
										  , SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO
										  , SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF);
			
			bs  = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivasPorUnidade(idUnidades);
			
		}else{
			bs  = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivas();
		}
		
		return toSelectItems(bs, "id", "descricaoCompleta");
	}
	
	
	/**
	 * Retorna a quantidade de multas cujas GRUs ainda não foram emitidas
	 *
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/multas/emiteGRUUnicaVariasMultasUsuario.jsp</li>
	 *   </ul>
	 *
	 * @int
	 */
	public int getQuantidadeGRUsNaoEmitidas(){
	
		int qtdGRUsNaoEmitidas = 0;
		
		if(multasAtivasUsuario != null && multasAtivasUsuario.size() > 0){
			for (MultaUsuarioBiblioteca multa : multasAtivasUsuario){
			
				if(multa.getIdGRUQuitacao() == null){
					qtdGRUsNaoEmitidas++;
				}
			}
		}
		
		return qtdGRUsNaoEmitidas;
	}
	
	/**
	 * Tela que lista as multas do usuário.
	 *
	 * 	  <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaListaMultasUsuario(){
		return forward(PAGINA_LISTA_MULTAS_USUARIO);
	}
	
	/**
	 * Tela que o bibliotecário pode criar um nova multa
	 *
	 *    <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaCadastraNovaMulta(){
		return forward(PAGINA_CRIAR_MULTA_MANUAL_USUARIO);
	}

	/**
	 * Tela que o bibliotecário pode criar um nova multa
	 *
	 *    <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 * @return
	 */
	public String telaConfirmaPagamento(){
		return forward(PAGINA_CONFIRMAR_PAGAMENTO);
	}
	
	/**
	 * Tela que o bibliotecário pode criar um nova multa
	 * 
	 *    <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 * @return
	 */
	public String telaConfirmaEstorno(){
		return forward(PAGINA_CONFIRMA_ESTORNA_MULTA);
	}
	
	
	
	///////  sets e gets ////////
	
	public InformacoesUsuarioBiblioteca getInformacaoUsuario() {
		return informacaoUsuario;
	}
	
	public void setInformacaoUsuario(InformacoesUsuarioBiblioteca informacaoUsuario) {
		this.informacaoUsuario = informacaoUsuario;
	}



	public List<MultaUsuarioBiblioteca> getMultasAtivasUsuario() {
		return multasAtivasUsuario;
	}

	public void setMultasAtivasUsuario(List<MultaUsuarioBiblioteca> multasAtivasUsuario) {
		this.multasAtivasUsuario = multasAtivasUsuario;
	}

	public String getMotivoEstorno() {
		return motivoEstorno;
	}


	public void setMotivoEstorno(String motivoEstorno) {
		this.motivoEstorno = motivoEstorno;
	}

	public BigDecimal getValorTotalMultas() {
		return valorTotalMultas;
	}

	public void setValorTotalMultas(BigDecimal valorTotalMultas) {
		this.valorTotalMultas = valorTotalMultas;
	}
	
	/**
	 * Para visualização na página JSF
	 *
	 * @return
	 */
	public String getValorTotalMultasFormatado() {
		return new PunicaoAtrasoEmprestimoStrategyFactory().getEstrategiaMulta().getValorFormatado(valorTotalMultas);
	}

	/**
	 * Para visualização na página JSF
	 */
	public void setValorTotalMultasFormatado(String valorTotalMultas) {
		// Não precisa atribuir
	}

	public String getNumeroReferencia() {
		return numeroReferencia;
	}

	public void setNumeroReferencia(String numeroReferencia) {
		this.numeroReferencia = numeroReferencia;
	}

	public ByteArrayOutputStream getOutputSteam() {
		return outputSteam;
	}

	public void setOutputSteam(ByteArrayOutputStream outputSteam) {
		this.outputSteam = outputSteam;
	}


	public Date getPrazoDevolucaoCalculoManual() {
		return prazoDevolucaoCalculoManual;
	}


	public void setPrazoDevolucaoCalculoManual(Date prazoDevolucaoCalculoManual) {
		this.prazoDevolucaoCalculoManual = prazoDevolucaoCalculoManual;
	}


	public Date getDataDevolucaoCalculoManual() {
		return dataDevolucaoCalculoManual;
	}


	public void setDataDevolucaoCalculoManual(Date dataDevolucaoCalculoManual) {
		this.dataDevolucaoCalculoManual = dataDevolucaoCalculoManual;
	}


	public Integer getQtdEmprestimosCalculoManual() {
		return qtdEmprestimosCalculoManual;
	}


	public void setQtdEmprestimosCalculoManual(Integer qtdEmprestimosCalculoManual) {
		this.qtdEmprestimosCalculoManual = qtdEmprestimosCalculoManual;
	}


	public Short getTipoPrazoCalculoManual() {
		return tipoPrazoCalculoManual;
	}


	public void setTipoPrazoCalculoManual(Short tipoPrazoCalculoManual) {
		this.tipoPrazoCalculoManual = tipoPrazoCalculoManual;
	}


	public Integer getQtdDiasEmAtrasoCalculoManual() {
		return qtdDiasEmAtrasoCalculoManual;
	}


	public void setQtdDiasEmAtrasoCalculoManual(Integer qtdDiasEmAtrasoCalculoManual) {
		this.qtdDiasEmAtrasoCalculoManual = qtdDiasEmAtrasoCalculoManual;
	}


	public BigDecimal getValorMultaCalculoManual() {
		return valorMultaCalculoManual;
	}


	public void setValorMultaCalculoManual(BigDecimal valorMultaCalculoManual) {
		this.valorMultaCalculoManual = valorMultaCalculoManual;
	}
	
	
	
}