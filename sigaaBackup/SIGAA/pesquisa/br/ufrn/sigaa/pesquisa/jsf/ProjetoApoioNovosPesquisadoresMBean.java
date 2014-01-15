package br.ufrn.sigaa.pesquisa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.integracao.dto.FormacaoAcademicaDTO;
import br.ufrn.integracao.interfaces.FormacaoAcademicaRemoteService;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.arq.dao.pesquisa.EditalPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoApoioNovosPesquisadoresDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.ElementoDespesa;
import br.ufrn.sigaa.extensao.dominio.ResumoOrcamentoDetalhado;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pesquisa.dao.PropostaGrupoPesquisaDao;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoApoioNovosPesquisadores;
import br.ufrn.sigaa.pesquisa.dominio.TipoPassoProjetoNovoPesquisador;
import br.ufrn.sigaa.pesquisa.form.TelaCronograma;
import br.ufrn.sigaa.pesquisa.negocio.ProjetoApoioNovosPesquisadoresValidator;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.negocio.CronogramaProjetoHelper;

/**
 * Controlador responsável pelo cadastro de projetos de apoio a novos pesquisadores.
 * 
 * @author Jean Guerethes
 *
 */
@Component @Scope("session")
public class ProjetoApoioNovosPesquisadoresMBean extends SigaaAbstractController<ProjetoApoioNovosPesquisadores> {

	/** Atributo utilizado para mostrar o cronograma */
	private TelaCronograma telaCronograma = new TelaCronograma();
    /** Armazena um orçamento detalhado. **/
    private OrcamentoDetalhado orcamento;
    /** Armazena um orçamento detalhado. **/
    private OrcamentoDetalhado orcamentoRemocao;
    /** Armazena a tabela orçamentária. **/
    private Map<ElementoDespesa, ResumoOrcamentoDetalhado> tabelaOrcamentaria;
    /** Armazena os Projetos de Apoio a Novos Pesquisadores **/
    private Collection<ProjetoApoioNovosPesquisadores> projetosApoio;

	/** Construtor padrão. */
	public ProjetoApoioNovosPesquisadoresMBean() {
		clear();
	}

	/**
	 * Inicializa os atributos utilizados pelo controlador.
	 */
	private void clear() {
		obj = new ProjetoApoioNovosPesquisadores();
		obj.setEditalPesquisa(new EditalPesquisa());
		obj.setGrupoPesquisa(new GrupoPesquisa());
		obj.setProjeto(new Projeto());
		obj.setCoordenador(new Servidor());
		orcamento = new OrcamentoDetalhado();
		orcamentoRemocao = new OrcamentoDetalhado();
		tabelaOrcamentaria = new HashMap<ElementoDespesa, ResumoOrcamentoDetalhado>();
	}

