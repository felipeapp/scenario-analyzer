/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on May 31, 2007
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.AlteracaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.DiscenteCalculosHelper;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe com métodos utilitários sobre matrícula em componentes
 *
 * @author Victor Hugo
 *
 */
public class MatriculaComponenteHelper {

	
	/**
	 * 
	 * Método utilizado para registrar uma alteração de mátricula.
	 * 
	 * @param matriculaAtual
	 * @param novaSituacao
	 * @param mov
	 * @param dao
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public static AlteracaoMatricula alterarSituacaoMatricula(MatriculaComponente matriculaAtual, SituacaoMatricula novaSituacao,
			Movimento mov, MatriculaComponenteDao dao) throws NegocioException, ArqException {
		return alterarSituacaoMatricula(matriculaAtual, novaSituacao, mov, dao, false);
	}
	
	/**
	 * registra o histórico de alteração da situação da matrícula do componente
	 *
	 * @param matriculaNova
	 *            nova matrícula já setada que a nova situação
	 * @param dao
	 * @param usuarioLogado
	 *            usuário que está realizando a alteração
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public static AlteracaoMatricula alterarSituacaoMatricula(MatriculaComponente matriculaAtual, SituacaoMatricula novaSituacao,
			Movimento mov, MatriculaComponenteDao dao, boolean atualizarCalculosDiscente) throws NegocioException, ArqException {

		if (matriculaAtual == null || dao == null || novaSituacao == null)
			throw new IllegalArgumentException("Erro ao registrar alteração de matrículas.");

		if (matriculaAtual.getId() == 0) {
			throw new NegocioException("Erro ao registrar alteração de matrículas.");
		}

		if (matriculaAtual.getTurma() != null 
				&& novaSituacao.getId() != SituacaoMatricula.CANCELADO.getId()
				&& novaSituacao.getId() != SituacaoMatricula.EXCLUIDA.getId()
				&& dao.countByDiscenteTurmas(matriculaAtual.getDiscente(), matriculaAtual.getTurma(), novaSituacao) > 1) {
			throw new NegocioException("Erro ao registrar alteração de matrículas.<br>"
					+ "Não é permitido alterar situação da matrícula dessa turma para a situação escolhida"
					+ ", pois já existem registros de matrículas dessa turma com essa situação");
		}

		AlteracaoMatricula alteracao = new AlteracaoMatricula();
		alteracao.setMatricula(matriculaAtual);
		alteracao.setSituacaoAntiga(matriculaAtual.getSituacaoMatricula());
		alteracao.setSituacaoNova(novaSituacao);
		alteracao.setDataAlteracao(new Date());
		alteracao.setUsuario((Usuario) mov.getUsuarioLogado());
		alteracao.setCodMovimento(mov.getCodMovimento().getId());
		if (SigaaListaComando.AFASTAR_ALUNO.equals(mov.getCodMovimento())) {
			MovimentacaoAluno afastamento = (MovimentacaoAluno) ((MovimentoCadastro) mov).getObjMovimentado();
			if (afastamento.isTrancamento() || afastamento.isCancelamento())
				alteracao.setMovimentacaoAluno(afastamento);
		}

		dao.create(alteracao);
		dao.updateField(MatriculaComponente.class, matriculaAtual.getId(), "situacaoMatricula", novaSituacao);

		DiscenteDao discenteDao = getDAO(DiscenteDao.class);
		DiscenteGraduacaoDao dgDao = getDAO(DiscenteGraduacaoDao.class);
		
		try {
			dgDao.zerarIntegralizacoes(matriculaAtual.getDiscente().getId());
			
			matriculaAtual = dao.findByPrimaryKey(matriculaAtual.getId(), MatriculaComponente.class);
			Discente disc = discenteDao.findByPrimaryKeyOtimizado( matriculaAtual.getDiscente().getId() );
			if( disc.isGraduacao()
					&& ( StatusDiscente.getStatusComVinculo().contains(disc.getStatus() ) || disc.getStatus() == StatusDiscente.CONCLUIDO ) ){
				if (atualizarCalculosDiscente) {
					DiscenteGraduacao grad = (DiscenteGraduacao) discenteDao.findByPK(matriculaAtual.getDiscente().getId());
					DiscenteCalculosHelper.atualizarTodosCalculosDiscente(grad, mov);
				} else {
					dao.updateField(DiscenteGraduacao.class, disc.getId(), "ultimaAtualizacaoTotais", null); 
				}
			} else if(disc.isTecnico()){
				
				/*
				 * Não está sendo realizado nenhum cálculo devido algumas pendências com a usuária.
				 * */
				
			}
		} finally {
			discenteDao.close();
			dgDao.close();
		}
		
		dao.detach(matriculaAtual);
		return alteracao;

	}

	/**
	 * 
	 * Utilizado para retornar uma das matriculas da coleção de matriculas, desde que uma delas seja do componente
	 * especificado e a matricula esteja em uma das condições também especificadas.
	 * 
	 * @param matriculas
	 * @param componente
	 * @param situacao
	 * @return
	 */
	public static MatriculaComponente searchMatricula(Collection<MatriculaComponente> matriculas, ComponenteCurricular componente, Collection<SituacaoMatricula> situacao) {
		for (MatriculaComponente mat : matriculas) {
			if (mat.getComponente().getId() == componente.getId() && situacao.contains(mat.getSituacaoMatricula()))
				return mat;
		}
		return null;
	}
	
	
	/**
	 * 
	 * Utilizado para retornar uma das matriculas da coleção de matriculas, desde que uma delas seja do componente
	 * especificado.
	 * 
	 * @param matriculas
	 * @param componente
	 * @return
	 */
	public static MatriculaComponente searchMatricula(Collection<MatriculaComponente> matriculas, ComponenteCurricular componente) {
		for (MatriculaComponente mat : matriculas) {
			if (mat.getComponente().getId() == componente.getId())
				return mat;
		}
		return null;
	}

	
	/**
	 * 
	 * Utilizado para retornar uma das matriculas da coleção de matriculas, desde que uma delas seja na turma
	 * especificada.
	 * 
	 * @param matriculas
	 * @param t
	 * @return
	 */
	public static MatriculaComponente searchMatricula(Collection<MatriculaComponente> matriculas, Turma t) {
		for (MatriculaComponente mat : matriculas) {
			if (mat.getTurma().getId() == t.getId())
				return mat;
		}
		return null;
	}

	/**
	 * Cadastra unidades e avaliações para uma matrícula que foi realizada fora do prazo ou de forma compulsória.
	 * @param numUnidades
	 */
	public static void cadastrarAvaliacoesMatriculaCompulsoria(MatriculaComponente matricula, Integer numUnidades, TurmaDao tDao, AvaliacaoDao aDao)
			throws DAOException {

		Turma turma = tDao.findByPrimaryKey(matricula.getTurma().getId(), Turma.class);
		MatriculaComponente outroAluno = tDao.findMatriculaAleatoriaByTurma(turma, matricula);

		if (outroAluno != null) {

			if (!outroAluno.getNotas().isEmpty()) {

				int unidade = 0;
				matricula.setNotas(new ArrayList<NotaUnidade>());
				for (int i = 0; i < numUnidades; i++) {
					/* Cadastro das notas */
					NotaUnidade nota = new NotaUnidade();
					nota.setMatricula(matricula);
					nota.setUnidade((byte) (unidade + 1));
					matricula.getNotas().add(nota);
					tDao.create(nota);

					/* Cadastro das Avaliações */
					NotaUnidade notaOutroAluno = outroAluno.getNotaByIndice(unidade + 1);

					if (notaOutroAluno != null) {
						List<Avaliacao> avaliacoes = aDao.findByNotaUnidade(notaOutroAluno.getId());
						if (avaliacoes != null) {
							for (Avaliacao avaliacaoOutroAluno : avaliacoes) {
								Avaliacao a = new Avaliacao();
								a.setAbreviacao(avaliacaoOutroAluno.getAbreviacao());
								a.setDenominacao(avaliacaoOutroAluno.getDenominacao());
								a.setNota(null);
								a.setPeso(avaliacaoOutroAluno.getPeso());
								a.setNotaMaxima(avaliacaoOutroAluno.getNotaMaxima());
								if ( avaliacaoOutroAluno.getAtividadeQueGerou() != null ) {
									Integer idAvaliacao = avaliacaoOutroAluno.getAtividadeQueGerou().getId();
									a.setAtividadeQueGerou(avaliacaoOutroAluno.getAtividadeQueGerou());
									a.getAtividadeQueGerou().setId(idAvaliacao);
								}
								a.setUnidade(nota);
								a.setEntidade(avaliacaoOutroAluno.getEntidade());

							tDao.create(a);
							}
						}
					}

					unidade++;
				}

			}
		}
	}

	/**
	 * 
	 * Retorna um dao especifico de acordo com a classe passada.
	 * 
	 * @param <T>
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}

	/**
	 * Utilizado para validar a matrícula de um discente
	 * @param discente
	 * @param matricula
	 * @param erros
	 * @throws ArqException
	 */
	public static void validarMatriculaComponente(DiscenteAdapter discente, MatriculaComponente matricula,
			ListaMensagens erros) throws ArqException {
		Collection<MatriculaComponente> mats = new ArrayList<MatriculaComponente>(0);
		mats.add(matricula);
		validarMatriculaComponente(discente, mats, null, erros);
	}

	/**
	 * 
	 * Utilizado para validar a matrículas de um discente
	 * 
	 * @param discente
	 * @param matriculas
	 * @param situacaoNova
	 * @param erros
	 * @throws ArqException
	 */
	public static void validarMatriculaComponente(DiscenteAdapter discente, Collection<MatriculaComponente> matriculas,
				SituacaoMatricula situacaoNova, ListaMensagens erros) throws ArqException {

		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
		try {
			Collection<MatriculaComponente> pagos = dao.findBySituacoes(discente, SituacaoMatricula.getSituacoesPagasArray());
			Collection<MatriculaComponente> matriculados = dao.findBySituacoes(discente, SituacaoMatricula.MATRICULADO, SituacaoMatricula.EM_ESPERA);
			Collection<MatriculaComponente> pagosEMatriculados = new ArrayList<MatriculaComponente>(pagos);
			pagosEMatriculados.addAll(matriculados);


			for (MatriculaComponente mat : matriculas) {

				// a nova situacao deveria ser passada sempre pelo metodo 
				// mas como ja tem muitos casos de uso usando situacaoNova = mat.getSituacaoMatricula()
				// deixei para manter a compatiblidade
				if (situacaoNova == null)
					situacaoNova = mat.getSituacaoMatricula();
				
				// situação atualmente presente no registro dessa matrícula no banco
				SituacaoMatricula situacaoOriginal = null;
				if (mat.getId() == 0) {
					situacaoOriginal = mat.getSituacaoMatricula();
				} else {
					situacaoOriginal = dao.findOtimizado(mat.getId()).getSituacaoMatricula();
				}

				// só valida se a situação tiver mudando de negativa pra positiva, ou vice-versa
				if (SituacaoMatricula.getSituacoesPositivas().contains(situacaoNova)
						&& (!SituacaoMatricula.getSituacoesPositivas().contains(situacaoOriginal) || situacaoNova.equals(situacaoOriginal))) {
					validarSituacoesPagasEMatriculadas(discente.getId(), mat, pagos, pagosEMatriculados, erros);
				} else if (SituacaoMatricula.getSituacoesNegativas().contains(situacaoNova)
						&& ( !SituacaoMatricula.getSituacoesNegativas().contains(situacaoOriginal)  || situacaoNova.equals(situacaoOriginal) ) ) {
					validarSituacoesExcluidas(discente.getId(), mat, pagosEMatriculados, erros);
				}
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Método utilitário usado em validações de matricula.
	 * @param idDiscente
	 * @param mat
	 * @param pagosEMatriculados
	 * @param erros
	 * @throws ArqException
	 */
	private static void validarSituacoesExcluidas(int idDiscente, MatriculaComponente mat, Collection<MatriculaComponente> pagosEMatriculados,
			ListaMensagens erros) throws ArqException {

		Collection<ComponenteCurricular> compPagosEMatExcetoAtual = new ArrayList<ComponenteCurricular>(0);
		for(MatriculaComponente m: pagosEMatriculados){
			if(m.getId() != mat.getId()){
				compPagosEMatExcetoAtual.add(m.getComponente());
			}
		}
		

		// Quando um componente é aproveitado não faz sentido verificar se ele faz as condições de pre-requisito.
		// Por isso é selecionada somente as disciplinas com situação APROVADO
		@SuppressWarnings("unchecked")
		Collection <MatriculaComponente> somenteAprovados = CollectionUtils.select(pagosEMatriculados, new Predicate(){
			public boolean evaluate(Object obj) {
				MatriculaComponente m = (MatriculaComponente) obj;
				if (m.getSituacaoMatricula().getId() == SituacaoMatricula.APROVADO.getId())
					return true;
				return false;
			}
		});		
		
		/**
		 * Se não tem co-requisito que precise ser marcado também
		 * Se não tem outros componentes tem ele como pré-requisito
		 */
		Collection<ComponenteCurricular> mats = new ArrayList<ComponenteCurricular>(0);
		mats.add(mat.getComponente());
		for (MatriculaComponente pag : somenteAprovados) {
			if (pag.getId() != mat.getId()) {
				ComponenteCurricular comp = pag.getComponente();
				/*validação suspensa pq o BD ainda possui dados inconsistentes que não permite essa validação
				 * if (comp.getCoRequisito() != null && ExpressaoUtil.evalComEquivalencias(comp.getCoRequisito(), mats)) {
					addMensagemErro("O componente " + mat.getComponenteDescricaoResumida() + " não pode ser cancelado ou excluído "
							+ "<Br>O discente possui componentes que o possuem como co-requisito", erros);
					return;
				}*/
				if (!StringUtils.isEmpty(comp.getPreRequisito()) && ExpressaoUtil.evalComTransitividade(comp.getPreRequisito(), idDiscente, mats) && !ExpressaoUtil.evalComTransitividade(comp.getPreRequisito(), idDiscente, compPagosEMatExcetoAtual)) {
					erros.addErro("O componente " + mat.getComponenteDescricaoResumida() + " não pode ser cancelado ou excluído "
							+ "<Br>O discente possui componentes que o possuem como pré-requisito");
					return;
				}
			}
		}

	}

	/**
	 * Método utilitário usado em validações de matricula.
	 * @param idDiscente
	 * @param mat
	 * @param pagos
	 * @param pagosEMatriculados
	 * @param erros
	 * @throws ArqException
	 */
	private static void validarSituacoesPagasEMatriculadas(int idDiscente, MatriculaComponente mat, Collection<MatriculaComponente> pagos,
			Collection<MatriculaComponente> pagosEMatriculados, ListaMensagens erros) throws ArqException {


		/**
		 * Se não já pagou (ou está matriculado) ele ou equivalente a ele se tem pré-requisito ou equivalente. Se tem (ou
		 * está matriculado)co-requisito ou equivalente.
		 */
		for (MatriculaComponente pag : pagosEMatriculados) {
			if (mat.getComponente().getId() == pag.getComponente().getId() && mat.getId() != pag.getId()
					&& getNumeroRepetidas(mat.getComponente(), pagosEMatriculados) + 1 > mat.getComponente().getQtdMaximaMatriculas() ) {
				erros.addErro("O componente " + mat.getComponenteDescricaoResumida() + " não pode ser pago ou matriculado."
						+ "<Br>O discente já pagou ou está matriculado no mesmo");
				return;
			}
		}
		Collection<ComponenteCurricular> componentesPagosMatriculados = new ArrayList<ComponenteCurricular>(0);
		for (MatriculaComponente pag : pagosEMatriculados) {
			if (pag.getId() != mat.getId())
				componentesPagosMatriculados.add(pag.getComponente());
		}
		Collection<ComponenteCurricular> componentesPagos = new ArrayList<ComponenteCurricular>(0);
		for (MatriculaComponente pag : pagos) {
			if (pag.getId() != mat.getId())
				componentesPagos.add(pag.getComponente());
		}

		if (!StringUtils.isEmpty(mat.getComponente().getEquivalencia()) && ExpressaoUtil.eval(mat.getComponente().getEquivalencia(), componentesPagosMatriculados)) {
			erros.addErro("O componente " + mat.getComponenteDescricaoResumida() + " não pode ser pago ou matriculado."
					+ "<Br>O discente já pagou ou está matriculado em componentes equivalentes");
		}
		// co-requisitos
		if (!StringUtils.isEmpty(mat.getComponente().getCoRequisito()) && !ExpressaoUtil.evalComTransitividade(mat.getComponente().getCoRequisito(), idDiscente, componentesPagosMatriculados)) {
			erros.addErro("O componente " + mat.getComponenteDescricaoResumida() + " não pode ser pago ou matriculado."
					+ "<Br>O discente não pagou ou não está matriculado nos co-requisitos necessários");
		}
		// pre-requisitos
		if (!StringUtils.isEmpty(mat.getComponente().getPreRequisito()) && !ExpressaoUtil.evalComTransitividade(mat.getComponente().getPreRequisito(), idDiscente, componentesPagos)) {
			erros.addErro("O componente " + mat.getComponenteDescricaoResumida() + " não pode ser pago ou matriculado."
					+ "<Br>O discente não pagou os pré-requisitos necessários");
		}

	}

	/**
	 * Método auxiliar que conta a quantidade de vezes que um componente curricular aparece numa lista de matrículas em componentes.
	 * Utilizado para saber quantas vezes um aluno já pagou uma determinada disciplina (Ensino Técnico).
	 * 
	 * @param disciplina
	 * @param matriculas
	 * @return
	 */
	private static int getNumeroRepetidas(ComponenteCurricular disciplina, Collection<MatriculaComponente> matriculas) {
		int qtd = 0;
		for(MatriculaComponente m: matriculas){
			if(m.getComponente().getId() == disciplina.getId())
				qtd++;
		}
		return qtd;
	}
	
	
	
	/**
	 * Insere no banco um registro de alteração de matrícula.
	 * @param matricula
	 * @param dao
	 * @param mov
	 * @param situacaoAntiga
	 * @throws DAOException
	 */
	public static void criarApenasRegistroAlteracaoMatricula(MatriculaComponente matricula, MatriculaComponenteDao dao, Movimento mov, 
			SituacaoMatricula situacaoAntiga) throws DAOException {
		
		AlteracaoMatricula alteracao = new AlteracaoMatricula();
		
		alteracao.setMatricula(matricula);
		alteracao.setSituacaoAntiga(situacaoAntiga);
		alteracao.setSituacaoNova(matricula.getSituacaoMatricula());
		alteracao.setDataAlteracao(new Date());
		alteracao.setUsuario((Usuario) mov.getUsuarioLogado());
		alteracao.setCodMovimento(mov.getCodMovimento().getId());
		
		dao.create(alteracao);
		
	}
	

}
