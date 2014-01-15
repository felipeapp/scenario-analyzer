/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Classe que possui os métodos necessários para a validação de um plano de Trabalho de um Discente.
 * 
 * 
 * @author UFRN
 *
 */
public class PlanoTrabalhoValidator {
    
    
    
    	/**
    	 * Verifica se a ação de extensão pode receber um novo plano de trabalho.
    	 * 
    	 * @param plano
    	 * @param lista
    	 * @throws DAOException
    	 * @throws ArqException
    	 */
	public static void validaAcao(AtividadeExtensao acao, ListaMensagens lista) {
	    	if (ValidatorUtil.isEmpty(acao)) {
		    lista.addMensagem(new MensagemAviso("Ação de Extensão não selecionada.", TipoMensagemUFRN.ERROR));
		}	
	    	// @negocio: Somente ações de extensão enviadas para avaliação dos membros do comitê de ensino, pesquisa e extensão podem cadastrar planos.
		if (acao.isProjetoAssociado() && ValidatorUtil.isEmpty(acao.getDataEnvio())) {
		    lista.addMensagem(MensagensExtensao.CADASTRO_PLANO_NAO_PERMITIDO_PARA_UMA_ACAO);
		}
	}

	/**
	 * Verifica se o discente indicado demonstrou interesse em participar da ação de extensão.
	 * Proíbe a indicação de algum discente que não esteja na lista.
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
				lista.addErro("Somente discentes que aderiram ao Cadastro Único e que demonstraram interesse em participar desta Ação de Extensão podem ser cadastrados neste plano de trabalho.");
				lista.addErro(de.getDiscente().getMatriculaNome() + " não faz parte da lista de interessados desta ação.");
		    }
		}finally {
		    dao.close();
		}
	}
	
	/**
	 * Classe com métodos responsáveis pela validação das operações realizadas
	 * com planos de trabalho dos discentes.
	 * 
	 * @author ilueny santos
	 * @throws ArqException 
	 * @throws DAOException 
	 * 
	 */
	public static void validaDadosGerais(PlanoTrabalhoExtensao plano, ListaMensagens lista) throws DAOException, ArqException {

		ValidatorUtil.validaData(Formatador.getInstance().formatarData(plano.getDataInicio()), "Início do período do plano", lista);
		ValidatorUtil.validaData(Formatador.getInstance().formatarData(plano.getDataFim()), "Fim do período do plano", lista);		
		ValidatorUtil.validateRequired(plano.getAtividade(),"Ação de Extensão", lista);
		ValidatorUtil.validateRequired(plano.getLocalTrabalho(), "Local Trabalho", lista);
		ValidatorUtil.validateRequired(plano.getJustificativa(), "Justificativa", lista);
		ValidatorUtil.validateRequired(plano.getObjetivo(), "Objetivos", lista);
		ValidatorUtil.validateRequired(plano.getOrientador(), "Orientador(a)", lista);
		ValidatorUtil.validateRequired(plano.getDescricaoAtividades(), "Atividade desenvolvidas", lista);

		if (plano.isValido()) {
			ValidatorUtil.validaInicioFim(plano.getDataInicio(), plano.getDataFim(), "Período do plano", lista);

			String inicioAcao = Formatador.getInstance().formatarData(plano.getAtividade().getProjeto().getDataInicio());
			String fimAcao = Formatador.getInstance().formatarData(plano.getAtividade().getProjeto().getDataFim()); 

			if (!plano.isPlanoNoPeriodoProjeto()) {
				lista.addErro("Período do plano de trabalho deve estar dentro do período de vigência da Ação de Extensão. " +
						"("+ inicioAcao + " até " + fimAcao  +")");
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
					lista.addErro("Data de início do discente deve estar dentro do período de vigência do Plano de Trabalho. " +
							"("+  Formatador.getInstance().formatarData(inicioPlano) + " até " +  Formatador.getInstance().formatarData(fimPlano)  +")");
				}
			}

			if (plano.getDiscenteExtensao().getDiscente() != null){
				ValidatorUtil.validateRequired(plano.getDiscenteExtensao().getDiscente(), "Discente", lista);
				ValidatorUtil.validateRequired(plano.getDiscenteExtensao().getTipoVinculo(), "Tipo de vínculo", lista);
				ValidatorUtil.validateRequired(plano.getDiscenteExtensao().getJustificativa(), "Justificativa para seleção do discente", lista);
			} else {
				lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,"Discente");
			}

