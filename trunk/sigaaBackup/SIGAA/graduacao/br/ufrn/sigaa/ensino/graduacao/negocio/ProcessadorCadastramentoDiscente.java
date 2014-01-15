/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 15/12/2010
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.graduacao.CadastramentoDiscenteDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AlteracaoStatusAluno;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.graduacao.dominio.CancelamentoConvocacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MotivoCancelamentoConvocacao;
import br.ufrn.sigaa.ensino.negocio.ProcessadorDiscente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processador responsável por persistir as informações do cadastramento de discentes.
 * 
 * @author Leonardo Campos
 *
 */
public class ProcessadorCadastramentoDiscente extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		validate(mov);
		MovimentoCadastramentoDiscente m = (MovimentoCadastramentoDiscente) mov;
		
		if(mov.getCodMovimento().equals(SigaaListaComando.CONFIRMAR_CADASTRAMENTO)){
			
			CadastramentoDiscenteDao dao = getDAO(CadastramentoDiscenteDao.class, mov);
			try {
			
				if(!ValidatorUtil.isEmpty(m.getCadastrados())) {
					
					dao.updateStatusDiscentes(m.getCadastrados(), StatusDiscente.CADASTRADO);
				
					for(Discente d: m.getCadastrados()) {
						registrarAlteracaoStatusAluno(mov, dao, d);
						if (isEmpty(d.getMatricula())) {
							ProcessadorDiscente procDiscente = new ProcessadorDiscente();
							procDiscente.gerarMatricula(d, dao);
							dao.updateMatriculaDiscente(d.getId(), d.getMatricula());
						}
					}
				}
				if (m.isDiscentesImportados()) {
					for(Discente d: m.getCancelados()) {
						dao.updateFields(Discente.class, d.getId(),
							new String[] { "status", "matriculaAntiga",	"matricula" }, 
							new Object[] { StatusDiscente.EXCLUIDO,	d.getMatricula(), null });
						registrarAlteracaoStatusAluno(mov, dao, d.getDiscente());
					}
				} else {
					for(CancelamentoConvocacao c: m.getCancelamentos()) {
						if(c.getMotivo().equals(MotivoCancelamentoConvocacao.NAO_COMPARECIMENTO_CADASTRO)) {
							dao.updateFields(Discente.class, c.getConvocacao().getDiscente().getId(),
									new String[] { "status", "matriculaAntiga",	"matricula" }, 
									new Object[] { StatusDiscente.EXCLUIDO,	c.getConvocacao().getDiscente().getMatricula(), null });
							registrarAlteracaoStatusAluno(mov, dao, c.getConvocacao().getDiscente().getDiscente());
						}
						dao.create(c);
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
	private void registrarAlteracaoStatusAluno(Movimento mov,
			CadastramentoDiscenteDao dao, Discente d) throws DAOException {
		AlteracaoStatusAluno alteracao = new AlteracaoStatusAluno();
		alteracao.setAno(d.getAnoIngresso());
		alteracao.setPeriodo(d.getPeriodoIngresso());
		alteracao.setData(new Date());
		alteracao.setMovimento(mov.getCodMovimento().getId());
		alteracao.setUsuario((Usuario) mov.getUsuarioLogado());
		alteracao.setStatus(StatusDiscente.PENDENTE_CADASTRO);
		alteracao.setDiscente(d);
		alteracao.setObservacao("Status alterado durante o cadastramento");
		dao.create(alteracao);
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS, mov);
		MovimentoCadastramentoDiscente m = (MovimentoCadastramentoDiscente) mov;
		for(Discente d: m.getCadastrados()){
			if(d.getStatus() != StatusDiscente.CADASTRADO)
				throw new NegocioException("Os discentes que confirmaram o cadastramento devem assumir status CADASTRADO.");
		}

		for(CancelamentoConvocacao c: m.getCancelamentos()){
			if(c.getMotivo().equals(MotivoCancelamentoConvocacao.NAO_COMPARECIMENTO_CADASTRO) && c.getConvocacao().getDiscente().getStatus() != StatusDiscente.EXCLUIDO)
				throw new NegocioException("Os discentes que não confirmaram o cadastramento devem assumir status EXCLUÍDO.");
			if(c.getMotivo().equals(MotivoCancelamentoConvocacao.REGRA_REGULAMENTO) && c.getConvocacao().getDiscente().getStatus() != StatusDiscente.CANCELADO)
				throw new NegocioException("Os discentes cuja convocação será cancelada devido à regra do regulamento devem permanecer com status CANCELADO.");
		}
	}

}
