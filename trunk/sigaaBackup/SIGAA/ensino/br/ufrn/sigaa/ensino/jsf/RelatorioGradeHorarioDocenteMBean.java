/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 19/06/2007
 *
 */

package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * MBean para gerar relatório de grade de horários dos docentes
 * @author leonardo
 *
 */
@Component("gradeDocente")
@Scope("request")
//Suppress necessário por que nesse caso não há forma de parametrizar a superclasse
@SuppressWarnings("unchecked")
public class RelatorioGradeHorarioDocenteMBean extends SigaaAbstractController {

	private final String JSP_GRADE_DOCENTE = "/ensino/relatorios/grade_docente.jsp";

	private Servidor docente;

	private Collection<Turma> turmas;

	private Collection<Horario> horarios;

	private Collection<HorarioTurma> horariosTurma;

	public RelatorioGradeHorarioDocenteMBean() throws SegurancaException {
		checkDocenteRole();
	}

	/**
	 * Gerar grade de horários do docentes
	 * JSP: 
	 * /sigaa.war/portais/docente/docente.jsp
	 * /sigaa.war/portais/docente/menu_docente.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String gerarGrade() throws ArqException {
		if(isEmpty(getTurmas())){
			addMensagemErro("O docente não possui turmas.");
			return null;
		}
		
		horariosTurma = new ArrayList<HorarioTurma>();

		//Percorre todas as turmas abertas do docente e adiciona os horários dessas turmas na coleção de HorarioTurma.
		for(Turma t: getTurmas()){
			horariosTurma.addAll(t.getHorarios());			
		}
		
		Date hoje = new Date();
		int mesAtual = CalendarUtils.getMesByData(hoje);
		
		//Se o HorarioTurma for de uma turma de um componente curricular que pemita horários flexéveis e a 
		//data atual não está dentro do peréodo definido pelas datas inicio e fim do HorarioTurma, removemos o HorarioTurma.
		for (Iterator<HorarioTurma> iterator = horariosTurma.iterator(); iterator.hasNext();) {
			HorarioTurma ht = iterator.next();
			if (ht.getTurma().getDisciplina().isPermiteHorarioFlexivel()) {
				boolean isDentroPeriodo = CalendarUtils.getMesByData(ht.getDataInicio()) <= mesAtual && CalendarUtils.getMesByData(ht.getDataFim()) >= mesAtual;
				if (!isDentroPeriodo)
					iterator.remove();
			}			
		}	
		
		// monta a lista de horários a partir dos horários da turma
		horarios = new TreeSet<Horario>();
		for (HorarioTurma ht : horariosTurma) {
			horarios.add(ht.getHorario());
		}

		return forward(JSP_GRADE_DOCENTE);
	}
	
	/**
	 * Retorna o ano-semestre atual
	 * @return
	 * @throws DAOException
	 */
	public String getAnoSemestre() throws DAOException {
		CalendarioAcademico calendario = getCalendarioVigente();
		if ( calendario == null) 
			return "";
		
		return calendario.getAnoPeriodo();
	}

	public Servidor getDocente() {
		return docente;
	}

	public void setDocente(Servidor docente) {
		this.docente = docente;
	}
	
	public Collection<Character> getNiveisEnsinoTurmas() throws DAOException, SegurancaException {
		Collection<Character> niveis = new ArrayList<Character>();
		for (Turma turma : getTurmas()) {
			if (!niveis.contains(turma.getDisciplina().getNivel()))
				niveis.add(turma.getDisciplina().getNivel());
		}
		return niveis;
	}

	public Collection<Turma> getTurmas() throws DAOException, SegurancaException {
		if (turmas == null) {
			TurmaDao turmaDao = getDAO(TurmaDao.class);
			if( getAcessoMenu().isDocenteUFRN() ){
				docente = getServidorUsuario();
				turmas = turmaDao.findByDocente(docente, null, SituacaoTurma.ABERTA, null, false, false);
			}else if ( getAcessoMenu().isDocente() ){
				turmas = turmaDao.findByDocenteExternoOtimizado( getUsuarioLogado().getDocenteExterno() , SituacaoTurma.ABERTA, true);
			} else {
				throw new SegurancaException("Somente usuários acessando o sistema como docentes podem realizar esta operação!");
			}
			// ordena a lista por nível de ensino.
			Comparator comparator = new Comparator<Turma>() {
				@Override
				public int compare(Turma obj1, Turma obj2) {
					Integer n1 = NivelEnsino.tabela.get(obj1.getDisciplina().getNivel());
					Integer n2 = NivelEnsino.tabela.get(obj2.getDisciplina().getNivel());
					int cmp = 0;
					if (n1 != null && n2 != null)
						cmp = n1 - n2;
					if (cmp == 0)
						cmp = obj1.getDisciplina().compareTo(obj2.getDisciplina());
					if (cmp == 0)
						cmp = obj1.getCodigo().compareTo(obj2.getCodigo());
					return cmp;
				}
			};
			Collections.sort((List) turmas, comparator);
		}
		return turmas;
	}

	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}

	public Collection<Horario> getHorarios() {
		return horarios;
	}

	public void setHorarios(Collection<Horario> horarios) {
		this.horarios = horarios;
	}

	public Collection<HorarioTurma> getHorariosTurma() {
		return horariosTurma;
	}

	public void setHorariosTurma(List<HorarioTurma> horariosTurma) {
		this.horariosTurma = horariosTurma;
	}
	
	public Collection<Character> getNiveisEnsino() {
		if (horariosTurma != null) {
			Collection<Character> niveis = new ArrayList<Character>();
			for (HorarioTurma horario : horariosTurma) {
				if (!niveis.contains(horario.getHorario().getNivel()))
					niveis.add(horario.getHorario().getNivel());
			}
			return niveis;
		} else return null;
	}
}
