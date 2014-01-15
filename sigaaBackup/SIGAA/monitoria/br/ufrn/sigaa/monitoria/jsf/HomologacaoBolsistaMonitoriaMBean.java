/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/12/2009
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.sigaa.SipacPapeis;
import br.ufrn.bolsas.negocio.IntegracaoBolsas;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.dto.InclusaoBolsaAcademicaDTO;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.bolsas.negocio.MovimentoBolsaAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.TipoVinculoDiscenteMonitoria;
import br.ufrn.sigaa.projetos.dominio.Edital;

/***
 * Mbean responsável por controlar a integração de bolsistas de monitoria entre o sipac e o sigaa. 
 * Atua na solicitação do cadastro de bolsas no sipac.
 * 
 * @author Igor Linnik
 * 
 */
@Component("homologacaoBolsistaMonitoria") @Scope("session")
public class HomologacaoBolsistaMonitoriaMBean extends SigaaAbstractController<DiscenteMonitoria> {

    /** Coleção de discentes de monitoria bolsistas no SIGAA. */
    private Collection<DiscenteMonitoria> discentesBolsistasSigaa = new ArrayList<DiscenteMonitoria>();

    /** Edital da Bolsa. */
    private Edital edital = new Edital();

    /** Construtor padrão. */
    public HomologacaoBolsistaMonitoriaMBean() {		
    }
    
    
    public String iniciarHomologacaoBolsas() {
    	
    	if( ! Sistema.isSipacAtivo() ) {
    		addMensagem(MensagensArquitetura.SISTEMA_DESATIVADO, RepositorioDadosInstitucionais.get("siglaSipac"));
    		return null;
    	}
    	
    	return forward(ConstantesNavegacaoMonitoria.DISCENTEMONITORIA_HOMOLOGACAO_BOLSA);
    }

    /**
     * Busca todos os discentes de monitoria ativos que receberam bolsas
     * para solicitar o cadastro da bolsa no SIPAC. 
     * 
     * 
     * @throws ArqException 
     * 
     */
    public String buscar() throws ArqException {
	
    	discentesBolsistasSigaa.clear();
	
    	if ((edital == null) || (edital.getId() == 0)) {	    
    		addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Edital");	    
    		return redirectMesmaPagina();				
    	}
	
    	DiscenteMonitoriaDao dao = getDAO(DiscenteMonitoriaDao.class);	
    	
    	try {	    
    		//Todos os bolsistas de monitoria cadastrados no SIGAA	    
    		discentesBolsistasSigaa = dao.findByEdital(edital.getId(), TipoVinculoDiscenteMonitoria.BOLSISTA, SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA);

    		//Lista dos discentes de monitoria que já solicitaram bolsas em lote via sigaa	    
    		Collection<Integer> solicitacoesSincronizadasSipac = dao.findDiscentesBolsasSolicitadasSipac();

    		//Lista das solicitações já cadastradas no SIPAC	
    		//Solicitações de inclusão
    		//Inclusão que não foram excluídas
    		//Que foram enviadas ou atendidas e não finalizada.    		
    		Collection<Long> solicitacoesSipac = new ArrayList<Long>();
	    	int[] idTiposBolsasMonitoriaSIPAC = ParametroHelper.getInstance().getParametroIntArray(ConstantesParametro.LISTA_BOLSAS_MONITORIA);	    	
	    	for (int i = 0; i < idTiposBolsasMonitoriaSIPAC.length; i++) {
	    	    solicitacoesSipac.addAll(IntegracaoBolsas.verificarSolicitacaoBolsaSIPAC(null, idTiposBolsasMonitoriaSIPAC[i]));    
		}
	    
    		//Removendo discentes que já solicitaram cadastros no sipac	    
    		//Solicitações de inclusão
    		//Inclusão que não foram excluídas
    		//Que foram enviadas ou atendidas e não finalizada.
    		for (Iterator<DiscenteMonitoria> it = discentesBolsistasSigaa.iterator(); it.hasNext();) {
    			DiscenteMonitoria d = it.next();
    			if ((solicitacoesSincronizadasSipac.contains(d.getId())) || (solicitacoesSipac.contains(d.getDiscente().getMatricula())) ) {	    		
    				it.remove();	    	
    			} else {	    		
    				d.setSelecionado(true); //marcando o discente para sincronizar por padrão(Aparece selecionado na tela de busca).	    	
    			}	    
    		}
	
    	}catch (LimiteResultadosException e) {	    
    		addMensagemErro(e.getMessage());	
    	} catch (DAOException e) {	 
    		notifyError(e);
    	}
	
    	return redirectMesmaPagina();    
    }

    
    /**
     * Realiza a chamada de inclusão de bolsas no sipac.
     *
     * 
     * @return
     * @throws ArqException 
     * @throws NegocioException 
     */
    
