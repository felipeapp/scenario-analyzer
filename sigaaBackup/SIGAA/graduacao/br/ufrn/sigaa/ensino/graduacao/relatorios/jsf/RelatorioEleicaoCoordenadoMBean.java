/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 10/01/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRange;

import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.dao.eleicao.EleicaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Controller responsável pela geração de relatórios utilizados para a Eleição
 * do Coordenador do Curso de Graduação.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Component("relatorioEleicaoCoordenadoMBean") @Scope("request")
public class RelatorioEleicaoCoordenadoMBean extends SigaaAbstractController<Object> {

	/** Ano ao qual o relatório se refere. */
	private int ano;
	/** Período ao qual o relatório se refere. */
	private int periodo;
	/** Lista de servidores aptos para votar na Eleição para Coordenador de Curso. */
	private List<Servidor> servidores;

	/**
	 * Inicia a geração do relatório de docentes aptos a votar na eleição para
	 * coordenador de curso.<br/>
	 * Método não invocado por JSP´s.
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciarListaDocenteEleicaoCoordenador() throws SegurancaException {
		// seta o ano-período atual
		this.ano = getCalendarioVigente().getAno();
		this.periodo = getCalendarioVigente().getPeriodo();
		// redefine o período para os períodos regulares, caso o período atual seja de férias.
		if (this.periodo == 4)
			this.periodo = 2;
		else
			this.periodo = 1;
		return forward("/graduacao/relatorios/docente/seleciona_eleicao.jsp");
	}

	/**
	 * Gera o relatório de docentes aptos a votar na eleição para coordenador de
	 * curso.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/relatorios/docente/seleciona_eleicao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String gerarListaDocenteEleicaoCoordenador() throws HibernateException, DAOException, SegurancaException{
		// validação de dados para o relatório
		if (getCursoAtualCoordenacao() == null)
			addMensagemErro("Não foi definido o curso atual do coordenador");
		validateRange(periodo, 1, 2, "Período", erros);
		validateMaxValue(ano, getCalendarioVigente().getAno(), "Ano", erros);
		if (hasOnlyErrors())
			return null;
		// geração do relatório
		EleicaoDao dao = getDAO(EleicaoDao.class);
		servidores = dao.findDocentesAptosVotarCoordenadorCurso(getCursoAtualCoordenacao().getId(), ano, periodo);
		if (isEmpty(servidores)){
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		} else {
			return forward("/graduacao/relatorios/docente/lista_eleicao.jsp");
		}
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public List<Servidor> getServidores() {
		return servidores;
	}
}
