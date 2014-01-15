/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/06/2009
 *
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.SuspensaoUsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PoliticaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SuspensaoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoEstornarSuspensoes;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.PunicaoAtrasoEmprestimoStrategyFactory;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.SuspensaoStrategyDefault;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.jsf.BuscaUsuarioBibliotecaMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * 
 *
 * <p>MBean responsável por gerenciar as suspensões dos usuários da biblioteca. </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 * @version 2.0 - jadson - Alterando para que os bibliotecário possam estornar suspensões 
 * de qualquer conta da biblioteca, mesmo as já quitadas.
 */
@Component("suspensaoUsuarioBibliotecaMBean")
@Scope("request")
public class SuspensaoUsuarioBibliotecaMBean extends SigaaAbstractController <SuspensaoUsuarioBiblioteca> implements PesquisarUsuarioBiblioteca{
	
	/** Página que lista as suspensões ativas de um usuário. */
	public static final String PAGINA_LISTA_SUSPENSOES_USUARIO = "/biblioteca/circulacao/listaSuspensoesUsuario.jsp";
	
	/** Página que permite ao bibliotecário suspender um usuário. */
	public static final String PAGINA_FORM_SUSPENSAO_USUARIO = "/biblioteca/circulacao/formSuspensaoUsuario.jsp";
	
	
	
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
	
	
	/** Armazena as informações pessoais (CPF, Nome, email, foto, etc...)  do usuário da Biblioteca selecionado. 
	 * Para mostrar para o operado do caso de uso. */
	private InformacoesUsuarioBiblioteca informacaoUsuario;
	
	
	/** Lista de suspensões que o usuário selecionado possui */
	private List <SuspensaoUsuarioBiblioteca> suspensoesUsuario;
	
	
	/** Armazena o motivo de cadastra da suspensão ou do estorno dependendo do caso de uso que se esteja usando. */
	private String motivo;
	
	/** Identifica se o acesso ao caso de uso está ativo ou não */
	private boolean ativo;
	
	/** Indica se o usuário já possui suspensões ou não */
	private boolean usuarioEstaSuspenso =  true;
	
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
	
	/** A quantidade de dias que o usuário vai ficar suspenso calculado pelo sistema. */
	private Integer qtdDiasSuspensoCalculoManual;
	
	/** A data até quando o usuário vai ficar suspenso calculado pelo sistema. */
	private Date dataFinalSuspensaoCalculoManual;
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/** Construtor do MBean */
	public SuspensaoUsuarioBibliotecaMBean (){
		obj = new SuspensaoUsuarioBiblioteca ();
	}
	
	/**
	 * <p>Inicia caso de uso de ver as suspensões dos usuários da biblitoeca</p>
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/circulacao.jsp</li>
	 *   </ul> 
	 */
	public String iniciarGerenciaSuspensoes () throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		BuscaUsuarioBibliotecaMBean pBean = getMBean ("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, true, true, false, true, "Gerenciar Suspesões dos Usuários", null);
	}
	
	
	/**
	 * <p>Inicia caso de uso de ver as suspensões dos usuários da biblitoeca</p>
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/informacao_referencia.jsp</li>
	 *   </ul> 
	 */
	public String iniciarGerenciaSuspensoesBibliotecas () throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		BuscaUsuarioBibliotecaMBean pBean = getMBean ("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, false, false, true, true, "Gerenciar Suspesões dos Usuários", null);
	}
	
	
	
