/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe respons�vel por validar cadastro de relat�rios de atividades mensais do monitor (frequ�ncia).
 * 
 * @author Ilueny Santos.
 *
 */
public class RelatorioMonitoriaValidator {


    /**
     * Realiza valida��es relativas ao envio de frequ�ncia do monitor.
     * 
     * @param discenteMonitoria discente para valida��o
     * @param lista lista de mensagens de erro.
     * @throws DAOException exce��o disparada das buscas para valida��o.
     */
	public static void validaEnvioRelatorioAtividadeMonitor(DiscenteMonitoria discenteMonitoria, ListaMensagens lista)throws DAOException {

		if ( discenteMonitoria == null ) {
			lista.addErro("Voc� n�o tem permiss�o para executar esta opera��o pois n�o faz parte de nenhum projeto de monitoria ativo.");
		}

		//Carregando configura��es para o envio de freq��ncias
		AtividadeMonitorDao daoAtividade = DAOFactory.getInstance().getDAO((AtividadeMonitorDao.class));

		try {
			EnvioFrequencia regraEnvioFrequencia = daoAtividade.findByExactField(
					EnvioFrequencia.class, "ativo", Boolean.TRUE).iterator().next();

			//Verifica se o projeto est� na faixa de anos liberados para envio.
			ProjetoEnsino projeto = discenteMonitoria.getProjetoEnsino();		
			if (!projeto.isPermitidoCadastrarRelatorioAtividadesMonitores(regraEnvioFrequencia)) {
				lista.addErro("Cadastro de Relat�rio de Atividades dos Monitores para projetos de " + projeto.getAno() 
						+ " n�o autorizado.");
			}

			//Verifica se est� no per�odo do recebimento de frequ�ncia
			if (!regraEnvioFrequencia.isEnvioLiberado()) {
				lista.addErro("Prazo para recebimento do Relat�rio de Atividades dos Monitores terminou.");
			}

			//Verifica se a data de entrada do discente � antes da data de envio da frequ�ncia.
			if (!discenteMonitoria.isPermitidoCadastrarRelatorioAtividade(regraEnvioFrequencia)) {
				SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
				lista.addErro("Discente n�o autorizado a enviar Relat�rio de Atividades entre " 
						+ sd.format(regraEnvioFrequencia.getDataInicioRecebimento())
						+ " e " + sd.format(regraEnvioFrequencia.getDataFimRecebimento()) +".");

				lista.addErro("Envio de frequ�ncia liberado para monitores cadastrados entre " + sd.format(regraEnvioFrequencia.getDataInicioEntradaMonitorPermitido()) 
						+ " e " + sd.format(regraEnvioFrequencia.getDataFimEntradaMonitorPermitido()) +".");
			}

			//Verifica se o discente j� cadastrou algum relat�rio de atividade este m�s.
			AtividadeMonitor atividade = daoAtividade.findByDiscenteMonitoriaMesCorrente(discenteMonitoria, 
					regraEnvioFrequencia.getMes(), regraEnvioFrequencia.getAno());

			if (atividade != null) {
				if (atividade.isAtividadeValidadaOrientador()) {
					lista.addErro("Relat�rio de Atividades do m�s de refer�ncia " + regraEnvioFrequencia.getMes() 
							+ "/" + regraEnvioFrequencia.getAno() + " j� foi enviado e validado.");
				} else {
					lista.addErro("Relat�rio de Atividades do m�s de refer�ncia " + regraEnvioFrequencia.getMes() 
							+ "/" + regraEnvioFrequencia.getAno() + " j� est� cadastrado.");
				}
			}
		} finally {
			daoAtividade.close();
		}		

    }


