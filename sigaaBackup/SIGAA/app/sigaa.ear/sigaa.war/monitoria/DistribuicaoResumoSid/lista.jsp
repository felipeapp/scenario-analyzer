<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<%@page import="br.ufrn.sigaa.monitoria.dominio.StatusRelatorio"%>
<f:view>
	<h2><ufrn:subSistema /> > Distribuir Resumos do Seminário de iniciação à Docência</h2>

	<h:outputText value="#{distribuicaoResumoSid.create}"/>
	<h:outputText value="#{projetoMonitoria.create}"/>	
	
 	<h:messages showDetail="true" showSummary="true" />
 	
 	<div class="descricaoOperacao">
 		<b>Atenção:</b><br/>
		 Localize o resumo através da busca abaixo e clique no ícone para distribui-lo aos Membros da Comissão de Monitoria ou Científica.
		 Somente poderão ser distribuídos para Comissão de Monitoria os Resumos do SID de Projetos de Ensino cuja situação sejam:<br/>
		 'AGUARDANDO DISTRIBUIÇÃO', 'AGUARDANDO AVALIAÇÃO'.<br/>
		 Os demais projetos NÃO poderão ser distribuídos, somente visualizados.<br/>
	</div>
	<br/>

	
 	<h:form id="formBusca">

	<table class="formulario" width="90%">
	<caption>Busca por Resumos de Projeto</caption>
	<tbody>
	
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{resumoSid.checkBuscaProjeto}" id="selectBuscaProjeto"/>
			</td>
		
			<td><label> Projeto: </label></td>
			<td>
				 <h:inputText id="tituloProjeto" value="#{ resumoSid.tituloProjeto }" size="60"  onchange="javascript:$('formBusca:selectBuscaProjeto').checked = true;"/>
			</td>
		</tr>		

	    <tr>
			<td>
			<h:selectBooleanCheckbox value="#{resumoSid.checkBuscaServidor}" id="selectBuscaServidor"/>
			</td>
	    	<td> <label for="nome"> Coordenador(a): </label> </td>
	    	<td> 
	    	
				<h:inputText value="#{resumoSid.buscaServidor.pessoa.nome}" id="nome" size="60" onchange="javascript:$('formBusca:selectBuscaServidor').checked = true;"/>
				<h:inputHidden value="#{resumoSid.buscaServidor.id}" id="idServidor"/>
	
				<ajax:autocomplete source="formBusca:nome" target="formBusca:idServidor"
					baseUrl="/sigaa/ajaxServidor" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
					parser="new ResponseXmlToHtmlListParser()" />
	
				<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>	    	
	    	
	    	</td>
	    </tr>

		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{resumoSid.checkBuscaAno}" id="selectBuscaAno" />

			</td>
	    	<td> <label for="anoProjeto"> Ano do Projeto </label> </td>
	    	<td> <h:inputText value="#{resumoSid.buscaAnoProjeto}" size="10" onchange="javascript:$('formBusca:selectBuscaAno').checked = true;"/></td>
	    </tr>




	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
			<h:commandButton value="Buscar" action="#{ resumoSid.buscar }"/>
			<h:commandButton value="Cancelar" action="#{ resumoSid.cancelar }"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>

	<br/>
	<br/>

	<c:set var="resumos" value="#{resumoSid.resumos}"/>


	<c:if test="${empty resumos}">
		<center><i> Nenhum Resumo localizado </i></center>
	</c:if>


	<c:if test="${not empty resumos}">

		<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif"style="overflow: visible;" />: Visualizar Resumo
		    <h:graphicImage value="/img/seta.gif"style="overflow: visible;" rendered="#{acesso.monitoria}"/><h:outputText rendered="#{acesso.monitoria}" value=": Distribuir Resumos para Avaliação"/>	    	</div>
		<br/>
	
		 <table class="listagem">
		    <caption>Resumos SID de Projetos de Ensino Encontrados (${fn:length(resumos)})</caption>
	
		      <thead>
		      	<tr>
		      		<th><h:outputText value="#" title="Total de avaliadores"/></th>
		      		<th>Ano</th>
		        	<th>Projeto</th>
		        	<th>SID</th>
		        	<th width="25%">Situação Resumo</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        </tr>
		 	</thead>
		 	<tbody>
	
			<c:set var="AGUARDANDO_DISTRIBUICAO" value="<%= String.valueOf(StatusRelatorio.AGUARDANDO_DISTRIBUICAO) %>" scope="application"/>
			<c:set var="AGUARDANDO_AVALIACAO" value="<%= String.valueOf(StatusRelatorio.AGUARDANDO_AVALIACAO) %>" scope="application"/>
	
	       	<c:forEach items="#{resumos}" var="resumo" varStatus="status">	       	
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	
						<td><h:outputText value="#{resumo.totalAvaliadoresResumoSidAtivos}" rendered="#{resumo.totalAvaliadoresResumoSidAtivos > 0}" title="Total de Avaliadores"/></td>
	                    <td> ${resumo.projetoEnsino.ano}</td>
	                    <td> ${resumo.projetoEnsino.titulo}</td>
	                    
	                    <td width="2%"> ${resumo.anoSid} </td>
						<td> ${resumo.status.descricao} </td>
						<td width="2%">				
								<h:commandLink title="Visualizar Resumo"  action="#{ resumoSid.view }"  id="btView" styleClass="noborder">
								      <f:param name="id" value="#{resumo.id}"/>
								      <h:graphicImage url="/img/view.gif" />
								</h:commandLink>
						</td>
						<td width="2%">
								<h:commandLink title="Distribuir para Avaliar" action="#{ distribuicaoResumoSid.selecionarResumoSid }" id="btDistribuir" styleClass="noborder">
								      <f:param name="id" value="#{resumo.id}"/>
								      <h:graphicImage url="/img/seta.gif" />
								</h:commandLink>									
						</td>	
	              </tr>
	          </c:forEach>
		 	</tbody>
		 </table>
	</c:if>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>