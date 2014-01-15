/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 05/04/2010
 *
 */
package br.ufrn.sigaa.ensino.infantil.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.infantil.dao.FormularioEvolucaoCriancaDao;
import br.ufrn.sigaa.ensino.infantil.dominio.FormularioEvolucaoCrianca;
import br.ufrn.sigaa.ensino.infantil.dominio.FormularioTurma;
import br.ufrn.sigaa.ensino.infantil.dominio.ItemInfantil;
import br.ufrn.sigaa.ensino.infantil.dominio.ItemInfantilFormulario;

/**
 * Processador responsável por persistir as informações do Formulário de Evolução
 * 
 * @author Thalisson Muriel
 * @author Jean Guerethes
 */

public class ProcessadorCadastroFormularioEvolucao extends AbstractProcessador{

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		validate(mov);
		MovimentoCadastro movForm = (MovimentoCadastro) mov;
		FormularioEvolucaoCrianca f = (FormularioEvolucaoCrianca) movForm.getObjMovimentado();
		FormularioTurma ft = (FormularioTurma) movForm.getObjAuxiliar();
		GenericDAO dao = getGenericDAO(movForm);
		
		try {
			if(mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_FORMULARIO_EVOLUCAO)){
				try {
					if ( f.getId() > 0 )
						verificaItensRemovidos(movForm);
					
					if ( isEmpty( f.getComponente() ) && !isEmpty( ft.getTurma().getDisciplina() ) )
						f.setComponente(ft.getTurma().getDisciplina());
	
					cadastrarItemInfantil(movForm);
					dao.createOrUpdate(f);
					dao.createOrUpdate(ft);
					
				} finally {
					dao.close();
				}
			}
			
