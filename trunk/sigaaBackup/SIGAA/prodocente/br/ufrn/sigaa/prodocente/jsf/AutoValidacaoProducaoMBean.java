/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Controlador responsável pela auto-validação de produções
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
	 * Listar todas as produções pendentes de validação do servidor associado
	 * ao usuário corrente
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String listarPendentes() throws ArqException {
		// Verificar permissão de validação
		Servidor servidor = getUsuarioLogado().getServidorAtivo();
		if (servidor == null) {
			addMensagemErro("Somente servidores podem realizar a auto-validação de produções intelectuais");
			return null;
		}
		
		// Buscar as produções pendentes de validação
		producoes = getDAO(ProducaoDao.class).findPendentesValidacao(servidor);
		
		Collection<Producao> praRemover = new ArrayList<Producao>();
		
		// Verificar se existe alguma produção pendente de validação
		if ( producoes.isEmpty() ) {
			addMensagemErro("Não foram encontradas produções pendentes de validação");
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
	 * Confirmar a validação
	 * 
	 * @return
	 * @throws RemoteException 
	 * @throws ArqException 
	 */
	public String confirmar() throws ArqException {
		
		
		// Verificar se a confirmação foi selecionada pelo usuário
		if ( !concordancia ) {
			addMensagemErro("É necessário selecionar a caixa de confirmação atestando que você concorda com os " +
			"termos da auto-validação das produções intelectuais.");
			return null;
		}
		
		HttpServletRequest req = getCurrentRequest();
		String[] selecaoProducao = req.getParameterValues("selecaoProducao");
		
		if (selecaoProducao == null) {
			addMensagemErro("É necessário selecionar pelo menos uma produção.");
			return null;
		}
		
		Collection<Producao> producoesSelecionados = new ArrayList<Producao>();
		
		ListaMensagens lista = new ListaMensagens();
		for (int i = 0; i < selecaoProducao.length; i++) {
			Producao prod = getGenericDAO().findByPrimaryKey(new Integer(selecaoProducao[i]), Producao.class);
			lista = prod.validate();
			if (!lista.isEmpty()){
				addMensagemErro("A produção \"" + prod.getTitulo() + "\" possui um ou mais campos obrigatórios não preenchidos.");
				continue;
			} else {
				producoesSelecionados.add(prod);
			}
		}
		if (!lista.isEmpty()){
			return null;
		}
		
		// Validar as produções
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setColObjMovimentado(producoesSelecionados);
		mov.setCodMovimento(SigaaListaComando.AUTO_VALIDAR_PRODUCAO);
		
		try {
			execute(mov);
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} 
		
		addMensagemInformation("Produções validadas com sucesso!");
		return cancelar();
	}
	
	/**
	 * Método que provê um ponto de acesso único para a operação de alteração dos diversos tipos de produção.
	 * Método chamado pela(s) seguinte(s) JSP(s):
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

		// Artigo, Periódicos, Jornais e Similares
		if (mBean instanceof ArtigoMBean) {
			return ((ArtigoMBean) mBean).atualizar();
		}
		// Capítulo de Livros
		else if (mBean instanceof CapituloMBean) {
			return ((CapituloMBean) mBean).atualizar();
		}
		// Livros
		else if (mBean instanceof LivroMBean) {
			return ((LivroMBean) mBean).atualizar();
		}
		// Participação em Eventos
		else if (mBean instanceof PublicacaoEventoMBean) {
			return ((PublicacaoEventoMBean) mBean).atualizar();
		}
		// Textos Didáticos e Discussão
		else if (mBean instanceof TextoDidaticoMBean) {
			return ((TextoDidaticoMBean) mBean).atualizar();
		}
		// AudioVisuais
		else if (mBean instanceof AudioVisualMBean) {
			return ((AudioVisualMBean) mBean).atualizar();
		}
		// Exposição ou Apresentação Artísticas
		else if (mBean instanceof ExposicaoApresentacaoMBean) {
			return ((ExposicaoApresentacaoMBean) mBean).atualizar();
		}
		// Montagens
		else if (mBean instanceof MontagemMBean) {
			return ((MontagemMBean) mBean).atualizar();
		}
		// Programação Visual
		else if (mBean instanceof ProgramacaoVisualMBean) {
			return ((ProgramacaoVisualMBean) mBean).atualizar();
		}
		// Maquetes, Protótipos, Softwares e Outros
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
		// Prêmio Recebido
		else if (mBean instanceof PremioRecebidoMBean) {
			return ((PremioRecebidoMBean) mBean).atualizar();
		}
		// Bolsas Obtidas
		else if (mBean instanceof BolsaObtidaMBean) {
			return ((BolsaObtidaMBean) mBean).atualizar();
		}
		// Visitas Científicas
		else if (mBean instanceof VisitaCientificaMBean) {
			return ((VisitaCientificaMBean) mBean).atualizar();
		}
		// Organização de Eventos, Consultorias, Edição e Revisão de Períodicos
		else if (mBean instanceof ParticipacaoComissaoOrgEventosMBean) {
			return ((ParticipacaoComissaoOrgEventosMBean) mBean).atualizar();
		}
		// Participação em Sociedades Científicas e Culturais
		else if (mBean instanceof ParticipacaoSociedadeMBean) {
			return ((ParticipacaoSociedadeMBean) mBean).atualizar();
		}
		// Participação em Colegiados e Comissões
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
