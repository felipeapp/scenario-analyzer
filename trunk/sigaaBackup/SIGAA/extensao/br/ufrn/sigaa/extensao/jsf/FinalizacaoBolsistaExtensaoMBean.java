/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/11/2009
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import br.ufrn.sigaa.extensao.dominio.TipoSituacaoDiscenteExtensao;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;

/***
 * Mbean responsável por controlar a integração de bolsistas de extensão 
 * entre o sipac e o sigaa. 
 * Atua na Finalização de bolsistas no SIPAC.
 * 
 * @author Ilueny Santos
 * 
 */
@Component("finalizacaoBolsistaExtensao") @Scope("session")
public class FinalizacaoBolsistaExtensaoMBean extends SigaaAbstractController<DiscenteExtensao> {

    /** Coleção de discentes bolsistas finalizados no SIGAA. */
    private Collection<DiscenteExtensao> bolsistasFinalizadosSigaa = new ArrayList<DiscenteExtensao>();

    /** Construtor padrão. */
    public FinalizacaoBolsistaExtensaoMBean() {		
    }

    /**
     * Busca todos os discentes de extensão ativos que receberam bolsas do faex
     * para solicitar o cadastro da bolsa no SIPAC.
     * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/extensao/DiscenteExtensao/finalizar_bolsa_discente.jsp</li>
	 * </ul>
	 *  
     * @throws ArqException 
     */
    public String listarBolsistasFinalizados() throws ArqException {
    	
    	if( ! Sistema.isSipacAtivo() ) {
    		addMensagem(MensagensArquitetura.SISTEMA_DESATIVADO, RepositorioDadosInstitucionais.get("siglaSipac"));
    		return null;
    	}

		bolsistasFinalizadosSigaa.clear();
		DiscenteExtensaoDao dao = getDAO(DiscenteExtensaoDao.class);
		try {
	
		    //Verificando discentes de extensão desligados do SIGAA, mas com bolsas ativas no SIPAC 
		    bolsistasFinalizadosSigaa = dao.findDiscenteExtensaoBySituacao(TipoSituacaoDiscenteExtensao.FINALIZADO, TipoVinculoDiscente.EXTENSAO_BOLSISTA_INTERNO);
		    List<Long> matriculas = new ArrayList<Long>();
		    for(DiscenteExtensao de: bolsistasFinalizadosSigaa) {
			matriculas.add(de.getDiscente().getMatricula());
		    }
	
		    Collection<Long> alunosComBolsasAtivasSipac = IntegracaoBolsas.verificarCadastroBolsaSIPAC(matriculas,new Integer[] {IntegracaoBolsas.TIPO_BOLSA_EXTENSAO});
		    if (alunosComBolsasAtivasSipac == null) {
			bolsistasFinalizadosSigaa.clear();
		    }else {
			for(Iterator<DiscenteExtensao> it = bolsistasFinalizadosSigaa.iterator(); it.hasNext();) {
			    DiscenteExtensao de = it.next();
			    if (!alunosComBolsasAtivasSipac.contains(de.getDiscente().getMatricula())){
				it.remove();
			    } else {
				de.setSelecionado(true); //marcando o discente para sincronizar por padrão.
			    }
			}
		    }
	
		} catch (LimiteResultadosException e) {
		    addMensagemErro(e.getMessage());
		} catch (DAOException e) {
		    notifyError(e);
		}
	
		return forward(ConstantesNavegacao.FINALIZACAO_BOLSISTA);		
    }


    /**
     * Realiza a chamada de inclusão de bolsas no sipac.
     * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/extensao/DiscenteExtensao/finalizacao_bolsa_discente.jsp</li>
	 * </ul>
     *  
     * @return
     * @throws ArqException 
     * @throws NegocioException 
     */
    public String finalizarBolsas() throws ArqException {
	checkRole(SipacPapeis.GESTOR_BOLSAS_LOCAL);
	
	Collection<InclusaoBolsaAcademicaDTO> solicitacoesFinalizacao = new ArrayList<InclusaoBolsaAcademicaDTO>();
	int idTipoBolsaExtensao = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.BOLSA_EXTENSAO);
	
	//Gerando solicitações para envio ao SIPAC
	for (DiscenteExtensao de : bolsistasFinalizadosSigaa) {
		
	    if (de.isSelecionado()) {
	    	
		getGenericDAO().initialize(de);
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
		solicitacao.setJustificativa("Finalização via SIGAA.");
		solicitacao.setMatricula(de.getDiscente().getMatricula());
		solicitacao.setObservacao("Finalização de bolsa realizada via SIGAA por " + getUsuarioLogado().getNome() + " em " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + ".");
		solicitacao.setNumeroContaBancaria(de.getConta());
		solicitacao.setOperacaoContaBancaria(de.getOperacao());
		solicitacao.setNivel(String.valueOf(de.getDiscente().getNivel()));
		solicitacao.setTipoBolsa(idTipoBolsaExtensao);
		solicitacao.setIdDiscenteProjeto(de.getId());
		
		solicitacoesFinalizacao.add(solicitacao);
	    }
	}

	try {

	    prepareMovimento(ArqListaComando.SOLICITAR_EXCLUSAO_BOLSA_ACADEMICA);
	    MovimentoBolsaAcademica mov = new MovimentoBolsaAcademica();
	    mov.setCodMovimento(ArqListaComando.SOLICITAR_EXCLUSAO_BOLSA_ACADEMICA);
	    mov.setSolicitacoes(solicitacoesFinalizacao);
	    execute(mov);
	    addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);

	    //Atualiza a tela de busca
	    listarBolsistasFinalizados();

	} catch (NegocioException e) {
	    notifyError(e);
	    addMensagemErro(e.getMessage());
	}		

	return redirectMesmaPagina();		
    }

    public Collection<DiscenteExtensao> getBolsistasFinalizadosSigaa() {
        return bolsistasFinalizadosSigaa;
    }

    public void setBolsistasFinalizadosSigaa(
    	Collection<DiscenteExtensao> bolsistasFinalizadosSigaa) {
        this.bolsistasFinalizadosSigaa = bolsistasFinalizadosSigaa;
    }

}
