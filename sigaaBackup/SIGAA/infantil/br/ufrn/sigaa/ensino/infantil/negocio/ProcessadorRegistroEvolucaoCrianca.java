/**
 * 
 */
package br.ufrn.sigaa.ensino.infantil.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.infantil.dominio.FormularioEvolucaoCrianca;
import br.ufrn.sigaa.ensino.infantil.dominio.ItemInfantilFormulario;
import br.ufrn.sigaa.ensino.infantil.dominio.ItemInfantilPeriodo;

/**
 * Processador responsável por persistir as informações do registro de evolução da criança
 * no ensino infantil
 * 
 * @author Leonardo Campos
 * @author Jean Guerethes
 */
public class ProcessadorRegistroEvolucaoCrianca extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		validate(mov);
		MovimentoCadastro movCad = (MovimentoCadastro) mov;
		
		if(mov.getCodMovimento().equals(SigaaListaComando.REGISTRAR_EVOLUCAO_CRIANCA)){
			gerenciarPreenchimento(movCad);
		}
		
		return null;
	}

	private void gerenciarPreenchimento(MovimentoCadastro mov) throws DAOException {
		
		FormularioEvolucaoCrianca formulario = (FormularioEvolucaoCrianca) mov.getObjAuxiliar();
		MatriculaComponente mc = mov.getObjMovimentado();
		GenericDAOImpl dao = getDAO(GenericDAOImpl.class, mov);
		Collection<ItemInfantilPeriodo> itens = new ArrayList<ItemInfantilPeriodo>();
		
		try {
			ItemInfantilFormulario itemFormulario = null;
			for (ItemInfantilFormulario itemInfForm : formulario.getItens()) {
				
				if ( itemInfForm.getPeriodo() == formulario.getPeriodo() ) {
					//Verifica se deve também persistir o conteúdo, somente persitido se houver Observação
					if ( itemInfForm.isConteudo() && itemInfForm.getItem().getFormaAvaliacao() != null ) {
						itemFormulario = UFRNUtils.deepCopy(itemInfForm);
					}
					
					if ( itemInfForm.isObjetivo() ) {
						ItemInfantilPeriodo item = new ItemInfantilPeriodo();
						item.setId( itemInfForm.getItemPeriodo().getId() );
						item.setItemFormulario( itemInfForm );
						item.setMatricula( mc );
						item.setResultado( itemInfForm.getItemPeriodo().getResultado() );
						item.setPeriodo( itemInfForm.getPeriodo() );
						
						// Se houver Conteúdo vínculado a esse objetivo o mesmo também deve ser persistido
						if ( nextIsObjOrSuper(mov, itemInfForm) && !isEmpty(itemFormulario) ) {
							ItemInfantilPeriodo itemConteudo = new ItemInfantilPeriodo();
							itemConteudo.setId( itemFormulario.getItemPeriodo().getId() );
							itemConteudo.setItemFormulario( itemFormulario );
							itemConteudo.setMatricula(mc);
							itemConteudo.setResultado(null);
							itemConteudo.setPeriodo( itemInfForm.getPeriodo() );
							itemConteudo.setObservacoes( itemInfForm.getItemPeriodo().getObservacoes() );
							itens.add(itemConteudo);
							itemFormulario = null;
						}
						
						if ( adicionarItem(item) )
							itens.add(item);
					}
				}
			}
			
			dao.getHibernateTemplate().saveOrUpdateAll(itens);
			
		} finally {
			dao.close();
		}
	}
	
	private boolean adicionarItem(ItemInfantilPeriodo item) {
		return item.getResultado() != null;
	}

	private boolean nextIsObjOrSuper(MovimentoCadastro mov, ItemInfantilFormulario item){
		try {
			FormularioEvolucaoCrianca formulario = (FormularioEvolucaoCrianca) mov.getObjAuxiliar();
			for (int i = 0; i < formulario.getItens().size(); i++) {
				if ( item.getId() == formulario.getItens().get(i).getId() && 
						formulario.getItens().get(i+1).getProfundidade() > ItemInfantilFormulario.CONTEUDO )
					return false;
			}
		} catch (Exception e) {
			return true;
		}
		
		return true;
	}
	
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}
	
}