/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 29/09/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.administracao.jsf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.administracao.dao.CamposAtualizacaoCacheTitulos;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf.ClassificacaoBibliograficaMBean;
import br.ufrn.sigaa.biblioteca.timer.AtualizaCacheMARCTitulosThread;
import br.ufrn.sigaa.biblioteca.util.ClassificacoesBibliograficasUtil;

/**
 *
 * <p> Mbean reponsável por atualizar a tabela de cache mantida no sistema para otimizar as pesquisa no acervo </p>
 * 
 * <p> <i> Sempre que as informações de novo campo precisa ser mostrado ao usuário ou foi adicionado um novo campo na pesquisa do acervo 
 * na bilioteca que usa a tabela cache, essa informação precisar ser atualizada em todos os objetos cache existentes. Esse caso de uso foi 
 * criado para isso. Para não precisar ficar criando sqls ou classes para atualizar o cache.</i> </p>
 * 
 * @author jadson
 * @version 1.5 Adicionando o campo idioma no cache. Alterando a lógica para não permitir o usuário selecionar mais de um campo para realizar a atualização 
 *              e o sistema só vai executar a atualização e o valor da coluna no banco calculado for diferente do valor atual.  Não vai mais atualizar tudo, 
 *              para melhorar a performace, porque esse caso de uso estava muito lento. 
 */
@Component("atualizaCacheMARCTitulosMBean")
@Scope("request")
public class AtualizaCacheMARCTitulosMBean extends SigaaAbstractController<CacheEntidadesMarc> {
	
	/** Formulário para o usuário executar o caso de uso */
	public static final String PAGINA_AGENDA_ATUALIZACAO_CACHE = "/biblioteca/administracao/agendaAtualizacaoCache.jsp";
	
	/** O email para o qual as informações da atualização vão ser enviadas no final do processo */
	private String email;
	
	/** O email para o qual as informações da atualização vão ser enviadas no final do processo */
	private Date horaExecucao;
	
	/** Indica a posição do campo selecionado pelo usuário*/
	private int posicaoCampoSelecionado;
	
	/**
	 * Os campos que o usuário pode alterar
	 */
	private List<CamposAtualizacaoCacheTitulos> camposCache = new ArrayList<CamposAtualizacaoCacheTitulos>();
	
	/**
	 * Os campos que o usuário escolheu para serem alterados
	 */
	private CamposAtualizacaoCacheTitulos campoAtualizacao = null;
	

	/** Guarda em cache a lista de classificações bibliográfica utilizadas no sistema para não precisar busca sempre no banco*/
	private List<ClassificacaoBibliografica> classificacoesUtilizadas = new ArrayList<ClassificacaoBibliografica>(); 
	
