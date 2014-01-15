/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 18/02/2008
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.graduacao.ProcessamentoMatriculaDAO;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.processamento.dominio.MatriculaEmProcessamento;

/**
 * Managed Bean para realizar a listagem dos indeferimentos após o
 * processamento da matricula.
 * 
 * @author David Pereira
 *
 */
@Component("listaIndeferimentos") @Scope("request")
public class ListaIndeferimentosMBean extends SigaaAbstractController<Object> {

	private Integer ano, periodo;
	
	private Unidade unidade = new Unidade();
	
	private boolean orientadorAcademico;
	
	private List<MatriculaEmProcessamento> indeferimentos;
	
	public String iniciar() throws DAOException {
		CalendarioAcademico ca = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		ano = ca.getAno();
		periodo = ca.getPeriodo();
		return forward("/processamento/lista_indeferimentos.jsp");
	}
	
	public String iniciarOrientadorAcademico() throws DAOException {
		CalendarioAcademico ca = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		orientadorAcademico = true;
		indeferimentos = getDAO(ProcessamentoMatriculaDAO.class).findIndeferimentos(ca.getAno(), ca.getPeriodo(), unidade, getServidorUsuario(), null);
		return forward("/processamento/lista_indeferimentos.jsp");
	}
	
	public String consultar() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validaInt(ano, "Ano", lista);
		ValidatorUtil.validaInt(periodo, "Periodo", lista);		
		
		if (!lista.isEmpty()) {
			addMensagens(lista);
			return null;
		}
		
		Curso curso= null;
		
		if (getCursoAtualCoordenacao() != null) {
			curso = getCursoAtualCoordenacao();
			indeferimentos = getDAO(ProcessamentoMatriculaDAO.class).findIndeferimentos(ano, periodo, curso);
		}
		else {
			indeferimentos = getDAO(ProcessamentoMatriculaDAO.class).findIndeferimentos(ano, periodo, unidade);
		}
		
		if (indeferimentos.isEmpty()) {
			addMensagemErro("Nenhum indeferimento encontrado.");
			return null;
		}
		
		return null;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public List<MatriculaEmProcessamento> getIndeferimentos() {
		return indeferimentos;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public boolean isOrientadorAcademico() {
		return orientadorAcademico;
	}

}
