/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/09/2006
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pesquisa.dominio.HistoricoPlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioBolsaFinal;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioBolsaParcial;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusPlanoTrabalho;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;

/**
 * Processador responsável pelo pelo cadastro de planos de trabalho de projetos de pesquisa
 *
 * @author ricardo
 *
 */
public class ProcessadorPlanoTrabalho extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		PlanoTrabalho planoTrabalho = (PlanoTrabalho) mov;
		Usuario usuario = (Usuario) mov.getUsuarioLogado();

		 if ( !planoTrabalho.getCodMovimento().equals(SigaaListaComando.REMOVER_PLANO_TRABALHO)) {
			 // Validar informações do plano de trabalho
			 validate(planoTrabalho);
		 }

		// Persistir plano de trabalho
		GenericDAO dao = getGenericDAO(mov);

		try {
			if ( planoTrabalho.getCodMovimento().equals(SigaaListaComando.CADASTRAR_PLANO_TRABALHO) ) {

				// Se for usuário de pesquisa, o status deve ter sido escolhido
				if (!usuario.isUserInRole(SigaaPapeis.GESTOR_PESQUISA) || TipoStatusPlanoTrabalho.getDescricao(planoTrabalho.getStatus()) == null) {

					// Solicitações de cotas e cadastros de planos voluntários iniciam com status cadastrado
					if (planoTrabalho.getTipoBolsa().getId() == TipoBolsaPesquisa.A_DEFINIR || 
							TipoBolsaPesquisa.isVoluntario(planoTrabalho.getTipoBolsa().getId()) ) {
						planoTrabalho.setStatus(TipoStatusPlanoTrabalho.CADASTRADO);
					} else {
						planoTrabalho.setStatus(TipoStatusPlanoTrabalho.EM_ANDAMENTO);
					}
				}

				planoTrabalho.setDataCadastro(new Date());
				planoTrabalho.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
				planoTrabalho.setMembroProjetoDiscente(null);

				if(planoTrabalho.getId() == 0) {
					dao.create(planoTrabalho);
					// Cadastrar histórico
					HistoricoPlanoTrabalho historico = gerarEntradaHistorico( planoTrabalho, mov);
					dao.create(historico);
				} else {
					throw new NegocioException("Tentativa de sobrescrição de plano de trabalho!!");
				}

			} else  if ( planoTrabalho.getCodMovimento().equals(SigaaListaComando.ALTERAR_PLANO_TRABALHO)
					|| planoTrabalho.getCodMovimento().equals(SigaaListaComando.GRAVAR_PLANO_TRABALHO)) {

				PlanoTrabalho planoAnterior = dao.findByPrimaryKey(planoTrabalho.getId(), PlanoTrabalho.class);

				// Remover cronogramas antigos antes de salvar os novos
				List<CronogramaProjeto> cronogramas = planoAnterior.getCronogramas();
				planoAnterior.setCronogramas(null);
				for (CronogramaProjeto cronograma : cronogramas) {
					dao.remove(cronograma);
				}

				if (!usuario.isUserInRole(SigaaPapeis.GESTOR_PESQUISA) && planoTrabalho.getStatus() == TipoStatusPlanoTrabalho.AGUARDANDO_RESUBMISSAO
				        || !usuario.isUserInRole(SigaaPapeis.GESTOR_PESQUISA) &&  planoTrabalho.getStatus() == TipoStatusPlanoTrabalho.APROVADO_COM_RESTRICOES
				        || !usuario.isUserInRole(SigaaPapeis.GESTOR_PESQUISA) &&  planoTrabalho.getStatus() == TipoStatusPlanoTrabalho.NAO_APROVADO ) {
					planoTrabalho.setStatus(TipoStatusPlanoTrabalho.CORRIGIDO);
				}

				if (planoTrabalho.getCota() != null && planoTrabalho.getCota().getId() == 0)
					planoTrabalho.setCota(null);
				
				dao.detach(planoAnterior);
				dao.update(planoTrabalho);

				// Cadastrar histórico
				HistoricoPlanoTrabalho historico = gerarEntradaHistorico( planoTrabalho, mov);
				dao.create(historico);

				// Caso o tipo de bolsa do plano tenha sido alterado, chamar o processador alteração de tipo de bolsa
				if (planoAnterior.getTipoBolsa() != planoTrabalho.getTipoBolsa()) {
					ProcessadorMudarTipoBolsa processadorMudarTipoBolsa = new ProcessadorMudarTipoBolsa();
					processadorMudarTipoBolsa.execute(mov);
				}

			} else  if ( planoTrabalho.getCodMovimento().equals(SigaaListaComando.REMOVER_PLANO_TRABALHO)) {

				planoTrabalho = dao.findByPrimaryKey(planoTrabalho.getId(), PlanoTrabalho.class);

				// Remover relatório parcial de bolsa
				if (planoTrabalho.getRelatorioBolsaParcial() != null) {
					RelatorioBolsaParcial relatorio = planoTrabalho.getRelatorioBolsaParcial();
					planoTrabalho.setRelatoriosBolsaParcial(null);
					dao.updateField(RelatorioBolsaParcial.class, relatorio.getId(), "ativo", false);
				}
				
				// Remover relatório final de bolsa
				if(planoTrabalho.getRelatorioBolsaFinal() != null) {
					RelatorioBolsaFinal relatorio = planoTrabalho.getRelatorioBolsaFinal();
					planoTrabalho.setRelatoriosBolsaFinal(null);
					dao.updateField(RelatorioBolsaFinal.class, relatorio.getId(), "ativo", false);
				}

				// Remover histórico de associação do plano aos bolsistas
				if (planoTrabalho.getMembrosDiscentes().size() > 0) {
					planoTrabalho.setMembroProjetoDiscente(null);

					Collection<MembroProjetoDiscente> membros = planoTrabalho.getMembrosDiscentes();
					planoTrabalho.setMembrosDiscentes(null);
					for (MembroProjetoDiscente membro : membros) {
						dao.updateField(MembroProjetoDiscente.class, membro.getId(), "bolsistaAnterior", null);
					}
					for (MembroProjetoDiscente membro : membros) {
						dao.updateField(MembroProjetoDiscente.class, membro.getId(), "dataFinalizacao", new Date());
					}
				}
				
				dao.updateField(PlanoTrabalho.class, planoTrabalho.getId(), "status", TipoStatusPlanoTrabalho.EXCLUIDO);
			}
		} finally {
			dao.close();
		}

		return null;
	}

	/**
	 * Gerar um objeto que representa uma entrada no histórico de um plano de trabalho
	 *
	 * @param planoTrabalho
	 * @param mov
	 * @return
	 */
	public static HistoricoPlanoTrabalho gerarEntradaHistorico(PlanoTrabalho planoTrabalho, Movimento mov) {
		HistoricoPlanoTrabalho historico = new HistoricoPlanoTrabalho();
		historico.setPlanoTrabalho(planoTrabalho);
		historico.setStatus(planoTrabalho.getStatus());
		historico.setTipoBolsa( planoTrabalho.getTipoBolsa() );
		historico.setData( new Date() );
		historico.setRegistroEntrada(  mov.getUsuarioLogado().getRegistroEntrada() );

		return historico;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		PlanoTrabalho planoTrabalho = (PlanoTrabalho) mov;
		ListaMensagens erros = new ListaMensagens();

		PlanoTrabalhoValidator.validarDadosGerais(planoTrabalho, null, mov.getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_PESQUISA), erros);
		if( !planoTrabalho.getCodMovimento().equals(SigaaListaComando.GRAVAR_PLANO_TRABALHO))
			PlanoTrabalhoValidator.validarCronograma(planoTrabalho, erros);
//		PlanoTrabalhoValidator.validarNumeroDeBolsistas(planoTrabalho, erros);
		
		checkValidation(erros);
	}

}