			//Verifica se um discente voluntário está sendo vinculado a um plano de bolsista.
			if (ValidatorUtil.isNotEmpty(plano.getTipoVinculo()) && plano.getDiscenteExtensao() != null && 
					ValidatorUtil.isNotEmpty(plano.getDiscenteExtensao().getTipoVinculo())) { 
				if (plano.getTipoVinculo().getId() != plano.getDiscenteExtensao().getTipoVinculo().getId()) {
					lista.addErro("Tipo de vínculo do discente e do plano de trabalho são incompatíveis.");	
				}			
			}
			
			ValidatorUtil.validaData(Formatador.getInstance().formatarData(plano.getDiscenteExtensao().getDataInicio()), "Data de Início do Discente", lista);
			if ( !isEmpty(plano.getDiscenteExtensao().getDiscente()) ) {
				validaIndicacaoDiscenteInteressado(plano.getDiscenteExtensao(), lista);
				validaBolsista(plano, lista, plano.getDiscenteExtensao(), false);
			}
		}			
	}



	/**
	 * Valida a indicação de discentes, verifica bolsas disponíveis, etc...
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
			ValidatorUtil.validateRequired(de.getMotivoSubstituicao(), "Motivo da Substituição", lista);
		}

		if (den != null){
			ValidatorUtil.validateRequired(den.getDiscente(), "Novo Discente", lista);
			ValidatorUtil.validateRequired(den.getTipoVinculo(), "Tipo de Vínculo do Novo Discente ", lista);
			ValidatorUtil.validaData(Formatador.getInstance().formatarData(den.getDataInicio()), "Data Início do Novo Discente", lista);

			if(lista.isEmpty()) {
				//se o discente antigo ainda estiver na bolsa será feita uma substituição.
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
	 * Verifica se o antigo discente está saindo depois do novo bolsista.
	 * Utilizado na substituição de discentes no plano de trabalho.
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
			lista.addErro("A data de finalização do antigo bolsista deve ser anterior a data de início do novo bolsista.");
		}
	}



	/**
	 * Validação só para bolsistas de extensão
	 * 
	 * Método utilizado para verificar se o discente de extensão informado 
	 * já possui bolsa ativa no sipac, se já faz parte de outra ação de extensão
	 * e se os dados bancários estão devidamente cadastrados (para o caso de alunos bolsistas)
	 * 
	 * @param plano
	 * @param lista
	 * @param de
	 * @param substituicao indica que está avendo substituição de discente no projeto, assim uma bolsa na contagem total será desconsiderada.
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

				//Verificando se o discente enviou relatório final
				if(!ValidatorUtil.isEmpty(discenteAtivoNoBanco)) {
					discenteAtivoNoBanco.setRelatorios(dao.findByExactField(RelatorioBolsistaExtensao.class, "discenteExtensao.id", discenteAtivoNoBanco.getId()));
					if (!discenteAtivoNoBanco.isEnviouRelatorioFinal()) {
						dao.initialize(discenteAtivoNoBanco);
						lista.addErro("O(a) discente " + discenteAtivoNoBanco.getDiscente().getMatriculaNome() +  " não realizou o envio do relatório " +
							" final das atividades desenvolvidas na outra ação de extensão, onde o mesmo apresenta vínculo de bolsista ativo. ");
					}
				}

				if (de.isBolsista()){
					if (de.isBolsistaInterno()){
						if (de.getBanco() == null) {
							de.setBanco(new Banco());
						}
						ValidatorUtil.validateRequiredId(de.getBanco().getId(), "Banco", lista);
						ValidatorUtil.validateRequired(de.getAgencia(), "Agência", lista);
						ValidatorUtil.validateRequired(de.getConta(), "Conta", lista);
						if(de.getConta() != null) {
							ValidatorUtil.validateMaxLength(de.getConta(), 15, "Conta", lista);
						}
						if(de.getAgencia() != null) {
							ValidatorUtil.validateMaxLength(de.getAgencia(), 15, "Agência", lista);
						}
						//Verifica se ainda tem bolsas livres na ação
						validaLimiteBolsistas(plano.getAtividade(), substituicao, lista);
						validaCalendarioPeriodoBolsa(de, lista);
					}

					//Testando se discente já participa e é membro ativo de alguma atividade de extensão
					if( (!ValidatorUtil.isEmpty(discenteAtivoNoBanco)) && (discenteAtivoNoBanco.isBolsista()) ) {
						lista.addErro(de.getDiscente().getMatriculaNome() +  " não poderá assumir esta bolsa porque já faz parte de outra ação de extensão com vínculo de bolsista ativo.");
					}

					// Para um discente poder participar de extensão com bolsa ele não deve possuir nenhum outro tipo de bolsa
					// se o discente já for bolsista  na UFRN e o coordenador tentar vinculá-lo a outra bolsa o sistema tem que travar!
					if (Sistema.isSipacAtivo()) {
						if( IntegracaoBolsas.verificarCadastroBolsaSIPAC(de.getDiscente().getMatricula(), null) != 0 ) {
							lista.addErro("SIPAC: " + de.getDiscente().getMatriculaNome() + " não poderá assumir esta bolsa pois já possui outra bolsa ativa.");
						}
					} else {
						tdao = DAOFactory.getInstance().getDAO(TipoBolsaPesquisaDao.class);
						//Verifica se já possui indicação de bolsa de monitoria ou pesquisa quando o banco administrativo não está ativo.
						boolean pesquisa = tdao.verificarIndicacaoPesquisa(de.getDiscente().getMatricula());
						boolean extensao = IntegracaoBolsas.verificarIndicacaoExtensao(de.getDiscente().getMatricula());
						boolean monitoria = IntegracaoBolsas.verificarIndicacaoMonitoria(de.getDiscente().getMatricula());
						if (pesquisa || extensao || monitoria)
							lista.addErro(de.getDiscente().getMatriculaNome() + " não poderá assumir esta bolsa pois já possui outra bolsa ativa.");
					}
				}
			} finally {
				dao.close();
				if (tdao!=null)
					tdao.close();
			}

		}else{ //sem vinculo!
			lista.addErro(de.getDiscente().getMatriculaNome() +  " não tem vículo definido com a ação de extensão. Selecione um vínculo entre bolsista, voluntário ou em atividade curricular.");
		}
	}




	/**
	 * Verifica se o projeto ainda possui bolsas livres.
	 * 
	 * @param atividade
	 * @param substituicao Informa se é um caso de substituição. Nesses casos conta 1 bolsa a menos do total encontrado.
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaLimiteBolsistas(AtividadeExtensao atividade, boolean substituicao, ListaMensagens lista)	throws DAOException {

		DiscenteExtensaoDao dao = DAOFactory.getInstance().getDAO(DiscenteExtensaoDao.class);
		int totalAtivos = dao.findTotalBolsistasByAtividade(atividade.getId());
		try {
			//Nos casos de substituição de bolsistas a bolsa do discente atual não pode ser contada.
			if (substituicao) {
				totalAtivos = totalAtivos - 1;
			}   
	
			if (totalAtivos >= (atividade.getBolsasConcedidas())) {
				lista.addErro("Não há mais bolsas disponíveis para esta ação de extensão. Verifique o número de bolsas concedidas.");
			}
		}finally {
			dao.close();
		}

	}



	/**
	 * Valida a finalização de discentes.
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
				lista.addErro("Data de finalização deve ser menor ou igual a data atual.");
			}		    

			if (de.getDataFim() != null && plano.getDataFim() != null && de.getDataFim().getTime() > plano.getDataFim().getTime()) {
				lista.addErro("Data de finalização deve ser menor ou igual a data de finalização do plano de trabalho do discente.");
			}
			
			if (de.getSituacaoDiscenteExtensao().getId() != TipoSituacaoDiscenteExtensao.ATIVO) {
				lista.addErro("Somente discentes ativos podem ser finalizados.");
			}
			
			ValidatorUtil.validateRequired(de.getMotivoSubstituicao(), "Motivo", lista);
		}
		
	}




	/**
	 * Validar cronograma do plano de trabalho de extensão
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
						+ "<i> deve conter no máximo 1000 caracteres.");
			}
		}
	}
	
	/**
	 * Verifica se a data escolhida para a indicação da bolsa esta entre o período definido no calendário de extensão.
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
				lista.addErro("Calendário de Extensão com o Período de indicação das bolsas de extensão não foi definido pela Pró-Reitoria de Extensão");
			}else {
				if(!CalendarUtils.isDentroPeriodo(calendario.getInicioCadastroBolsa(), calendario.getFimCadastroBolsa(), de.getDataInicio())) {
					lista.addErro("Data de Início do Discente deve estar dentro do período de autorização para indicação. " +
							"Este período é definido pela Pró-Reitoria de Extensão e atualmente estende-se de " + 
							Formatador.getInstance().formatarData(calendario.getInicioCadastroBolsa()) +" até " +
							Formatador.getInstance().formatarData(calendario.getFimCadastroBolsa())); 
				}
			}
		}finally {
			dao.close();
		}
	}

}