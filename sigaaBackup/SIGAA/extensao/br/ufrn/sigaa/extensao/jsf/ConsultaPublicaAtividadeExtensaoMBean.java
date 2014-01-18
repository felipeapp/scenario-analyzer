/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 15/04/2009
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.Objetivo;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;
import br.ufrn.sigaa.projetos.dominio.ArquivoProjeto;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.FotoProjeto;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/*******************************************************************************
 * MBean responsável SOMENTE pelas consultas públicas no 
 * Portal Público do SIGAA. Qualquer ação referente a alteração dos dados,
 * deverá ser utilizado a classe AtividadeExtensaoMBean.java
 * 
 * @author Mário Rizzi (rizzi@info.ufrn.br)
 * 
 ******************************************************************************/
@Scope("session")
@Component("consultaPublicaAtividadeExtensao")
public class ConsultaPublicaAtividadeExtensaoMBean extends SigaaAbstractController<AtividadeExtensao> {

	/**
	 * Atributo que define o membro da equipe seleciona na consulta.
	 * 
	 * <p> Na consultas pública guarda o coordenador da ação.</p>
	 */
	private MembroProjeto membroEquipe;
	
	/**
	 * Utilizado em telas de listagem
	 */
	private Collection<AtividadeExtensao> atividadesLocalizadas;

	/**
	 * Utilizado para a busca por nome de atividade
	 */
	private String buscaNomeAtividade;

	/**
	 * Utilizado para a busca por tipo de atividade
	 */
	private int buscaTipoAtividade;

	/**
	 * Utilizado para a busca por unidade da atividade
	 */
	private Integer buscaUnidade;

	/**
	 * Utilizado para a busca por ano da atividade
	 */
	private int buscaAno = CalendarUtils.getAnoAtual();

	/**
	 * Utilizado para a busca por servidor pertencente a atividade
	 */
	private Servidor servidor = new Servidor();

	/**
	 * Utilizado para a busca por docente pertencente a atividade
	 */
	private Servidor docente = new Servidor();

	/**
	 * Utilizado na opcao de busca por titulo
	 */
	private boolean checkBuscaTitulo;

	/**
	 * Utilizado na opcao de busca por tipo de atividade
	 */
	private boolean checkBuscaTipoAtividade;

	/**
	 * Utilizado na opcao de busca por unidade proponente
	 */
	private boolean checkBuscaUnidadeProponente;

	/**
	 * Utilizado na opcao de busca por servidor
	 */
	private boolean checkBuscaServidor;

	/**
	 * Utilizado na opcao de busca por ano
	 */
	private boolean checkBuscaAno;

	/**
	 * Atributo que define a atividade selecionado no formulário da consulta.
	 */
	private AtividadeExtensao atividadeSelecionada = new AtividadeExtensao();
	
	/**
	 * Atributo que define a sub-atividade selecionado no formulário da consulta.
	 */
	private SubAtividadeExtensao subAtividadeSelecionada = new SubAtividadeExtensao();

	public ConsultaPublicaAtividadeExtensaoMBean() {
		clear();
		getInitMBean();
	}

	/**
	 * Faz o papel do construtor em MBeans em escopo de sessão, é chamado a cada
	 * vez que a jsp é carregada
	 * JSP: \public\extensao\consulta_extensao.jsp
	 * @author Mário Rizzi (rizzi@info.ufrn.br)
	 */
	private void getInitMBean() {
		
		/**
		 * Id determina de onde está vindo a ação na parte pública do sigaa
		 * (Programas, Produto, Curso, Eventos...)
		*/
		if (getParameterInt("acao", 0) > 0) {
			buscaTipoAtividade = getParameterInt("acao", 0);
			checkBuscaTipoAtividade = true;
		}
		
		/**
		 * Id determina qual atividade que deverá ser exibido os detalhes,
		 * utilizado somente quando se necessita visualizar a atividade
		 */
		if(getParameterInt("id", 0) >0)
			view();
		
		/**
		 * Id determina qual subAtividade que deverá ser exibido os detalhes,
		 * utilizado somente quando se necessita visualizar a atividade
		 */
		if(getParameterInt("idSub", 0) >0)
			viewSubAtividade();
		
	}

