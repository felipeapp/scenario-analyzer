/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '31/03/2008'
 *
 */
package br.ufrn.sigaa.prodocente.jsf;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.prodocente.ProducaoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.jsf.AbstractControllerProdocente;

/**
 * Controlador respons�vel pela consolida��o das valida��es de produ��es intelectuais.
 * 
 * @author Ricardo Wendell
 *
 */
@Component("consolidacaoProducaoBean") @Scope("session")
public class ConsolidacaoProducaoMBean extends AbstractControllerProdocente {

	private Collection<Servidor> docentes;
	
	private Collection<? extends Producao> producoes;
	
	public ConsolidacaoProducaoMBean() {
		clear();
	}
	
	private void clear() {
		producoes = null;
		docentes = null;
	}
	
	/**
	 * Listar os docentes com produ��es validadas mas pendentes de consolida��o. 
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String listarPendentes() throws DAOException {
		// Verificar permiss�es
		if (!isChefe() && !isDiretorCentro()) {
			addMensagemErro("Voc� n�o possui permiss�o para realizar a consolida��o de valida��es de produ��es intelectuais");
			return null;
		}
		
		// Buscar docentes
		Servidor consolidador = getUsuarioLogado().getServidorAtivo();
		Unidade unidade = isChefe() ? consolidador.getUnidade() : consolidador.getUnidade().getGestora();
		docentes = getDAO(ProducaoDao.class).findDocentesPendentesConsolidacao(consolidador, unidade, isChefe());
		
		// Verificar exist�ncia de produ��es pendentes
		if (docentes == null || docentes.isEmpty()) {
			addMensagemWarning("N�o h� produ��es pendentes de consolida��o de valida��o");
			redirectJSF(getSubSistema().getLink());
		}
		
		return forward("/prodocente/validacao/pendentes_consolidacao.jsp");
	}
	
	/**
	 * Listar as produ��es pendentes de consolida��o do docente selecionado
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String listarProducoes() throws ArqException {
		
		// Buscar as produ��es pendentes de consolida��o
		producoes = getDAO(ProducaoDao.class).findPendentesConsolidacao(docente);
		
		// Verificar se existe alguma produ��o pendente de consolida��o
		if ( producoes.isEmpty() ) {
			addMensagemErro("N�o foram encontradas produ��es pendentes de consolida��o para o servidor selecionado");
			return null;
		}
		
		popularMapaProducoes(producoes);
		prepareMovimento(SigaaListaComando.CONSOLIDAR_VALIDACAO_PRODUCAO);
		
		return forward("/prodocente/validacao/consolidacao.jsp");
	}
	
	/**
	 * Registrar a consolida��o das produ��es intelectuais
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String confirmar() throws ArqException {
		
		// Validar as produ��es
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setColObjMovimentado(producoes);
		mov.setCodMovimento(SigaaListaComando.CONSOLIDAR_VALIDACAO_PRODUCAO);
		
		try {
			execute(mov);
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} 
		
		addMensagemInformation("Consolida��o das produ��es de " + getGenericDAO().refresh(docente).getNome() + " realizada com sucesso!");
		
		return listarPendentes();
	}
	
	@Override
	public String cancelar() {
		clear();
		try {
			redirectJSF(getSubSistema().getLink());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Collection<Servidor> getDocentes() {
		return this.docentes;
	}

	public void setDocentes(Collection<Servidor> docentes) {
		this.docentes = docentes;
	}

	public Collection<? extends Producao> getProducoes() {
		return this.producoes;
	}

	public void setProducoes(Collection<? extends Producao> producoes) {
		this.producoes = producoes;
	}
	
}
