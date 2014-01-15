/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/13 - 12:12:49
 */
package br.ufrn.sigaa.ead.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isAllNotEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;
import static br.ufrn.sigaa.ead.dominio.MetodoAvaliacao.DUAS_PROVAS_RECUPERACAO;
import static br.ufrn.sigaa.ead.dominio.MetodoAvaliacao.UMA_PROVA;
import static br.ufrn.sigaa.ead.dominio.MetodoAvaliacao.getDescricao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ead.EADDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dao.MetodologiacaAvaliacaoEadDao;
import br.ufrn.sigaa.ead.dominio.ItemAvaliacaoEad;
import br.ufrn.sigaa.ead.dominio.MetodologiaAvaliacao;

/**
 * Managed Bean para cadastro de metodologia de avaliação
 *
 * @author David Pereira
 *
 */
@Component("metodologiaAvaliacaoEad") @Scope("session")
public class MetodologiaAvaliacaoMBean extends SigaaAbstractController<MetodologiaAvaliacao> {

	/** Armazena a lista de itens de avaliação cadastrados. */
	private List<ItemAvaliacaoEad> lista;
	
	/** Armazena a lista de itens da avaliação removidos*/
	private List<ItemAvaliacaoEad> remover;

	/** Armazena a lista de metodologias */
	private List<MetodologiaAvaliacao> metodologias = null; 

	/** Curso selecionado */
	private Curso cursoSelecionado = new Curso();
	
	public MetodologiaAvaliacaoMBean() {
		clear();
	}

	/** Armazena a operação de volta */
	public String operacaoVoltar;
	
	
	/**
	 * Limpa os dados do MBean.
	 */
	private void clear() {
		this.obj = new MetodologiaAvaliacao();
		this.obj.setCurso(new Curso());
		this.obj.setItens(new ArrayList<ItemAvaliacaoEad>());
		this.obj.setAtiva(true);
		this.lista = new ArrayList<ItemAvaliacaoEad>();
		this.remover = new ArrayList<ItemAvaliacaoEad>();
	}

	/**
	 * Retorna uma lista de {@link SelectItem} referentes aos cursos oferecidos a distância.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getCursos() throws DAOException {
		return toSelectItems(getDAO(CursoDao.class).findAllCursosADistancia(), "id", "nome");
	}

	/**
	 * Retorna uma lista de {@link SelectItem} com os métodos de avaliação possíveis.
	 * 
	 * @return
	 */
	public List<SelectItem> getMetodosAvaliacao() {
		List<SelectItem> itens = new ArrayList<SelectItem>();
		itens.add(new SelectItem("0", "-- SELECIONE --"));
		itens.add(new SelectItem(String.valueOf(UMA_PROVA), getDescricao(UMA_PROVA)));
		itens.add(new SelectItem(String.valueOf(DUAS_PROVAS_RECUPERACAO), getDescricao(DUAS_PROVAS_RECUPERACAO)));
		return itens;
	}
	
	@Override
	protected void afterCadastrar() throws ArqException {
		GenericDAO dao = getGenericDAO();
		for (ItemAvaliacaoEad item : remover) {
			dao.remove(item);
		}
		
		clear();
		super.afterCadastrar();
	}

	/**
	 * Calcula o peso que o professor na metodologia de avaliação
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/MetodologiaAvaliacao/form.jsp</li>
	 * </ul>
	 * @param evt
	 */
	public void calcularPorcentagemProfessor(ActionEvent evt) {
		if (!obj.isPermiteTutor()) {
			obj.setPorcentagemProfessor(100);
			obj.setPorcentagemTutor(0);
		} else {
			int valor = obj.getPorcentagemTutor() == null ? 0 : obj.getPorcentagemTutor();
			
			if (valor > 100) {
				addMensagemErroAjax("Peso do Tutor: O valor não pode ultrapassar 100%");
				return ;
			}
				
			
			obj.setPorcentagemProfessor(100 - valor);
		}
	}
	
