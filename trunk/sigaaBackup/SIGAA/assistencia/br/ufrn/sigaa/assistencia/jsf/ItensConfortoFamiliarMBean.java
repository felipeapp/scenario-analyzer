package br.ufrn.sigaa.assistencia.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.cadunico.dominio.ItemConfortoFamiliar;

/**
 * Apenas o CRUD da entidade Itens Conforto Familiar
 * 
 * @author agostinho
 *
 */
@Component("itensConfortoFamiliarMBean") 
@Scope("session")
public class ItensConfortoFamiliarMBean extends SigaaAbstractController<ItemConfortoFamiliar> {

//	private List<ItemConfortoFamiliar> listaItensConfortoFamiliar = new ArrayList<ItemConfortoFamiliar>();
//	
//	public ItensConfortoFamiliarMBean() {
//	}
//	
//	public String instanciar() {
//		clear();
//		return forward("/sae/" + obj.getClass().getSimpleName() + "/form.jsf");
//	}
//	
//	public String cancelar() {
//		return forward("/sae/menu.jsf");
//	}
//	
//	public String alterar() throws ArqException {
//		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
//		prepareMovimento(ArqListaComando.ALTERAR);
//		int id = getParameterInt("id", 0);
//		obj = getDAO(GenericDAOImpl.class).findByPrimaryKey(id, ItemConfortoFamiliar.class);
//		return forward("/sae/" + obj.getClass().getSimpleName() + "/form.jsf");
//	}
//	
//	public String remover() throws ArqException{
//
//		prepareMovimento(ArqListaComando.ALTERAR);
//		int id = getParameterInt("id", 0);
//		obj = getDAO(GenericDAOImpl.class).findByPrimaryKey(id, ItemConfortoFamiliar.class);
//		obj.setAtivo(false); // marca como removido
//		obj.setQuantidade(null);
//		
//		try {
//			super.cadastrar();
//		} catch (NegocioException e) {
//			e.printStackTrace();
//		}
//		return listar();
//	}
//	
//	private void clear() {
//		obj = new ItemConfortoFamiliar();
//	}
//
//	@Override
//	protected String forwardRemover() {
//		return forward("/sae/" + obj.getClass().getSimpleName() + "/lista.jsf");
//	}
//	
//	@Override
//	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
//		
//		if (!obj.getItem().equals("")) {
//		
//			super.cadastrar();
//			return null;
//		}
//		else {
//			addMensagemErro("Itens de confortor não pode ser vazio!");
//			return null;
//		}
//	}
//	
//	@Override
//	public String forwardCadastrar() {
//		return "/sae/index.jsf";
//	}
//
//	@Override
//	public String listar() throws ArqException {
//		clear();
//		listaItensConfortoFamiliar.clear();
//		List<ItemConfortoFamiliar> listaTemp = (List<ItemConfortoFamiliar>) getAllObj(ItemConfortoFamiliar.class);
//		
//		for (ItemConfortoFamiliar condicaoMoradia : listaTemp) {
//			if (condicaoMoradia.isAtivo())
//				listaItensConfortoFamiliar.add(condicaoMoradia);
//		}
//		
//		return forward("/sae/" + obj.getClass().getSimpleName() + "/lista.jsf");
//	}
//	
//	public List<ItemConfortoFamiliar> getListaItensConfortoFamiliar() {
//		return listaItensConfortoFamiliar;
//	}
//	
//
//	public void setListaItensConfortoFamiliar(
//			List<ItemConfortoFamiliar> listaItensConfortoFamiliar) {
//		this.listaItensConfortoFamiliar = listaItensConfortoFamiliar;
//	}
	
}
