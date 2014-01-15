/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 09/05/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.administracao.jsf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.administracao.dao.CamposAtualizacaoCacheArtigos;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.timer.AtualizaCacheMARCArtigosThread;

/**
 *  <p> Mbean reponsável por atualizar a tabela de cache para artigos mantida no sistema para otimizar as pesquisa no acervo </p>
 * 
 * @author jadson
 *
 */
@Component("atualizaCacheMARCArtigosMBean")
@Scope("request")
public class AtualizaCacheMARCArtigosMBean extends SigaaAbstractController<CacheEntidadesMarc> {
	
	
	/** Formulário para o usuário executar o caso de uso */
	public static final String PAGINA_AGENDA_ATUALIZACAO_CACHE = "/biblioteca/administracao/agendaAtualizacaoCacheArtigos.jsp";
	
	/** O email para o qual as informações da atualização vão ser enviadas no final do processo */
	private String email;
	
	/** O email para o qual as informações da atualização vão ser enviadas no final do processo */
	private Date horaExecucao;
	
	
	/**
	 * Os campos que o usuário escolheu para serem alterados
	 */
	private CamposAtualizacaoCacheArtigos campoAtualizacao = null;
	
	/**
	 * Os campos que o usuário pode alterar
	 */
	private List<CamposAtualizacaoCacheArtigos> camposCache = new ArrayList<CamposAtualizacaoCacheArtigos>();
	
	
	/** Indica a posição do campo selecionado pelo usuário*/
	private int posicaoCampoSelecionado;
	
	
	/**
	 * Inicia o caso de uso de atualizar o cache dos artigos
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/agendaAtualizacaoCacheArtigos.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String iniciarAgendamentoAtualizacao(){	
		camposCache.add(CamposAtualizacaoCacheArtigos.TITULO);
		camposCache.add(CamposAtualizacaoCacheArtigos.AUTOR);
		camposCache.add(CamposAtualizacaoCacheArtigos.AUTORES_SECUNDARIOS);
		camposCache.add(CamposAtualizacaoCacheArtigos.LOCAL_PUBLICACAO);
		camposCache.add(CamposAtualizacaoCacheArtigos.EDITORA);
		camposCache.add(CamposAtualizacaoCacheArtigos.ANO_PUBLICACAO);
		camposCache.add(CamposAtualizacaoCacheArtigos.PALAVRAS_CHAVES);
		camposCache.add(CamposAtualizacaoCacheArtigos.INTERVALO_DE_PAGINAS);
		camposCache.add(CamposAtualizacaoCacheArtigos.RESUMO);
		
		return forward(PAGINA_AGENDA_ATUALIZACAO_CACHE);
	}
	
	
	
	/**
	 * Inicia o thread que vai executa a atualização das informações do cache no horário agendado
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/agendaAtualizacaoCacheArtigos.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String atualizarCacheArtigos(){
		
		boolean erroValidacao = false;
		
		if(horaExecucao == null){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Hora do Agendamento"); 
			erroValidacao = true;
		}
		
		if(StringUtils.isEmpty(email)){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Email"); 
			erroValidacao = true;
		}
		
		if(erroValidacao)
			return null;
		
		for (CamposAtualizacaoCacheArtigos campo : camposCache) {
			if(campo.getPosicao() == posicaoCampoSelecionado)
				campoAtualizacao = campo ;
		}
		
		if(campoAtualizacao == null){
			addMensagemErro("Selecione um campo para ser atualizado !");
			return null;
		}
		
		SimpleDateFormat formatador = new SimpleDateFormat("HH:mm");
		
		AtualizaCacheMARCArtigosThread atualizaCache = new AtualizaCacheMARCArtigosThread(email, horaExecucao, campoAtualizacao);
		
		Thread execucao = new Thread(atualizaCache);
		execucao.start();
		
		addMensagemInformation("O Cache de Artigos será atualizado as "+formatador.format(horaExecucao)+". Será enviado um email de confirmação para o endereço: "+email);
		
		return cancelar();
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getHoraExecucao() {
		return horaExecucao;
	}

	public void setHoraExecucao(Date horaExecucao) {
		this.horaExecucao = horaExecucao;
	}

	public CamposAtualizacaoCacheArtigos getCampoAtualizacao() {
		return campoAtualizacao;
	}

	public void setCampoAtualizacao(CamposAtualizacaoCacheArtigos campoAtualizacao) {
		this.campoAtualizacao = campoAtualizacao;
	}
	
	public List<CamposAtualizacaoCacheArtigos> getCamposCache() {
		return camposCache;
	}

	public void setCamposCache(List<CamposAtualizacaoCacheArtigos> camposCache) {
		this.camposCache = camposCache;
	}
	
	public int getPosicaoCampoSelecionado() {
		return posicaoCampoSelecionado;
	}
	
	public void setPosicaoCampoSelecionado(int posicaoCampoSelecionado) {
		this.posicaoCampoSelecionado = posicaoCampoSelecionado;
	}
	
}
