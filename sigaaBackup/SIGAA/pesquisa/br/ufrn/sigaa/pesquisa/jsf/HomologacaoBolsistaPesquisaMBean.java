/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 22/06/2009
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.bolsas.negocio.IntegracaoBolsas;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.dto.InclusaoBolsaAcademicaDTO;
import br.ufrn.sigaa.arq.dao.pesquisa.MembroProjetoDiscenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.bolsas.negocio.MovimentoBolsaAcademica;
import br.ufrn.sigaa.dominio.TipoBolsa;
import br.ufrn.sigaa.mensagens.MensagensPesquisa;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.negocio.MovimentoIndicarBolsista;

/**
 * Controlador responsável por realizar a integração de bolsistas de pesquisa 
 * entre o SIGAA e o SIPAC. Efetua solicitações de cadastro no SIPAC de bolsistas indicados no SIGAA
 * como também encerramento de bolsas no SIPAC dos bolsistas já finalizados no SIGAA.
 * 
 * @author Leonardo Campos
 *
 */
@Component("homologacaoBolsistaPesquisaBean") @Scope("session")
public class HomologacaoBolsistaPesquisaMBean extends SigaaAbstractController<Object> {

	private enum Operacao {INCLUSAO, EXCLUSAO;}

	/** Constantes das Views */
	public final String JSP_LISTA_HOMOLOGACAO = "/pesquisa/homologacao_bolsas_sipac/lista.jsf";
	public final String JSP_LISTA_FINALIZACAO = "/pesquisa/homologacao_bolsas_sipac/lista_finalizacao.jsf";
	
	private Operacao operacao;
	
	/** Coleção de discentes bolsistas de pesquisa no SIGAA. */
	private Collection<MembroProjetoDiscente> discentesBolsistasSigaa = new ArrayList<MembroProjetoDiscente>();
	
	/** Tipo da bolsa para filtrar os alunos */
	private TipoBolsaPesquisa tipoBolsa = new TipoBolsaPesquisa();
	
	/** Construtor padrão. */
	public HomologacaoBolsistaPesquisaMBean() {
	}