    /**
     * O envio de relat�rios parciais deve ser autorizado pela prograd atrav�s do calend�rio de monitoria
     * no menu principal da Pr�-Reitoria de Gradua��o.
     * 
     * @param dm relat�rio de a��o de extens�o
     * @param lista lista de erros
     * @throws DAOException Erro na busca dos dados para valida��o
     */
	public static void validaEnvioRelatorioParcialMonitor(DiscenteMonitoria dm, ListaMensagens lista) throws DAOException {
		ProjetoMonitoriaDao dao = DAOFactory.getInstance().getDAO(ProjetoMonitoriaDao.class);
		try {
			CalendarioMonitoria cm = dao.findCalendarioByAnoAtivo(CalendarUtils.getAnoAtual());

			if (cm == null || !cm.isAtivo()) {
				lista.addErro("Per�odo de envio do relat�rio ainda n�o foi definido pela Pr�-Reitoria de Gradua��o.");
			} else { 
				if (cm.getAnoProjetoRelatorioParcialMonitor() != dm.getProjetoEnsino().getAno()) {
					lista.addErro("Atualmente, o recebimento de Relat�rio Parcial de Monitoria est� autorizado somente para projetos submetidos em " + cm.getAnoProjetoRelatorioParcialMonitor());
				}
				if (!cm.isRelatorioParcialMonitorEmAberto()) {
					lista.addErro("O prazo para recebimento do Relat�rio Parcial de Monitoria terminou.");
				}
			}
		} finally {
			dao.close();
		}		
    }
    
    
    /**
     * O envio de relat�rios finais deve ser autorizado pela prograd atrav�s do calend�rio de monitoria
     * no menu principal da Pr�-Reitoria de Gradua��o.
     * 
     * @param dm relat�rio de a��o de extens�o
     * @param lista lista de erros
     * @throws DAOException Erro na busca dos dados para valida��o
     */
	public static void validaEnvioRelatorioFinalMonitor(DiscenteMonitoria dm, ListaMensagens lista) throws DAOException {	
		ProjetoMonitoriaDao dao = DAOFactory.getInstance().getDAO(ProjetoMonitoriaDao.class);
		try {
			CalendarioMonitoria cm = dao.findCalendarioByAnoAtivo(CalendarUtils.getAnoAtual());

			if (cm == null || !cm.isAtivo()) {
				lista.addErro("Per�odo de envio do relat�rio ainda n�o foi definido pela Pr�-Reitoria de Gradua��o!");
			} else {
				if (cm.getAnoProjetoRelatorioFinalMonitor() != dm.getProjetoEnsino().getAno()) {
					lista.addErro("Atualmente, o recebimento de Relat�rio Final de Monitoria est� autorizado somente para projetos submetidos em " + cm.getAnoProjetoRelatorioFinalMonitor());
				}
				if (!cm.isRelatorioFinalProjetoEmAberto()) {
					lista.addErro("O prazo para recebimento do Relat�rio Final de Monitoria terminou.");
				}
			}
		} finally {
			dao.close();
		}		
    }


    /**
     * Verifica se os discente do projeto ainda tem bolsa de monitoria ativa na ufrn (sipac).
     *
     * @param discenteMonitoria discente para valida��o
     * @param lista lista de erros retornados
     * @throws ArqException Exce��o disparada devido ao acesso aos dados do sipac.
     */
	public static void validaDiscenteComBolsaMonitoriaAtivaSIPAC(DiscenteMonitoria discenteMonitoria, ListaMensagens lista) throws ArqException {
		

		if(Sistema.isSipacAtivo()) {
			if (discenteMonitoria.equals(null)) {
			    lista.addMensagem(new MensagemAviso("Voc� n�o tem permiss�o para executar esta opera��o pois n�o " 
				    + "faz parte de nenhum projeto de monitoria ativo.", TipoMensagemUFRN.ERROR));
			}
			
			// testando se discente j� participa � membro ativo de algum projeto de monitoria
			DiscenteMonitoriaDao dao = DAOFactory.getInstance().getDAO(DiscenteMonitoriaDao.class);
			try {
			    //para que o bolsista seja finalizado no sigaa sua bolsa deve ser finalizada antes no sipac
			    if (discenteMonitoria.getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.BOLSISTA) {		
					//n�o pode ter bolsa ativa no sipac
					int[] idTiposBolsasMonitoria = ParametroHelper.getInstance().getParametroIntArray(ConstantesParametro.LISTA_BOLSAS_MONITORIA);
					if (IntegracaoBolsas.verificarCadastroBolsaSIPAC(discenteMonitoria.getDiscente().getMatricula(), idTiposBolsasMonitoria) != 0) {
			
					    lista.addMensagem(new MensagemAviso("SIPAC: Esta opera��o n�o poder� ser realizada porque " 
						    + discenteMonitoria.getDiscente().getMatriculaNome() + " ainda possui bolsa ativa no Sistema Administrativo (SIPAC)." , TipoMensagemUFRN.ERROR));
			
					    lista.addMensagem(new MensagemAviso("Providencie a finaliza��o da bolsa junto ao SIPAC e tente realizar esta opera��o novamente." , 
						    TipoMensagemUFRN.INFORMATION));
					}
		
			    }
			} finally {
			    dao.close();
			}
		}				
	}
	
