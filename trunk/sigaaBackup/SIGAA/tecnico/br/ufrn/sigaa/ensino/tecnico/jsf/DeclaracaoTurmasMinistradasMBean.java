/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/08/2007
 *
 */
package br.ufrn.sigaa.ensino.tecnico.jsf;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * MBean para emitir declaração de turmas ministradas por docentes.
 * @author leonardo
 *
 */

@Component("declaracaoTecnico") @Scope("request")
public class DeclaracaoTurmasMinistradasMBean extends SigaaAbstractController {

	/** Constantes das Views */
	public final String JSP_BUSCA_DOCENTE = "/ensino/tecnico/relatorios/form_busca_docente.jsp";
	public final String JSP_DECLARACAO = "/ensino/tecnico/relatorios/declaracao_turmas_ministradas.jsp";

	/** Indica o Docente interessado na emissão da declaração. */
	private Servidor docente;
	
	/** Indica as turmas que o Docente ministrou. */
	private Collection<DocenteTurma> turmas;

	/** Ano/Período inicial e final na qual o docente ministrou aulas. */
	private Integer ano;
	private Integer periodo;
	private Integer anoFim;
	private Integer periodoFim;

	public DeclaracaoTurmasMinistradasMBean(){
		docente = new Servidor();
	}

	/**
	 * Verifica as permissões de acesso, popula as informações necessárias e
	 * redireciona para a página de busca.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciar() throws SegurancaException, DAOException{
		// validar quem pode emitir a declaração
		checkRole(new int[]{SigaaPapeis.GESTOR_TECNICO});
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		periodo = cal.getPeriodo();
		anoFim = cal.getAno();
		periodoFim = cal.getPeriodo();
		return forward(JSP_BUSCA_DOCENTE);
	}

	/**
	 * Responsável por emitir uma declaração informando o período e ano na qual um docente ministrou aulas.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/tecnico/relatorios/form_busca_docente.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public String emitirDeclaracao() throws DAOException{
		TurmaDao dao = getDAO(TurmaDao.class);
		erros = new ListaMensagens();
		ValidatorUtil.validateRequiredAjaxId(docente.getId(), "Docente", erros);
		ValidatorUtil.validateRequired(ano, "Ano", erros);
		ValidatorUtil.validateRequired(periodo, "Período", erros);
		if(hasErrors()){
			addMensagens(erros);
			return null;
		}

		turmas = new ArrayList<DocenteTurma>();
		docente = dao.findByPrimaryKey(docente.getId(), Servidor.class);
		
		int periodoAtual = periodo;
		for(int anoAtual = ano; anoAtual <= anoFim; anoAtual++){
			if(anoAtual != anoFim){
				while(periodoAtual <= 2){
					turmas.addAll( dao.findByDocente(docente.getId(), anoAtual, periodoAtual, NivelEnsino.TECNICO) );
					periodoAtual++;
				}
				periodoAtual = 1;
			}else{
				while(periodoAtual <= periodoFim){
					turmas.addAll( dao.findByDocente(docente.getId(), anoAtual, periodoAtual, NivelEnsino.TECNICO) );
					periodoAtual++;
				}
			}
		}
		if(turmas != null && turmas.size() == 0){
			addMensagemErro("O docente informado não possui turmas nesse semestre.");
			return null;
		}
		return forward(JSP_DECLARACAO);
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Servidor getDocente() {
		return docente;
	}

	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Collection<DocenteTurma> getTurmas() {
		return turmas;
	}

	public void setTurmas(Collection<DocenteTurma> turmas) {
		this.turmas = turmas;
	}

	public Integer getAnoFim() {
		return anoFim;
	}

	public void setAnoFim(Integer anoFim) {
		this.anoFim = anoFim;
	}

	public Integer getPeriodoFim() {
		return periodoFim;
	}

	public void setPeriodoFim(Integer periodoFim) {
		this.periodoFim = periodoFim;
	}
}
