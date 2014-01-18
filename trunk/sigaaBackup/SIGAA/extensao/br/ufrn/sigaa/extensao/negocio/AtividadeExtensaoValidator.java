/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 * 
 * Created on 30/11/2006
 *
 */
package br.ufrn.sigaa.extensao.negocio;

import static br.ufrn.arq.mensagens.MensagensArquitetura.DATA_POSTERIOR_A;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang.time.DateUtils;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.EditalDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.CursoEventoExtensao;
import br.ufrn.sigaa.extensao.dominio.EditalExtensao;
import br.ufrn.sigaa.extensao.dominio.MembroAtividade;
import br.ufrn.sigaa.extensao.dominio.Objetivo;
import br.ufrn.sigaa.extensao.dominio.ObjetivoAtividades;
import br.ufrn.sigaa.extensao.dominio.OrcamentoConsolidado;
import br.ufrn.sigaa.extensao.dominio.ProjetoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.negocio.ProjetoBaseValidator;


/*******************************************************************************
 * Classe com m�todos de valida��o para os passos do cadastro de uma a��o de
 * extens�o
 * 
 * @author Victor Hugo
 * @author Ilueny Santos
 * 
 ******************************************************************************/
public class AtividadeExtensaoValidator {

	/**
	 * Valida os dados gerais de uma A��o de Extens�o
	 * 
	 * @param atividade
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaDadosGerais(AtividadeExtensao atividade, ListaMensagens lista, boolean gestorExtensao) throws DAOException {
		EditalDao dao = new EditalDao();
		if (atividade == null || ValidatorUtil.isEmpty(atividade.getTipoAtividadeExtensao())) {
			lista.addErro("Tipo de A��o de Extens�o n�o informado, reinicie o cadastro ou edi��o da proposta.");
		} else {

			if ( atividade.isAutoFinanciado()  ) {
				atividade.setFinanciamentoInterno(false);
				atividade.setFinancProex(false);
				atividade.setEditalExtensao(null);
				atividade.setBolsasConcedidas(0);
				atividade.setBolsasSolicitadas(0);
				atividade.setFinanciamentoExterno(false);
				atividade.setClassificacaoFinanciadora(null);
				atividade.setEditalExterno(null);
				atividade.setAutoFinanciado(true);
			
			} else {
				
				// em caso de financiamento do FAEX o edital � obrigat�rio
				if (atividade.getBolsasConcedidas() == null) {
					atividade.setBolsasConcedidas(0);
				}
				if (atividade.getBolsasSolicitadas() == null) {
					atividade.setBolsasSolicitadas(0);
				}
				if (atividade.getTotalDiscentes() == null) {
					atividade.setTotalDiscentes(0);
				}
				if (atividade.isFinanciamentoExterno()) {
					validateRequired(atividade.getClassificacaoFinanciadora(), "Tipo de Financiador", lista);
				}

			}

			validateRequired(atividade.getTitulo(), "T�tulo", lista);
			ValidatorUtil.validateMaxLength(atividade.getTitulo(), 400, "T�tulo", lista);			
			validateRequired(atividade.getAno(), "Ano", lista);
			ValidatorUtil.validaInt(atividade.getAno(), "Ano", lista);
			validateRequired(atividade.getDataInicio(), "Data de In�cio", lista);
			validateRequired(atividade.getDataFim(), "Data de Fim", lista);
			ValidatorUtil.validaOrdemTemporalDatas(atividade.getDataInicio(), atividade.getDataFim(), true, "Data de In�cio", lista);
			
			if(!gestorExtensao && !atividade.isRegistro() && atividade.getDataInicio().before(new Date()) && !DateUtils.isSameDay(atividade.getDataInicio(), new Date()))
				lista.addMensagem(DATA_POSTERIOR_A, "Data de Inicio", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
			
			validateRequired(atividade.getAreaConhecimentoCnpq(), "�rea de conhecimento do CNPQ", lista);
			validateRequired(atividade.getTipoRegiao(), "Abrang�ncia", lista);
			validateRequiredId(atividade.getAreaTematicaPrincipal().getId(), "�rea Tem�tica de Extens�o", lista);
			
			if ( atividade.isVinculadoProgramaEstrategico() ) {
				// Condi��o da op��o "N�o Informado" para o Programa Estrat�gico
				if ( atividade.getProgramaEstrategico().getId() != -1 ){
					validateRequiredId(atividade.getProgramaEstrategico().getId(), "Programa Estrat�gico de Extens�o", lista);
				}
			}
			
			validateRequired(atividade.getPublicoEstimado(), "Discriminar P�blico Alvo Interno", lista);
			validateRequired(atividade.getPublicoEstimado(), "Quantificar P�blico Alvo Interno", lista);
			ValidatorUtil.validateMinValue(atividade.getPublicoEstimado(), 1, "Quantificar P�blico Alvo Interno", lista);
			ValidatorUtil.validateMaxValue(atividade.getPublicoEstimado(), 2147483647, "Quantificar P�blico Alvo Interno", lista);

			if ( !atividade.isFinanciamentoInterno() && !atividade.isAutoFinanciado() && !atividade.isFinanciamentoExterno() ) {
				lista.addErro("� necess�rio informar uma forma de financiamento.");
			}
			
		    ValidatorUtil.validateEmptyCollection("� necess�rio informar pelo menos um Local de Realiza��o.", 
		    		atividade.getLocaisRealizacao(), lista);
			
			if ( atividade.isFinanciamentoInterno() ) {
				if ( !atividade.isFinancUnidProponente() && !atividade.isFinancProex() ) {
					lista.addErro("� necess�rio informar umas das formas de financiamento interno.");
				} else {
					if ( atividade.isFinancProex() ) {
						validateRequiredId(atividade.getEditalExtensao().getId(), "Edital de Extens�o", lista);
						validateRequired(atividade.getBolsasSolicitadas(), "N� Bolsas Solicitadas", lista);
						if(!isEmpty(atividade.getEditalExtensao()) && !isEmpty(dao.findLinhasAtuacaoByEditalExtensao(atividade.getEditalExtensao().getId())))
							validateRequiredId(atividade.getLinhaAtuacao().getId(), "Linha de Atua��o", lista);
					} 
				}
				
			}
			
			if ( atividade.isFinanciamentoExterno() ) {
				if ( !atividade.isFinancExternoEdital() && !atividade.isFinancExternoEspecial() ) {
					lista.addErro("� necess�rio informar umas das formas de financiamento Externo.");	
				} else {
					if ( atividade.isFinancExternoEspecial() ) {
						validateRequiredId(atividade.getClassificacaoFinanciadora().getId(), "Financiador", lista);
						validateRequired(atividade.getBolsasConcedidas(), "Bolsas Concedidas", lista);
					} 
					if ( atividade.isFinancExternoEdital() ) {
						validateRequired(atividade.getEditalExterno(), "Edital Externo", lista);
						validateRequiredId(atividade.getClassificacaoFinanciadora().getId(), "Financiador", lista);
						validateRequired(atividade.getBolsasConcedidas(), "Bolsas Concedidas", lista);
					}
				}
			}
			
			/** N�o pode haver mais de um projeto, no mesmo ano, com o mesmo t�tulo. A n�o ser que seja uma renova��o de projeto. */
			if ((!atividade.isProjetoAssociado()) && (atividade.getAno() != null) && (atividade.getRenovacao() != null && !atividade.getRenovacao())) {
				ProjetoBaseValidator.validaProjetosComMesmoTitulo(atividade.getProjeto(), lista);
			}