	/**
	 * Método e criar uma nova instância 
	 * Método não invocado por JSP's.
	 */
	private void clear() {
		this.obj = new AtividadeExtensao();
		this.membroEquipe = new MembroProjeto();
		this.membroEquipe.setFuncaoMembro(new FuncaoMembro());		
		this.membroEquipe.setServidor(new Servidor());
		this.membroEquipe.getServidor().setPessoa(new Pessoa());
		
		ProgramaMBean programaMBean = (ProgramaMBean) getMBean("programaExtensao");
		AtividadeExtensao atv = new AtividadeExtensao();
		atv.getProjeto().setCoordenador(new MembroProjeto());
		programaMBean.setObj(atv);
	}

	/**
	 * Método que invoca limpar instâncias da classe e suas dependências
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li>\public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 */
	@Override
	public String cancelar() {
		limparMBeans();
		return redirectJSF("/public");
	}

	/**
	 * Método limpa os outros MBeans (ProjetoMBean, ProdutoMBean, CursoEventoMBean,
	 * ProgramMBean) utilizados durante o caso de uso.
	 * Método não invocado por JSP's.
	 */
	private void limparMBeans() {

		switch (obj.getTipoAtividadeExtensao().getId()) {
		case TipoAtividadeExtensao.PROJETO:
			ProjetoExtensaoMBean projetoMBean = (ProjetoExtensaoMBean) getMBean("projetoExtensao");
			projetoMBean.setObj(new AtividadeExtensao());
			break;

		case TipoAtividadeExtensao.CURSO:
		case TipoAtividadeExtensao.EVENTO:
			CursoEventoMBean cursoEventoMBean = (CursoEventoMBean) getMBean("cursoEventoExtensao");
			cursoEventoMBean.setObj(new AtividadeExtensao());
			break;

		case TipoAtividadeExtensao.PRODUTO:
			@SuppressWarnings("unused")
			ProdutoMBean produtoMBean = (ProdutoMBean) getMBean("produtoExtensao");
			// produtoMBean.setObj( new AtividadeExtensao() );
			break;

		case TipoAtividadeExtensao.PROGRAMA:
			ProgramaMBean programaMBean = (ProgramaMBean) getMBean("programaExtensao");
			programaMBean.setObj(new AtividadeExtensao());
			
			break;
		}
	}

	/**
	 * Método que recebe os filtros para 
	 * localizar as atividades de extensão pela parte pública do Sigaa
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 * 	</ul>
	 */
	public void localizarPublic() throws DAOException {

		// mostrar a view publica :D
		erros.getMensagens().clear();
		if (atividadesLocalizadas != null)
			atividadesLocalizadas.clear();

		/* Analisando filtros selecionados */
		String titulo = null;
		Integer[] idTipoAtividade = new Integer[1];
		Integer idUnidadeProponente = null;
		Integer idCordenador = null;
		Integer ano = null;
		Integer[] idSituacaoAtividade = new Integer[0];

		ListaMensagens lista = new ListaMensagens();

		// Definição dos filtros e validações
		if (checkBuscaTipoAtividade && buscaTipoAtividade > 0)
			idTipoAtividade[0] = buscaTipoAtividade;
		if (checkBuscaUnidadeProponente && buscaUnidade > 0)
			idUnidadeProponente = buscaUnidade;
		if (checkBuscaServidor && membroEquipe.getServidor().getId() > 0)
			idCordenador = membroEquipe.getServidor().getId();
		if (checkBuscaAno && buscaAno > 0)
			ano = buscaAno;
		if (checkBuscaTitulo)
			titulo = buscaNomeAtividade;

		// Listar somente as situações que estão valendo
		idSituacaoAtividade = new Integer[3];
		idSituacaoAtividade[0] = TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO;
		idSituacaoAtividade[1] = TipoSituacaoProjeto.EXTENSAO_CONCLUIDO;
		idSituacaoAtividade[2] = TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO;

		if (!checkBuscaTipoAtividade && !checkBuscaUnidadeProponente
				&& !checkBuscaServidor && !checkBuscaAno && !checkBuscaTitulo)
			lista.addErro("Selecione uma opção para efetuar a busca por ações de extensão.");
		else {
			AtividadeExtensaoDao dao = null;
			dao = getDAO(AtividadeExtensaoDao.class);
			if (lista.isEmpty()) {
				PagingInformation paginas = null;
				try {
				atividadesLocalizadas = dao.filter(null, null, null, null, null, null, null, titulo,
						idTipoAtividade, idSituacaoAtividade,
						idUnidadeProponente, null, null, null, idCordenador, null, ano,
						null, null, null, null, null, paginas, false, null, null, null,null, null, false);
				}
				catch(LimiteResultadosException e) {
					addMensagemErro("A consulta retornou resultados excessivos. Por favor, restrinja mais a busca.");
					return;
				}
			} else
				addMensagens(lista);
		}
	}

