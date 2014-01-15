/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/10/22
 */
package br.ufrn.sigaa.ead.jsf;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.faces.context.FacesContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * Managed bean para exportação de dados dos alunos de ensino a distância
 * do SIGAA para o Moodle.
 * @author David Pereira
 *
 */
@Component("exportarAlunosEad") @Scope("request")
public class ListaAlunosCSVMBean extends SigaaAbstractController<DiscenteGraduacao> {

	private Polo polo = new Polo();
	
	private Curso curso = new Curso();

	/**
	 * Gera uma lista de discentes com seus respectivos e-mails e envia como
	 * arquivo para usuário fazer download.
	 * 
	 * @return
	 * @throws IOException
	 * @throws DAOException
	 */
	public String gerar() throws IOException, DAOException {
		CalendarioAcademico calendario = getCalendarioVigente();
		
		DiscenteDao dao = getDAO(DiscenteDao.class);
		List<DiscenteGraduacao> discentes = dao.findDadosExportacaoAlunosEad(polo, curso, calendario.getAno(), calendario.getPeriodo());
		
		PrintWriter out = getCurrentResponse().getWriter();
		
		if (discentes != null && !discentes.isEmpty()) {
			for (DiscenteGraduacao dg : discentes) {
				int indiceEspaco = dg.getNome().indexOf(" ");
				
				String primeiroNome = dg.getNome().substring(0, indiceEspaco).trim();
				String ultimoNome = dg.getNome().substring(indiceEspaco).trim();
				String email = dg.getPessoa().getEmail();
				
				if (email == null) email = dg.getMatricula() + "@sedis.ufrn.br";
				
				StringBuilder sb = new StringBuilder(dg.getMatricula() + "," + dg.getMatricula() + "," + primeiroNome + "," + ultimoNome + "," + email + "," + StringUtils.toAscii(dg.getPolo().getCidade().getNome()));
				for (MatriculaComponente mc : dg.getMatriculasDisciplina())
					sb.append("," + mc.getComponenteCodigo());
				out.println(sb.toString());
			}
		}

		getCurrentResponse().setContentType( "application/octet-stream" );
		getCurrentResponse().addHeader("Content-Disposition","attachment; filename=\"alunos.txt\"");
		FacesContext.getCurrentInstance().responseComplete();
		return null;
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
	
}
