/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 13/11/2009
 *
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioDeMovimentacaoDeUsuariosDao;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 * MBean Respons�vel por gerar Relat�rios de Totais de Usu�rios Cadastrados da Biblioteca <br/>
 * 
 * @author Arlindo Rodrigues
 * @version 2.0 Br�ulio: modifica��es devido �s mudan�as na arquitetura dos Empr�stimos
 * @version 3.0 27/04/2011Jadson: modifica��es devido �s novas mudan�as na arquitetura dos Empr�stimos.  <br/>
 *  <p>Agora as informa��es do v�nculo est�o ligadas ao usu�rio biblioteca. Uma pessoa vai poder ter v�rias contas na biblioteca durante
 *  a sua vida acad�mcia, mas apenas 1 n�o quitada por vez. <br/> 
 *  Assim � poss�vel saber quantos usu�rios de um determinado v�nculo est�o utilizando a biblioteca, e quantos foram quitados. Por isso esse relat�rio vai 
 * passar a se chamar Relat�rio de Movimenta��o de Usu�rios.
 * </p> 
 * Vai conter novos usu�rios cadastrados, usu�riso quitados, e o total ativo,  Antes era Relatorio de Totais Usuarios Cadastrados
 */
@Component("relatorioMotivimentacaoDeUsuariosMBean")
@Scope("request")
public class RelatorioMotivimentacaoDeUsuariosMBean extends AbstractRelatorioBibliotecaMBean{

	/** A p�gina que implementa o relat�rio. */
	private static final String RELATORIO_TOTAIS_USUARIOS_CADASTRADOS = "/biblioteca/controle_estatistico/relatorioMovimentacaoUsuarios.jsp";
	
	/** Total de novos usu�rios no per�odo*/
	private List<Object[]> novosUsuarios;
	
	/** Total de novos usu�rios quitados no per�odo */
	private List<Object[]> usuariosQuitados;
	
	/** Total de usu�rios ativos no sistema */
	private List<Object[]> usuariosAtivos;
	
	/** Inclui todos os usu�rios que utilizando o sistema, os ativos mais os que foram quitados */
	private List<Object[]> usuariosTotais;
	
	/**
	 * Guarda a quantidade total de novos usu�rios
	 */
	private Long qtdTotalNovosUsuarios = 0L;
	
	/**
	 * Guarda a quantidade total de usu�rios quitados
	 */
	private Long qtdTotalUsuariosQuitados = 0L;
	
	/**
	 * Guarda a quantidade total de  usu�rios ativos
	 */
	private Long qtdTotalUsuariosAtivos = 0L;
	
