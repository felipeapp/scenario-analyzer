/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/09/27
 */
package br.ufrn.sigaa.ead.jsf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.ead.PoloDao;
import br.ufrn.sigaa.arq.dao.ead.TutorOrientadorDao;
import br.ufrn.sigaa.arq.dao.ead.TutoriaAlunoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.CoordenacaoPolo;
import br.ufrn.sigaa.ead.dominio.HorarioTutor;
import br.ufrn.sigaa.ead.dominio.HorarioTutoria;
import br.ufrn.sigaa.ead.dominio.Polo;

/**
 * Managed bean para gerar o relatório de horários dos tutores e discentes
 * de ensino a distancia
 * @author David Pereira
 *
 */
@SuppressWarnings("unchecked")
@Component("relatorioHorario") @Scope("request")
public class RelatorioHorarioTutorDiscenteMBean extends SigaaAbstractController {

	private Polo polo = new Polo();
	
	private List<SelectItem> polos;
	
	private Curso curso = new Curso();
	
	private Integer dia;
	
	private Character turno;
	
	private boolean tutores;
	
	private boolean discentes;
	
	private Map<String, List<HorarioTutor>> horariosTutores;
	
	private Map<String, List<HorarioTutoria>> horariosDiscentes;
	
	/**
	 * Preenche os horários dos tutores
	 */
	public RelatorioHorarioTutorDiscenteMBean() {
		horariosTutores = new HashMap<String, List<HorarioTutor>>();
		horariosTutores.put(String.valueOf(Calendar.MONDAY), new ArrayList<HorarioTutor>());
		horariosTutores.put(String.valueOf(Calendar.TUESDAY), new ArrayList<HorarioTutor>());
		horariosTutores.put(String.valueOf(Calendar.WEDNESDAY), new ArrayList<HorarioTutor>());
		horariosTutores.put(String.valueOf(Calendar.THURSDAY), new ArrayList<HorarioTutor>());
		horariosTutores.put(String.valueOf(Calendar.FRIDAY), new ArrayList<HorarioTutor>());
		horariosTutores.put(String.valueOf(Calendar.SATURDAY), new ArrayList<HorarioTutor>());
		
		horariosDiscentes = new HashMap<String, List<HorarioTutoria>>();
		horariosDiscentes.put(String.valueOf(Calendar.MONDAY), new ArrayList<HorarioTutoria>());
		horariosDiscentes.put(String.valueOf(Calendar.TUESDAY), new ArrayList<HorarioTutoria>());
		horariosDiscentes.put(String.valueOf(Calendar.WEDNESDAY), new ArrayList<HorarioTutoria>());
		horariosDiscentes.put(String.valueOf(Calendar.THURSDAY), new ArrayList<HorarioTutoria>());
		horariosDiscentes.put(String.valueOf(Calendar.FRIDAY), new ArrayList<HorarioTutoria>());
		horariosDiscentes.put(String.valueOf(Calendar.SATURDAY), new ArrayList<HorarioTutoria>());
	}
	
	/**
	 * Retorna uma lista de pólos
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getPolos() throws DAOException {
		if (polos == null) {
			PoloDao dao = getDAO(PoloDao.class);
			polos = toSelectItems(dao.findAllPolos(), "id", "cidade.nomeUF");
		}
		
		CoordenacaoPolo cp = getUsuarioLogado().getVinculoAtivo().getCoordenacaoPolo();
		if (cp != null && cp.getPolo() != null) {
			polo = cp.getPolo();
		}
		
		return polos;
	}

	public void setPolos(List<SelectItem> polos) {
		this.polos = polos;
	}
	
	/**
	 * Gera  um relatório com os tutores e seus respectivos horários 
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerar() throws DAOException {
		CoordenacaoPolo cp = getUsuarioLogado().getVinculoAtivo().getCoordenacaoPolo();
		if (cp != null && cp.getPolo() != null) {
			polo = cp.getPolo();
		}
		
		if (validate()) return null;
		
		TutorOrientadorDao tDao = getDAO(TutorOrientadorDao.class);
		TutoriaAlunoDao aDao = getDAO(TutoriaAlunoDao.class);
		
		List<HorarioTutor> horarioTutor = tDao.findHorarios(dia, turno, polo, curso);
		List<HorarioTutoria> horarioTutoria = aDao.findHorarios(dia, turno, polo, curso);
		
		if (tutores && horarioTutor != null) {
			for (HorarioTutor h : horarioTutor) {
				horariosTutores.get(String.valueOf(h.getDia())).add(h);
			}
		}
		
		if (discentes && horarioTutoria != null) {
			for (HorarioTutoria h : horarioTutoria) {
				if (horariosDiscentes.get(String.valueOf(h.getDiaSemana())) != null)
					horariosDiscentes.get(String.valueOf(h.getDiaSemana())).add(h);
			}
		}
		
		return forward("/ead/relatorios/relatorio_horario_list.jsp");
	}

	public Polo getPolo() {
		return polo;
	}

	public void setPolo(Polo polo) {
		this.polo = polo;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public boolean isTutores() {
		return tutores;
	}

	public void setTutores(boolean tutores) {
		this.tutores = tutores;
	}

	public boolean isDiscentes() {
		return discentes;
	}

	public void setDiscentes(boolean discentes) {
		this.discentes = discentes;
	}

	public Integer getDia() {
		return dia;
	}

	public void setDia(Integer dia) {
		this.dia = dia;
	}

	public Character getTurno() {
		return turno;
	}

	public void setTurno(Character turno) {
		this.turno = turno;
	}

	public Map<String, List<HorarioTutor>> getHorariosTutores() {
		return horariosTutores;
	}

	public void setHorariosTutores(Map<String, List<HorarioTutor>> horariosTutores) {
		this.horariosTutores = horariosTutores;
	}

	public Map<String, List<HorarioTutoria>> getHorariosDiscentes() {
		return horariosDiscentes;
	}

	public void setHorariosDiscentes(Map<String, List<HorarioTutoria>> horariosDiscentes) {
		this.horariosDiscentes = horariosDiscentes;
	}
	
	/**
	 * Valida o objeto de domínio
	 * @return
	 */
	private boolean validate() {
		boolean erro = false;
		
		if (!tutores && !discentes) {
			addMensagemErro("Informe se o relatório deve ser gerado para tutores, discentes ou ambos.");
			erro = true;
		}
		
		if (curso == null || curso.getId() == 0) {
			addMensagemErro("Informe o curso para o qual deve ser gerado o relatório.");
			erro = true;
		}
		
		if (polo == null || polo.getId() == 0) {
			addMensagemErro("Informe pólo para o qual deve ser gerado o relatório.");
			erro = true;
		}
		
		return erro;
	}
	
}
