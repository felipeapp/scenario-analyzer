package br.ufrn.sigaa.projetos.negocio;

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
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.bolsas.negocio.IntegracaoBolsas;
import br.ufrn.sigaa.arq.dao.projetos.DiscenteProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.InscricaoSelecaoProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.pessoa.dominio.Banco;
import br.ufrn.sigaa.projetos.dominio.CalendarioProjeto;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;
import br.ufrn.sigaa.projetos.dominio.DiscenteProjeto;
import br.ufrn.sigaa.projetos.dominio.InscricaoSelecaoProjeto;
import br.ufrn.sigaa.projetos.dominio.PlanoTrabalhoProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;

/**
 * Classe que possui os métodos necessários para a validação de um Plano de Trabalho de um Discente
 * de Projeto.
 * 
 * @author Ilueny Santos
 *  
 */
public class PlanoTrabalhoProjetoValidator {
    
    	/**
    	 * Verifica se o projeto pode receber um novo plano de trabalho.
    	 * 
    	 * @param plano
    	 * @param lista
    	 * @throws DAOException
    	 * @throws ArqException
    	 */
	public static void validaNovoPlano(Projeto projeto, ListaMensagens lista) {
		ProjetoDao dao = DAOFactory.getInstance().getDAO(ProjetoDao.class);
		
		if (ValidatorUtil.isEmpty(projeto)) {
			lista.addMensagem(new MensagemAviso("Projeto não selecionado.", TipoMensagemUFRN.ERROR));
		}
		
		boolean aprovado = dao.isProjetoPassouPorSituacao(projeto.getId(), 
					new Integer[] { TipoSituacaoProjeto.PROJETO_BASE_APROVADO_COM_RECURSOS, 
									TipoSituacaoProjeto.PROJETO_BASE_APROVADO_SEM_RECURSOS,
									TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO });
		
//		boolean coordenadorAceitouExecucao = dao.isProjetoPassouPorSituacao(projeto.getId(), 
//				new Integer[] {TipoSituacaoProjeto.PROJETO_BASE_COORDENACAO_ACEITOU_EXECUCAO});
		
		// @negocio: Somente projetos aprovados na avaliação dos membros do comitê de ensino, pesquisa e extensão podem cadastrar planos.
		// @negocio: Somente projetos aceitos pelo coordenador podem cadastrar planos de trabalho.
		if (!aprovado ) {
			lista.addMensagem(new MensagemAviso("Somente projetos aprovados e com a execução aceita pela coordenação podem cadastrar planos de trabalho.", TipoMensagemUFRN.ERROR));
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
	public static void validaIndicacaoDiscenteInteressado(DiscenteProjeto de, ListaMensagens lista) throws ArqException {
		InscricaoSelecaoProjetoDao dao = DAOFactory.getInstance().getDAO(InscricaoSelecaoProjetoDao.class);
		try {
		    boolean inscrito = false;
		    Collection<InscricaoSelecaoProjeto> inscritos = dao.findInscritosProcessoSeletivoByProjeto(de.getProjeto().getId());
		    for (InscricaoSelecaoProjeto inscricao : inscritos) {
			if (inscricao.getDiscente().equals(de.getDiscente())) {
			    inscrito = true;
			    break;
			}
		    }
		    if (!inscrito) { 
			lista.addErro("Somente discentes que aderiram ao Cadastro Único e que demonstraram interesse em participar desta Ação Associada podem ser cadastrados neste plano de trabalho.");
			lista.addErro(de.getDiscente().getMatriculaNome() + " não faz parte da lista de interessados desta ação associada.");
		    }
		}finally {
		    dao.close();
		}
	}

	
	/**
	 * Valida dados gerais do cadastro do plano.
	 * 
	 * @author ilueny santos
	 * @throws ArqException 
	 * @throws DAOException 
	 * 
	 */
	public static void validaDadosGerais(PlanoTrabalhoProjeto plano, ListaMensagens lista, CalendarioAcademico calendario) throws DAOException, ArqException {

		ValidatorUtil.validateRequired(plano.getProjeto(),"Projeto", lista);
		ValidatorUtil.validateRequired(Formatador.getInstance().formatarData(plano.getDataInicio()), "Período Inicial", lista);
		ValidatorUtil.validateRequired(Formatador.getInstance().formatarData(plano.getDataFim()), "Período Final", lista);

		if ((plano.getDataInicio() != null) && (plano.getDataFim() != null)) {
			ValidatorUtil.validaInicioFim(plano.getDataInicio(), plano.getDataFim(), "Período", lista);
			
			boolean inicioForaProjeto = CalendarUtils.isDentroPeriodo(plano.getProjeto().getDataInicio(), plano.getProjeto().getDataFim(), plano.getDataInicio());
			boolean fimForaProjeto = CalendarUtils.isDentroPeriodo(plano.getProjeto().getDataInicio(), plano.getProjeto().getDataFim(), plano.getDataFim());
			boolean discenteInicioForaPlano = CalendarUtils.isDentroPeriodo(plano.getDataInicio(), plano.getDataFim(), plano.getDiscenteProjeto().getDataInicio());
			if (!inicioForaProjeto || !fimForaProjeto || !discenteInicioForaPlano) {
				lista.addErro("Período do Plano de Trabalho e do Discente devem estar dentro do período de vigência do Projeto. (" 
						+ Formatador.getInstance().formatarData(plano.getProjeto().getDataInicio()) + " até " 
						+ Formatador.getInstance().formatarData(plano.getProjeto().getDataFim()) +")");
			}
		}

		ValidatorUtil.validateRequired(plano.getLocalTrabalho(), "Local Trabalho", lista);
		ValidatorUtil.validateRequired(plano.getJustificativa(), "Justificativa", lista);
		ValidatorUtil.validateRequired(plano.getObjetivos(), "Objetivos", lista);
		ValidatorUtil.validateRequired(plano.getMetodologia(), "Metodologia/Atividades desenvolvidas", lista);

		if(plano.getDiscenteProjeto() != null && !ValidatorUtil.isEmpty(plano.getDiscenteProjeto().getDiscente())){
				ValidatorUtil.validateRequired(plano.getDiscenteProjeto().getTipoVinculo(), "Tipo de vínculo", lista);
				ValidatorUtil.validateRequired(plano.getDiscenteProjeto().getJustificativa(), "Justificativa para seleção do discente", lista);
				validaBolsista(plano, lista, plano.getDiscenteProjeto(), false);
		}else {
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,"Discente");
		}
		
		//Verifica se um discente voluntário está sendo vinculado a um plano de bolsista.
		if (ValidatorUtil.isNotEmpty(plano.getTipoVinculo()) && plano.getDiscenteProjeto() != null && 
				ValidatorUtil.isNotEmpty(plano.getDiscenteProjeto().getTipoVinculo())) { 
			if (plano.getTipoVinculo().getId() != plano.getDiscenteProjeto().getTipoVinculo().getId()) {
				lista.addErro("Tipo de vínculo do discente e do plano de trabalho são incompatíveis.");	
			}			
		}

		ValidatorUtil.validaData(Formatador.getInstance().formatarData(plano.getDiscenteProjeto().getDataInicio()), "Data Início Discente", lista);
		validaIndicacaoDiscenteInteressado(plano.getDiscenteProjeto(), lista); 
	}

	/**
	 * Verifica se o antigo discente está saindo depois do novo bolsista.
	 * Utilizado na substituição de discentes no plano de trabalho.
	 * 
	 * @param discenteAntigo
	 * @param discenteNovo
	 * @param lista
	 * @throws ArqException
	 */
	public static void validaDataSaidaDataEntradaBolsistas(DiscenteProjeto discenteAntigo, DiscenteProjeto discenteNovo, ListaMensagens lista) throws ArqException {
		Date dataFimBolsaAntiga = discenteAntigo.getDataFim();
		Date dataInicioBolsaNova = discenteNovo.getDataInicio();

		if (dataFimBolsaAntiga != null && dataInicioBolsaNova != null && dataFimBolsaAntiga.getTime() > dataInicioBolsaNova.getTime()) { 
			lista.addErro("Data da Finalização do Discente Atual: Informe uma data de início menor que a data de fim.");
		}
	}



	/**
	 * Validação só para bolsistas de projetos
	 * 
	 * Método utilizado para verificar se o discente de projeto informado 
	 * já possui bolsa ativa no sipac, se já faz parte de outro projeto
	 * e se os dados bancários estão devidamente cadastrados (para o caso de alunos bolsistas)
	 * 
	 * @param plano
	 * @param lista
	 * @param discenteProjeto
	 * @param substituicao indica que está avendo substituição de discente no projeto, assim uma bolsa na contagem total será desconsiderada.
	 * @throws DAOException
	 * @throws ArqException
	 */
	public static void validaBolsista(PlanoTrabalhoProjeto plano, ListaMensagens lista, DiscenteProjeto discenteProjeto, boolean substituicao)
	throws DAOException, ArqException {

		if ((discenteProjeto.getTipoVinculo() != null) && (discenteProjeto.getTipoVinculo().getId() > 0)) {
		
			if (discenteProjeto.getTipoVinculo().isRemunerado()) {
				if (discenteProjeto.getBanco() == null) {
					discenteProjeto.setBanco(new Banco());
				}
	
				ValidatorUtil.validateRequiredId(discenteProjeto.getBanco().getId(), "Banco", lista);
				ValidatorUtil.validateRequired(discenteProjeto.getAgencia(), "Agência", lista);
				ValidatorUtil.validateRequired(discenteProjeto.getConta(), "Conta", lista);
	
				if(discenteProjeto.getConta() != null) {
					ValidatorUtil.validateMaxLength(discenteProjeto.getConta(), 15, "Conta", lista);
				}
				if(discenteProjeto.getAgencia() != null) {
					ValidatorUtil.validateMaxLength(discenteProjeto.getAgencia(), 15, "Agência", lista);
				}
	
				if(discenteProjeto.getTipoVinculo().getId() != TipoVinculoDiscente.ACAO_ASSOCIADA_VOLUNTARIO ){
					//Verifica se ainda tem bolsas livres no projeto
					if(!plano.isAlterando()){
						validaLimiteBolsistas(plano.getProjeto(), substituicao, lista);
					}
				}
	
				// Para um discente poder participar de projeto com bolsa ele não deve possuir nenhum outro tipo de bolsa
				// se o discente já for bolsista  na IFES e o coordenador tentar vinculá-lo a outra bolsa o sistema tem que travar.
				if( IntegracaoBolsas.verificarCadastroBolsaSIPAC(discenteProjeto.getDiscente().getMatricula(), null) != 0 ) {
					lista.addErro(RepositorioDadosInstitucionais.get("siglaSipac")+ ": " + discenteProjeto.getDiscente().getMatriculaNome() + " não poderá assumir esta bolsa pois já possui outra bolsa ativa.");
				}
				
				// Verificando se o discente já possui bolsa remunerada em outro projeto ativo.
				DiscenteProjetoDao dao = DAOFactory.getInstance().getDAO(DiscenteProjetoDao.class);
				try {
					Collection<DiscenteProjeto> discentesEmProjetos = dao.findByDiscentesEmProjetos(discenteProjeto.getDiscente().getId());
					for (DiscenteProjeto dep : discentesEmProjetos) {
						if (dep.getTipoVinculo().isRemunerado() && dep.getId() != discenteProjeto.getId()){
							lista.addErro(discenteProjeto.getDiscente().getMatriculaNome() + " não poderá assumir esta bolsa pois já possui outra bolsa ativa em projetos.");
						}
					}					
				}finally {
					dao.close();
				}
				
			}
		} else { //sem vinculo!
			lista.addErro(discenteProjeto.getDiscente().getMatriculaNome() +  " não tem vículo definido com o projeto. Selecione um vínculo entre as opções disponíveis.");
		}
	}


	/**
	 * Verifica se o projeto ainda possui bolsas livres.
	 * 
	 * @param projeto
	 * @param substituicao Informa se é um caso de substituição. Nesses casos conta 1 bolsa a menos do total encontrado.
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaLimiteBolsistas(Projeto projeto, boolean substituicao, ListaMensagens lista)	throws DAOException {

		ProjetoDao dao = DAOFactory.getInstance().getDAO(ProjetoDao.class);
		int planosAtivosComBolsas = dao.quantidadePlanosTrabalhoAtivos(projeto.getId(), TipoVinculoDiscente.ACAO_ASSOCIADA_BOLSISTA_INTERNO);
		
		//Nos casos de substituição de bolsistas a bolsa do discente atual não pode ser contada.
		if (substituicao) {
			planosAtivosComBolsas = planosAtivosComBolsas - 1;
		}   

		if ((projeto.getBolsasConcedidas() == null) || (planosAtivosComBolsas >= projeto.getBolsasConcedidas())) {
			lista.addErro("Não há bolsas disponíveis para este projeto. Verifique o número de bolsas concedidas.");
		}
	}



	/**
	 * Valida a finalização de discentes.
	 * 
	 * @author ilueny santos
	 * @throws DAOException 
	 * 
	 */
	public static void validaFinalizarDiscente(PlanoTrabalhoProjeto plano, ListaMensagens lista) throws DAOException {
		DiscenteProjeto discenteProjeto = plano.getDiscenteProjeto();
		if (discenteProjeto != null){			
			ValidatorUtil.validaData(Formatador.getInstance().formatarData(discenteProjeto.getDataFim()), "Data Fim do Discente Atual", lista);

			if (discenteProjeto.getDataFim() != null && discenteProjeto.getDataFim().getTime() > new Date().getTime()){
				lista.addErro("Data de finalização deve ser menor ou igual a data atual.");
			}		    

			if (discenteProjeto.getDataFim() != null && plano.getDataFim() != null && discenteProjeto.getDataFim().getTime() > plano.getDataFim().getTime()) {
				lista.addErro("Data de finalização deve ser menor ou igual a data de finalização do plano de trabalho do discente.");
			}
			
			//TODO: verificar necessidade de validação do envio dos relatórios obrigatórios do discente.
			
		}
	}


	/**
	 * Validar cronograma do plano de trabalho de extensão
	 * 
	 * @param planoTrabalho
	 * @param lista
	 */
	public static void validarCronogramaProjeto(PlanoTrabalhoProjeto plano,
			ListaMensagens lista) {

		ValidatorUtil.validateEmptyCollection("Informe ao menos uma atividade para o cronograma",
				plano.getCronogramas(), lista);


		for (CronogramaProjeto crono : plano.getCronogramas()) {
			if (crono.getDescricao().trim().length() > 1000) {
				lista.addErro("A descricao da atividade <i>"
						+ crono.getDescricao()
						+ "<i> deve conter no máximo 1000 caracteres.");
			}
		}
	}
	
	/**
	 * Verifica se a data escolhida para a indicação da bolsa esta entre o período definido no calendário de projeto.
	 * 
	 * @param dataCorrente
	 * @param calendario
	 * @return
	 */
	public static void estaDentroPeriodoBolsa(Date dataCorrente, final CalendarioProjeto calendario, ListaMensagens lista) {
		if(CalendarUtils.compareTo( dataCorrente, new Date() ) < 0 ) {
			lista.addErro("Não é possível fazer uma indicação de bolsa com data início do discente inferior a data atual.");
		}		
		
		if(calendario == null) {
			lista.addErro("Calendário de Projetos com o Período de indicação das bolsas não foi definido por um Gestor de Projetos.");
			return;
		}
		
		if(!CalendarUtils.isDentroPeriodo(calendario.getInicioCadastroBolsa(), calendario.getFimCadastroBolsa(), dataCorrente)) {
			lista.addErro("Data de Início informada nos Dados do Discente é inválida: A Data de Início deve estar dentro do período de indicação de discentes. " +
					      "Este período foi definido por um Gestor de Projetos e atualmente se estende de " + 
							CalendarUtils.format(calendario.getInicioCadastroBolsa(),"dd/MM/yyyy") +" até " +
							CalendarUtils.format(calendario.getFimCadastroBolsa(),"dd/MM/yyyy")); 
		}	
	}
	
	/**
	 * Valida a indicação de discentes, verifica bolsas disponíveis, etc...
	 * 
	 * @author ilueny santos
	 * @throws ArqException 
	 * 
	 */
	public static void validaIndicarDiscente(PlanoTrabalhoProjeto plano, ListaMensagens lista) throws ArqException {

		DiscenteProjeto de = plano.getDiscenteProjeto();
		DiscenteProjeto den = plano.getDiscenteProjetoNovo();

		if (de != null){			
			ValidatorUtil.validateRequired(Formatador.getInstance().formatarData(de.getDataFim()), "Data da Finalização do Discente Atual", lista);			
			ValidatorUtil.validaInicioFim(de.getDataInicio(), de.getDataFim(), "Data da Finalização do Discente Atual", lista);
			ValidatorUtil.validaDataAnteriorIgual(de.getDataFim(), new Date(), "Data da Finalização do Discente Atual", lista);
			ValidatorUtil.validateRequired(de.getMotivoSubstituicao(), "Motivo da Substituição", lista);
		}

		if (den != null){
			ValidatorUtil.validateRequired(den.getDiscente(), "Novo Discente", lista);
			ValidatorUtil.validateRequired(den.getTipoVinculo(), "Tipo de Vínculo do Novo Discente ", lista);
			ValidatorUtil.validateRequired(Formatador.getInstance().formatarData(den.getDataInicio()), "Data Início do Novo Discente", lista);

			if(den.getDataInicio().before(new Date()) && !DateUtils.isSameDay(den.getDataInicio(), new Date())){
				lista.addErro("Data Início do Novo Discente: esta data deve ser posterior a " + new SimpleDateFormat("dd/MM/yyyy").format(CalendarUtils.subtraiDias(new Date(), 1)) + "");
			}

			//se o discente antigo ainda estiver na bolsa será feita uma substituição.
			boolean substituicao = (de != null);
			validaBolsista(plano, lista, den, substituicao);

		}

		if (de != null && den != null) {
			validaDataSaidaDataEntradaBolsistas(de, den, lista);
		}
		
		validaIndicacaoDiscenteInteressado(den, lista);
		
	}

}