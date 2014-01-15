/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 26/10/2010
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.TipoBolsaPesquisaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;

/**
 * 
 * Controlador para gerenciar a operação de finalização de planos de trabalho de pesquisa.
 * 
 * @author Thalisson Muriel
 *
 */
@Component("finalizarPlanoTrabalho") @Scope("session")
public class FinalizarPlanoTrabalhoMBean extends SigaaAbstractController<PlanoTrabalho> {
	
	/** Responsável por armazenar os Planos de Trabalho. **/
	private Collection<PlanoTrabalho> planos;
	
	/**
	 * Responsável por iniciar o processo de finalização de planos de trabalho sem cota.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/menu/iniciacao.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		checkChangeRole();
		obj = new PlanoTrabalho();
		obj.setTipoBolsa(new TipoBolsaPesquisa());
		erros = new ListaMensagens();
		prepareMovimento(SigaaListaComando.FINALIZAR_PLANOS_TRABALHO_SEM_COTA);
		setOperacaoAtiva(SigaaListaComando.FINALIZAR_PLANOS_TRABALHO_SEM_COTA.getId());
		return forward(getFormPage());
	}
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
	}
	
	@Override
	public String getDirBase() {
		return "/pesquisa/FinalizarPlanoTrabalho";
	}
	
	/**
	 * Retorna uma coleção de Tipos de Bolsa de Pesquisa não vínculadas à cotas.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/FinalizarPlanoTrabalho/form.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getTiposBolsa() throws ArqException{
		TipoBolsaPesquisaDao dao = getDAO(TipoBolsaPesquisaDao.class);
		return toSelectItems(dao.findBolsasSemCota(), "id", "descricaoResumida");
	}
	
	/**
	 * Responsável por buscar todos os planos de trabalho sem cota.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/FinalizarPlanoTrabalho/form.jsp</li>
	 *	</ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String buscarPlanos() throws DAOException{
		if(!isOperacaoAtiva(SigaaListaComando.FINALIZAR_PLANOS_TRABALHO_SEM_COTA.getId())){
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return cancelar();
		}
		
		ValidatorUtil.validateRequiredId(obj.getTipoBolsa().getId(), "Tipo da Bolsa", erros);
		if (!erros.isEmpty()) {
			addMensagens(erros);
			return null;
		}
	
		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class);
		planos = planoDao.findPlanosByTipoBolsa(obj.getTipoBolsa().getId());
		
		if ( planos.isEmpty() ) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		return forward(getListPage());
	}
	
	/**
	 * Responsável por finalizar, pelo Gestor de Pesquisa, os Planos de Trabalho sem cota.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/pesquisa/FinalizarPlanoTrabalho/lista.jsp</li>
	 *	</ul>
	 *
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String confirmar() throws NegocioException, ArqException{
		if(!isOperacaoAtiva(SigaaListaComando.FINALIZAR_PLANOS_TRABALHO_SEM_COTA.getId())){
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return cancelar();
		}
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setColObjMovimentado(planos);
		mov.setCodMovimento(getUltimoComando());
		
		execute(mov);
		
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		
		return iniciar();
	}

	public Collection<PlanoTrabalho> getPlanos() {
		return planos;
	}

	public void setPlanos(Collection<PlanoTrabalho> planos) {
		this.planos = planos;
	}
	
}