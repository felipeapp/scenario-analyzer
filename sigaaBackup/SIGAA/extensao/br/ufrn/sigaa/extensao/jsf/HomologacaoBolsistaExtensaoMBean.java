/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/04/2009
 *
 */
package br.ufrn.sigaa.extensao.jsf;

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
import br.ufrn.sigaa.arq.dao.extensao.DiscenteExtensaoDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.bolsas.negocio.MovimentoBolsaAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;

/***
 * Mbean responsável por controlar a integração de bolsistas de extensão 
 * entre o sipac e o sigaa. A integração se dá por uma solicitação de um serviço
 * ao SIPAC, que pode ser uma solicitação de inclusão ou exclusão de bolsas, por exemplo. 
 * Atua na solicitação do cadastro de bolsas.
 * 
 * @author Ilueny Santos
 * 
 */
@Component("homologacaoBolsistaExtensao") @Scope("session")
public class HomologacaoBolsistaExtensaoMBean extends SigaaAbstractController<DiscenteExtensao> {

    /** Coleção de discentes bolsistas no SIGAA. */
    private Collection<DiscenteExtensao> discentesBolsistasSigaa = new ArrayList<DiscenteExtensao>();

    /** Edital da Bolsa. */
    private Edital edital = new Edital();

    /** Construtor padrão. */
    public HomologacaoBolsistaExtensaoMBean() {		
    }

    
    
    /**
     * Redireciona para a página de homologação de bolsas caso o SIPAC esteja ativo.
     * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/menu.jsp</li>
	 * </ul>
     *  
     * 
     * @return
     */
    public String iniciarHomologacaoBolsas() {    	
    	
    	if( ! Sistema.isSipacAtivo() ) {
    		addMensagem(MensagensArquitetura.SISTEMA_DESATIVADO, RepositorioDadosInstitucionais.get("siglaSipac"));
    		return null;
    	}
    	
    	return forward(ConstantesNavegacao.INICIAR_HOMOLOGACAO_BOLSAS);
    }
    
    
    /**
     * Busca todos os discentes de extensão ativos que receberam bolsas do faex
     * para solicitar o cadastro da bolsa no SIPAC.
     * 
     * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/extensao/DiscenteExtensao/homologacao_bolsa_discente.jsp</li>
	 * </ul>
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

	DiscenteExtensaoDao dao = getDAO(DiscenteExtensaoDao.class);
	try {

	    //Todos os bolsistas cadastrados no SIGAA
	    discentesBolsistasSigaa = dao.findByEdital(edital.getId(), TipoVinculoDiscente.EXTENSAO_BOLSISTA_INTERNO);

	    //Lista dos discentes que já solicitaram bolsas em lote via sigaa
	    Collection<Integer> solicitacoesSincronizadasSipac = dao.findDiscentesBolsasSolicitadasSipac();

	    //Lista das solicitações já cadastradas no SIPAC
	    Collection<Long> solicitacoesSipac = new ArrayList<Long>();
	    int[] idTiposBolsasExtensaoSIPAC = ParametroHelper.getInstance().getParametroIntArray(ConstantesParametro.LISTA_BOLSAS_EXTENSAO);	    	
	    for (int i = 0; i < idTiposBolsasExtensaoSIPAC.length; i++) {
	        solicitacoesSipac.addAll(IntegracaoBolsas.verificarSolicitacaoBolsaSIPAC(null, idTiposBolsasExtensaoSIPAC[i]));    
	    }	
	    

	    //Removendo discentes que já solicitaram cadastros no sipac
	    for (Iterator<DiscenteExtensao> it = discentesBolsistasSigaa.iterator(); it.hasNext();) {
		DiscenteExtensao d = it.next();				
		if ((solicitacoesSincronizadasSipac.contains(d.getId())) || (solicitacoesSipac.contains(d.getDiscente().getMatricula())) ) { 
		    it.remove();
		} else {
		    d.setSelecionado(true); //marcando o discente para sincronizar por padrão.
		}
	    }

	} catch (LimiteResultadosException e) {
	    addMensagemErro(e.getMessage());
	} catch (DAOException e) {
	    notifyError(e);
	}

	return forward(ConstantesNavegacao.INICIAR_HOMOLOGACAO_BOLSAS);		
    }


    /**
     * Realiza a chamada de inclusão de bolsas no sipac.
     * 
     * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/extensao/DiscenteExtensao/homologacao_bolsa_discente.jsp</li>
	 * </ul>
     * 
     * @return
     * @throws ArqException 
     * @throws NegocioException 
     */
    public String homologarBolsas() throws ArqException {
	checkRole(SipacPapeis.GESTOR_BOLSAS_LOCAL);

	Collection<InclusaoBolsaAcademicaDTO> solicitacoesCadastro = new ArrayList<InclusaoBolsaAcademicaDTO>();
	int idTipoBolsaExtensaoSIPAC = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.BOLSA_EXTENSAO);
	
	//Gerando solicitações para envio ao SIPAC
	for (DiscenteExtensao de : discentesBolsistasSigaa) {

	    if (de.isSelecionado()) {
		
		InclusaoBolsaAcademicaDTO solicitacao = new InclusaoBolsaAcademicaDTO();
		solicitacao.setAgenciaBancaria(de.getAgencia());
		solicitacao.setCodigoBanco(de.getBanco().getCodigo());
		solicitacao.setDataInicio(de.getDataInicio());
		solicitacao.setDataFim(de.getDataFim());

		solicitacao.setIdCurso(de.getDiscente().getCurso().getId());
		solicitacao.setIdPessoa(de.getDiscente().getPessoa().getId());
		solicitacao.setIdRegistroEntrada(getUsuarioLogado().getRegistroEntrada().getId());
		solicitacao.setIdUnidadeLocalTrabalho(de.getPlanoTrabalhoExtensao().getAtividade().getUnidade().getId());
		solicitacao.setIdUnidadeResponsavel(Unidade.BOLSAS_EXTENSAO);
		solicitacao.setIdUsuarioCadastro(getUsuarioLogado().getId());
		solicitacao.setJustificativa("Homologação via SIGAA.");
		solicitacao.setMatricula(de.getDiscente().getMatricula());
		solicitacao.setObservacao("Solicitação de cadastro de bolsa realizada via SIGAA por " + getUsuarioLogado().getNome() + " em " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + ".");
		solicitacao.setNumeroContaBancaria(de.getConta());
		solicitacao.setOperacaoContaBancaria(de.getOperacao());
		solicitacao.setNivel(String.valueOf(de.getDiscente().getNivel()));
		solicitacao.setTipoBolsa(idTipoBolsaExtensaoSIPAC);
		solicitacao.setIdDiscenteProjeto(de.getId());
		
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
    public Collection<DiscenteExtensao> getDiscentesBolsistasSigaa() {
	return discentesBolsistasSigaa;
    }

    /** Seta uma coleção de discentes bolsistas no SIGAA.
     * @param discentesBolsistasSigaa
     */
    public void setDiscentesBolsistasSigaa(
	    Collection<DiscenteExtensao> discentesBolsistasSigaa) {
	this.discentesBolsistasSigaa = discentesBolsistasSigaa;
    }

}
