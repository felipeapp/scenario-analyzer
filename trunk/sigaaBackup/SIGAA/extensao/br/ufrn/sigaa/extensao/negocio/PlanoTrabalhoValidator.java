/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/05/2008
 *
 */
package br.ufrn.sigaa.extensao.negocio;

import static br.ufrn.arq.mensagens.MensagensArquitetura.DATA_POSTERIOR_A;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.bolsas.negocio.IntegracaoBolsas;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.extensao.DiscenteExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.InscricaoSelecaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.PlanoTrabalhoExtensaoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.TipoBolsaPesquisaDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.CalendarioExtensao;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoSelecaoExtensao;
import br.ufrn.sigaa.extensao.dominio.PlanoTrabalhoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoSituacaoDiscenteExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioBolsistaExtensao;
import br.ufrn.sigaa.mensagens.MensagensExtensao;
import br.ufrn.sigaa.pessoa.dominio.Banco;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;


/**
 * Classe que possui os m�todos necess�rios para a valida��o de um plano de Trabalho de um Discente.
 * 
 * 
 * @author UFRN
 *
 */
public class PlanoTrabalhoValidator {
    
    
    
    	/**
    	 * Verifica se a a��o de extens�o pode receber um novo plano de trabalho.
    	 * 
    	 * @param plano
    	 * @param lista
    	 * @throws DAOException
    	 * @throws ArqException
    	 */
	public static void validaAcao(AtividadeExtensao acao, ListaMensagens lista) {
	    	if (ValidatorUtil.isEmpty(acao)) {
		    lista.addMensagem(new MensagemAviso("A��o de Extens�o n�o selecionada.", TipoMensagemUFRN.ERROR));
		}	
	    	// @negocio: Somente a��es de extens�o enviadas para avalia��o dos membros do comit� de ensino, pesquisa e extens�o podem cadastrar planos.
		if (acao.isProjetoAssociado() && ValidatorUtil.isEmpty(acao.getDataEnvio())) {
		    lista.addMensagem(MensagensExtensao.CADASTRO_PLANO_NAO_PERMITIDO_PARA_UMA_ACAO);
		}
	}

	/**
	 * Verifica se o discente indicado demonstrou interesse em participar da a��o de extens�o.
	 * Pro�be a indica��o de algum discente que n�o esteja na lista.
	 * 
	 * @param de
	 * @param lista
	 * @throws ArqException
	 */
	public static void validaIndicacaoDiscenteInteressado(DiscenteExtensao de, ListaMensagens lista) throws ArqException {
		InscricaoSelecaoExtensaoDao dao = DAOFactory.getInstance().getDAO(InscricaoSelecaoExtensaoDao.class);
		try {
		    boolean inscrito = false;
		    Collection<InscricaoSelecaoExtensao> inscritos = dao.findInscritosProcessoSeletivoByAtividade(de.getAtividade().getId());
		    de.setDiscente( dao.findByPrimaryKey(de.getDiscente().getId(), Discente.class) );
		    for (InscricaoSelecaoExtensao inscricao : inscritos) {
				if (inscricao.getDiscente().equals(de.getDiscente())) {
				    inscrito = true;
				    break;
				}
		    }
		    if (!inscrito) { 
				lista.addErro("Somente discentes que aderiram ao Cadastro �nico e que demonstraram interesse em participar desta A��o de Extens�o podem ser cadastrados neste plano de trabalho.");
				lista.addErro(de.getDiscente().getMatriculaNome() + " n�o faz parte da lista de interessados desta a��o.");
		    }
		}finally {
		    dao.close();
		}
	}
	
