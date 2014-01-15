package br.ufrn.sigaa.extensao.negocio;

import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.DistribuicaoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoSelecaoExtensao;

/**
 * Movimento para cadastro de ecoes de extens�o em geral
 *
 * @author Ilueny Santos
 *
 */
@SuppressWarnings("serial")
public class CadastroExtensaoMov extends MovimentoCadastro {
	
	//Utilizado para armazenar Discente de Extens�o para utiliza��o no processador
	private DiscenteExtensao discenteExtensao = null;
	
	//Utilizado para armazenar Atividade de Extens�o para utiliza��o no processador
	private AtividadeExtensao atividade  = null;
	
	//Utilizado para armazenar atividades para utiliza��o no processador
	private Collection<AtividadeExtensao> atividades  = null;
	
	//Utilizado para armazenar Discentes de Extens�o para utiliza��o no processador
	private Collection<DiscenteExtensao> discentesExtensao  = null;
	
	//Utilizado para armazenar Inscri��oes de sele��o para utiliza��o no processador
	private Collection<InscricaoSelecaoExtensao> inscricoesSelecao  = null;
	
	//Utilizado para armazenar informa��es sobre uma distribui��o para utiliza��o no processador
	private DistribuicaoAtividadeExtensao distribuicaoExtensao = null;
	
	//Utilizado para armazenar mensagem a ser enviada a coordenadores pendentes de relat�rio.
	private String msgCoordenadorPendenteRelatorio = null;

	private boolean naoValidar = false;
	
	private Date novaDataFinalizacaoAcao = new Date();
	
	public boolean isNaoValidar() {
		return naoValidar;
	}

	public void setNaoValidar(boolean naoValidar) {
		this.naoValidar = naoValidar;
	}

	public DiscenteExtensao getDiscenteExtensao() {
		return discenteExtensao;
	}

	public void setDiscenteExtensao(DiscenteExtensao discenteExtensao) {
		this.discenteExtensao = discenteExtensao;
	}

	public AtividadeExtensao getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeExtensao atividade) {
		this.atividade = atividade;
	}

	public Collection<DiscenteExtensao> getDiscentesExtensao() {
		return discentesExtensao;
	}

	public void setDiscentesExtensao(Collection<DiscenteExtensao> discentesExtensao) {
		this.discentesExtensao = discentesExtensao;
	}

	public Collection<InscricaoSelecaoExtensao> getInscricoesSelecao() {
		return inscricoesSelecao;
	}

	public void setInscricoesSelecao(
			Collection<InscricaoSelecaoExtensao> inscricoesSelecao) {
		this.inscricoesSelecao = inscricoesSelecao;
	}

	public DistribuicaoAtividadeExtensao getDistribuicaoExtensao() {
		return distribuicaoExtensao;
	}

	public void setDistribuicaoExtensao(DistribuicaoAtividadeExtensao distribuicaoExtensao) {
		this.distribuicaoExtensao = distribuicaoExtensao;
	}

	public Collection<AtividadeExtensao> getAtividades() {
		return atividades;
	}

	public void setAtividades(Collection<AtividadeExtensao> atividades) {
		this.atividades = atividades;
	}
	
	public String getMsgCoordenadorPendenteRelatorio() {
		return msgCoordenadorPendenteRelatorio;
	}

	public void setMsgCoordenadorPendenteRelatorio(
			String msgCoordenadorPendenteRelatorio) {
		this.msgCoordenadorPendenteRelatorio = msgCoordenadorPendenteRelatorio;
	}

	public Date getNovaDataFinalizacaoAcao() {
	    return novaDataFinalizacaoAcao;
	}

	public void setNovaDataFinalizacaoAcao(Date novaDataFinalizacaoAcao) {
	    this.novaDataFinalizacaoAcao = novaDataFinalizacaoAcao;
	}

}