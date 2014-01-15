package br.ufrn.sigaa.prodocente.jsf;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.prodocente.producao.dominio.Patente;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Controlador respons�vel pelo carregamento dos dados das produ��es.
 * 
 * @author guerethes
 */
@Component @Scope("request")
public class CarregarDadosProducoesMBean extends SigaaAbstractController<Producao>{
	
	/**
	 * M�todo cuja a responsabilidade � carregara produ��o
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/Artigo/lista.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/AudioVisual/lista.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/Banca/lista_concurso.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/Banca/lista_curso.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/BolsaObtida/lista.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/Capitulo/lista.jsp</li> 
	 * 	 	<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/ExposicaoApresentacao/lista.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/Livro/lista.jsp</li>
	 *  	<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/MaquetePrototipoOutro/lista.jsp</li>
	 *  	<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/Montagem/lista.jsp</li>
	 *  	<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/ParticipacaoColegiadoComissao/lista.jsp</li>
	 *  	<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/ParticipacaoComissaoOrgEventos/lista.jsp</li>
	 *  	<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/ParticipacaoSociedade/lista.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/Patente/lista.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/PremioRecebido/lista.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/ProgramacaoVisual/lista.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/PublicacaoEvento/lista.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/TextoDidatico/lista.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/producao/VisitaCientifica/lista.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 */
	public Producao carregar() throws DAOException {
		obj = new Producao();
		obj.setId(RequestUtils.getParameterInt("id", (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())); 
		setObj(getGenericDAO().findByPrimaryKey(obj.getId(), Producao.class));
		popularProducao(obj);
		return obj;
	}
	
	/**
	 * Popular atributos lazy da produ��o
	 *
	 * @param p
	 */
	private void popularProducao(Producao p) {
		if (p instanceof Patente) {
			((Patente) p).getPatrocinadora().iterator();
		}
	}

	/**
	 * Verifica se a produ��o � do tipo Artigo.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/include_painel.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isArtigo(){
		if (obj != null && obj.getTipoProducao().getId() == TipoProducao.ARTIGO_PERIODICO_JORNAIS_SIMILARES.getId()) 
			return true;
		else
			return false;
	}
	
	/**
	 * Verifica se a produ��o de do tipo Capitulo de Livro.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/include_painel.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isCaptuloLivro(){
		if (obj != null && obj.getTipoProducao().getId() == TipoProducao.CAPITULO_LIVROS.getId()) 
			return true;
		else
			return false;
	}
	
	/**
	 * Verifica se a produ��o de do tipo Livro.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/include_painel.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isLivro(){
		if (obj != null && obj.getTipoProducao().getId() == TipoProducao.LIVRO.getId()) 
			return true;
		else
			return false;
	}

	/**
	 * Verifica se a produ��o � do tipo Livro.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/include_painel.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isEvento(){
		if (obj != null && obj.getTipoProducao().getId() == TipoProducao.PUBLICACOES_EVENTOS.getId()) 
			return true;
		else
			return false;
	}

	/**
	 * Verifica se a produ��o � do tipo Texto did�tico
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/include_painel.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isTextoDidatico() {
		if (obj != null && obj.getTipoProducao().getId() == TipoProducao.TEXTO_DIDATICO.getId()) 
			return true;
		else
			return false;
	}

	/**
	 * Verifica se a produ��o � do tipo Audio Visual.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/include_painel.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isAudioVisual() {
		if (obj != null && obj.getTipoProducao().getId() == TipoProducao.AUDIO_VISUAIS.getId()) 
			return true;
		else
			return false;
	}

	/**
	 * Verifica se a produ��o � do tipo Exposi��o Apresenta��o.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/include_painel.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isExposicaoApresentacao() {
		if (obj != null && obj.getTipoProducao().getId() == TipoProducao.EXPOSICAO_APRESENTACAO_ARTISTICAS.getId()) 
			return true;
		else
			return false;
	}

	/**
	 * Verifica se a produ��o � do tipo Montagem.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/include_painel.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isMontagem() {
		if (obj != null && obj.getTipoProducao().getId() == TipoProducao.MONTAGENS.getId()) 
			return true;
		else
			return false;
	}

	/**
	 * Verifica se a produ��o � do tipo Programa��o Visual.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/include_painel.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isProgramacaoVisual() {
		if (obj != null && obj.getTipoProducao().getId() == TipoProducao.PROGRAMACAO_VISUAL.getId()) 
			return true;
		else
			return false;
	}

	/**
	 * Verifica se a produ��o � do tipo Maquete Prototipo.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/include_painel.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isMaquetePrototipo() {
		if (obj != null && obj.getTipoProducao().getId() == TipoProducao.MAQUETES_PROTOTIPOS_OUTROS.getId()) 
			return true;
		else
			return false;
	}
	
	/**
	 * Verifica se a produ��o � do tipo Patente.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/include_painel.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isPatente() {
		if (obj != null && obj.getTipoProducao().getId() == TipoProducao.PATENTE.getId()) 
			return true;
		else
			return false;
	}

	/**
	 * Verifica se a produ��o � do tipo Trabalho de Conclus�o
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/include_painel.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isTrabalhoConclusao() {
		if (obj != null && obj.getTipoProducao().getId() == TipoProducao.BANCA_CURSO_SELECOES.getId()) 
			return true;
		else
			return false;
	}

	/**
	 * Verifica se a produ��o � do tipo Comiss�o Julgadora.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/include_painel.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isComissaoJulgadora() {
		if (obj != null && obj.getTipoProducao().getId() == TipoProducao.BANCA_CONCURSO.getId()) 
			return true;
		else
			return false;
	}

	/**
	 * Verifica se a produ��o � do tipo Pr�mio.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/include_painel.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isPremio() {
		if (obj != null && obj.getTipoProducao().getId() == TipoProducao.PREMIO_RECEBIDO.getId()) 
			return true;
		else
			return false;
	}

	/**
	 * Verifica se a produ��o � do tipo Bolsa Obtida.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/include_painel.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isBolsaObtida() {
		if (obj != null && obj.getTipoProducao().getId() == TipoProducao.BOLSA_OBTIDA.getId()) 
			return true;
		else
			return false;
	}

	/**
	 * Verifica se a produ��o � do tipo Visita Cient�fica.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/include_painel.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isVisitaCientifica() {
		if (obj != null && obj.getTipoProducao().getId() == TipoProducao.VISITA_CIENTIFICA.getId()) 
			return true;
		else
			return false;
	}

	/**
	 * Verifica se a produ��o � do tipo Participa��o Evento.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/include_painel.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isParticipacaoEvento() {
		if (obj != null && obj.getTipoProducao().getId() == TipoProducao.PARTICIPACAO_COMISSAO_ORGANIZACAO_EVENTO.getId()) 
			return true;
		else
			return false;
	}

	/**
	 * Verifica se a produ��o � do tipo Participa��o Sociedade.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/include_painel.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isParticipacaoSociedade() {
		if (obj != null && obj.getTipoProducao().getId() == TipoProducao.PARTICIPACAO_SOCIEDADE_CIENTIFICA_CULTURAIS.getId()) 
			return true;
		else
			return false;
	}

	/**
	 * Verifica se a produ��o � do tipo Participa��o Colegiado.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/include_painel.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isParticipacaoColegiado() {
		if (obj != null && obj.getTipoProducao().getId() == TipoProducao.PARTICIPACAO_COLEGIADOS_COMISSOES.getId()) 
			return true;
		else
			return false;
	}

}