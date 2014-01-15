/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.caixa_postal.Mensagem;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.graduacao.AgregadorBolsasDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.arq.dao.projetos.OportunidadeBolsaAssociadaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.AgregadorBolsas;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoAgregadorBolsas;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.jsf.AtividadeExtensaoMBean;
import br.ufrn.sigaa.extensao.jsf.ConstantesNavegacao;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecao;
import br.ufrn.sigaa.monitoria.jsf.ProjetoMonitoriaMBean;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.jsf.ProjetoBaseMBean;

/**
 * Essa classe agrega todos os tipo de bolsas, fazendo busca em monitoria
 * extensão, pesquisa e apoio técnico
 * 
 * @author Henrique André
 *
 */
@Component("agregadorBolsas")
@Scope("session")
public class AgregadorBolsasMBean extends SigaaAbstractController {

	/** Constante que representa Monitoria. */
	public static final int MONITORIA		 = 1;
	
	/** Constante que representa Extensao. */
	public static final int EXTENSAO		 = 2;
	
	/** Constante que representa Pesquisa. */
	public static final int PESQUISA		 = 3;
	
	/** Constante que representa Apoio tecnico. */
	public static final int APOIO_TECNICO	 = 4;
	
	/** Contante que representa Acoes Associadas. */
	public static final int ACOES_ASSOCIADAS = 5;
	
	/** Parametros da busca. */
	private ParametroBuscaAgregadorBolsas parametros = new ParametroBuscaAgregadorBolsas();
	
	/** Restricoes da busca. */
	private RestricoesBuscaAgregadorBolsas restricoes = new RestricoesBuscaAgregadorBolsas();
	
	/** Lista de resultados da busca. */
	private List<AgregadorBolsas> resultado;

	/** Mensagem que sera enviada. */ 
	private Mensagem mensagem;
	
	/** Usuario que sera o destinatario da mensagem. */
	private Usuario destinarioMensagem;
	
	/** Colunas que vao aparecer na listagem resultante da busca. */
	private Colunas colunas = new Colunas();
	
	/** Lista de erros referentes a tela de busca.*/
	Collection<MensagemAviso> erros;
	
	/** Hash que guarda os tipos de bolsas (Pesquisa, Extesao, Monitoria, Apoio Tecnico, Acao Associada ...) junto a sua descricao. */
	Map<Integer, String> tipos = new HashMap<Integer, String>();
	
	/** Link para a tela de busca. */
	public final String TELA_BUSCA = "/graduacao/agregador_bolsa/busca.jsp";
	
	/** AgregadorBolsa para apoio tecnico. */
	private AgregadorBolsas apoioTecnico;
	
	/**
	 * Iniciar procura por bolsa
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war\graduacao\agregador_bolsa\acompanhamento_interesse\lista.jsp</li>
	 * 		<li>sigaa.war\graduacao\agregador_bolsa\inscricao_oportunidade.jsp</li>
	 * 		<li>sigaa.war\portais\discente\include\bolsas.jsp</li>
	 * 		<li>sigaa.war\portais\discente\include\monitoria.jsp</li>
	 * 		<li>sigaa.war\portais\discente\medio\menu_discente_medio.jsp</li>
	 * 		<li>sigaa.war\portais\discente\menu_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarBuscar() {

		parametros = new ParametroBuscaAgregadorBolsas();
		parametros.setDisciplina(new ComponenteCurricular());
		parametros.setServidor(new Servidor());
		parametros.getServidor().setPessoa(new Pessoa());
		parametros.setAno(CalendarUtils.getAnoAtual());
		
		if (getDiscenteUsuario().isTecnico()) {
			preparaComboTecnico();
		} else if (getDiscenteUsuario().isGraduacao()) {
			preparaComboGraduacao();
		} else if ( getDiscenteUsuario().isMedio() ) {
			preparaComboMedio();
			parametros.setDepartamento( getDiscenteUsuario().getGestoraAcademica().getId() );
			restricoes.setBuscaDepartamento(true);
			parametros.setNivelEnsino( getDiscenteUsuario().getNivel() );
		}else if (getDiscenteUsuario().isStricto()){
			preparaComboStricto();
		}else if (getDiscenteUsuario().isLato()){
			preparaComboLato();
		}
		
		clear();
		return telaBusca();
	}
	
	/** Retorna o link para o formulário de busca de bolsas.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war\monitoria\DiscenteMonitoria\inscricao_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaBusca() {
		return forward(TELA_BUSCA);
	}
	
	/**
	 * Prepara combo com os tipo válidos de busca para o nível graduação
	 */
	private void preparaComboGraduacao() {
		tipos = new HashMap<Integer, String>();
		
		tipos.put(MONITORIA, "MONITORIA");
		tipos.put(EXTENSAO, "EXTENSÃO");
		tipos.put(PESQUISA, "PESQUISA");
		tipos.put(APOIO_TECNICO, "APOIO TÉCNICO");
		tipos.put(ACOES_ASSOCIADAS, "AÇÕES ASSOCIADAS");
		
	}

