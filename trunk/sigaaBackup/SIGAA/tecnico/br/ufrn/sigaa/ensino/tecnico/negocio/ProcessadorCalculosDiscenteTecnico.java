/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 10/11/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.tecnico.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;
import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.expressao.ArvoreExpressao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.AlteracaoMatricula;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoIntegralizacao;
import br.ufrn.sigaa.ensino.negocio.MatriculaComponenteHelper;
import br.ufrn.sigaa.ensino.tecnico.dao.DiscenteTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DisciplinaComplementar;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;

/**
 * Recalcular totais e tipos integralização do discente de nível técnico.
 * 
 * @author Rafael Gomes
 *
 */
public class ProcessadorCalculosDiscenteTecnico extends AbstractProcessador{
	
	public Object execute(Movimento mov) throws NegocioException, ArqException {
		validate(mov);
		calcularTiposIntegralizacao(mov);
		calcularIntegralizacoes(mov);
		return null;
	}
	
	/**
	 * Calcular os tipos de integralização das matrículas
	 * de um discente de técnico.
	 * @param mov
	 * @throws ArqException 
	 */
	private void calcularTiposIntegralizacao(Movimento mov) throws ArqException{
		MovimentoCadastro m = (MovimentoCadastro) mov;
		DiscenteTecnico discente =  (DiscenteTecnico) m.getObjMovimentado();
		
		MatriculaComponenteDao mcdao = getDAO(MatriculaComponenteDao.class, mov);
		
		// loop das matrículas do aluno (pagas e matriculadas)
		Collection<MatriculaComponente> matriculas = mcdao.findPagaseMatriculadasByDiscente(discente);
		
		// conjunto de alterações das matrículas que devem ser realizadas no final do método.
		// Ao ser identificada uma alteração no registro de matriculaComponente
		// é criado e adicionado nessa coleção.
		Collection<AlteracaoMatricula> alteracoes = new ArrayList<AlteracaoMatricula>();
		
		try {
			tiposObrigatorios(mov, discente, matriculas, alteracoes);
			tiposComplementares(mov, discente, matriculas, alteracoes);
			tiposExtraCurricular(mov, discente, matriculas, alteracoes);
			
			if (isNotEmpty(alteracoes))
				mcdao.persistirAlteracoes(alteracoes);
		
		} finally {
			if ( mcdao != null) mcdao.close();
		}
	}

