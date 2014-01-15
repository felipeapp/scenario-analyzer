/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '31/03/2008'
 *
 */
package br.ufrn.sigaa.prodocente.jsf;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.prodocente.ProducaoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.producao.dominio.BolsaObtida;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.prodocente.producao.jsf.AbstractControllerProdocente;
import br.ufrn.sigaa.prodocente.producao.jsf.ArtigoMBean;
import br.ufrn.sigaa.prodocente.producao.jsf.AudioVisualMBean;
import br.ufrn.sigaa.prodocente.producao.jsf.BancaMBean;
import br.ufrn.sigaa.prodocente.producao.jsf.BolsaObtidaMBean;
import br.ufrn.sigaa.prodocente.producao.jsf.CapituloMBean;
import br.ufrn.sigaa.prodocente.producao.jsf.ExposicaoApresentacaoMBean;
import br.ufrn.sigaa.prodocente.producao.jsf.LivroMBean;
import br.ufrn.sigaa.prodocente.producao.jsf.MaquetePrototipoOutroMBean;
import br.ufrn.sigaa.prodocente.producao.jsf.MontagemMBean;
import br.ufrn.sigaa.prodocente.producao.jsf.ParticipacaoColegiadoComissaoMBean;
import br.ufrn.sigaa.prodocente.producao.jsf.ParticipacaoComissaoOrgEventosMBean;
import br.ufrn.sigaa.prodocente.producao.jsf.ParticipacaoSociedadeMBean;
import br.ufrn.sigaa.prodocente.producao.jsf.PatenteMBean;
import br.ufrn.sigaa.prodocente.producao.jsf.PremioRecebidoMBean;
import br.ufrn.sigaa.prodocente.producao.jsf.ProgramacaoVisualMBean;
import br.ufrn.sigaa.prodocente.producao.jsf.PublicacaoEventoMBean;
import br.ufrn.sigaa.prodocente.producao.jsf.TextoDidaticoMBean;
import br.ufrn.sigaa.prodocente.producao.jsf.VisitaCientificaMBean;

/**
 * Controlador respons�vel pela auto-valida��o de produ��es
 * intelectuais
 * 
 * @author Ricardo Wendell
 *
 */
@SuppressWarnings("unchecked")
@Component("autoValidacaoProducaoBean") @Scope("session")
public class AutoValidacaoProducaoMBean extends AbstractControllerProdocente {

	private Collection<? extends Producao> producoes;
	
	private boolean concordancia;
	
	public AutoValidacaoProducaoMBean() {
		clear();
	}

	private void clear() {
		producoes = null;
		concordancia = false;
	}
	
	/**
	 * Listar todas as produ��es pendentes de valida��o do servidor associado
	 * ao usu�rio corrente
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String listarPendentes() throws ArqException {
		// Verificar permiss�o de valida��o
		Servidor servidor = getUsuarioLogado().getServidorAtivo();
		if (servidor == null) {
			addMensagemErro("Somente servidores podem realizar a auto-valida��o de produ��es intelectuais");
			return null;
		}
		
		// Buscar as produ��es pendentes de valida��o
		producoes = getDAO(ProducaoDao.class).findPendentesValidacao(servidor);
		
		Collection<Producao> praRemover = new ArrayList<Producao>();
		
		// Verificar se existe alguma produ��o pendente de valida��o
		if ( producoes.isEmpty() ) {
			addMensagemErro("N�o foram encontradas produ��es pendentes de valida��o");
			return null;
		} else {
			for(Producao p: producoes){
				if(p.getTipoProducao().getId() == TipoProducao.BOLSA_OBTIDA.getId()){
					BolsaObtida b = (BolsaObtida) p;
					if(b.getTipoBolsaProdocente().isProdutividade()){
						praRemover.add(p);
					}
				}
			}
		}
		
		producoes.removeAll(praRemover);
		
		popularMapaProducoes(producoes);
		prepareMovimento(SigaaListaComando.AUTO_VALIDAR_PRODUCAO);
		
		return forward("/prodocente/validacao/auto_validacao.jsp");
	}
	
	/**
	 * Confirmar a valida��o
	 * 
	 * @return
	 * @throws RemoteException 
	 * @throws ArqException 
	 */
	public String confirmar() throws ArqException {
		
		
		// Verificar se a confirma��o foi selecionada pelo usu�rio
		if ( !concordancia ) {
			addMensagemErro("� necess�rio selecionar a caixa de confirma��o atestando que voc� concorda com os " +
			"termos da auto-valida��o das produ��es intelectuais.");
			return null;
		}
		
		HttpServletRequest req = getCurrentRequest();
		String[] selecaoProducao = req.getParameterValues("selecaoProducao");
		
		if (selecaoProducao == null) {
			addMensagemErro("� necess�rio selecionar pelo menos uma produ��o.");
			return null;
		}
		
		Collection<Producao> producoesSelecionados = new ArrayList<Producao>();
		
		ListaMensagens lista = new ListaMensagens();
		for (int i = 0; i < selecaoProducao.length; i++) {
			Producao prod = getGenericDAO().findByPrimaryKey(new Integer(selecaoProducao[i]), Producao.class);
			lista = prod.validate();
			if (!lista.isEmpty()){
				addMensagemErro("A produ��o \"" + prod.getTitulo() + "\" possui um ou mais campos obrigat�rios n�o preenchidos.");
				continue;
			} else {
				producoesSelecionados.add(prod);
			}
		}
		if (!lista.isEmpty()){
			return null;
		}
		
		// Validar as produ��es
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setColObjMovimentado(producoesSelecionados);
		mov.setCodMovimento(SigaaListaComando.AUTO_VALIDAR_PRODUCAO);
		
		try {
			execute(mov);
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} 
		
		addMensagemInformation("Produ��es validadas com sucesso!");
		return cancelar();
	}
	