			if(mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_FORMULARIO_EVOLUCAO)){
				verificaItensRemovidos(movForm);
				cadastrarItemInfantil(movForm);
				alterarItemInfantil(movForm);
				dao.createOrUpdate(f);
				dao.createOrUpdate(ft);
			}
			
			if( mov.getCodMovimento().equals(SigaaListaComando.DUPLICAR_FORMULARIO_EVOLUCAO) ) {
				int ultimoPeriodo = f.getPeriodo() - 1;
				Collection<Integer> ids = removerItensBimestreAtual(f);
				List<ItemInfantilFormulario> itemAdd = new ArrayList<ItemInfantilFormulario>();
				for (ItemInfantilFormulario item : f.getItens()) {
					if ( item.getPeriodo() == ultimoPeriodo && item.getItem().isAtivo() )
						itemAdd.add(montarItemInfantilFormulario(item, (f.getItens().get( f.getItens().size() - 1).getOrdem() + itemAdd.size() + 1)));
				}
	
				for (ItemInfantilFormulario itemInfantilFormulario : itemAdd) {
					f.getItens().add(itemInfantilFormulario.getOrdem(), itemInfantilFormulario);
				}
				
				verificaItensRemovidos(movForm);
				cadastrarItemInfantil(movForm);
				if ( !ids.isEmpty() )
					remocaoItens(ids, movForm);
			}
				
		} finally {
			dao.close();
		}

		return null;
	}

	private void alterarItemInfantil(MovimentoCadastro movForm) throws DAOException {
		FormularioEvolucaoCrianca f = (FormularioEvolucaoCrianca) movForm.getObjMovimentado();
		
		if (isNotEmpty(f.getItens())) {
			GenericDAO dao = getGenericDAO(movForm);
			
			for (ItemInfantilFormulario item : f.getItens()) {
				if (item.isSelecionado()) {
					if ( !item.isConteudo() ) {
						item.getItem().setFormaAvaliacao(null);
					}					
					dao.update(item);
					
					dao.detach(item.getItem());
					dao.detach(item);
					
					item.setSelecionado(false);
				}
			}
		}
	}

	private Collection<Integer> removerItensBimestreAtual( FormularioEvolucaoCrianca f ) {
		Collection<Integer> ids = new ArrayList<Integer>();
		for (ItemInfantilFormulario item : f.getItens()) {
			if ( item.getPeriodo() == f.getPeriodo() && item.getItem().isAtivo() )
				ids.add( item.getItem().getId() );
		}
		return ids;
	}
	
	private void remocaoItens(Collection<Integer> ids, MovimentoCadastro movForm) throws DAOException {
		GenericDAO dao = getGenericDAO(movForm);
		try {
			for (Integer id : ids)
				dao.updateField(ItemInfantil.class, id, "ativo", Boolean.FALSE);	
		} finally {
			dao.close();
		}
	}

	private ItemInfantilFormulario montarItemInfantilFormulario(ItemInfantilFormulario item, int ordem) {
		ItemInfantilFormulario itemFormulario = new ItemInfantilFormulario(item.getProfundidade());
		ItemInfantil itemf = new ItemInfantil();
		itemf.setAtivo(item.getItem().isAtivo());
		itemf.setDescricao(item.getItem().getDescricao());
		itemf.setFormaAvaliacao(item.getItem().getFormaAvaliacao());
		itemf.setTemObservacao(item.getItem().isTemObservacao());
		itemFormulario.setItem(itemf);
		itemFormulario.setProfundidade( item.getProfundidade() );
		itemFormulario.setFormulario( item.getFormulario() );
		itemFormulario.setPeriodo(item.getPeriodo() + 1);
		itemFormulario.setOrdem(ordem);
		itemFormulario.setEditavel(false);
		return itemFormulario;
	}

	private void cadastrarItemInfantil(MovimentoCadastro movForm) throws NegocioException, ArqException {
		FormularioEvolucaoCrianca f = (FormularioEvolucaoCrianca) movForm.getObjMovimentado();
		FormularioEvolucaoCriancaDao dao = getDAO(FormularioEvolucaoCriancaDao.class, movForm);
		Collection<FormularioTurma> findByExactField = dao.findByExactField(FormularioTurma.class, "formulario", f.getId());
		
		List<ItemInfantilFormulario> itens = new ArrayList<ItemInfantilFormulario>();
		
		if (isNotEmpty(findByExactField)) {
			
			FormularioTurma ft = findByExactField.iterator().next();
			FormularioEvolucaoCrianca fBD = dao.findByTurma(ft.getTurma().getId(), f.getComponente().getId());
			if (fBD != null)
				itens = fBD.getItens();
		}
		

		List<ItemInfantilFormulario> subtract = (List<ItemInfantilFormulario>) CollectionUtils.subtract(f.getItens(),itens);		
		
		if (subtract != null && !subtract.isEmpty()) {
			for (ItemInfantilFormulario itemForm : subtract) {
				
				if ( !itemForm.isConteudo() ) {
					itemForm.getItem().setFormaAvaliacao(null);
				}
				
				dao.createOrUpdate(itemForm.getItem());
				dao.createOrUpdate(itemForm);

				dao.detach(itemForm.getItem());
				dao.detach(itemForm);
				
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void verificaItensRemovidos(MovimentoCadastro movForm) throws DAOException {
		FormularioEvolucaoCrianca f = (FormularioEvolucaoCrianca) movForm.getObjMovimentado();
		FormularioEvolucaoCriancaDao dao = getDAO(FormularioEvolucaoCriancaDao.class, movForm);
		Collection<FormularioTurma> findByExactField = dao.findByExactField(FormularioTurma.class, "formulario", f.getId());
		
		
		FormularioTurma ft = findByExactField.iterator().next();
		FormularioEvolucaoCrianca fBD = dao.findByTurma(ft.getTurma().getId(), f.getComponente().getId());
		
		List<ItemInfantilFormulario> itens = new ArrayList<ItemInfantilFormulario>();
		
		if (fBD != null)
			itens = fBD.getItens();
		
		List<ItemInfantilFormulario> subtract = (List<ItemInfantilFormulario>) CollectionUtils.subtract(itens, f.getItens());
			
		if (subtract != null && !subtract.isEmpty()) {
			for (ItemInfantilFormulario item : subtract) {
				dao.updateField(ItemInfantil.class, item.getItem().getId(), "ativo", Boolean.FALSE);
			}
		}
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}
