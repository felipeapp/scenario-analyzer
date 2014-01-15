<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<f:view>
<a4j:keepAlive beanName="expirarTempoCadastro" />
<%@include file="/portais/docente/menu_docente.jsp"%>
	<h:form id="formAtividade">
	
		<h2><ufrn:subSistema /> &gt; Minhas Ações de Extensão com Tempo de Cadastro Expirado</h2>
		
		<!-- Ações -->
		<c:if test="${not empty expirarTempoCadastro.atividadesGravadas}">
			<div class="infoAltRem">
				<h:graphicImage value="/img/table_refresh.png" style="overflow: visible;"/>: Reativar Proposta	    			    
			</div>
			<br />
			
			<table class="listagem">
				<caption class="listagem">Lista das Ações de Extensão</caption>
				<thead>
					<tr>
						<th>Código</th>
						<th>Título</th>
						<th>Tipo Ação</th>								
						<th> Situação </th>
						<th></th>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="#{expirarTempoCadastro.atividadesGravadas}" var="atividade" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td> ${atividade.codigo} </td>
						<td> ${atividade.titulo}</td>			
						<td><h:outputText value="#{atividade.tipoAtividadeExtensao.descricao}  #{atividade.registro 
							? '<font color=red>(REGISTRO)</font>' : '(PROPOSTA)' }" escape="false" /> </td>						
						<td>${atividade.situacaoProjeto.descricao} </td>
						<td> <h:commandLink title="Reativar Proposta de Ação"
					 onclick="return confirm('Tem certeza que deseja Reativar esta proposta?');"
					 action="#{expirarTempoCadastro.reativarProposta}" >
						<f:param name="id" value="#{atividade.id}" />
				    	<h:graphicImage value="/img/table_refresh.png" style="overflow: visible;"/>
					</h:commandLink> </td>
		
				</c:forEach>
			</tbody>
		</table>
			
		
		<br/>
		<br/>
		</c:if>
		<!-- FIM DAS Ações -->
		
		
			
			
		<c:if test="${empty expirarTempoCadastro.atividadesGravadas }">
			<center><font color='red'>Não há ações de extensão.</font></center>
		</c:if>
		
		
			
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>