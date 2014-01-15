<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@page import="br.ufrn.comum.dominio.Sistema"%>


<div class="descricaoOperacao">
	Gerencia a parte de solicitações, realização de compras e tombamento de materiais para o acervo
do sistemas de bibliotecas da ${ configSistema['siglaInstituicao'] }.

</div>

<ul>
	
	<%-- Chama a action do struts LogonSistemaAction que cria um passaporte e abre o sipac na pagina principal da biblioteca --%>
	<% if (Sistema.isSipacAtivo()) { %>
		<li> <ufrn:link action="entrarSistema" param="sistema=sipac&url=/sipac/requisicoes/biblioteca/index.jsf"> Acessar o Módulo de Biblioteca do ${ configSistema['siglaSipac'] } </ufrn:link> </li>
	<% } %>
	
	<br/><br/><br/>
	
	<li> Assinatura de Periódicos
		<ul>
			<li> <h:commandLink value="Listar/Criar/Alterar Assinaturas" action="#{assinaturaPeriodicoMBean.iniciarPesquisaAssinaturas}"  onclick="setAba('aquisicoes')"/> </li>
	 		<li> <h:commandLink value="Realizar Renovações de Assinaturas" action="#{renovaAssinaturaPeriodicoMBean.iniciarRenovacaoAssinatura}"  onclick="setAba('aquisicoes')"/> </li>
	   		<li> <h:commandLink value="Remover Assinaturas" action="#{removeAssinaturaPeriodicosMBean.iniciarRemocaoAssinatura}"  onclick="setAba('aquisicoes')"/> </li>
		</ul>
	</li>
	
	
	<li> Registro de Chegada de Fascículos
		<ul>
			<li> <h:commandLink action="#{registraChegadaFasciculoMBean.iniciarRegistroChegadaFasciculo}" value="Registrar Chegada de Fascículos" onclick="setAba('aquisicoes')"/> </li>
	   	    <li> <h:commandLink value="Alterar / Remover Fascículos Registrados" action="#{registraChegadaFasciculoMBean.iniciarBuscaAlteracaoFasciculosRegistrados}"  onclick="setAba('aquisicoes')"/> </li>
		</ul>
	</li>
	
	<br/><br/><br/><br/><br/><br/>
	
	<li> Associação entre Títulos (Catalogações) e Assinaturas
		<ul>
			<li> <h:commandLink action="#{associaAssinaturaATituloMBean.iniciaAssociacaoEntreAssinaturaETitulo}" value="Associar Assinatura a um Título (Catalogação)" onclick="setAba('aquisicoes')"/> </li> 
			<li> <h:commandLink action="#{associaAssinaturaATituloMBean.iniciaAlteracaoAssociacaoEntreAssinaturaETitulo}" value="Listar/ Alterar Associações" onclick="setAba('aquisicoes')"/> </li>
		</ul> 
	</li>
	
	<li>Periodicidade
		<ul>
			<li><h:commandLink action="#{frequenciaPeriodicosMBean.listar}" onclick="setAba('aquisicoes')" value="Listar/Cadastrar Nova Periodicidade" /></li>
		</ul>	
	</li>
	
	
	<li>Gerenciar os Códigos de Barras dos Fascículos
	 	<ul>
			<li><h:commandLink action="#{gerenciarCodigosBarrasFasciculosMBean.iniciaReOrganizarCodigosBarras}" onclick="setAba('aquisicoes')" value="Reorganizar os Códigos de Barras" /></li>
		</ul>
	</li> 
	 
	 
	 
</ul>