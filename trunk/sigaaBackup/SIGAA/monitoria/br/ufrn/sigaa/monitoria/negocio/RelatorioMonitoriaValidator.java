/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/09/2008
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.text.SimpleDateFormat;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.bolsas.negocio.IntegracaoBolsas;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.monitoria.AtividadeMonitorDao;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.monitoria.dominio.AtividadeMonitor;
import br.ufrn.sigaa.monitoria.dominio.CalendarioMonitoria;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EnvioFrequencia;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.TipoVinculoDiscenteMonitoria;

/**
 * Classe responsável por validar cadastro de relatórios de atividades mensais do monitor (frequência).
 * 
 * @author Ilueny Santos.
 *
 */
public class RelatorioMonitoriaValidator {


    /**
     * Realiza validações relativas ao envio de frequência do monitor.
     * 
     * @param discenteMonitoria discente para validação
     * @param lista lista de mensagens de erro.
     * @throws DAOException exceção disparada das buscas para validação.
     */
	public static void validaEnvioRelatorioAtividadeMonitor(DiscenteMonitoria discenteMonitoria, ListaMensagens lista)throws DAOException {

		if ( discenteMonitoria == null ) {
			lista.addErro("Você não tem permissão para executar esta operação pois não faz parte de nenhum projeto de monitoria ativo.");
		}

		//Carregando configurações para o envio de freqüências
		AtividadeMonitorDao daoAtividade = DAOFactory.getInstance().getDAO((AtividadeMonitorDao.class));

		try {
			EnvioFrequencia regraEnvioFrequencia = daoAtividade.findByExactField(
					EnvioFrequencia.class, "ativo", Boolean.TRUE).iterator().next();

			//Verifica se o projeto está na faixa de anos liberados para envio.
			ProjetoEnsino projeto = discenteMonitoria.getProjetoEnsino();		
			if (!projeto.isPermitidoCadastrarRelatorioAtividadesMonitores(regraEnvioFrequencia)) {
				lista.addErro("Cadastro de Relatório de Atividades dos Monitores para projetos de " + projeto.getAno() 
						+ " não autorizado.");
			}

			//Verifica se está no período do recebimento de frequência
			if (!regraEnvioFrequencia.isEnvioLiberado()) {
				lista.addErro("Prazo para recebimento do Relatório de Atividades dos Monitores terminou.");
			}

			//Verifica se a data de entrada do discente é antes da data de envio da frequência.
			if (!discenteMonitoria.isPermitidoCadastrarRelatorioAtividade(regraEnvioFrequencia)) {
				SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
				lista.addErro("Discente não autorizado a enviar Relatório de Atividades entre " 
						+ sd.format(regraEnvioFrequencia.getDataInicioRecebimento())
						+ " e " + sd.format(regraEnvioFrequencia.getDataFimRecebimento()) +".");

				lista.addErro("Envio de frequência liberado para monitores cadastrados entre " + sd.format(regraEnvioFrequencia.getDataInicioEntradaMonitorPermitido()) 
						+ " e " + sd.format(regraEnvioFrequencia.getDataFimEntradaMonitorPermitido()) +".");
			}

			//Verifica se o discente já cadastrou algum relatório de atividade este mês.
			AtividadeMonitor atividade = daoAtividade.findByDiscenteMonitoriaMesCorrente(discenteMonitoria, 
					regraEnvioFrequencia.getMes(), regraEnvioFrequencia.getAno());

			if (atividade != null) {
				if (atividade.isAtividadeValidadaOrientador()) {
					lista.addErro("Relatório de Atividades do mês de referência " + regraEnvioFrequencia.getMes() 
							+ "/" + regraEnvioFrequencia.getAno() + " já foi enviado e validado.");
				} else {
					lista.addErro("Relatório de Atividades do mês de referência " + regraEnvioFrequencia.getMes() 
							+ "/" + regraEnvioFrequencia.getAno() + " já está cadastrado.");
				}
			}
		} finally {
			daoAtividade.close();
		}		

    }


