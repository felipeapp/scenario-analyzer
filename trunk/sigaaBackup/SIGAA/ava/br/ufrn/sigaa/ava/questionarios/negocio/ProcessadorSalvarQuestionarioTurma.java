/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/01/2011
 *
 */
package br.ufrn.sigaa.ava.questionarios.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.dominio.MaterialTurma;
import br.ufrn.sigaa.ava.dominio.TipoMaterialTurma;
import br.ufrn.sigaa.ava.negocio.AvaliacaoHelper;
import br.ufrn.sigaa.ava.negocio.MaterialTurmaHelper;
import br.ufrn.sigaa.ava.negocio.MovimentoCadastroAva;
import br.ufrn.sigaa.ava.questionario.dao.QuestionarioTurmaDao;
import br.ufrn.sigaa.ava.questionarios.dominio.ConjuntoRespostasQuestionarioAluno;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Processador que salva um questionário da turma virtual.
 * 
 * @author Fred_Castro
 *
 */
public class ProcessadorSalvarQuestionarioTurma extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
	
		if (mov.getCodMovimento().equals(SigaaListaComando.SALVAR_QUESTIONARIO_TURMA)) {
			return salvarQuestionario(mov);
		} else {
			return removerQuestionario(mov);
		}
	}
	
	public Object salvarQuestionario(Movimento mov) throws NegocioException, ArqException  {
		
		validate (mov);

		MovimentoCadastroAva pMov = (MovimentoCadastroAva) mov;
		
		QuestionarioTurma questionario = pMov.getObjMovimentado();
		Turma turma = questionario.getTurma();
		
		TurmaDao dao = null;
		AvaliacaoDao aDao = null;
		
		try {
			dao = getDAO(TurmaDao.class, pMov);
			aDao = getDAO(AvaliacaoDao.class, pMov);
			
			// Se está criando o questionário
			if (questionario.getId() == 0){
				
				if (questionario.getMaterial() == null) {
					questionario.setMaterial(new MaterialTurma());
					questionario.getMaterial().setTipoMaterial(TipoMaterialTurma.QUESTIONARIO);
				}						
				MaterialTurmaHelper.definirNovoMaterialParaTopico(questionario, questionario.getAula(), questionario.getTurma());
				dao.create(questionario);
				MaterialTurmaHelper.atualizarMaterial(dao, questionario,true);
				
				// Se a tarefa tiver nota, cadastrar avaliações para cada discente
				// para realizar integração com a consolidação
				if (questionario.isPossuiNota()) {
					
					Collection<NotaUnidade> notas = null;
					
					if (!turma.isAgrupadora())
						notas = dao.findNotasByTurmaUnidade(turma.getId(), questionario.getUnidade());
					else
						notas = dao.findNotasByTurmaAgrupadoraUnidade(questionario.getId(), questionario.getUnidade());
						
					if (!isEmpty(notas)) {
						for (NotaUnidade nota : notas) {
							Avaliacao avaliacao = AvaliacaoHelper.criarAvaliacao(questionario);
							avaliacao.setUnidade(nota);
							dao.create(avaliacao);
						}
					}
				}
			
			// Está alterando o questionário.
			} else {
				
				QuestionarioTurma questionarioAnterior = dao.findByPrimaryKey(questionario.getId(), QuestionarioTurma.class);
				if ( questionarioAnterior != null ) {
					if (!questionarioAnterior.getMediaNotas().equals(questionario.getMediaNotas()) )
						recalcularNotasConjuntos(pMov);
					dao.detach(questionarioAnterior);
				}
				
				questionario.getMaterial().setTopicoAula(dao.findByPrimaryKey(questionario.getId(), QuestionarioTurma.class, new String [] { "aula.id"}).getAula());
				dao.update(questionario);
				MaterialTurmaHelper.atualizarMaterial(dao, questionario, false);
				
				// Se a tarefa for alterada, é nescessário verificar as avaliações.
				ArrayList<Avaliacao> avaliacoes = (ArrayList<Avaliacao>) aDao.findAvaliacoesByQuestionario (questionario.getId());
				Collection<NotaUnidade> notas = null;
				
				// Caso esteja sendo alterados campos comuns a avaliação.
				if ( avaliacoes != null && !avaliacoes.isEmpty() ) {
					
					boolean modificouAbrev = !avaliacoes.get(0).getAbreviacao().equals(questionario.getAbreviacao());
					boolean modificouDenominacao = !avaliacoes.get(0).getDenominacao().equals(questionario.getTitulo());
					boolean modificouNotaMaxima = questionario.getNotaMaxima() == null || avaliacoes.get(0).getNotaMaxima() != questionario.getNotaMaxima()/10;
					boolean modificouUnidade = avaliacoes.get(0).getUnidade().getUnidade() != questionario.getUnidade();
					boolean modificouPeso = avaliacoes.get(0).getPeso() != questionario.getPeso();
					
					// Caso a unidade seja modificada esse mapa irá conter a avaliação e a nova NotaUnidade referente a avaliação
					HashMap<Integer,NotaUnidade> map = null;
					
					// Popula o mapa com as novas unidades de cada avaliação
					if ( modificouUnidade ) {
						if (!turma.isAgrupadora())
							notas = dao.findNotasByTurmaUnidade(turma.getId(), questionario.getUnidade());
						else
							notas = dao.findNotasByTurmaAgrupadoraUnidade(turma.getId(), questionario.getUnidade());
						
						map = new HashMap<Integer,NotaUnidade>();
						
						for ( Avaliacao a : avaliacoes ) {
							for ( NotaUnidade n : notas ) {	
								if ( a.getUnidade().getMatricula().getId() == n.getMatricula().getId() )
									map.put(a.getId(), n);
							}
						}	
					}				
					
					// Caso a tarefa seja alterada para não ter nota, as avaliações serão removidas
					if ( !questionario.isPossuiNota() )
						for ( Avaliacao a : avaliacoes )
								dao.remove(a);
					else if ( modificouAbrev || modificouDenominacao || modificouPeso || modificouNotaMaxima || modificouUnidade ) {
						for ( Avaliacao a : avaliacoes ) {
							a.setDenominacao(questionario.getTitulo());
							a.setAbreviacao(questionario.getAbreviacao());
							a.setPeso(questionario.getPeso());
							a.setNotaMaxima(questionario.getNotaMaxima());
							if ( map != null )
								a.setUnidade(map.get(a.getId()));
							dao.updateFields(Avaliacao.class, a.getId(), 
									new String [] { "denominacao","abreviacao","peso","notaMaxima","unidade" },
									new Object [] { a.getDenominacao(),a.getAbreviacao(),a.getPeso(),a.getNotaMaxima(),a.getUnidade() });
						}	
					}
				}
					 
				// Caso o usuário esteja colocando notas na tarefa avaliações serão criadas.
				if (questionario.isPossuiNota()) {
		
					// Se a tarefa não tem avaliações cria-se avaliações.
					if ( avaliacoes == null || avaliacoes.size() == 0) {
						
						if (!turma.isAgrupadora())
							notas = dao.findNotasByTurmaUnidade(turma.getId(), questionario.getUnidade());
						else
							notas = dao.findNotasByTurmaAgrupadoraUnidade(turma.getId(), questionario.getUnidade());
							
						if (!isEmpty(notas)) {
							for (NotaUnidade nota : notas) {
								Avaliacao avaliacao = AvaliacaoHelper.criarAvaliacao(questionario);
								avaliacao.setUnidade(nota);
								dao.create(avaliacao);
							}
						}
					}
				}
			}
		} finally {
			if (dao != null)
				dao.close();
			
			if (aDao != null)
				aDao.close();
		}
		
		return null;
	}

	public Object removerQuestionario(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastroAva cMov = (MovimentoCadastroAva) mov;
		TurmaDao dao = null;
		AvaliacaoDao aDao = null;
		
		try {
			dao = getDAO(TurmaDao.class, mov);
			aDao = getDAO(AvaliacaoDao.class, mov);
			
			cMov.setMensagem("Questionário Removido");

			QuestionarioTurma questionario = cMov.getObjMovimentado();
			
			if (questionario.isPossuiNota() ) {
				
				ArrayList<Avaliacao> avaliacoes = (ArrayList<Avaliacao>) aDao.findAvaliacaoByAtividade(questionario.getId());	
							
				if ( avaliacoes != null ){
					boolean possuiMaisDeUmaAvaliacao =  aDao.isMaisDeUmaAvaliacao(avaliacoes.get(0).getId() , avaliacoes.get(0).getUnidade().getId());
					
					for ( Avaliacao a : avaliacoes ) {
						if ( !possuiMaisDeUmaAvaliacao && a.getUnidade().getMatricula().isMatriculado() )
							dao.updateField(NotaUnidade.class, a.getUnidade().getId(), "nota", "null");
						dao.remove(a);
					}
				}
			}

			dao.updateField(QuestionarioTurma.class, questionario.getId(), "ativo", false);
			dao.updateField(MaterialTurma.class, questionario.getMaterial().getId(), "ativo", false);
			MaterialTurmaHelper.reOrdenarMateriais(questionario.getAula());

			return new Notification();
		}finally {
			if (dao != null)
				dao.close();
			if (aDao != null)
				aDao.close();
		}		
	}
	
	private void recalcularNotasConjuntos ( MovimentoCadastroAva mov ) throws DAOException {		
		QuestionarioTurmaDao qtDAO = null;
		AvaliacaoDao aDAO = null;
		
		try {
			
			qtDAO = getDAO(QuestionarioTurmaDao.class, mov);
			aDAO = getDAO(AvaliacaoDao.class, mov);
			
			QuestionarioTurma questionario = mov.getObjMovimentado();
	
			List<ConjuntoRespostasQuestionarioAluno> conjuntos = qtDAO.findConjuntoRespostasByQuestionario(questionario);

			for ( ConjuntoRespostasQuestionarioAluno c : conjuntos ){
				c.setQuestionario(questionario);
				c.calcularNotas();
				qtDAO.updateField(ConjuntoRespostasQuestionarioAluno.class, c.getId(), "porcentagem", c.getPorcentagem());				
			}	
			
			// Desmarca o questionário para o docente saber que tem que publicar as notas novamente.
			((QuestionarioTurma) mov.getObjMovimentado()).setNotasPublicadas(false);
			
		} finally {
			if ( qtDAO != null )
				qtDAO.close();
			if ( aDAO != null )
				aDAO.close();
		}
	}
	
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro pMov = (MovimentoCadastro) mov;
		
		QuestionarioTurma questionario = (QuestionarioTurma) pMov.getObjMovimentado();
		
		if (questionario.isPossuiNota() && questionario.getUnidade() < 1)
			throw new NegocioException ("Já que este questionário valerá nota, por favor, selecione uma unidade.");
			
	}

}