	/**
	 * Adiciona um novo item à metodologia.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/MetodologiaAvaliacao/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public void novoItem(ActionEvent evt) {
		ItemAvaliacaoEad item = new ItemAvaliacaoEad();
		item.setData(new Date());
		item.setUsuario(getUsuarioLogado());
		item.setMetodologia(obj);
		item.setAtivo(true);
		item.setNome("");

		this.obj.getItens().add(item);
	}

	/**
	 * Remoção de um item do Metodologia de Avaliação
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ead/MetodologiaAvaliacao/form.jsp</li>
	 * </ul>
	 * 
	 */
	public void removerItem(ActionEvent e){
		int id = getParameterInt("id");
		String nome = getParameter("nome");
		lista = new ArrayList<ItemAvaliacaoEad>();
		lista.addAll(obj.getItens());
		for (ItemAvaliacaoEad item : obj.getItens()) {
			if (item.getId() == id && id != 0) {
				obj.getItens().remove(item);
				break;
			}
			if (item.getNome().equals(nome)) {
				obj.getItens().remove(item);
				break;
			}
		}
	}

	@Override
	public Collection<MetodologiaAvaliacao> getAll() throws ArqException {
		checkRole(SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_TUTORIA_EAD);
		EADDao dao = getDAO(EADDao.class);
		return dao.findMetodologiasOrderByAtivos();
	}

	/***
	 * Carrega a metodologia para a sua visualuização.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/MetodologiaAvaliacao/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String visualizar() throws DAOException {
		int id = getParameterInt("id");
		obj = getGenericDAO().findByPrimaryKey(id, MetodologiaAvaliacao.class);
		return forward(getViewPage());
	}

	/**
	 * Utilizado para voltar a tela de listagem  
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/MetodologiaAvaliacao/form.jsp</li>
	 * </ul>
	 * 
	 * Serve para iniciliazar novamente o obj e direcionar para a tela de listagem.
	 * @return
	 */
	public String voltar() {
		
		if (isEmpty(operacaoVoltar)) {
			addMensagemErro("Não é possível voltar porque o sistema não conseguiu identificar o operação anterior.");
			return null;
		}
		
		return forward(NavegacaoVoltar.getByValor(Integer.valueOf(operacaoVoltar)).getUrl());
				
	}

	/**
	 * Carrega as informações padrões para a realização de um novo cadastro.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ead/menu.jsp</li>
	 * </ul>
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		clear();
		
		obj.setCurso(cursoSelecionado);
		
		sugestaoAnoPeriodoInicial();
		
		validarDuplicidade();
		
		if (hasErrors())
			return null;
		
		prepareMovimento(SigaaListaComando.CADASTRAR_METODOLOGIA_AVALIACAO);
		return super.preCadastrar();
	}
	
	/**
	 * Dá uma sugestão de Ano e período iniciais
	 * @throws DAOException
	 */
	private void sugestaoAnoPeriodoInicial() throws DAOException {
		
		MetodologiacaAvaliacaoEadDao dao = getDAO(MetodologiacaAvaliacaoEadDao.class);
		
		MetodologiaAvaliacao ultimaMetodologiaInativada = dao.ultimaMetodologiaInativada(obj.getCurso().getId());
		
		obj.setMetodologiaAnterior(ultimaMetodologiaInativada);
		
		if (isNotEmpty(ultimaMetodologiaInativada)) {
			if( ultimaMetodologiaInativada.getPeriodoFim() == 2 ){
				obj.setAnoInicio( ultimaMetodologiaInativada.getAnoFim() + 1 );
				obj.setPeriodoInicio(1);
			} else{
				obj.setAnoInicio(ultimaMetodologiaInativada.getAnoFim());
				obj.setPeriodoInicio(2);
			}
		}
		
	}

	/**
	 * Pré inativa uma metodologia de avaliação
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/MetodologiaAvaliacao/listaMetodologias.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String preInativar() throws ArqException {

		prepareMovimento(SigaaListaComando.INATIVAR_METODOLOGIA_AVALIACAO);
		
		int id = getParameterInt("id");
		setObj(getGenericDAO().findAndFetch(id, MetodologiaAvaliacao.class));
		
		return forward(getDirBase() + "/inativar.jsp");
	}
	
	/**
	 * Inativa a metodologia selecionada.  
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/MetodologiaAvaliacao/lista.jsp</li>
	 * </ul>
	 * 
	 * Serve para iniciliazar novamente o obj e direcionar para a tela de listagem.
	 * @return
	 */
	public String inativar() throws ArqException {
	    
		validarInativacao();
		
		if (hasErrors())
			return null;
		
		try {
		    MovimentoCadastro mov = new MovimentoCadastro(obj, SigaaListaComando.INATIVAR_METODOLOGIA_AVALIACAO);
		    execute(mov);
		    addMensagemInformation("Metodologia inativada com sucesso.");
		} catch (NegocioException e) {
		    addMensagemErro(e.getMessage());
		}
		resetBean();
		return listar();
	}
	
