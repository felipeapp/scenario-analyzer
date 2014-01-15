/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '11/08/2008'
 *
 */
package br.ufrn.sigaa.prodocente.jsf;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.prodocente.ProducaoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.prodocente.producao.dominio.BolsaObtida;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.jsf.AbstractControllerProdocente;

/**
 * Controlador para efetuar a operação de validação das bolsas de produtividade
 * realizada pela PROPESQ.
 * 
 * @author leonardo
 *
 */
@SuppressWarnings("unchecked")
@Component("validacaoBolsaProdutividadeBean") @Scope("session")
public class ValidacaoBolsaProdutividadeMBean extends AbstractControllerProdocente {

	private Collection<BolsaObtida> bolsas;
	
	public ValidacaoBolsaProdutividadeMBean() {
		clear();
	}

	private void clear() {
		bolsas = null;
	}
	
	private List<SelectItem> anos;
	
	/**
	 * Retorna todos os anos que tem publicados válidas
	 *
	 * @return
	 * @throws DAOException
	 */
	@Override
	public List<SelectItem> getAnos() throws DAOException {

		if (anos == null || anos.isEmpty()) {
			ProducaoDao dao = getDAO(ProducaoDao.class);
			Collection<Integer> anosBD = dao.getAnosCadastradosBolsaProdutividadePendente();
			anos = new ArrayList<SelectItem>();
			for (Integer ano : anosBD) {
				if (ano != null) {
					SelectItem item = new SelectItem(ano.toString(), ano
							.toString());
					anos.add(item);
				}
			}
		}

		return this.anos;
	}
	
	/**
	 * Popula a tela com os dados para a validação.
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String popular() throws ArqException{
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		prepareMovimento(SigaaListaComando.AUTO_VALIDAR_PRODUCAO);
		return forward("/pesquisa/validacao/bolsas_produtividade.jsf");
	}
	
	/**
	 * Listar todas as bolsas de produtividade pendentes de validação do ano selecionado.
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public void listarPendentes(ValueChangeEvent evt) throws ArqException {
		// Verificar permissão de validação
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		
		Integer ano = new Integer( (String) evt.getNewValue() );
		
		if(ano > 0){
			// Buscar as bolsas de produtividade pendentes de validação
			bolsas = getDAO(ProducaoDao.class).findBolsasProdutividadePendentesValidacao(ano);
		}
						
		popularMapaProducoes(bolsas);
	}	
	
	/**
	 * Confirmar a validação
	 * 
	 * @return
	 * @throws RemoteException 
	 * @throws ArqException 
	 */
	public String confirmar() throws ArqException {
		
		HttpServletRequest req = getCurrentRequest();
		String[] selecaoProducao = req.getParameterValues("selecaoProducao");
		
		if (selecaoProducao == null) {
			addMensagemErro("É necessário selecionar pelo menos uma produção.");
			return null;
		}
		
		Collection<Producao> producoesSelecionados = new ArrayList<Producao>();
		
		for (int i = 0; i < selecaoProducao.length; i++) {
			Producao prod = getGenericDAO().findByPrimaryKey(new Integer(selecaoProducao[i]), Producao.class);
			producoesSelecionados.add(prod);
		}
		
		// Validar as produções
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setColObjMovimentado(producoesSelecionados);
		mov.setCodMovimento(SigaaListaComando.AUTO_VALIDAR_PRODUCAO);
		
		try {
			execute(mov);
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} 
		
		addMensagemInformation("Produções validadas com sucesso!");
		return cancelar();
	}

	@Override
	public String cancelar() {
		clear();
		resetBean();
		try {
			redirectJSF(getSubSistema().getLink());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Collection<BolsaObtida> getBolsas() {
		return bolsas;
	}

	public void setBolsas(Collection<BolsaObtida> bolsas) {
		this.bolsas = bolsas;
	}

}