	/**
	 * Método que popula atividade e prepara MBeans para visualização.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\view.jsp</li>
	 *   </ul>
	 * 
	 * @Deprecated subsitituido por  InscricaoParticipanteAtividadeMBean.visualizarDadosCursoEvento()
	 * 
	 */
	@Deprecated
	public String view() {

		Integer id = getParameterInt("id", 0);

		if (id > 0) {
			try {

				atividadeSelecionada = getGenericDAO().findByPrimaryKey(id,
						AtividadeExtensao.class);

				atividadeSelecionada.setOrcamentosDetalhados(getGenericDAO()
						.findByExactField(OrcamentoDetalhado.class,
								"projeto.id", atividadeSelecionada.getProjeto().getId()));

				// carregando todos os objetivos pra evitar erro de lazy
				if ((atividadeSelecionada.getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PROJETO)
						&& (atividadeSelecionada.getProjetoExtensao() != null)) {
					atividadeSelecionada.setObjetivo(
							getGenericDAO().findByExactField(
									Objetivo.class,
									"atividadeExtensao.id",
									atividadeSelecionada.getProjetoExtensao()
											.getId()));

					for (Objetivo objetivo : atividadeSelecionada.getObjetivo())
						objetivo.getAtividadesPrincipais().iterator();

				}
				return forward(ConstantesNavegacao.ATIVIDADE_VIEW_PUBLIC);
				

			} catch (Exception e) {
				notifyError(e);
				addMensagemErro(e.getMessage());
				return null;
			}
		} else
			addMensagemErro("Ação de extensao não selecionada");

		return null;

	}
	
	/**
	 * Método que popula a sub-atividade e prepara MBeans para visualização.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\view.jsp</li>
	 *   </ul>
	 * 
	 */
	public String viewSubAtividade() {

		Integer id = getParameterInt("idSub", 0);

		if (id > 0) {
			try {

				subAtividadeSelecionada = getGenericDAO().findAndFetch(id, SubAtividadeExtensao.class, "atividade");
				return forward(ConstantesNavegacao.SUB_ATIVIDADE_VIEW_PUBLIC);	

			} catch (Exception e) {
				notifyError(e);
				addMensagemErro(e.getMessage());
				return null;
			}
		} else
			addMensagemErro("Ação de extensao não selecionada");

		return null;

	}
	
	/**
	 * Visualizar o arquivo anexo a atividade de extensao
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *   <li>\public\extensao\view.jsp</li>
	 *  </ul>
	 */
	public void viewArquivo() {
		try {
			int idArquivo = getParameterInt("idArquivo");
			EnvioArquivoHelper.recuperaArquivo(getCurrentResponse(), idArquivo,
					false);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(" Arquivo não encontrado!");
		}
		FacesContext.getCurrentInstance().responseComplete();
	}

	/**
	 * Retorna uma coleção de discentes membros do Projeto.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\view.jsp</li>
	 *   </ul>
	 * @return
	 */
	public Collection<MembroProjeto> getDiscentesMembrosAtividade() {

		// retorna somente os discentes do projeto
		ArrayList<MembroProjeto> result = new ArrayList<MembroProjeto>();
		for (MembroProjeto m : obj.getMembrosEquipe()) {
			if (m.getCategoriaMembro().getId() == CategoriaMembro.DISCENTE)
				result.add(m);
		}

		return result;

	}

	/**
	 * Retorna todas as atividades de extensão dea cordo com os filtros da consulta.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li>\public\extensao\view.jsp</li>
	 *   </ul>
	 * @return
	 */
	public Collection<AtividadeExtensao> getAtividadesLocalizadas() {
		return atividadesLocalizadas;
	}

	/**
	 * Popula todas as atividades de extensão dea cordo com os filtros da consulta.
	 * Método não invocado por JSP's
	 * @param atividadesLocalizadas
	 */
	public void setAtividadesLocalizadas(
			Collection<AtividadeExtensao> atividadesLocalizadas) {
		this.atividadesLocalizadas = atividadesLocalizadas;
	}

