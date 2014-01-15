/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Controlador responsável pela consolidação das validações de produções intelectuais.
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
	 * Listar os docentes com produções validadas mas pendentes de consolidação. 
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String listarPendentes() throws DAOException {
		// Verificar permissões
		if (!isChefe() && !isDiretorCentro()) {
			addMensagemErro("Você não possui permissão para realizar a consolidação de validações de produções intelectuais");
			return null;
		}
		
		// Buscar docentes
		Servidor consolidador = getUsuarioLogado().getServidorAtivo();
		Unidade unidade = isChefe() ? consolidador.getUnidade() : consolidador.getUnidade().getGestora();
		docentes = getDAO(ProducaoDao.class).findDocentesPendentesConsolidacao(consolidador, unidade, isChefe());
		
		// Verificar existência de produções pendentes
		if (docentes == null || docentes.isEmpty()) {
			addMensagemWarning("Não há produções pendentes de consolidação de validação");
			redirectJSF(getSubSistema().getLink());
		}
		
		return forward("/prodocente/validacao/pendentes_consolidacao.jsp");
	}
	
	/**
	 * Listar as produções pendentes de consolidação do docente selecionado
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String listarProducoes() throws ArqException {
		
		// Buscar as produções pendentes de consolidação
		producoes = getDAO(ProducaoDao.class).findPendentesConsolidacao(docente);
		
		// Verificar se existe alguma produção pendente de consolidação
		if ( producoes.isEmpty() ) {
			addMensagemErro("Não foram encontradas produções pendentes de consolidação para o servidor selecionado");
			return null;
		}
		
		popularMapaProducoes(producoes);
		prepareMovimento(SigaaListaComando.CONSOLIDAR_VALIDACAO_PRODUCAO);
		
		return forward("/prodocente/validacao/consolidacao.jsp");
	}
	
	/**
	 * Registrar a consolidação das produções intelectuais
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String confirmar() throws ArqException {
		
		// Validar as produções
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setColObjMovimentado(producoes);
		mov.setCodMovimento(SigaaListaComando.CONSOLIDAR_VALIDACAO_PRODUCAO);
		
		try {
			execute(mov);
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} 
		
		addMensagemInformation("Consolidação das produções de " + getGenericDAO().refresh(docente).getNome() + " realizada com sucesso!");
		
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