	/** Verifica as equivalências obrigatórias do componente curricular.
	 * @param mov
	 * @param discente
	 * @param matriculas
	 * @param alteracoes
	 * @throws ArqException
	 */
	private void verificarEquivalenciasObrigatorias(Movimento mov, DiscenteTecnico discente, Collection<MatriculaComponente> matriculas, Collection<AlteracaoMatricula> alteracoes) throws ArqException {
		DiscenteTecnicoDao dao = getDAO(DiscenteTecnicoDao.class, mov);
		try {
			Collection<ComponenteCurricular> componentesPagos = new ArrayList<ComponenteCurricular>();
			for (MatriculaComponente mc : matriculas) {
				componentesPagos.add(mc.getComponente());
			}
			List<ComponenteCurricular> pendentes = dao.findComponentesPendentesTecnicoSemEquivalentes(discente.getId(), (List<MatriculaComponente>) matriculas);
			for (ComponenteCurricular ccPendente : pendentes) {
				String equivalencia = ccPendente.getEquivalencia();
				if (equivalencia != null && ExpressaoUtil.eval(equivalencia, ccPendente, componentesPagos)) {
					Collection<ComponenteCurricular> equivalentes = ArvoreExpressao.getMatchesComponentes(equivalencia, ccPendente, componentesPagos);
					if (equivalentes != null) {
						for (ComponenteCurricular ccEquivalente : equivalentes) {
							// pega a matrícula do equivalente
							MatriculaComponente mat = MatriculaComponenteHelper.searchMatricula(matriculas, ccEquivalente);
							if (mat != null) {
								
								AlteracaoMatricula alteracao = createAlteracaoMatricula(mat, mov, TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA);
								if (alteracao != null) {
									alteracoes.add(alteracao);
									mat.setTipoIntegralizacao(TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA);
								}
							}
						}
					}
				}
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Define os componentes pagos que são complementares
	 * 
	 * @param mov
	 * @param discente
	 * @param matriculas
	 * @param alteracoes
	 */
	private void tiposComplementares(Movimento mov, DiscenteTecnico discente, Collection<MatriculaComponente> matriculas, Collection<AlteracaoMatricula> alteracoes) {
		Set<DisciplinaComplementar> disciplinasComplementares = discente.getEstruturaCurricularTecnica().getDisciplinasComplementares();
		List<Integer> idComplementares = new ArrayList<Integer>();
		for (DisciplinaComplementar dc : disciplinasComplementares) {
			idComplementares.add(dc.getDisciplina().getId());
		}
		
		
		// verifica as matrículas que são complementares na estrutura Curricular
		for (MatriculaComponente matricula : matriculas) {
			if ( idComplementares.contains(matricula.getComponente().getId()) ){
				
				if ( (matricula.getTipoIntegralizacao() != null 
						&& !matricula.getTipoIntegralizacao().equals(TipoIntegralizacao.OBRIGATORIA) 
						&& !matricula.getTipoIntegralizacao().equals(TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA) )
					 || matricula.getTipoIntegralizacao() == null ) {

					AlteracaoMatricula alteracao = createAlteracaoMatricula(matricula, mov, TipoIntegralizacao.OPTATIVA_DA_GRADE);
					if (alteracao != null) {
						alteracoes.add(alteracao);
						matricula.setTipoIntegralizacao(TipoIntegralizacao.OPTATIVA_DA_GRADE);
					}
				}
			}
		}
	}
	
	/**
	 * Define os componentes pagos que são extra curriculares
	 * 
	 * @param mov
	 * @param discente
	 * @param matriculas
	 * @param alteracoes
	 */
	private void tiposExtraCurricular(Movimento mov, DiscenteTecnico discente, Collection<MatriculaComponente> matriculas, Collection<AlteracaoMatricula> alteracoes) {
		
		// verifica as matrículas que são extra curriculares, sendo consideradas as disciplinas pagas sem tipo de integralização definido anteriormente.
		for (MatriculaComponente matricula : matriculas) {
			if ( matricula.getTipoIntegralizacao() == null ) {
				AlteracaoMatricula alteracao = createAlteracaoMatricula(matricula, mov, TipoIntegralizacao.EXTRA_CURRICULAR);
				if (alteracao != null) {
					alteracoes.add(alteracao);
					matricula.setTipoIntegralizacao(TipoIntegralizacao.EXTRA_CURRICULAR);
				}
			}
		}
	}

	/**
	 * Define os componentes pagos que são obrigatórios
	 * 
	 * @param mov
	 * @param discente
	 * @param matriculas
	 * @param alteracoes
	 * @throws ArqException 
	 */
	private void tiposObrigatorios(Movimento mov, DiscenteTecnico discente, Collection<MatriculaComponente> matriculas, Collection<AlteracaoMatricula> alteracoes) throws ArqException {
		
		verificarObrigatorias(mov, discente, matriculas, alteracoes);
		
		verificarEquivalenciasObrigatorias(mov, discente, matriculas, alteracoes);
	}

	/**
	 * Verifica matrículas OBRIGATORIAS
	 * 
	 * @param mov
	 * @param discente
	 * @param matriculas
	 * @param alteracoes
	 * @throws DAOException
	 */
	private void verificarObrigatorias(Movimento mov, DiscenteTecnico discente, Collection<MatriculaComponente> matriculas, Collection<AlteracaoMatricula> alteracoes) throws DAOException {
		Set<Modulo> modulos = discente.getEstruturaCurricularTecnica().getModulos();
		
		Set<Integer> idDisciplinasObrigatorias = new HashSet<Integer>();
		
		for (Modulo modulo : modulos) {
			for (ComponenteCurricular cc : modulo.getDisciplinas()) {
				idDisciplinasObrigatorias.add(cc.getId());
			} 
		}
		
		// verifica as matrículas que são complementares na estrutura Curricular
		for (MatriculaComponente matricula : matriculas) {
			if ( idDisciplinasObrigatorias.contains(matricula.getComponente().getId()) ){
				
				AlteracaoMatricula alteracao = createAlteracaoMatricula(matricula, mov, TipoIntegralizacao.OBRIGATORIA);
				if (alteracao != null) {
					alteracoes.add(alteracao);
					matricula.setTipoIntegralizacao(TipoIntegralizacao.OBRIGATORIA);
				}
			}
		}
	}
	
	/**
	 * Calcula o valor das carga horárias das integralizações do discente
	 *  
	 */
	private void calcularIntegralizacoes(Movimento mov) throws ArqException{
		MovimentoCadastro m = (MovimentoCadastro) mov;
		DiscenteTecnico discente =  (DiscenteTecnico) m.getObjMovimentado();
		discente.setChOptativaIntegralizada((short) 0);
		discente.setChOptativaPendente((short) 0);
		
		MatriculaComponenteDao mcDao = getDAO(MatriculaComponenteDao.class, mov);
		DiscenteTecnicoDao dtDao = getDAO(DiscenteTecnicoDao.class, mov);
		
		try {
			Collection<MatriculaComponente> matriculas = mcDao.findByDiscente(discente, SituacaoMatricula.getSituacoesPagas());
			integralizarObrigatoriasEEquivalentes(discente, matriculas);
			integralizarOptativas(discente, matriculas);
			dtDao.atualizaTotaisIntegralizados(discente);
		} finally {
			if ( mcDao != null ) mcDao.close();
			if ( dtDao != null)	dtDao.close();	
		}
	}

	/**
	 * Calcula CH de optativas
	 * @param discente
	 * @param matriculas
	 */
	private void integralizarOptativas(DiscenteTecnico discente, Collection<MatriculaComponente> matriculas) {
		Integer chOptativasMinima = discente.getEstruturaCurricularTecnica().getChOptativasMinima();
		short chOptativaIntegralizada = 0;
		for (MatriculaComponente matricula : matriculas) {
			if (matricula.getTipoIntegralizacao() != null && matricula.getTipoIntegralizacao().equals(TipoIntegralizacao.OPTATIVA_DA_GRADE))
				chOptativaIntegralizada += matricula.getComponenteCHTotal();
		}
		
		discente.setChOptativaIntegralizada( chOptativaIntegralizada );
		discente.setChOptativaPendente(
				(short) max(0, (chOptativasMinima != null ? chOptativasMinima : 0) - discente.getChOptativaIntegralizada()));
		
		
	}
	
	/**
	 * Calcula Ch de Obrigatorias e Equilalentes a obrigatorias
	 * 
	 * @param discente
	 * @param matriculas
	 */
	private void integralizarObrigatoriasEEquivalentes(DiscenteTecnico discente, Collection<MatriculaComponente> matriculas) {
		
		Integer chTotal = discente.getEstruturaCurricularTecnica().getChTotalModulos();
		
		int chObrigatoriaIntegralizada = 0;
		for (MatriculaComponente matricula : matriculas) {
			if (matricula.getTipoIntegralizacao() != null && matricula.getTipoIntegralizacao().equals(TipoIntegralizacao.OBRIGATORIA))
				chObrigatoriaIntegralizada += matricula.getComponenteCHTotal();
			
			if (matricula.getTipoIntegralizacao() != null && matricula.getTipoIntegralizacao().equals(TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA))
				chObrigatoriaIntegralizada += matricula.getComponenteCHTotal();
		}
		
		discente.setChObrigatoriaIntegralizada( chObrigatoriaIntegralizada );
		discente.setChObrigatoriaPendente( max(0, (chTotal != null ? chTotal : 0) - discente.getChObrigatoriaIntegralizada()) );
		 
	}	
	
	/**
	 * Cria um registro de alteração de situação de matrícula de um discente
	 * 
	 * @param matricula
	 * @param mov
	 * @param novoTipo
	 * @return
	 */
	private AlteracaoMatricula createAlteracaoMatricula(MatriculaComponente matricula, Movimento mov, String novoTipo) {
		// só é criada uma instância de AlteracaoMatricula se o tipo de integer.
		// for realmente diferente
		if (novoTipo != null && novoTipo.equalsIgnoreCase(matricula.getTipoIntegralizacao())) {
			return null;
		}
		AlteracaoMatricula alteracao = new AlteracaoMatricula();
		alteracao.setMatricula(matricula);
		alteracao.setDataAlteracao(new Date());
		alteracao.setUsuario((Usuario) mov.getUsuarioLogado());
		alteracao.setCodMovimento(mov.getCodMovimento().getId());
		alteracao.setTipoIntegralizacaoAntigo(matricula.getTipoIntegralizacao());
		alteracao.setTipoIntegralizacaoNovo(novoTipo);
		alteracao.setSituacaoAntiga(matricula.getSituacaoMatricula());
		alteracao.setSituacaoNova(matricula.getSituacaoMatricula());

		return alteracao;
	}
	
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro m = (MovimentoCadastro) mov;
		DiscenteTecnico discente =  (DiscenteTecnico) m.getObjMovimentado();
		
		if(ValidatorUtil.isEmpty( discente.getEstruturaCurricularTecnica() )) {
			throw new NegocioException("O histórico não pode ser calculado pois o discente não está vinculado a uma estrutura curricular.");
		}
		if(ValidatorUtil.isEmpty( discente.getTurmaEntradaTecnico() )) {
			throw new NegocioException("O histórico não pode ser calculado pois o discente não está vinculado a uma turma de entrada.");
		}
	}
}
