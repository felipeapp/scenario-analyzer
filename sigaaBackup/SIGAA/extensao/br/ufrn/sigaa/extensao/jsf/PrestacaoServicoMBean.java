/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/12/2006
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.TipoEsferaAdministrativa;
import br.ufrn.sigaa.extensao.dominio.FatorGerador;
import br.ufrn.sigaa.extensao.dominio.FormaCompromisso;
import br.ufrn.sigaa.extensao.dominio.MembroPrestacaoServico;
import br.ufrn.sigaa.extensao.dominio.NaturezaServico;
import br.ufrn.sigaa.extensao.dominio.PrestacaoServico;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/*******************************************************************************
 * Classe utilizada no contexto de PrestacaoServicos. O modulo de prestacao de
 * servicos ainda nao estah totalmente definido no SIGAA.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Component("prestacaoServico")
@Scope("request")
public class PrestacaoServicoMBean extends
		SigaaAbstractController<PrestacaoServico> {

	private String tipoPessoa = "publica";

	private String tipoMembro = "servidor";

	private boolean coordenador = false;

	private MembroPrestacaoServico membroEquipePrestacao = new MembroPrestacaoServico();

	public PrestacaoServicoMBean() {
		obj = new PrestacaoServico();
	}

	public Collection<SelectItem> getAllPrestacaoServicoCombo() {
		return getAll(PrestacaoServico.class, "id", "titulo");
	}

	public Collection<SelectItem> getAllTipoEsferaAdministrativaCombo() {
		return getAll(TipoEsferaAdministrativa.class, "id", "descricao");
	}

	public Collection<SelectItem> getAllFatorGeradorCombo() {
		return getAll(FatorGerador.class, "id", "descricao");
	}

	public Collection<SelectItem> getAllFormaCompromissoCombo() {
		return getAll(FormaCompromisso.class, "id", "descricao");
	}

	public Collection<SelectItem> getAllNaturezaServicoCombo() {
		return getAll(NaturezaServico.class, "id", "descricao");
	}

	public String irTelaContratante() {

		// TODO validar equipe. ver regras

		return forward(ConstantesNavegacao.CONTRATANTE);
	}

	public String irTelaEquipe() {

		// TODO validar identificacao do contratante. ver regras

		return forward(ConstantesNavegacao.EQUIPE);
	}

	public String irTelaServico() {

		// TODO validar equipe. ver regras

		return forward(ConstantesNavegacao.SERVICO);
	}

	public String irTelaDescricao() {

		// TODO validar caracterizacao do servico ver regras

		return forward(ConstantesNavegacao.DESCRICAO);
	}

	public String irTelaResumo() {

		// TODO validar contratante ver regras

		return forward(ConstantesNavegacao.RESUMO_PS);
	}

	/**
	 * Adiciona o Membro da equipe na classe PrestacaoServico
	 * 
	 * @return
	 */
	public String adicionarMembro() {

		ListaMensagens mensagens = new ListaMensagens();

		// TODO validar membro da equipe

		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}

		GenericDAO dao = getGenericDAO();
		try {

			// testa se é servidor ou aluno
			if ("servidor".equals(getTipoMembro())) {
			    Servidor serv = dao.findByPrimaryKey(membroEquipePrestacao.getServidor().getId(), Servidor.class);
			    membroEquipePrestacao.setServidor(serv);
			}
			else {
			    Discente dis = dao.findByPrimaryKey(membroEquipePrestacao.getDiscente().getId(), Discente.class);
			    membroEquipePrestacao.setDiscente(dis);
			}
			if (isCoordenador()) {
				membroEquipePrestacao
						.setTipo(MembroPrestacaoServico.COORDENADOR);
			} else {
				membroEquipePrestacao.setTipo(MembroPrestacaoServico.MEMBRO);
			}
			if (obj.getMembrosPrestacaoServico() == null) {
				obj.setMembrosPrestacaoServico(new HashSet<MembroPrestacaoServico>());
			}
			
			// testa se o membro já não está na equipe
			if (!obj.getMembrosPrestacaoServico().contains(membroEquipePrestacao)) {
				// adiciona o membro a equipe do projeto.
				obj.getMembrosPrestacaoServico().add(membroEquipePrestacao);
			} else {
				addMensagemErro("Inpossível incluir. Esta pessoa já pertence a equipe!");
				return null;
			}

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
		} finally {
			dao.close();
		}

		// limpa os dados
		membroEquipePrestacao = new MembroPrestacaoServico();
		coordenador = false;

		return null;

	}

	/**
	 * Remove o membro da equipe do serviço com base no idPessoa
	 * 
	 * 
	 * @return
	 */
	public String removeMembro() {

		MembroPrestacaoServico m = new MembroPrestacaoServico();
		m.setPessoa(new Pessoa(getParameterInt("idPessoa")));

		for (Iterator<MembroPrestacaoServico> it = obj
				.getMembrosPrestacaoServico().iterator(); it.hasNext();) {
			if (it.next().getPessoa().getId() == m.getPessoa().getId()) {
				it.remove();
				break;
			}
		}

		return null;

	}

	public String getTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(String tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}

	public boolean isCoordenador() {
		return coordenador;
	}

	public void setCoordenador(boolean coordenador) {
		this.coordenador = coordenador;
	}

	public MembroPrestacaoServico getMembroEquipePrestacao() {
		return membroEquipePrestacao;
	}

	public void setMembroEquipePrestacao(
			MembroPrestacaoServico membroEquipePrestacao) {
		this.membroEquipePrestacao = membroEquipePrestacao;
	}

	public String getTipoMembro() {
		return tipoMembro;
	}

	public void setTipoMembro(String tipoMembro) {
		this.tipoMembro = tipoMembro;
	}

}