	@Override
	public String getDirBase() {
		return "/pesquisa/ProjetoApoioNovosPesquisadores";
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		if ( !obj.isVisualizar() ) {
			addMensagemErro("É necessário aceitar os termos, para a submissão do projeto de Apoio.");
			return null;
		}
		
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_PROJETO_APOIO_NOVOS_PESQUISADORES);
			prepareMovimento(SigaaListaComando.CADASTRAR_PROJETO_APOIO_NOVOS_PESQUISADORES);
			execute(mov);
			if (isReprepare())
				prepareMovimento(SigaaListaComando.CADASTRAR_PROJETO_APOIO_NOVOS_PESQUISADORES);
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		
		if (hasErrors()) {
			return null;
		} else {
			addMensagemInformation("O Projeto foi Submetido com Sucesso.");
			if(isPesquisa() && isUserInRole(SigaaPapeis.GESTOR_PESQUISA)){
				BuscaProjetoApoioNovosPesquisadoresMBean bean = (BuscaProjetoApoioNovosPesquisadoresMBean) getMBean("buscaProjetoApoioNovosPesquisadoresMBean");
				return bean.iniciar();
			}
			return listar();
		}
	}

	@Override
	public String remover() throws ArqException {
		setId();
		setObj( getGenericDAO().findAndFetch(obj.getId(), ProjetoApoioNovosPesquisadores.class, "projeto"));
		
		if ( ValidatorUtil.isEmpty(obj) ) {
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setCodMovimento(SigaaListaComando.REMOVER_PROJETO_APOIO_NOVOS_PESQUISADORES);
			prepareMovimento(SigaaListaComando.REMOVER_PROJETO_APOIO_NOVOS_PESQUISADORES);
			execute(mov);
			if (isReprepare())
				prepareMovimento(SigaaListaComando.CADASTRAR_PROJETO_APOIO_NOVOS_PESQUISADORES);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
		if (hasErrors()) {
			return null;
		} else {
			addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Projeto");
			if(isPesquisa() && isUserInRole(SigaaPapeis.GESTOR_PESQUISA)){
				BuscaProjetoApoioNovosPesquisadoresMBean bean = (BuscaProjetoApoioNovosPesquisadoresMBean) getMBean("buscaProjetoApoioNovosPesquisadoresMBean");
				return bean.iniciar();
			}
			return listar();
		}
	}
	
	/**
	 * Registra parcialmente as informações do projeto no banco.
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void gravarParcialmente() throws SegurancaException, ArqException,
			NegocioException {
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setCodMovimento(SigaaListaComando.ENVIAR_PROJETO_APOIO_NOVOS_PESQUISADORES);
			prepareMovimento(SigaaListaComando.ENVIAR_PROJETO_APOIO_NOVOS_PESQUISADORES);
			execute(mov);
			if (isReprepare())
				prepareMovimento(SigaaListaComando.ENVIAR_PROJETO_APOIO_NOVOS_PESQUISADORES);
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}
	}
	
	@Override
	public String preCadastrar() throws ArqException, NegocioException {

		if(getUsuarioLogado().getVinculoAtivo().getServidor() == null){
			addMensagemErro("Operação restrita à docentes efetivos da instituição.");
			return null;
		}
		
		PropostaGrupoPesquisaDao dao = getDAO(PropostaGrupoPesquisaDao.class);
		try {
			clear();
			obj.setTipoPassosProjeto( TipoPassoProjetoNovoPesquisador.TELA_DADOS_GERAIS);
			obj.setCoordenador( getUsuarioLogado().getVinculoAtivo().getServidor() );

			obj.getCoordenador().getPessoa().setPerfil(new PerfilPessoa());
			obj.getCoordenador().getPessoa().getPerfil().setEnderecoLattes( dao.getEnderecoLattes(obj.getCoordenador()));
			
			
			FormacaoAcademicaRemoteService serviceFormacao = getMBean("formacaoAcademicaInvoker");
			List<FormacaoAcademicaDTO> formacaoDoutorado = serviceFormacao.consultarFormacaoAcademica(obj.getCoordenador().getId(), 
					null, null, null, null, null, null,	Formacao.DOUTOR);
			
			if ( isEmpty(formacaoDoutorado) ) {
				addMensagemErro("Não foi encontrada nenhuma formação de Doutorado cadastrada.");
				return null;
			} else {
				Date fimDoutorado = formacaoDoutorado.get(0).getFim();
				
				if(ValidatorUtil.isEmpty(fimDoutorado)){
					addMensagemErro("A data de término da sua formação de doutorado não foi informada.<br/>" +
							"Atualize a sua formação acessando o " +RepositorioDadosInstitucionais.getAll().get("siglaSigrh")+ " no menu Serviços -> Atualizar Formação Acadêmica.");
					return null;
				}
				
				Date dataReferencia = fimDoutorado;
				
				ParametroHelper parametroHelper = ParametroHelper.getInstance();
				if(parametroHelper.getParametroBoolean(ParametrosPesquisa.UTILIZA_DATA_ADMISSAO_APOIO_NOVOS_PESQUISADORES))
				{
					Date admissao = obj.getCoordenador().getDataAdmissao();
					if(admissao.after(fimDoutorado))
						dataReferencia = admissao;
				}
				
				int anos =  parametroHelper.getParametroInt(ParametrosPesquisa.QNT_EM_ANOS_PARA_OS_RECEM_DOUTORES);
				
				if(CalendarUtils.getAnoAtual() - CalendarUtils.getAno(dataReferencia) > anos ){
					addMensagemErro("Só podem solicitar Apoio os Doutores com no máximo " + anos + " anos de doutorado.");
					return null;
				}
			}
			
		} finally {
			dao.close();
		}
		
		return super.preCadastrar();
	}

	@Override
	public String listar() throws ArqException {
		ProjetoApoioNovosPesquisadoresDao dao = getDAO(ProjetoApoioNovosPesquisadoresDao.class);
		try {
			projetosApoio = dao.projetosCadastradosLeve( getUsuarioLogado().getServidor().getId() );
		} finally {
			dao.close();
		}
		return forward(getListPage());
	}

	/**
	 * Carrega as informações do projeto a partir do banco.
	 * @return
	 * @throws DAOException
	 */
	private ProjetoApoioNovosPesquisadores carregarInformacoes() throws DAOException{
		setId();
		ProjetoApoioNovosPesquisadoresDao dao = getDAO(ProjetoApoioNovosPesquisadoresDao.class);
		try {
			setObj( dao.carregarProjetoApoio(obj.getId()) );
			
			carregarOrcamento(dao);
			obj.setCoordenador(dao.findByPrimaryKey(obj.getCoordenador().getId(), Servidor.class));
			
			dao.initialize(obj.getEditalPesquisa());
			// Inicializar tela do cronograma
			carregarCronograma();
			obj.setVisualizar(true);
			
			if ( obj == null ) {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				return null;
			}
		} finally {
			dao.close();
		}
		return obj;
	}

	/**
	 * Carrega as informações do orçamento do projeto a partir do banco.
	 * @param dao
	 * @throws DAOException
	 */
	private void carregarOrcamento(ProjetoApoioNovosPesquisadoresDao dao)
			throws DAOException {
		obj.getProjeto().setOrcamento(new ArrayList<OrcamentoDetalhado>());
		obj.getProjeto().getOrcamento().addAll(
				dao.findByExactField(OrcamentoDetalhado.class, "projeto.id", obj.getProjeto().getId()) );
		recalculaTabelaOrcamentaria(obj.getProjeto().getOrcamento());
	}

	/**
	 * Encaminha para a tela de resumo do projeto.
	 * @return
	 * @throws DAOException
	 */
	public String view() throws DAOException{
		PropostaGrupoPesquisaDao dao = getDAO(PropostaGrupoPesquisaDao.class);
		try {
			clear();
			carregarInformacoes();
			obj.setTipoPassosProjeto(TipoPassoProjetoNovoPesquisador.TELA_DADOS_GERAIS);
			obj.getCoordenador().getPessoa().setPerfil(new PerfilPessoa());
			obj.getCoordenador().getPessoa().getPerfil().setEnderecoLattes( dao.getEnderecoLattes(obj.getCoordenador()));
		} finally {
			dao.close();
		}

		return telaResumo();
	}
	
	@Override
	public String atualizar() throws ArqException {
		
		super.atualizar();
		PropostaGrupoPesquisaDao dao = getDAO(PropostaGrupoPesquisaDao.class);
		try {
			obj.setTipoPassosProjeto(TipoPassoProjetoNovoPesquisador.TELA_DADOS_GERAIS);
			obj.getCoordenador().getPessoa().setPerfil(new PerfilPessoa());
			obj.getCoordenador().getPessoa().getPerfil().setEnderecoLattes( dao.getEnderecoLattes(obj.getCoordenador()));
			if (isEmpty(obj.getGrupoPesquisa()))
				obj.setGrupoPesquisa(new GrupoPesquisa());
		} finally {
			dao.close();
		}
		
		return telaDadosGerais(); 
	}
	
	/**
	 * Submete o formulário de dados gerais do projeto para validação.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\pesquisa\ProjetoApoioNovosPesquisadores\form.jsp</li>
	 *   </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String submeterDadosGerais() throws SegurancaException, ArqException, NegocioException {
		ProjetoApoioNovosPesquisadoresValidator.validaDadosGerais(obj, getDAO(ProjetoApoioNovosPesquisadoresDao.class), erros);
		if ( hasErrors() ) {
			return null;
		}

		gravarParcialmente();
		carregarCronograma();
		obj.setTipoPassosProjeto(TipoPassoProjetoNovoPesquisador.TELA_CRONOGRAMA_PROJETO);
		return telaCronograma();
	}

	/**
	 * Carrega as informações do Cronograma
	 * @throws DAOException
	 */
	private void carregarCronograma() throws DAOException {
		setTelaCronograma(CronogramaProjetoHelper.carregarTelaCronogramaProjetoEditalPesquisa(getGenericDAO(), obj.getProjeto(), obj.getEditalPesquisa()));
	}
	
	/**
	 * Submete a tela do Cronograma.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\pesquisa\ProjetoApoioNovosPesquisadores\cronograma.jsp</li>
	 *   </ul>
	 * @return
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public String submeterCronograma() throws NumberFormatException, Exception{
		ProjetoApoioNovosPesquisadoresValidator.validaCronograma(getCurrentRequest(), telaCronograma, obj.getProjeto(), erros);
		if ( hasErrors() ) {
			return null;
		}
		
		gravarParcialmente();
		carregarOrcamento(getDAO(ProjetoApoioNovosPesquisadoresDao.class));
		
		return telaOrcamento();
	}

	/**
	 * Submete o orçamento do projeto para validação. 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\pesquisa\ProjetoApoioNovosPesquisadores\orcamento.jsp</li>
	 *   </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String submeterOrcamento() throws SegurancaException, ArqException, NegocioException{
		gravarParcialmente();
		obj.setEditalPesquisa( 
				getGenericDAO().findAndFetch(obj.getEditalPesquisa().getId(), EditalPesquisa.class, "edital") );
		obj.setVisualizar(false);
		
		float valorTotal = 0;
		for (OrcamentoDetalhado orcamento : obj.getOrcamentosDetalhados())
			 valorTotal += orcamento.getValorTotal();
		
		int valorMaximo =  ParametroHelper.getInstance().getParametroInt(ParametrosPesquisa.VALOR_MAXIMO_ORCAMENTO_PROJ_APOIO_NOVOS_PESQUISADORES);
		if (valorTotal > valorMaximo) {
			addMensagemErro("O valor máximo permitido para o orçamento é de R$ " + Formatador.getInstance().formatarMoeda(valorMaximo));
			return null;
		}
		return telaResumo();
	}

	/**
	 * Encaminha para a tela do cronograma.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\pesquisa\ProjetoApoioNovosPesquisadores\orcamento.jsp</li>
	 *   </ul>
	 * @return
	 */
	public String telaCronograma() {
		obj.setTipoPassosProjeto(TipoPassoProjetoNovoPesquisador.TELA_CRONOGRAMA_PROJETO);
		return forward(getDirBase() + "/cronograma.jsp");
	}

	/**
	 * Encaminha para a tela do orçamento do projeto.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\pesquisa\ProjetoApoioNovosPesquisadores\resumo.jsp</li>
	 *   </ul>
	 * @return
	 */
	public String telaOrcamento() {
		obj.setTipoPassosProjeto(TipoPassoProjetoNovoPesquisador.TELA_ORCAMENTO_PROJETO);
		return forward(getDirBase() + "/orcamento.jsp");
	}

	/**
	 * Encaminha para a tela de resumo do projeto.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\pesquisa\ProjetoApoioNovosPesquisadores\lista.jsp</li>
	 *    <li>sigaa.war\pesquisa\ProjetoApoioNovosPesquisadores\busca.jsp</li>
	 *   </ul>
	 * @return
	 */
	public String telaResumo() {
		obj.setTipoPassosProjeto(TipoPassoProjetoNovoPesquisador.TELA_RESUMO_PROJETO);
		return forward(getDirBase() + "/view.jsp");
	}
	
	/**
	 * Encaminha para a tela de dados gerais do projeto.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\pesquisa\ProjetoApoioNovosPesquisadores\cronograma.jsp</li>
	 *   </ul>
	 * @return
	 */
	public String telaDadosGerais() {
		obj.setTipoPassosProjeto(TipoPassoProjetoNovoPesquisador.TELA_DADOS_GERAIS);
		if ( isEmpty( obj.getGrupoPesquisa() ) )
			obj.setGrupoPesquisa( new GrupoPesquisa() );
		return forward( getDirBase() + "/form.jsp" );
	}

	/**
	 * Adiciona um item de orçamento ao projeto.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\pesquisa\ProjetoApoioNovosPesquisadores\orcamento.jsp</li>
	 *   </ul>
	 * @return
	 */
	public String adicionaOrcamento() {
		try {
			ListaMensagens mensagens = new ListaMensagens();
			Integer idElementoDespesa = getParameterInt("idElementoDespesa", 0);
			orcamento.setElementoDespesa(getGenericDAO().findByPrimaryKey(idElementoDespesa, ElementoDespesa.class));
			orcamento.setAtivo(true);
			
			if ( idElementoDespesa == 0) {
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Elemento de Despesa");
				return null;
			}
			
			// Mantem o botão precionado
			getCurrentRequest().getSession().setAttribute("idElementoDespesa", orcamento.getElementoDespesa().getId());
			
			if ((idElementoDespesa == null) || (idElementoDespesa == 0)) {
				addMensagemErro("Elemento de Despesa é Obrigatório: Selecione um elemento de despesa");
				return null;
			}
			
			if (orcamento.getValorUnitario() == null){
				addMensagem(MensagensArquitetura.VALOR_MAIOR_IGUAL_ZERO, "Valor Unitário");
				return null;
			}

			ProjetoApoioNovosPesquisadoresValidator.validaAdicionaOrcamento(orcamento, mensagens);
			if (!mensagens.isEmpty()) {
				addMensagens(mensagens);
				return null;
			}

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
		}

		obj.addOrcamentoDetalhado(orcamento);

		if (orcamento.getElementoDespesa() == null)
			orcamento.setElementoDespesa( new ElementoDespesa() );
		
		// Prepara para novo item do orçamento
		getCurrentSession().setAttribute("idElementoDespesa", orcamento.getElementoDespesa().getId());
		orcamento = new OrcamentoDetalhado();
		orcamento.setElementoDespesa(new ElementoDespesa());
		recalculaTabelaOrcamentaria(obj.getOrcamentosDetalhados());
		return redirectMesmaPagina();
	}

	/**
	 * Recalcula a tabela orçamentária do projeto.
	 * @param orcamentos
	 */
	private void recalculaTabelaOrcamentaria(Collection<OrcamentoDetalhado> orcamentos) {
		tabelaOrcamentaria.clear();

		for (OrcamentoDetalhado orca : orcamentos) {
			ResumoOrcamentoDetalhado resumo = tabelaOrcamentaria.get(orca
					.getElementoDespesa());
			if (resumo == null) {
				resumo = new ResumoOrcamentoDetalhado();
			}
			resumo.getOrcamentos().add(orca);
			tabelaOrcamentaria.put(orca.getElementoDespesa(), resumo);
		}
	}

	/**
	 * Remove um item de orçamento do projeto.
	 * @return
	 * @throws DAOException
	 */
	public String removeOrcamento() throws DAOException {
		List<OrcamentoDetalhado> orcamentos = obj.getOrcamentosDetalhados();
		OrcamentoDetalhado orcamentoLinha = null;
		for (int i = 0; i < orcamentos.size(); i++) {
			orcamentoLinha = orcamentos.get(i);
			if ( ((orcamentoLinha.getElementoDespesa() != null && orcamentoRemocao.getElementoDespesa() != null
					&& orcamentoLinha.getElementoDespesa().getId() == orcamentoRemocao.getElementoDespesa().getId()) ||
					orcamentoLinha.getId() == orcamentoRemocao.getId() ) 
					&& orcamentoLinha.getQuantidade() == orcamentoRemocao.getQuantidade() ){
				orcamentoLinha = orcamentos.remove(i);
				break;
			}
		}
		if ( orcamentoLinha != null && orcamentoLinha.getId() > 0 )
			getGenericDAO().remove(orcamentoLinha);
		obj.setOrcamentosDetalhados(orcamentos);
		orcamentoRemocao = new OrcamentoDetalhado();
		recalculaTabelaOrcamentaria(obj.getOrcamentosDetalhados());

		return redirectMesmaPagina();
	}

	public OrcamentoDetalhado getOrcamento() {
		return orcamento;
	}

	public void setOrcamento(OrcamentoDetalhado orcamento) {
		this.orcamento = orcamento;
	}

	public OrcamentoDetalhado getOrcamentoRemocao() {
		return orcamentoRemocao;
	}

	public void setOrcamentoRemocao(OrcamentoDetalhado orcamentoRemocao) {
		this.orcamentoRemocao = orcamentoRemocao;
	}

	public Map<ElementoDespesa, ResumoOrcamentoDetalhado> getTabelaOrcamentaria() {
		return tabelaOrcamentaria;
	}

	public void setTabelaOrcamentaria(
			Map<ElementoDespesa, ResumoOrcamentoDetalhado> tabelaOrcamentaria) {
		this.tabelaOrcamentaria = tabelaOrcamentaria;
	}

	public TelaCronograma getTelaCronograma() {
		return telaCronograma;
	}

	public void setTelaCronograma(TelaCronograma telaCronograma) {
		this.telaCronograma = telaCronograma;
	}

	public Collection<ProjetoApoioNovosPesquisadores> getProjetosApoio() {
		return projetosApoio;
	}

	public void setProjetosApoio(
			Collection<ProjetoApoioNovosPesquisadores> projetosApoio) {
		this.projetosApoio = projetosApoio;
	}

	/**
	 * Retorna uma coleção de itens selecionáveis com os editais possíveis.
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getEditaisCombo() throws ArqException{
		if(isPesquisa() && isUserInRole(SigaaPapeis.GESTOR_PESQUISA))
			return toSelectItems(getDAO(EditalPesquisaDao.class).findAllApoioFinanceiro(), "id", "edital.descricao");
		return toSelectItems(getDAO(EditalPesquisaDao.class).findAllAbertosTipo(false), "id", "edital.descricao");
	}
}