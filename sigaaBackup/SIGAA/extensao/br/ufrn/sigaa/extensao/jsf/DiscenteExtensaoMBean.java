/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/02/2008
 *
 */
package br.ufrn.sigaa.extensao.jsf; 

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.DiscenteExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.InscricaoSelecaoExtensaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoSelecaoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoSituacaoDiscenteExtensao;
import br.ufrn.sigaa.extensao.negocio.CadastroExtensaoMov;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.portal.jsf.PortalDocenteMBean;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;
 
/*******************************************************************************
 * MBean responsável por realizar as operações sobre os Discentes de Extensão.
 * 
 * Buscas de discentes e Cadastros dos resultados das seleções realizadas com os
 * discentes que se inscreveram através do portal do discente.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Component("discenteExtensao")
@Scope("session")
public class DiscenteExtensaoMBean extends
		SigaaAbstractController<DiscenteExtensao> {	
	
	/** Usado para armazenar dados de busca. */
	private AtividadeExtensao atividade = new AtividadeExtensao();

	/** Usado para armazenar dados de busca. */
	private Collection<InscricaoSelecaoExtensao> inscricoesAtualizar = new ArrayList<InscricaoSelecaoExtensao>();

	/** Usado para armazenar dados de busca. */
	private Collection<DiscenteExtensao> discentesExtensao = new ArrayList<DiscenteExtensao>();

	/** Auxiliar de tela de busca.  */
	private Curso curso = new Curso();

	/** Auxiliar de tela de busca.  */
	private TipoSituacaoDiscenteExtensao situacaoDiscenteExtensao = new TipoSituacaoDiscenteExtensao();

	/** Auxiliar de tela de busca.  */
	private TipoVinculoDiscente vinculoDiscente = new TipoVinculoDiscente();
	
	/** Auxiliar de tela de busca.  */
	private boolean checkBuscaAnoInicioBolsa;
	
	/** Auxiliar de tela de busca. */
	private boolean checkBuscaAnoInicioFinalizacao;	

	/** Auxiliar de tela de busca. */
	private boolean checkBuscaEdital;

	/** Auxiliar de tela de busca. */
	private boolean checkBuscaTituloAtividade;

	/** Auxiliar de tela de busca. */
	private boolean checkBuscaSituacao;

	/** Auxiliar de tela de busca. */
	private boolean checkBuscaVinculo;

	/** Auxiliar de tela de busca. */
	private boolean checkGerarRelatorio;

	/** Auxiliar de tela de busca. */
	private boolean checkBuscaDiscente;

	/** Auxiliar de tela de busca. */
	private boolean checkBuscaAno;
	
	/** Auxiliar de tela de busca. */
	private boolean checkBuscaAnoInicio;
	
	/** Auxiliar de tela de busca. */
	private boolean checkBuscaPeriodoCadastro;
	
	/** Usado para armazenar informação digitada na tela de busca. */
	private Date dataFim = new Date();
	
	/** Usado para armazenar informação digitada na tela de busca. */
	private Date dataInicioBolsa = new Date();
	
	/** Usado para armazenar informação digitada na tela de busca. */
	private Date dataFimBolsa = new Date();
	
	/** Usado para armazenar informação digitada na tela de busca. */
	private Date dataInicioFinalizacao = new Date();
	
	/** Usado para armazenar informação digitada na tela de busca. */
	private Date dataFimFinalizacao = new Date();	

	/** Usado para armazenar informação digitada na tela de busca. */
	private Date dataInicio = new Date();
	
	/** Usado para armazenar informação digitada na tela de busca. */
	private Date dataInicioCadastro;
	
	/** Usado para armazenar informação digitada na tela de busca. */
	private Date dataFimCadastro;
	
	/** Usado para armazenar informação digitada na tela de busca. */
	private boolean checkBuscaDiscenteAtivo; 

	/** Usado para armazenar informação digitada na tela de busca. */
	private boolean checkBuscaCurso;

	/** Usado para armazenar informação digitada na tela de busca. */
	private Discente discente = new Discente();

	/** Usado para armazenar informação digitada na tela de busca. */
	private String buscaTituloAtividade;

	/** Usado para armazenar informação digitada na tela de busca. */
	private int anoReferencia = CalendarUtils.getAnoAtual();

	/** Usado para armazenar informação digitada na tela de busca. */
	private int buscaEdital;

	/** Usado para armazenar informação digitada na tela de busca. */
	private boolean discenteAtivo;

	/** Se é pra ordenar pelo período que assumio a bolsa. */
	private boolean checkOrdenarDataCadastro = false;
	
	/** Construtor padrão. */ 
	public DiscenteExtensaoMBean() {
		obj = new DiscenteExtensao();
	}

	/**
	 * Retorna somente os tipos de vínculo que fazem um discente ativo (BOLSISTA
	 * e NÂO REMUNERADO)...
	 * 
	 * Não é chamado por JSPs.
	 * 
	 * @return
	 */
	public Collection<SelectItem> getTiposAtivosDiscenteCombo() {

		Collection<SelectItem> tiposAtivosDiscenteCombo = new ArrayList<SelectItem>();
		tiposAtivosDiscenteCombo.add(new SelectItem(
				TipoVinculoDiscente.EXTENSAO_BOLSISTA_INTERNO, "BOLSISTA FAEX"));
		tiposAtivosDiscenteCombo.add(new SelectItem(
				TipoVinculoDiscente.EXTENSAO_BOLSISTA_EXTENO, "BOLSISTA EXTERNO"));
		tiposAtivosDiscenteCombo.add(new SelectItem(
				TipoVinculoDiscente.EXTENSAO_VOLUNTARIO, "VOLUNTÁRIO"));
		tiposAtivosDiscenteCombo.add(new SelectItem(
				TipoVinculoDiscente.EXTENSAO_EM_ATIVIDADE_CURRICULAR,
				"EM ATIVIDADE CURRICULAR"));

		return tiposAtivosDiscenteCombo;

	}

	/**
	 * Carrega a ação para o cadastro de novos resultados de seleção e a lista
	 * de resultados de seleções já realizadas para aquela ação.
	 * 
	 * sigaa.war/extensao/Atividade/lista_minhas_atividades.jsp
	 * sigaa.war/pesquisa/invencao/lista_minhas_invencoes.jsp
	 * 
	 * @return
	 */
	public String iniciarCadatroResultadoSelecao() {

		try {

			Integer idAtividade = getParameterInt("id");
			atividade = getGenericDAO().findByPrimaryKey(idAtividade,
					AtividadeExtensao.class);

			// TODO Atualizar total de bolsas já distribuidas

			for (Iterator<InscricaoSelecaoExtensao> it = atividade
					.getInscricoesSelecao().iterator(); it.hasNext();) {
				InscricaoSelecaoExtensao insc = it.next();
				if (insc.getSituacaoDiscenteExtensao().getId() != TipoSituacaoDiscenteExtensao.INSCRITO_PROCESSO_SELETIVO)
					it.remove(); // Remove os já selecionados...
			}

			prepareMovimento(SigaaListaComando.CADASTRAR_SELECAO_EXTENSAO);

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
		}

		setConfirmButton("Confirmar");

		return forward(ConstantesNavegacao.DISCENTEEXTENSAO_FORM);

	}

	/**
	 * Adiciona o resultado de uma seleção à lista de discentes da ação de
	 * extensão
	 * 
	 * sigaa.war/extensao/DiscenteExtensao/form.jsp
	 * 
	 * @return
	 */
	public String adicionarResultadoSelecao() {

		try {

			boolean selecionouDiscente = false;

			// Pega todas as notas cadastradas
			for (Iterator<InscricaoSelecaoExtensao> it = atividade
					.getInscricoesSelecao().iterator(); it.hasNext();) {

				InscricaoSelecaoExtensao insc = it.next();

				if (insc.isSelecionado()) {
					selecionouDiscente = true;

					if ("".equals(insc.getJustificativa().trim())) {
						addMensagemErro("Justificativa para a seleção do discente deve ser informada.");
						return null;
					}

					// Só inclui bolsistas e voluntários
					if ((insc.getTipoVinculo().getId() == TipoVinculoDiscente.EXTENSAO_BOLSISTA_INTERNO)
							|| (insc.getTipoVinculo().getId() == TipoVinculoDiscente.EXTENSAO_VOLUNTARIO)) {

						obj = new DiscenteExtensao();
						obj.setAtivo(true);
						obj
								.setSituacaoDiscenteExtensao(new TipoSituacaoDiscenteExtensao(
										TipoSituacaoDiscenteExtensao.ATIVO));

						// Criando um discente com tipo de vínculo compatível
						// com a inscrição feita pelo aluno (bolsista ou
						// voluntário)
						obj.setTipoVinculo(insc.getTipoVinculo());
						obj.setDataCadastro(new Date());
						obj.setDiscente(insc.getDiscente());
						obj.setAtividade(atividade);

						// Adiciona o discente na lista de resultados da
						// prova....
						atividade.getDiscentesSelecionados().add(obj);

					}

					if (inscricoesAtualizar.add(insc))
						it.remove(); // Remove o adicionado

				}
			}

			if (!selecionouDiscente)
				addMessage(
						"Selecione um discente usando as caixas de seleção ao lado e click no botão [Incluir Discentes na Seleção] para cadastrar o resultado.",
						TipoMensagemUFRN.INFORMATION);

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
		}

		return null;
	}

	/**
	 * Cadastra o resultado da seleção, notas dos alunos na prova....
	 * 
	 * sigaa.war/extensao/DiscenteExtensao/form.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	@Override
	public String cadastrar() throws DAOException {

		CadastroExtensaoMov mov = new CadastroExtensaoMov();
		mov.setDiscentesExtensao(atividade.getDiscentesSelecionados());
		mov.setInscricoesSelecao(inscricoesAtualizar);

		mov.setCodMovimento(SigaaListaComando.CADASTRAR_SELECAO_EXTENSAO);

		try {

			execute(mov, getCurrentRequest());
			addMessage("Operação realizada com sucesso!",
					TipoMensagemUFRN.INFORMATION);

			// Limpa lista de seleções marcadas para remoção
			obj = new DiscenteExtensao();

		} catch (Exception e) {
			addMensagemErro("Erro Inesperado: " + e.getMessage());
			notifyError(e);
		}

		resetBean();

		return forward(PortalDocenteMBean.PORTAL_DOCENTE);

	}

	/**
	 * Remove um discente da seleção
	 * 
	 * sigaa.war/extensao/DiscenteExtensao/form.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String removeSelecao() throws DAOException {

		int idDiscente = getParameterInt("idDiscente", 0);

		if (idDiscente > 0) {
			DiscenteExtensao d = new DiscenteExtensao();
			d.setDiscente(new Discente(idDiscente));

			atividade.getDiscentesSelecionados().remove(d);

			// Retorna do banco...
			InscricaoSelecaoExtensaoDao dao = getDAO(InscricaoSelecaoExtensaoDao.class);
			InscricaoSelecaoExtensao ins = dao.findByDiscenteAtividade(
					idDiscente, atividade.getId());

			atividade.getInscricoesSelecao().add(ins);

			inscricoesAtualizar.remove(ins);
		} else
			addMensagemErro("Discente não selecionado!");

		return null;

	}

	/**
	 * Retorna lista de situações do discente extensão cadastradas
	 * 
	 * Não é chamado por JSPs.
	 * 
	 * @return
	 */
	public Collection<SelectItem> getAllSituacaoDiscenteExtensaoCombo() {

		try {
			return toSelectItems(getGenericDAO().findAll(
					TipoSituacaoDiscenteExtensao.class), "id", "descricao");
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}

	}

	public AtividadeExtensao getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeExtensao atividade) {
		this.atividade = atividade;
	}

	public Collection<InscricaoSelecaoExtensao> getInscricoesAtualizar() {
		return inscricoesAtualizar;
	}

	public void setInscricoesAtualizar(
			Collection<InscricaoSelecaoExtensao> inscricoesAtualizar) {
		this.inscricoesAtualizar = inscricoesAtualizar;
	}

	/**
	 * Localiza discente na tela de buscar discente
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/extensao/DiscenteExtensao/busca_discente.jsp</li>
	 * </ul>
	 * 
	 * @throws LimiteResultadosException
	 * @throws DAOException
	 */
	public void localizar() {

		erros.getMensagens().clear();
		if (discentesExtensao != null)
			discentesExtensao.clear();

		/* Analisando filtros selecionados */
		String titulo = null;
		Integer idEdital = null;
		Integer idSituacao = null;
		Integer idVinculo = null;
		Integer idDiscente = null;
		Integer anoAtividade = null;
		Boolean disAtivo = null;
		Integer idCurso = null;
		Date dataInicioPlanoTrabalho = null;
		Date dataFimPlanoTrabalho = null;
		Date dataInicioBolsa = null;
		Date dataFimBolsa = null;
		Date dataInicioFinalizacao = null;
		Date dataFimFinalizacao = null;
		Date dataInicioCadastro = null;
		Date dataFimCadastro = null;

		ListaMensagens erros = new ListaMensagens();

		// Definição dos filtros e validações
		if(checkBuscaAnoInicioBolsa) {
			dataInicioBolsa = this.dataInicioBolsa;
			dataFimBolsa = this.dataFimBolsa;
			ValidatorUtil.validateRequired(dataInicioBolsa,"Data Início Bolsa", erros);
			ValidatorUtil.validateRequired(dataFimBolsa,"Data Fim Bolsa", erros);
		}
		
		if(checkBuscaAnoInicioFinalizacao) {
			dataInicioFinalizacao = this.dataInicioFinalizacao;
			dataFimFinalizacao = this.dataFimFinalizacao;
			ValidatorUtil.validateRequired(dataInicioFinalizacao,"Data Início Finalização", erros);
			ValidatorUtil.validateRequired(dataFimFinalizacao,"Data Fim Finalização", erros);
		}
		
		if (checkBuscaTituloAtividade) {
			titulo = buscaTituloAtividade;
			ValidatorUtil.validateRequired(titulo, "Título", erros);
		}
		
		if (checkBuscaEdital) {
			idEdital = buscaEdital;
			if(idEdital == 0)
				erros.addErro("Edital: Campo obrigatório não informado.");
		}
		
		if (checkBuscaSituacao) {
			idSituacao = situacaoDiscenteExtensao.getId();
			if(idSituacao == 0)
				erros.addErro("Situação: Campo obrigatório não informado.");
		}
		
		if (checkBuscaVinculo) {
			idVinculo = vinculoDiscente.getId();
			if(idVinculo == 0)
				erros.addErro("Vínculo: Campo obrigatório não informado.");
		}
		
		if (checkBuscaDiscente) {
			idDiscente = discente.getId();
			ValidatorUtil.validateRequired(discente.getPessoa().getNome(), "Discente", erros);
		}
		
		if (checkBuscaAno) {
			anoAtividade = anoReferencia;
			ValidatorUtil.validateRequired(anoAtividade, "Ano da Ação", erros);
			
		}
		
		if (checkBuscaDiscenteAtivo) {
			disAtivo = discenteAtivo;
		}
		
		if (checkBuscaCurso) {
			idCurso = curso.getId();
			if(idCurso == 0)
				erros.addErro("Curso: Campo obrigatório não informado.");
		}
		
		if (checkBuscaAnoInicio) {
			dataInicioPlanoTrabalho = dataInicio;
			dataFimPlanoTrabalho = dataFim;
			ValidatorUtil.validateRequired(dataInicioPlanoTrabalho,"Data Início Plano", erros);
			ValidatorUtil.validateRequired(dataFimPlanoTrabalho,"Data Fim Plano", erros);
		}
		
		if (checkBuscaPeriodoCadastro) {
			dataInicioCadastro 	= this.dataInicioCadastro;
			dataFimCadastro		= this.dataFimCadastro;
			if (ValidatorUtil.isEmpty(dataInicioCadastro) || ValidatorUtil.isEmpty(dataFimCadastro))
				addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Período de Cadastro");
			else
				ValidatorUtil.validaOrdemTemporalDatas(dataInicioCadastro, dataFimCadastro, false, "Período de Cadastro", erros);
		}

		if ((!checkBuscaTituloAtividade) && (!checkBuscaSituacao)
				&& (!checkBuscaDiscente) && (!checkBuscaAno)
				&& (!checkBuscaCurso) && (!checkBuscaEdital)
				&& (!checkBuscaVinculo) && (!checkBuscaAnoInicio) && (!checkBuscaAnoInicio) 
				&& (!checkBuscaAnoInicioBolsa && !checkBuscaAnoInicioFinalizacao)
				&& (!checkBuscaPeriodoCadastro)) {
			addMensagemErro("Selecione pelo menos uma opção para buscar os discentes!");			
		} else {

			try{
				
				DiscenteExtensaoDao dao = getDAO(DiscenteExtensaoDao.class);
				
				if (erros.isEmpty()) {					
					discentesExtensao = dao.filter(idEdital, titulo, idDiscente,
							idSituacao, anoAtividade, disAtivo,
							idCurso, idVinculo, dataInicioPlanoTrabalho, dataFimPlanoTrabalho,
							dataInicioBolsa, dataFimBolsa, dataInicioFinalizacao, dataFimFinalizacao, 
							dataInicioCadastro, dataFimCadastro, checkOrdenarDataCadastro);
					
					if(discentesExtensao.isEmpty())
						addMensagemWarning("Nenhum discente encontrado para os parâmetros de busca informados.");
				}
				else {
					addMensagens(erros);
				}
			    
			}catch (LimiteResultadosException e) {
				addMensagemWarning("A consulta retornou muitos registros. Por favor, restrinja mais a busca.");
			}catch (DAOException e) {
			    notifyError(e);
			}			
		}
	}

	
	/**
	 * Relatório ou em tela
	 * 
	 * sigaa.war/extensao/Relatorios/dados_bancarios_discentes_form.jsp 
	 * 
	 * @return
	 * @throws LimiteResultadosException
	 * @throws DAOException
	 */
	public String localizarRelatorioDadosBancarios() throws DAOException {
		
		localizar();
		if(discentesExtensao.isEmpty() && !isCheckGerarRelatorio()) {
			addMensagemWarning("Nenhum Discente localizado!");
			return null;
		}

		if (isCheckGerarRelatorio()) {
			getGenericDAO().initialize(situacaoDiscenteExtensao);
			getGenericDAO().initialize(vinculoDiscente);
			getGenericDAO().initialize(curso);
							
			return forward(ConstantesNavegacao.RELATORIO_DADOS_BANCARIOS_DISCENTES_REL);
		} else
			return null;

	}

	/**
	 * Carrega plano e prepara MBeans para visualizaçao.
	 * 
	 * sigaa.war/extensao/DiscenteExtensao/busca_discente.jsp
	 * sigaa.war/extensao/DiscenteExtensao/homologacao_bolsa_discente.jsp
	 * sigaa.war/extensao/Relatorios/dados_bancarios_discentes_form.jsp
	 * 
	 * @return
	 */
	public String view() {
	    Integer id;
	    try {
	    	
	    	if(null != getParameterInt("idDiscenteExtensao") && !"".equals(getParameterInt("idDiscenteExtensao"))){
	    		id = getParameterInt("idDiscenteExtensao");
	    	}else{
	    		id = obj.getId();
	    	}
	    	
		obj = getGenericDAO().findAndFetch(id, DiscenteExtensao.class, "planoTrabalhoExtensao");
		
		//Evitar erro de Lazy
		if (obj.getDiscente() != null && obj.getDiscente().getCurso() != null) {
		    obj.getDiscente().getCurso().getNomeCompleto();
		}
		if (obj.getHistoricoSituacao() != null) {
		    obj.getHistoricoSituacao().iterator();
		}
		
		return forward(ConstantesNavegacao.DISCENTEEXTENSAO_VIEW);
	    } catch (DAOException e) {
		tratamentoErroPadrao(e);
		return null;
	    }
	}

	public Collection<DiscenteExtensao> getDiscentesExtensao() {
		return discentesExtensao;
	}

	public void setDiscentesExtensao(
			Collection<DiscenteExtensao> discentesExtensao) {
		this.discentesExtensao = discentesExtensao;
	}

	public boolean isCheckBuscaSituacao() {
		return checkBuscaSituacao;
	}

	public void setCheckBuscaSituacao(boolean checkBuscaSituacao) {
		this.checkBuscaSituacao = checkBuscaSituacao;
	}

	public boolean isCheckGerarRelatorio() {
		return checkGerarRelatorio;
	}

	public void setCheckGerarRelatorio(boolean checkGerarRelatorio) {
		this.checkGerarRelatorio = checkGerarRelatorio;
	}

	public boolean isCheckBuscaDiscente() {
		return checkBuscaDiscente;
	}

	public void setCheckBuscaDiscente(boolean checkBuscaDiscente) {
		this.checkBuscaDiscente = checkBuscaDiscente;
	}

	public boolean isCheckBuscaAno() {
		return checkBuscaAno;
	}

	public void setCheckBuscaAno(boolean checkBuscaAno) {
		this.checkBuscaAno = checkBuscaAno;
	}

	public boolean isCheckBuscaDiscenteAtivo() {
		return checkBuscaDiscenteAtivo;
	}

	public void setCheckBuscaDiscenteAtivo(boolean checkBuscaDiscenteAtivo) {
		this.checkBuscaDiscenteAtivo = checkBuscaDiscenteAtivo;
	}

	public boolean isCheckBuscaCurso() {
		return checkBuscaCurso;
	}

	public void setCheckBuscaCurso(boolean checkBuscaCurso) {
		this.checkBuscaCurso = checkBuscaCurso;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public int getAnoReferencia() {
		return anoReferencia;
	}

	public void setAnoReferencia(int anoReferencia) {
		this.anoReferencia = anoReferencia;
	}

	public boolean isDiscenteAtivo() {
		return discenteAtivo;
	}

	public void setDiscenteAtivo(boolean discenteAtivo) {
		this.discenteAtivo = discenteAtivo;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public TipoSituacaoDiscenteExtensao getSituacaoDiscenteExtensao() {
		return situacaoDiscenteExtensao;
	}

	public void setSituacaoDiscenteExtensao(
			TipoSituacaoDiscenteExtensao situacaoDiscenteExtensao) {
		this.situacaoDiscenteExtensao = situacaoDiscenteExtensao;
	}

	public boolean isCheckBuscaTituloAtividade() {
		return checkBuscaTituloAtividade;
	}

	public void setCheckBuscaTituloAtividade(boolean checkBuscaTituloAtividade) {
		this.checkBuscaTituloAtividade = checkBuscaTituloAtividade;
	}

	public String getBuscaTituloAtividade() {
		return buscaTituloAtividade;
	}

	public void setBuscaTituloAtividade(String buscaTituloAtividade) {
		this.buscaTituloAtividade = buscaTituloAtividade;
	}

	public boolean isCheckBuscaEdital() {
		return checkBuscaEdital;
	}

	public void setCheckBuscaEdital(boolean checkBuscaEdital) {
		this.checkBuscaEdital = checkBuscaEdital;
	}

	public int getBuscaEdital() {
		return buscaEdital;
	}

	public void setBuscaEdital(int buscaEdital) {
		this.buscaEdital = buscaEdital;
	}

	public TipoVinculoDiscente getVinculoDiscente() {
		return vinculoDiscente;
	}

	public void setVinculoDiscente(TipoVinculoDiscente vinculoDiscente) {
		this.vinculoDiscente = vinculoDiscente;
	}

	public boolean isCheckBuscaVinculo() {
		return checkBuscaVinculo;
	}

	public void setCheckBuscaVinculo(boolean checkBuscaVinculo) {
		this.checkBuscaVinculo = checkBuscaVinculo;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public boolean isCheckBuscaAnoInicio() {
		return checkBuscaAnoInicio;
	}

	public void setCheckBuscaAnoInicio(boolean checkBuscaAnoInicio) {
		this.checkBuscaAnoInicio = checkBuscaAnoInicio;
	}

	public boolean isCheckBuscaAnoInicioBolsa() {
		return checkBuscaAnoInicioBolsa;
	}

	public void setCheckBuscaAnoInicioBolsa(boolean checkBuscaAnoInicioBolsa) {
		this.checkBuscaAnoInicioBolsa = checkBuscaAnoInicioBolsa;
	}

	public boolean isCheckBuscaAnoInicioFinalizacao() {
		return checkBuscaAnoInicioFinalizacao;
	}

	public void setCheckBuscaAnoInicioFinalizacao(
			boolean checkBuscaAnoInicioFinalizacao) {
		this.checkBuscaAnoInicioFinalizacao = checkBuscaAnoInicioFinalizacao;
	}

	public Date getDataInicioBolsa() {
		return dataInicioBolsa;
	}

	public void setDataInicioBolsa(Date dataInicioBolsa) {
		this.dataInicioBolsa = dataInicioBolsa;
	}

	public Date getDataFimBolsa() {
		return dataFimBolsa;
	}

	public void setDataFimBolsa(Date dataFimBolsa) {
		this.dataFimBolsa = dataFimBolsa;
	}

	public Date getDataInicioFinalizacao() {
		return dataInicioFinalizacao;
	}

	public void setDataInicioFinalizacao(Date dataInicioFinalizacao) {
		this.dataInicioFinalizacao = dataInicioFinalizacao;
	}

	public Date getDataFimFinalizacao() {
		return dataFimFinalizacao;
	}

	public void setDataFimFinalizacao(Date dataFimFinalizacao) {
		this.dataFimFinalizacao = dataFimFinalizacao;
	}

	public void setCheckOrdenarDataCadastro(boolean checkOrdenarDataCadastro) {
		this.checkOrdenarDataCadastro = checkOrdenarDataCadastro;
	}

	public boolean isCheckOrdenarDataCadastro() {
		return checkOrdenarDataCadastro;
	}

	public boolean isCheckBuscaPeriodoCadastro() {
		return checkBuscaPeriodoCadastro;
	}

	public void setCheckBuscaPeriodoCadastro(boolean checkBuscaPeriodoCadastro) {
		this.checkBuscaPeriodoCadastro = checkBuscaPeriodoCadastro;
	}

	public Date getDataInicioCadastro() {
		return dataInicioCadastro;
	}

	public void setDataInicioCadastro(Date dataInicioCadastro) {
		this.dataInicioCadastro = dataInicioCadastro;
	}

	public Date getDataFimCadastro() {
		return dataFimCadastro;
	}

	public void setDataFimCadastro(Date dataFimCadastro) {
		this.dataFimCadastro = dataFimCadastro;
	}
	
}
