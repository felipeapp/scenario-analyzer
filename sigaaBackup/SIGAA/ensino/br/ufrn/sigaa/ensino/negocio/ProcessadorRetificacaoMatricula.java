/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.DefaultExtensionPointFactory;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ParametrosGestoraAcademicaDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.RetificacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.DiscenteCalculosHelper;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacao;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacaoFactory;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Retifica matrícula de componentes podendo alterar media final e/ou faltas
 *
 * @author Andre M Dantas
 *
 */
public class ProcessadorRetificacaoMatricula extends ProcessadorCadastro {

	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		MatriculaComponenteDao mcdao = getDAO(MatriculaComponenteDao.class, movimento);
		ParametrosGestoraAcademicaDao dao = getDAO(ParametrosGestoraAcademicaDao.class, movimento);
		
		try {
			validate(movimento);
			MatriculaComponente matriculaAlterada = (MatriculaComponente) ((MovimentoCadastro) movimento).getObjMovimentado();
			dao.detach(matriculaAlterada);
			MatriculaComponente matriculaOriginal = dao.findByPrimaryKey(matriculaAlterada.getId(), MatriculaComponente.class);

			// cria o registro que a matrícula foi retificada
			RetificacaoMatricula retificacao = new RetificacaoMatricula(matriculaOriginal);
			retificacao.setRegistroEntrada(movimento.getUsuarioLogado().getRegistroEntrada());
			retificacao.setData(new Date());
			dao.create(retificacao);

			matriculaOriginal.setFoiRetificada(true);
			matriculaOriginal.setValoresRetificacao(matriculaAlterada);

			//Discente discente = matriculaOriginal.getDiscente();
			Discente discente = dao.findByPrimaryKey(matriculaOriginal.getDiscente().getId(), Discente.class);

			// Só consolida se a retificação NÃO for de um APROVEITAMENTO, CANCELADO OU EXCLUIDO
			if( !matriculaOriginal.isAproveitadoDispensado() && !matriculaOriginal.getSituacaoMatricula().equals(SituacaoMatricula.EXCLUIDA) && !matriculaOriginal.getSituacaoMatricula().equals(SituacaoMatricula.CANCELADO) ){
				//ParametrosGestoraAcademica params = dao.findByUnidade(discente.getCurso().getUnidade().getGestoraAcademica().getId(), discente.getNivel());
				ParametrosGestoraAcademica params = ParametrosGestoraAcademicaHelper.getParametros(discente);
				if (matriculaOriginal.getTurma() != null) {
					//Caso for Graduação e turma de EAD, recarrega o Polo e Curso para evitar LazyInitializationException...
					if (matriculaOriginal.getTurma().getDisciplina().isGraduacao()){
						if (matriculaOriginal.getTurma().getIdPolo() != null)
							matriculaOriginal.getTurma().setPolo( dao.findByPrimaryKey(matriculaOriginal.getTurma().getIdPolo(), Polo.class) );
						if (matriculaOriginal.getTurma().isEad()){
							Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();
							matriculaOriginal.getDiscente().setCurso( dao.refresh( matriculaOriginal.getDiscente().getCurso() ));
							matriculas.add(matriculaOriginal);
							matriculaOriginal.getTurma().setMatriculasDisciplina(matriculas);					
						}
					}			
				}
				
				EstrategiaConsolidacaoFactory factory = (EstrategiaConsolidacaoFactory) DefaultExtensionPointFactory.getImplementation(ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY);
				EstrategiaConsolidacao estrategia = null;
				
				if (matriculaOriginal.getDiscente().getCurso() != null) {
					Curso curso = dao.findByPrimaryKey(matriculaOriginal.getDiscente().getCurso().getId(), Curso.class);
					estrategia = factory.getEstrategia(matriculaOriginal.getDiscente(), params);
				} else {
					estrategia = factory.getEstrategia(matriculaOriginal.getTurma(), params);
				}
				
				matriculaOriginal.setMetodoAvaliacao(params.getMetodoAvaliacao());
				matriculaOriginal.setEstrategia(estrategia);
				matriculaOriginal.consolidar();
			}
			dao.update(matriculaOriginal);

			MatriculaComponenteHelper.alterarSituacaoMatricula(matriculaOriginal, matriculaOriginal.getSituacaoMatricula(), movimento, mcdao);
			
			// Se a disciplina atual fizer parte de um bloco, processar o bloco completamente
			if (matriculaOriginal.getTurma() != null && matriculaOriginal.getTurma().getDisciplina().getBlocoSubUnidade() != null) {
				
				retificarBloco(movimento, mcdao, matriculaOriginal);
				
				ProcessadorConsolidacaoTurma procTurma = new ProcessadorConsolidacaoTurma();
				procTurma.processarMatriculasBloco(movimento, matriculaOriginal);

			}
			
			if (discente.getNivel() == NivelEnsino.GRADUACAO) {
				DiscenteGraduacao dg = mcdao.findByPrimaryKey(discente.getId(), DiscenteGraduacao.class);
				if (dg.getStatus() == StatusDiscente.CONCLUIDO)
					DiscenteCalculosHelper.atualizarIRA(dg, movimento);
				else
					DiscenteCalculosHelper.atualizarTodosCalculosDiscente(dg, movimento);
			}

		} finally {
			if (dao != null)
				dao.close();
			if (mcdao != null)
				mcdao.close();
		}
		
