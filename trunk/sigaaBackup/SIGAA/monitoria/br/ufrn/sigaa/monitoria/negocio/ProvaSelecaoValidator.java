/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 04/08/2008
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecao;
import br.ufrn.sigaa.monitoria.dominio.TipoVinculoDiscenteMonitoria;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Classe responsável pela validação de Provas Seletivas de um Projeto de Monitoria.
 * 
 * @author IgorGeyson
 *
 */
public class ProvaSelecaoValidator {

	/**
	 * Verifica se ainda há vagas para monitores no projeto da prova seletiva informada.
	 * Verifica se a soma das vagas reservadas de todas as provas seletivas cadastradas, incluindo a informada, não
	 * ultrapassa o total de vagas concedidas ao projeto pela comissão de monitoria.
	 * 
	 * Utilizado na criação da prova seletiva
	 * 
	 * Prograd(Pró-Reitoria de Graduação)
	 * 
	 * @param prova
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaReservaVagaProva(ProvaSelecao prova, ListaMensagens lista)throws DAOException {
		
		ProjetoMonitoriaDao dao = DAOFactory.getInstance().getDAO(ProjetoMonitoriaDao.class);
		try{
			
			//Verificando o total de bolsas já reservadas em outras provas...
			int totalRemuneradasNasProvas = dao.findTotalBolsasRemuneradasProvas(prova);		
			totalRemuneradasNasProvas += prova.getVagasRemuneradas();			
			if(totalRemuneradasNasProvas > prova.getProjetoEnsino().getBolsasConcedidas()) {
				lista.addErro("Total de Bolsas Remuneradas reservadas ultrapassa o limite de bolsas concedidas ao projeto. " +
						"Reduza a quantidade de Bolsas Remuneradas da Prova Seletiva.");
			}
			
			//Verificando o total de vagas não remuneradas em outras provas
			int totalBolsasNaoRemuneradasNasProvas = dao.findTotalBolsasNaoRemuneradasProvas(prova);		
			totalBolsasNaoRemuneradasNasProvas += prova.getVagasNaoRemuneradas();			
			if(totalBolsasNaoRemuneradasNasProvas > prova.getProjetoEnsino().getBolsasNaoRemuneradas()) {
				lista.addErro("Total de Bolsas Não Remuneradas reservadas ultrapassa o limite de bolsas concedidas ao projeto. " +
						"Reduza a quantidade de Bolsas Não Remuneradas da Prova Seletiva.");
			}
			
			//Verificando o total de bolsas ativas na prova.			
			int totalBolsistasAtivos = dao.findTotalDiscentesAtivosProva(prova, TipoVinculoDiscenteMonitoria.BOLSISTA);			
			//proibindo o docente de reduzir as vagas reservadas da prova para um número inferior ao de bolsistas ativos
			if(prova.getVagasRemuneradas() < totalBolsistasAtivos) {
				lista.addErro("Total de Bolsas Remuneradas reservadas é menor que a quantidade de bolsistas remunerados ativos. " +
						"Aumente a quantidade de Bolsas Remuneradas da Prova Seletiva.");
			}
			
			//Verificando o total de bolsas ativas na prova.			
			int totalNaoRemuneradosAtivos = dao.findTotalDiscentesAtivosProva(prova, TipoVinculoDiscenteMonitoria.NAO_REMUNERADO);			
			//proibindo o docente de reduzir as vagas reservadas da prova para um número inferior ao de bolsistas ativos
			if(prova.getVagasNaoRemuneradas() < totalNaoRemuneradosAtivos) {
				lista.addErro("Total de Bolsas Não Remuneradas reservadas é menor que a quantidade de bolsistas não remunerados ativos. " +
						"Aumente a quantidade de Bolsas Não Remuneradas da Prova Seletiva.");
			}
			
			
			//Permite zerar bolsas de provas já realizadas (finalizada) para liberar vagas para novas provas
			//TODO CRIAR METODO DE FINALIZAÇÃO DE PROVAS SELETIVAS
			//TODO retirar data da prova do if e fazer a verificação pela situação da prova
																								//prova já realizada...
			if (prova.getProjetoEnsino().getProjeto().isInterno() && ( (!prova.isProvaConcluida()) && (prova.getVagasRemuneradas() == 0) && (prova.getVagasNaoRemuneradas() == 0)) ) {
				lista.addErro("O total de bolsas reservadas para a prova deve ser maior que 0 (zero).");
			}
			
			
		}finally{
			dao.close();
		}

		
	}
	
	
	/**
	 * Verifica se ainda há vagas para monitores na prova seletiva informada.
	 * A Prova Seletiva deve ter o resultado de seleção cadastrada.
	 * 
	 * utilizado na validação da prova seletiva pela prograd(Pró-Reitoria de Graduação)
	 * 
	 * @param prova
	 * @param lista
	 * @throws Exception 
	 */
	public static void validaBolsasDisponiveisProva(ProvaSelecao prova, ListaMensagens lista){
		
		if ((prova.getResultadoSelecao() != null) && (!prova.getResultadoSelecao().isEmpty())) {

			int totalBolsas = 0;
			int totalNaoRemunerados = 0;
			
			for (DiscenteMonitoria dm : prova.getResultadoSelecao()) {	
				// controlando alteração da quantidade de bolsas
				if ((dm.isAtivo()) && (dm.isVinculoBolsista() && 
						( ( dm.isAssumiuMonitoria() && dm.isConvocado())))) {
					totalBolsas++;
				}	
				if ((dm.isAtivo()) && (dm.isVinculoNaoRemunerado() && 
						( ( dm.isAssumiuMonitoria() && dm.isConvocado())))) {
					totalNaoRemunerados++;
				}	
			}
			
			// controlando alteração da quantidade de bolsas
			if (totalBolsas > prova.getVagasRemuneradas()) { 
				lista.addErro("Bolsas Remuneradas concedidas superou o limite reservado para prova.");
			}
			if (totalNaoRemunerados > prova.getVagasNaoRemuneradas()) { 
				lista.addErro("Bolsas Não Remuneradas concedidas superou o limite reservado para prova.");
			}
			
		}else {
			lista.addErro("Resultado da prova seletiva não foi cadastrado.");
		}

	}

	
	
	
	/**
	 * Verifica se ainda há vagas para monitores no projeto informado.
	 * 
	 * utilizado na validação da prova seletiva pela PROGRAD(Pró-Reitoria de Graduação)
	 * 
	 * @param prova
	 * @param lista
	 * @throws DAOException 
	 * @throws Exception 
	 */
	public static void validaBolsasDisponiveisProjeto(ProvaSelecao prova, ListaMensagens lista) throws DAOException{
		
		if ((prova.getResultadoSelecao() != null) && (!prova.getResultadoSelecao().isEmpty())){

			DiscenteMonitoriaDao dao = DAOFactory.getInstance().getDAO(DiscenteMonitoriaDao.class);			

			try{
					//somando bolsas ativas do projeto todo
					int totalBolsas = dao.countMonitoresByProjetoExcetoDaProva(prova.getProjetoEnsino().getId(), prova.getId(), TipoVinculoDiscenteMonitoria.BOLSISTA);
					int totalNaoRemunerados = dao.countMonitoresByProjetoExcetoDaProva(prova.getProjetoEnsino().getId(), prova.getId(), TipoVinculoDiscenteMonitoria.NAO_REMUNERADO);	
					
					for (DiscenteMonitoria dm : prova.getResultadoSelecao()) {			
						// controlando alteração da quantidade de bolsas
						if ((dm.isAssumiuMonitoria() || dm.isConvocado()) && (dm.isVinculoBolsista())) {
							totalBolsas++;
						}			
						if ((dm.isAssumiuMonitoria() || dm.isConvocado()) && (dm.isVinculoNaoRemunerado())) {
							totalNaoRemunerados++;
						}			
					}
					
					// controlando alteração da quantidade de bolsas
					if (totalBolsas > prova.getProjetoEnsino().getBolsasConcedidas()) { 
						lista.addErro("Bolsas Remuneradas concedidas superou o limite de bolsas do projeto.");
					}
			
					if (totalNaoRemunerados > prova.getProjetoEnsino().getBolsasNaoRemuneradas()) { 
						lista.addErro("Bolsas Não Remuneradas concedidas superou o limite de bolsas do projeto.");
					}
			
			}finally{
				dao.close();
			}

		}else {
			lista.addErro("Resultado da provas seletiva não foi cadastrado!");
		}

	}

