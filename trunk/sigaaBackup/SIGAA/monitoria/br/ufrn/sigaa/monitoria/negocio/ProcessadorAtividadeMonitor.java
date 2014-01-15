/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/08/2007
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.bolsas.negocio.IntegracaoBolsas;
import br.ufrn.sigaa.arq.dao.monitoria.AtividadeMonitorDao;
import br.ufrn.sigaa.arq.dao.monitoria.AvaliacaoMonitoriaDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.AtividadeMonitor;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;
import br.ufrn.sigaa.monitoria.dominio.TipoVinculoDiscenteMonitoria;
import br.ufrn.sigaa.parametros.dominio.ParametrosMonitoria;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Processador responsável por realizar operações de cadastro e validação de 
 * relatório de atividades do monitor (freqüências).
 * 
 * @author ilueny santos
 * 
 */
public class ProcessadorAtividadeMonitor extends AbstractProcessador {

	/** Lista de mensagens de erros. */
	ListaMensagens erros = new ListaMensagens();

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		validate(mov);
		
		if (!erros.isErrorPresent()) {
			if (((MovimentoCadastro) mov).getCodMovimento().equals(
					SigaaListaComando.CADASTRAR_ATIVIDADE_MONITOR)) {
				criarAtividadeMonitor((MovimentoCadastro) mov);
	
			} else if (((MovimentoCadastro) mov).getCodMovimento().equals(
					SigaaListaComando.ORIENTADOR_VALIDAR_ATIVIDADE_MONITOR)) {
				orientadorValidarAtividadeMonitor((MovimentoCadastro) mov);
			}else if (((MovimentoCadastro) mov).getCodMovimento().equals(
					SigaaListaComando.DESVALIDAR_ATIVIDADE_MONITOR)) {
				desvalidarAtividadeMonitor((MovimentoCadastro) mov);
			}

		}else{
			return erros;
		}
		return null;
	}

	/**
	 * Invalida relatório.
	 * 
	 * @param mov
	 * @throws DAOException
	 */
	private void desvalidarAtividadeMonitor(MovimentoCadastro mov) throws DAOException {
	    GenericDAO dao =  getGenericDAO(mov);
	    try {
		AtividadeMonitor atv = mov.getObjMovimentado();
		atv.setRegistroEntradaPrograd(mov.getUsuarioLogado().getRegistroEntrada());
		/** @negocio: Permite ao orientador/coordenador re-validar o relatório de atividades do monitor */
		atv.setDataValidacaoOrientador(null);
		atv.setValidadoOrientador(false);		
		dao.update(atv);
	    }finally {
	    	dao.close();
	    }
	}

	/**
	 * Cria ou atualiza um Relatório de Atividades do Monitor.
	 * 
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void criarAtividadeMonitor(MovimentoCadastro mov) throws ArqException, NegocioException {
		AtividadeMonitorDao dao = getDAO(AtividadeMonitorDao.class, mov);  
		try {
			AtividadeMonitor relatorioFreq = mov.getObjMovimentado();
			relatorioFreq.setAtivo(true);

			/** @negocio: Cadastros Realizados pelo Gestor de Monitoria não necessitam de validação do orientador/coordenado do projeto. */
			if (mov.getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_MONITORIA)) {
				relatorioFreq.setRegistroEntradaPrograd(mov.getUsuarioLogado().getRegistroEntrada());
				relatorioFreq.setValidadoPrograd(true);
				relatorioFreq.setDataValidacaoPrograd(new Date());
			}
			
			dao.createOrUpdate(relatorioFreq);

			/** @negocio: E-mail solicitando a validação do relatório só é enviado quando o cadastro da frequência NÃO for realizado por um gestor de montoria. */
			if (!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_MONITORIA)) {
				enviarEmailOrientadores(mov);
				enviarEmailCoordenador(mov);
			}
		}finally {
			dao.close();
		}
	}
	
	/**
	 * Envia e-mail a todos os orientadores do Discente.
	 * 
	 * @param mov
	 * @throws DAOException 
	 */
	private void enviarEmailOrientadores(MovimentoCadastro mov) throws DAOException {

	    AtividadeMonitor atv = mov.getObjMovimentado();
	    GenericDAO dao =  getGenericDAO(mov);
	    try {
        	    Formatador formatador = Formatador.getInstance();		
        	    atv.getDiscenteMonitoria().setOrientacoes( dao.findByExactField(Orientacao.class, "discenteMonitoria.id", atv.getDiscenteMonitoria().getId()) );
        	    for(Orientacao orientacao : atv.getDiscenteMonitoria().getOrientacoes()) {
        		MailBody mail = new MailBody();
        		mail.setAssunto("Monitoria - Relatório de Atividade Mensal(Frequência)");
        		mail.setFromName("Sistemas/" + RepositorioDadosInstitucionais.get("nomeInstituicao"));					
        		mail.setEmail( orientacao.getEquipeDocente().getServidor().getPessoa().getEmail() );		
        		mail.setMensagem("Prezado(a) Professor(a) " + orientacao.getEquipeDocente().getServidor().getPessoa().getNome() + ", o(a) discente " + 
        			atv.getDiscenteMonitoria().getDiscente().getPessoa().getNome() + 
        			" enviou relatório de atividade mensal(frequência) do mês de " + formatador.formatarMes(atv.getMes()-1) + " de "+  atv.getAno() +  ", referente ao seguinte projeto: "+
        			"<br /><br /><br />" + "Ano Projeto: " + atv.getDiscenteMonitoria().getProjetoEnsino().getAno() + "<br />" + "Projeto: " +
        			atv.getDiscenteMonitoria().getProjetoEnsino().getTitulo()  );
        		Mail.send(mail);
        	    }
	    }finally {
		dao.close();
	    }
	}
	
	/**
	 * Envia E-mail para o coordenador do projeto.
	 * 
	 * @throws DAOException 
	 */
	private void enviarEmailCoordenador(MovimentoCadastro mov) throws DAOException {
	    AtividadeMonitor atv = mov.getObjMovimentado();
	    GenericDAO dao =  getGenericDAO(mov);
	    try {
        	    atv.getDiscenteMonitoria().getProjetoEnsino().setEquipeDocentes(dao.findByExactField(EquipeDocente.class, "projetoEnsino.id", atv.getDiscenteMonitoria().getProjetoEnsino().getId()) );
        	    Servidor coordenador = atv.getDiscenteMonitoria().getProjetoEnsino().getCoordenacao();
        	    Formatador formatador = Formatador.getInstance();
        
        	    //Envia E-mail para o Coordenador do projeto.
        	    MailBody mailCoordenador = new MailBody();
        	    mailCoordenador.setAssunto("Monitoria - Relatório de Atividade Mensal(Frequência)");
        	    mailCoordenador.setFromName("Sistemas/" + RepositorioDadosInstitucionais.get("nomeInstituicao"));					
        	    mailCoordenador.setEmail( coordenador.getPessoa().getEmail() );		
        	    mailCoordenador.setMensagem("Prezado(a) Professor(a) " + coordenador.getPessoa().getNome() + ", o(a) discente " + 
        		    atv.getDiscenteMonitoria().getDiscente().getPessoa().getNome() + 
        		    " enviou relatório de atividade mensal(frequência) do mês de " + formatador.formatarMes(atv.getMes()-1) + " de "+  atv.getAno() +  ", referente ao seguinte projeto: "+
        		    "<br /><br /><br />" + "Ano Projeto: " + atv.getDiscenteMonitoria().getProjetoEnsino().getAno() + "<br />" + "Projeto: " +
        		    atv.getDiscenteMonitoria().getProjetoEnsino().getTitulo()  );
        	    Mail.send(mailCoordenador);
	    }finally {
		dao.close();
	    }
	}
	
	

	/**
	 * Método que inplementa a validação das atividades do monitor pelo orientador.
	 * 
	 * @param mov
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void orientadorValidarAtividadeMonitor(MovimentoCadastro mov)
			throws ArqException, NegocioException {

		AvaliacaoMonitoriaDao daoAM = getDAO(AvaliacaoMonitoriaDao.class, mov);

		try {

			AtividadeMonitor atv = (AtividadeMonitor) mov.getObjMovimentado();

			// atualizando avaliações
			if (atv.getId() != 0)
				daoAM.update(atv);

			// verifica se o aluno(a) já tem bolsa de MONITORIA cadastrada no SIPAC
			if (atv.getDiscenteMonitoria().getTipoVinculo().getId() == TipoVinculoDiscenteMonitoria.BOLSISTA && ParametroHelper.getInstance().getParametroBoolean(ParametrosMonitoria.FREQUENCIA_MONITORIA)) {
			    
			    
			    int[] idTiposBolsasMonitoria = ParametroHelper.getInstance().getParametroIntArray(ConstantesParametro.LISTA_BOLSAS_MONITORIA);
				int idBolsa = IntegracaoBolsas.verificarCadastroBolsaSIPAC(atv.getDiscenteMonitoria().getDiscente().getMatricula(), idTiposBolsasMonitoria);

				if (idBolsa == 0) {
					throw new NegocioException(
							atv.getDiscenteMonitoria().getDiscente()
									.getMatriculaNome()
									+ "  NÃO possui cadastrado de bolsa de monitoria ativo no SIPAC.<br/>"
									+ "Solicite a Pró-Reitoria de Graduação que realize o cadastro do(a) bolsista no SIPAC"
									+ " para que a validação desta freqüência possa ser realizada.");
				} else {

					// só cadastrar freqüências do mês atual, freqüências
					// fora do prazo seguem outro fluxo.
					// if (atv.getMes() == SIPACUtils.getMesAtual())

					// cadastra a freqüência no SIPAC, se já existir, atualiza
					if (atv.getDataValidacaoPrograd() == null)
						IntegracaoBolsas.cadastrarFrequenciaSIPAC(idBolsa, 
								(atv.getMes() - 1), atv.getAno(), atv
								.getFrequencia(), mov.getUsuarioLogado()
								.getId(), atv.getId());

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new NegocioException(
					"Erro na validação do relatório de atividades.<br/>"
							+ e.getMessage());
		} finally {
			daoAM.close();
		}

	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro movc = (MovimentoCadastro) mov;
		
			if (movc.getCodMovimento().equals(SigaaListaComando.CADASTRAR_ATIVIDADE_MONITOR)) {
				if (mov.getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_MONITORIA)) {
					ValidatorUtil.validateRequired(((AtividadeMonitor) movc.getObjMovimentado()).getObservacaoPrograd(), "Observações (Motivo do Cadastro)", erros);
					if(movc.getAcao() != ArqListaComando.ALTERAR.getId()){
						RelatorioMonitoriaValidator.validaFrequenciaMesmoMes((AtividadeMonitor) movc.getObjMovimentado(), erros);
					}
				} else {
					if(movc.getAcao() != ArqListaComando.ALTERAR.getId()){
						RelatorioMonitoriaValidator.validaFrequenciaMesmoMes((AtividadeMonitor) movc.getObjMovimentado(), erros);
					}
					RelatorioMonitoriaValidator.validaEnvioLiberadoRelatorioAtividade((AtividadeMonitor) movc.getObjMovimentado(), erros);
				}
				RelatorioMonitoriaValidator.validaRelatorioAtividadeDuplicado((AtividadeMonitor) movc.getObjMovimentado(), erros);
			}		
	}
}