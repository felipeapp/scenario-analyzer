/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/10/2006
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.InscricaoSelecaoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.HistoricoSituacaoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.InscricaoSelecaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecao;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecaoComponenteCurricular;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.TipoSituacaoProvaSelecao;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Processador responsável pelo cadastro do resultado da seleção, validação e
 * desvalidação desse resultado.
 * 
 * @author ilueny santos
 * @author david
 * 
 */
public class ProcessadorSelecaoMonitoria extends AbstractProcessador {

	/**
	 * Realiza operações de cadastro do resultado da seleção, validação e
     * desvalidação desse resultado.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);
		ProjetoEnsino pm = null;

		switch (((ProjetoMonitoriaMov) mov).getAcao()) {
		case ProjetoMonitoriaMov.ACAO_VALIDAR_CADASTRO_SELECAO:
			pm = (ProjetoEnsino) validarCadastroSelecao(mov);
			break;
		case ProjetoMonitoriaMov.ACAO_DESVALIDAR_SELECAO:
			pm = (ProjetoEnsino) desvalidarSelecao(mov);
			break;
		case ProjetoMonitoriaMov.ACAO_CADASTRAR_PROVA_SELECAO:
			cadastrarProvaSeletiva((ProjetoMonitoriaMov) mov);
			break;
		default:
			throw new NegocioException("Tipo de ação desconhecida!");
		}

		return pm;

	}

	/**
	 * Cadastrar/Alterar provas seletivas.
	 * 
	 * Todas as provas seletivas possuem uma lista de componentes curriculares.
	 * Este componentes auxiliam o discente com o conteúdo que será cobrado na
	 * prova e permite que somente alunos com notas acima de 7.0 nestes
	 * componentes se inscrevam na seleção.
	 * 
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	public Object cadastrarProvaSeletiva(ProjetoMonitoriaMov mov)
			throws DAOException {
		ProvaSelecao prova = mov.getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		try {
			// removendo componentes do banco.
			if (mov.getProvaComponentesRemovidos() != null) {
				for (ProvaSelecaoComponenteCurricular pscc : mov
						.getProvaComponentesRemovidos()) {
					if (pscc.getId() > 0) {
						dao.remove(pscc);
					}
				}
			}

			dao.getSession().flush();
			if (prova.getId() == 0) {
				prova.setDataCadastro(new Date());
				prova.setSituacaoProva(new TipoSituacaoProvaSelecao(
						TipoSituacaoProvaSelecao.AGUARDANDO_INSCRICAO));
				dao.create(prova);
			} else {
				dao.update(prova);
			}

			return prova;
		} finally {
			dao.close();
		}
	}

	/**
	 * Validação do resultado da prova seletiva realizado por membro da PROGRAD.
	 * Verifica se a nota cadastrada no sistema corresponde a nota da prova do
	 * discente e autoriza o cadastro do monitor como bolsista.
	 * 
	 * 
	 * @param mov
	 * @return
	 * @throws ArqException
	 */
	public Object validarCadastroSelecao(Movimento mov) throws ArqException {
		InscricaoSelecaoMonitoriaDao dao = getDAO(InscricaoSelecaoMonitoriaDao.class, mov);
		MembroProjetoDao mpDao = getDAO(MembroProjetoDao.class, mov);
		ProjetoMonitoriaMov pMov = (ProjetoMonitoriaMov) mov;
		ProvaSelecao prova = (ProvaSelecao) pMov.getObjMovimentado();
		ProjetoEnsino pm = prova.getProjetoEnsino();
		Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

		try {		

			// Atualizando situações dos monitores
			Collection<DiscenteMonitoria> resultadosSelecao = prova.getResultadoSelecao();

			if (!ValidatorUtil.isEmpty(resultadosSelecao)) {        			
				boolean algumMonitorAssumiu = false;

				for (DiscenteMonitoria monitor : resultadosSelecao) {        				
					if (monitor.isSelecionado()) {

						//ASSUMIU MONITORIA IMEDIATAMENTE...		 
						if (monitor.isAssumiuMonitoria() && 
								(monitor.isVinculoBolsista() || monitor.isVinculoNaoRemunerado())) {

							algumMonitorAssumiu = true;

							monitor.setAtivo(true);
							monitor.setDataInicio(hoje);
							monitor.setDataFim(pm.getProjeto().getDataFim());							
							monitor.setDataValidacaoPrograd(new Date());
							dao.update(monitor);

							// iniciando orientações que ainda estão em aberto
							for (Orientacao ori : monitor.getOrientacoes()) {
				    			if (ori.getDataInicio() == null) {
				    				ori.setDataInicio(hoje);
				    			}
				    			if (ori.getDataFim() == null) {
				    				ori.setDataFim(ori.getDiscenteMonitoria().getProjetoEnsino().getProjeto().getDataFim());
				    			}
				    			ori.setAtivo(true);
				    			dao.update(ori);
							}

						//NÃO ASSUMIU MONITORIA...		
						} else {
							
							// Apenas convocado, pode assumir ou não....
							if (monitor.isConvocado() && 
									(monitor.isVinculoBolsista() || monitor.isVinculoNaoRemunerado())) {
								monitor.setDataInicio(null);
								monitor.setDataFim(null);
								monitor.setDataValidacaoPrograd(new Date());
								dao.update(monitor);
								
							} else {

								// Nunca vai assumir..
								if (       (monitor.getSituacaoDiscenteMonitoria().getId() == SituacaoDiscenteMonitoria.INVALIDADO_PROGRAD)
										|| (monitor.getSituacaoDiscenteMonitoria().getId() == SituacaoDiscenteMonitoria.CONVOCADO_MAS_REJEITOU_MONITORIA)									
										|| (monitor.getSituacaoDiscenteMonitoria().getId() == SituacaoDiscenteMonitoria.MONITORIA_CANCELADA)
										|| (monitor.getSituacaoDiscenteMonitoria().getId() == SituacaoDiscenteMonitoria.NAO_APROVADO)
										|| (monitor.getSituacaoDiscenteMonitoria().getId() == SituacaoDiscenteMonitoria.AGUARDANDO_CONVOCACAO)) {
	
									monitor.setDataInicio(null);
									monitor.setDataFim(null);
									monitor.setDataValidacaoPrograd(new Date());
									dao.update(monitor);
	
									// finalizando orientações que ainda estão em aberto
									// finaliza a orientação na data que finaliza o discente
									for (Orientacao ori : monitor.getOrientacoes()) {
										ori.setDataInicio(null);
										ori.setDataFim(null);
										ori.setAtivo(true);
										dao.update(ori);
									}							
								}
								
							}
						}
						
						//Envia e-mail para bolsistas remunerados convocados ou que assumiram a monitoria.
						if (monitor.isVinculoBolsista() && (monitor.isConvocado() || monitor.isAssumiuMonitoria())) {
							if (monitor.isVinculoBolsista()){        			
								InscricaoSelecaoMonitoria inscricao = dao.findByDiscenteProjeto(monitor.getDiscente(), monitor.getProvaSelecao());
								pm.getProjeto().setCoordenador(mpDao.findCoordenadorAtualProjeto(pm.getProjeto().getId()));
								enviarEmailBolsista(pm, inscricao);											
							}
						}

						HistoricoSituacaoDiscenteMonitoria historico = new HistoricoSituacaoDiscenteMonitoria();
						historico.setData(new Date());
						historico.setDiscenteMonitoria(monitor);
						historico.setSituacaoDiscenteMonitoria(monitor.getSituacaoDiscenteMonitoria());
						historico.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
						historico.setTipoVinculo(monitor.getTipoVinculo());
						dao.create(historico);					

					}  				
				}


				//Alterando a situação da prova seletiva
				if (prova.isValidacoesFinalizadas()) {
					dao.updateField(ProvaSelecao.class, prova.getId(), "situacaoProva.id", TipoSituacaoProvaSelecao.CONCLUIDA);
				} else {
					dao.updateField(ProvaSelecao.class, prova.getId(), "situacaoProva.id", TipoSituacaoProvaSelecao.VALIDACAO_EM_ANDAMENTO);
				}					    

				// Se pelo menos um dos discentes Assumiu Monitoria na validação da prova o projeto passa para 'EM EXECUÇÃO'
				if (algumMonitorAssumiu && pm.getSituacaoProjeto().getId() != TipoSituacaoProjeto.MON_EM_EXECUCAO) {								
						pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_EM_EXECUCAO));
						pm.setAtivo(true);
						dao.clearSession();
						dao.update(pm);
						ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);
						ProjetoHelper.gravarHistoricoSituacaoProjeto(pm.getSituacaoProjeto().getId(), pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());        								
				}				
			}

			return pm;

		} finally {
			dao.close();
			mpDao.close();
		}
	}

	/**
	 * Método responsável por enviar email para todos os bolsistas que tiveram o
	 * resultado da prova validado.
	 * 
	 * @param inscricaoSelecao
	 * @throws DAOException
	 */
	private void enviarEmailBolsista(ProjetoEnsino pm,
			InscricaoSelecaoMonitoria inscricaoSelecao) throws DAOException {

		if (inscricaoSelecao != null
				&& inscricaoSelecao.getRegistroEntrada() != null
				&& inscricaoSelecao.getRegistroEntrada().getUsuario()
						.getEmail() != null) {
			// enviando e-mail para o candidato
			MailBody email = new MailBody();
			email.setAssunto("[SIGAA] Resultado da Seleção de Monitoria");
			email.setContentType(MailBody.HTML);
			email.setNome(inscricaoSelecao.getRegistroEntrada().getUsuario()
					.getNome());
			email.setEmail(inscricaoSelecao.getRegistroEntrada().getUsuario()
					.getEmail());

			StringBuffer msg = new StringBuffer();
			msg.append("Você foi aprovado(a) como Bolsista Remunerado(a) de Projeto de Ensino.<br/>");
			msg.append("O resultado da prova seletiva foi homologado pela Pró-Reitoria de Graduação. <br/><br/>");

			msg.append("DADOS DO PROJETO: <br/>");
			msg.append("ANO: " + pm.getAno() + "<br/>");
			msg.append("TÍTULO: " + pm.getTitulo() + "<br/>");
			msg.append("COORDENADOR(A): "
					+ pm.getProjeto().getCoordenador().getPessoa().getNome() + "<br/><br/>");

			msg.append("Acesse agora o SIGAA e atualize, com urgência, seus dados bancários através do Portal do Discente no menu: "
					+ "Monitoria > Projetos > Listar meus projetos > Atualizar dados bancários.<br/><br/>");

			msg.append("----- <br/>"
					+ "Esta mensagem foi gerada automaticamente pelo Sistema Integrado de Gestão de Atividades Acadêmicas.<br/>"
					+ "Não é necessário respondê-la.<br/>"
					+ "SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas.<br/>"
					+ "Universidade Federal do Rio Grande do Norte.<br/>");

			email.setMensagem(msg.toString());
			Mail.send(email);
		}
	}

	/**
	 * Permite que o docente recadastre a nota do aluno. Utilizado quando ocorre
	 * cadastro de nota errada do discente seguido de validação, também
	 * equivocada, por membro da PROGRAD
	 * 
	 * @param mov
	 * @return
	 * @throws ArqException
	 */
	public Object desvalidarSelecao(Movimento mov) throws ArqException {
		DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class, mov);
		ProjetoMonitoriaMov pMov = (ProjetoMonitoriaMov) mov;
		DiscenteMonitoria monitor = (DiscenteMonitoria) pMov
				.getObjMovimentado();
		try {

			if (monitor.getSituacaoDiscenteMonitoria().getId() == SituacaoDiscenteMonitoria.DESVALIDADO_PELA_PROGRAD) {

				monitor.setAtivo(true);
				monitor.setDataInicio(null);
				monitor.setDataFim(null);
				monitor.setDataValidacaoPrograd(new Date());
				dao.update(monitor);
				dao.detach(monitor);

				for (Orientacao ori : monitor.getOrientacoes()) {
					ori.setDataInicio(null);
					ori.setDataFim(null);
					ori.setAtivo(true);
					dao.update(ori);
					dao.update(ori);
				}

				HistoricoSituacaoDiscenteMonitoria historico = new HistoricoSituacaoDiscenteMonitoria();
				historico.setData(new Date());
				historico.setDiscenteMonitoria(monitor);
				historico.setSituacaoDiscenteMonitoria(monitor
						.getSituacaoDiscenteMonitoria());
				historico.setRegistroEntrada(mov.getUsuarioLogado()
						.getRegistroEntrada());
				historico.setTipoVinculo(monitor.getTipoVinculo());
				dao.create(historico);

			}
			return monitor.getProjetoEnsino();

		} finally {
			dao.close();
		}
	}

	/**
	 * Valida operações
	 * @see br.ufrn.sigaa.monitoria.negocio.ProjetoMonitoriaValidator
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens lista = new ListaMensagens();
		ProvaSelecao prova = null;

		switch (((ProjetoMonitoriaMov) mov).getAcao()) {
		case ProjetoMonitoriaMov.ACAO_VALIDAR_CADASTRO_SELECAO:
			prova = (ProvaSelecao) ((ProjetoMonitoriaMov) mov)
					.getObjMovimentado();
			ProvaSelecaoValidator.validaBolsasDisponiveisProjeto(prova, lista);
			break;

		case ProjetoMonitoriaMov.ACAO_DESVALIDAR_SELECAO:
			DiscenteMonitoria monitor = (DiscenteMonitoria) ((ProjetoMonitoriaMov) mov)
					.getObjMovimentado();
			// Na desvalidação de bolsistas, orienta o gestor a cancelar
			// primeiramente a bolsa no Administrativo(SIPAC)
			DiscenteMonitoriaValidator.validaDiscenteComBolsa(monitor, lista);
			break;

		case ProjetoMonitoriaMov.ACAO_CADASTRAR_PROVA_SELECAO:
			prova = (ProvaSelecao) ((ProjetoMonitoriaMov) mov)
					.getObjMovimentado();
			ProvaSelecaoValidator.validaPermissaoProjetoAtivo(
					prova.getProjetoEnsino(), lista);
			ProvaSelecaoValidator.validaReservaVagaProva(prova, lista);
			break;
		}

		checkValidation(lista);
	}

}