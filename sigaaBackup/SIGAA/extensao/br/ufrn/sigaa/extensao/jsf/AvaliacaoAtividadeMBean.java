/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/12/2007
 *
 */
package br.ufrn.sigaa.extensao.jsf;  

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlInputText;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.Permissao;
import br.ufrn.rh.dominio.Servidor;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.extensao.AvaliacaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroComissaoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.AvaliacaoAtividade;
import br.ufrn.sigaa.extensao.dominio.AvaliacaoOrcamentoProposto;
import br.ufrn.sigaa.extensao.dominio.OrcamentoConsolidado;
import br.ufrn.sigaa.extensao.dominio.TipoAvaliacao;
import br.ufrn.sigaa.extensao.dominio.TipoParecerAvaliacaoExtensao;
import br.ufrn.sigaa.monitoria.dominio.GrupoItemAvaliacao;
import br.ufrn.sigaa.monitoria.dominio.ItemAvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.NotaItemMonitoria;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/*******************************************************************************
 * MBean usado na avaliação das atividades de extensão.
 * <br> 
 * Os avaliadores, tanto os membros do comitê quanto os ad hoc, listam todas as
 * ações pendentes de avaliação e selecionam uma para avaliar. Um formulário é
 * apresentado com os dados da ação de extensão e os campos onde o avaliador
 * pode dar o seu parecer.
 * <br>
 * Este MBean é utilizado também por membros da proex que desejam verificar o
 * andamento das avaliações das ações.
 * 
 * 
 * @author Victor Hugo
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Scope("session")
@Component("avaliacaoAtividade")
public class AvaliacaoAtividadeMBean extends SigaaAbstractController<AvaliacaoAtividade> {

	/** avaliações realizadas pelos pareceristas, ou membros da comissão */
	Collection<AvaliacaoAtividade> avaliacoesParciais = new ArrayList<AvaliacaoAtividade>();
	/** Atributo utilizado pare representar as avaliações Localizadas */
	Collection<AvaliacaoAtividade> avaliacoesLocalizadas = new ArrayList<AvaliacaoAtividade>();
	/** Atributo utilizado para representar um Collection de Atividades pendentes de Avaliação */
	Collection<AvaliacaoAtividade> atividadesPendentesAvaliacao;
	/** Atributo utilizado para representar uma avaliação selecionada */
	AvaliacaoAtividade avaliacaoSelecionada = new AvaliacaoAtividade();
	/** 
	 * Atributo utilizado para representar um Collection de notas recebidas pelo avaliador de sobre 
	 * determinados itens da avaliação de um projeto. 
	 */
	private Collection<NotaItemMonitoria> notasItem = new ArrayList<NotaItemMonitoria>();
	/** Atributo utilizado para representar os avisos do a4j */
	private String a4jAviso;
	/** Atributo utilizado para verificar se há busca por Título */
	private boolean checkBuscaTitulo;
	/** Atributo utilizado para verificar se há busca por Tipo de Atividade */
	private boolean checkBuscaTipoAtividade;
	/** Atributo utilizado para verificar se há busca por Unidade Proponente */
	private boolean checkBuscaUnidadeProponente;
	/** Atributo utilizado para verificar se há busca pela Área do CNPQ */
	private boolean checkBuscaAreaCNPq;
	/** Atributo utilizado para verificar se há busca por Temática Principal */
	private boolean checkBuscaAreaTematicaPrincipal;
	/** Atributo utilizado para verificar se há busca por Servidor da Atividade */
	private boolean checkBuscaServidorAtividade;
	/** Atributo utilizado para verificar se há busca por Ano da Atividade */
	private boolean checkBuscaAnoAtividade;
	/** Atributo utilizado para verificar se há busca por Tipo de Avaliação  */
	private boolean checkBuscaTipoAvaliacao;
	/** Atributo utilizado para verificar se há busca por Servidor Avaliador */
	private boolean checkBuscaServidorAvaliador;
	/** Atributo utilizado para verificar se há busca por Status da Avaliação */
	private boolean checkBuscaStatusAvaliacao;
	/** Atributo utilizado para verificar se há busca por Edital */
	private Boolean checkBuscaEdital = false;
	/** Atributo utilizado para representar o Edital a ser buscado */
	private Integer buscaEdital;
	/** Atributo utilizado para representar o Título da Atividade a ser buscado */
	private String buscaTituloAtividade;
	/** Atributo utilizado para representar o Tipo da Atividade a ser buscada */
	private Integer buscaTipoAtividade;
	/** Atributo utilizado para representar a Unidade a ser buscada */
	private Integer buscaUnidade;
	/** Atributo utilizado para representar a Área do CNPQ a ser buscada */
	private Integer buscaAreaCNPq;
	/** Atributo utilizado para representar a Área da Temática Principal a ser buscada */
	private Integer buscaAreaTematicaPrincipal;
	/** Atributo utilizado para representar o Ano da Atividade a ser buscada */
	private Integer buscaAnoAtividade;
	/** Atributo utilizado para representar o Tipo de Avaliação a ser buscado */
	private Integer buscaTipoAvaliacao;
	/** Atributo utilizado para representar o Status da Avaliação a ser buscado */
	private Integer buscaStatusAvaliacao;
	/** Atributo utilizado para representar o Servidor Avaliador a ser buscado */
	private Servidor servidorAvaliador = new Servidor();
	/** Atributo utilizado para representar o Servidor da Atividade a ser buscado */
	private Servidor servidorAtividade = new Servidor();
	/** Atributo utilizado para representar o DataTable HTML*/
	private HtmlDataTable htmlDataTable;
	/** Atributo utilizado para representar a nota HTML */
	private HtmlInputText htmlNota;
	/** Código utilizado para carregar o membro do grupo de pesquisa */
	private String codigo;
	
	/**
	 * Construtos padrão
	 */
	public AvaliacaoAtividadeMBean() {
		obj = new AvaliacaoAtividade();
	}

	/**
	 * Retorna todas as avaliações de um membro do comitê
	 * 
	 * Chamado por: 
	 * sigaa.war/extensao/AvaliacaoAtividade/lista.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public Collection<AvaliacaoAtividade> getAvaliacoesMembroComite() throws SegurancaException {
	    checkRole(SigaaPapeis.MEMBRO_COMITE_EXTENSAO);
	    AvaliacaoExtensaoDao dao = getDAO(AvaliacaoExtensaoDao.class);
	    try {
	    	return dao.findByAvaliadorAtividade(getUsuarioLogado().getServidor().getId(), null, TipoAvaliacao.AVALIACAO_ACAO_COMITE);
	    } catch (DAOException e) {
			tratamentoErroPadrao(e);
			return new ArrayList<AvaliacaoAtividade>();
	    }
	}

	/**
	 * Retorna todas as avaliações de um membro do comitê
	 * 
	 * Chamado por: 
	 * sigaa.war/extensao/AvaliacaoAtividade/lista.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public Collection<AvaliacaoAtividade> getAvaliacoes() throws SegurancaException, DAOException {
	    AvaliacaoExtensaoDao dao = getDAO(AvaliacaoExtensaoDao.class);
	    try {
	    	return dao.findByAvaliadorAtividade(getUsuarioLogado().getServidor().getId(), null, TipoAvaliacao.AVALIACAO_ACAO_COMITE);
	    } finally {
	    	dao.close();
	    }
	}
	
	public String carregarAvaliacoes() throws DAOException, NumberFormatException {
		UsuarioDao dao = getDAO(UsuarioDao.class);
		try {
			if (codigo.contains(".jsp") || codigo.contains(".jsf"))
				codigo = codigo.substring(0, codigo.length()-4);
			br.ufrn.sigaa.pessoa.dominio.Servidor serv = dao.findByPrimaryKey(Integer.parseInt(codigo), 
					br.ufrn.sigaa.pessoa.dominio.Servidor.class);
			Usuario usua = dao.findByServidorLeve(serv);
			getCurrentRequest().getSession(true).setAttribute("usuario", usua);
			getUsuarioLogado().setServidor(serv);
			getUsuarioLogado().setPessoa(serv.getPessoa());
			setSubSistemaAtual(SigaaSubsistemas.PORTAL_PUBLICO);
			getUsuarioLogado().setPermissoes(new ArrayList<Permissao>());
			Permissao permissao = new Permissao();
			permissao.setPapel(new Papel(SigaaPapeis.MEMBRO_COMITE_INTEGRADO));
			getUsuarioLogado().getPermissoes().add(permissao);
			getUsuarioLogado().setPapeis(new ArrayList<Papel>());
			getUsuarioLogado().getPapeis().add(new Papel(SigaaPapeis.MEMBRO_COMITE_INTEGRADO));
			return redirectJSF("public/extensao/avaliacao.jsp");
		} finally {
			dao.close();
		}
	}
	
	/**
	 * 
	 * Retorna todas as avaliações pendentes para o parecerista (usuário logado)
	 * 
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/AvaliacaoAtividade/lista_parecerista.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public Collection<AvaliacaoAtividade> getAvaliacoesPendentesParecerista() throws SegurancaException {
	    checkRole(SigaaPapeis.PARECERISTA_EXTENSAO);
	    AvaliacaoExtensaoDao dao = getDAO(AvaliacaoExtensaoDao.class);
	    
	    try {
		return dao.findByAvaliadorAtividade(getUsuarioLogado().getServidor().getId(), null, TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA);
	    } catch (DAOException e) {
		tratamentoErroPadrao(e);
		return new ArrayList<AvaliacaoAtividade>();
	    }
	}

	/**
	 * 
	 * Retorna todas as avaliações pendentes para avaliação pelos membros do
	 * comitê.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Método não chamado por JSPs.</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public Collection<AvaliacaoAtividade> getAvaliacoesPendentesComite() throws SegurancaException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_EXTENSAO);
		AvaliacaoExtensaoDao dao = getDAO(AvaliacaoExtensaoDao.class);
		try {
			return dao.findByAtividadeStatusAvaliacao(0, true,
					StatusAvaliacao.AGUARDANDO_AVALIACAO,
					TipoAvaliacao.AVALIACAO_ACAO_COMITE);

		} catch (DAOException e) {
			tratamentoErroPadrao(e);
			return new ArrayList<AvaliacaoAtividade>();
		}
	}

	/**
	 * Inicar a avaliação da ação por um membro do comitê.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/AvaliacaoAtividade/lista.jsp
	 * 
	 * @return
	 * @throws NegocioException
	 * @throws RemoteException
	 * @throws ArqException
	 */
	public String iniciarAvaliacaoComite() throws ArqException, RemoteException, NegocioException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_EXTENSAO);
		try {

			int idAvaliacao = getParameterInt("idAvaliacao", 0);
			if (idAvaliacao > 0) {
				prepareMovimento(SigaaListaComando.AVALIAR_ATIVIDADE_EXTENSAO);
				obj = getGenericDAO().findByPrimaryKey(idAvaliacao, AvaliacaoAtividade.class);
				getGenericDAO().refresh(obj.getAtividade().getProjeto().getCoordenador());

				//Definindo valor padrão como APROVADO
				if (obj.getDataAvaliacao() == null) {
					obj.setParecer(getGenericDAO().findByPrimaryKey(TipoParecerAvaliacaoExtensao.APROVADO, TipoParecerAvaliacaoExtensao.class));
				}

				obj.getAtividade().setMembrosEquipe(getDAO(MembroProjetoDao.class).findAtivosByProjeto(obj.getAtividade().getProjeto().getId(),
								CategoriaMembro.DISCENTE, CategoriaMembro.DOCENTE, CategoriaMembro.EXTERNO, CategoriaMembro.SERVIDOR));
				// evitar erro de lazy na busca do coordenador
				for (MembroProjeto mp : obj.getAtividade().getMembrosEquipe()) {
					mp.getId();
				}
				
				// exibir dados da atividade para o avaliador
				AtividadeExtensaoMBean atividadeMBean = (AtividadeExtensaoMBean) getMBean("atividadeExtensao");
				atividadeMBean.clear();
				atividadeMBean.setObj(obj.getAtividade());
				atividadeMBean.getIniciarOrcamento();

				// lista as avaliações dos pareceristas sobre esta atividade
				AvaliacaoExtensaoDao avaDao = getDAO(AvaliacaoExtensaoDao.class);
				avaliacoesParciais = avaDao.findByAtividadeStatusAvaliacao(obj
						.getAtividade().getId(), false, 0,
						TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA);

				return forward(ConstantesNavegacao.AVALIACAO_PROPOSTA_FORM);
			} else
				return null;

		} catch (DAOException e) {
			notifyError(e);
			return null;

		}
	}

	/**
	 * Inicia a avaliação da ação pelo presidente do comitê de extensão.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/AvaliacaoAtividade/lista_presidente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException
	 * @throws RemoteException
	 * @throws ArqException
	 */
	public String iniciarAvaliacaoPresidenteComite() throws ArqException,
			RemoteException, NegocioException {

		checkRole(SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO);
		
		try {

			prepareMovimento(SigaaListaComando.AVALIAR_ATIVIDADE_EXTENSAO);

			int idAtividade = getParameterInt("id", 0);
			AtividadeExtensao atividade = getGenericDAO().findByPrimaryKey(idAtividade, AtividadeExtensao.class);
			getGenericDAO().refresh(atividade.getProjeto().getCoordenador());

			atividade.setMembrosEquipe(getDAO(MembroProjetoDao.class).findAtivosByProjeto(atividade.getProjeto().getId(),
					CategoriaMembro.DISCENTE, CategoriaMembro.DOCENTE, CategoriaMembro.EXTERNO, CategoriaMembro.SERVIDOR));
			// evitar erro de lazy na busca do coordenador
			for (MembroProjeto mp : atividade.getMembrosEquipe()) {
				mp.getId();
			}
			
			// exibir dados da atividade para que o avaliador possa avaliar melhor...
			AtividadeExtensaoMBean atividadeMBean = (AtividadeExtensaoMBean) getMBean("atividadeExtensao");
			atividadeMBean.clear();
			atividadeMBean.setObj(atividade);
			atividadeMBean.getIniciarOrcamento();

			// verifica se já existe uma avaliação de presidente do comitê para esta atividade
			AvaliacaoExtensaoDao avaDao = getDAO(AvaliacaoExtensaoDao.class);
			obj = avaDao.findAvaliacaoAcaoPresidenteByAtividade(atividade.getId());

			MembroComissaoDao daoComissao = getDAO(MembroComissaoDao.class);
			MembroComissao membro = daoComissao.findByServidorPapel(getServidorUsuario().getId(),
					MembroComissao.MEMBRO_COMISSAO_EXTENSAO);

			if (ValidatorUtil.isEmpty(obj)) {

				obj = new AvaliacaoAtividade();
				obj.setAtividade(atividade);
				obj.setAvaliadorAtividadeExtensao(null);
				obj.setMembroComissao(membro);				
				obj.setTipoAvaliacao(new TipoAvaliacao(TipoAvaliacao.AVALIACAO_ACAO_PRESIDENTE_COMITE));
				obj.setStatusAvaliacao(new StatusAvaliacao(StatusAvaliacao.AVALIACAO_EM_ANDAMENTO));

				// lista de orçamentos consolidados da atividade para
				// criar orçamentos propostos da avaliação nova
				Collection<OrcamentoConsolidado> consolidados = avaDao
						.findByExactField(OrcamentoConsolidado.class,
								"projeto.id", atividade.getProjeto().getId());

				for (OrcamentoConsolidado consolidado : consolidados) {
					AvaliacaoOrcamentoProposto proposto = new AvaliacaoOrcamentoProposto();
					proposto.setAvaliacaoAtividade(obj);
					proposto.setElementoDespesa(consolidado.getElementoDespesa());
					obj.getOrcamentoProposto().add(proposto);
				}

			}

			// define valor padrão para aprovado no formulário de avaliação
			if (obj.getDataAvaliacao() == null) {
				obj.setParecer(getGenericDAO().findByPrimaryKey(TipoParecerAvaliacaoExtensao.APROVADO, TipoParecerAvaliacaoExtensao.class));
			}

			avaliacoesParciais = new ArrayList<AvaliacaoAtividade>();

			// lista as avaliações dos outros membros da comissão sobre esta atividade
			avaliacoesParciais.addAll(avaDao.findByAtividadeStatusAvaliacao(obj
					.getAtividade().getId(), false, 0,
					TipoAvaliacao.AVALIACAO_ACAO_COMITE));

			// lista as avaliações dos pareceristas sobre esta atividade
			avaliacoesParciais.addAll(avaDao.findByAtividadeStatusAvaliacao(obj
					.getAtividade().getId(), false, 0,
					TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA));

			return forward(ConstantesNavegacao.AVALIACAO_PROPOSTA_FORM_PRESIDENTE);

		} catch (DAOException e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;

		}

	}

	/**
	 * Carrega a Avaliação para que seja dado o parecer pelo avaliador
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/AvaliacaoAtividade/lista_parecerista.jsp
	 * 
	 * @return
	 * @throws NegocioException
	 * @throws RemoteException
	 * @throws ArqException
	 */
	public String iniciarAvaliacaoParecerista() throws ArqException, RemoteException, NegocioException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.GESTOR_EXTENSAO, 
				SigaaPapeis.MEMBRO_COMITE_EXTENSAO, SigaaPapeis.PARECERISTA_EXTENSAO, 
				SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO, SigaaPapeis.MEMBRO_COMITE_MONITORIA,
				SigaaPapeis.MEMBRO_COMITE_PESQUISA, SigaaPapeis.MEMBRO_COMITE_CIENTIFICO_MONITORIA);
		try {
			prepareMovimento(SigaaListaComando.AVALIAR_ATIVIDADE_EXTENSAO);
			int idAvaliacao = getParameterInt("idAvaliacao", 0);
			obj = getGenericDAO().findByPrimaryKey(idAvaliacao, AvaliacaoAtividade.class);
			getGenericDAO().refresh(obj.getAtividade().getProjeto().getCoordenador());
			
			if ( obj.getParecer() == null || obj.getParecer().getId() == 0 ) 
				obj.setParecer(getGenericDAO().findByPrimaryKey(TipoParecerAvaliacaoExtensao.APROVADO, TipoParecerAvaliacaoExtensao.class));

			obj.getNotasItem().iterator();
			obj.getAtividade().setMembrosEquipe(getDAO(MembroProjetoDao.class).findAtivosByProjeto(obj.getAtividade().getProjeto().getId(), 
					CategoriaMembro.DISCENTE, CategoriaMembro.DOCENTE,CategoriaMembro.EXTERNO,CategoriaMembro.SERVIDOR));
		    // evitar erro de lazy na busca do coordenador
		    for (MembroProjeto mp : obj.getAtividade().getMembrosEquipe()) {
		    	mp.getId();
		    }
			
			// Carrega os grupos pra montar o formulário de avaliação
			carregaGruposAvaliacoesAtivos(obj.getNotasItem());			

			// exibir dados da atividade para que o avaliador possa avaliar...
			AtividadeExtensaoMBean atividadeExtensao = (AtividadeExtensaoMBean) getMBean("atividadeExtensao");
			atividadeExtensao.clear();
			atividadeExtensao.setObj(obj.getAtividade());
			atividadeExtensao.getIniciarOrcamento();
			return forward(ConstantesNavegacao.AVALIACAO_PROPOSTA_FORM_PARECER);

		} catch (DAOException e) {
			notifyError(e);
			return null;
		}		
	}
	
	/**
	 * Carrega do banco todos os grupos de avaliações que estiverem ativos
	 * os grupos compõem o formulário de avaliação do projeto de extensão
	 * @param notas
	 * 
	 * Não é chamado por JSPs.
	 * 
	 * @throws DAOException
	 */
	private void carregaGruposAvaliacoesAtivos(List<NotaItemMonitoria> notas) throws DAOException {
		if (isEmpty(notas)) {
			// Pega todos os grupos de formulário de avaliação de projetos
			AvaliacaoExtensaoDao dao = getDAO(AvaliacaoExtensaoDao.class);
			List<GrupoItemAvaliacao> grupos = (List<GrupoItemAvaliacao>) dao.findByGruposAtivosDoTipo(new Character('E'));
			obj.setNotasItem(new ArrayList<NotaItemMonitoria>());
			for ( GrupoItemAvaliacao grupo : grupos ) {
				for (ItemAvaliacaoMonitoria pergunta : grupo.getItens()) {
					if (pergunta.isAtivo()) {
						// Seta as notas carregadas nos grupos na avaliação
						NotaItemMonitoria notaPergunta = new NotaItemMonitoria();
						notaPergunta.setItemAvaliacao(pergunta);
						obj.addNotaItemMonitoria(notaPergunta);					
					}
				}
			}
		} 
	}

	
	/**
	 * Confirmar avaliação de um projeto de monitoria
	 * feita por um membro da comissão de monitoria (docente)
	 *
	 * Chamado por:
	 * sigaa.war/monitoria/AvaliacaoMonitoria/confirmar_avaliacao.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws ParseException
	 */
	public String confirmarAvaliacaoParecerista() throws ArqException{
		checkRole(SigaaPapeis.GESTOR_EXTENSAO, SigaaPapeis.MEMBRO_COMITE_EXTENSAO, 
			SigaaPapeis.PARECERISTA_EXTENSAO, SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO,
			SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.MEMBRO_COMITE_MONITORIA,
			SigaaPapeis.MEMBRO_COMITE_PESQUISA, SigaaPapeis.MEMBRO_COMITE_CIENTIFICO_MONITORIA);
		
		if (!confirmaSenha())
			return null;
		
		try {
			Double notaAvaliacao = new Double(obj.getNota());
			avaliarAtividadeExtensao();
			obj = new AvaliacaoAtividade();			
			addMensagemInformation("Avaliação realizada com sucesso. A média do projeto é " +
					Formatador.getInstance().formatarDecimalInt( notaAvaliacao ));
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}

		if ( isUserInRole(SigaaPapeis.GESTOR_EXTENSAO, SigaaPapeis.MEMBRO_COMITE_EXTENSAO, 
				SigaaPapeis.PARECERISTA_EXTENSAO, SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO) ) {
			resetBean();
			return forward(ConstantesNavegacao.AVALIACAO_PROPOSTA_LISTA_PARECER);
		} else if ( isPortalDocente() ) {
			resetBean();
			return forward("/extensao/AvaliacaoAtividade/lista_avaliacoes.jsf");
		} else {
			return redirect("/public/extensao/avaliacaoProjeto/"+codigo+".jsp");
		}

	}
	
	/**
	 * Método usado para avaliação ad hoc de ações de extensão.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/AvaliacaoAtividade/form_parecerista.jsp
	 * sigaa.war/extensao/AvaliacaoAtividade/form_presidente.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws ParseException
	 * @throws DAOException 
	 */
	public String avaliarPareceristaAdHoc() throws SegurancaException, ParseException, DAOException {		
		checkRole(SigaaPapeis.GESTOR_EXTENSAO, SigaaPapeis.MEMBRO_COMITE_EXTENSAO, 
				SigaaPapeis.PARECERISTA_EXTENSAO, SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO,
				SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.MEMBRO_COMITE_MONITORIA,
				SigaaPapeis.MEMBRO_COMITE_PESQUISA, SigaaPapeis.MEMBRO_COMITE_CIENTIFICO_MONITORIA);

		obj.calcularMedia();
		obj.setDataAvaliacao(new Date());
		getGenericDAO().initialize(obj.getParecer());

		ListaMensagens lista = obj.validate();
		if (!isEmpty(lista.getErrorMessages())) {
			addMensagens(lista);
			return forward(ConstantesNavegacao.AVALIACAO_PROPOSTA_FORM_PARECER);
		}

		return forward(ConstantesNavegacao.AVALIACAO_PROPOSTA_CONFIRMAR_AVALIACAO);
	}
	
	
	/**
	 * Método usado para avaliação de membros do comitê.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/AvaliacaoAtividade/form.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String confirmarAvaliacaoMembroComite() throws SegurancaException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_EXTENSAO);

		try {
			ListaMensagens mensagens = new ListaMensagens();
			mensagens = obj.validate();
			
			if (!mensagens.isEmpty()) {
				addMensagens(mensagens);
				return null;
			}

			avaliarAtividadeExtensao();
			obj = new AvaliacaoAtividade();
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return forward(ConstantesNavegacao.AVALIACAO_PROPOSTA_LISTA);

		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}

		resetBean();
		return getSubSistema().getForward();

	}

	/** 
	 * Chama o processador para avaliar a atividade de extensão.
	 * Este método não é chamado por JSP.
	 *  
	 */
	private AvaliacaoAtividade avaliarAtividadeExtensao() throws NegocioException, ArqException {		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.AVALIAR_ATIVIDADE_EXTENSAO);
		mov.setObjMovimentado(obj);
		return execute(mov, getCurrentRequest());		
	}
	

	/**
	 * 
	 * Método usado para avaliação de membros do comitê.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/AvaliacaoAtividade/form.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String confirmarAvaliacaoPresidenteComite() throws SegurancaException {
		checkRole(SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO);
		
		try {
			ListaMensagens mensagens = new ListaMensagens();
			mensagens = obj.validate();
			
			if (!mensagens.isEmpty()) {
				addMensagens(mensagens);
				return null;
			}

			avaliarAtividadeExtensao();
			obj = new AvaliacaoAtividade();
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			FiltroAtividadesMBean fMBean = (FiltroAtividadesMBean)getMBean("filtroAtividades");
			fMBean.setTipoFiltro(FiltroAtividadesMBean.FILTRO_AVALIAR_ATIVIDADES);
			fMBean.filtrar();
			return forward(ConstantesNavegacao.AVALIACAO_PROPOSTA_LISTA_PRESIDENTE);

		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}

		resetBean();
		return getSubSistema().getForward();

	}

	/**
	 * Método utilizado no redirecionamento para página de 
	 * busca de avaliações após a remoção de alguma.
	 * 
	 */
	@Override
	protected String forwardInativar() {
	    try {
		localizarAvaliacoes();			
	    } catch (ArqException e) {
		tratamentoErroPadrao(e);
	    }
	    return ConstantesNavegacao.AVALIACAO_CONSULTAR_AVALIACOES;
	}
	
	/**
	 * Carrega avaliação e prepara MBeans para visualização.
	 * 
	 * Chamado por: 
	 * sigaa.war/extensao/AnalisarSolicitacaoReconsideracao/form.jsp
	 * sigaa.war/extensao/AvaliacaoAtividade/consultar_avaliacoes.jsp
	 * sigaa.war/extensao/AvaliacaoAtividade/consultar_avaliadores.jsp
	 * sigaa.war/extensao/AvaliacaoAtividade/form_presidente.jsp
	 * sigaa.war/extensao/AvaliacaoAtividade/form.jsp
	 * sigaa.war/extensao/AvaliacaoAtividade/lista_coordenador.jsp
	 * sigaa.war/extensao/AvaliacaoAtividade/lista_parecerista.jsp
	 * sigaa.war/extensao/AvaliacaoAtividade/lista.jsp
	 * 
	 * @return
	 */
	public String view() {
		Integer id = getParameterInt("idAvaliacao", 0);
		try {
			avaliacaoSelecionada = getGenericDAO().findByPrimaryKey(id, AvaliacaoAtividade.class);			
			notasItem = avaliacaoSelecionada.getNotasItem();			
			return forward(ConstantesNavegacao.AVALIACAO_PROPOSTA_VIEW);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}
	}

	/**
	 * Método utilizado para iniciar uma Consulta do avaliadores da Atividade
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSPs.</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarConsultarAvaliadoresAtividade() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		return forward(ConstantesNavegacao.AVALIACAO_CONSULTAR_AVALIADORES);
	}

	/**
	 * Método usado para redirecionar para pagina de consulta de avaliações.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/menu.jsp
	 * sigaa.war/portais/rh_plan/abas/extensao.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarConsultarAvaliacoesAtividade() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO , SigaaPapeis.PORTAL_PLANEJAMENTO);
		return forward(ConstantesNavegacao.AVALIACAO_CONSULTAR_AVALIACOES);
	}

	public Collection<AvaliacaoAtividade> getAvaliacoesParciais() {
		return avaliacoesParciais;
	}

	public void setAvaliacoesParciais(Collection<AvaliacaoAtividade> avaliacoesParciais) {
		this.avaliacoesParciais = avaliacoesParciais;
	}

	public AvaliacaoAtividade getAvaliacaoSelecionada() {
		return avaliacaoSelecionada;
	}

	public void setAvaliacaoSelecionada(AvaliacaoAtividade avaliacaoSelecionada) {
		this.avaliacaoSelecionada = avaliacaoSelecionada;
	}

	public String getA4jAviso() {
		return a4jAviso;
	}

	public void setA4jAviso(String aviso) {
		a4jAviso = aviso;
	}

	/**
	 * Avaliações realizadas por membros do comitê de extensão ou por
	 * pareceristas.
	 * 
	 * chamado por:
	 * sigaa.war/extensao/AvaliacaoAtividade/consultar_avaliacoes.jsp
	 * @throws ArqException 
	 * 
	 * @throws LimiteResultadosException
	 * 
	 */
	public void localizarAvaliacoes() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO , SigaaPapeis.PORTAL_PLANEJAMENTO);
		prepareMovimento(ArqListaComando.DESATIVAR);
		
		if (avaliacoesLocalizadas != null) {
			avaliacoesLocalizadas.clear();
		}

		/* Analisando filtros selecionados */

		String  tituloAtividade = null;
		Integer idTipoAtividade = null;
		Integer idUnidadeProponente = null;
		Integer idAreaCNPq = null;
		Integer idAreaTematicaPrincipal = null;
		Integer idServidorAtividade = null;
		Integer anoAtividade = null;
		Integer idTipoAvaliacao = null;
		Integer idServidorAvaliador = null;
		Integer idStatusAvaliacao = null;
		Integer idEdital = null;

		ListaMensagens lista = new ListaMensagens();

		// Definição dos filtros e validações
		if (checkBuscaTitulo) {
			tituloAtividade = buscaTituloAtividade;
		}
		if (checkBuscaTipoAtividade) {
			idTipoAtividade = buscaTipoAtividade;
		}
		if (checkBuscaUnidadeProponente) {
			idUnidadeProponente = buscaUnidade;
		}
		if (checkBuscaAreaCNPq) {
			idAreaCNPq = buscaAreaCNPq;
		}
		if (checkBuscaAreaTematicaPrincipal) {
			idAreaTematicaPrincipal = buscaAreaTematicaPrincipal;
		}
		if (checkBuscaServidorAtividade) {
			idServidorAtividade = servidorAtividade.getId();
		}
		if (checkBuscaAnoAtividade) {
			anoAtividade = buscaAnoAtividade;
		}
		if (checkBuscaTipoAvaliacao) {
			idTipoAvaliacao = buscaTipoAvaliacao;
		}
		if (checkBuscaServidorAvaliador) {
			idServidorAvaliador = servidorAvaliador.getId();
		} 
		if (checkBuscaStatusAvaliacao) {
			idStatusAvaliacao = buscaStatusAvaliacao;
		} 
		if(getCheckBuscaEdital()) {
			idEdital = getBuscaEdital();
		}

		if (!checkBuscaTitulo && !checkBuscaAreaCNPq
				&& !checkBuscaTipoAtividade && !checkBuscaUnidadeProponente
				&& !checkBuscaAreaTematicaPrincipal
				&& !checkBuscaServidorAtividade && !checkBuscaAnoAtividade
				&& !checkBuscaTipoAvaliacao && !checkBuscaServidorAvaliador
				&& !checkBuscaStatusAvaliacao && !getCheckBuscaEdital()) {

			lista.addErro("Selecione uma opção para efetuar a busca por avaliações.");

		} else {

			AvaliacaoExtensaoDao dao = null;
			dao = getDAO(AvaliacaoExtensaoDao.class);
			if (lista.isEmpty()) {

				try {

					avaliacoesLocalizadas = dao.filter(tituloAtividade,
							idTipoAtividade, idUnidadeProponente, idAreaCNPq,
							idAreaTematicaPrincipal, idServidorAtividade,
							anoAtividade, idTipoAvaliacao, idServidorAvaliador,
							idStatusAvaliacao, idEdital);
					
					if (avaliacoesLocalizadas.isEmpty()) {
					    addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
					}

				} catch (LimiteResultadosException e) {
					addMensagemErro(e.getMessage());
				}

				obj = new AvaliacaoAtividade();

			} else {
				addMensagens(lista);
			}

		}

	}

	/**
	 * Método que redireciona o usuário pra um Servlet capaz de exportar todas
	 * as atividades passíveis de avaliação pelo comitê de extensão. A lista é
	 * gerada em forma de planilha para possibilitar a manipulação de dados no
	 * excel. Esse método é normalmente chamado durante a avaliação final das
	 * ações feita pelo presidente do comitê de extensão.
	 * 
	 * Chamado por: 
	 * sigaa.war/extensao/AvaliacaoAtividade/lista_presidente.jsp
	 * 
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String exportarListaAcoes() throws ArqException {
		checkRole(SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO,
				SigaaPapeis.GESTOR_EXTENSAO);
		return redirect(ConstantesNavegacao.EXPORTAR_LISTA_FAEX_AVALIACAO_FINAL);
	}
	
	/**
	 * Método utilizado para atualizar a média
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/AvaliacaoAtividade/form_parecerista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String atualizarMedia() {
	    NotaItemMonitoria nota = (NotaItemMonitoria)htmlDataTable.getRowData();
	    try {
		double valorSubmetido = new Double(htmlNota.getSubmittedValue().toString().replace(',', '.'));

		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateMaxValue(valorSubmetido, nota.getItemAvaliacao().getNotaMaxima(), valorSubmetido + ", nota atribuída para '" + nota.getItemAvaliacao().getDescricao() + "'", lista);
		ValidatorUtil.validateMinValue(valorSubmetido, new Double(0), valorSubmetido + ", nota atribuída para '" + nota.getItemAvaliacao().getDescricao() + "'", lista);
		if (!isEmpty(lista.getErrorMessages())) {
		    addMensagensAjax(lista);
		    return null;
		}else {
		    //Atualiza a nota digitada na avaliação.
		    nota.setNota(valorSubmetido);
		    int index = obj.getNotasItem().indexOf(nota);
		    obj.getNotasItem().get(index).setNota(valorSubmetido);
		    obj.calcularMedia();
		}
	    }catch (Exception e) {
		addMensagemAjax(MensagensArquitetura.CONTEUDO_INVALIDO, "Nota atribuída para '" + nota.getItemAvaliacao().getDescricao() + "'");
	    }
	    return null;
	}

	public Collection<SelectItem> getAllTipoAvaliacaoCombo() {
		return getAll(TipoAvaliacao.class, "id", "descricao");
	}

	public Collection<SelectItem> getAllStatusAvaliacaoCombo() {
		return getAll(StatusAvaliacao.class, "id", "descricao");
	}

	/**
	 * Verifica se ação de extensão foi marcada como não atendendo aos critérios de avaliação do edital
	 * @param arg
	 * @throws DAOException
	 */
	public void criteriosAvaliacao(ActionEvent arg) throws DAOException{
		if ( !obj.isAtendeCriterios() && obj.getNotasItem() != null && !obj.getNotasItem().isEmpty() ) {
			for (NotaItemMonitoria nota : obj.getNotasItem()) {
				nota.setNota(0);
			}
			obj.setParecer(new TipoParecerAvaliacaoExtensao(TipoParecerAvaliacaoExtensao.REPROVADO));
		}
	}
	
	public Collection<AvaliacaoAtividade> getAvaliacoesLocalizadas() {
		return avaliacoesLocalizadas;
	}

	public void setAvaliacoesLocalizadas(
			Collection<AvaliacaoAtividade> avaliacoesLocalizadas) {
		this.avaliacoesLocalizadas = avaliacoesLocalizadas;
	}

	public boolean isCheckBuscaTitulo() {
		return checkBuscaTitulo;
	}

	public void setCheckBuscaTitulo(boolean checkBuscaTitulo) {
		this.checkBuscaTitulo = checkBuscaTitulo;
	}

	public boolean isCheckBuscaTipoAtividade() {
		return checkBuscaTipoAtividade;
	}

	public void setCheckBuscaTipoAtividade(boolean checkBuscaTipoAtividade) {
		this.checkBuscaTipoAtividade = checkBuscaTipoAtividade;
	}

	public boolean isCheckBuscaUnidadeProponente() {
		return checkBuscaUnidadeProponente;
	}

	public void setCheckBuscaUnidadeProponente(
			boolean checkBuscaUnidadeProponente) {
		this.checkBuscaUnidadeProponente = checkBuscaUnidadeProponente;
	}

	public boolean isCheckBuscaAreaCNPq() {
		return checkBuscaAreaCNPq;
	}

	public void setCheckBuscaAreaCNPq(boolean checkBuscaAreaCNPq) {
		this.checkBuscaAreaCNPq = checkBuscaAreaCNPq;
	}

	public boolean isCheckBuscaAreaTematicaPrincipal() {
		return checkBuscaAreaTematicaPrincipal;
	}

	public void setCheckBuscaAreaTematicaPrincipal(
			boolean checkBuscaAreaTematicaPrincipal) {
		this.checkBuscaAreaTematicaPrincipal = checkBuscaAreaTematicaPrincipal;
	}

	public boolean isCheckBuscaServidorAtividade() {
		return checkBuscaServidorAtividade;
	}

	public void setCheckBuscaServidorAtividade(
			boolean checkBuscaServidorAtividade) {
		this.checkBuscaServidorAtividade = checkBuscaServidorAtividade;
	}

	public boolean isCheckBuscaAnoAtividade() {
		return checkBuscaAnoAtividade;
	}

	public void setCheckBuscaAnoAtividade(boolean checkBuscaAnoAtividade) {
		this.checkBuscaAnoAtividade = checkBuscaAnoAtividade;
	}

	public boolean isCheckBuscaTipoAvaliacao() {
		return checkBuscaTipoAvaliacao;
	}

	public void setCheckBuscaTipoAvaliacao(boolean checkBuscaTipoAvaliacao) {
		this.checkBuscaTipoAvaliacao = checkBuscaTipoAvaliacao;
	}

	public boolean isCheckBuscaServidorAvaliador() {
		return checkBuscaServidorAvaliador;
	}

	public void setCheckBuscaServidorAvaliador(
			boolean checkBuscaServidorAvaliador) {
		this.checkBuscaServidorAvaliador = checkBuscaServidorAvaliador;
	}

	public boolean isCheckBuscaStatusAvaliacao() {
		return checkBuscaStatusAvaliacao;
	}

	public void setCheckBuscaStatusAvaliacao(boolean checkBuscaStatusAvaliacao) {
		this.checkBuscaStatusAvaliacao = checkBuscaStatusAvaliacao;
	}

	public String getBuscaTituloAtividade() {
		return buscaTituloAtividade;
	}

	public void setBuscaTituloAtividade(String buscaTituloAtividade) {
		this.buscaTituloAtividade = buscaTituloAtividade;
	}

	public Integer getBuscaTipoAtividade() {
		return buscaTipoAtividade;
	}

	public void setBuscaTipoAtividade(Integer buscaTipoAtividade) {
		this.buscaTipoAtividade = buscaTipoAtividade;
	}

	public Integer getBuscaUnidade() {
		return buscaUnidade;
	}

	public void setBuscaUnidade(Integer buscaUnidade) {
		this.buscaUnidade = buscaUnidade;
	}

	public Integer getBuscaAreaCNPq() {
		return buscaAreaCNPq;
	}

	public void setBuscaAreaCNPq(Integer buscaAreaCNPq) {
		this.buscaAreaCNPq = buscaAreaCNPq;
	}

	public Integer getBuscaAreaTematicaPrincipal() {
		return buscaAreaTematicaPrincipal;
	}

	public void setBuscaAreaTematicaPrincipal(Integer buscaAreaTematicaPrincipal) {
		this.buscaAreaTematicaPrincipal = buscaAreaTematicaPrincipal;
	}

	public Integer getBuscaAnoAtividade() {
		return buscaAnoAtividade;
	}

	public void setBuscaAnoAtividade(Integer buscaAnoAtividade) {
		this.buscaAnoAtividade = buscaAnoAtividade;
	}

	public Integer getBuscaTipoAvaliacao() {
		return buscaTipoAvaliacao;
	}

	public void setBuscaTipoAvaliacao(Integer buscaTipoAvaliacao) {
		this.buscaTipoAvaliacao = buscaTipoAvaliacao;
	}

	public Integer getBuscaStatusAvaliacao() {
		return buscaStatusAvaliacao;
	}

	public void setBuscaStatusAvaliacao(Integer buscaStatusAvaliacao) {
		this.buscaStatusAvaliacao = buscaStatusAvaliacao;
	}

	public Servidor getServidorAvaliador() {
		return servidorAvaliador;
	}

	public void setServidorAvaliador(Servidor servidorAvaliador) {
		this.servidorAvaliador = servidorAvaliador;
	}

	public Servidor getServidorAtividade() {
		return servidorAtividade;
	}

	public void setServidorAtividade(Servidor servidorAtividade) {
		this.servidorAtividade = servidorAtividade;
	}

	public Collection<AvaliacaoAtividade> getAtividadesPendentesAvaliacao() {
		return atividadesPendentesAvaliacao;
	}

	public void setAtividadesPendentesAvaliacao(
			Collection<AvaliacaoAtividade> atividadesPendentesAvaliacao) {
		this.atividadesPendentesAvaliacao = atividadesPendentesAvaliacao;
	}

	public Boolean getCheckBuscaEdital() {
		return checkBuscaEdital;
	}

	public void setCheckBuscaEdital(Boolean checkBuscaEdital) {
		this.checkBuscaEdital = checkBuscaEdital;
	}

	public Integer getBuscaEdital() {
		return buscaEdital;
	}

	public void setBuscaEdital(Integer buscaEdital) {
		this.buscaEdital = buscaEdital;
	}

	public Collection<NotaItemMonitoria> getNotasItem() {
		return notasItem;
	}

	public void setNotasItem(Collection<NotaItemMonitoria> notasItem) {
		this.notasItem = notasItem;
	}
	
	
	/**
	 * Método utilizado para redirecionar para a página de avaliação
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<lu>/sigaa.war/extensao/AvaliacaoAtividade/confirmar_avaliacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String redirecionaPaginaAvaliacao() {
		return forward(ConstantesNavegacao.AVALIACAO_PROPOSTA_FORM_PARECER);
	}

	public HtmlDataTable getHtmlDataTable() {
	    return htmlDataTable;
	}

	public void setHtmlDataTable(HtmlDataTable htmlDataTable) {
	    this.htmlDataTable = htmlDataTable;
	}

	public HtmlInputText getHtmlNota() {
	    return htmlNota;
	}

	public void setHtmlNota(HtmlInputText htmlNota) {
	    this.htmlNota = htmlNota;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

}