	/**
	 * M�todo que prov� um ponto de acesso �nico para a opera��o de altera��o dos diversos tipos de produ��o.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     *  <li>sigaa.war/prodocente/validacao/auto_validacao.jsp</li>
     * </ul>
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public String atualizar() throws ArqException {
		int id = getParameterInt("id", 0);
		Producao p = getGenericDAO().findByPrimaryKey(id, Producao.class);
		String nomeMBean = p.getTipoProducao().getNomeMBean();

		AbstractControllerProdocente mBean = (AbstractControllerProdocente) getMBean(nomeMBean);
		mBean.setDirBase("/prodocente/producao/");
		mBean.setForwardAlterar("/prodocente/validacao/auto_validacao.jsp");
		mBean.setForwardCancelar("/prodocente/validacao/auto_validacao.jsp");

		// Artigo, Peri�dicos, Jornais e Similares
		if (mBean instanceof ArtigoMBean) {
			return ((ArtigoMBean) mBean).atualizar();
		}
		// Cap�tulo de Livros
		else if (mBean instanceof CapituloMBean) {
			return ((CapituloMBean) mBean).atualizar();
		}
		// Livros
		else if (mBean instanceof LivroMBean) {
			return ((LivroMBean) mBean).atualizar();
		}
		// Participa��o em Eventos
		else if (mBean instanceof PublicacaoEventoMBean) {
			return ((PublicacaoEventoMBean) mBean).atualizar();
		}
		// Textos Did�ticos e Discuss�o
		else if (mBean instanceof TextoDidaticoMBean) {
			return ((TextoDidaticoMBean) mBean).atualizar();
		}
		// AudioVisuais
		else if (mBean instanceof AudioVisualMBean) {
			return ((AudioVisualMBean) mBean).atualizar();
		}
		// Exposi��o ou Apresenta��o Art�sticas
		else if (mBean instanceof ExposicaoApresentacaoMBean) {
			return ((ExposicaoApresentacaoMBean) mBean).atualizar();
		}
		// Montagens
		else if (mBean instanceof MontagemMBean) {
			return ((MontagemMBean) mBean).atualizar();
		}
		// Programa��o Visual
		else if (mBean instanceof ProgramacaoVisualMBean) {
			return ((ProgramacaoVisualMBean) mBean).atualizar();
		}
		// Maquetes, Prot�tipos, Softwares e Outros
		else if (mBean instanceof MaquetePrototipoOutroMBean) {
			return ((MaquetePrototipoOutroMBean) mBean).atualizar();
		}
		// Patentes
		else if (mBean instanceof PatenteMBean) {
			return ((PatenteMBean) mBean).atualizar();
		}
		// Bancas
		else if (mBean instanceof BancaMBean) {
			return ((BancaMBean) mBean).atualizar();
		}
		// Pr�mio Recebido
		else if (mBean instanceof PremioRecebidoMBean) {
			return ((PremioRecebidoMBean) mBean).atualizar();
		}
		// Bolsas Obtidas
		else if (mBean instanceof BolsaObtidaMBean) {
			return ((BolsaObtidaMBean) mBean).atualizar();
		}
		// Visitas Cient�ficas
		else if (mBean instanceof VisitaCientificaMBean) {
			return ((VisitaCientificaMBean) mBean).atualizar();
		}
		// Organiza��o de Eventos, Consultorias, Edi��o e Revis�o de Per�odicos
		else if (mBean instanceof ParticipacaoComissaoOrgEventosMBean) {
			return ((ParticipacaoComissaoOrgEventosMBean) mBean).atualizar();
		}
		// Participa��o em Sociedades Cient�ficas e Culturais
		else if (mBean instanceof ParticipacaoSociedadeMBean) {
			return ((ParticipacaoSociedadeMBean) mBean).atualizar();
		}
		// Participa��o em Colegiados e Comiss�es
		else if (mBean instanceof ParticipacaoColegiadoComissaoMBean) {
			return ((ParticipacaoColegiadoComissaoMBean) mBean).atualizar();
		}

		return null;
	}

	@Override
	public String cancelar() {
		clear();
		try {
			redirectJSF(getSubSistema().getLink());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Collection<? extends Producao> getProducoes() {
		return this.producoes;
	}

	public void setProducoes(Collection<? extends Producao> producoes) {
		this.producoes = producoes;
	}

	public boolean isConcordancia() {
		return this.concordancia;
	}

	public void setConcordancia(boolean concordancia) {
		this.concordancia = concordancia;
	}
	
}