	/**
	 * Valida a inativação da metodologia de avaliação
	 */
	private void validarInativacao() {
		ValidatorUtil.validateRequired(obj.getAnoFim(), "Ano", erros);
		ValidatorUtil.validateRequired(obj.getPeriodoFim(), "Período", erros);
		
		if (!erros.isEmpty())
			return ;
		
		if (isAllNotEmpty(obj.getAnoInicio(),obj.getPeriodoInicio())) {
			int anoPeriodoFim = (obj.getAnoFim() * 10 ) + obj.getPeriodoFim();
			int anoPeriodoInicio = (obj.getAnoInicio() * 10 ) + obj.getPeriodoInicio();
			if (anoPeriodoFim < anoPeriodoInicio) {
				erros.addErro("Ano-Período informado não pode ser menor que o início da metodologia ("  + obj.getAnoInicio() + "." + obj.getPeriodoInicio() + ")");
			}
		}
		
		
	}

	@Override
	public String forwardCadastrar() {
		return "/ead/menu.jsf";
	}

	/**
	 * Retorna true se tem duas provas e tem tutor,
	 * caso contrário retorna false
	 * @return
	 */
	public boolean isMostrarNumeroAulas() {
		if (obj.isDuasProvas() && obj.isPermiteTutor())
			return true;
		return false;
	}

	@Override
	public String getListPage() {
		return getDirBase() + "/listaCursosMetodologia.jsp";
	}
	
	/**
	 * Seleciona o curso dado o id.
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	private String selecionarCurso(int idCurso) throws DAOException {
		clear();
		
		MetodologiacaAvaliacaoEadDao dao = getDAO(MetodologiacaAvaliacaoEadDao.class);
		
		cursoSelecionado = dao.findByPrimaryKey(idCurso, Curso.class);
		
		if (isEmpty(cursoSelecionado)) {
			addMensagemErro("Curso não encontrado.");
			return null;
		}
		
		metodologias = dao.findByCurso(idCurso);
		
		return forward(getDirBase() + "/listaMetodologias.jsp");
	}
	
	/**
	 * Seleciona o curso passado o id
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/MetodologiaAvaliacao/listaCursosMetodologia.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String selecionarCurso() throws DAOException {
		
		clear();
		
		Integer id = getParameterInt("id");
		
		return selecionarCurso(id);
	}

	/**
	 * Cadastra a metodologia de avaliação
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/MetodologiaAvaliacao/resumo_metodologia.jsp</li>
	 * </ul>
	 */
	public String cadastrar() throws ArqException {
		
		checkRole(SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_TUTORIA_EAD);

		if (obj.getId() != 0) {
			addMensagemErro("Metodologia não pode ser alterada.");
			return null;
		}
		
		ListaMensagens listaErros = obj.validate();
		if (!listaErros.isEmpty()) { 
			addMensagens(listaErros);
			return null;
		}
			
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_METODOLOGIA_AVALIACAO);
		mov.setObjMovimentado(obj);
		
		try {
			execute(mov);
		} catch (NegocioException e) {
			return tratamentoErroPadrao(e);
		}
		
		addMensagemInformation("Metodologia de Avaliação cadastrada com sucesso.");
		
