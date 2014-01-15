<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
table.listagem tr.subprojeto td {
	padding-left: 20px;
}
</style>
<f:view>
	<h2><ufrn:subSistema /> &gt; Lista de Projetos de Infra-Estrutura em Pesquisa</h2>

	<c:choose>
	<c:when test="${empty projetoInfraPesq.resultadosBusca}">
		Não há projetos cadastrados.
	</c:when>
	
	<c:otherwise>
	
	<center>
	<div class="infoAltRem">
	<h:graphicImage value="/img/view.gif" style="overflow: visible;" />:
		Visualizar Projeto
		<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />:
		Alterar Projeto
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:
		Remover Projeto <br />
	</div>
	</center>
	
    <table class="listagem" width="80%">
		<caption class="listagem">Lista de Projetos</caption>
	        <thead>
	        	<tr>
		        	<td>Título</td>
		        	<td>Coordenador Geral</td>
		        	<td></td>
		        	<td></td>
		        	<td></td>
		       	</tr>
	        </thead>
	        <tbody>
			
			<h:form>
			<c:set value="true" var="flag" />
	        <c:forEach items="#{projetoInfraPesq.resultadosBusca}" var="projeto" varStatus="status">
	        	<c:if test="${projeto.ativo}">
	        	<c:choose>
	        	<c:when test="${flag}">
	        		<c:set value="linhaPar" var="classe"/>
		            <c:set value="false" var="flag" />
	        	</c:when>
	        	<c:otherwise>
	        		<c:set value="linhaImpar" var="classe"/>
		            <c:set value="true" var="flag" />
	        	</c:otherwise>
	        	</c:choose>
	            <tr class="${classe}">
                    <td>${projeto.projeto.titulo}</td>
                    <td>${projeto.coordenadorGeral.pessoa.nome}</td>
                    <td width="5%">
                    	<h:commandLink action="#{projetoInfraPesq.view}">
                    		<f:param name="id" value="#{projeto.id}" />
                    		<h:graphicImage url="/img/view.gif" alt="Visualizar" />
                    	</h:commandLink>
                    </td>
                    <td width="5%">
                    	<h:commandLink action="#{projetoInfraPesq.atualizar}" rendered="#{!projetoInfraPesq.portalReitoria}">
                    		<f:param name="id" value="#{projeto.id}" />
                    		<h:graphicImage url="/img/alterar.gif" alt="Alterar" />
                    	</h:commandLink>
                    </td>
                    <td width="5%">
                    	<h:commandLink action="#{projetoInfraPesq.preRemover}" rendered="#{!projetoInfraPesq.portalReitoria}">
                    		<f:param name="id" value="#{projeto.id}" />
                    		<h:graphicImage url="/img/delete.gif" alt="Remover" />
                    	</h:commandLink>
                    </td>                    
	            </tr>
	            </c:if>
	        </c:forEach>
	        </h:form>
	        </tbody>
	    </table>
		</c:otherwise>
	</c:choose>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