	/**
	 * Método que indica se o filtro nome da atividade foi setado.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * @return
	 */
	public String getBuscaNomeAtividade() {
		return buscaNomeAtividade;
	}

	/**
	 * Método que indica se o filtro nome da atividade foi setado.
	 * <br />
	 * Método não invocado por JSP's.
	 * @return
	 */
	public void setBuscaNomeAtividade(String buscaNomeAtividade) {
		this.buscaNomeAtividade = buscaNomeAtividade;
	}

	/**
	 * Método que indica se o filtro tipo da atividade foi setado.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * @return
	 */
	public int getBuscaTipoAtividade() {
		return buscaTipoAtividade;
	}

	/**
	 * Método que indica se o filtro tipo da atividade foi setado.
	 * <br />
	 * Método não invocado por JSP's.
	 */
	public void setBuscaTipoAtividade(int buscaTipoAtividade) {
		this.buscaTipoAtividade = buscaTipoAtividade;
	}

	/**
	 * Método que retorna todas as situações do projeto par seja
	 * selecionado na consulta.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * @return
	 */
	public Collection<SelectItem> getSituacoesProjetoSelectItem()
			throws DAOException {
		// return toSelectItems(getSituacoesProjeto(), "id", "descricao");
		return toSelectItems(
				getGenericDAO().findByExactField(TipoSituacaoProjeto.class,
						"contexto", "E", "asc", "descricao"), "id", "descricao");
	}

	/**
	 * Método que retorna todas as atividades de extensão como opção
	 * para consula.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * @return
	 */
	public Collection<SelectItem> getAllCombo() {
		return getAll(AtividadeExtensao.class, "id", "descricao");
	}

	/**
	 * Método que retorna todas os tipos de regiaão como opção
	 * para consula.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * @return
	 */
	public Collection<SelectItem> getAllTipoRegiaoCombo() {
		return getAll(TipoRegiao.class, "id", "descricao");
	}

	/**
	 * Método que retorna o membro da equipe selecionado na consulta.
	 * para consula.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>_serv
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * @return
	 */
	public MembroProjeto getMembroEquipe() {
		return membroEquipe;
	}

	/**
	 * Método que popula o membro da equipe selecionado na consulta.
	 * para consula.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * @return
	 */
	public void setMembroEquipe(MembroProjeto membroEquipeAtividadeExtensao) {
		this.membroEquipe = membroEquipeAtividadeExtensao;
	}

	/**
	 * Método que retorna a unidade selecionada na consulta.
	 * para consula.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * @return
	 */
	public Integer getBuscaUnidade() {
		return buscaUnidade;
	}

	/**
	 * Método que popula a unidade selecionada na consulta.
	 * para consula.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * @return
	 */
	public void setBuscaUnidade(Integer buscaUnidade) {
		this.buscaUnidade = buscaUnidade;
	}

	/**
	 * Método que retorna o docente na visualização 
	 * dos dados da atividade de extensão
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\view.jsp</li>
	 *   </ul>
	 * @return
	 */
	public Servidor getDocente() {
		return docente;
	}

	/**
	 * Método que popula o docente para visualização 
	 * dos dados da atividade de extensão
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\view.jsp</li>
	 *   </ul>
	 * Método não invocado por JSP's.
	 * @return
	 */
	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	/**
	 * Método que retorna o servidor na visualização 
	 * dos dados da atividade de extensão
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\view.jsp</li>
	 *   </ul>
	 * @return
	 */
	public Servidor getServidor() {
		return servidor;
	}

	/**
	 * Método que popula o servidor na visualização 
	 * dos dados da atividade de extensão
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\view.jsp</li>
	 *   </ul>
	 * @return
	 */
	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	/**
	 * Método que retorna se o servidor foi informado
	 * na consulta.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * @return
	 */
	public boolean isCheckBuscaServidor() {
		return checkBuscaServidor;
	}

	/**
	 * Método que popula se o servidor foi informado
	 * na consulta.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * @return
	 */
	public void setCheckBuscaServidor(boolean checkBuscaServidor) {
		this.checkBuscaServidor = checkBuscaServidor;
	}

	/**
	 * Método que retorna se o título da atividade de extensão foi informada
	 * na consulta.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * @return
	 */
	public boolean isCheckBuscaTitulo() {
		return checkBuscaTitulo;
	}

