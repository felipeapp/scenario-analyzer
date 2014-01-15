package br.ufrn.sigaa.projetos.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.component.UIData;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlInputText;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.AvaliacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.projetos.dominio.Avaliacao;
import br.ufrn.sigaa.projetos.dominio.NotaItemAvaliacao;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.negocio.AvaliacaoProjetosFactory;

/**
 * Bean responsável por controlar operações relacionadas a avaliação do projeto.
 *  
 * @author Ilueny Santos
 *
 */
@Scope("session")
@Component("avaliacaoProjetoBean")
public class AvaliacaoProjetoMBean extends SigaaAbstractController<Avaliacao> {

	/** Componente utilizado na construção da tabela de avaliação do projeto. */
	private UIData uiData = new UIData();

	/** Componente utilizado no campo da nota dada pelo avaliador ao projeto. */
	private HtmlInputText htmlNota = new HtmlInputText();

	/** Representa todas as avaliações disponíveis para o avaliador. */
	private List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();

	/** Construtor Padrão. */
	public AvaliacaoProjetoMBean() {
		obj = new Avaliacao();	
	}

	/**
	 *  Lista todas as avaliações de projetos para o usuário logado.
	 *  Chamado por Jsp:
	 *  <ul>
	 *  	<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *  </ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
	public String listarAvaliacoes() throws DAOException {
		avaliacoes = getDAO(AvaliacaoDao.class).findByAvaliador(getUsuarioLogado(),false);
		uiData = new HtmlDataTable();
		return forward(ConstantesNavegacaoProjetos.AVALIACAO_PROJETOS_LISTA);
	}
	
	/**
	 *  Lista todas as avaliações de um projeto específico.
	 *  Chamado por Jsp:
	 *  <ul>
	 *  	<li>sigaa.war/projetos/ProjetoBase/meus_projetos.jsp</li>
	 *  </ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
	public String listarAvaliacoesProjeto() throws DAOException {
		int id = getParameterInt("id", 0);
		avaliacoes = getDAO(AvaliacaoDao.class).findByProjeto(new Projeto(id));
		uiData = new HtmlDataTable();
		return forward(ConstantesNavegacaoProjetos.AVALIACOES_PROJETO);
	}


	/**
	 * Carrega a Avaliação para que seja dado o parecer pelo avaliador
	 * 
	 * Chamado por:
	 * sigaa.war/projetos/Avaliacao/lista.jsp
	 * 
	 * @return
	 * @throws NegocioException
	 * @throws RemoteException
	 * @throws ArqException
	 */
	public String iniciarAvaliacao() throws ArqException, RemoteException, NegocioException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.AVALIADOR_ACOES_ASSOCIADAS, SigaaPapeis.MEMBRO_COMITE_PESQUISA);
		try {
			prepareMovimento(SigaaListaComando.AVALIAR_PROJETO);
			obj = (Avaliacao) uiData.getRowData();	    
			obj = getGenericDAO().findByPrimaryKey(obj.getId(), Avaliacao.class);			
			obj.getNotas().iterator();
			obj.inicializarNotas();
			uiData = new HtmlDataTable();
			obj.getProjeto().getAvaliacoes().iterator();

			ProjetoBaseMBean projetoBean = getMBean("projetoBase");
			projetoBean.setObj(obj.getProjeto());
			projetoBean.iniciarDetalhesParaView(obj.getProjeto());

			return forward(ConstantesNavegacaoProjetos.AVALIACAO_PROJETOS_FORM);
		} catch (DAOException e) {
			notifyError(e);
			return null;
		}		
	}


	/**
	 * Método usado para avaliação ad hoc de projetos.
	 * 
	 * Chamado por:
	 * sigaa.war/projetos/Avaliacoes/form.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws ParseException
	 * @throws DAOException 
	 */
	public String avaliar() throws SegurancaException, ParseException, DAOException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.AVALIADOR_ACOES_ASSOCIADAS, SigaaPapeis.MEMBRO_COMITE_PESQUISA);
		obj.calcularMedia();
		obj.setDataAvaliacao(new Date());
		ListaMensagens lista = obj.validate();
		if (!isEmpty(lista.getErrorMessages())) {
			addMensagens(lista);
			return forward(ConstantesNavegacaoProjetos.AVALIACAO_PROJETOS_FORM);
		}
		return forward(ConstantesNavegacaoProjetos.AVALIACAO_PROJETOS_CONFIRM);
	}


	/**
	 * Método usado para confirmação da avaliação de projetos.
	 * 
	 * Chamado por:
	 * sigaa.war/projetos/Avaliacao/form.jsp
	 * 
	 * @return
	 * @throws ParseException
	 * @throws ArqException 
	 */
	public String confirmarAvaliacao() throws ParseException, ArqException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.AVALIADOR_ACOES_ASSOCIADAS, SigaaPapeis.MEMBRO_COMITE_PESQUISA);
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.AVALIAR_PROJETO);
			mov.setObjMovimentado(obj);
			mov.setObjAuxiliar(AvaliacaoProjetosFactory.getInstance().getEstrategia(obj.getProjeto()));
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return listarAvaliacoes();
		} catch(NegocioException e) {
			addMensagemErro(e.getMessage());
		}
		return forward(ConstantesNavegacaoProjetos.AVALIACAO_PROJETOS_FORM);
	}


	/**
	 * Método utilizado no cálculo automático da média a medida em que 
	 * o docente atribui a nota de cada pergunta da avaliação.
	 * 
	 *  Chamado por Jsp:
	 *  <ul>
	 *  	<li>sigaa.war/projetos/Avaliacoes/form.jsp</li>
	 *  </ul>
	 * 
	 * @return
	 */
	public String atualizarMedia() {
		NotaItemAvaliacao nota = (NotaItemAvaliacao)uiData.getRowData();
		try {
			double valorSubmetido = new Double(htmlNota.getSubmittedValue().toString().replace(',', '.'));

			ListaMensagens lista = new ListaMensagens();
			ValidatorUtil.validateMaxValue(valorSubmetido, nota.getItemAvaliacao().getNotaMaxima(), valorSubmetido + ", nota atribuída para '" + nota.getItemAvaliacao().getPergunta().getDescricao() + "'", lista);
			ValidatorUtil.validateMinValue(valorSubmetido, new Double(0), valorSubmetido + ", nota atribuída para '" + nota.getItemAvaliacao().getPergunta().getDescricao() + "'", lista);
			if (!isEmpty(lista.getErrorMessages())) {
				addMensagensAjax(lista);
				return null;
			}else {
				//Atualiza a nota digitada na avaliação.
				nota.setNota(valorSubmetido);
				int index = obj.getNotas().indexOf(nota);
				obj.getNotas().get(index).setNota(valorSubmetido);
				obj.calcularMedia();
			}
		}catch (Exception e) {
			addMensagemAjax(MensagensArquitetura.CONTEUDO_INVALIDO, "Nota atribuída para '" + nota.getItemAvaliacao().getPergunta().getDescricao() + "'");
		}
		return null;
	}

	/** Rediretiona o usuário para a página de Avaliação do Projeto. 
	 *  Chamado por Jsp:
	 *  <ul>
	 *  	<li>sigaa.war/projetos/Avaliacoes/confirm.jsp</li>
	 *  </ul>
	 */
	public String redirecionaPaginaAvaliacao() {
		return forward(ConstantesNavegacaoProjetos.AVALIACAO_PROJETOS_FORM);
	}

	/**
	 * Método utilizado para visualizar avalições.
	 * 
	 *  Chamado por Jsp:
	 *  <ul>
	 *  	<li>sigaa.war/projetos/Avaliacoes/lista.jsp</li>
	 *  </ul>
	 * 
	 * @return
	 */
	public String view() throws ArqException, RemoteException, NegocioException {
		try {
			obj = (Avaliacao) uiData.getRowData();	    
			obj = getGenericDAO().findByPrimaryKey(obj.getId(), Avaliacao.class);			
			obj.getNotas().iterator();
			uiData = new HtmlDataTable();
			return forward(ConstantesNavegacaoProjetos.AVALIACAO_PROJETOS_VIEW);
		} catch (DAOException e) {
			notifyError(e);
			return null;
		}		
	}

	/**
	 * Método utilizado para prepara a remoção de avalições.
	 * 
	 *  Chamado por Jsp:
	 *  <ul>
	 *  	<li>sigaa.war/projetos/Avaliacoes/buscar.jsp</li>
	 *  </ul>
	 * 
	 * @return
	 * @throws NegocioException 
	 * @throws RemoteException 
	 * @throws ArqException 
	 */
	@Override
	public String preRemover()  {
		try {
			prepareMovimento(SigaaListaComando.REMOVER_AVALIACAO_PROJETO);
			this.setConfirmButton("Remover");
			return view();
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}
		return null;
	}


	/**
	 * Remove uma avaliação do projeto.
	 * 
	 *  Chamado por Jsp:
	 *  <ul>
	 *  	<li>sigaa.war/projetos/Avaliacoes/buscar.jsp</li>
	 *  </ul>
	 *   
	 * @return
	 * @throws SegurancaException 
	 * @throws ArqException 
	 */
	public String remover() throws SegurancaException{
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.GESTOR_PESQUISA);
		try {
			MovimentoCadastro mov = new MovimentoCadastro();			
			mov.setCodMovimento(SigaaListaComando.REMOVER_AVALIACAO_PROJETO);
			mov.setObjMovimentado(obj);
			execute(mov, getCurrentRequest());
			this.setConfirmButton("");
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		} catch(NegocioException e) {
			addMensagemErro(e.getMessage());
		} catch (ArqException e) {
			addMensagemErro(e.getMessage());
		}

		return null;
	}


	public List<Avaliacao> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(List<Avaliacao> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	public HtmlInputText getHtmlNota() {
		return htmlNota;
	}

	public void setHtmlNota(HtmlInputText htmlNota) {
		this.htmlNota = htmlNota;
	}

	public UIData getUiData() {
		return uiData;
	}

	public void setUiData(UIData uiData) {
		this.uiData = uiData;
	}

}
