/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/06/2007
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.MovimentacaoCota;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.TipoVinculoDiscenteMonitoria;

/**
 * 
 * Responsável por controlar a movimentação de cotas de bolsas entre projetos de monitoria.
 * 
 * @author Victor Hugo
 * @author Ilueny Santos
 *
 */
@Component("movimentacaoCotasMonitoria")
@Scope("request")
public class MovimentacaoCotaMBean extends SigaaAbstractController<MovimentacaoCota> {

	/**
	 * Quantitativos do edital
	 */
	private int total, qtdBolsistas, qtdVoluntarios, qtdBolsasConcedidas;
	
	/**
	 * Total de ativos no projeto 
	 */
	private int totalBolsistasProjeto, totalVoluntariosProjeto;
	
	
	public MovimentacaoCotaMBean() {
		this.obj = new MovimentacaoCota();
	}
	
	/**
	 * Inicia o caso de uso de movimentar cotas entre projetos
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war\monitoria\index.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException{
		checkRole(SigaaPapeis.GESTOR_MONITORIA, SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
		return forward(ConstantesNavegacaoMonitoria.MOVIMENTAR_COTA_LISTA);
	}

	/**
	 * Exibe as informações do projeto e o formulário para movimentação de cotas
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul> 
	 * 	<li>sigaa.war\projetos\AlteracaoProjeto\lista_vinculados.jsp</li>
	 * 	<li>sigaa.war\monitoria\MovimentacaoCota\lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String selecionarProjeto() throws ArqException{

		prepareMovimento( SigaaListaComando.MOVIMENTAR_COTAS_MONITORIA);		
		DiscenteMonitoriaDao dao = getDAO( DiscenteMonitoriaDao.class );
		int id = getParameterInt("id");
		ProjetoEnsino pm =  dao.findByPrimaryKey(id, ProjetoEnsino.class);		
		if( pm == null || pm.getId() == 0 ){
			addMensagemErro("Projeto não pôde ser localizado. Contacte administração do sistema.");
			return null;
		}
		
		obj.setProjetoEnsino(pm);		
		if (pm.getProjeto().isInterno()) {
			qtdBolsistas = dao.countMonitoresByEdital(pm.getEditalMonitoria().getId(), TipoVinculoDiscenteMonitoria.BOLSISTA);
			qtdVoluntarios = dao.countMonitoresByEdital(pm.getEditalMonitoria().getId(), TipoVinculoDiscenteMonitoria.NAO_REMUNERADO);
			total = dao.countMonitoresByProjetoOuProvaOuEdital(null, null, pm.getEditalMonitoria().getId(), new Integer[] {TipoVinculoDiscenteMonitoria.BOLSISTA, TipoVinculoDiscenteMonitoria.NAO_REMUNERADO});
			qtdBolsasConcedidas = dao.countBolsasConcedidasByEdital(pm.getEditalMonitoria().getId());
		} else {
			qtdBolsistas = dao.countMonitoresByProjeto(pm.getId(), TipoVinculoDiscenteMonitoria.BOLSISTA);
			qtdVoluntarios = dao.countMonitoresByProjeto(pm.getId(), TipoVinculoDiscenteMonitoria.NAO_REMUNERADO);
			total = qtdBolsistas + qtdVoluntarios;;
			qtdBolsasConcedidas = pm.getBolsasConcedidas();
		}
		totalBolsistasProjeto = dao.countMonitoresByProjeto(pm.getId(), TipoVinculoDiscenteMonitoria.BOLSISTA);
		totalVoluntariosProjeto = dao.countMonitoresByProjeto(pm.getId(), TipoVinculoDiscenteMonitoria.NAO_REMUNERADO);
		
		return forward(ConstantesNavegacaoMonitoria.MOVIMENTAR_COTA_FORM);		
		
	}
	
	/**
	 * Realiza movimentação de cota
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war\monitoria\MovimentacaoCota\form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		
		try {
		    MovimentoCadastro mov = new MovimentoCadastro();
		    mov.setObjMovimentado(obj);
		    mov.setCodMovimento( SigaaListaComando.MOVIMENTAR_COTAS_MONITORIA );
		    execute(mov, getCurrentRequest());
		    addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		    //Atualiza busca
		    ((ProjetoMonitoriaMBean)getMBean("projetoMonitoria")).localizar();
		    return iniciar();
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return null;
		}
	}

	
	public int getQtdBolsistas() {
		return qtdBolsistas;
	}

	public void setQtdBolsistas(int qtdBolsistas) {
		this.qtdBolsistas = qtdBolsistas;
	}

	public int getQtdVoluntarios() {
		return qtdVoluntarios;
	}

	public void setQtdVoluntarios(int qtdVoluntarios) {
		this.qtdVoluntarios = qtdVoluntarios;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getTotalBolsistasProjeto() {
		return totalBolsistasProjeto;
	}

	public void setTotalBolsistasProjeto(int totalBolsistasProjeto) {
		this.totalBolsistasProjeto = totalBolsistasProjeto;
	}

	public int getTotalVoluntariosProjeto() {
		return totalVoluntariosProjeto;
	}

	public void setTotalVoluntariosProjeto(int totalVoluntariosProjeto) {
		this.totalVoluntariosProjeto = totalVoluntariosProjeto;
	}

	public int getQtdBolsasConcedidas() {
		return qtdBolsasConcedidas;
	}

	public void setQtdBolsasConcedidas(int qtdBolsasConcedidas) {
		this.qtdBolsasConcedidas = qtdBolsasConcedidas;
	}

}