    /**
     * O envio de relatórios parciais deve ser autorizado pela prograd através do calendário de monitoria
     * no menu principal da Pró-Reitoria de Graduação.
     * 
     * @param dm relatório de ação de extensão
     * @param lista lista de erros
     * @throws DAOException Erro na busca dos dados para validação
     */
	public static void validaEnvioRelatorioParcialMonitor(DiscenteMonitoria dm, ListaMensagens lista) throws DAOException {
		ProjetoMonitoriaDao dao = DAOFactory.getInstance().getDAO(ProjetoMonitoriaDao.class);
		try {
			CalendarioMonitoria cm = dao.findCalendarioByAnoAtivo(CalendarUtils.getAnoAtual());

			if (cm == null || !cm.isAtivo()) {
				lista.addErro("Período de envio do relatório ainda não foi definido pela Pró-Reitoria de Graduação.");
			} else { 
				if (cm.getAnoProjetoRelatorioParcialMonitor() != dm.getProjetoEnsino().getAno()) {
					lista.addErro("Atualmente, o recebimento de Relatório Parcial de Monitoria está autorizado somente para projetos submetidos em " + cm.getAnoProjetoRelatorioParcialMonitor());
				}
				if (!cm.isRelatorioParcialMonitorEmAberto()) {
					lista.addErro("O prazo para recebimento do Relatório Parcial de Monitoria terminou.");
				}
			}
		} finally {
			dao.close();
		}		
    }
    
    
    /**
     * O envio de relatórios finais deve ser autorizado pela prograd através do calendário de monitoria
     * no menu principal da Pró-Reitoria de Graduação.
     * 
     * @param dm relatório de ação de extensão
     * @param lista lista de erros
     * @throws DAOException Erro na busca dos dados para validação
     */
	public static void validaEnvioRelatorioFinalMonitor(DiscenteMonitoria dm, ListaMensagens lista) throws DAOException {	
		ProjetoMonitoriaDao dao = DAOFactory.getInstance().getDAO(ProjetoMonitoriaDao.class);
		try {
			CalendarioMonitoria cm = dao.findCalendarioByAnoAtivo(CalendarUtils.getAnoAtual());

			if (cm == null || !cm.isAtivo()) {
				lista.addErro("Período de envio do relatório ainda não foi definido pela Pró-Reitoria de Graduação!");
			} else {
				if (cm.getAnoProjetoRelatorioFinalMonitor() != dm.getProjetoEnsino().getAno()) {
					lista.addErro("Atualmente, o recebimento de Relatório Final de Monitoria está autorizado somente para projetos submetidos em " + cm.getAnoProjetoRelatorioFinalMonitor());
				}
				if (!cm.isRelatorioFinalProjetoEmAberto()) {
					lista.addErro("O prazo para recebimento do Relatório Final de Monitoria terminou.");
				}
			}
		} finally {
			dao.close();
		}		
    }


    /**
     * Verifica se os discente do projeto ainda tem bolsa de monitoria ativa na ufrn (sipac).
     *
     * @param discenteMonitoria discente para validação
     * @param lista lista de erros retornados
     * @throws ArqException Exceção disparada devido ao acesso aos dados do sipac.
     */
	public static void validaDiscenteComBolsaMonitoriaAtivaSIPAC(DiscenteMonitoria discenteMonitoria, ListaMensagens lista) throws ArqException {
		

		if(Sistema.isSipacAtivo()) {
			if (discenteMonitoria.equals(null)) {
			    lista.addMensagem(new MensagemAviso("Você não tem permissão para executar esta operação pois não " 
				    + "faz parte de nenhum projeto de monitoria ativo.", TipoMensagemUFRN.ERROR));
			}
			
			// testando se discente já participa é membro ativo de algum projeto de monitoria
			DiscenteMonitoriaDao dao = DAOFactory.getInstance().getDAO(DiscenteMonitoriaDao.class);
			try {
			    //para que o bolsista seja finalizado no sigaa sua bolsa deve ser finalizada antes no sipac
			    if (discenteMonitoria.getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.BOLSISTA) {		
					//não pode ter bolsa ativa no sipac
					int[] idTiposBolsasMonitoria = ParametroHelper.getInstance().getParametroIntArray(ConstantesParametro.LISTA_BOLSAS_MONITORIA);
					if (IntegracaoBolsas.verificarCadastroBolsaSIPAC(discenteMonitoria.getDiscente().getMatricula(), idTiposBolsasMonitoria) != 0) {
			
					    lista.addMensagem(new MensagemAviso("SIPAC: Esta operação não poderá ser realizada porque " 
						    + discenteMonitoria.getDiscente().getMatriculaNome() + " ainda possui bolsa ativa no Sistema Administrativo (SIPAC)." , TipoMensagemUFRN.ERROR));
			
					    lista.addMensagem(new MensagemAviso("Providencie a finalização da bolsa junto ao SIPAC e tente realizar esta operação novamente." , 
						    TipoMensagemUFRN.INFORMATION));
					}
		
			    }
			} finally {
			    dao.close();
			}
		}				
	}
	
