/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '19/05/2011'
 *
 */
package br.ufrn.sigaa.projetos.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIData;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.AvaliacaoDao;
import br.ufrn.sigaa.arq.dao.projetos.EditalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.Projeto;


/**
 * Classe responsável pela classificação das ações acadêmicas 
 * de acordo com as avaliações dos membros do comitê Ad Hoc.
 * 
 * @author ilueny santos
 *
 */
@Component("classificarProjetosBean")
@Scope("request")
public class ClassificacaoProjetoMBean  extends SigaaAbstractController<Projeto> {

		/** Edital*/
		private Edital edital = new Edital();
		
		/** Projetos classificados. */
		private List<Map<String, Object>> classificacao = new ArrayList<Map<String,Object>>();
		/** Projetos classificados. */
		private List<Projeto> projetos = new ArrayList<Projeto>();		
		/** Projetos classificados. */
		private UIData uiProjetos = new HtmlDataTable();

		
		/** Contrutor padrão. */
		public ClassificacaoProjetoMBean() {
			obj = new Projeto();			
		}
		
		/**
		 * Exibe todos os editais.
		 * 
		 * Chamado por:
		 * <ul>
		 *  <li>/sigaa.war/projetos/Avaliacoes/Classificar/form.jsp</li>
		 * </ul>
		 * 
		 * @return
		 * @throws ArqException
		 */
		public List<SelectItem> getEditais() throws DAOException {
		    Collection<Edital> editais = getDAO(EditalDao.class).findAllAtivosAssociados();
		    return toSelectItems(editais, "id", "descricaoCompleta");
		}
		
		/** Realiza a classificação prévia dos projetos. */
		private void preClassificarProjetos() throws SegurancaException {
			checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.GESTOR_PESQUISA);
			AvaliacaoDao dao =  getDAO(AvaliacaoDao.class);
			try {
				if (ValidatorUtil.isNotEmpty(edital)) {
					dao.initialize(edital);
					classificacao = dao.findProjetosAvaliadosClassificados(edital);
				}
				
				Integer preClass = 0;
				for (Map<String, Object> mapPj : classificacao) {
					preClass++;
					mapPj.put("classificacao", preClass); 
				}
				
			} catch (DAOException e) {
				notifyError(e);
			}
		}
		
		/**
		 * Exibe o resultado da pré-classificação para impressão.
		 * 
		 * Chamado por:
		 * <ul>
		 * 	<li>/sigaa.war/projetos/menu.jsp</li>
		 * 	<li>/sigaa.war/projetos/Avaliações/Classificar/lista.jsp</li>		  
		 * </ul>
		 * 
		 * @return
		 * @throws SegurancaException 
		 */
		public String preView() throws SegurancaException {
			preClassificarProjetos();
		    return forward(ConstantesNavegacaoProjetos.CLASSIFICAR_PROJETOS_LISTA);
		}


		/**
		 * Ordena uma coleção de Ações e redireciona o usuário para tela de confirmação.
		 * 
		 * Chamado por:
		 * <ul>
		 * 	<li>/sigaa.war/projetos/Avaliacoes/Classificacao/form.jsp</li>
		 * </ul>
		 * 
		 * @return
		 * @throws SegurancaException 
		 */
		public String iniciarClassificacao() throws SegurancaException {
			checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
			try {
				preClassificarProjetos();
				if (ValidatorUtil.isEmpty(classificacao)) {
					addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
					return null;
				}

				prepareMovimento(SigaaListaComando.CLASSIFICAR_PROJETOS);				
				//confirmar a classificação.
				return forward(ConstantesNavegacaoProjetos.CLASSIFICAR_PROJETOS_FORM);
			} catch(Exception e) {
				notifyError(e);
			}
			return null;
		}
			
		/**
		 * Finaliza o processo de avaliação das ações.
		 * O próximo passo no processo é a distribuição de recursos.
		 * 
		 * Chamado por:
		 * <ul>
		 * 	<li>/sigaa.war/projetos/Avaliacoes/Classificacao/form.jsp</li>
		 * </ul>
		 * 
		 * 
		 * @return
		 * @throws ArqException 
		 */
		public String confirmarClassificacao() throws ArqException {
			checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.GESTOR_PESQUISA);	    
			try {
				
				projetos.clear();
				for (Map<String, Object> mapPj : classificacao) {
					Projeto pj = new Projeto((Integer)mapPj.get("id_projeto"));
					pj.setClassificacao(((Integer)mapPj.get("classificacao")).intValue());
					projetos.add(pj);
				}
				
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjAuxiliar(edital);
				mov.setColObjMovimentado(projetos);
				mov.setCodMovimento(SigaaListaComando.CLASSIFICAR_PROJETOS);
				execute(mov);
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
				return preView();
				
			} catch(NegocioException e) {
				addMensagemErro(e.getMessage());	
			} catch(Exception e) {
				notifyError(e);
			}
			return null;
		}

		public Edital getEdital() {
			return edital;
		}

		public void setEdital(Edital edital) {
			this.edital = edital;
		}

		public List<Projeto> getProjetos() {
			return projetos;
		}

		public void setProjetos(List<Projeto> projetos) {
			this.projetos = projetos;
		}

		public List<Map<String, Object>> getClassificacao() {
			return classificacao;
		}

		public void setClassificacao(List<Map<String, Object>> classificacao) {
			this.classificacao = classificacao;
		}

		public UIData getUiProjetos() {
			return uiProjetos;
		}

		public void setUiProjetos(UIData uIProjetos) {
			uiProjetos = uIProjetos;
		}
		
	}
