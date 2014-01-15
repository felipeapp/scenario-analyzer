/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/12/2007
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.extensao.DiscenteExtensaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.monitoria.dominio.MovimentacaoCota;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;

/*******************************************************************************
 * MBean responsável por controlar operações relativas a movimentação de cotas
 * de bolsas entre propostas de ações de extensão aprovadas.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Component("movimentacaoCotasExtensao")
@Scope("request")
public class MovimentacaoCotaMBean extends SigaaAbstractController<MovimentacaoCota> {

	/**
	 * Total de ativos na ação de extensão 
	 */
	private int totalBolsistasAtividade, totalVoluntariosAtividade, totalDiscentesAtividade;
	
	public MovimentacaoCotaMBean() {
		this.obj = new MovimentacaoCota();
	}

	/**
	 * Inicia o caso de uso de movimentar cotas entre projetos
	 * <br />
	 * Chamado pela(s) jsp(s): 
	 * <ul>
	 * <li>sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException {
	    checkRole(SigaaPapeis.GESTOR_MONITORIA, SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
	    return forward(ConstantesNavegacao.MOVIMENTAR_COTA_LISTA);
	}

	/**
	 * Exibe as informações do projeto e o formulário para movimentação de cotas
	 * <br />
	 * Chamado pela(s) jsp(s):
	 * <ul>	
	 *  <li>sigaa.war/extensao/AlterarAtividade/lista.jsp</li>
	 * 	<li>sigaa.war/extensao/MovimentacaoCota/lista.jsp</li>
	 * 	<li>/sigaa.war/projetos/AlteracaoProjeto/lista_vinculados.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String selecionarAtividade() throws ArqException {

		prepareMovimento(SigaaListaComando.MOVIMENTAR_COTAS_EXTENSAO);
		DiscenteExtensaoDao dao = getDAO(DiscenteExtensaoDao.class);
		int id = getParameterInt("id");
		AtividadeExtensao acaoExtensao = dao.findByPrimaryKey(id, AtividadeExtensao.class);

		if (acaoExtensao == null || acaoExtensao.getId() == 0) {
			addMensagemErro("Ação não pôde ser localizada. Contacte administração do sistema.");
			return null;
		}

		obj.setAcaoExtensao(acaoExtensao);
		totalBolsistasAtividade = dao.countBolsistaByAtividadeOuVinculos(acaoExtensao.getId(), new Integer[] {TipoVinculoDiscente.EXTENSAO_BOLSISTA_INTERNO,TipoVinculoDiscente.EXTENSAO_BOLSISTA_EXTENO});
 		totalVoluntariosAtividade = dao.countBolsistaByAtividadeOuVinculos(acaoExtensao.getId(), new Integer[] {TipoVinculoDiscente.EXTENSAO_VOLUNTARIO});
 		totalDiscentesAtividade = dao.countBolsistaByAtividadeOuVinculos(acaoExtensao.getId(), new Integer[] {TipoVinculoDiscente.EXTENSAO_BOLSISTA_INTERNO, TipoVinculoDiscente.EXTENSAO_BOLSISTA_EXTENO, TipoVinculoDiscente.EXTENSAO_VOLUNTARIO});
		return forward(ConstantesNavegacao.MOVIMENTAR_COTA_FORM);

	}

	/**
	 * Cadastra movimentação entre bolsas.
	 * <br /><br />
	 * Chamado pela(s) jsp(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/MovimentacaoCota/form.jsp</li>
	 *  </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
	    	checkRole(SigaaPapeis.GESTOR_MONITORIA, SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
	    	try {
        
        		MovimentoCadastro mov = new MovimentoCadastro();
        		mov.setObjMovimentado(obj);
        		mov.setCodMovimento(SigaaListaComando.MOVIMENTAR_COTAS_EXTENSAO);
        		execute(mov, getCurrentRequest());
        		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
        		obj = new MovimentacaoCota();
        		return iniciar();
	    	} catch (NegocioException e) {
	    	    addMensagens(e.getListaMensagens());
	    	    return null;
	    	}
	}
	
	@Override
	public String cancelar() {
		return redirect(getListPage());
	}

	public int getTotalVoluntariosAtividade() {
		return totalVoluntariosAtividade;
	}

	public void setTotalVoluntariosAtividade(int totalVoluntariosAtividade) {
		this.totalVoluntariosAtividade = totalVoluntariosAtividade;
	}

	public int getTotalDiscentesAtividade() {
		return totalDiscentesAtividade;
	}

	public void setTotalDiscentesAtividade(int totalDiscentesAtividade) {
		this.totalDiscentesAtividade = totalDiscentesAtividade;
	}

	public int getTotalBolsistasAtividade() {
		return totalBolsistasAtividade;
	}

	public void setTotalBolsistasAtividade(int totalBolsistasAtividade) {
		this.totalBolsistasAtividade = totalBolsistasAtividade;
	}

}