		return selecionarCurso(obj.getCurso().getId());
	}

	/**
	 * Valida os dados gerais
	 * @throws DAOException
	 */
	private void validarDadosGerais() throws DAOException {
		if (!obj.isPermiteTutor())
			obj.setPorcentagemTutor(0);
		
		obj.setUsuarioCadastro(getUsuarioLogado());

		erros.addAll(obj.validarDadosGerais());
	}

	/**
	 * Verifica duplicidades de metodologias para o curso selecionado.
	 * @throws DAOException
	 */
	private void validarDuplicidade() throws DAOException {
		EADDao dao = getDAO(EADDao.class);
		MetodologiaAvaliacao metodologia = dao.findMetodologiaAvaliacaoPorCurso(obj.getCurso());
		if (obj.getId() == 0 && metodologia != null) {
			addMensagemErro("Já existe uma metodologia para o curso selecionado. Inative a metodologia atual para cadastrar uma nova.");
		}
	}

	/**
	 * Valida os Itens a serem cadastrados
	 */
	private void validarItens() {
		
		if (obj.isPermiteTutor()) {
			erros.addAll(obj.validarItens());
			
			for(int i = 0;i < lista.size();i++) {
				ItemAvaliacaoEad item = lista.get(i);
				if (!obj.getItens().contains(item)) {
					lista.remove(i);
					remover.add(item);
				}
			}
		}
	}
	
	/**
	 * Submete os Dados Gerais
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/MetodologiaAvaliacao/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String submeterDadosGerais() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_TUTORIA_EAD);
		
		validarDadosGerais();
		
		if (hasErrors())
			return null;
		
		if (obj.isPermiteTutor())
			return forward(getDirBase() + "/itens_ficha_avaliacao.jsp");
		else
			return forward(getDirBase() + "/resumo_metodologia.jsp");
	}
	
	/**
	 * Submete os Itens cadastrados
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ead/MetodologiaAvaliacao/itens_ficha_avaliacao.jsp</li>
	 * </ul>
	 * @return
	 */
	public String submeterItens() {
		
		validarItens();
		
		if (hasErrors())
			return null;
		
		return forward(getDirBase() + "/resumo_metodologia.jsp");
	}
	
	
	public List<MetodologiaAvaliacao> getMetodologias() {
		return metodologias;
	}

	public void setMetodologias(List<MetodologiaAvaliacao> metodologias) {
		this.metodologias = metodologias;
	}

	public Curso getCursoSelecionado() {
		return cursoSelecionado;
	}

	public void setCursoSelecionado(Curso cursoSelecionado) {
		this.cursoSelecionado = cursoSelecionado;
	}

	/**
	 * Verifica se tem alguma metodologia de avaliação ativa
	 * @return
	 */
	public boolean isAnyMetodologiaAtiva() {
		for (MetodologiaAvaliacao m : metodologias) {
			if (m.isAtiva())
				return true;
		}
		return false;
	}

	/**
	 * Regra de navegação para voltar no browser
	 * @author sigaa
	 *
	 */
	public enum NavegacaoVoltar {
		/** Form dos Dados Gerais */
		DADOS_GERAIS (1, "Dados Gerais do form", "/ead/MetodologiaAvaliacao/form.jsp"),
		/** Tela dos Itens */
		TELA_ITENS(2, "Tela de itens", "/ead/MetodologiaAvaliacao/itens_ficha_avaliacao.jsp"),
		/** Tela dos Cursos */
		TELA_CURSOS(3, "Tela de cursos", "/ead/MetodologiaAvaliacao/listaCursosMetodologia.jsp"),
		/** Tela das metodologias */
		TELA_METODOLOGIAS(4, "Tela de cursos", "/ead/MetodologiaAvaliacao/listaMetodologias.jsp");
		
		/**
		 * Retorna a regra de navegação para voltar
		 * @param id
		 * @return
		 */
		public static NavegacaoVoltar getByValor(int id) {
			
			if (DADOS_GERAIS.getValor() == id) 
				return DADOS_GERAIS;
			else if (TELA_ITENS.getValor() == id) 
				return TELA_ITENS;
			else if (TELA_CURSOS.getValor() == id) 
				return TELA_CURSOS;
			else if (TELA_METODOLOGIAS.getValor() == id) 
				return TELA_METODOLOGIAS;

			return null;
		}
		
		/**
		 * O valor que representa o Enum
		 */
		private int valor;
		
		/**
		 * A descrição
		 */
		private String descricao;

		/**
		 * url da pagina
		 */
		private String url;

		private NavegacaoVoltar(int valor, String descricao, String url) {
			this.valor = valor;
			this.descricao = descricao;
			this.url = url;	
		}
		
		public int getValor() {
			return valor;
		}

		public String getDescricao() {
			return descricao;
		}
		
		public void setValor(int valor) {
			this.valor = valor;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public void setDescricao(String descricao) {
			this.descricao = descricao;
		}

		@Override
		public String toString() {
			return String.valueOf(valor);
		}
		
	}

	public String getOperacaoVoltar() {
		return operacaoVoltar;
	}

	public void setOperacaoVoltar(String operacaoVoltar) {
		this.operacaoVoltar = operacaoVoltar;
	}

	
}
