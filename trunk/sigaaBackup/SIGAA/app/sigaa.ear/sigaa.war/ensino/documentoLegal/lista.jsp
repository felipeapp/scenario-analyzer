<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> &gt; Documento Legal</h2>

<h:form>

	<div class="infoAltRem" style="width: 100%">
		<img src="/shared/img/adicionar.gif" style="overflow: visible;"/>
		<h:commandLink action="#{documentoLegalMBean.preCadastrar}" value="Cadastrar Novo Documento Legal" />
		<img src="/shared/img/alterar.gif" style="overflow: visible;"/>: Alterar 
		<img src="/shared/img/delete.gif" style="overflow: visible;"/>: Remover 
	</div>

	<table class="listagem">
	  <caption> Lista de Documentos Legais</caption>
			<thead>
				<tr>
					<th>Curso</th>
					<th style="text-align: left;">Nome do Documento</th>
					<th style="text-align: right;">Número do Documento</th>
					<th style="text-align: center;">Data da Aprovação</th>
					<th>Local da Publicação</th>
					<th>Número do Parecer</th>
					<th>Validade</th>
					<th></th><th></th>
				</tr>
			</thead>
	
	<c:choose>
  	  <c:when test="${ not empty documentoLegalMBean.all }">
		 <c:forEach items="#{documentoLegalMBean.all}" var="item" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${item.curso}</td>
						<td style="text-align: left;">${item.nomeDocumento}</td>
						<td style="text-align: right;">${item.numeroDocumento}</td>
						<td style="text-align: center;"><fmt:formatDate value="${item.dataAprovacao}" pattern="dd/MM/yyyy"/> </td>
						<td>${item.localPublicacao}</td>
						<td>${item.numeroParecer}</td>
						<td>${item.validade} ${item.validade == 1 ? 'ano' : item.validade > 1 ? 'anos' : ''}</td>
						<td>
							<h:commandLink action="#{ documentoLegalMBean.atualizar }">
								<f:verbatim><img src="/shared/img/alterar.gif" alt="Alterar" title="Alterar"/></f:verbatim>
								<f:param name="id" value="#{ item.id }" />
							</h:commandLink>
						</td>
						<td>
							<h:commandLink action="#{ documentoLegalMBean.remover}" onclick="#{confirmDelete}">
								<f:verbatim><img src="/shared/img/delete.gif" alt="Remover" title="Remover"/></f:verbatim>
								<f:param name="id" value="#{ item.id }" />
							</h:commandLink>
						</td>
					</tr>
		 </c:forEach>
	  </c:when>
	 <c:otherwise>
		<tr>
			<td colspan="10" style="color: red; text-align: center;">Não há nenhum Documento Legal cadastrado</td>
		</tr>
	</c:otherwise>
</c:choose>
</table>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>