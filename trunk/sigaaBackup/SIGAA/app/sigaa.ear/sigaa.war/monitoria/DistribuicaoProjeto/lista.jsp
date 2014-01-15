<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view>
	<h2><ufrn:subSistema /> > Distribuir Projetos de Ensino</h2>

	<h:outputText value="#{distribuicaoProjeto.create}"/>
		
 	<h:messages />
	<div class="descricaoOperacao">
	 	
		 Localize o projeto atrav�s da busca abaixo e clique no �cone para distribui-lo aos Membros da Comiss�o de Monitoria.
		 Somente poder�o ser distribu�dos os Projeto de Ensino com as seguintes situa��es:<br/>
		 'AGUARDANDO DISTRIBUI��O DO PROJETO', 'AGUARDANDO AVALIA��O' ou 'AVALIADO COM DISCREP�NCIA DE NOTAS'.<br/>
		 
	</div> 

<br/>


	<%@include file="/monitoria/form_busca_projeto.jsp"%>


	<c:set var="projetos" value="#{projetoMonitoria.projetosLocalizados}"/>



	<c:if test="${empty projetos}">
		<center><i> Nenhum Projeto localizado </i></center>
	</c:if>



	<c:if test="${not empty projetos}">

		<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar Projeto de Ensino
		    <h:graphicImage value="/img/seta.gif"style="overflow: visible;" rendered="#{acesso.monitoria}"/><h:outputText rendered="#{acesso.monitoria}" value=": Distribuir Projeto para Avalia��o"/>	    	</div>
		<br/>
	
	
		<h:form>
		 <table class="listagem">
		    <caption>Projetos de Ensino Encontrados (${fn:length(projetos)})</caption>
	
		      <thead>
		      	<tr>
		      		<th>#</th>
		        	<th>Projeto</th>
		        	<th>Tipo</th>
		        	<th>Dimens�o</th>		        	
		        	<th>Situa��o</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        </tr>
		 	</thead>
		 	<tbody>
	
	       	<c:forEach items="#{projetos}" var="projeto" varStatus="status">
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	
						<td><h:outputText value="#{projeto.totalAvaliadoresProjetoAtivos}" rendered="#{projeto.totalAvaliadoresProjetoAtivos > 0}" title="Total de Avaliadores"/></td>
	                    <td> ${projeto.anoTitulo} </td>
	                    <td> ${projeto.tipoProjetoEnsino.sigla} </td> 
	                    <td> ${projeto.projetoAssociado ? 'PROJETO ASSOCIADO' : 'PROJETO ISOLADO'}</td>
						<td> ${projeto.situacaoProjeto.descricao} </td>
						<td width="2%">
								<h:commandLink  title="Visualizar Projeto de Ensino" action="#{ projetoMonitoria.view }" id="btView">
								      <f:param name="id" value="#{projeto.id}"/>
								      <h:graphicImage url="/img/view.gif" />
								</h:commandLink>
						</td>		
						<td width="2%">
								<h:commandLink title="Distribuir Projeto para Avalia��o" action="#{ distribuicaoProjeto.selecionarProjeto }" 
										rendered="#{((acesso.monitoria) && (projeto.permitidoDistribuirComissaoMonitoria))}">
								      <f:param name="id" value="#{projeto.id}"/>
								      <h:graphicImage url="/img/seta.gif" />
								</h:commandLink>
						</td>
	
	              </tr>
	          </c:forEach>
		 	</tbody>
		 </table>
	</h:form>

	</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>