/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * <p>MBean respons�vel por gerenciar as suspens�es dos usu�rios da biblioteca. </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 * @version 2.0 - jadson - Alterando para que os bibliotec�rio possam estornar suspens�es 
 * de qualquer conta da biblioteca, mesmo as j� quitadas.
 */
@Component("suspensaoUsuarioBibliotecaMBean")
@Scope("request")
public class SuspensaoUsuarioBibliotecaMBean extends SigaaAbstractController <SuspensaoUsuarioBiblioteca> implements PesquisarUsuarioBiblioteca{
	
	/** P�gina que lista as suspens�es ativas de um usu�rio. */
	public static final String PAGINA_LISTA_SUSPENSOES_USUARIO = "/biblioteca/circulacao/listaSuspensoesUsuario.jsp";
	
	/** P�gina que permite ao bibliotec�rio suspender um usu�rio. */
	public static final String PAGINA_FORM_SUSPENSAO_USUARIO = "/biblioteca/circulacao/formSuspensaoUsuario.jsp";
	
	
	
	/** Guarda a informa��es sobre a pessoa selecionada da busca padr�o.
	 * Isso porque esse caso de uso deve poder retirar suspens�es, mesmo que o v�nculo do usu�rio esteja quitado. 
	 */
	private Pessoa pessoaSelecioandaBuscaPadrao;
	
	/** Guarda a informa��es sobre a biblioteca selecionada da busca padr�o.
	 * Isso porque esse caso de uso deve poder retirar suspens�es, mesmo que o v�nculo do usu�rio esteja quitado. 
	 */
	private Biblioteca bibliotecaSelecioandaBuscaPadrao;
	
	
	/**
	 * Informa��es sobre todas as contas que a pessoa j� tive na biblioteca
	 */
	private List<UsuarioBiblioteca> contasUsuarioBiblioteca = new ArrayList<UsuarioBiblioteca>(); 
	
	
	/** Armazena as informa��es de conta do usu�rio ATUAL na biblioteca (N�o quitada)
	 *
	 * � para esse conta que s�o cadastrada as novas suspens�es, por esse caso de uso.
	 */
	private UsuarioBiblioteca usuarioBiblioteca = new UsuarioBiblioteca();
	
	
	/** Armazena as informa��es pessoais (CPF, Nome, email, foto, etc...)  do usu�rio da Biblioteca selecionado. 
	 * Para mostrar para o operado do caso de uso. */
	private InformacoesUsuarioBiblioteca informacaoUsuario;
	
	
	/** Lista de suspens�es que o usu�rio selecionado possui */
	private List <SuspensaoUsuarioBiblioteca> suspensoesUsuario;
	
	
	/** Armazena o motivo de cadastra da suspens�o ou do estorno dependendo do caso de uso que se esteja usando. */
	private String motivo;
	
	/** Identifica se o acesso ao caso de uso est� ativo ou n�o */
	private boolean ativo;
	
	/** Indica se o usu�rio j� possui suspens�es ou n�o */
	private boolean usuarioEstaSuspenso =  true;
	
	/** Cont�m o prazo da devolu��o para o calculo da data de suspens�o manual pelo sistema. */
	private Date prazoDevolucaoCalculoManual;
	
	/** Cont�m a data de devolu��o para o calculo da data de suspens�o manual pelo sistema. */
	private Date dataDevolucaoCalculoManual;
	
	/** Cont�m a quantidade de empr�stimos feitos para o calculo da data de suspens�o manual pelo sistema. */
	private Integer qtdEmprestimosCalculoManual = 1;
	
	/** Cont�m o tipo de prazo dos empr�stimos para o calculo da data de suspens�o manual pelo sistema. */
	private Short tipoPrazoCalculoManual = PoliticaEmprestimo.TIPO_PRAZO_EM_DIAS;  
	
	/** A quantidade de dias que o usu�rio atrazou calculado pelo sistema. */
	private Integer qtdDiasEmAtrasoCalculoManual;
	
