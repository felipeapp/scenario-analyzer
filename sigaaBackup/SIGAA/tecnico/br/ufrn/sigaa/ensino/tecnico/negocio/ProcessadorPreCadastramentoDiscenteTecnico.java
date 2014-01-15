/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 15/12/2010
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AlteracaoStatusAluno;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.tecnico.dao.CadastramentoDiscenteTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.CancelamentoConvocacaoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.MotivoCancelamentoConvocacaoTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processador responsável por persistir as informações do cadastramento de discentes.
 * 
 * @author Leonardo Campos
 *
 */
public class ProcessadorPreCadastramentoDiscenteTecnico extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		validate(mov);
		MovimentoCadastramentoDiscenteTecnico m = (MovimentoCadastramentoDiscenteTecnico) mov;
		
		
		if (mov.getCodMovimento().equals(SigaaListaComando.CONFIRMAR_PRE_CADASTRAMENTO_TECNICO)){
			
			CadastramentoDiscenteTecnicoDao dao = getDAO(CadastramentoDiscenteTecnicoDao.class, mov);
			int novoStatus = StatusDiscente.PRE_CADASTRADO;
			
			try {
			
				if (!ValidatorUtil.isEmpty(m.getCadastrados())) {
					
					dao.updateStatusDiscentes(m.getCadastrados(), novoStatus);
				
					for (Discente d: m.getCadastrados())
						registrarAlteracaoStatusAluno(mov, dao, d, novoStatus);
				}
				if (m.isDiscentesImportados()) {
					for(Discente d: m.getCancelados()) {
						dao.updateFields(Discente.class, d.getId(),
							new String[] { "status", "matriculaAntiga",	"matricula" }, 
							new Object[] { StatusDiscente.EXCLUIDO,	d.getMatricula(), null });
						registrarAlteracaoStatusAluno(mov, dao, d.getDiscente(), StatusDiscente.EXCLUIDO);
					}
				} else {
					for(CancelamentoConvocacaoTecnico c: m.getCancelamentos()) {
						if(c.getMotivo().equals(MotivoCancelamentoConvocacaoTecnico.NAO_COMPARECIMENTO_CADASTRO)) {
							dao.updateFields(Discente.class, c.getConvocacao().getDiscente().getId(),
									new String[] { "status", "matriculaAntiga",	"matricula" }, 
									new Object[] { StatusDiscente.EXCLUIDO,	c.getConvocacao().getDiscente().getMatricula(), null });
							registrarAlteracaoStatusAluno(mov, dao, c.getConvocacao().getDiscente().getDiscente(), StatusDiscente.EXCLUIDO);
						}
						dao.createNoFlush(c);
					}
				}
			} finally {
				dao.close();
			}
			
		}
		
		return null;
	}

	/**
	 * Registra a alteração de status do aluno.
	 * 
	 * @param mov
	 * @param dao
	 * @param d
	 * @throws DAOException
	 */
	private void registrarAlteracaoStatusAluno(Movimento mov, CadastramentoDiscenteTecnicoDao dao, Discente d, int novoStatus) throws DAOException {
		AlteracaoStatusAluno alteracao = new AlteracaoStatusAluno();
		alteracao.setAno(d.getAnoIngresso());
		alteracao.setPeriodo(d.getPeriodoIngresso());
		alteracao.setData(new Date());
		alteracao.setMovimento(mov.getCodMovimento().getId());
		alteracao.setUsuario((Usuario) mov.getUsuarioLogado());
		alteracao.setStatus(novoStatus);
		alteracao.setDiscente(d);
		alteracao.setObservacao("Status alterado durante o cadastramento");
		dao.createNoFlush(alteracao);
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Checar papel
		//checkRole(SigaaPapeis.GESTOR_CONVOCACOES_VESTIBULAR, mov);
		MovimentoCadastramentoDiscenteTecnico m = (MovimentoCadastramentoDiscenteTecnico) mov;
		for(Discente d: m.getCadastrados()){
			if(d.getStatus() != StatusDiscente.CADASTRADO && d.getStatus() != StatusDiscente.PRE_CADASTRADO)
				throw new NegocioException("Os discentes que confirmaram o cadastramento devem assumir status CADASTRADO.");
		}

		for(CancelamentoConvocacaoTecnico c: m.getCancelamentos()){
			if(c.getMotivo().equals(MotivoCancelamentoConvocacaoTecnico.NAO_COMPARECIMENTO_CADASTRO) && c.getConvocacao().getDiscente().getStatus() != StatusDiscente.EXCLUIDO)
				throw new NegocioException("Os discentes que não confirmaram o cadastramento devem assumir status EXCLUÍDO.");
			if(c.getMotivo().equals(MotivoCancelamentoConvocacaoTecnico.REGRA_REGULAMENTO) && c.getConvocacao().getDiscente().getStatus() != StatusDiscente.CANCELADO)
				throw new NegocioException("Os discentes cuja convocação será cancelada devido à regra do regulamento devem permanecer com status CANCELADO.");
		}
	}

}
