/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '24/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.HashSet;

import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

public class EntidadeFinanciadoraMBean extends SigaaAbstractController<EntidadeFinanciadora> {
	
	public EntidadeFinanciadoraMBean() {
		obj = new EntidadeFinanciadora();
	}
		
	@Override
	public String getFormPage() {
		return "/administracao/cadastro/EntidadeFinanciadora/form.jsf";
	}
	
	@Override
	public String getListPage() {
		return "/administracao/cadastro/EntidadeFinanciadora/lista.jsf";
	}
	
	@Override
	protected void afterCadastrar() {
		obj = new EntidadeFinanciadora();
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole( new int[] { SigaaPapeis.ADMINISTRADOR_SIGAA, SigaaPapeis.GESTOR_PESQUISA });
	}

	public Collection<SelectItem> getAllCombo() {
		return getAllAtivo(EntidadeFinanciadora.class, "id", "nome");
	}
	
	@Override
	public void beforeRemover() throws DAOException {
		try {
			prepareMovimento(ArqListaComando.REMOVER);
			setOperacaoAtiva(ArqListaComando.REMOVER.getId());			
			setId();
			obj = getGenericDAO().findByPrimaryKey(obj.getId(), obj.getClass());
			
			if (obj == null) {
				obj = new EntidadeFinanciadora();
				obj.setId(0);
			}
			
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}		
	}
	
	@Override
	public void afterRemover() {
//		removeOperacaoAtiva();
	}
	

	
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		 
		Collection<EntidadeFinanciadora> todosNoBanco = getGenericDAO().findAll(EntidadeFinanciadora.class);
		HashSet<EntidadeFinanciadora> entidadesNoBanco = new HashSet<EntidadeFinanciadora>();
		
		for(EntidadeFinanciadora entidade : todosNoBanco)
			entidadesNoBanco.add(entidade);				
		
		//necessário para evitar erro com o equals
		if( getUltimoComando().getId() == ArqListaComando.ALTERAR.getId() ) { 
			for(EntidadeFinanciadora entid : entidadesNoBanco) {
				if( entid.getNome().equals(obj.getNome()) && !isEmpty( entid.getGrupoEntidadeFinanciadora() ) 
					&& entid.getGrupoEntidadeFinanciadora().getId() == obj.getGrupoEntidadeFinanciadora().getId()
					&& entid.getSigla().equals(obj.getSigla()) && !isEmpty(entid.getClassificacaoFinanciadora())
					&& entid.getClassificacaoFinanciadora().getId() == obj.getClassificacaoFinanciadora().getId()
					&& entid.getPais().getId() == obj.getPais().getId()) 
					if( entid.getUnidadeFederativa() == null && obj.getUnidadeFederativa().getId()!=0 )
						return super.cadastrar();
					else
						if(entid.getUnidadeFederativa()!=null)
							if(entid.getUnidadeFederativa().getId()==obj.getUnidadeFederativa().getId()){
								addMensagemErro("Entidade já cadastrada no sistema");
								return null;
							}				
			}	
			return super.cadastrar();
		}
		
		if(entidadesNoBanco.contains(obj))
		{
			addMensagemErro("Entidade já cadastrada no sistema");
			return null;			
		}
		
		return super.cadastrar();
	}
}