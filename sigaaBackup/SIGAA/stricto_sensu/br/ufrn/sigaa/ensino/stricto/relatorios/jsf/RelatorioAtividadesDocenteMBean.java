/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.relatorios.jsf;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/** MBean responsável pela geração de relatórios referente à atividades de um docente na pós-graduação.
 * 
 * @author Édipo Elder F. Melo
 *
 */
@Component("relatorioAtividadesDocente") 
@Scope("request")
public class RelatorioAtividadesDocenteMBean extends SigaaAbstractController<Servidor>{

	/** Ano de início das disciplinas ministradas e orientações do docente. */
	private int ano;
	
	/** Período das turmas ministradas pelo docente. */
	private int periodo;
	
	/** Coleção de turmas ministradas no ano informado. */
	private Collection<Turma> turmas;
	
	/** Coleção de orientações acadêmicas que iniciaram no ano informado. */
	private Collection<OrientacaoAcademica> orientacoesAcademicas;

	/** Lista de docentes sem atividades/orientações no ano/período informados.*/
	private Collection<Servidor> docentes;
	
	/** ID da unidade ao qual se restringe o relatório de docentes sem atividades/orientandos.*/
	private int idUnidade;
	
	/** Inicia a geração do Relatório de Atividades do Docente.
	 * Chamado por /menus/relatorios.jsp
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioAtividades() throws SegurancaException {
		checkRole(SigaaPapeis.PPG);
		ano = getAnoAtual();
		this.turmas = null;
		this.orientacoesAcademicas = null;
		this.obj = new Servidor();
		return forward("/stricto/relatorios/seleciona_docente.jsp"); 
	}
	
	/** Inicia a geração do Relatório de Atividades do Docente.
	 * Chamado por /menus/relatorios.jsp
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioSemAtividades() throws SegurancaException {
		checkRole(SigaaPapeis.PPG);
		ano = getAnoAtual();
		periodo = getPeriodoAtual();
		this.turmas = null;
		this.orientacoesAcademicas = null;
		this.obj = new Servidor();
		return forward("/stricto/relatorios/form_sem_atividade.jsp"); 
	}
	
	/** Redireciona para o relatório de atividades do docente. 
	 * Chamado por /stricto/relatorios/seleciona_docente.jsp
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String gerarRelatorioAtividades() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.PPG);
		ValidatorUtil.validateRequired(obj, "Docente", erros);
		ValidatorUtil.validateMinValue(ano, 1900, "Ano", erros);
		if (hasErrors()) return null;
		TurmaDao turmasDao = getDAO(TurmaDao.class);
		OrientacaoAcademicaDao oaDao = getDAO(OrientacaoAcademicaDao.class);
		turmas = turmasDao.findByDocente(obj, NivelEnsino.STRICTO, ano, null, null, true, false);
		orientacoesAcademicas = oaDao.findAllByServidorPeriodo(obj, ano, ano, false, NivelEnsino.STRICTO, NivelEnsino.MESTRADO, NivelEnsino.DOUTORADO, NivelEnsino.LATO);
		obj = turmasDao.refresh(obj);
		return forward("/stricto/relatorios/atividades_orientacao_docente.jsp");
	}
	
	/** Redireciona para o relatório de docentes sem atividades. 
	 * Chamado por /stricto/relatorios/form_sem_atividade.jsp
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String gerarRelatorioSemAtividades() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.PPG);
		ValidatorUtil.validateMinValue(ano, 1900, "Ano", erros);
		ValidatorUtil.validateRange(periodo, 1, 4, "Período", erros);
		ServidorDao dao = getDAO(ServidorDao.class);
		if (hasErrors()) return null;
		docentes = dao.findDocenteSemTurmaOrientacaoAcademica(ano, periodo, NivelEnsino.STRICTO, idUnidade);
		if (docentes == null || docentes.isEmpty()) {
			addMensagemWarning("Não há docentes sem orientandos ou sem turmas no ano/período informados.");
			return null;
		}
		if (idUnidade != 0) {
			obj = new Servidor();
			obj.setUnidade(dao.findByPrimaryKey(idUnidade, Unidade.class));
		}
		return forward("/stricto/relatorios/relatorio_docentes_sem_atividades.jsp");
	}
	
	/** Retorna a coleção de turmas ministradas no ano informado. 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> getTurmasDocente() throws DAOException {
		return turmas;
	}
	
	/** Retorna a coleção de orientações acadêmicas que iniciaram no ano informado. 
	 * @return
	 * @throws DAOException
	 */
	public Collection<OrientacaoAcademica> getOrientacoesDocente() throws DAOException {
		return orientacoesAcademicas;
	}

	/** Retorna o ano de início das disciplinas ministradas e orientações do docente. 
	 * @return
	 */
	public int getAno() {
		return ano;
	}

	/** Seta o ano de início das disciplinas ministradas e orientações do docente.
	 * @param ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna o período das turmas ministradas pelo docente. 
	 * @return
	 */
	public int getPeriodo() {
		return periodo;
	}

	/** Seta o período das turmas ministradas pelo docente.
	 * @param periodo
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	/** Retorna a lista de docentes sem atividades/orientações no ano/período informados.
	 * @return
	 */
	public Collection<Servidor> getDocentes() {
		return docentes;
	}

	/** Seta a lista de docentes sem atividades/orientações no ano/período informados.
	 * @param docentes
	 */
	public void setDocentes(Collection<Servidor> docentes) {
		this.docentes = docentes;
	}

	/** Retorna o ID da unidade ao qual se restringe o relatório de docentes sem atividades/orientandos.
	 * @return
	 */
	public int getIdUnidade() {
		return idUnidade;
	}

	/** Seta o ID da unidade ao qual se restringe o relatório de docentes sem atividades/orientandos.
	 * @param idUnidade
	 */
	public void setIdUnidade(int idUnidade) {
		this.idUnidade = idUnidade;
	}

}
