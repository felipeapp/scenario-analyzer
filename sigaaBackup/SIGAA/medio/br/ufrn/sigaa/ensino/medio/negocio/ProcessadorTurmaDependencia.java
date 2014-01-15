/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 15/01/2013
 * Autor: Joab Galdino
 */
package br.ufrn.sigaa.ensino.medio.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.medio.dao.CurriculoMedioDao;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerieAno;

/**
 * Processador para cadastrar turmas de dependência de ensino médio.
 * Neste processador serão cadastradas as turmas virtuais das disciplinas pertencentes a uma turma de dependência,
 * assim como, o vinculo das disciplinas com a turma, esta nomeada por A, B, C, etc...
 * 
 * @author Joab Galdino
 *
 */
public class ProcessadorTurmaDependencia extends ProcessadorTurmaSerie{

	@Override
	public Object execute(Movimento movimento) throws NegocioException,	ArqException, RemoteException {
		MovimentoCadastrarTurmaDependencia mc = (MovimentoCadastrarTurmaDependencia) movimento;
		validate(movimento);
		
		/* Cadastrar / Alterar TurmaSerie */
		if (mc.getCodMovimento().equals(SigaaListaComando.CADASTRAR_TURMA_SERIE_DEPENDENCIA)) {
			return criarTurmaDependencia(mc);
		} else if (mc.getCodMovimento().equals(SigaaListaComando.ALTERAR_TURMA_SERIE_DEPENDENCIA)) {
			return alterarTurmaDependencia(mc);
		} 	
		return null;
	}
	
	@Override
	public void validate(Movimento mov){
		// TODO Auto-generated method stub
	}
	/**
	 * Método responsável pela persistência da TurmaSerie 
	 * @param mc
	 * @return
	 */
	private List<TurmaSerieAno> criarTurmaDependencia( MovimentoCadastrarTurmaDependencia mc ) throws ArqException, NegocioException, RemoteException {		
		List<TurmaSerieAno> listaDisciplinas = new ArrayList<TurmaSerieAno>(); 
		CurriculoMedioDao cmDao = getDAO(CurriculoMedioDao.class, mc);
		List<Integer> idsDisciplinasSelecionadas = mc.getdisciplinasSelecionadas();
		TurmaSerie ts = (TurmaSerie) mc.getObjMovimentado();
		try {
			Collection<ComponenteCurricular> disciplinas = cmDao.findByIds(idsDisciplinasSelecionadas, ts.getCurriculo());
			if ( disciplinas.isEmpty() ){ 
				throw new NegocioException("Não é possível cadastrar Turma com Estrutura Curricular, que não tenha Disciplinas vinculadas.");
			} else {
				criar(mc); 
				for (ComponenteCurricular disciplina : disciplinas) {		
							listaDisciplinas.add( criarTurmaDisciplina(disciplina, mc) );
				}
			}	
			return listaDisciplinas;
		} finally {
			cmDao.close();
		}
	}
	
	/**
	 * Método responsável pela alteração dos dados da TurmaSerie 
	 * @param mc
	 * @return
	 */
	private List<TurmaSerieAno> alterarTurmaDependencia( MovimentoCadastrarTurmaDependencia mc ) throws ArqException, NegocioException, RemoteException {
		List<TurmaSerieAno> listaDisciplinas = new ArrayList<TurmaSerieAno>();
		CurriculoMedioDao cmDao = getDAO(CurriculoMedioDao.class, mc);
		List<Integer> idsDisciplinasSelecionadas = mc.getdisciplinasSelecionadas();
		List<Integer> idsDisciplinasAdicionar = mc.getdisciplinasAdicionadas();		
		TurmaSerie ts = (TurmaSerie) mc.getObjMovimentado();
		Boolean alterarCurriculo = (Boolean) mc.getObjAuxiliar();
		try {
			ts.setDisciplinas(cmDao.findByExactField(TurmaSerieAno.class, "turmaSerie", ts.getId()));
			if( alterarCurriculo ){
				Collection<ComponenteCurricular> disciplinasCadastrar = cmDao.findByIds(idsDisciplinasAdicionar, ts.getCurriculo());
				if ( disciplinasCadastrar.isEmpty() ){ 
					throw new NegocioException("Não é possível Alterar Turma para uma Estrutura Curricular sem haver Disciplinas vinculadas.");
				}else{
					List<TurmaSerieAno> disciplinasExcluir = new ArrayList<TurmaSerieAno>();
					disciplinasExcluir.addAll( ts.getDisciplinas() );
					
					ts.setDisciplinas(new ArrayList<TurmaSerieAno>());
					for (TurmaSerieAno tsa : disciplinasExcluir) {
						if(!idsDisciplinasSelecionadas.contains(tsa.getTurma().getDisciplina().getId()))
							removerDisciplina(tsa.getTurma(), tsa, mc);
					}
					if(!idsDisciplinasAdicionar.isEmpty())
					for (ComponenteCurricular disciplina : disciplinasCadastrar) {						
						listaDisciplinas.add( criarTurmaDisciplina(disciplina, mc) );
					}
				}
			}
		} finally {
			cmDao.close();
		}
			
		// Alteração dos dados da disciplinas, conforme alteração da TurmaSerie.
		for (TurmaSerieAno tsa : ts.getDisciplinas()) {
			alterarDisciplina(tsa.getTurma(), ts, mc);
		}
		
		alterar(mc);
		
		return listaDisciplinas;
	}
	
}
