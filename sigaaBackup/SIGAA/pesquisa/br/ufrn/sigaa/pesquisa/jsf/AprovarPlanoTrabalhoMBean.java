/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 25/08/2009
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;

/**
 * 
 * Controlador para gerenciar a operação de aprovação de planos de trabalho de pesquisa
 * pelo gestor do módulo de pesquisa.
 * 
 * @author Geyson
 *
 */
@Component("aprovarPlanoTrabalho")
@Scope("request")
public class AprovarPlanoTrabalhoMBean extends SigaaAbstractController<PlanoTrabalho> {
	
	Collection<PlanoTrabalho> planosSelecionados = new ArrayList<PlanoTrabalho>();
	Collection<PlanoTrabalho> lista = new ArrayList<PlanoTrabalho>();
	
	public AprovarPlanoTrabalhoMBean(){
		obj = new PlanoTrabalho();
		planosSelecionados = new ArrayList<PlanoTrabalho>();
	}
	
	/**
	 * Redireciona para a página com a lista de Planos de Trabalhos com status CORRIGIDO. 
	 * JSP: sigaa.war/pesquisa/menu.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciarBusca() throws ArqException{
		checkListRole();
		prepareMovimento(SigaaListaComando.APROVAR_PLANO_TRABALHO_CORRIGIDO);
		setOperacaoAtiva(SigaaListaComando.APROVAR_PLANO_TRABALHO_CORRIGIDO.getId());
		return forward("/pesquisa/aprovar_plano_de_trabalho/form.jsp");
	}
	
	/**
	 * Sobrescreve comportamento para buscar Planos de Trabalhos CORRIGIDOS.
	 * JSP: sigaa.war/pesquisa/aprovar_plano_de_trabalho/form.jsp 
	 */
	@Override
	public Collection<PlanoTrabalho> getAll() throws ArqException {
		PlanoTrabalhoDao dao = getDAO(PlanoTrabalhoDao.class);
		lista = dao.findByStatusCorrigido();
		
		setResultadosBusca(lista);
		
		if(lista == null){
			return null;
		}
		else
			return lista;
	}
	
	/**
	 * Define como APROVADO o status dos planos de trabalhos selecionados.
	 * JSP: sigaa.war/pesquisa/aprovar_plano_de_trabalho/form.jsp 
	 * @return
	 * @throws ArqException
	 */
	public String aprovar() throws ArqException{
			setId();
			
			if(!checkOperacaoAtiva(SigaaListaComando.APROVAR_PLANO_TRABALHO_CORRIGIDO.getId())){
				return forward("/pesquisa/menu.jsp");
			}
			
			MovimentoCadastro mov = new MovimentoCadastro();	
			for (PlanoTrabalho plano : lista) {
				if(plano.isSelecionado()){
					planosSelecionados.add(plano);	
					
				}
			}
			if(planosSelecionados.size() <= 0){
				addMensagemWarning("Selecione algum Plano de Trabalho para Aprovação.");
				return null;
			}
			if(!(planosSelecionados == null))
				mov.setCodMovimento(SigaaListaComando.APROVAR_PLANO_TRABALHO_CORRIGIDO);
			
			
			mov.setObjMovimentado(obj);
			mov.setColObjMovimentado(planosSelecionados);
			try {
				execute(mov);
				removeOperacaoAtiva();
			} catch (NegocioException e) {
				return tratamentoErroPadrao(e);
			} 
			addMensagem(OPERACAO_SUCESSO);
			return forward("/pesquisa/menu.jsp");
			
	}
	
	/**
	 * Esse método tem como finalidade a visualização do plano de trabalho
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/aprovar_plano_de_trabalho/form.jsp
	 */
	public String visualizarPlano(){
		int id = getParameterInt("id");
		return redirect("/pesquisa/planoTrabalho/wizard.do?dispatch=view&obj.id="+id);
	}
	
}