/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 18/07/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.internacionalizacao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.Order;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.internacionalizacao.dao.TraducaoElementoDao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.IdiomasEnum;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.ItemTraducao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.ItemTraducaoElementos;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.TraducaoElemento;

/**
 * Managed Bean utilizado para manter os métodos genéricos de cadastro 
 * e manutenção da tradução dos elementos de documentos oficiais da instituição.
 * 
 * @author Rafael Gomes
 * @param <T>
 */
public abstract class AbstractTraducaoElementoMBean<T> extends SigaaAbstractController<T>{

	/** Lista com os elementos de tradução de um elemento. */
	protected List<ItemTraducaoElementos> listaTraducaoElemento = new ArrayList<ItemTraducaoElementos>();
	/** Quantidade de idiomas disponíveis para tradução no sistema. */
	protected int qtdeIdiomas;
	
	
	/**
	 * Método utilizado para preparar e carregar as propriedades da entidade 
	 * passada por parâmetros para serem traduzidos. 
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não invocado por JSP</li>
	 * </ul>
	 * @param <T>
	 * @param classe
	 * @param idElemento
	 * @throws DAOException
	 */
	@SuppressWarnings("hiding")
	public <T> void carregarElementosTraducao(Class<T> classe, Integer idElemento, Order order) throws DAOException{
		
		TraducaoElementoDao dao = getDAO(TraducaoElementoDao.class);
		
		try {
			Collection<ItemTraducao> atributosTraducao = dao.findItensByClasse(classe.getName(), order);
			Object objTraduzido = ReflectionUtils.newInstance(classe.getName());
			objTraduzido = dao.findByExactField(classe, "id", idElemento).iterator().next();
			
			qtdeIdiomas = IdiomasEnum.values().length;
			listaTraducaoElemento = new ArrayList<ItemTraducaoElementos>();
			
			List<TraducaoElemento> listElementoFinal = new ArrayList<TraducaoElemento>();
			boolean inputDisabled = false;
			for (ItemTraducao itemTraducao : atributosTraducao) {
				listElementoFinal = new ArrayList<TraducaoElemento>();
				List<TraducaoElemento> listTraducaoElemento = (List<TraducaoElemento>) dao.findByItemTraducaoAndElemento(itemTraducao, idElemento); 
				if (listTraducaoElemento.isEmpty()){
					for (String str : IdiomasEnum.getAll()) {
						TraducaoElemento elemento = new TraducaoElemento(itemTraducao, str, idElemento);
						elemento.setDescricaoIdioma(IdiomasEnum.getDescricaoIdiomas().get(str));
						if (IdiomasEnum.PORTUGUES.getId().equalsIgnoreCase(str) 
								&& ReflectionUtils.evalPropertyObj(objTraduzido, itemTraducao.getAtributo()) != null) {
							elemento.setValor(ReflectionUtils.evalPropertyObj(objTraduzido, itemTraducao.getAtributo()).toString());
						}	
						if (IdiomasEnum.PORTUGUES.getId().equalsIgnoreCase(str) ){
							inputDisabled = ValidatorUtil.isEmpty(elemento.getValor());
						}
						elemento.setInputDisabled(inputDisabled);
						listElementoFinal.add(elemento);
					}
				} else {
					boolean insereElemento = true;
					for (String str : IdiomasEnum.getAll()) {
						insereElemento = true;
						for (TraducaoElemento te : listTraducaoElemento) {
							if (te.getIdioma().equals(str)){
								te.setDescricaoIdioma(IdiomasEnum.getDescricaoIdiomas().get(str));
								listElementoFinal.add(te);
								insereElemento = false;
								break;
							}
						}
						if (insereElemento){
							listElementoFinal.add(new TraducaoElemento(itemTraducao, str, idElemento, IdiomasEnum.getDescricaoIdiomas().get(str)));
						}
					}
				}
				listaTraducaoElemento.add(new ItemTraducaoElementos(itemTraducao, listElementoFinal));
			}
		} catch (Exception e) {
			e.printStackTrace();
			addMensagemErroPadrao();
		}	
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		checkChangeRole();
		if ("remover".equalsIgnoreCase(getConfirmButton()))
			return remover();
		
		List<TraducaoElemento> listElementosComTraducao = new ArrayList<TraducaoElemento>();
		
		for (ItemTraducaoElementos ite : listaTraducaoElemento) {
			listElementosComTraducao.addAll(ite.getElementos());
		}
		
		for (Iterator<TraducaoElemento> iterator = listElementosComTraducao.iterator(); iterator.hasNext();) {
			TraducaoElemento elem = iterator.next();
			if( ValidatorUtil.isEmpty(elem.getValor()) && elem.getId() == 0 )
				iterator.remove();
		}
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.TRADUZIR_ELEMENTO);
		mov.setObjAuxiliar(listElementosComTraducao);
		try {
			execute(mov);
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Internacionalização");
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			removeOperacaoAtiva();
			return null;
		} 
		removeOperacaoAtiva();
		return forward(getListPage());
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_TRADUCAO_DOCUMENTOS, SigaaPapeis.TRADUTOR_DADOS_ACADEMICOS);
	}
	
	public List<ItemTraducaoElementos> getListaTraducaoElemento() {
		return listaTraducaoElemento;
	}

	public void setListaTraducaoElemento(
			List<ItemTraducaoElementos> listaTraducaoElemento) {
		this.listaTraducaoElemento = listaTraducaoElemento;
	}

	public int getQtdeIdiomas() {
		return qtdeIdiomas;
	}

	public void setQtdeIdiomas(int qtdeIdiomas) {
		this.qtdeIdiomas = qtdeIdiomas;
	}
	
}
