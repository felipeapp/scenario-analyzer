package br.ufrn.sigaa.monitoria.jsf;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

@Component @Scope("request")
public class AlterarStatusProjetoMonitoriaMBean extends SigaaAbstractController<ProjetoEnsino> {

	private Collection<ProjetoEnsino> projetosSelecionados;
	private int idSituacao;
	
	public AlterarStatusProjetoMonitoriaMBean() {
		clear();
	}

	private void clear() {
		obj = new ProjetoEnsino();
		projetosSelecionados = new ArrayList<ProjetoEnsino>();
		idSituacao = 0;
	}

	public String iniciarAlteracao() {
		projetosSelecionados = new ArrayList<ProjetoEnsino>();
		return forward("/monitoria/AlterarSituacaoProjeto/projetosSelecionados.jsf");
	}

	public String alterarStatus() throws DAOException {
		
		ValidatorUtil.validateRequiredId(idSituacao, "Situação do Projeto", erros);		
		if ( hasOnlyErrors() )
			return null;
		
		for (ProjetoEnsino proj : projetosSelecionados) {
			proj.setSituacaoProjeto(new TipoSituacaoProjeto(idSituacao));
			getGenericDAO().updateField(ProjetoEnsino.class, proj.getId(), "situacaoProjeto", proj.getSituacaoProjeto().getId());
			ProjetoHelper.gravarHistoricoSituacaoProjeto(proj.getSituacaoProjeto().getId(), proj.getProjeto().getId(), getUsuarioLogado().getRegistroEntrada());
			ProjetoHelper.sincronizarSituacaoComProjetoBase(getGenericDAO(), proj);
		}

		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		ProjetoMonitoriaMBean mBean = getMBean("projetoMonitoria");
		mBean.setProjetosLocalizados(new ArrayList<ProjetoEnsino>());
			
		return iniciarSelecao();
	}
	
	public String iniciarSelecao() {
		clear();
		return forward("/monitoria/AlterarSituacaoProjeto/alterarStatus.jsf");
	}
	
	public Collection<ProjetoEnsino> getProjetosSelecionados() {
		if ( projetosSelecionados.isEmpty() ) {
			ProjetoMonitoriaMBean mBean = getMBean("projetoMonitoria");
			for (ProjetoEnsino projeto : mBean.getProjetosLocalizados()) {
				if ( projeto.getProjeto().isSelecionado() )
					projetosSelecionados.add(projeto);
			}
		}
		return projetosSelecionados;
	}

	public void setProjetosSelecionados(Collection<ProjetoEnsino> projetosSelecionados) {
		this.projetosSelecionados = projetosSelecionados;
	}

	public int getIdSituacao() {
		return idSituacao;
	}

	public void setIdSituacao(int idSituacao) {
		this.idSituacao = idSituacao;
	}

}