	/** A quantidade de dias que o usu�rio vai ficar suspenso calculado pelo sistema. */
	private Integer qtdDiasSuspensoCalculoManual;
	
	/** A data at� quando o usu�rio vai ficar suspenso calculado pelo sistema. */
	private Date dataFinalSuspensaoCalculoManual;
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/** Construtor do MBean */
	public SuspensaoUsuarioBibliotecaMBean (){
		obj = new SuspensaoUsuarioBiblioteca ();
	}
	
	/**
	 * <p>Inicia caso de uso de ver as suspens�es dos usu�rios da biblitoeca</p>
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/circulacao.jsp</li>
	 *   </ul> 
	 */
	public String iniciarGerenciaSuspensoes () throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		BuscaUsuarioBibliotecaMBean pBean = getMBean ("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, true, true, false, true, "Gerenciar Suspes�es dos Usu�rios", null);
	}
	
	
	/**
	 * <p>Inicia caso de uso de ver as suspens�es dos usu�rios da biblitoeca</p>
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/informacao_referencia.jsp</li>
	 *   </ul> 
	 */
	public String iniciarGerenciaSuspensoesBibliotecas () throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		BuscaUsuarioBibliotecaMBean pBean = getMBean ("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, false, false, true, true, "Gerenciar Suspes�es dos Usu�rios", null);
	}
	
	
	
	/**
	 * Exibe a lista de suspens�es ativas do usu�rio, para que possam ser estornadas.
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
	 * Estorna as suspens�es selecionadas.
	 * <p>
	 * M�todo chamado pela seguinte JSP: /sigaa.war/biblioteca/circulacao/listaSuspensoesUsuario.jsp
	 */
	public String estornar () throws ArqException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		if (!ativo){
			addMensagemErro("A opera��o foi cancelada. Reinicie o caso de uso.");
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
				addMensagemErro("Selecione pelo menos uma suspens�o a estornar.");
				return null;
			}
			
			MovimentoEstornarSuspensoes mov = new MovimentoEstornarSuspensoes (suspensoesAEstornar, motivo);
			mov.setUsuarioLogado(getUsuarioLogado());
			
			try {
				execute(mov);
				addMensagemInformation("Suspens�es estornardas com sucesso!");

			} catch (NegocioException e){
				addMensagens(e.getListaMensagens());
				return null;
			}
			
