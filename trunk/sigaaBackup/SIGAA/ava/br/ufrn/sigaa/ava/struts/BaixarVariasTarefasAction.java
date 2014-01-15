/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.struts;

import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.GrupoDiscentes;
import br.ufrn.sigaa.ava.dominio.RespostaTarefaTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Action para que o docente possa baixar os arquivos de tarefas de vários alunos
 * em uma turma virtual
 *
 * @author David Pereira
 *
 */
public class BaixarVariasTarefasAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		int idTarefa = Integer.parseInt(req.getParameter("idTarefa"));
		int idTurma = Integer.parseInt(req.getParameter("idTurma"));
		
		TurmaVirtualDao dao = getDAO(TurmaVirtualDao.class, req);
		MatriculaComponenteDao mDao = getDAO(MatriculaComponenteDao.class, req);
		DiscenteDao dDao = getDAO(DiscenteDao.class, req);
				
		try {
	
			List<RespostaTarefaTurma> respostas = dao.findRespostasByTarefa(idTarefa);
			Turma turma = dao.findByPrimaryKey(idTurma, Turma.class);
			
			if (respostas != null && !respostas.isEmpty()) 
			{
				res.setContentType("application/octet-stream");
				res.setHeader("Content-disposition", "attachment; filename=\"tarefas.zip\"");
				
				ZipOutputStream zip = new ZipOutputStream(res.getOutputStream());
				String nomePasta = "";
		
				if ( turma.isAgrupadora() )
				{
					List<Turma> subturmas = turma.getSubturmas();
					
					for ( Turma subturma : subturmas )
					{
						nomePasta = StringUtils.toAscii(subturma.getNome()) + "/";
						Map <Integer, Long> matriculas = mDao.findMatriculasByTurma(subturma.getId());
						organizarTarefas(nomePasta,respostas,matriculas,zip,dDao,dao,subturma);
					}
				}
				else
				{
					Map <Integer, Long> matriculas = mDao.findMatriculasByTurma(idTurma);
					organizarTarefas(nomePasta,respostas,matriculas,zip,dDao,dao,turma);
				}
				
				zip.flush();
				zip.close();
			}
			else
				res.sendRedirect("/sigaa/ava/TarefaTurma/avaliarTarefas.jsf");
			
		} finally {
			dao.close();
			mDao.close();
			dDao.close();
		}

		return null;
	}
	
	/** 
	 * Nomeia os arquivos com a matrícula e os dois primeiros nomes de discente 
	 * e organiza os arquivos em pastas segundo as subturmas. <br></br>
	 * 
	 * @param nomePasta 
	 * @param respostas
	 * @param matriculas
	 * @param zip
	 * @param dDao
	 * @param dao
	 * @param turma
	 * 
	 * @return
	 */
	private void organizarTarefas ( String nomePasta , List<RespostaTarefaTurma> respostas , Map <Integer, Long> matriculas , ZipOutputStream zip ,DiscenteDao dDao, TurmaVirtualDao dao , Turma turma) 
	{
		for (RespostaTarefaTurma resposta : respostas) {
			// Extensão do arquivo
			try {

				Long matricula = matriculas.get(resposta.getUsuarioEnvio().getPessoa().getId());

				if ( matricula != null )
				{	
					Discente discente = dDao.findAtivosByMatricula(matricula);
					
					if ( dao.isDiscenteTurma(discente,turma) )
					{	
						String nome = EnvioArquivoHelper.recuperaNomeArquivo(resposta.getIdArquivo());
						String extensao = (nome.lastIndexOf(".") > -1 ? nome.substring(nome.lastIndexOf(".")) : "");
												
						// Os arquivos serão nomeados com a matrícula e dois primeiros nomes do discente
						String nomeAluno = StringUtils.toAscii((matricula != null ? matricula + "_" : "") + resposta.getUsuarioEnvio().getPessoa().getNome());
						String[] partesNome = nomeAluno.split(" ");
						String doisNomes = partesNome[0] + (partesNome.length > 1 ? " " + partesNome[1] : "");
						String doisNomesGrupo = "";
						
						if ( resposta.getTarefa().isEmGrupo() ) {
							GrupoDiscentes grupo = dao.findByPrimaryKey(resposta.getGrupoDiscentes().getId(), GrupoDiscentes.class); 
							if ( grupo != null ) {
								String nomeGrupo = grupo.getNome();
								String[] partesNomeGrupo = nomeGrupo.split(" ");
								doisNomesGrupo = partesNomeGrupo[0] + (partesNomeGrupo.length > 1 ? "_" + partesNomeGrupo[1] : "");
							}	
						}
						
						// Nome do Arquivo no zip será os dois primeiros nomes do aluno + extensao
						ZipEntry entry = null;
						if ( doisNomesGrupo.isEmpty() )
							entry = new ZipEntry( nomePasta + doisNomes + extensao);
						else
							entry = new ZipEntry( nomePasta + doisNomesGrupo + extensao);
						zip.putNextEntry(entry);
		
						EnvioArquivoHelper.recuperaArquivo(zip, resposta.getIdArquivo());
						zip.closeEntry();
					}
				}	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
