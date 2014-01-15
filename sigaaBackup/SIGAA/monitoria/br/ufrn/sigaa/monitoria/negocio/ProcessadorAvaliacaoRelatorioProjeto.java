/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 13/07/2007
 * Autor: ilueny
 *
 */

package br.ufrn.sigaa.monitoria.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.monitoria.EquipeDocenteDao;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoRelatorioProjeto;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.RecomendacaoCorteBolsa;
import br.ufrn.sigaa.monitoria.dominio.RelatorioMonitor;
import br.ufrn.sigaa.monitoria.dominio.RelatorioProjetoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.RenovacaoProjetoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.monitoria.dominio.StatusRelatorio;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Processador para avaliação dos relatórios final/parcial de projeto de monitoria.
 *
 * @author ilueny santos
 */
public class ProcessadorAvaliacaoRelatorioProjeto extends AbstractProcessador {

	/**
	 * Método responsável pelo processamento das avaliações dos relatórios de projeto.
	 *
	 * @throws NegocioException -
	 * @throws ArqException -
	 * @throws RemoteException -
	 * @return -
	 * @param mov -
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);

		GenericDAO dao = getGenericDAO(mov);
		MovimentoCadastro aMov = (MovimentoCadastro) mov;
		AvaliacaoRelatorioProjeto avaliacao = (AvaliacaoRelatorioProjeto) aMov.getObjMovimentado();
		ProjetoEnsino pm = avaliacao.getRelatorioProjetoMonitoria().getProjetoEnsino();

		try {
			avaliacao.setStatusAvaliacao(new StatusAvaliacao(StatusAvaliacao.AVALIADO));
			dao.update(avaliacao);

			// @negocio: Coordenação do projeto deseja renovar.
			if (avaliacao.getRelatorioProjetoMonitoria().isDesejaRenovarProjeto()) {

				/// @negocio Avaliador recomenda a avaliação.
				if (avaliacao.isRecomendaRenovacao()) {

					// Verificando se houve solicitação de corte de bolsas pelo avaliador do relatório.
					if ((avaliacao.getRecomendacoesCorteBolsa() == null) ||
							((avaliacao.getRecomendacoesCorteBolsa() != null) && (avaliacao.getRecomendacoesCorteBolsa().isEmpty()))) {
						ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.MON_RENOVADO_SEM_ALTERACOES,
								pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
					} else {
						ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.MON_RENOVADO_COM_REDUCAO_BOLSAS,
								pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
					}

					pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_EM_EXECUCAO));
					registrarRenovacao(aMov, pm);
					ajustarDatasEquipeDocente(aMov, pm);

					// @negocio: Renovação negada pelo avaliador.
				} else {
					//@negocio: Participantes do projeto são punidos e ficam 1 ano sem submeter novas propostas.
					//Altera situação para que depois o gestor de monitoria possa finalizar manualmente.
					pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_NAO_RENOVADO_PELA_COMISSAO_MONITORIA));
				}

				// @negocio: O coordenador Não deseja renovar o projeto.
			} else {
				//Altera situação para que depois o gestor de monitoria possa finalizar manualmente.
				pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_NAO_RENOVADO_PELA_COORDENACAO_DO_PROJETO));
			}

			/** @negocio: Alterando a situação do relatório de projeto utilizado na avaliação. */
			dao.updateField(RelatorioProjetoMonitoria.class, avaliacao.getRelatorioProjetoMonitoria().getId(), "status.id", StatusRelatorio.AVALIADO);


			/** @negocio: Alterando a situação dos relatórios de monitores utilizados na avaliação. */
			for (DiscenteMonitoria dm : pm.getDiscentesMonitoria()) {
				for (RelatorioMonitor rm : dm.getRelatoriosMonitor()) {
					if (rm.isAtivo() && rm.isEnviado() && rm.isRelatorioDesligamento()) {
						dao.updateField(RelatorioMonitor.class, rm.getId(), "status.id", StatusRelatorio.AVALIADO);
					}
				}
			}

			//Criando todas as recomendações de corte de bolsa
			for (RecomendacaoCorteBolsa rec : avaliacao.getRecomendacoesCorteBolsa()) {
				dao.create(rec);
			}

			pm.setAtivo(true);
			dao.update(pm);
			ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);
			ProjetoHelper.gravarHistoricoSituacaoProjeto(pm.getSituacaoProjeto().getId(), pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
			return null;

		} finally {
			dao.close();
		}
	}

	/**
	 * Registra a Renovação do projeto em local específico para melhor controle.
	 * O projeto será renovado por mais 1 ano.
	 *
	 * @param mov Dados do movimento atual.
	 * @param pm Projeto de monitoria que será renovado.
	 * @throws DAOException -
	 */
	private void registrarRenovacao(Movimento mov, ProjetoEnsino pm) throws DAOException {
		GenericDAO dao = getGenericDAO(mov);
		try {
			RenovacaoProjetoMonitoria renovacao = new RenovacaoProjetoMonitoria();
			renovacao.setProjetoEnsino(pm);
			renovacao.setDataRenovacao(new Date());
			renovacao.setDataVigencia(CalendarUtils.adicionarAnos(new Date(), 1));
			renovacao.setEdital(pm.getEditalMonitoria());
			renovacao.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			dao.create(renovacao);
		} finally {
			dao.close();
		}
	}
	
	private void ajustarDatasEquipeDocente(Movimento mov, ProjetoEnsino pm ) throws DAOException {
		EquipeDocenteDao dao = getDAO(EquipeDocenteDao.class, mov);
		pm.setEquipeDocentes(dao.findByProjeto(pm.getId(), true));
		try {
			if(pm.getEquipeDocentes() != null){
				for (EquipeDocente eq : pm.getEquipeDocentes()) {
					if(eq.isAtivo()){
						eq.setDataSaidaProjeto(pm.getProjeto().getDataFim());
						eq.setDataFimOrientacao(pm.getProjeto().getDataFim());
						if(eq.isCoordenador()){
							eq.setDataFimCoordenador(pm.getProjeto().getDataFim());
						}
						dao.update(eq);
					}
				}
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Método responsável pela validação do processamento de avaliação do relatório de projeto.
	 * @param mov -
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		AvaliacaoRelatorioProjeto avaliacao = (AvaliacaoRelatorioProjeto) ((MovimentoCadastro)mov).getObjMovimentado();
		ListaMensagens lista = new ListaMensagens();

		//Se for cortar bolsas, não pode cortar todas...
		if ((avaliacao.getRecomendacoesCorteBolsa() != null) && (!avaliacao.getRecomendacoesCorteBolsa().isEmpty())) {
			int bolsas = avaliacao.getRelatorioProjetoMonitoria().getProjetoEnsino().getBolsasConcedidas();
			int cortes = 0;
			for (RecomendacaoCorteBolsa rcb : avaliacao.getRecomendacoesCorteBolsa()) {
				if (rcb.getDiscenteMonitoria().isSelecionado())	{
					cortes++;
				}
			}

			if (cortes >= bolsas) {
				lista.addErro("Não é permitido o corte total das bolsas do projeto.");
				avaliacao.getRecomendacoesCorteBolsa().clear();
			}
		}

		checkValidation(lista);
	}

}
