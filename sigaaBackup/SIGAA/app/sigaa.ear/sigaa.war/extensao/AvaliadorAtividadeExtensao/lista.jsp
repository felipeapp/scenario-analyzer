<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2><ufrn:subSistema /> > Avaliadores de Ações de Extensão</h2>
	<br>

	<h:form id="form">
		<table width="50%" align="center" class="formulario">
			<caption class="listagem"> Escolha a área temática e clique buscar </caption>
			<tbody>
			<tr>
				<td>Área Temática:</td>
				<td>
					<h:selectOneMenu id="areaTematica"	
						value="#{avaliadorExtensao.areaTematicaBusca}"	
						readonly="#{avaliadorExtensao.readOnly}">
						<f:selectItem itemValue="-1" itemLabel=" -- TODAS AS ÁREAS --"/>						
						<f:selectItems value="#{areaTematica.allCombo}"/>
					</h:selectOneMenu>				

				</td>
			</tr>
			</tbody>
			<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton action="#{avaliadorExtensao.busca}" value="Buscar" />
					<h:commandButton action="#{avaliadorExtensao.cancelar}" value="Cancelar" onclick="#{confirm}" />
				</td>
			</tr>
			</tfoot>
		</table>
	<br><br>




	<c:if test="${empty avaliadorExtensao.avaliadores}">
		<center>Nenhum resultado foi encontrado.</center>
	</c:if>

	<c:if test="${not empty avaliadorExtensao.avaliadores}">

		<div class="infoAltRem">
			<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>: <h:commandLink action="#{avaliadorExtensao.preCadastrarAvaliadorExtensao}" value="Cadastrar" />
		    <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar
   		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover<br/>   		    
		</div>

		<table class="listagem">
			<caption class="listagem">Avaliadores de Ações de Extensão (${fn:length(avaliadorExtensao.avaliadores)})</caption>
			<thead>
			<tr>
				<th>Avaliador(a)</th>
				<th>Área Temática</th>
				<th>Data Início</th>
				<th></th>								
				<th></th>								
			</tr>
			</thead>
			<tbody>
			<c:forEach items="#{avaliadorExtensao.avaliadores}" var="avaliador" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${avaliador.servidor.pessoa.nome}</td>
					<td>${avaliador.areaTematica.descricao}</td>					
					<td><fmt:formatDate value="${avaliador.dataInicio}" pattern="dd/MM/yyyy" /></td>					
					<td width="2%">
						<h:commandLink  action="#{avaliadorExtensao.atualizar}" style="border: 0;" id="cmdAlterar" title="Alterar">
					    	<f:param name="id" value="#{avaliador.id}"/>
					      	<h:graphicImage url="/img/alterar.gif" />
						</h:commandLink>
					</td>		
					<td width="2%">
						<h:commandLink  id="cmdRemover" action="#{avaliadorExtensao.inativar}" style="border: 0;" onclick="return confirm('Atenção! Deseja realmente remover este Avaliador?');" title="Remover">
					    	<f:param name="id" value="#{avaliador.id}"/>
					      	<h:graphicImage url="/img/delete.gif" />
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