		return movimento;
	}

	/**
	 * Retifica e reconsolida o bloco
	 * 
	 * @param movimento
	 * @param mcdao
	 * @param matriculaDisciplina
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws DAOException
	 */
	private void retificarBloco(Movimento movimento, MatriculaComponenteDao mcdao, MatriculaComponente matriculaDisciplina) throws ArqException, NegocioException, DAOException {
		
		ComponenteCurricular bloco = mcdao.findByPrimaryKey( matriculaDisciplina.getTurma().getDisciplina().getBlocoSubUnidade().getId(), ComponenteCurricular.class);
		MatriculaComponente matriculaNoBloco = mcdao.findMatriculaByComponenteDiscenteAnoPeriodo(bloco, matriculaDisciplina.getDiscente().getDiscente(), matriculaDisciplina.getTurma().getAno(), matriculaDisciplina.getTurma().getPeriodo());
		
		if (matriculaNoBloco != null) {
			// cria o registro que a matrícula foi retificada
			RetificacaoMatricula retificacao = new RetificacaoMatricula(matriculaNoBloco);
			retificacao.setRegistroEntrada(movimento.getUsuarioLogado().getRegistroEntrada());
			retificacao.setData(new Date());
			mcdao.create(retificacao);		
		}
		
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(new int[] {SigaaPapeis.ADMINISTRADOR_DAE,SigaaPapeis.DAE, 
				SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR, 
				SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO,
				SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG,
				SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO}, mov);

		GenericDAO dao = getGenericDAO(mov);

		MatriculaComponente matriculaAlterada = (MatriculaComponente) ((MovimentoCadastro) mov).getObjMovimentado();
		MatriculaComponente matriculaOriginal = dao.findByPrimaryKey(matriculaAlterada.getId(), MatriculaComponente.class);

		if( matriculaOriginal.isAproveitadoDispensado() && ( !matriculaAlterada.isAproveitadoDispensado() && !matriculaAlterada.getSituacaoMatricula().equals(SituacaoMatricula.EXCLUIDA) && !matriculaAlterada.getSituacaoMatricula().equals(SituacaoMatricula.CANCELADO) ) ){
			throw new NegocioException("Não é possível realizar a retificação desta matrícula pois esta matrícula foi aproveitada e a " +
					"nova situação desta matrícula também deve ser de APROVEITADA, EXCLUIDA ou CANCELADA.");
		}

		if( !matriculaOriginal.isAproveitadoDispensado() && matriculaAlterada.isAproveitadoDispensado() ){
			throw new NegocioException("Não é possível realizar a retificação desta matrícula pois a nova situação desta matrícula não pode ser aproveitada.");
		}

	}

}
