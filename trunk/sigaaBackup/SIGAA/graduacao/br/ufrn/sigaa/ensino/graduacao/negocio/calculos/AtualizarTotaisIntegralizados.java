/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 02/03/2010
 * Autor:     David Pereira
 */
package br.ufrn.sigaa.ensino.graduacao.negocio.calculos;

import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.AlteracaoDiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.negocio.calculos.CalculosDiscenteChainNode;

/**
 * Classe para atualizar os totais integralizados
 * por um discente de graduação.
 * 
 * @author David Pereira
 *
 */
public class AtualizarTotaisIntegralizados extends CalculosDiscenteChainNode<DiscenteGraduacao> {

	@Override
	public void processar(DiscenteGraduacao d, Movimento mov, boolean preProcessamento) throws ArqException, NegocioException {
		DiscenteGraduacaoDao dao = getDAO(DiscenteGraduacaoDao.class);
		dao.setUsuario(mov.getUsuarioLogado());
		
		try {
			
			IntegralizacoesHelper.calcularIntegralizacoes(d, SituacaoMatricula.getSituacoesPagas(), mov);
			dao.atualizaTotaisIntegralizados(d);

			registraAlteracaoCalculos(d.getId(), dao, mov);
			
		} finally {
			dao.close();
		}
	}

	
	
	/**
	 * Registra a alteração nos cálculos realizados para o discente
	 * 
	 * @param idDiscenteGraduacao
	 * @param dao
	 * @param mov
	 * @throws DAOException
	 */
	private void registraAlteracaoCalculos(int idDiscenteGraduacao, DiscenteGraduacaoDao dao, Movimento mov) throws DAOException {

		DiscenteGraduacao dg = dao.findTotaisIntegralizadosByDiscente(idDiscenteGraduacao);

		AlteracaoDiscenteGraduacao alteracao = new AlteracaoDiscenteGraduacao();
		alteracao.setDiscente(dg);
		alteracao.setData(new Date());
		alteracao.setOperacao(mov.getCodMovimento().getId());
		alteracao.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());

		alteracao.setChTotalIntegralizada(dg.getChTotalIntegralizada());
		alteracao.setChTotalPendente(dg.getChTotalPendente());
		alteracao.setChOptativaIntegralizada(dg.getChOptativaIntegralizada());
		alteracao.setChOptativaPendente(dg.getChOptativaPendente());
		alteracao.setChNaoAtividadeObrigInteg(dg.getChNaoAtividadeObrigInteg());
		alteracao.setChNaoAtividadeObrigPendente(dg.getChNaoAtividadeObrigPendente());
		alteracao.setChAtividadeObrigInteg(dg.getChAtividadeObrigInteg());
		alteracao.setChAtividadeObrigPendente(dg.getChAtividadeObrigPendente());
		alteracao.setChAulaIntegralizada(dg.getChAulaIntegralizada());
		alteracao.setChAulaPendente(dg.getChAulaPendente());
		alteracao.setChLabIntegralizada(dg.getChLabIntegralizada());
		alteracao.setChLabPendente(dg.getChLabPendente());
		alteracao.setChEstagioIntegralizada(dg.getChEstagioIntegralizada());
		alteracao.setChEstagioPendente(dg.getChEstagioPendente());
		alteracao.setCrTotalIntegralizados(dg.getCrTotalIntegralizados());
		alteracao.setCrTotalPendentes(dg.getCrTotalPendentes());
		alteracao.setCrExtraIntegralizados(dg.getCrExtraIntegralizados());
		alteracao.setCrNaoAtividadeObrigInteg(dg.getCrNaoAtividadeObrigInteg());
		alteracao.setCrNaoAtividadeObrigPendente(dg.getCrNaoAtividadeObrigPendente());
		alteracao.setCrLabIntegralizado(dg.getCrLabIntegralizado());
		alteracao.setCrLabPendente(dg.getCrLabPendente());
		alteracao.setCrEstagioIntegralizado(dg.getCrEstagioIntegralizado());
		alteracao.setCrEstagioPendente(dg.getCrEstagioPendente());
		alteracao.setCrAulaIntegralizado(dg.getCrAulaIntegralizado());
		alteracao.setCrAulaPendente(dg.getCrAulaPendente());
		alteracao.setChIntegralizadaAproveitamentos(dg.getChIntegralizadaAproveitamentos());
		
		dao.create(alteracao);
	}
	
	
	
}