	/**
	 * Prepara combo com os tipo válidos de busca para o nível técnico
	 */
	private void preparaComboTecnico() {
		tipos = new HashMap<Integer, String>();
		tipos.put(APOIO_TECNICO, "APOIO TÉCNICO");
		tipos.put(EXTENSAO, "EXTENSÃO");
		tipos.put(PESQUISA, "PESQUISA");
	}

	/**
	 * Prepara combo com os tipo válidos de busca para o nível Médio
	 */
	private void preparaComboMedio() {
		tipos = new HashMap<Integer, String>();
		tipos.put(PESQUISA, "PESQUISA");
	}

	
	/**
	 * Prepara combo com os tipo válidos de busca para o nível stricto
	 */
	private void preparaComboStricto() {
		tipos = new HashMap<Integer, String>();
		
		tipos.put(MONITORIA, "MONITORIA");
		tipos.put(EXTENSAO, "EXTENSÃO");
		tipos.put(PESQUISA, "PESQUISA");
		tipos.put(APOIO_TECNICO, "APOIO TÉCNICO");
		tipos.put(ACOES_ASSOCIADAS, "AÇÕES ASSOCIADAS");
		
	}

	/**
	 * Prepara combo com os tipo válidos de busca para o nível lato
	 */
	private void preparaComboLato() {
		tipos = new HashMap<Integer, String>();
		
		tipos.put(MONITORIA, "MONITORIA");
		tipos.put(EXTENSAO, "EXTENSÃO");
		tipos.put(PESQUISA, "PESQUISA");
		tipos.put(APOIO_TECNICO, "APOIO TÉCNICO");
		tipos.put(ACOES_ASSOCIADAS, "AÇÕES ASSOCIADAS");
		
	}
	
	/**
	 * 
	 * Limpa as buscas
	 */
	private void clear() {
		destinarioMensagem = null;
		resultado = null;		
	}

	/**
	 * Monta o combo com as opções de busca
	 * @return
	 */
	public Collection<SelectItem> getAllTipoBolsas() {
		return toSelectItems(tipos);
	}

	/**
	 * Verifica o preenchimento de campos obrigatórios. 
	 * @param lista
	 */
	private void verificaCamposObrigatorios(ListaMensagens lista) {
		switch (parametros.getTipo()) {
		case EXTENSAO:
			verificaCamposObrigatoriosExtensao(lista);
			break;		
		default:{}		
	
		}
	}
	
	/**
	 * Método de busca
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war\graduacao\agregador_bolsa\busca.jsp</li>
	 * </ul>
	 * @param eventtipos.
	 * @throws DAOException
	 */
	@Override
	public String buscar() throws DAOException {
		clear();		
		
		ListaMensagens erros = new ListaMensagens();
		
		verificaPermissaoDeBusca(erros);		
		verificaCamposObrigatorios(erros);		
		
		if( !erros.isEmpty() ) {
			addMensagens(erros);
			return null;
		}
		
		
		switch (parametros.getTipo()) {
			case MONITORIA:
				colunas = colunas.getMonitoria();
				buscaMonitoria();
				break;
			case EXTENSAO:
				colunas = colunas.getExtensao();				
				buscaExtensao();
				break;
			case PESQUISA:
				colunas = colunas.getPesquisa();
				buscaPesquisa();
				break;
			case APOIO_TECNICO:
				colunas = colunas.getApoioTecnico();
				buscaApoioTecnico();
				break;
			case ACOES_ASSOCIADAS:
				colunas = colunas.getAssociadas();
				buscarAssociadas();
				break;
			default:
				addMensagemErro("Selecione um dos tipos de bolsa disponíveis");
				break;
		}
		
		if (isEmpty(resultado)) {
			addMensagemWarning("Nenhuma oportunidade foi encontrada de acordo com os critérios de busca informados.");
		}
		
		return forward(TELA_BUSCA);
	}

