/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/02/2008
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.AvaliacaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.AvaliadorAtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroComissaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.extensao.dominio.AreaTematica;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.AvaliacaoAtividade;
import br.ufrn.sigaa.extensao.dominio.AvaliadorAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.DistribuicaoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAvaliacao;
import br.ufrn.sigaa.extensao.negocio.CadastroExtensaoMov;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/*******************************************************************************
 * Mbean respons�vel por gerenciar a distribui��o das a��es de extens�o para
 * an�lise dos membros do comit� de extens�o e pareceristas do comit� ad hoc.
 * <br>
 * Um Gestor de extens�o busca pela a��o que deseja distribuir e ap�s selecion�-la
 * distribui para uma lista de avaliadores.
 * 
 * 
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Component("distribuicaoExtensao")
@Scope("session")
public class DistribuicaoAtividadeExtensaoMBean extends
		SigaaAbstractController<DistribuicaoAtividadeExtensao> {

	/** Atributo utilizado para representar o tipo de Avalia��o */
	private TipoAvaliacao tipoAvaliacao;
	/** Atributo utilizado para representar a �rea Tem�tica */
	private AreaTematica areaTematica;

	/** Atributo utilizado para representar a Avalia��o da Atividade */
	private AvaliacaoAtividade avaliacao = new AvaliacaoAtividade();

	/** atividade de extens�o que possui outras atividades agregadas (programa ou produto */
	private AtividadeExtensao atividadePai = new AtividadeExtensao();
	
	public DistribuicaoAtividadeExtensaoMBean() {
		obj = new DistribuicaoAtividadeExtensao();

		avaliacao.setMembroComissao(new MembroComissao());
	}

	/**
	 * M�todo usado para fazer a distribui��o de a��es aos pareceristas.
	 *
	 * sigaa.war/extensao/DistribuicaoPareceristaExtensao/form.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String distribuirOutraAtividadeParecerista()
			throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		resetBean();
		return forward(ConstantesNavegacao.DISTRIBUICAOPARECERISTA_LISTA);
	}

	
	/**
	 * 
	 * M�todo usado para fazer distribui��o de a��es para membros do Comit� de Extens�o.
	 *
	 * sigaa.war/extensao/DistribuicaoComiteExtensao/form.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String distribuirOutraAtividadeComite() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		resetBean();
		return forward(ConstantesNavegacao.DISTRIBUICAOCOMITE_LISTA);
	}

	/**
	 * Retira uma a��o de extens�o da avaliac�o do membro do comit�
	 * 
	 * sigaa.war/extensao/DistribuicaoPareceristaExtensao/form.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String removerAvaliacaoParecerista() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);

		try {

			Integer idAvaliador = getParameterInt("idAvaliador", 0);
			Integer idAvaliacao = getParameterInt("idAvaliacao", 0);

			Collection<AvaliacaoAtividade> avaliacoes = obj.getAtividade().getAvaliacoes();

			// Avalia��o j� est� no banco...
			if (idAvaliacao > 0) {

				for (Iterator<AvaliacaoAtividade> it = avaliacoes.iterator(); it
						.hasNext();) {
					AvaliacaoAtividade aa = it.next();
					if (aa.getId() == idAvaliacao) {
						obj.getAvaliacoesRemovidas().add(aa);
						it.remove();
					}
				}

				// Avalia��o ainda transient (n�o remove do banco...)
			} else {

				for (Iterator<AvaliacaoAtividade> it = avaliacoes.iterator(); it
						.hasNext();) {
					AvaliacaoAtividade aa = it.next();

					if (aa.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA) {
						if (aa.getAvaliadorAtividadeExtensao().getId() == idAvaliador) {
							it.remove(); // Remove a avalia��o da view
							break;
						}
					}
				}

			}

		} catch (Exception e) {
			notifyError(e);
		}
		filtrarPareceristasDisponiveis(obj.getAtividade().getAreaTematicaPrincipal().getId());
		return forward(ConstantesNavegacao.DISTRIBUICAOPARECERISTA_FORM);

	}

	/**
	 * Retira uma a��o de extens�o da avalia��o do membro do comit�
	 * 
	 * sigaa.war/extensao/DistribuicaoComiteExtensao/form.jsp
	 * 
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String removerAvaliacaoComite() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);

		try {

			Integer idMembroComite = getParameterInt("idMembroComite", 0);
			Integer idAvaliacao = getParameterInt("idAvaliacao", 0);

			Collection<AvaliacaoAtividade> avaliacoes = obj.getAtividade().getAvaliacoes();

			// Avalia��o j� est� no banco...
			if (idAvaliacao > 0) {

				for (Iterator<AvaliacaoAtividade> it = avaliacoes.iterator(); it.hasNext();) {
					AvaliacaoAtividade aa = it.next();

					if (aa.getId() == idAvaliacao) {
						// Marca pra remover do banco
						obj.getAvaliacoesRemovidas().add(aa);
						it.remove();
					}
				}

				// Avalia��o ainda transient
			} else {

				for (Iterator<AvaliacaoAtividade> it = avaliacoes.iterator(); it.hasNext();) {
					AvaliacaoAtividade aa = it.next();

					if (aa.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_COMITE) {
						if (aa.getMembroComissao().getId() == idMembroComite) {
							it.remove(); // Remove a avalia��o da view
							break;
						}
					}
				}

			}

		} catch (Exception e) {
			notifyError(e);
		}

		filtrarMembrosComiteDisponiveis();
		return redirectMesmaPagina();

	}

	/**
	 * Cria uma avalia��o para o parecerista selecionado
	 * 
	 * 
	 * sigaa.war/extensao/DistribuicaoPareceristaExtensao/form.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String adicionarAvaliacaoParecerista() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);

		try {

			int idAvaliador = getParameterInt("idAvaliador", 0);

			if (idAvaliador > 0) {

				AvaliacaoExtensaoDao daoAE = getDAO(AvaliacaoExtensaoDao.class);

				// Pega o avaliador completo do banco
				AvaliadorAtividadeExtensao avaliador = daoAE.findByPrimaryKey(idAvaliador, AvaliadorAtividadeExtensao.class);
				// Recuperando o usu�rio
				avaliador.getServidor().setPrimeiroUsuario(daoAE.findByExactField(Usuario.class, "pessoa.id", avaliador.getServidor().getPessoa().getId(), true));
				// Criar Avalia��o
				AvaliacaoAtividade avaliacaoAtividade = new AvaliacaoAtividade();

				avaliacaoAtividade.setAvaliadorAtividadeExtensao(avaliador);
				avaliacaoAtividade.setAtividade(obj.getAtividade());

				avaliacaoAtividade.setDataDistribuicao(new Date());
				avaliacaoAtividade.setStatusAvaliacao(daoAE.findByPrimaryKey(StatusAvaliacao.AGUARDANDO_AVALIACAO,	StatusAvaliacao.class));
				avaliacaoAtividade.setTipoAvaliacao(new TipoAvaliacao(TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA));
				avaliacaoAtividade.setRegistroEntradaDistribuicao(getUsuarioLogado().getRegistroEntrada());

				obj.getAtividade().getAvaliacoes().add(avaliacaoAtividade);
				filtrarPareceristasDisponiveis(obj.getAtividade().getAreaTematicaPrincipal().getId());

			} else {
				addMensagemErro("Avaliador Selecionado � inv�lido!");
			}

		} catch (DAOException e) {
			notifyError(e);
			addMensagemErro("Erro ao Adicionar avaliador!");
		}

		return null;

	}

	/**
	 * Cria uma avalia��o para o membro do comite de extens�o selecionado
	 * 
	 * 
	 * sigaa.war/extensao/DistribuicaoComiteExtensao/form.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String adicionarAvaliacaoComite() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);

		try {

			int idMembroComite = getParameterInt("idMembroComite", 0);

			if (idMembroComite > 0) {

				AvaliacaoExtensaoDao daoAE = getDAO(AvaliacaoExtensaoDao.class);

				// Pega o membro da comiss�o completo do banco
				MembroComissao membro = daoAE.findByPrimaryKey(idMembroComite, MembroComissao.class);
				// Recuperando o usu�rio
				membro.getServidor().setPrimeiroUsuario(daoAE.findByExactField(Usuario.class, "pessoa.id", membro.getServidor().getPessoa().getId(), true));
				// Criar Avalia��o
				AvaliacaoAtividade avaliacaoAtividade = new AvaliacaoAtividade();

				avaliacaoAtividade.setMembroComissao(membro);
				avaliacaoAtividade.setAtividade(obj.getAtividade());

				avaliacaoAtividade.setDataDistribuicao(new Date());
				avaliacaoAtividade.setStatusAvaliacao(daoAE.findByPrimaryKey(StatusAvaliacao.AGUARDANDO_AVALIACAO,	StatusAvaliacao.class));
				avaliacaoAtividade.setTipoAvaliacao(new TipoAvaliacao(TipoAvaliacao.AVALIACAO_ACAO_COMITE));
				avaliacaoAtividade.setRegistroEntradaDistribuicao(getUsuarioLogado().getRegistroEntrada());

				obj.getAtividade().getAvaliacoes().add(avaliacaoAtividade);
				filtrarMembrosComiteDisponiveis();

			} else {

				addMensagemErro("Avaliador Selecionado � inv�lido!");

			}

		} catch (DAOException e) {
			notifyError(e);
			addMensagemErro("Erro ao Adicionar avaliador!");
		}

		return null;

	}

	/**
	 * Auxilia na distribui��o das atividades para o comite de extens�o que
	 * pertencem ao programa/produto.
	 * 
	 * sigaa.war/extensao/DistribuicaoComiteExtensao/lista_atividades_vinculadas.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String distribuirAtividadesVinculadasComite() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);

		try {
			int id = getParameterInt("id");
			// seta a atividade escolhida na distribui��o
			atividadePai = getGenericDAO().findByPrimaryKey(id,	AtividadeExtensao.class);
			return forward(ConstantesNavegacao.DISTRIBUICAOCOMITE_LISTA_ATIVIDADES_VINCULADAS);

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro buscar projeto selecionado");
		}

		return null;
	}

	/**
	 * Auxilia na distribui��o das atividades para os pareceristas de extens�o
	 * que pertencem ao programa/produto.
	 * 
	 * sigaa.war/extensao/DistribuicaoPareceristaExtensao/lista_atividades_vinculadas.jsp
	 * sigaa.war/extensao/DistribuicaoPareceristaExtensao/lista.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String distribuirAtividadesVinculadasParecerista() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);

		try {
			int id = getParameterInt("id");
			// Seta a atividade escolhida na distribui��o
			atividadePai = getGenericDAO().findByPrimaryKey(id,AtividadeExtensao.class);
			return forward(ConstantesNavegacao.DISTRIBUICAOPARECERISTA_LISTA_ATIVIDADES_VINCULADAS);

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro buscar projeto selecionado");
		}

		return null;
	}

	/**
	 * Seleciona a a��o de extens�o que sera distribuida para os membros do
	 * comit� de extens�o
	 * 
	 * sigaa.war/extensao/DistribuicaoComiteExtensao/lista_atividades_vinculadas.jsp
	 * sigaa.war/extensao/DistribuicaoComiteExtensao/lista.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String selecionarAtividadeComite() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);

		try {

			// Seta o tipo de avalia��o que dever� ser feita ap�s a distribui��o
			GenericDAO dao = getGenericDAO();
			setTipoAvaliacao(dao.findByPrimaryKey(TipoAvaliacao.AVALIACAO_ACAO_COMITE, TipoAvaliacao.class));

			prepareMovimento(SigaaListaComando.DISTRIBUIR_ATIVIDADE_EXTENSAO_MANUAL);
			int id = getParameterInt("id");

			// Seta a atividade escolhida na distribui��o
			obj.setAtividade(dao.findByPrimaryKey(id, AtividadeExtensao.class));
			obj.getAtividade().getAvaliacoes().iterator();
			dao.refresh(obj.getAtividade().getProjeto().getCoordenador());
			obj.setTipoAvaliacao(new TipoAvaliacao(TipoAvaliacao.AVALIACAO_ACAO_COMITE));
			filtrarMembrosComiteDisponiveis();

			return forward(ConstantesNavegacao.DISTRIBUICAOCOMITE_FORM);

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro buscar projeto selecionado");
		}

		return null;
	}

	/**
	 * Seleciona a a��o de extens�o que ser� distribu�da para o avaliador ad
	 * hoc (parecerista)
	 * 
	 * sigaa.war/extensao/DistribuicaoPareceristaExtensao/lista_atividades_vinculadas.jsp
	 * sigaa.war/extensao/DistribuicaoPareceristaExtensao/lista.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String selecionarAtividadeParecerista() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);

		try {

			// Seta o tipo de avalia��o que dever� ser feita ap�s a distribui��o
			GenericDAO dao = getGenericDAO();
			setTipoAvaliacao(dao.findByPrimaryKey(TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA,	TipoAvaliacao.class));

			prepareMovimento(SigaaListaComando.DISTRIBUIR_ATIVIDADE_EXTENSAO_MANUAL);
			int id = getParameterInt("id");

			// Seta a atividade escolhida na distribui��o
			obj.setAtividade(dao.findByPrimaryKey(id, AtividadeExtensao.class));
			obj.getAtividade().getAvaliacoes().iterator();
			dao.refresh(obj.getAtividade().getProjeto().getCoordenador());
			obj.setTipoAvaliacao(new TipoAvaliacao(TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA));
			filtrarPareceristasDisponiveis(obj.getAtividade().getAreaTematicaPrincipal().getId());

			return forward(ConstantesNavegacao.DISTRIBUICAOPARECERISTA_FORM);

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro buscar projeto selecionado");
		}

		return null;

	}

	/**
	 * Lista todos os avaliadores que podem avaliar a atividade selecionada
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>N�o � chamada por JSPs.</li>
	 * </ul>
	 * 
	 */
	private void filtrarPareceristasDisponiveis(int idAreaTematicaPrincipal) {
		try {
			AvaliadorAtividadeExtensaoDao dao = getDAO(AvaliadorAtividadeExtensaoDao.class);

			/** @negocio: Somente avaliadores da mesma �rea da a��o de extens�o podem avali�-la. */
			obj.setAvaliadoresPossiveis(dao.findByAreaTematica(idAreaTematicaPrincipal));

			/** @negocio: Retira da lista os avaliadores que j� est�o avaliando a atividade */
			obj.getAtividade().getAvaliacoes().iterator();
			for (AvaliacaoAtividade avaAtv : obj.getAtividade().getAvaliacoes()) {
				if ((avaAtv.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA)
						&& (avaAtv.getStatusAvaliacao().getId() != StatusAvaliacao.AVALIACAO_CANCELADA))
					obj.getAvaliadoresPossiveis().remove(avaAtv.getAvaliadorAtividadeExtensao());
			}

		} catch (DAOException e) {
			notifyError(e);
		}
	}

	/**
	 * Lista todos os membros do comite que podem avaliar a atividade selecionada
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>N�o � chamada por JSPs.</li>
	 * </ul>
	 * 
	 */
	private void filtrarMembrosComiteDisponiveis() {
		try {
			MembroComissaoDao dao = getDAO(MembroComissaoDao.class);

			// Todos os avaliadores ativos da �rea da a��o
			obj.setMembrosComitePossiveis(dao.findByComissao(MembroComissao.MEMBRO_COMISSAO_EXTENSAO));

			// Retira da lista os avaliadores que j� est�o avaliando a atividade
			obj.getAtividade().getAvaliacoes().iterator();
			for (AvaliacaoAtividade avaAtv : obj.getAtividade().getAvaliacoes()) {
				if ((avaAtv.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_COMITE)
						&& (avaAtv.getStatusAvaliacao().getId() != StatusAvaliacao.AVALIACAO_CANCELADA))
					obj.getMembrosComitePossiveis().remove(avaAtv.getMembroComissao());
			}
		} catch (DAOException e) {
			notifyError(e);
		}
	}

	/**
	 * 
	 * Confere as regras e distribui os projetos para os avaliadores. Verifica a
	 * situa��o de cada avaliador da lista e muda sua situa��o junto ao projeto
	 * atual
	 * 
	 * sigaa.war/extensao/DistribuicaoPareceristaExtensao/form.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String distribuir() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		try {
			CadastroExtensaoMov mov = new CadastroExtensaoMov();

			// setando a nova situacao do projeto
			obj.getAtividade().setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AVALIACAO));
			mov.setCodMovimento(SigaaListaComando.DISTRIBUIR_ATIVIDADE_EXTENSAO_MANUAL);
			mov.setDistribuicaoExtensao(obj);
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			String result = null;
			
			if (getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA) {
				result = forward(ConstantesNavegacao.DISTRIBUICAOPARECERISTA_LISTA);
			}
			if (getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_COMITE) {
				result = forward(ConstantesNavegacao.DISTRIBUICAOCOMITE_LISTA);
			}
			
			//atualizando lista de a��es para distribui��o.
			((FiltroAtividadesMBean)getMBean("filtroAtividades")).filtrar();
			
			return result;
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
			
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}
		return null;

	}
	
	/**
	 * 
	 * Confere as regras e distribui automaticamente os projetos para os avaliadores. Verifica a
	 * situa��o de cada avaliador da lista e muda sua situa��o junto ao projeto
	 * atual
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/extensao/DistribuicaoPareceristaExtensao/lista_auto.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String preDistribuirAuto() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		try {
			FiltroAtividadesMBean fBean = ((FiltroAtividadesMBean) getMBean("filtroAtividades"));
			
			// selecionando atividades para distribui��o
			Collection<AtividadeExtensao> atividades = fBean.getResultadosBusca();
			
			obj.setAtividades(new ArrayList<AtividadeExtensao>());
			for (AtividadeExtensao atv : atividades) {
				if (atv.isSelecionado()){
					obj.getAtividades().add(atv);
				}
			}
			obj.setTipoAvaliacao(new TipoAvaliacao(TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA));
			obj.setAreaTematica(new AreaTematica(fBean.getBuscaAreaTematicaPrincipal()));
			
			if (ValidatorUtil.isEmpty(obj.getAtividades())){
				addMensagemErro("Nenhuma A��o de Extens�o foi selecionada.");
				return null;
			}
			
			prepareMovimento(SigaaListaComando.DISTRIBUIR_ATIVIDADE_EXTENSAO_AUTO);			
			return forward(ConstantesNavegacao.DISTRIBUICAOPARECERISTA_AUTO_FORM);

		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}
		return null;

	}
	
	/**
	 * Confirma a distribui��o de a��es de extens�o automaticamente.
	 * 
 	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/extensao/DistribuicaoPareceristaExtensao/form_auto.jsp</li>
	 * </ul>
 
	 * @return
	 * @throws SegurancaException 
	 */
	public String confirmarDistribuicaoAuto() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		try {

			CadastroExtensaoMov mov = new CadastroExtensaoMov();
			mov.setCodMovimento(SigaaListaComando.DISTRIBUIR_ATIVIDADE_EXTENSAO_AUTO);
			mov.setDistribuicaoExtensao(obj);
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return novaDistribuicaoAuto();
			
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
			
		} catch (ArqException e) {
			addMensagemErro(e.getMessage());
			return null;
			
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}
		return null;
	}
	
	
	/**
	 * Prepara o sistema para uma nova distribui��o autom�tica
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String novaDistribuicaoAuto() throws ArqException {
		obj = new DistribuicaoAtividadeExtensao();
		FiltroAtividadesMBean fBean = ((FiltroAtividadesMBean) getMBean("filtroAtividades"));
		fBean.filtrar();
		return forward(ConstantesNavegacao.DISTRIBUICAOPARECERISTA_AUTO_LISTA);
	}


	public TipoAvaliacao getTipoAvaliacao() {
		return tipoAvaliacao;
	}

	public void setTipoAvaliacao(TipoAvaliacao tipoAvaliacao) {
		this.tipoAvaliacao = tipoAvaliacao;
	}

	/**
	 * M�todo utilizado para checar o papel do usu�rio que acessa.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamado por JSP(s)</li>
	 * </ul>
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
	}

	public AreaTematica getAreaTematica() {
		return areaTematica;
	}

	public void setAreaTematica(AreaTematica areaTematica) {
		this.areaTematica = areaTematica;
	}

	public AtividadeExtensao getAtividadePai() {
		return atividadePai;
	}

	public void setAtividadePai(AtividadeExtensao atividadePai) {
		this.atividadePai = atividadePai;
	}

	public AvaliacaoAtividade getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(AvaliacaoAtividade avaliacao) {
		this.avaliacao = avaliacao;
	}

}