	/**
	 * Método que popula se o título da atividade de extensão foi informada
	 * na consulta.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * @return
	 */
	public void setCheckBuscaTitulo(boolean checkBuscaTitulo) {
		this.checkBuscaTitulo = checkBuscaTitulo;
	}

	/**
	 * Método que retorna se a unidade proponente da atividade de extensão foi informada
	 * na consulta.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * @return
	 */
	public boolean isCheckBuscaUnidadeProponente() {
		return checkBuscaUnidadeProponente;
	}

	/**
	 * Método que popula se a unidade proponente da atividade de extensão foi informada
	 * na consulta.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * @return
	 */
	public void setCheckBuscaUnidadeProponente(
			boolean checkBuscaUnidadeProponente) {
		this.checkBuscaUnidadeProponente = checkBuscaUnidadeProponente;
	}

	/**
	 * Retorna todas as situações possí­veis de atividades cadastradas no banco
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>  \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getTipoSituacaoAtividadeCombo() {

		try {
			return toSelectItems(getGenericDAO().findByExactField(
					TipoSituacaoProjeto.class, "contexto", "E", "asc",
					"descricao"), "id", "descricao");
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}

	}

	/**
	 * Carrega o atividade do BD
	 * Método não invocado por JSP's.
	 * @param id
	 */
	private Object loadObj(int id) {
		try {

			GenericDAO dao = getGenericDAO();
			return dao.findByPrimaryKey(id, AtividadeExtensao.class);

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}

	}

	/**
	 * Retorna coleção de selectItem com todos os municípios ativos cadastrados
	 * em public.municipio
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 * 	 </ul>
	 * @return
	 */
	public Collection<SelectItem> getAllMunicipiosAtivosCombo() {

		try {
			return toSelectItems(getDAO(MunicipioDao.class).findByExactField(
					Municipio.class, "ativo", Boolean.TRUE, "asc", "nome"),
					"id", "nomeUF");
		} catch (Exception e) {
			notifyError(e);
			return new ArrayList<SelectItem>();
		}

	}

	/**
	 * Retorna a lista de todos os arquivos (documentos) anexados a ação de
	 * extensão
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li>  \public\extensao\view.jsp</li>
	 *   </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<ArquivoProjeto> getArquivosAtividade()
			throws DAOException {
		obj.setArquivos(getGenericDAO().findByExactField(
				ArquivoProjeto.class, "atividade.id", obj.getId()));
		return obj.getArquivos();
	}

	/**
	 * Retorna todas as fotos anexadas a ação de extensão
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\view.jsp</li>
	 *   </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<FotoProjeto> getFotosAtividade()
			throws DAOException {
		obj.setFotos(getGenericDAO().findByExactField(
				FotoProjeto.class, "atividade.id", obj.getId()));
		return obj.getFotos();
	}

	/**
	 * Retorna a atividades de extensão selecionada para visualização.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li>  \public\extensao\view.jsp</li>
	 *   </ul>
	 * @return
	 */
	public AtividadeExtensao getAtividadeSelecionada() {
		return atividadeSelecionada;
	}

	/**
	 * Popula a atividades de extensão selecionada para visualização.
	 * <br />
	 * Método não invocado por JSP's
	 * @return
	 */
	public void setAtividadeSelecionada(AtividadeExtensao atividadeSelecionada) {
		this.atividadeSelecionada = atividadeSelecionada;
	}

	/**
	 * Retorna se o ano foi informado na consulta.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 * 	 </ul>
	 * @return
	 */
	public boolean isCheckBuscaAno() {
		return checkBuscaAno;
	}

	/**
	 * Popula se o ano foi informado na consulta.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 * 	 </ul>
	 * @return
	 */
	public void setCheckBuscaAno(boolean checkBuscaAno) {
		this.checkBuscaAno = checkBuscaAno;
	}

	/**
	 * Retorna o ano informado na consulta.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 * 	 </ul>
	 * @return
	 */
	public int getBuscaAno() {
		return buscaAno;
	}

	/**
	 * Popula o ano informado na consulta.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 * 	 </ul>
	 * @return
	 */
	public void setBuscaAno(int buscaAno) {
		this.buscaAno = buscaAno;
	}