			return forward ("/biblioteca/index.jsp");
		}
		
		return null;
	}
	
	/**
	 * Exibe o formul�rio para se cadastrar uma suspens�o para o usu�rio selecionado.
	 * <p>
	 * M�todo chamado pela seguinte JSP: /sigaa.war/biblioteca/circulacao/listaSuspensoesUsuario.jsp
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
			addMensagemErro("N�o � poss�vel cadastrar uma nova suspens�o para o usu�rio, porque todas as suas contas na biblioteca est�o quitadas.");
			return null;
		}
		
		SuspensaoUsuarioBibliotecaDao dao = null;
		
		try {
			dao = getDAO(SuspensaoUsuarioBibliotecaDao.class);
			dataInicio = dao.findFimSuspensao(usuarioBiblioteca.ehPessoa() ?  usuarioBiblioteca.getPessoa().getId() : null
											, usuarioBiblioteca.ehBiblioteca() ? usuarioBiblioteca.getBiblioteca().getId() : null );
			
		} finally {
			
			// se n�o tiver suspens�es o a data da �ltima suspens�o for menor que a de hoje
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
	 * Cadastra uma nova suspens�o para o usu�rio selecionado.
	 * <p>
	 * M�todo chamado pela seguinte JSP: /sigaa.war/biblioteca/circulacao/formSuspensaoUsuario.jsp
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
	 * Exibe o formul�rio para alterar a suspens�o.
	 * <p>
	 * M�todo chamado pela seguinte JSP: /sigaa.war/biblioteca/circulacao/listaSuspensoesUsuario.jsp
	 */
	public String preAlterar () throws ArqException{
		
		apagaDadosCalculoManual();
		
		Boolean isSupensaoManual = getParameterBoolean("suspensao_manual");
		
		if(isSupensaoManual!= null && ! isSupensaoManual){
			addMensagemErro("A suspens�o n�o pode ter seu per�odo alterado porque ela n�o � manual.");
			return null;
		}
		
		setConfirmButton("Alterar");
		populateObj(true);
		prepareMovimento(SigaaListaComando.ALTERAR_SUSPENSAO_USUARIO_BIBLIOTECA);
		return forward(PAGINA_FORM_SUSPENSAO_USUARIO);
	}
	
	/**
	 * Atualiza uma suspens�o, corrigindo as datas para todas as pr�ximas suspens�es do usu�rio.
	 * <p>
	 * M�todo chamado pela seguinte JSP: /sigaa.war/biblioteca/circulacao/formSuspensaoUsuario.jsp
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
	 * Realiza os calculos manuais da suspens�o para o usu�rio.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
		
		// a data de in�cio da suspens�o � sempre a data de hoje ou a data do �ltimo dia que o usu�rio estar� suspenso.
		dataFinalSuspensaoCalculoManual = obj.getDataInicio();
		
		dataFinalSuspensaoCalculoManual = CalendarUtils.adicionaDias(dataFinalSuspensaoCalculoManual, qtdDiasSuspensoCalculoManual);
	}
	
	
	/**
	 * Apara os dados do calculo manual
	 *
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
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
	 * Retorna as suspens�es do usu�rio.
	 * <p>
	 * M�todo chamado pela seguinte JSP: /sigaa.war/biblioteca/circulacao/listaSuspensoesUsuario.jsp
	 */
	public List <SuspensaoUsuarioBiblioteca> getSuspensoesUsuario() throws DAOException {
		
		if (suspensoesUsuario == null){
			SuspensaoUsuarioBibliotecaDao dao = null;
			
			try{
			
						
				dao = getDAO (SuspensaoUsuarioBibliotecaDao.class);
				
				// Encontra todas as suspens�es de todas as contas do usu�rio //
				suspensoesUsuario = dao.findSuspensoesFuturasDoUsuario(
						pessoaSelecioandaBuscaPadrao != null ? pessoaSelecioandaBuscaPadrao.getId() : null
						, bibliotecaSelecioandaBuscaPadrao != null ? bibliotecaSelecioandaBuscaPadrao.getId() : null, new Date());
					
				/*
				 *  Configura as informa��es de acordo com o tipo suspens�o: se manual ou autom�tica.
				 */
				for (SuspensaoUsuarioBiblioteca su : suspensoesUsuario){
					
					if (su.getEmprestimo() != null){
						
						su.getEmprestimo().getMaterial().setInformacao(  BibliotecaUtil.obtemDadosResumidosTituloDoMaterial(  su.getEmprestimo().getMaterial().getId() ) );
						
					} else {
						
						// Apenas para mostrar as inforama��es ao usu�rio //
						su.setEmprestimo( new Emprestimo());
						su.getEmprestimo().setMaterial( new Exemplar()); 
						
						if(su.isManual() ){
							su.getEmprestimo().getMaterial().setInformacao( "SUSPENS�O MANUAL, motivo: "+ ( br.ufrn.arq.util.StringUtils.notEmpty( su.getMotivoCadastro()) ? su.getMotivoCadastro() : " N�o informado ")  );
						}else{
							su.getEmprestimo().getMaterial().setInformacao( "SUSPENS�O MIGRADA (n�o est� associada a um material) "  );
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
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
		return listarSuspensoesUsuario();
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		bibliotecaSelecioandaBuscaPadrao = biblioteca;
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setParametrosExtra(boolean, java.lang.String[])
	 */
	@Override
	public void setParametrosExtra(boolean parametroDePessoa, String... parametros) {
		
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		pessoaSelecioandaBuscaPadrao = p;
	}
}