	/**
	 * Exibe a lista de suspensões ativas do usuário, para que possam ser estornadas.
	 * <p>
	 * Usado em /sigaa.war/biblioteca/buscaUsuarioBiblioteca.jsp
	 */
	public String listarSuspensoesUsuario () throws ArqException{
		
		motivo = null;
		suspensoesUsuario = null;
		informacaoUsuario = null;
		ativo = true;
		
		
		prepareMovimento(SigaaListaComando.ESTORNAR_SUSPENSOES_BIBLIOTECA);

		UsuarioBibliotecaDao dao = null;
		
		try {
			dao = getDAO(UsuarioBibliotecaDao.class);
			
			
			if(pessoaSelecioandaBuscaPadrao != null){
				contasUsuarioBiblioteca = dao.findUsuarioBibliotecaAtivoByPessoa(pessoaSelecioandaBuscaPadrao.getId() );
				informacaoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(null, pessoaSelecioandaBuscaPadrao.getId(), null);
			}
			
			if(bibliotecaSelecioandaBuscaPadrao != null){
				contasUsuarioBiblioteca = dao.findUsuarioBibliotecaAtivoByBiblioteca(bibliotecaSelecioandaBuscaPadrao.getId() );
				informacaoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(null, null, bibliotecaSelecioandaBuscaPadrao.getId() );
			}
			
			
			return forward(PAGINA_LISTA_SUSPENSOES_USUARIO);
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Estorna as suspensões selecionadas.
	 * <p>
	 * Método chamado pela seguinte JSP: /sigaa.war/biblioteca/circulacao/listaSuspensoesUsuario.jsp
	 */
	public String estornar () throws ArqException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		if (!ativo){
			addMensagemErro("A operação foi cancelada. Reinicie o caso de uso.");
			return forward("/biblioteca/index.jsp");
		}
		
		if (StringUtils.isEmpty(motivo))
			addMensagemErro("Informe o motivo do estorno.");
		
		if(!confirmaSenha())
			return forward(PAGINA_LISTA_SUSPENSOES_USUARIO);

		
		if (!hasErrors()){
			
			List <SuspensaoUsuarioBiblioteca> suspensoesAEstornar = new ArrayList <SuspensaoUsuarioBiblioteca>();
			
			for (SuspensaoUsuarioBiblioteca s : getSuspensoesUsuario())
				if (s.isSelecionado())
					suspensoesAEstornar.add(s);
			
			if (suspensoesAEstornar.isEmpty()){
				addMensagemErro("Selecione pelo menos uma suspensão a estornar.");
				return null;
			}
			
			MovimentoEstornarSuspensoes mov = new MovimentoEstornarSuspensoes (suspensoesAEstornar, motivo);
			mov.setUsuarioLogado(getUsuarioLogado());
			
			try {
				execute(mov);
				addMensagemInformation("Suspensões estornardas com sucesso!");

			} catch (NegocioException e){
				addMensagens(e.getListaMensagens());
				return null;
			}
			
			return forward ("/biblioteca/index.jsp");
		}
		
		return null;
	}
	
	/**
	 * Exibe o formulário para se cadastrar uma suspensão para o usuário selecionado.
	 * <p>
	 * Método chamado pela seguinte JSP: /sigaa.war/biblioteca/circulacao/listaSuspensoesUsuario.jsp
	 */
	@Override
	public String preCadastrar () throws ArqException{
		
		setConfirmButton("Cadastrar");
		
		apagaDadosCalculoManual();
		
		Date dataInicio = null;
		usuarioEstaSuspenso =  false;
		
		boolean possuiContaNaoQuitada = false;
		
		for (UsuarioBiblioteca contaBiblioteca : contasUsuarioBiblioteca) {
			if(! contaBiblioteca.isQuitado()){
				usuarioBiblioteca = contaBiblioteca;
				possuiContaNaoQuitada = true;
			}
		}
		
		if(! possuiContaNaoQuitada ){
			addMensagemErro("Não é possível cadastrar uma nova suspensão para o usuário, porque todas as suas contas na biblioteca estão quitadas.");
			return null;
		}
		
		SuspensaoUsuarioBibliotecaDao dao = null;
		
		try {
			dao = getDAO(SuspensaoUsuarioBibliotecaDao.class);
			dataInicio = dao.findFimSuspensao(usuarioBiblioteca.ehPessoa() ?  usuarioBiblioteca.getPessoa().getId() : null
											, usuarioBiblioteca.ehBiblioteca() ? usuarioBiblioteca.getBiblioteca().getId() : null );
			
		} finally {
			
			// se não tiver suspensões o a data da última suspensão for menor que a de hoje
			if (dataInicio == null || CalendarUtils.estorouPrazo(dataInicio, new Date()))
				dataInicio = new Date();
			else
				usuarioEstaSuspenso =  true;
			
			if (dao != null)
				dao.close();
		}
		
		obj = new SuspensaoUsuarioBiblioteca();
		obj.setUsuarioBiblioteca(new UsuarioBiblioteca(usuarioBiblioteca.getId()));
		obj.setDataInicio(dataInicio);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataInicio);
		cal.add(Calendar.DAY_OF_MONTH, 3);
		
		obj.setDataFim(cal.getTime());
		
		obj.setUsuarioCadastro(getUsuarioLogado());
		obj.setManual(true);
		
		prepareMovimento(ArqListaComando.CADASTRAR);
		return forward(PAGINA_FORM_SUSPENSAO_USUARIO);
	}
	
	
	/**
	 * Cadastra uma nova suspensão para o usuário selecionado.
	 * <p>
	 * Método chamado pela seguinte JSP: /sigaa.war/biblioteca/circulacao/formSuspensaoUsuario.jsp
	 */
	@Override
	public String cadastrar () throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		if (getConfirmButton().equalsIgnoreCase("alterar"))
			return alterar();
		