	/**
	 * Muda o ação do Mbean utilizado na tela de visualizar avaliadores da ação
	 * 
	 * Método não invocado por JSP's.
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void changeAtividadeExtensao(ValueChangeEvent evt)
			throws DAOException {
		Integer idAtividade = 0;
		try {

			idAtividade = new Integer(evt.getNewValue().toString());
			if (idAtividade != null && idAtividade != 0) {
				obj = (AtividadeExtensao) loadObj(idAtividade);
				obj.getAvaliacoes().iterator();
			}

		} catch (Exception e) {
			notifyError(e);
		}

	}

	/**
	 * 
	 * Retorna as atividades localizadas de acordo com as opções de buscas selecionadas.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li> \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * @return
	 */
	public Collection<SelectItem> getAtividadesLocalizadasCombo() {
		List<AtividadeExtensao> lista = new ArrayList<AtividadeExtensao>();

		if (atividadesLocalizadas != null)
			lista = new ArrayList<AtividadeExtensao>(atividadesLocalizadas);

		return toSelectItems(lista, "id", "titulo");
	}


	/**
	 * Método que lista todos os docentes da atividade de extensão selecionada
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li>  \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * @return
	 */
	public Collection<SelectItem> getDocentesCombo() {

		try {
			List<MembroProjeto> lista = new ArrayList<MembroProjeto>();
			Collection<MembroProjeto> docentes = getGenericDAO()
					.findByExactField(MembroProjeto.class, "projeto.id", obj.getProjeto().getId());

			for (MembroProjeto doc : docentes) {
				if (doc.isAtivo()
						&& doc.getCategoriaMembro().getId() == CategoriaMembro.DOCENTE)
					lista.add(doc);
			}

			return toSelectItems(lista, "id", "pessoa.nome");

		} catch (DAOException e) {
			notifyError(e);
			return null;
		}

	}

	/**
	 * Retorna se o tipo da atividade de extensão foi informada.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li>  \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * @return
	 */
	public boolean isCheckBuscaTipoAtividade() {
		return checkBuscaTipoAtividade;
	}

	/**
	 * Popula se o tipo da atividade de extensão foi informada.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   <li>  \public\extensao\consulta_extensao.jsp</li>
	 *   </ul>
	 * @return
	 */
	public void setCheckBuscaTipoAtividade(boolean checkBuscaTipoAtividade) {
		this.checkBuscaTipoAtividade = checkBuscaTipoAtividade;
	}

	public void setSubAtividadeSelecionada(SubAtividadeExtensao subAtividadeSelecionada) {
		this.subAtividadeSelecionada = subAtividadeSelecionada;
	}

	public SubAtividadeExtensao getSubAtividadeSelecionada() {
		return subAtividadeSelecionada;
	}

	/**
	 * Realiza a busca de ações de extensão a partir da unidade.
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public String consultarAcoesUnidade() throws DAOException, NegocioException {
		clearBusca();
		Integer idUnidade = getDAO(UnidadeDao.class).findIdByCodigo(buscaUnidade);
		if(ValidatorUtil.isEmpty(idUnidade))
			throw new NegocioException("Unidade não localizada.");
		buscaUnidade = idUnidade;
		setCheckBuscaUnidadeProponente(true);
		localizarPublic();
		return redirect("/public/extensao/consulta_extensao.jsf");
	}
	
	/**
	 * Realiza a busca de projetos de extensão a partir da unidade.
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public String consultarProjetosUnidade() throws DAOException, NegocioException {
		clearBusca();
		Integer idUnidade = getDAO(UnidadeDao.class).findIdByCodigo(buscaUnidade);
		if(ValidatorUtil.isEmpty(idUnidade))
			throw new NegocioException("Unidade não localizada.");
		buscaUnidade = idUnidade;
		buscaTipoAtividade = TipoAtividadeExtensao.PROJETO;
		setCheckBuscaTipoAtividade(true);
		setCheckBuscaUnidadeProponente(true);
		localizarPublic();
		return redirect("/public/extensao/consulta_extensao.jsf");
	}
	
	private void clearBusca() {
		setCheckBuscaAno(false);
		setCheckBuscaServidor(false);
		setCheckBuscaTipoAtividade(false);
		setCheckBuscaTitulo(false);
		setCheckBuscaUnidadeProponente(false);
		setBuscaAno(CalendarUtils.getAnoAtual());
		setBuscaNomeAtividade(null);
		setBuscaTipoAtividade(0);
		setMembroEquipe(null);
		setAtividadesLocalizadas(null);
	}
}