	/**
	 * Verifica se o usuário tem permissão para realizar a busca no tipo especificado de bolsa
	 * 
	 * @param erros
	 * @return
	 */
	private ListaMensagens verificaPermissaoDeBusca(ListaMensagens erros)  {
		
//		if (!tipos.containsKey(parametros.getTipo())) 
//			ValidatorUtil.addMensagemErro("Opção de busca inválido.", erros);
		
		if (!tipos.containsKey(parametros.getTipo())) 
			erros.addMensagem(new MensagemAviso("Tipo de Bolsa: Campo obrigatório nao informado.", TipoMensagemUFRN.ERROR));
		
		return erros;	
	}

	/**
	 * Procura por vagas cadastradas no sipac
	 * @throws DAOException 
	 */
	private void buscaApoioTecnico() throws DAOException {
		AgregadorBolsasDao dao = getDAO(AgregadorBolsasDao.class);
		
		parametros.nivelEnsino = getDiscenteUsuario().getNivel();
		
		if (restricoes.isBuscaVoltadasAoCurso())
			parametros.voltadasAoCurso = getDiscenteUsuario().getCurso().getId();
		
		resultado = dao.findBolsasSIPAC(restricoes, parametros);
		
		if (resultado == null || resultado.isEmpty()) {
			return;
		}		
		
		popularUsuario(resultado);
	}

	/**
	 * Realiza a busca em pesquisa
	 * 
	 * @throws DAOException
	 */
	private void buscaPesquisa() throws DAOException {
		PlanoTrabalhoDao dao = getDAO(PlanoTrabalhoDao.class);
		resultado = dao.findPlanoSemBolsista(restricoes, parametros);
		//popularUsuario(resultado);
	}

	/**
	 * Realiza a busca em extensão
	 * 
	 * @throws DAOException
	 */
	private void buscaExtensao() throws DAOException {		
		restricoes.buscaAno = true;
		AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);
		List<AtividadeExtensao> resultadoExtensao = dao.findBolsasDisponiveis(restricoes, parametros);		
		resultado = AgregadorBolsas.toList(resultadoExtensao);
		
		if (resultado == null || resultado.isEmpty()) {
			addMensagemErro("Nenhum registro localizado.");
			return;
		}
		