	/**
	 * Classe com m�todos respons�veis pela valida��o das opera��es realizadas
	 * com planos de trabalho dos discentes.
	 * 
	 * @author ilueny santos
	 * @throws ArqException 
	 * @throws DAOException 
	 * 
	 */
	public static void validaDadosGerais(PlanoTrabalhoExtensao plano, ListaMensagens lista) throws DAOException, ArqException {

		ValidatorUtil.validaData(Formatador.getInstance().formatarData(plano.getDataInicio()), "In�cio do per�odo do plano", lista);
		ValidatorUtil.validaData(Formatador.getInstance().formatarData(plano.getDataFim()), "Fim do per�odo do plano", lista);		
		ValidatorUtil.validateRequired(plano.getAtividade(),"A��o de Extens�o", lista);
		ValidatorUtil.validateRequired(plano.getLocalTrabalho(), "Local Trabalho", lista);
		ValidatorUtil.validateRequired(plano.getJustificativa(), "Justificativa", lista);
		ValidatorUtil.validateRequired(plano.getObjetivo(), "Objetivos", lista);
		ValidatorUtil.validateRequired(plano.getOrientador(), "Orientador(a)", lista);
		ValidatorUtil.validateRequired(plano.getDescricaoAtividades(), "Atividade desenvolvidas", lista);

		if (plano.isValido()) {
			ValidatorUtil.validaInicioFim(plano.getDataInicio(), plano.getDataFim(), "Per�odo do plano", lista);

			String inicioAcao = Formatador.getInstance().formatarData(plano.getAtividade().getProjeto().getDataInicio());
			String fimAcao = Formatador.getInstance().formatarData(plano.getAtividade().getProjeto().getDataFim()); 

			if (!plano.isPlanoNoPeriodoProjeto()) {
				lista.addErro("Per�odo do plano de trabalho deve estar dentro do per�odo de vig�ncia da A��o de Extens�o. " +
						"("+ inicioAcao + " at� " + fimAcao  +")");
			}			 
		}

		if (plano.getDiscenteExtensao() == null) {			
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,"Discente");
		} else {

			//Validando discente no plano.
			if (plano.isValido()) {
				Date inicioPlano = plano.getDataInicio();
				Date fimPlano = plano.getDataFim();
				boolean inicioDiscenteDentroPlano = CalendarUtils.isDentroPeriodo(inicioPlano, fimPlano, plano.getDiscenteExtensao().getDataInicio());

				if (!inicioDiscenteDentroPlano) {
					lista.addErro("Data de in�cio do discente deve estar dentro do per�odo de vig�ncia do Plano de Trabalho. " +
							"("+  Formatador.getInstance().formatarData(inicioPlano) + " at� " +  Formatador.getInstance().formatarData(fimPlano)  +")");
				}
			}

			if (plano.getDiscenteExtensao().getDiscente() != null){
				ValidatorUtil.validateRequired(plano.getDiscenteExtensao().getDiscente(), "Discente", lista);
				ValidatorUtil.validateRequired(plano.getDiscenteExtensao().getTipoVinculo(), "Tipo de v�nculo", lista);
				ValidatorUtil.validateRequired(plano.getDiscenteExtensao().getJustificativa(), "Justificativa para sele��o do discente", lista);
			} else {
				lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,"Discente");
			}

			//Verifica se um discente volunt�rio est� sendo vinculado a um plano de bolsista.
			if (ValidatorUtil.isNotEmpty(plano.getTipoVinculo()) && plano.getDiscenteExtensao() != null && 
					ValidatorUtil.isNotEmpty(plano.getDiscenteExtensao().getTipoVinculo())) { 
				if (plano.getTipoVinculo().getId() != plano.getDiscenteExtensao().getTipoVinculo().getId()) {
					lista.addErro("Tipo de v�nculo do discente e do plano de trabalho s�o incompat�veis.");	
				}			
			}
			
