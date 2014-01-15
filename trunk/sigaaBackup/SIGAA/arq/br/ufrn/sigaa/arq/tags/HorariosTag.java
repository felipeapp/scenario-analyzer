/**
 *
 */
package br.ufrn.sigaa.arq.tags;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.Formatador;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * @author Andre M Dantas
 *
 */
public class HorariosTag extends TagSupport {

	private  int border = 1;

	private Collection<Turma> turmas;

	public int getBorder() {
		return border;
	}

	public void setBorder(int border) {
		this.border = border;
	}

	public Collection<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			String[][] horarios = criarTabelaHorario(turmas);
			if (horarios == null) return SKIP_BODY;

			JspWriter out = pageContext.getOut();

			out.println("<table class=\"horarios\" border=\""+border+"\">");
			out.println("<caption>Horários das Turmas</caption>");
			for (int i = 0; i < horarios.length; i++) {
				out.println("<tr>");
				for (int j = 0; j < 7; j++) {
					out.println("<td"+((j==0 || i==0)?" class=\"titulo\"":"")+">");
					out.print(horarios[i][j]);
					out.println("</td>");
				}
				out.println("</tr>");
			}
			out.println("</table>");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return SKIP_BODY;
	}

	private String[][] criarTabelaHorario(Collection<Turma> turmas) throws Exception {
		HttpSession session = pageContext.getSession();
		SubSistema sub = (SubSistema) session.getAttribute("subsistema");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		char nivelEnsino = SigaaSubsistemas.getNivelEnsino(sub);

		Formatador fmt = Formatador.getInstance();
		ArrayList<Horario> horarios = null;
		HorarioDao dao = new HorarioDao();
		horarios = (ArrayList<Horario>) dao.findAtivoByUnidade(usuario.getUnidade().getId(), nivelEnsino);
		dao.close();
		horarios.add(0, new Horario());
		if (horarios == null) return null;
		String[][] matrizHorario = new String[horarios.size()][7];

//		primeira linha
		matrizHorario[0][0] = "";
		matrizHorario[0][1] = "Segunda";
		matrizHorario[0][2] = "Terça";
		matrizHorario[0][3] = "Quarta";
		matrizHorario[0][4] = "Quinta";
		matrizHorario[0][5] = "Sexta";
		matrizHorario[0][6] = "Sábado";

		Horario[] horariosArr = (Horario[]) horarios.toArray(new Horario[0]);

		// preencher as linhas
		for (int lin = 0; lin < horariosArr.length; lin++) {
			// o elem. da 1a. linha da 1a. coluna é em branco
			String descHorario = "";
			if (lin > 0) {
				descHorario = fmt.formatarHora(horariosArr[lin].getInicio()) + " - ";
				descHorario += fmt.formatarHora(horariosArr[lin].getFim());
			}
			matrizHorario[lin][0] = descHorario;
			// se alguma turma tem esse horario, procurar em qual dia
			for (Turma turma : turmas) {
				String cod = turma.getDisciplina().getCodigo();
				for (HorarioTurma ht : turma.getHorarios()) {
					//TODO: CONSIDERAR NOVA ESTRUTURA DE HORARIO
					/*if ((ht.getInicio().getInicio().equals(horariosArr[lin].getInicio()) &&
							ht.getInicio().getFim().equals(horariosArr[lin].getFim())) ||
							(ht.getFim().getInicio().equals(horariosArr[lin].getInicio()) &&
							ht.getFim().getFim().equals(horariosArr[lin].getFim()))) {
						if (ht.isSegunda())
							matrizHorario[lin][1] = cod;
						if (ht.isTerca())
							matrizHorario[lin][2] = cod;
						if (ht.isQuarta())
							matrizHorario[lin][3] = cod;
						if (ht.isQuinta())
							matrizHorario[lin][4] = cod;
						if (ht.isSexta())
							matrizHorario[lin][5] = cod;
						if (ht.isSabado())
							matrizHorario[lin][6] = cod;
						break;
					}*/
				}
			}
		}
		boolean preenchido = false;
		for (int lin = 0; lin < horariosArr.length; lin++) {
			for (int col = 0; col < 7; col++) {
				if (matrizHorario[lin][col] == null)
					matrizHorario[lin][col] = "---";
				else if (lin > 0 &&	 col > 0)
					preenchido = true;
			}
		}
		if (!preenchido) return null;

		return matrizHorario;
	}

}