	/**
	 * <p>Inicia o caso de uso de atualizar o cache dos títulos. </p>
	 * 
	 * <p> <strong>Sempre que adicionar uma novo campo no cache para buscas ou visualização, adicionar nesse método para sua atualização pode ser via sistema. </strong> </p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/administracao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarAgendamentoAtualizacao() throws DAOException{	
		
		ClassificacaoBibliograficaMBean beanClassificacao = getMBean("classificacaoBibliograficaMBean"); 
		
		classificacoesUtilizadas = (List<ClassificacaoBibliografica>) beanClassificacao.getClassificacoesAtivas();
		
		camposCache.add(CamposAtualizacaoCacheTitulos.TITULO);
		camposCache.add(CamposAtualizacaoCacheTitulos.SUB_TITULO);
		camposCache.add(CamposAtualizacaoCacheTitulos.MEIO_PUBLICACAO);
		camposCache.add(CamposAtualizacaoCacheTitulos.TITULO_ASCII);
		camposCache.add(CamposAtualizacaoCacheTitulos.FORMAS_VARIANTES_DO_TITULO);
		camposCache.add(CamposAtualizacaoCacheTitulos.TITULO_UNIFORME);
		camposCache.add(CamposAtualizacaoCacheTitulos.AUTOR);
		camposCache.add(CamposAtualizacaoCacheTitulos.AUTOR_SECUNDARIO);
		camposCache.add(CamposAtualizacaoCacheTitulos.ASSUNTO);
		camposCache.add(CamposAtualizacaoCacheTitulos.NUMERO_CHAMADA);
		camposCache.add(CamposAtualizacaoCacheTitulos.ISBN);
		camposCache.add(CamposAtualizacaoCacheTitulos.ISSN);
		
		if(beanClassificacao.isSistemaUtilizandoClassificacao1()){
			
			ClassificacaoBibliografica primeira = ClassificacoesBibliograficasUtil.encontraClassificacaoByCampo
					(classificacoesUtilizadas, ClassificacaoBibliografica.OrdemClassificacao.PRIMERA_CLASSIFICACAO);
			
			CamposAtualizacaoCacheTitulos campo = CamposAtualizacaoCacheTitulos.CLASSIFICACAO1;
			
			// Configura dimanicamente a string de busca
			campo.setSqlBuscaInformacoesMontarCampo(campo.getSqlBuscaInformacoesMontarCampo()
					.replace(":campo", primeira.getCampoMARC().getCampo()).replace(":subcampo", ""+primeira.getCampoMARC().getSubCampo()));
			camposCache.add(campo);
		}
		
		if(beanClassificacao.isSistemaUtilizandoClassificacao2()){
			
			ClassificacaoBibliografica segunda = ClassificacoesBibliograficasUtil.encontraClassificacaoByCampo
			(classificacoesUtilizadas, ClassificacaoBibliografica.OrdemClassificacao.SEGUNDA_CLASSIFICACAO);
			
			CamposAtualizacaoCacheTitulos campo = CamposAtualizacaoCacheTitulos.CLASSIFICACAO2;
			
			// Configura dimanicamente a string de busca
			campo.setSqlBuscaInformacoesMontarCampo(campo.getSqlBuscaInformacoesMontarCampo()
					.replace(":campo", segunda.getCampoMARC().getCampo()).replace(":subcampo", ""+segunda.getCampoMARC().getSubCampo()));
			
			camposCache.add(campo);
		
		}
		
		if(beanClassificacao.isSistemaUtilizandoClassificacao3()){
			
			ClassificacaoBibliografica terceira = ClassificacoesBibliograficasUtil.encontraClassificacaoByCampo
			(classificacoesUtilizadas, ClassificacaoBibliografica.OrdemClassificacao.TERCEIRA_CLASSIFICACAO);
			
			CamposAtualizacaoCacheTitulos campo = CamposAtualizacaoCacheTitulos.CLASSIFICACAO3;
			
			// Configura dimanicamente a string de busca
			campo.setSqlBuscaInformacoesMontarCampo(campo.getSqlBuscaInformacoesMontarCampo()
					.replace(":campo", terceira.getCampoMARC().getCampo()).replace(":subcampo", ""+terceira.getCampoMARC().getSubCampo()));
			
			camposCache.add(campo);
		}
		
		
		camposCache.add(CamposAtualizacaoCacheTitulos.DESCRICAO_FISICA);
		camposCache.add(CamposAtualizacaoCacheTitulos.SERIE);
		camposCache.add(CamposAtualizacaoCacheTitulos.EDICAO);
		camposCache.add(CamposAtualizacaoCacheTitulos.LOCAL_PUBLICACAO);
		camposCache.add(CamposAtualizacaoCacheTitulos.EDITORA);
		camposCache.add(CamposAtualizacaoCacheTitulos.IDIOMA); // Adicionado em 22/11/2011  - #71567 Busca avançada
		camposCache.add(CamposAtualizacaoCacheTitulos.ANO);
		camposCache.add(CamposAtualizacaoCacheTitulos.ANO_PUBLICACAO);
		camposCache.add(CamposAtualizacaoCacheTitulos.NOTAS_CONTEUDO);
		camposCache.add(CamposAtualizacaoCacheTitulos.NOTAS_GERAIS);
		camposCache.add(CamposAtualizacaoCacheTitulos.NOTAS_LOCAIS);
		camposCache.add(CamposAtualizacaoCacheTitulos.NOTAS_ASCII);
		camposCache.add(CamposAtualizacaoCacheTitulos.ENDERECO_ELETRONICO);
		camposCache.add(CamposAtualizacaoCacheTitulos.RESUMO);
		
		return forward(PAGINA_AGENDA_ATUALIZACAO_CACHE);
	}

	
	/**
	 * Inicia o thread que vai executa a atualização das informações do cache no horário agendado
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/agendaAtualizacaoCache.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String atualizarCacheTitulos(){
		
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
		
		
		for (CamposAtualizacaoCacheTitulos campo : camposCache) {
			if(campo.getPosicao() == posicaoCampoSelecionado)
				campoAtualizacao = campo ;
		}
		
		if(campoAtualizacao == null){
			addMensagemErro("Selecione um campo para ser atualizado !");
			return null;
		}
		
		SimpleDateFormat formatador = new SimpleDateFormat("HH:mm");
		
		AtualizaCacheMARCTitulosThread atualizaCache = new AtualizaCacheMARCTitulosThread(email, horaExecucao, campoAtualizacao);
		
		Thread execucao = new Thread(atualizaCache);
		execucao.start();
		
		addMensagemInformation("O Cache de Títulos será atualizado as "+formatador.format(horaExecucao)+". Será enviado um email de confirmação para o endereço: "+email);
		
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

	public List<CamposAtualizacaoCacheTitulos> getCamposCache() {
		return camposCache;
	}

	public void setCamposCache(List<CamposAtualizacaoCacheTitulos> camposCache) {
		this.camposCache = camposCache;
	}


	public int getPosicaoCampoSelecionado() {
		return posicaoCampoSelecionado;
	}

	public void setPosicaoCampoSelecionado(int posicaoCampoSelecionado) {
		this.posicaoCampoSelecionado = posicaoCampoSelecionado;
	}
	
	
	
}