	/**
	 * Guarda a quantidade total de usu�rios do relat�rio (ativos + quitados )
	 */
	private Long qtdTotalGeralUsuarios = 0L;
	
	
	public RelatorioMotivimentacaoDeUsuariosMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	/**
	 * Configura o relat�rio.
	 * <p>Chamado pela seguinte JSP, indiretamente:
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	@Override
	public void configurar() {
		setTitulo("Relat�rio de Movimenta��o Usu�rios por Per�odo");
		setDescricao(
				"<p>A partir das informa��es deste relat�rio � poss�vel verificar a quantidade de novos usu�rios, usu�rios que foram quitados e " +
				"a quantidade de usu�rios que permanecem com a sua conta ativa no sistema. </p>" +
				"<p> Para a obten��o desses dados, o per�odo considerado � a <strong>data em que o usu�rio fez seu cadastro no sistema </strong> e a <strong>data em que ele emitiu o documento " +
				"de quita��o</strong>. </p>");
		
		inicioPeriodo = CalendarUtils.subtrairMeses(new Date(), 1); // A data de in�cio por padr�o come�a no m�s anterior
		
		setFiltradoPorPeriodo(true);
		setFiltradoPorVariasCategoriasDeUsuario(true);
	}

	/**
	 * M�todo Sobrescrito da classe AbstractRelatorioBibliotecaMBean, para gerar o relat�rio de Usu�rios Cadastrados.
	 * M�todo chamado pela seguinte JSP: /biblioteca/controle_estatistico/relatorioTotaisUsuariosCadastrados.jsp
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		
		RelatorioDeMovimentacaoDeUsuariosDao dao = null;
		
		try {
			
			List<VinculoUsuarioBiblioteca> vinculos = new ArrayList<VinculoUsuarioBiblioteca>();
			
			for (String valorVinculo : variasCategoriasDeUsuario) {
				vinculos.add(VinculoUsuarioBiblioteca.getVinculo(Integer.parseInt(valorVinculo)));
			}
			
			dao = getDAO(RelatorioDeMovimentacaoDeUsuariosDao.class);
			
			List<List<Object[]>> listaResultado = dao.findInformacoesMotimentacaoUsuarios(vinculos, inicioPeriodo, fimPeriodo);
			
			novosUsuarios = listaResultado.get(0);
			usuariosQuitados = listaResultado.get(1);
			usuariosAtivos = listaResultado.get(2);
			usuariosTotais = listaResultado.get(3);
			
			qtdTotalNovosUsuarios = 0L;
			for (Object[] dados : novosUsuarios) {
				qtdTotalNovosUsuarios += (Long) dados[1];
			}
			
			qtdTotalUsuariosQuitados = 0L;
			for (Object[] dados : usuariosQuitados) {
				qtdTotalUsuariosQuitados += (Long) dados[1];
			}
			
			qtdTotalUsuariosAtivos = 0L;
			for (Object[] dados : usuariosAtivos) {
				qtdTotalUsuariosAtivos += (Long) dados[1];
			}
			
			qtdTotalGeralUsuarios= 0L;
			for (Object[] dados : usuariosTotais) {
				qtdTotalGeralUsuarios += (Long) dados[1];
			}
			
			
			if (novosUsuarios.size() == 0 && usuariosQuitados.size() == 0 && usuariosAtivos.size() == 0 && usuariosTotais.size() == 0){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			
			return forward(RELATORIO_TOTAIS_USUARIOS_CADASTRADOS);
		
		} finally {
			if (dao != null) dao.close();
		}
	}

	public List<Object[]> getNovosUsuarios() {
		return novosUsuarios;
	}

	public void setNovosUsuarios(List<Object[]> novosUsuarios) {
		this.novosUsuarios = novosUsuarios;
	}

	public List<Object[]> getUsuariosQuitados() {
		return usuariosQuitados;
	}

	public void setUsuariosQuitados(List<Object[]> usuariosQuitados) {
		this.usuariosQuitados = usuariosQuitados;
	}

	public List<Object[]> getUsuariosAtivos() {
		return usuariosAtivos;
	}

	public void setUsuariosAtivos(List<Object[]> usuariosAtivos) {
		this.usuariosAtivos = usuariosAtivos;
	}

	public Long getQtdTotalUsuariosAtivos() {
		return qtdTotalUsuariosAtivos;
	}

	public void setQtdTotalUsuariosAtivos(Long qtdTotalUsuariosAtivos) {
		this.qtdTotalUsuariosAtivos = qtdTotalUsuariosAtivos;
	}

	public Long getQtdTotalNovosUsuarios() {
		return qtdTotalNovosUsuarios;
	}

	public void setQtdTotalNovosUsuarios(Long qtdTotalNovosUsuarios) {
		this.qtdTotalNovosUsuarios = qtdTotalNovosUsuarios;
	}

	public Long getQtdTotalUsuariosQuitados() {
		return qtdTotalUsuariosQuitados;
	}

	public void setQtdTotalUsuariosQuitados(Long qtdTotalUsuariosQuitados) {
		this.qtdTotalUsuariosQuitados = qtdTotalUsuariosQuitados;
	}

	public List<Object[]> getUsuariosTotais() {
		return usuariosTotais;
	}

	public void setUsuariosTotais(List<Object[]> usuariosTotais) {
		this.usuariosTotais = usuariosTotais;
	}

	public Long getQtdTotalGeralUsuarios() {
		return qtdTotalGeralUsuarios;
	}

	public void setQtdTotalGeralUsuarios(Long qtdTotalGeralUsuarios) {
		this.qtdTotalGeralUsuarios = qtdTotalGeralUsuarios;
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos1ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos1ComboBox() {
		return null;
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#getAgrupamentos2ComboBox()
	 */
	
	@Override
	public Collection<SelectItem> getAgrupamentos2ComboBox() {
		return null;
	}

	
	

}
