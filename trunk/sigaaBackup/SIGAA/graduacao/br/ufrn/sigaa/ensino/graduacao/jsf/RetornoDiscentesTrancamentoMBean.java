/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/09/21 - 12:12:31
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.graduacao.dao.RetornoDiscentesTrancamentoDao;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoAfastamentoAluno;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Managed bean para realização do cancelamento de programa por
 * abandono de curso.
 *
 * @author David Pereira
 * @author Victor Hugo
 *
 */
public class RetornoDiscentesTrancamentoMBean extends SigaaAbstractController {

	private List<Discente> listaDiscentes;
	private String[] selecao;

	public List<Discente> getListaDiscentes() {
		return listaDiscentes;
	}

	public void setListaDiscentes(List<Discente> listaDiscentes) {
		this.listaDiscentes = listaDiscentes;
	}
	
	/**
	 * Buscar discentes com programa trancado no semestre anterior que não
	 * tem trancamento para o semestre atual.
	 */
	public String iniciarTecnico() throws DAOException {
		RetornoDiscentesTrancamentoDao retornoDao = getDAO(RetornoDiscentesTrancamentoDao.class);
		retornoDao.clearSession();
		listaDiscentes = retornoDao.findDiscentesRetornoTrancamento(getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo(), getUsuarioLogado().getNivelEnsino(), getUsuarioLogado().getVinculoAtivo().getUnidade());

		return forward ("/graduacao/discente/lista_alunos_trancamento.jsf");
	}

	/**
	 * Buscar discentes com programa trancado no semestre anterior que não
	 * tem trancamento para o semestre atual.
	 */
	public String iniciar() throws DAOException {

		RetornoDiscentesTrancamentoDao retornoDao = getDAO(RetornoDiscentesTrancamentoDao.class);
		retornoDao.clearSession();
		listaDiscentes = retornoDao.findDiscentesRetornoTrancamento(getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo(), getUsuarioLogado().getNivelEnsino(), null);

		return forward ("/graduacao/discente/lista_alunos_trancamento.jsf");
	}

	public String confirmar() throws ArqException {

		DiscenteDao discenteDao = getDAO(DiscenteDao.class);
		String[] items = getParameterValues("items");

		if (items != null) {

			Collection<Discente> discentes = new ArrayList<Discente>();

			Collection<Integer> ids = new ArrayList<Integer>();
			for (String idsSelecionados : items) {
				ids.add( Integer.parseInt(idsSelecionados) );
			}

			discentes = discenteDao.findByIds( ids );

			prepareMovimento(SigaaListaComando.RETORNAR_ALUNO_AFASTADO);

			CalendarioAcademico cal = getCalendarioVigente();

			int ano = cal.getAno();
			int periodo = cal.getPeriodo();
			int anoPassado = 0, periodoPassado = 0;

			if (periodo == 2) {
				anoPassado = ano;
				periodoPassado = 1;
			}else if (periodo == 1) {
				anoPassado = ano - 1;
				periodoPassado = 2;
			}


			MovimentoAfastamentoAluno mov = new MovimentoAfastamentoAluno();
			mov.setCodMovimento(SigaaListaComando.RETORNAR_ALUNO_AFASTADO);
			mov.setAnoPassado(anoPassado);
			mov.setPeriodoPassado(periodoPassado);
			mov.setColObjMovimentado( discentes );
			try {
				executeWithoutClosingSession(mov, getCurrentRequest());
			} catch (NegocioException e) {
				addMensagemErro(e.getMessage());
				return null;
			} catch (Exception e) {
				notifyError(e);
				addMensagemErroPadrao();
				e.printStackTrace();
				return null;
			}

			addMessage("Todos os alunos foram retornados com sucesso!", TipoMensagemUFRN.INFORMATION);
			return cancelar();
		}
		else {
			addMensagemErro("É obrigatório selecionar pelo menos um discente!");
			return iniciar();
		}

	}

	public String[] getSelecao() {
		return selecao;
	}

	public void setSelecao(String[] selecao) {
		this.selecao = selecao;
	}

}