	/**
	 * Valida se n�o frenqu�ncia para um mesmo m�s.
	 * @param atividadeMonitor
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaFrequenciaMesmoMes(AtividadeMonitor atividadeMonitor, ListaMensagens lista) throws DAOException{
		AtividadeMonitorDao dao = DAOFactory.getInstance().getDAO(AtividadeMonitorDao.class);
		try{
			if(dao.findByDiscenteMonitoriaMesCorrente(atividadeMonitor.getDiscenteMonitoria(), atividadeMonitor.getMes(), atividadeMonitor.getAno()) != null){
				lista.addErro("J� existe um relat�rio de atividades cadastrado para o m�s e o ano informados.");
			}
		}
		finally{
			dao.close();
		}
	}
	
	/**
	 * Valida envio de relat�rio de atividade do m�s. 
	 * Discente n�o pode cadastrar duas atividades(freq��ncias) para um mesmo m�s.
	 *  
	 * @param atividade relat�rio de atividades (frequ�ncia)
	 * @param lista lista de erros reportados.
	 * @throws DAOException poss�veis erros em busca no banco de dados.
	 */
	public static void validaRelatorioAtividadeDuplicado(AtividadeMonitor atividade,  ListaMensagens lista)throws DAOException {

		AtividadeMonitorDao dao = DAOFactory.getInstance().getDAO(AtividadeMonitorDao.class);		
		AtividadeMonitor atividadeJaNoBanco = dao.findByDiscenteMonitoriaMesCorrente(atividade.getDiscenteMonitoria(),	atividade.getMes(), atividade.getAno());
		if (atividadeJaNoBanco != null && atividadeJaNoBanco.getId() != atividade.getId()) {
			if (atividadeJaNoBanco.getDataValidacaoOrientador() != null) {
				lista.addMensagem(new MensagemAviso("O Relat�rio de Atividades do m�s refer�ncia " 
						+ atividade.getMes() + "/" + atividade.getAno() 
						+ " j� foi enviado e validado por seu(a) orientador(a).", TipoMensagemUFRN.ERROR));
			}else {
				lista.addMensagem(new MensagemAviso("Relat�rio de Atividades do m�s refer�ncia " 
					+ atividade.getMes() + "/" + atividade.getAno() 
					+ " j� foi cadastrado antes e n�o pode ser cadastrado novamente. ", TipoMensagemUFRN.ERROR));
			}
		}

	}

	/**
	 * Valida envio de relat�rio de atividade do m�s. 
	 *  
	 * @param atividade relat�rio de atividades (frequ�ncia) do discente.
	 * @param lista lista de erros reportados 
	 * @throws DAOException pode ser gerada nas buscas ao banco de dados.
	 */
	public static void validaEnvioLiberadoRelatorioAtividade(AtividadeMonitor atividade, ListaMensagens lista) throws DAOException {

		// Verificando se o discente j� cadastrou a atividade do m�s atual, caso afirmativo popular o formul�rio com a atividade 
		// cadastrada pois ele n�o pode cadastrar duas atividades para um mesmo m�s.
		AtividadeMonitorDao dao = DAOFactory.getInstance().getDAO(AtividadeMonitorDao.class);		
		EnvioFrequencia envioFrequenciaAtivo = dao.findByExactField(EnvioFrequencia.class, "ativo", Boolean.TRUE).iterator().next();

		int anoProjeto = atividade.getDiscenteMonitoria().getProjetoEnsino().getAno();
		// Verifica se o projeto est� na faixa de anos liberados para envio.
		if ((anoProjeto >= envioFrequenciaAtivo.getAnoInicioProjetosPermitidos()) && (anoProjeto <= envioFrequenciaAtivo.getAnoFimProjetosPermitidos())) {
			// Verifica se t� no per�odo do recebimento de frequ�ncia
			if (!envioFrequenciaAtivo.isEnvioLiberado()) {
				lista.addMensagem(new MensagemAviso("Prazo para recebimento do Relat�rio de Atividades dos Monitores terminou." , TipoMensagemUFRN.ERROR));
			}
		} else {
			lista.addMensagem(new MensagemAviso("Cadastro de Relat�rio de Atividades dos Monitores para projetos de " 
					+ anoProjeto + " n�o autorizado." , TipoMensagemUFRN.ERROR));
		}
	}

	
}