	/**
	 * Realiza a checagem de papéis e popula os dados para iniciar o caso de uso
	 * de homologação de bolsistas.
	 * <br><br>
	 * JSP: /WEB-INF/jsp/pesquisa/menu/iniciacao.jsp
	 * @return
	 * @throws ArqException
	 */
	public String popular() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA);	
		
		if( ! Sistema.isSipacAtivo() ) {
    		addMensagem(MensagensArquitetura.SISTEMA_DESATIVADO, RepositorioDadosInstitucionais.get("siglaSipac"));
    		return null;
    	}		
		
		discentesBolsistasSigaa.clear();
		setOperacao(Operacao.INCLUSAO);
		return forward(JSP_LISTA_HOMOLOGACAO);
	}
	
	/**
	 * Realiza a checagem de papéis e popula os dados para iniciar o caso de uso
	 * de finalização de bolsistas.
	 * <br><br>
	 * JSP: /WEB-INF/jsp/pesquisa/menu/iniciacao.jsp
	 * @return
	 * @throws ArqException
	 */
	public String popularFinalizar() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		
		if( ! Sistema.isSipacAtivo() ) {
    		addMensagem(MensagensArquitetura.SISTEMA_DESATIVADO, RepositorioDadosInstitucionais.get("siglaSipac"));
    		return null;
    	}		
		
		discentesBolsistasSigaa.clear();
		setOperacao(Operacao.EXCLUSAO);
		return forward(JSP_LISTA_FINALIZACAO);
	}
	
	/**
	 * Busca todos os alunos de iniciação à pesquisa que foram indicados para bolsas
	 * no SIGAA para solicitar o cadastro da bolsa no SIPAC, ou busca todos os bolsistas
	 * finalizados no SIGAA que ainda possuem bolsa ativa no SIPAC, de acordo com a operação
	 * que está sendo realizada.
	 *  <br><br>
	 * JSP:<ul><li> /pesquisa/homologacao_bolsas_sipac/lista.jsp</li>
	 * 		   <li> /pesquisa/homologacao_bolsas_sipac/lista_finalizacao.jsp</li></ul>
	 * @throws ArqException 
	 * 
	 */
	public String buscar() throws ArqException {
		
		discentesBolsistasSigaa.clear();
		MembroProjetoDiscenteDao dao = getDAO(MembroProjetoDiscenteDao.class);
		
		if(operacao == Operacao.INCLUSAO){
			// todos os bolsistas de pesquisa com cadastro ativo no SIGAA
			discentesBolsistasSigaa = dao.findByTipoBolsa(tipoBolsa.getId());
			
			//lista dos discentes que já solicitaram bolsas em lote via SIGAA
			Collection<Integer> solicitacoesSincronizadasSipac = dao.findDiscentesBolsasSolicitadasSipac();
			
			//lista das solicitações já cadastradas no SIPAC
			Collection<Long> solicitacoesSipac = IntegracaoBolsas.verificarSolicitacaoBolsaSIPAC(null, TipoBolsa.BOLSA_PESQUISA);
			solicitacoesSipac.addAll( IntegracaoBolsas.verificarSolicitacaoBolsaSIPAC(null, TipoBolsa.BOLSA_PPQ) );
			solicitacoesSipac.addAll( IntegracaoBolsas.verificarSolicitacaoBolsaSIPAC(null, TipoBolsa.BOLSA_PESQUISA_REUNI) );
			
			//removendo discentes que já solicitaram cadastro no SIPAC
			for (Iterator<MembroProjetoDiscente> it = discentesBolsistasSigaa.iterator(); it.hasNext();) {
				MembroProjetoDiscente mpd = it.next();				
				if ((solicitacoesSincronizadasSipac.contains(mpd.getDiscente().getId())) ||
					(solicitacoesSipac.contains(mpd.getDiscente().getMatricula())) ) 
					it.remove();
				else 
					mpd.setSelecionado(true);
			}
			
			if(discentesBolsistasSigaa.isEmpty()){
				addMensagem(MensagensPesquisa.NAO_HA_DISCENTES_PENDENTES_HOMOLOGACAO);
			}
		} else if(operacao == Operacao.EXCLUSAO){
			// todos os bolsistas finalizados no SIGAA
			discentesBolsistasSigaa = dao.findByTipoBolsa(tipoBolsa.getId(), false);
			
			List<Long> matriculas = new ArrayList<Long>();
			for(MembroProjetoDiscente mpd: discentesBolsistasSigaa){
				matriculas.add(mpd.getDiscente().getMatricula());
			}
			
			Collection<Long> alunosComBolsasAtivasSipac = null;
			if(tipoBolsa.getId() == 0)
				alunosComBolsasAtivasSipac = IntegracaoBolsas.verificarCadastroBolsaSIPAC(matriculas,new Integer[] {TipoBolsa.BOLSA_PESQUISA, TipoBolsa.BOLSA_PPQ, TipoBolsa.BOLSA_PESQUISA_REUNI});
			else {
				tipoBolsa = getGenericDAO().refresh(tipoBolsa);
				alunosComBolsasAtivasSipac = IntegracaoBolsas.verificarCadastroBolsaSIPAC(matriculas,new Integer[] {tipoBolsa.getTipoBolsaSipac()});
			}
				
			if (alunosComBolsasAtivasSipac == null) {
				discentesBolsistasSigaa.clear();
			} else {
				for(Iterator<MembroProjetoDiscente> it = discentesBolsistasSigaa.iterator(); it.hasNext();){
					MembroProjetoDiscente mpd = it.next();
					if(!alunosComBolsasAtivasSipac.contains(mpd.getDiscente().getMatricula())){
						it.remove();
					} else {
						mpd.setSelecionado(true); //marcando o discente para sincronizar por padrão.
					}
				}
				
			}
			if(discentesBolsistasSigaa.isEmpty()){
				addMensagem(MensagensPesquisa.NAO_HA_DISCENTES_PENDENTES_FINALIZACAO);
			}
		}

		return redirectMesmaPagina();
	}
	

	/**
	 * Realiza a chamada de inclusão de bolsas no SIPAC.
	 * <br><br>
	 * JSP: /pesquisa/homologacao_bolsas_sipac/lista.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String homologarBolsas() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		Collection<InclusaoBolsaAcademicaDTO> solicitacoesCadastro = new ArrayList<InclusaoBolsaAcademicaDTO>();
		boolean selecionado = false;
		for (MembroProjetoDiscente mpd : discentesBolsistasSigaa) {
			
			if (mpd.isSelecionado()) {
			    selecionado = true;
				InclusaoBolsaAcademicaDTO solicitacao = new InclusaoBolsaAcademicaDTO();
				    solicitacao.setCodigoBanco(mpd.getDiscente().getPessoa().getContaBancaria().getBanco().getCodigo());
					solicitacao.setAgenciaBancaria(mpd.getDiscente().getPessoa().getContaBancaria().getAgencia());
					solicitacao.setNumeroContaBancaria(mpd.getDiscente().getPessoa().getContaBancaria().getNumero());
					solicitacao.setOperacaoContaBancaria(mpd.getDiscente().getPessoa().getContaBancaria().getOperacao());
					solicitacao.setDataFim(mpd.getPlanoTrabalho().getDataFim());
					
					if (mpd.getDataInicio() != null)
						solicitacao.setDataInicio(new Date());
					else
						solicitacao.setDataInicio(new Date());
					
					solicitacao.setIdCurso(mpd.getDiscente().getCurso().getId());
					solicitacao.setIdPessoa(mpd.getDiscente().getPessoa().getId());
					solicitacao.setIdRegistroEntrada(getUsuarioLogado().getRegistroEntrada().getId());
					solicitacao.setIdUnidadeLocalTrabalho(mpd.getPlanoTrabalho().getProjetoPesquisa().getUnidade().getId());
					solicitacao.setIdUnidadeResponsavel(ParametroHelper.getInstance().getParametroInt(ParametrosPesquisa.ID_UNIDADE_PROPESQ));
					solicitacao.setIdUsuarioCadastro(getUsuarioLogado().getId());
					solicitacao.setJustificativa("[HOMOLOGAÇÃO DE BOLSA REALIZADA ATRAVÉS DO MENU DA PROPESQ]");
					solicitacao.setMatricula(mpd.getDiscente().getMatricula());
					solicitacao.setObservacao("[SOLICITAÇÃO DE CADASTRO EM LOTE REALIZADA VIA SIGAA]");
					solicitacao.setNivel(String.valueOf(mpd.getDiscente().getNivel()));
					solicitacao.setTipoBolsa(mpd.getTipoBolsa().getTipoBolsaSipac());
					solicitacao.setIdDiscenteProjeto(mpd.getId());
				solicitacoesCadastro.add(solicitacao);
			}
		}
		
		if(selecionado) {
		    try {
		        
		        prepareMovimento(ArqListaComando.SOLICITAR_INCLUSAO_BOLSA_ACADEMICA);
		        MovimentoBolsaAcademica mov = new MovimentoBolsaAcademica();
		        mov.setSolicitacoes(solicitacoesCadastro);
		        mov.setCodMovimento(ArqListaComando.SOLICITAR_INCLUSAO_BOLSA_ACADEMICA);
		        execute(mov);
		        addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		        
		        //atualiza a tela de busca
		        buscar();
		        
		    } catch (NegocioException e) {
		        notifyError(e);
		        addMensagens(e.getListaMensagens());
		    }		
		} else {
		    addMensagem(MensagensPesquisa.SELECIONE_DISCENTE_HOMOLOGACAO);
		}
		
		
		return redirectMesmaPagina();		
	}
	
	/**
	 * Realiza a chamada de exclusão de bolsas no SIPAC.
	 * <br><br>
	 * JSP: /pesquisa/homologacao_bolsas_sipac/lista_finalizacao.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String finalizarBolsas() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		Collection<InclusaoBolsaAcademicaDTO> solicitacoesFinalizacao = new ArrayList<InclusaoBolsaAcademicaDTO>();
		boolean selecionado = false;
		for (MembroProjetoDiscente mpd : discentesBolsistasSigaa) {
			
			if (mpd.isSelecionado()) {
				selecionado = true;
				InclusaoBolsaAcademicaDTO solicitacao = new InclusaoBolsaAcademicaDTO();
				solicitacao.setCodigoBanco(mpd.getDiscente().getPessoa().getContaBancaria().getBanco().getCodigo());
				solicitacao.setAgenciaBancaria(mpd.getDiscente().getPessoa().getContaBancaria().getAgencia());
				solicitacao.setNumeroContaBancaria(mpd.getDiscente().getPessoa().getContaBancaria().getNumero());
				solicitacao.setOperacaoContaBancaria(mpd.getDiscente().getPessoa().getContaBancaria().getOperacao());
				solicitacao.setDataFim(mpd.getPlanoTrabalho().getDataFim());
				solicitacao.setDataInicio(mpd.getDataIndicacao());
				solicitacao.setDataFim(mpd.getDataFinalizacao());
				solicitacao.setIdCurso(mpd.getDiscente().getCurso().getId());
				solicitacao.setIdPessoa(mpd.getDiscente().getPessoa().getId());
				solicitacao.setIdRegistroEntrada(getUsuarioLogado().getRegistroEntrada().getId());
				solicitacao.setIdUnidadeLocalTrabalho(mpd.getPlanoTrabalho().getProjetoPesquisa().getUnidade().getId());
				solicitacao.setIdUnidadeResponsavel(ParametroHelper.getInstance().getParametroInt(ParametrosPesquisa.ID_UNIDADE_PROPESQ));
				solicitacao.setIdUsuarioCadastro(getUsuarioLogado().getId());
				solicitacao.setJustificativa("[FINALIZAÇÃO DE BOLSA REALIZADA ATRAVÉS DO MENU DA PROPESQ]");
				solicitacao.setMatricula(mpd.getDiscente().getMatricula());
				solicitacao.setObservacao("Finalização de bolsa realizada via SIGAA por " + getUsuarioLogado().getNome() + " em " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + ".");
				solicitacao.setNivel(String.valueOf(mpd.getDiscente().getNivel()));
				solicitacao.setTipoBolsa(mpd.getTipoBolsa().getTipoBolsaSipac());
				solicitacao.setIdDiscenteProjeto(mpd.getId());
				solicitacoesFinalizacao.add(solicitacao);
			}
		}
		
		if(selecionado) {
			try {
				
				prepareMovimento(ArqListaComando.SOLICITAR_EXCLUSAO_BOLSA_ACADEMICA);
				MovimentoBolsaAcademica mov = new MovimentoBolsaAcademica();
				mov.setSolicitacoes(solicitacoesFinalizacao);
				mov.setCodMovimento(ArqListaComando.SOLICITAR_EXCLUSAO_BOLSA_ACADEMICA);
				execute(mov);
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
				
				//atualiza a tela de busca
				buscar();
				
			} catch (NegocioException e) {
				notifyError(e);
				addMensagens(e.getListaMensagens());
			}		
		} else {
			addMensagem(MensagensPesquisa.SELECIONE_DISCENTE_FINALIZACAO);
		}
		
		
		return redirectMesmaPagina();		
	}
	
	/**
	 * Invoca o processador para marcar a indicação/finalização como ignorada para a operação de homologação/finalização.
	 * Dessa forma, aquela indicação/finalização identificada como incorreta pelo gestor de pesquisa não irá mais aparecer
	 * nas operações de homologação/finalização.
	 * <br><br>
	 * JSPs:<ul><li>/pesquisa/homologacao_bolsas_sipac/lista.jsp</li> 
	 * 			<li>/pesquisa/homologacao_bolsas_sipac/lista_finalizacao.jsp</li></ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String ignorarBolsista() throws ArqException, NegocioException {
		prepareMovimento(SigaaListaComando.IGNORAR_BOLSISTA);
		int id = getParameterInt("id");
		MembroProjetoDiscente bolsista = new MembroProjetoDiscente(id);
		
		MovimentoIndicarBolsista mov = new MovimentoIndicarBolsista();
		mov.setBolsistaAnterior(bolsista);
		mov.setCodMovimento(SigaaListaComando.IGNORAR_BOLSISTA);
		
		execute(mov);
		addMensagem(MensagensPesquisa.IGNORADO_SUCESSO, operacao == Operacao.INCLUSAO ? "Indicação" : "Finalização");
		buscar();
		return redirectMesmaPagina();
	}
	
	public Collection<MembroProjetoDiscente> getDiscentesBolsistasSigaa() {
		return discentesBolsistasSigaa;
	}

	public void setDiscentesBolsistasSigaa(
			Collection<MembroProjetoDiscente> discentesBolsistasSigaa) {
		this.discentesBolsistasSigaa = discentesBolsistasSigaa;
	}

	public TipoBolsaPesquisa getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(TipoBolsaPesquisa tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}

	public Operacao getOperacao() {
		return operacao;
	}

	public void setOperacao(Operacao operacao) {
		this.operacao = operacao;
	}
	
}