	/**
	 * Verifica qual a situação atual do projeto.
	 * 
	 * @param projeto
	 * @param lista
	 */
	public static void validaPermissaoProjetoAtivo(ProjetoEnsino projeto, ListaMensagens lista){
		
		if( (projeto.getSituacaoProjeto().getId() != TipoSituacaoProjeto.MON_EM_EXECUCAO) &&
			(projeto.getSituacaoProjeto().getId() != TipoSituacaoProjeto.MON_RECOMENDADO) && 
			(projeto.getSituacaoProjeto().getId() != TipoSituacaoProjeto.MON_RENOVADO_SEM_ALTERACOES) &&
			(projeto.getSituacaoProjeto().getId() != TipoSituacaoProjeto.MON_RENOVADO_COM_REDUCAO_BOLSAS) &&
			(projeto.getSituacaoProjeto().getId() != TipoSituacaoProjeto.MON_AGUARDANDO_DISTRIBUICAO_DO_RELATORIO) &&
			//Projeto associado com projeto de monitoria.
			//TODO: corrigir quando a situação por descentralizada.
			(projeto.getSituacaoProjeto().getId() != TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO) ){
			
			lista.addErro("A situação atual deste projeto é "					
					+ projeto.getSituacaoProjeto().getDescricao()
					+ ". <br/>A operação solicitada não pode ser realizada para projetos nesta situação.<br/>"
					+ "Aguarde a alteração do estado do projeto e tente realizar a operação novamente.");
		}
		
		if ( projeto.getProjeto().isInterno() && ( !CalendarUtils.isDentroPeriodo(projeto.getEditalMonitoria().getInicioSelecaoMonitor(), 
				projeto.getEditalMonitoria().getFimSelecaoMonitor()) ) ) {
			
			lista.addErro("A prova de seleção dos monitores só pode ser cadastrada entre as datas " +
					CalendarUtils.format( projeto.getEditalMonitoria().getInicioSelecaoMonitor() , "dd/MM/yyyy") + 
					" à " + CalendarUtils.format( projeto.getEditalMonitoria().getFimSelecaoMonitor() , "dd/MM/yyyy") + ".");
		}
		
	}
	
}