			ValidatorUtil.validaData(Formatador.getInstance().formatarData(plano.getDiscenteExtensao().getDataInicio()), "Data de In�cio do Discente", lista);
			if ( !isEmpty(plano.getDiscenteExtensao().getDiscente()) ) {
				validaIndicacaoDiscenteInteressado(plano.getDiscenteExtensao(), lista);
				validaBolsista(plano, lista, plano.getDiscenteExtensao(), false);
			}
		}			
	}



	/**
	 * Valida a indica��o de discentes, verifica bolsas dispon�veis, etc...
	 * 
	 * @author ilueny santos
	 * @throws ArqException 
	 * 
	 */
	public static void validaIndicarDiscente(PlanoTrabalhoExtensao plano, ListaMensagens lista) throws ArqException {

		DiscenteExtensao de = plano.getDiscenteExtensao();
		DiscenteExtensao den = plano.getDiscenteExtensaoNovo();

		if (de != null){			
			ValidatorUtil.validaData(Formatador.getInstance().formatarData(de.getDataFim()), "Data Fim do Discente Atual", lista);			
			ValidatorUtil.validaInicioFim(de.getDataInicio(), de.getDataFim(), "Data Fim do Discente Atual", lista);
			ValidatorUtil.validateRequired(de.getMotivoSubstituicao(), "Motivo da Substitui��o", lista);
		}

		if (den != null){
			ValidatorUtil.validateRequired(den.getDiscente(), "Novo Discente", lista);
			ValidatorUtil.validateRequired(den.getTipoVinculo(), "Tipo de V�nculo do Novo Discente ", lista);
			ValidatorUtil.validaData(Formatador.getInstance().formatarData(den.getDataInicio()), "Data In�cio do Novo Discente", lista);

			if(lista.isEmpty()) {
				//se o discente antigo ainda estiver na bolsa ser� feita uma substitui��o.
				boolean substituicao = (de != null);
				validaBolsista(plano, lista, den, substituicao);
			}
		}

		if (de != null && den != null) {
			validaDataSaidaDataEntradaBolsistas(de, den, lista);
		}
		
		validaIndicacaoDiscenteInteressado(den, lista);
	}

	/**
	 * Verifica se o antigo discente est� saindo depois do novo bolsista.
	 * Utilizado na substitui��o de discentes no plano de trabalho.
	 * 
	 * @param bolsistaAntigo
	 * @param bolsitaNovo
	 * @param lista
	 * @throws ArqException
	 */
	public static void validaDataSaidaDataEntradaBolsistas(DiscenteExtensao bolsistaAntigo, DiscenteExtensao bolsitaNovo, ListaMensagens lista) throws ArqException {
		Date dataFimBolsaAntiga = bolsistaAntigo.getDataFim();
		Date dataInicioBolsaNova = bolsitaNovo.getDataInicio();

		if (dataFimBolsaAntiga != null && dataInicioBolsaNova != null && dataFimBolsaAntiga.getTime() > dataInicioBolsaNova.getTime()) { 
			lista.addErro("A data de finaliza��o do antigo bolsista deve ser anterior a data de in�cio do novo bolsista.");
		}
	}



	/**
	 * Valida��o s� para bolsistas de extens�o
	 * 
	 * M�todo utilizado para verificar se o discente de extens�o informado 
	 * j� possui bolsa ativa no sipac, se j� faz parte de outra a��o de extens�o
	 * e se os dados banc�rios est�o devidamente cadastrados (para o caso de alunos bolsistas)
	 * 
	 * @param plano
	 * @param lista
	 * @param de
	 * @param substituicao indica que est� avendo substitui��o de discente no projeto, assim uma bolsa na contagem total ser� desconsiderada.
	 * @throws DAOException
	 * @throws ArqException
	 */
	public static void validaBolsista(PlanoTrabalhoExtensao plano, ListaMensagens lista, DiscenteExtensao de, boolean substituicao)
		throws DAOException, ArqException {

		if ((de.getTipoVinculo() != null) && (de.getTipoVinculo().getId() > 0)) {
			DiscenteExtensaoDao dao = DAOFactory.getInstance().getDAO(DiscenteExtensaoDao.class);
			TipoBolsaPesquisaDao tdao = null;
			try {

				DiscenteExtensao discenteAtivoNoBanco = dao.findByDiscenteExtensao(de.getDiscente().getId(), TipoSituacaoDiscenteExtensao.ATIVO);

				//Verificando se o discente enviou relat�rio final
				if(!ValidatorUtil.isEmpty(discenteAtivoNoBanco)) {
					discenteAtivoNoBanco.setRelatorios(dao.findByExactField(RelatorioBolsistaExtensao.class, "discenteExtensao.id", discenteAtivoNoBanco.getId()));
					if (!discenteAtivoNoBanco.isEnviouRelatorioFinal()) {
						dao.initialize(discenteAtivoNoBanco);
						lista.addErro("O(a) discente " + discenteAtivoNoBanco.getDiscente().getMatriculaNome() +  " n�o realizou o envio do relat�rio " +
							" final das atividades desenvolvidas na outra a��o de extens�o, onde o mesmo apresenta v�nculo de bolsista ativo. ");
					}
				}

				if (de.isBolsista()){
					if (de.isBolsistaInterno()){
						if (de.getBanco() == null) {
							de.setBanco(new Banco());
						}
						ValidatorUtil.validateRequiredId(de.getBanco().getId(), "Banco", lista);
						ValidatorUtil.validateRequired(de.getAgencia(), "Ag�ncia", lista);
						ValidatorUtil.validateRequired(de.getConta(), "Conta", lista);
						if(de.getConta() != null) {
							ValidatorUtil.validateMaxLength(de.getConta(), 15, "Conta", lista);
						}
						if(de.getAgencia() != null) {
							ValidatorUtil.validateMaxLength(de.getAgencia(), 15, "Ag�ncia", lista);
						}
						//Verifica se ainda tem bolsas livres na a��o
						validaLimiteBolsistas(plano.getAtividade(), substituicao, lista);
						validaCalendarioPeriodoBolsa(de, lista);
					}

					//Testando se discente j� participa e � membro ativo de alguma atividade de extens�o
					if( (!ValidatorUtil.isEmpty(discenteAtivoNoBanco)) && (discenteAtivoNoBanco.isBolsista()) ) {
						lista.addErro(de.getDiscente().getMatriculaNome() +  " n�o poder� assumir esta bolsa porque j� faz parte de outra a��o de extens�o com v�nculo de bolsista ativo.");
					}

					// Para um discente poder participar de extens�o com bolsa ele n�o deve possuir nenhum outro tipo de bolsa
					// se o discente j� for bolsista  na UFRN e o coordenador tentar vincul�-lo a outra bolsa o sistema tem que travar!
					if (Sistema.isSipacAtivo()) {
						if( IntegracaoBolsas.verificarCadastroBolsaSIPAC(de.getDiscente().getMatricula(), null) != 0 ) {
							lista.addErro("SIPAC: " + de.getDiscente().getMatriculaNome() + " n�o poder� assumir esta bolsa pois j� possui outra bolsa ativa.");
						}
					} else {
						tdao = DAOFactory.getInstance().getDAO(TipoBolsaPesquisaDao.class);
						//Verifica se j� possui indica��o de bolsa de monitoria ou pesquisa quando o banco administrativo n�o est� ativo.
						boolean pesquisa = tdao.verificarIndicacaoPesquisa(de.getDiscente().getMatricula());
						boolean extensao = IntegracaoBolsas.verificarIndicacaoExtensao(de.getDiscente().getMatricula());
						boolean monitoria = IntegracaoBolsas.verificarIndicacaoMonitoria(de.getDiscente().getMatricula());
						if (pesquisa || extensao || monitoria)
							lista.addErro(de.getDiscente().getMatriculaNome() + " n�o poder� assumir esta bolsa pois j� possui outra bolsa ativa.");
					}
				}
			} finally {
				dao.close();
				if (tdao!=null)
					tdao.close();
			}

		}else{ //sem vinculo!
			lista.addErro(de.getDiscente().getMatriculaNome() +  " n�o tem v�culo definido com a a��o de extens�o. Selecione um v�nculo entre bolsista, volunt�rio ou em atividade curricular.");
		}
	}




	/**
	 * Verifica se o projeto ainda possui bolsas livres.
	 * 
	 * @param atividade
	 * @param substituicao Informa se � um caso de substitui��o. Nesses casos conta 1 bolsa a menos do total encontrado.
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaLimiteBolsistas(AtividadeExtensao atividade, boolean substituicao, ListaMensagens lista)	throws DAOException {

		DiscenteExtensaoDao dao = DAOFactory.getInstance().getDAO(DiscenteExtensaoDao.class);
		int totalAtivos = dao.findTotalBolsistasByAtividade(atividade.getId());
		try {
			//Nos casos de substitui��o de bolsistas a bolsa do discente atual n�o pode ser contada.
			if (substituicao) {
				totalAtivos = totalAtivos - 1;
			}   
	
			if (totalAtivos >= (atividade.getBolsasConcedidas())) {
				lista.addErro("N�o h� mais bolsas dispon�veis para esta a��o de extens�o. Verifique o n�mero de bolsas concedidas.");
			}
		}finally {
			dao.close();
		}

	}



	/**
	 * Valida a finaliza��o de discentes.
	 * 
	 * @author ilueny santos
	 * @throws DAOException 
	 * 
	 */
	public static void validaFinalizarDiscente(PlanoTrabalhoExtensao plano, ListaMensagens lista) throws DAOException {
		DiscenteExtensao de = plano.getDiscenteExtensao();
		
		if (de != null){			
			ValidatorUtil.validaData(Formatador.getInstance().formatarData(de.getDataFim()), "Data Fim do Discente Atual", lista);

			if (de.getDataFim() != null && de.getDataFim().getTime() > new Date().getTime()){
				lista.addErro("Data de finaliza��o deve ser menor ou igual a data atual.");
			}		    

			if (de.getDataFim() != null && plano.getDataFim() != null && de.getDataFim().getTime() > plano.getDataFim().getTime()) {
				lista.addErro("Data de finaliza��o deve ser menor ou igual a data de finaliza��o do plano de trabalho do discente.");
			}
			
			if (de.getSituacaoDiscenteExtensao().getId() != TipoSituacaoDiscenteExtensao.ATIVO) {
				lista.addErro("Somente discentes ativos podem ser finalizados.");
			}
			
			ValidatorUtil.validateRequired(de.getMotivoSubstituicao(), "Motivo", lista);
		}
		
	}




	/**
	 * Validar cronograma do plano de trabalho de extens�o
	 * 
	 * @param planoTrabalho
	 * @param lista
	 */
	public static void validarCronogramaExtensao(PlanoTrabalhoExtensao planoTrabalhoExtensao,
			ListaMensagens lista) {

		for (CronogramaProjeto crono : planoTrabalhoExtensao.getCronogramas()) {
			if (crono.getDescricao().trim().length() > 1000) {
				lista.addErro("A descricao da atividade <i>"
						+ crono.getDescricao()
						+ "<i> deve conter no m�ximo 1000 caracteres.");
			}
		}
	}
	
	/**
	 * Verifica se a data escolhida para a indica��o da bolsa esta entre o per�odo definido no calend�rio de extens�o.
	 * 
	 * @param dataCorrente
	 * @param calendario
	 * @return
	 * @throws DAOException 
	 */
	public static void validaCalendarioPeriodoBolsa(DiscenteExtensao de, ListaMensagens lista) throws DAOException {
		PlanoTrabalhoExtensaoDao dao = DAOFactory.getInstance().getDAO(PlanoTrabalhoExtensaoDao.class);
		try {
			CalendarioExtensao calendario = dao.findByExactField(CalendarioExtensao.class, "anoReferencia", CalendarUtils.getAnoAtual(), true);
			if (calendario == null) {
				lista.addErro("Calend�rio de Extens�o com o Per�odo de indica��o das bolsas de extens�o n�o foi definido pela Pr�-Reitoria de Extens�o");
			}else {
				if(!CalendarUtils.isDentroPeriodo(calendario.getInicioCadastroBolsa(), calendario.getFimCadastroBolsa(), de.getDataInicio())) {
					lista.addErro("Data de In�cio do Discente deve estar dentro do per�odo de autoriza��o para indica��o. " +
							"Este per�odo � definido pela Pr�-Reitoria de Extens�o e atualmente estende-se de " + 
							Formatador.getInstance().formatarData(calendario.getInicioCadastroBolsa()) +" at� " +
							Formatador.getInstance().formatarData(calendario.getFimCadastroBolsa())); 
				}
			}
		}finally {
			dao.close();
		}
	}

}