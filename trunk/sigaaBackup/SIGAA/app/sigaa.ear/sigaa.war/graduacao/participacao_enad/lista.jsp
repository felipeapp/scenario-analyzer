<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	.centro{text-align:center !important;}
</style>
<f:view>
	<h2><ufrn:subSistema /> &gt; Participação no ENADE</h2>
	<a4j:keepAlive beanName="participacaoEnade"></a4j:keepAlive>
	<center>
		<div class="infoAltRem">
			<h:form id="formLegenda">
				<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>
				<h:commandLink action="#{participacaoEnade.preCadastrar}" value="Cadastrar"/>
				<f:verbatim><h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>:</f:verbatim> Alterar
		        <f:verbatim><h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>:</f:verbatim> Remover <br/>
			</h:form>
		</div>
	</center>
		
	<c:if test="${not empty participacaoEnade.all}">
		<c:set var="tipoAnterior" value=""/>
		<c:set var="parImpar" value="0"/>
		<table class="listagem">
			<caption class="listagem">Participações do ENADE Cadastradas (${ fn:length(participacaoEnade.all)})</caption>
			<thead>
			<tr>
				<th>Descrição</th>
				<th class="centro">Indica Pendência?</th>
				<th></th>
				<th></th>
			</tr>
			</thead>
			<h:form id="formListagem">
			<c:forEach items="#{participacaoEnade.all}" var="item" varStatus="status">
				<c:if test="${tipoAnterior != item.tipoEnade}">
					<tr>
						<td colspan="4" class="subFormulario">ENADE ${item.tipoEnade}</td>
					</tr>
					<c:set var="tipoAnterior" value="${item.tipoEnade}"/>
					<c:set var="parImpar" value="0"/>
				</c:if>
				<tr class="${parImpar % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.descricao}</td>
					<td class="centro">${item.participacaoPendente?'Sim':'Não'}</td>
					<td width=20>
						<h:commandLink title="Alterar" action="#{participacaoEnade.atualizar}">
							<f:param name="id" value="#{item.id}"/>
							<h:graphicImage url="/img/alterar.gif"/>
						</h:commandLink>
					</td>
					<td width=25>
						<h:commandLink title="Remover" action="#{participacaoEnade.preRemover}">
							<f:param name="id" value="#{item.id}"/>
							<h:graphicImage url="/img/delete.gif"/>
						</h:commandLink>
					</td>
				</tr>
				<c:set var="parImpar" value="${parImpar + 1}"/>
			</c:forEach>
			</h:form>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