		MovimentoCadastro mov = new MovimentoCadastro (obj);
		mov.setCodMovimento(ArqListaComando.CADASTRAR);
		
		addMensagens(obj.validate());
		
		if (!hasErrors())
			try {
				execute(mov);
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
				
				suspensoesUsuario = null;
				
				return forward(PAGINA_LISTA_SUSPENSOES_USUARIO);
				
			} catch (NegocioException e){
				addMensagens(e.getListaMensagens());
			}
		
		return null;
	}
	
	/**
	 * Exibe o formulário para alterar a suspensão.
	 * <p>
	 * Método chamado pela seguinte JSP: /sigaa.war/biblioteca/circulacao/listaSuspensoesUsuario.jsp
	 */
	public String preAlterar () throws ArqException{
		
		apagaDadosCalculoManual();
		
		Boolean isSupensaoManual = getParameterBoolean("suspensao_manual");
		
		if(isSupensaoManual!= null && ! isSupensaoManual){
			addMensagemErro("A suspensão não pode ter seu período alterado porque ela não é manual.");
			return null;
		}
		
		setConfirmButton("Alterar");
		populateObj(true);
		prepareMovimento(SigaaListaComando.ALTERAR_SUSPENSAO_USUARIO_BIBLIOTECA);
		return forward(PAGINA_FORM_SUSPENSAO_USUARIO);
	}
	
	/**
	 * Atualiza uma suspensão, corrigindo as datas para todas as próximas suspensões do usuário.
	 * <p>
	 * Método chamado pela seguinte JSP: /sigaa.war/biblioteca/circulacao/formSuspensaoUsuario.jsp
	 */
	public String alterar () throws ArqException{
		
		MovimentoCadastro mov = new MovimentoCadastro(obj);
		mov.setCodMovimento(SigaaListaComando.ALTERAR_SUSPENSAO_USUARIO_BIBLIOTECA);
		
		try {
			execute(mov);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			suspensoesUsuario = null;
			return forward(PAGINA_LISTA_SUSPENSOES_USUARIO);
			
		} catch (NegocioException e){
			addMensagens(e.getListaMensagens());
		}
		
		return null;
	}

	public InformacoesUsuarioBiblioteca getInformacaoUsuario() {
		return informacaoUsuario;
	}

	public void setInformacaoUsuario(InformacoesUsuarioBiblioteca informacaoUsuario) {
		this.informacaoUsuario = informacaoUsuario;
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
	public void calcularDataSuspensaoManual(ActionEvent evnt){
		
		PunicaoAtrasoEmprestimoStrategyFactory p = new PunicaoAtrasoEmprestimoStrategyFactory();
		SuspensaoStrategyDefault estrategia =   p.getEstrategiaSuspensao();
		
		if(prazoDevolucaoCalculoManual != null && dataDevolucaoCalculoManual != null ){
			qtdDiasEmAtrasoCalculoManual = CirculacaoUtil.calculaDiasEmAtrasoBiblioteca(prazoDevolucaoCalculoManual, dataDevolucaoCalculoManual);
			
			Emprestimo e = new Emprestimo();
			e.setPoliticaEmprestimo(new PoliticaEmprestimo(tipoPrazoCalculoManual));
			qtdDiasSuspensoCalculoManual = estrategia.calculaDiasSuspensao(e, prazoDevolucaoCalculoManual, dataDevolucaoCalculoManual);
			
			if(qtdEmprestimosCalculoManual != null)
				qtdDiasSuspensoCalculoManual *= qtdEmprestimosCalculoManual;
		}else{
			qtdDiasSuspensoCalculoManual = 0;
		}
		
		// a data de início da suspensão é sempre a data de hoje ou a data do último dia que o usuário estará suspenso.
		dataFinalSuspensaoCalculoManual = obj.getDataInicio();
		
		dataFinalSuspensaoCalculoManual = CalendarUtils.adicionaDias(dataFinalSuspensaoCalculoManual, qtdDiasSuspensoCalculoManual);
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
		qtdDiasSuspensoCalculoManual = null;
		dataFinalSuspensaoCalculoManual = null;
	}
	
	
	/**
	 * Retorna as suspensões do usuário.
	 * <p>
	 * Método chamado pela seguinte JSP: /sigaa.war/biblioteca/circulacao/listaSuspensoesUsuario.jsp
	 */
	public List <SuspensaoUsuarioBiblioteca> getSuspensoesUsuario() throws DAOException {
		
		if (suspensoesUsuario == null){
			SuspensaoUsuarioBibliotecaDao dao = null;
			
			try{
			
						
				dao = getDAO (SuspensaoUsuarioBibliotecaDao.class);
				
				// Encontra todas as suspensões de todas as contas do usuário //
				suspensoesUsuario = dao.findSuspensoesFuturasDoUsuario(
						pessoaSelecioandaBuscaPadrao != null ? pessoaSelecioandaBuscaPadrao.getId() : null
						, bibliotecaSelecioandaBuscaPadrao != null ? bibliotecaSelecioandaBuscaPadrao.getId() : null, new Date());
					
				/*
				 *  Configura as informações de acordo com o tipo suspensão: se manual ou automática.
				 */
				for (SuspensaoUsuarioBiblioteca su : suspensoesUsuario){
					
					if (su.getEmprestimo() != null){
						
						su.getEmprestimo().getMaterial().setInformacao(  BibliotecaUtil.obtemDadosResumidosTituloDoMaterial(  su.getEmprestimo().getMaterial().getId() ) );
						
					} else {
						
						// Apenas para mostrar as inforamações ao usuário //
						su.setEmprestimo( new Emprestimo());
						su.getEmprestimo().setMaterial( new Exemplar()); 
						
						if(su.isManual() ){
							su.getEmprestimo().getMaterial().setInformacao( "SUSPENSÃO MANUAL, motivo: "+ ( br.ufrn.arq.util.StringUtils.notEmpty( su.getMotivoCadastro()) ? su.getMotivoCadastro() : " Não informado ")  );
						}else{
							su.getEmprestimo().getMaterial().setInformacao( "SUSPENSÃO MIGRADA (não está associada a um material) "  );
						}
						
					}
				}
				
				
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		
		return suspensoesUsuario;
	}

	public void setSuspensoesUsuario(List<SuspensaoUsuarioBiblioteca> suspensoesUsuario) {
		this.suspensoesUsuario = suspensoesUsuario;
	}

	public UsuarioBiblioteca getUsuarioBiblioteca() {
		return usuarioBiblioteca;
	}

	public void setUsuarioBiblioteca(UsuarioBiblioteca usuarioBiblioteca) {
		this.usuarioBiblioteca = usuarioBiblioteca;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
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

	public Integer getQtdDiasSuspensoCalculoManual() {
		return qtdDiasSuspensoCalculoManual;
	}

	public void setQtdDiasSuspensoCalculoManual(Integer qtdDiasSuspensoCalculoManual) {
		this.qtdDiasSuspensoCalculoManual = qtdDiasSuspensoCalculoManual;
	}

	public Date getDataFinalSuspensaoCalculoManual() {
		return dataFinalSuspensaoCalculoManual;
	}

	public void setDataFinalSuspensaoCalculoManual(Date dataFinalSuspensaoCalculoManual) {
		this.dataFinalSuspensaoCalculoManual = dataFinalSuspensaoCalculoManual;
	}

	public boolean isUsuarioEstaSuspenso() {
		return usuarioEstaSuspenso;
	}

	public void setUsuarioEstaSuspenso(boolean usuarioEstaSuspenso) {
		this.usuarioEstaSuspenso = usuarioEstaSuspenso;
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
		return listarSuspensoesUsuario();
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		bibliotecaSelecioandaBuscaPadrao = biblioteca;
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setParametrosExtra(boolean, java.lang.String[])
	 */
	@Override
	public void setParametrosExtra(boolean parametroDePessoa, String... parametros) {
		
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		pessoaSelecioandaBuscaPadrao = p;
	}
}