		//Preenche o ResponsavelProjeto com o servidor do MembroProjeto que e coordenador.   
		int i = 0;
		for(AgregadorBolsas ab:resultado) {
			AtividadeExtensao ae = resultadoExtensao.get(i);
			for (MembroProjeto mp: ae.getProjeto().getEquipe() ) {
				if (mp.isCoordenador()) {
					ab.setResponsavelProjeto(mp.getServidor());
					break;
				}
			}
			i++;
		}
		popularUsuario(resultado);	
	}
	
	/**
	 * Realiza a busca em associadas
	 * 
	 * @throws DAOException
	 */
	private void buscarAssociadas() throws DAOException {		
		restricoes.buscaAno = true;
		OportunidadeBolsaAssociadaDao dao = getDAO(OportunidadeBolsaAssociadaDao.class);
		List<Projeto> resultadoAssociadas = dao.findBolsasDisponiveis(restricoes, parametros);		
		resultado = AgregadorBolsas.toList(resultadoAssociadas);
		
		if (resultado == null || resultado.isEmpty()) {
			return;
		}
		
		popularUsuario(resultado);
	}
	
	/**
	 * Verifica campos de preenchimento obrigatório em extensão.
	 * 
	 * @param lista
	 */
	private void verificaCamposObrigatoriosExtensao(ListaMensagens lista) {
		if(parametros.ano == null)
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
	}

	/**
	 * Realiza a busca em monitoria
	 * 
	 * @throws DAOException
	 */
	private void buscaMonitoria() throws DAOException {
		
		if (restricoes.buscaDisciplina)
			parametros.disciplina = getGenericDAO().refresh(parametros.getDisciplina());
		
		ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);
		
		List<ProvaSelecao> resultadoMonitoria = dao.findSelecao(restricoes, parametros, new Date());
		
		resultado = AgregadorBolsas.toList(resultadoMonitoria);
		popularUsuario(resultado);
	}

	/**
	 * Busca o Usuário responsável pelo projeto
	 * 
	 * @param res
	 * @throws DAOException
	 */
	private void popularUsuario(List<AgregadorBolsas> res) throws DAOException {
		UsuarioDao dao = getDAO(UsuarioDao.class);
		for (AgregadorBolsas ab : res) {
			if (ab.getIdUsuario() != null) 
				ab.setUsuario(dao.findByPrimaryKey(ab.getIdUsuario()));
			else if (ab.getResponsavelProjeto() != null) {
				ab.setUsuario(dao.findByServidorLeve(ab.getResponsavelProjeto()));
			}
		}
	}	
	
	/**
	 * Mostra os detalhes de uma tipo de bolsa selecionada
	 * verDetalhesProjeto
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>\sigaa.war\graduacao\agregador_bolsa\busca.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String verDetalhesProjeto() throws DAOException {
		Integer id = getParameterInt("id");
		switch (parametros.getTipo()) {
		case MONITORIA:
			ProjetoMonitoriaMBean projetoMonitoria = getMBean("projetoMonitoria");
			getCurrentRequest().setAttribute("id", id);
			return projetoMonitoria.view();
		case EXTENSAO:
			AtividadeExtensaoMBean atividadeExtensao = getMBean("atividadeExtensao");
			return atividadeExtensao.view(id);
		case PESQUISA:
			redirect("/pesquisa/planoTrabalho/wizard.do?dispatch=view&obj.id=" + id);
			return null;
		case APOIO_TECNICO:
			break;
		case ACOES_ASSOCIADAS:
			ProjetoBaseMBean projeto = getMBean("projetoBase");
			return projeto.view();
		default:
			addMensagemErro("Não foi possível localizar detalhes do projeto.");
			return null;
		}
		return null;
	}
	
	/**
	 * Redireciona para a tela de detalhes da bolsa de apoio tecnico.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war\graduacao\agregador_bolsa\acompanhamento_interesse\lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public String viewApoioTecnico() {
		
		Integer id = getParameterInt("id");
		AgregadorBolsasDao dao = getDAO(AgregadorBolsasDao.class);
		apoioTecnico = dao.findApoioTecnicoById(id);
		
		if (apoioTecnico == null) {
			addMensagemErro("Registro não encontrado.");
			return null;
		}
		
		return forward("/graduacao/agregador_bolsa/view.jsp");
	}
	
	/**
	 * Inicia o processo de envio de mensagem.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war\graduacao\agregador_bolsa\acompanhamento_interesse\lista.jsp</li>
	 * 		<li>\sigaa.war\graduacao\agregador_bolsa\busca.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarEnviarResponsavel() throws ArqException {
		
		pegarDestinatario();
		
		if (destinarioMensagem == null) {
			addMensagemErro("Não foi possível localizar o coordenador.");
			return null;
		}
		
		criarMensagem();
		
		return forward(ConstantesNavegacao.CONTATO_COORDENADOR_FORM);
	}
	
	/**
	 * Metodo que volta para a tela de listagem.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war\extensao\Atividade\form_contato.jsp</li>
	 * </ul>
	 * @return
	 */
	public String voltarListagem(){
		return forward("/graduacao/agregador_bolsa/busca.jsp");
	}


	
	/**
	 * Cria a mensagem a ser enviada
	 */
	private void criarMensagem() {
		mensagem = new Mensagem();
		mensagem.setAutomatica(false);
		mensagem.setUsuarioDestino(destinarioMensagem);
		mensagem.setRemetente(getUsuarioLogado());
		mensagem.setLeituraObrigatoria(true);
		mensagem.setUsuario(getUsuarioLogado());
	}

	/**
	 * Pega o id de usuário passado por parâmetro
	 * 
	 * @throws ArqException
	 * @throws DAOException
	 */
	private void pegarDestinatario() throws ArqException, DAOException {
		Integer idUsuario = getParameterInt("idUsuario");
		
		prepareMovimento(SigaaListaComando.ENVIAR_MENSAGEM_COORDENAOR);
		UsuarioDao dao = getDAO(UsuarioDao.class);
		destinarioMensagem = dao.findByPrimaryKey(idUsuario);

	}
	
	/**
	 * Envia mensagem ao servidor
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/extensao/Atividade/form_contato.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String enviarMensagem() throws ArqException {
		
		if(mensagem.getMensagem().isEmpty()){
			addMensagemErro("Mensagem: campo obrigatório não informado.");
			return null;
		}
		MovimentoAgregadorBolsas mov = new MovimentoAgregadorBolsas();
		mov.setCodMovimento(SigaaListaComando.ENVIAR_MENSAGEM_COORDENAOR);
		mov.setMensagem(mensagem);
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
			addMensagemInformation("Mensagem enviada ao responsável.");
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
		}
		
		return forward("/graduacao/agregador_bolsa/busca.jsp");
	}	

	/**
	 * Monta o combo de UnidadesSipac.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/graduacao/agregador_bolsa/busca.jsp</li>
	 * </ul> 
	 * @return
	 */
	public List<SelectItem> getUnidadesSipacCombo() {
		AgregadorBolsasDao dao = getDAO(AgregadorBolsasDao.class);
		List<Unidade> unidades = dao.findUnidadesAnunciantes();
		
		return toSelectItems(unidades, "id", "nome");
		
	}
	
	public ParametroBuscaAgregadorBolsas getParametros() {
		return parametros;
	}

	public void setParametros(ParametroBuscaAgregadorBolsas parametros) {
		this.parametros = parametros;
	}

	public RestricoesBuscaAgregadorBolsas getRestricoes() {
		return restricoes;
	}

	public Colunas getColunas() {
		return colunas;
	}

	public void setColunas(Colunas colunas) {
		this.colunas = colunas;
	}

	public void setRestricoes(RestricoesBuscaAgregadorBolsas restricoes) {
		this.restricoes = restricoes;
	}

	public Mensagem getMensagem() {
		return mensagem;
	}

	public void setMensagem(Mensagem mensagem) {
		this.mensagem = mensagem;
	}

	public Usuario getDestinarioMensagem() {
		return destinarioMensagem;
	}

	public void setDestinarioMensagem(Usuario destinarioMensagem) {
		this.destinarioMensagem = destinarioMensagem;
	}	

	public List<AgregadorBolsas> getResultado() {
		return resultado;
	}

	public void setResultado(List<AgregadorBolsas> resultado) {
		this.resultado = resultado;
	}

	public AgregadorBolsas getApoioTecnico() {
		return apoioTecnico;
	}

	public void setApoioTecnico(AgregadorBolsas apoioTecnico) {
		this.apoioTecnico = apoioTecnico;
	}	

	/** Informa se deve ser habilitado o combo para a seleção do departamento por parte do discente */
	public boolean isExibeComboDepartamento(){
		return isEmpty( parametros.getNivelEnsino() ) || parametros.getNivelEnsino() != NivelEnsino.MEDIO;
	}
	
	/**Classe que informa quais colunas devem aparece na listagem resutante da busca de acordo com o tipo de bolsa.*/
	public class Colunas {
		
		/** Informa que a coluna descricao deve aparecer na listagem. */
		private boolean descricao;
		
		/** Informa que a coluna vagasRemuneradas deve aparecer na listagem. */
		private boolean vagasRemuneradas;
		
		/** Informa que a coluna vagasNaoRemuneradas deve aparecer na listagem. */
		private boolean vagasNaoRemuneradas;
		
		/** Informa que a coluna responsavelProjeto deve aparecer na listagem. */
		private boolean responsavelProjeto;
		
		/** Informa que a coluna unidades deve aparecer na listagem. */
		private boolean unidade;
		
		/** Informa que a coluna tipoBolsa deve aparecer na listagem. */
		private boolean tipoBolsa;
				
		/** Informa que a coluna bolsaValor deve aparecer na listagem. */
		private boolean bolsaValor;
		
		/** Informa que a coluna emailContato deve aparecer na listagem. */
		private boolean emailContato;
		
		/** Informa que a coluna cursosAlvo deve aparecer na listagem. */
		private boolean cursosAlvo;
		
		/** Informa se o resultado sera agurpado por responsavel. */
		private boolean agruparPorResponsavel;
		
		/**
		 * Retorna as colunas que serão exibidas no resultado de monitoria
		 * 
		 * @return
		 */
		public Colunas getMonitoria() {
			Colunas colunas = new Colunas();
			colunas.setDescricao(true);
			colunas.setVagasNaoRemuneradas(true);
			colunas.setVagasRemuneradas(true);
			colunas.setResponsavelProjeto(true);
			return colunas; 
		}
		
		/**
		 * Retorna as colunas que serão exibidas no resultado de apoio técnico
		 * 
		 * @return
		 */
		public Colunas getApoioTecnico() {
			Colunas colunas = new Colunas();
			colunas.setDescricao(true);
			colunas.setEmailContato(true);
			colunas.setCursosAlvo(true);
			colunas.setUnidade(true);
			return colunas;
		}

		/**
		 * Retorna as colunas que serão exibidas no resultado de extensão
		 * 
		 * @return
		 */
		public Colunas getExtensao() {
			Colunas colunas = new Colunas();
			colunas.setDescricao(true);
			colunas.setResponsavelProjeto(true);
			colunas.setUnidade(true);
			colunas.setVagasRemuneradas(true);
			return colunas;
		}

		/**
		 * Retorna as colunas que serão exibidas no resultado de pesquisa
		 * 
		 * @return
		 */
		public Colunas getPesquisa() {
			Colunas colunas = new Colunas();
			colunas.setDescricao(true);
			colunas.setResponsavelProjeto(true);
			colunas.setUnidade(true);
			colunas.setAgruparPorResponsavel(true);
			return colunas;
		}
		
		/**
		 * Retorna as colunas que serão exibidas no resultado de ações associadas
		 * 
		 * @return
		 */
		public Colunas getAssociadas() {
			Colunas colunas = new Colunas();
			colunas.setDescricao(true);
			colunas.setResponsavelProjeto(true);
			colunas.setUnidade(true);
			colunas.setVagasRemuneradas(true);
			return colunas;
		}
		
		public boolean isDescricao() {
			return descricao;
		}

		public void setDescricao(boolean descricao) {
			this.descricao = descricao;
		}

		public boolean isVagasRemuneradas() {
			return vagasRemuneradas;
		}

		public void setVagasRemuneradas(boolean vagasRemuneradas) {
			this.vagasRemuneradas = vagasRemuneradas;
		}

		public boolean isVagasNaoRemuneradas() {
			return vagasNaoRemuneradas;
		}

		public void setVagasNaoRemuneradas(boolean vagasNaoRemuneradas) {
			this.vagasNaoRemuneradas = vagasNaoRemuneradas;
		}

		public boolean isResponsavelProjeto() {
			return responsavelProjeto;
		}

		public void setResponsavelProjeto(boolean responsavelProjeto) {
			this.responsavelProjeto = responsavelProjeto;
		}

		public boolean isUnidade() {
			return unidade;
		}

		public void setUnidade(boolean unidade) {
			this.unidade = unidade;
		}

		public boolean isTipoBolsa() {
			return tipoBolsa;
		}

		public void setTipoBolsa(boolean tipoBolsa) {
			this.tipoBolsa = tipoBolsa;
		}

		public boolean isBolsaValor() {
			return bolsaValor;
		}

		public void setBolsaValor(boolean bolsaValor) {
			this.bolsaValor = bolsaValor;
		}

		public boolean isEmailContato() {
			return emailContato;
		}

		public void setEmailContato(boolean emailContato) {
			this.emailContato = emailContato;
		}

		public boolean isCursosAlvo() {
			return cursosAlvo;
		}

		public void setCursosAlvo(boolean cursosAlvo) {
			this.cursosAlvo = cursosAlvo;
		}

		public boolean isAgruparPorResponsavel() {
			return agruparPorResponsavel;
		}

		public void setAgruparPorResponsavel(boolean agruparPorResponsavel) {
			this.agruparPorResponsavel = agruparPorResponsavel;
		}
		
	}
	
	/**
	 * Classe que armazena os valores das informacoes inseridas em tela de busca. 
	 */
	public class ParametroBuscaAgregadorBolsas {
		
		/** Utilizado para armazenar informacao inserida em tela de busca. */
		private Integer tipo;
		
		/** Utilizado para armazenar informacao inserida em tela de busca. */
		private Integer departamento;
		
		/** Utilizado para armazenar informacao inserida em tela de busca. */
		private Integer centro;
		
		/** Utilizado para armazenar informacao inserida em tela de busca. */
		private Integer areaConhecimentoCnpq;
		
		/** Utilizado para armazenar informacao inserida em tela de busca. */
		private String palavraChave;
		
		/** Utilizado para armazenar informacao inserida em tela de busca. */
		private Servidor servidor;
		
		/** Utilizado para armazenar informacao inserida em tela de busca. */
		private ComponenteCurricular disciplina;
		
		/** Utilizado para armazenar informacao inserida em tela de busca. */
		private Integer tipoAtividade;
		
		/** Utilizado para armazenar informacao inserida em tela de busca. */
		private Integer ano;
		
		/** Utilizado para armazenar informacao inserida em tela de busca. */
		private Integer unidadeAdm;
		
		/** Utilizado para armazenar informacao inserida em tela de busca. */
		private Integer voltadasAoCurso;
		
		/** Utilizado para armazenar informacao inserida em tela de busca. */
		private Character nivelEnsino;
		
		public Integer getAno() {
			return ano;
		}

		public void setAno(Integer ano) {
			this.ano = ano;
		}
		
		public Integer getTipo() {
			return tipo;
		}

		public void setTipo(Integer tipo) {
			this.tipo = tipo;
		}

		public Integer getDepartamento() {
			return departamento;
		}

		public void setDepartamento(Integer departamento) {
			this.departamento = departamento;
		}

		public Integer getCentro() {
			return centro;
		}

		public void setCentro(Integer centro) {
			this.centro = centro;
		}

		public Integer getAreaConhecimentoCnpq() {
			return areaConhecimentoCnpq;
		}

		public void setAreaConhecimentoCnpq(Integer areaConhecimentoCnpq) {
			this.areaConhecimentoCnpq = areaConhecimentoCnpq;
		}

		public String getPalavraChave() {
			return palavraChave;
		}

		public void setPalavraChave(String palavraChave) {
			this.palavraChave = palavraChave;
		}

		public Servidor getServidor() {
			return servidor;
		}

		public void setServidor(Servidor servidor) {
			this.servidor = servidor;
		}

		public ComponenteCurricular getDisciplina() {
			return disciplina;
		}

		public void setDisciplina(ComponenteCurricular disciplina) {
			this.disciplina = disciplina;
		}

		public Integer getTipoAtividade() {
			return tipoAtividade;
		}

		public void setTipoAtividade(Integer tipoAtividade) {
			this.tipoAtividade = tipoAtividade;
		}

		public Integer getUnidadeAdm() {
			return unidadeAdm;
		}

		public void setUnidadeAdm(Integer unidadeAdm) {
			this.unidadeAdm = unidadeAdm;
		}

		public Integer getVoltadasAoCurso() {
			return voltadasAoCurso;
		}

		public void setVoltadasAoCurso(Integer voltadasAoCurso) {
			this.voltadasAoCurso = voltadasAoCurso;
		}

		public Character getNivelEnsino() {
			return nivelEnsino;
		}

		public void setNivelEnsino(Character nivelEnsino) {
			this.nivelEnsino = nivelEnsino;
		}

	}
	
	/** Classe das restricoes para a busca de no agregador bolsa. */
	public class RestricoesBuscaAgregadorBolsas {
		
		/** Utilizado para informar se o usuario deseja efetuar uma busca utilizando como opcao determinado criterio. */
		private boolean buscaTipo;
		
		/** Utilizado para informar se o usuario deseja efetuar uma busca utilizando como opcao determinado criterio. */
		private boolean buscaDepartamento;
		
		/** Utilizado para informar se o usuario deseja efetuar uma busca utilizando como opcao determinado criterio. */
		private boolean buscaCentro;
		
		/** Utilizado para informar se o usuario deseja efetuar uma busca utilizando como opcao determinado criterio. */
		private boolean buscaAreaConhecimentoCnpq;
		
		/** Utilizado para informar se o usuario deseja efetuar uma busca utilizando como opcao determinado criterio. */
		private boolean buscaPalavraChave;
		
		/** Utilizado para informar se o usuario deseja efetuar uma busca utilizando como opcao determinado criterio. */
		private boolean buscaServidor;
		
		/** Utilizado para informar se o usuario deseja efetuar uma busca utilizando como opcao determinado criterio. */
		private boolean buscaDisciplina;
		
		/** Utilizado para informar se o usuario deseja efetuar uma busca utilizando como opcao determinado criterio. */
		private boolean buscaTipoAtividade;
		
		/** Utilizado para informar se o usuario deseja efetuar uma busca utilizando como opcao determinado criterio. */
		private boolean buscaUnidadeAdm;
		
		/** Utilizado para informar se o usuario deseja efetuar uma busca utilizando como opcao determinado criterio. */
		private boolean buscaVoltadasAoCurso;
		
		/** Utilizado para informar se o usuario deseja efetuar uma busca utilizando como opcao determinado criterio. */
		private boolean buscaAno;
		
		public boolean isBuscaTipo() {
			return buscaTipo;
		}

		public void setBuscaTipo(boolean buscaTipo) {
			this.buscaTipo = buscaTipo;
		}

		public boolean isBuscaDepartamento() {
			return buscaDepartamento;
		}

		public void setBuscaDepartamento(boolean buscaDepartamento) {
			this.buscaDepartamento = buscaDepartamento;
		}

		public boolean isBuscaCentro() {
			return buscaCentro;
		}

		public void setBuscaCentro(boolean buscaCentro) {
			this.buscaCentro = buscaCentro;
		}

		public boolean isBuscaAreaConhecimentoCnpq() {
			return buscaAreaConhecimentoCnpq;
		}

		public void setBuscaAreaConhecimentoCnpq(boolean buscaAreaConhecimentoCnpq) {
			this.buscaAreaConhecimentoCnpq = buscaAreaConhecimentoCnpq;
		}

		public boolean isBuscaPalavraChave() {
			return buscaPalavraChave;
		}

		public void setBuscaPalavraChave(boolean buscaPalavraChave) {
			this.buscaPalavraChave = buscaPalavraChave;
		}

		public boolean isBuscaServidor() {
			return buscaServidor;
		}

		public void setBuscaServidor(boolean buscaServidor) {
			this.buscaServidor = buscaServidor;
		}

		public boolean isBuscaDisciplina() {
			return buscaDisciplina;
		}

		public void setBuscaDisciplina(boolean buscaDisciplina) {
			this.buscaDisciplina = buscaDisciplina;
		}

		public boolean isBuscaTipoAtividade() {
			return buscaTipoAtividade;
		}

		public void setBuscaTipoAtividade(boolean buscaTipoAtividade) {
			this.buscaTipoAtividade = buscaTipoAtividade;
		}

		public boolean isBuscaUnidadeAdm() {
			return buscaUnidadeAdm;
		}

		public void setBuscaUnidadeAdm(boolean buscaUnidadeAdm) {
			this.buscaUnidadeAdm = buscaUnidadeAdm;
		}

		public boolean isBuscaVoltadasAoCurso() {
			return buscaVoltadasAoCurso;
		}

		public void setBuscaVoltadasAoCurso(boolean buscaVoltadasAoCurso) {
			this.buscaVoltadasAoCurso = buscaVoltadasAoCurso;
		}

		public boolean isBuscaAno() {
			return buscaAno;
		}

		public void setBuscaAno(boolean buscaAno) {
			this.buscaAno = buscaAno;
		}
		
		
		
	}

}