    public String homologarBolsas() throws ArqException {
	checkRole(SipacPapeis.GESTOR_BOLSAS_LOCAL);

	Collection<InclusaoBolsaAcademicaDTO> solicitacoesCadastro = new ArrayList<InclusaoBolsaAcademicaDTO>();
	int idTipoBolsaMonitoriaSIPAC = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.BOLSA_MONITORIA);
	
	//Gerando solicitações para envio ao SIPAC
	for (DiscenteMonitoria dm : discentesBolsistasSigaa) {

	    if (dm.isSelecionado()) {
		
		InclusaoBolsaAcademicaDTO solicitacao = new InclusaoBolsaAcademicaDTO();
		solicitacao.setAgenciaBancaria(dm.getAgencia());
		solicitacao.setCodigoBanco(dm.getBanco().getCodigo());		
		
		
		solicitacao.setDataInicio(dm.getDataInicio());		
		solicitacao.setDataFim(dm.getDataFim());
		
		solicitacao.setIdCurso(dm.getDiscente().getCurso().getId());
		solicitacao.setIdPessoa(dm.getDiscente().getPessoa().getId());
		solicitacao.setIdRegistroEntrada(getUsuarioLogado().getRegistroEntrada().getId());
		solicitacao.setIdUnidadeLocalTrabalho(dm.getProjetoEnsino().getUnidade().getId());
		solicitacao.setIdUnidadeResponsavel(Unidade.BOLSAS_MONITORIA);
		solicitacao.setIdUsuarioCadastro(getUsuarioLogado().getId());
		solicitacao.setJustificativa("Homologação via SIGAA.");
		solicitacao.setMatricula(dm.getDiscente().getMatricula());
		solicitacao.setObservacao("Solicitação de cadastro de bolsa realizada via SIGAA por " + getUsuarioLogado().getNome() + " em " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + ".");
		solicitacao.setNumeroContaBancaria(dm.getConta());
		solicitacao.setOperacaoContaBancaria(dm.getOperacao());
		solicitacao.setNivel(String.valueOf(dm.getDiscente().getNivel()));
		solicitacao.setTipoBolsa(idTipoBolsaMonitoriaSIPAC);
		solicitacao.setIdDiscenteProjeto(dm.getId());
		
		solicitacoesCadastro.add(solicitacao);
	    }
	}

	try {

	    prepareMovimento(ArqListaComando.SOLICITAR_INCLUSAO_BOLSA_ACADEMICA);
	    MovimentoBolsaAcademica mov = new MovimentoBolsaAcademica();
	    mov.setCodMovimento(ArqListaComando.SOLICITAR_INCLUSAO_BOLSA_ACADEMICA);
	    mov.setSolicitacoes(solicitacoesCadastro);
	    execute(mov);
	    addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);

	    //Atualiza a tela de busca
	    buscar();

	} catch (NegocioException e) {
	    notifyError(e);
	    addMensagemErro(e.getMessage());
	}		

	return redirectMesmaPagina();		
    }
	

    /** Retorna o Edital da Bolsa. 
     * @return
     */
    public Edital getEdital() {
	return edital;
    }

    /** Seta o Edital da Bolsa.
     * @param edital
     */
    public void setEdital(Edital edital) {
	this.edital = edital;
    }

    /** Retorna uma coleção de discentes bolsistas no SIGAA. 
     * @return
     */
    public Collection<DiscenteMonitoria> getDiscentesBolsistasSigaa() {
	return discentesBolsistasSigaa;
    }

    /** Seta uma coleção de discentes bolsistas no SIGAA.
     * @param discentesBolsistasSigaa
     */
    public void setDiscentesBolsistasSigaa(
	    Collection<DiscenteMonitoria> discentesBolsistasSigaa) {
	this.discentesBolsistasSigaa = discentesBolsistasSigaa;
    }

}