			// Verifica se o ano informado est� dentro do per�odo de realiza��o do projeto
			if ((atividade.getAno() != null) && (atividade.getDataInicio() != null) && (atividade.getDataFim() != null)) {
			    int anoDataInicio = CalendarUtils.getAno(atividade.getDataInicio());
			    int anoDataFim = CalendarUtils.getAno(atividade.getDataFim());
			    if (atividade.getAno() < anoDataInicio || atividade.getAno() > anoDataFim) {
			    	lista.addErro("Campo obrigat�rio: Ano informado n�o est� dentro do per�odo de realiza��o do projeto.");
			    }			    
			}

			validaTempoMinimoExecucao(atividade, lista);
			
			validaEditalAbrangeAcao(atividade, lista);

			validaDataAcaoRegistro(atividade, gestorExtensao, lista);
			
		}

	}
	
	/**
	 * Valida a data da a��o no registro da mesma. S� � poss�vel cadastrar a��es do ano atual ou 
	 * se ainda estiver em Janeiro ou Fevereiro (2 meses de toler�ncia.)
	 *
	 * @param atividade
	 * @param gestorExtensao
	 * @param lista
	 */
	private static void validaDataAcaoRegistro(AtividadeExtensao atividade, boolean gestorExtensao, ListaMensagens lista) {
		if ( atividade.isRegistro() && !gestorExtensao && atividade.getDataInicio() != null ) {
			int anoLimite = ParametroHelper.getInstance().getParametroInt(ParametrosExtensao.ANO_LIMITE_REGISTRO_ACAO_ANTIGA);
			Date dataLimiteInferior = CalendarUtils.descartarHoras(CalendarUtils.adicionarAnos(new Date(), -anoLimite));
			Date dataLimiteSuperior = CalendarUtils.adicionaDias(CalendarUtils.descartarHoras(new Date()), -1);
			if ( !CalendarUtils.isDentroPeriodoAberto(dataLimiteInferior, dataLimiteSuperior, atividade.getDataInicio()))
				lista.addErro("S� � poss�vel o registro de projetos que ocorreram entre " + Formatador.getInstance().formatarData(dataLimiteInferior) 
						+ " e " + Formatador.getInstance().formatarData(dataLimiteSuperior) + ".");
		}
	}

	/** 
	 * Validando o tempo m�nimo de execu��o da proposta previsto no edital.
	 * 
	 * @param atividade
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaTempoMinimoExecucao(AtividadeExtensao atividade, ListaMensagens lista) throws DAOException {
	    if (atividade.isFinanciamentoInterno() && !ValidatorUtil.isEmpty(atividade.getEditalExtensao())) {
		EditalDao dao = DAOFactory.getInstance().getDAO(EditalDao.class);
		try {
		    atividade.setEditalExtensao(dao.findByPrimaryKey(atividade.getEditalExtensao().getId(), EditalExtensao.class));
		    if (atividade.getRegrasEdital() != null) {
			int periodoMinimo = atividade.getRegrasEdital().getPeriodoMinimoExecucao();

			if ((periodoMinimo > 0) && (CalendarUtils.calculoMeses(atividade.getDataInicio(), atividade.getDataFim()) < periodoMinimo)) {
			    lista.addErro("O Per�odo de realiza��o da a��o de extens�o � incompat�vel com o m�nimo exigido no edital selecionado. Um "
				    + atividade.getTipoAtividadeExtensao().getDescricao() + " deve ser executado, no m�nimo, em " + periodoMinimo
				    + " meses. Verifique as datas de in�cio e fim da proposta.");
			}
		    }
		}finally {
		    dao.close();
		}
	    }
	}
	
	/**
	 * Verifica se o edital selecionado possui regras definidas para a a��o informada.
	 * 
	 * @param atividade
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaEditalAbrangeAcao(AtividadeExtensao atividade, ListaMensagens lista) throws DAOException {
	    if (atividade.isFinancProex() && !ValidatorUtil.isEmpty(atividade.getEditalExtensao())) {
		EditalDao dao = DAOFactory.getInstance().getDAO(EditalDao.class);
		try {
		    atividade.setEditalExtensao(dao.findByPrimaryKey(atividade.getEditalExtensao().getId(), EditalExtensao.class));
		    if (ValidatorUtil.isEmpty(atividade.getRegrasEdital())) {
			lista.addErro("O edital selecionado n�o abrange o tipo de A��o de Extens�o que est� sendo cadastrado ("	+
				atividade.getTipoAtividadeExtensao().getDescricao() + ").");
		    }
		}finally {
		    dao.close();
		}
	    }
	}
	
	
	/**
	 * Valida os dados de um projeto de uma A��o de Extens�o.
	 * 
	 * @param atividade
	 * @param lista
	 */
	public static void validaDadosProjeto(AtividadeExtensao atividade, ListaMensagens lista) {
		validateRequired(atividade.getProjeto().getResumo(), "Resumo", lista);
		validateRequired(atividade.getJustificativa(), "Justificativa",	lista);
		validateRequired(atividade.getFundamentacaoTeorica(), "Fundamenta��o Te�rica", lista);
		validateRequired(atividade.getMetodologia(), "Metodologia", lista);
		validateRequired(atividade.getProjeto().getReferencias(), "Refer�ncias", lista);
		validateRequired(atividade.getProjeto().getObjetivos(), "Objetivos Gerais", lista);
		validateRequired(atividade.getProjeto().getResultados(), "Resultados Esperados", lista);
	}


	/**
	 * 
	 * Valida os dados de um Programa de uma A��o de Extens�o.
	 * 
	 * @param atividade
	 * @param coordenador
	 * @param chSemanal
	 * @param lista
	 * @throws DAOException 
	 */
	public static void validaDadosPrograma(AtividadeExtensao atividade, ListaMensagens lista) throws DAOException {
		validateRequired(atividade.getResumo(), "Resumo", lista);
		validateRequired(atividade.getJustificativa(), "Justificativa", lista);
		validateRequired(atividade.getFundamentacaoTeorica(), "Fundamenta��o Te�rica", lista);
		validateRequired(atividade.getMetodologia(), "Metodologia", lista);
		validateRequired(atividade.getReferencias(), "Refer�ncias", lista);
		validateRequired(atividade.getObjetivos(), "Objetivos Gerais", lista);
		validateRequired(atividade.getResultados(), "Resultados Esperados", lista);
	}


	/**
	 * Valida os dados de um Produto de uma A��o de Extens�o.
	 * 
	 * @param atividade
	 * @param lista
	 */
	public static void validaDadosProduto(AtividadeExtensao atividade, ListaMensagens lista) {

		validateRequired(atividade.getProdutoExtensao().getTipoProduto(), "Selecione o tipo do produto.", lista);
		ValidatorUtil.validaInt(atividade.getProdutoExtensao().getTiragem(), "Tiragem", lista);
		ValidatorUtil.validateMinValue(atividade.getProdutoExtensao().getTiragem(), 1, "Tiragem", lista);
		validateRequired(atividade.getResumo(), "Resumo", lista);
		ValidatorUtil.validateMinLength(atividade.getResumo(), 200, "Resumo", lista);
		ValidatorUtil.validateMaxLength(atividade.getResumo(), 400, "Resumo", lista);
		validateRequired(atividade.getJustificativa(), "Justificativa", lista);
		validateRequired(atividade.getObjetivos(), "Objetivos", lista);
		validateRequired(atividade.getResultados(), "Resultados", lista);
		ValidatorUtil.validateMaxLength(atividade.getResultados(), 400, "Resultados", lista);
	}

	/**
	 * Valida os dados de um Curso e Evento de uma A��o de Extens�o.
	 * 
	 * @param atividade
	 * @param lista
	 */
	public static void validaDadosCursoEvento(AtividadeExtensao atividade,	ListaMensagens lista) {
		CursoEventoExtensao cursoEvento = atividade.getCursoEventoExtensao();

		if (atividade.getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.CURSO) {
			validateRequired(cursoEvento.getModalidadeEducacao(), "Selecione a modalidade do curso", lista);
			validateRequired(cursoEvento.getResumo(), "Resumo", lista);
			validateRequired(cursoEvento.getProgramacao(),"Programa��o", lista);
		}

		// Validar campos obrigat�rios
		validateRequiredId(cursoEvento.getTipoCursoEvento().getId(), "Selecione o tipo do curso/evento", lista);
		validateRequired(cursoEvento.getCargaHoraria(), "Carga Hor�ria", lista);
		validateRequired(cursoEvento.getNumeroVagas(), "Previs�o de N� de Vagas Oferecidas", lista);
		validateRequired(cursoEvento.getResumo(), "Resumo", lista);
		validateRequired(cursoEvento.getProgramacao(), "Programa��o", lista);
		
		// Removendo coment�rios inseridos quando se copia e cola conte�dos de arquivos do Word.
		cursoEvento.setResumo(StringUtils.removerComentarios(cursoEvento.getResumo()));
		cursoEvento.setProgramacao(StringUtils.removerComentarios(cursoEvento.getProgramacao()));
		

		// Validar carga hor�ria de acordo com o tipo de curso ou evento
		if ((cursoEvento.getTipoCursoEvento() != null) && (cursoEvento.getTipoCursoEvento().getId() != 0)
				&& (!cursoEvento.isCargaHorariaValida())) {
			lista.addErro("A carga hor�ria para "
					+ atividade.getTipoAtividadeExtensao().getDescricao()
					+ " DE " + cursoEvento.getTipoCursoEvento().getDescricao()
					+ " deve ser de, no m�nimo, "
					+ cursoEvento.getTipoCursoEvento().getChMinima()
					+ " horas.");
		}
		
	}

	/**
	 * Valida or�amento da a��o de extens�o.
	 * 
	 * @param atividade
	 * @param lista
	 */
	public static void validaOrcamento(AtividadeExtensao atividade,	ListaMensagens lista) {
	    NumberFormat formatter = new DecimalFormat("#,##0.00");
	    for (OrcamentoConsolidado oc : atividade.getOrcamentosConsolidados()) {
	    	ValidatorUtil.validaDouble(oc.getFundacao(), "Funda��o", lista);
	    	ValidatorUtil.validaDouble(oc.getFundo(), "Interno", lista);
	    	ValidatorUtil.validaDouble(oc.getOutros(), "Outros", lista);
			if ( oc.getFundacao() != null && oc.getFundo() != null && oc.getOutros() != null) { 
					if(oc.getTotalConsolidado() != oc.getTotalOrcamento()) {
							lista.addErro("O or�amento de " + oc.getElementoDespesa().getDescricao() 
									+ " desta a��o n�o foi totalmente consolidado. R$ " + formatter.format((oc.getTotalOrcamento() - oc.getTotalConsolidado())) +" ainda pendente de consolida��o.");
					}
			}
	    }
	}

	/**
	 * Valida quantas a��es de extens�o s�o necess�rias para para submiss�o de um programa. 
	 * 
	 * @param programa
	 * @param lista
	 * @throws DAOException 
	 */
	public static void validaAtividadesPrograma(AtividadeExtensao programa, ListaMensagens lista) throws DAOException {		
		if (ValidatorUtil.isNotEmpty(programa.getEditalExtensao()) && programa.isFinancProex()) {
			EditalDao dao = DAOFactory.getInstance().getDAO(EditalDao.class);			
			try {
				programa.setEditalExtensao(dao.findByPrimaryKey(programa.getEditalExtensao().getId(), EditalExtensao.class));
				if ( programa.getEditalExtensao() != null ) {
					programa.getEditalExtensao().getRegras().iterator();
					int minAcoes = programa.getRegrasEdital().getMinAcoesVinculadas();
					if ( minAcoes > 0 && ((programa.getAtividades() == null) || (programa.getAtividades().size() < minAcoes))) {
						lista.addErro("Quantidade m�nima de a��es de extens�o vinculadas ao programa n�o foi atingida.");
						lista.addErro("Um programa de extens�o � composto de no m�nimo " + minAcoes +" a��es de extens�o vinculadas.");
					}				
				}
			}finally {
				dao.close();
			}
		}
	}

	/**
	 * Valida as atividades com as quais o produto se relaciona. Um produto pode
	 * ser fruto de uma outra a��o de extens�o como por exemplo, uma
	 * apostila(produto) utilizada em um curso.
	 *  
	 * @param programa
	 * @param lista
	 */
	public static void validaAtividadesProduto(AtividadeExtensao programa, ListaMensagens lista) {
	}

	/**
	 * M�todo utilizado para validar os objetivos da Atividade de Extens�o
	 * <br />
	 * M�todo chamado pelas(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamada por JSP(s)</li>
	 * </ul>
	 * @param projeto
	 * @param lista
	 */
	public static void validaObjetivos(ProjetoExtensao projeto, ListaMensagens lista) {
		// deve haver pelo menos um objetivo
		if (projeto == null) {
			lista.addErro("Tipo de A��o de Extens�o n�o informado, reinicie o cadastro ou edi��o da proposta.");
		}
	}


	/**
	 * Faz a valida��o do or�amento de uma a��o de extens�o.
	 * 
	 * 
	 * @param atividade
	 * @param orcamento
	 * @param lista
	 */
	public static void validaAdicionaOrcamento(AtividadeExtensao atividade,	OrcamentoDetalhado orcamento, ListaMensagens lista) {

		validateRequired(orcamento.getDiscriminacao(), "Discrimina��o", lista);
		validateRequired(orcamento.getElementoDespesa(), "Selecione um elemento de despesa.", lista);
		validateRequired(orcamento.getQuantidade(), "Quantidade", lista);
		validateRequired(orcamento.getValorUnitario(), "Valor Unit�rio", lista);

		if (orcamento.getElementoDespesa() != null) {
			validateRequiredId(orcamento.getElementoDespesa().getId(), "Selecione um elemento de despesa.", lista);
			
			if(! orcamento.getElementoDespesa().isPermiteValorFracionado()){
				if ( orcamento.getQuantidade() % 1 != 0 ){
					lista.addErro("A quantidade n�o pode ser um valor fracionado.");
				}
			}
		}
		
		if ((orcamento.getQuantidade() != null)	&& (orcamento.getQuantidade() == 0)) {
			lista.addErro("Quantidade deve ser maior que 0 (zero)");
		}
		if (orcamento.getValorUnitario() == 0) {
			lista.addErro("Valor Unit�rio deve ser maior que 0 (zero)");
		}
	}

	/**
	 * Verifica a possibilidade de vincular uma a��o de extens�o � outra.
	 * 
	 * @param atividade
	 * @param atividadeNova
	 * @param lista
	 */
	public static void validaAdicionaAtividadeAtividade(AtividadeExtensao atividade, AtividadeExtensao atividadeNova, ListaMensagens lista) {
		if (atividadeNova.getTipoAtividadeExtensao().getId() == atividade.getTipoAtividadeExtensao().getId()) {
			lista.addErro("N�o � poss�vel vincular a��o do tipo '"
					+ atividadeNova.getTipoAtividadeExtensao().getDescricao()
					+ " DE EXTENS�O' a outra a��o do mesmo tipo.");
		}

		if (atividade.getAtividades().contains(atividadeNova.getId())) {
			lista.addErro("A��o de Extens�o j� vinculada.");
		}
	}


	/**
	 * Verifica se uma atividade j� atingiu o limite m�ximo or�ament�rio. 
	 * 
	 * @param atividade
	 * @param lista
	 */
	public static void validaLimiteMaxOrcamento(final AtividadeExtensao atividade, ListaMensagens lista) throws DAOException {
		if (ValidatorUtil.isNotEmpty(atividade.getEditalExtensao())) {			
			EditalDao dao = DAOFactory.getInstance().getDAO(EditalDao.class);
			try {
				atividade.setEditalExtensao(dao.findByPrimaryKey(atividade.getEditalExtensao().getId(), EditalExtensao.class));
				atividade.getEditalExtensao().getRegras().iterator();

				if (atividade.isFinanciamentoInterno() && !ValidatorUtil.isEmpty(atividade.getRegrasEdital())) {		
					Iterator<OrcamentoConsolidado> iterator = atividade.getOrcamentosConsolidados().iterator();
					double somatorio = 0.00;
					while(iterator.hasNext()){
						somatorio += iterator.next().getFundo();
					}
					if (somatorio <= 0.00) return;

					String msg = "A soma dos valores solicitados ao Fundo de Apoio � Extens�o (FAEx) n�o podem ultrapassar o valor definido no Edital (R$ ";
					double maximoPermitido = atividade.getRegrasEdital().getOrcamentoMaximo();
					if((maximoPermitido > 0) && somatorio > maximoPermitido) {
						lista.addErro((msg + atividade.getEditalExtensao().getRegraByTipo(atividade.getTipoAtividadeExtensao()).getOrcamentoMaximo() + ")").replace('.', ','));
					}
				}
			}finally {
				dao.close();
			}
		}
	}

	/**
	 * Valida se os membros passados participam de pelos menos uma das atividades passadas. � estranho mas, onde tem objetivos leia-se atividade.
	 */
	public static void validaEquipeExecutora(Collection<MembroProjeto> membros, Collection<Objetivo> objetivos, ListaMensagens lista) throws DAOException {
		boolean membroPresente = false;
		for (MembroProjeto membro : membros) {
			membroPresente = false;
			for (Objetivo objetivo : objetivos) {
				if ( !isEmpty( objetivo.getAtividadesPrincipais() ) ) {
					for (ObjetivoAtividades atividade : objetivo.getAtividadesPrincipais()) {
						if ( !isEmpty( atividade.getMembrosAtividade() ) ) {
							if ( MembroAtividade.containsMembro(atividade.getMembrosAtividade(), membro) ) {
								membroPresente = true;
							}
						}
					}
				}
			}
			if ( !membroPresente ) {
				lista.addErro("O membro " + membro.getNomeMembroProjeto() + " n�o participa de nenhuma atividade do projeto.");
			}
		}
	}
	
}