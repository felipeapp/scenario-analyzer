/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 13/10/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;

/**
 *
 * <p> Passa os dados para o processador que vai alterar dados de de vários materiais ao mesmo tempo</p>
 *
 * 
 * @author jadson
 *
 */
public class MovimentoAlteraVariosMateriais extends AbstractMovimentoAdapter{

	
	// Variáveis que indicam qual o campo que vai ser alterado, apenas um campo por vez pode ser alterado.
	/** Indica que o campo número de chamada vai ser alterado */
	private boolean alterarNumeroChamada = false;
	/** Indica que o campo segunda localização vai ser alterado */
	private boolean alterarSegundaLocalizacao = false;
	/** Indica que o campo nota geral vai ser alterado */
	private boolean alterarNotaGeral = false;
	/** Indica que o campo nota de usuário vai ser alterado */
	private boolean alterarNotaUsuario = false;
	/** Indica que o campo coleção vai ser alterado */
	private boolean alterarColecao = false;
	/** Indica que o campo situação vai ser alterado */
	private boolean alterarSituacao = false;
	/** Indica que o campo status vai ser alterado */
	private boolean alterarStatus = false;
	/** Indica que o campo tipo de material vai ser alterado */
	private boolean alterarTipoMaterial = false;
	/** Indica que o campo nota de tese/dissertação vai ser alterado */
	private boolean alterarNotaTeseDissertacao = false;
	/** Indica que o campo nota de conteúdo vai ser alterado */
	private boolean alterarNotaConteudo = false;
	/** Indica que o campo número do volume vai ser alterado */
	private boolean alterarNumeroVolume = false;
	/** Indica que o campo ano cronológico vai ser alterado */
	private boolean alterarAnoCronologico = false;
	/** Indica que o campo ano vai ser alterado */
	private boolean alterarAno = false;
	/** Indica que o campo volume vai ser alterado */
	private boolean alterarVolume = false;
	/** Indica que o campo número vai ser alterado */
	private boolean alterarNumero = false;
	/** Indica que o campo edição vai ser alterado */
	private boolean alterarEdicao = false;
	/** Indica que o campo descrição suplemento vai ser alterado */
	private boolean alterarDescricaoSuplemento = false;
	
	/**
	 * Os materiais que vão ser alterados
	 */
	private List<MaterialInformacional> materiaisAlteracao;
	
	public MovimentoAlteraVariosMateriais(List<MaterialInformacional> materiaisAlteracao){
		this.materiaisAlteracao = materiaisAlteracao;
	}

	public boolean isAlterarNumeroChamada() {
		return alterarNumeroChamada;
	}

	public void setAlterarNumeroChamada(boolean alterarNumeroChamada) {
		this.alterarNumeroChamada = alterarNumeroChamada;
	}

	public boolean isAlterarSegundaLocalizacao() {
		return alterarSegundaLocalizacao;
	}

	public void setAlterarSegundaLocalizacao(boolean alterarSegundaLocalizacao) {
		this.alterarSegundaLocalizacao = alterarSegundaLocalizacao;
	}

	public boolean isAlterarNotaGeral() {
		return alterarNotaGeral;
	}

	public void setAlterarNotaGeral(boolean alterarNotaGeral) {
		this.alterarNotaGeral = alterarNotaGeral;
	}

	public boolean isAlterarNotaUsuario() {
		return alterarNotaUsuario;
	}

	public void setAlterarNotaUsuario(boolean alterarNotaUsuario) {
		this.alterarNotaUsuario = alterarNotaUsuario;
	}

	public boolean isAlterarColecao() {
		return alterarColecao;
	}

	public void setAlterarColecao(boolean alterarColecao) {
		this.alterarColecao = alterarColecao;
	}

	public boolean isAlterarSituacao() {
		return alterarSituacao;
	}

	public void setAlterarSituacao(boolean alterarSituacao) {
		this.alterarSituacao = alterarSituacao;
	}

	public boolean isAlterarStatus() {
		return alterarStatus;
	}

	public void setAlterarStatus(boolean alterarStatus) {
		this.alterarStatus = alterarStatus;
	}

	public boolean isAlterarTipoMaterial() {
		return alterarTipoMaterial;
	}

	public void setAlterarTipoMaterial(boolean alterarTipoMaterial) {
		this.alterarTipoMaterial = alterarTipoMaterial;
	}

	public boolean isAlterarNotaTeseDissertacao() {
		return alterarNotaTeseDissertacao;
	}

	public void setAlterarNotaTeseDissertacao(boolean alterarNotaTeseDissertacao) {
		this.alterarNotaTeseDissertacao = alterarNotaTeseDissertacao;
	}

	public boolean isAlterarNotaConteudo() {
		return alterarNotaConteudo;
	}

	public void setAlterarNotaConteudo(boolean alterarNotaConteudo) {
		this.alterarNotaConteudo = alterarNotaConteudo;
	}

	public boolean isAlterarNumeroVolume() {
		return alterarNumeroVolume;
	}

	public void setAlterarNumeroVolume(boolean alterarNumeroVolume) {
		this.alterarNumeroVolume = alterarNumeroVolume;
	}

	public boolean isAlterarAnoCronologico() {
		return alterarAnoCronologico;
	}

	public void setAlterarAnoCronologico(boolean alterarAnoCronologico) {
		this.alterarAnoCronologico = alterarAnoCronologico;
	}

	public boolean isAlterarAno() {
		return alterarAno;
	}

	public void setAlterarAno(boolean alterarAno) {
		this.alterarAno = alterarAno;
	}

	public boolean isAlterarVolume() {
		return alterarVolume;
	}

	public void setAlterarVolume(boolean alterarVolume) {
		this.alterarVolume = alterarVolume;
	}

	public boolean isAlterarNumero() {
		return alterarNumero;
	}

	public void setAlterarNumero(boolean alterarNumero) {
		this.alterarNumero = alterarNumero;
	}

	public boolean isAlterarEdicao() {
		return alterarEdicao;
	}

	public void setAlterarEdicao(boolean alterarEdicao) {
		this.alterarEdicao = alterarEdicao;
	}

	public boolean isAlterarDescricaoSuplemento() {
		return alterarDescricaoSuplemento;
	}

	public void setAlterarDescricaoSuplemento(boolean alterarDescricaoSuplemento) {
		this.alterarDescricaoSuplemento = alterarDescricaoSuplemento;
	}

	public List<MaterialInformacional> getMateriaisAlteracao() {
		return materiaisAlteracao;
	}
	
}
