/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/09/2006
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Set;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DisciplinaComplementar;
import br.ufrn.sigaa.ensino.tecnico.dominio.EstruturaCurricularTecnica;
import br.ufrn.sigaa.ensino.tecnico.dominio.ModuloCurricular;
/**
 * Processador para cadastrar uma estrutura curricular do ensino técnico
 *
 * @author Andre M Dantas
 *
 */
public class ProcessadorEstruturaCurricularTecnico extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		EstruturaCurricularTecnicaMov ectMov = (EstruturaCurricularTecnicaMov) mov;

		GenericDAO dao = getGenericDAO(mov);

		if ( ectMov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_ESTRUTURA_CURR_TECNICO) ) {
			dao.create(ectMov.getObjMovimentado());
		} else if ( ectMov.getCodMovimento().equals(SigaaListaComando.ALTERAR_ESTRUTURA_CURR_TECNICO) ) {
			atualizar(ectMov);
		}

		return null;
	}
	/**
	 * Atualizando objeto do modulo
	 * @param mov
	 * @throws DAOException
	 */
	private void atualizar(Movimento mov) throws ArqException {
		EstruturaCurricularTecnicaMov ectMov = (EstruturaCurricularTecnicaMov) mov;
		EstruturaCurricularTecnica ect = (EstruturaCurricularTecnica) ectMov.getObjMovimentado();

		GenericDAO dao = getGenericDAO(mov);

		/* removendo individualmente e manualmente os elem.
		 * da coleção de disciplinas complementares e modulosCurriculares
		 */
		Set<DisciplinaComplementar> discRemov = ectMov.getDiscComplemRemovidas();
		if (discRemov != null) {
			for (DisciplinaComplementar disc : discRemov) {
				dao.remove(disc);
			}
		}

		Set<ModuloCurricular> mcRemov = ectMov.getModCurrRemovidos();
		if (mcRemov != null) {
			for (ModuloCurricular modC : mcRemov) {
				dao.remove(modC);
			}
		}
		EstruturaCurricularTecnica ectBD = dao.findByPrimaryKey(ect.getId(), EstruturaCurricularTecnica.class);
		if(ect.getPrazoMaxConclusao() != ectBD.getPrazoMaxConclusao()){
			atualizarPrazoMaximo(ect, mov);
		}
		dao.detach(ectBD);
		dao.update(ect);
	
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

		EstruturaCurricularTecnicaMov ectMov = (EstruturaCurricularTecnicaMov) mov;
		ListaMensagens erros = new ListaMensagens();
		EstruturaCurricularTecnica estrutura = (EstruturaCurricularTecnica) ectMov.getObjMovimentado();

		if (estrutura.getTurno() != null && estrutura.getTurno().getId() == 0)
			estrutura.setTurno(null);
		if (estrutura.getUnidadeTempo() != null && estrutura.getUnidadeTempo().getId() == 0)
			estrutura.setUnidadeTempo(null);

		checkValidation(erros);

	}

	/**
	 * Atualiza o prazoMaxConclusao de todos os alunos ativos de uma estrutura curricular
	 * do nível técnico
	 * @param estrutura
	 * @throws DAOException
	 */
	private void atualizarPrazoMaximo(EstruturaCurricularTecnica estrutura, Movimento mov) throws DAOException {
		DiscenteDao dao = getDAO(DiscenteDao.class, mov);
		try {
			Collection<DiscenteTecnico> discentes = dao.findByEstruturaCurricularTecnica(estrutura);
			for (DiscenteTecnico d : discentes) {
				
				int incr = 0;
				// diminui do semestre atual
				incr = estrutura.getPrazoMaxConclusao() - 1;
				
				// além do prazo máximo do currículo, deve ser somado o número de trancamento de programas
				Collection<MovimentacaoAluno> trancamentos = dao.findTrancamentosByDiscente(d, true);
				if (trancamentos != null)
					incr += trancamentos.size();
				
				// e incrementa também o número de prorrogações administrativas e judiciais
				Collection<MovimentacaoAluno> prorrogacoes = dao.findProrrogacoesByDiscente(d, TipoMovimentacaoAluno.PRORROGACAO_ADMINISTRATIVA, TipoMovimentacaoAluno.PRORROGACAO_JUDICIAL);
				if (prorrogacoes != null) {
					for (MovimentacaoAluno prorrog : prorrogacoes) {
						incr += prorrog.getValorMovimentacao();
					}
				}
				
				int prazo = DiscenteHelper.somaSemestres( d.getAnoIngresso() , d.getPeriodoIngresso(), incr);
				if (d.getPrazoConclusao() == null || prazo != d.getPrazoConclusao()) {
					System.out.println("CORRIGIU PRAZO DE CONCLUSAO");
					dao.updateDiscente(d.getId(), "prazoConclusao", prazo);
				}
			}
		} finally {
			dao.close();
		}

	}

}