	/**
	 * Valida se não frenquência para um mesmo mês.
	 * @param atividadeMonitor
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaFrequenciaMesmoMes(AtividadeMonitor atividadeMonitor, ListaMensagens lista) throws DAOException{
		AtividadeMonitorDao dao = DAOFactory.getInstance().getDAO(AtividadeMonitorDao.class);
		try{
			if(dao.findByDiscenteMonitoriaMesCorrente(atividadeMonitor.getDiscenteMonitoria(), atividadeMonitor.getMes(), atividadeMonitor.getAno()) != null){
				lista.addErro("Já existe um relatório de atividades cadastrado para o mês e o ano informados.");
			}
		}
		finally{
			dao.close();
		}
	}
	
	/**
	 * Valida envio de relatório de atividade do mês. 
	 * Discente não pode cadastrar duas atividades(freqüências) para um mesmo mês.
	 *  
	 * @param atividade relatório de atividades (frequência)
	 * @param lista lista de erros reportados.
	 * @throws DAOException possíveis erros em busca no banco de dados.
	 */
	public static void validaRelatorioAtividadeDuplicado(AtividadeMonitor atividade,  ListaMensagens lista)throws DAOException {

		AtividadeMonitorDao dao = DAOFactory.getInstance().getDAO(AtividadeMonitorDao.class);		
		AtividadeMonitor atividadeJaNoBanco = dao.findByDiscenteMonitoriaMesCorrente(atividade.getDiscenteMonitoria(),	atividade.getMes(), atividade.getAno());
		if (atividadeJaNoBanco != null && atividadeJaNoBanco.getId() != atividade.getId()) {
			if (atividadeJaNoBanco.getDataValidacaoOrientador() != null) {
				lista.addMensagem(new MensagemAviso("O Relatório de Atividades do mês referência " 
						+ atividade.getMes() + "/" + atividade.getAno() 
						+ " já foi enviado e validado por seu(a) orientador(a).", TipoMensagemUFRN.ERROR));
			}else {
				lista.addMensagem(new MensagemAviso("Relatório de Atividades do mês referência " 
					+ atividade.getMes() + "/" + atividade.getAno() 
					+ " já foi cadastrado antes e não pode ser cadastrado novamente. ", TipoMensagemUFRN.ERROR));
			}
		}

	}

	/**
	 * Valida envio de relatório de atividade do mês. 
	 *  
	 * @param atividade relatório de atividades (frequência) do discente.
	 * @param lista lista de erros reportados 
	 * @throws DAOException pode ser gerada nas buscas ao banco de dados.
	 */
	public static void validaEnvioLiberadoRelatorioAtividade(AtividadeMonitor atividade, ListaMensagens lista) throws DAOException {

		// Verificando se o discente já cadastrou a atividade do mês atual, caso afirmativo popular o formulário com a atividade 
		// cadastrada pois ele não pode cadastrar duas atividades para um mesmo mês.
		AtividadeMonitorDao dao = DAOFactory.getInstance().getDAO(AtividadeMonitorDao.class);		
		EnvioFrequencia envioFrequenciaAtivo = dao.findByExactField(EnvioFrequencia.class, "ativo", Boolean.TRUE).iterator().next();

		int anoProjeto = atividade.getDiscenteMonitoria().getProjetoEnsino().getAno();
		// Verifica se o projeto está na faixa de anos liberados para envio.
		if ((anoProjeto >= envioFrequenciaAtivo.getAnoInicioProjetosPermitidos()) && (anoProjeto <= envioFrequenciaAtivo.getAnoFimProjetosPermitidos())) {
			// Verifica se tá no período do recebimento de frequência
			if (!envioFrequenciaAtivo.isEnvioLiberado()) {
				lista.addMensagem(new MensagemAviso("Prazo para recebimento do Relatório de Atividades dos Monitores terminou." , TipoMensagemUFRN.ERROR));
			}
		} else {
			lista.addMensagem(new MensagemAviso("Cadastro de Relatório de Atividades dos Monitores para projetos de " 
					+ anoProjeto + " não autorizado." , TipoMensagemUFRN.ERROR));
		}
	}

	
}