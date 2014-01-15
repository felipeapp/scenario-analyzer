package br.ufrn.sigaa.ensino.tecnico.jsf;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dao.JubilamentoQuery;
import br.ufrn.sigaa.ensino.dominio.TipoJubilamento;
import br.ufrn.sigaa.ensino.jsf.JubilamentoMBean;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean responsável por cancelar os discente que tenham excedido o número de reprovações.
 * 
 * @author Jean Guerethes
 */
@Component @Scope("session")
public class CancelamentoReprovacoesTecnicoMBean extends SigaaAbstractController<DiscenteTecnico> {

	Collection<Discente> discenteReprovacoesMesmoComponente;
	Collection<Discente> discenteReprovacoesComponenteDistinto;
	private TipoJubilamento tipoJub;
	public static final int tipoJubilamento = 5;
	/**Utilizados para armazenar a consulta a ser realizada.*/
	private  JubilamentoQuery jubilamentoQuery;

	public CancelamentoReprovacoesTecnicoMBean() {
		obj = new DiscenteTecnico();
		discenteReprovacoesComponenteDistinto = new ArrayList<Discente>();
		discenteReprovacoesMesmoComponente = new ArrayList<Discente>();
	}

	public String iniciar() throws DAOException {
		carregarQuery(tipoJubilamento);			
		
		discenteReprovacoesMesmoComponente = jubilamentoQuery.findAlunosPassiveisJubilamento(null, null, null, getUsuarioLogado().getUnidade(), Boolean.TRUE);
		discenteReprovacoesComponenteDistinto = jubilamentoQuery.findAlunosPassiveisJubilamento(null, null, null, getUsuarioLogado().getUnidade(), Boolean.FALSE);
		
		if ( discenteReprovacoesComponenteDistinto.isEmpty() && discenteReprovacoesMesmoComponente.isEmpty() ) {
			addMensagemErro("Não foi encontrado nenhum discente.");
			return null;
		}
		
		setOperacaoAtiva(SigaaListaComando.JUBILAR_DISCENTE.getId());
		
		return forward("/ensino/tecnico/cancelamento_reprovacoes/lista.jsf");
	}

	/**
	 * Utilizado para carregar a consulta a ser executada. 
	 * @param id
	 * @throws DAOException
	 */
	private void carregarQuery(Integer id) throws DAOException {
		tipoJub = getGenericDAO().findByPrimaryKey(id, TipoJubilamento.class);
		try {			
			jubilamentoQuery = (JubilamentoQuery) ReflectionUtils.newInstance(tipoJub.getClasseConsulta());			
		} catch (Exception e) {
			throw new ConfiguracaoAmbienteException("Não foi possível carregar a classe de consulta para este tipo de jubilamento. Por favor entre em contato com a administração do sistema.");
		}
	}
	
	public String cancelarDiscentes() throws ArqException {

		Collection<Discente> discentes = new ArrayList<Discente>();
		
		for (Discente d : discenteReprovacoesMesmoComponente) {
			if ( !discentes.contains(d) ){
				d.setSelecionado(true);
				discentes.add(d);
			}
		}

		for (Discente d : discenteReprovacoesComponenteDistinto) {
			if ( !discentes.contains(d) ){
				d.setSelecionado(true);
				discentes.add(d);
			}
		}
		
		JubilamentoMBean mBean = getMBean("jubilamentoMBean");
		mBean.setDiscentes(discentes);
		mBean.setTipoJub(tipoJub);
		mBean.cancelarAlunos();
		removeOperacaoAtiva();
		
		return redirectJSF(getSubSistema().getLink());
	}
	
	public TipoJubilamento getTipoJub() {
		return tipoJub;
	}

	public void setTipoJub(TipoJubilamento tipoJub) {
		this.tipoJub = tipoJub;
	}

	public JubilamentoQuery getJubilamentoQuery() {
		return jubilamentoQuery;
	}

	public void setJubilamentoQuery(JubilamentoQuery jubilamentoQuery) {
		this.jubilamentoQuery = jubilamentoQuery;
	}

	public Collection<Discente> getDiscenteReprovacoesMesmoComponente() {
		return discenteReprovacoesMesmoComponente;
	}

	public void setDiscenteReprovacoesMesmoComponente(
			Collection<Discente> discenteReprovacoesMesmoComponente) {
		this.discenteReprovacoesMesmoComponente = discenteReprovacoesMesmoComponente;
	}

	public Collection<Discente> getDiscenteReprovacoesComponenteDistinto() {
		return discenteReprovacoesComponenteDistinto;
	}

	public void setDiscenteReprovacoesComponenteDistinto(
			Collection<Discente> discenteReprovacoesComponenteDistinto) {
		this.discenteReprovacoesComponenteDistinto = discenteReprovacoesComponenteDistinto;
	}

}