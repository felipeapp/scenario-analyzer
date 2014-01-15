<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<f:view>
	<h2><ufrn:subSistema /> > Tipos de Material</h2>
	
	<div class="descricaoOperacao"> 
    	<p> Abaixo estão listados os Tipos de Materiais existentes no sistema. </p>
    	<br/>
    	<p> <strong>Um Tipo de Material é uma informação utilizada para discriminar o meio físico no qual o material está contido. </strong> </p> 
    	<p> Não podem existir dois os mais Tipos de Materiais com a mesma descrição no sistema.</p>
		<br/>
	</div>
	
	<h:form>
		<h:messages showDetail="true" />

		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
			<div class="infoAltRem" style="width:80%;">
	
				<h:graphicImage value="/img/adicionar.gif" />
				<h:commandLink action="#{tipoMaterialMBean.preCadastrar}" value="Novo Tipo de Material" />
	
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: 
				Alterar Tipo de Material
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
				Remover Tipo de Material
			</div>
		</ufrn:checkRole>

		<%-- Para não ficar buscando várias vezes no banco --%>
		<c:set var="listaTipoMaterial" value="${tipoMaterialMBean.all}" scope="request" />



		<table class="listagem" style="width:80%;">
			<caption>Lista de Tipos de Material(${tipoMaterialMBean.size})</caption>
			<thead>
				<tr>
					<th>Descrição</th>
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
						<th width="20"></th>
						<th width="20"></th>
					</ufrn:checkRole>
				</tr>
			</thead>
			
			<c:forEach items="#{listaTipoMaterial}" var="t" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${t.descricao}</td>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
						<td>
							<h:commandLink title="Alterar Tipo de Material" action="#{tipoMaterialMBean.preAtualizar}">
								<f:param name="id" value="#{t.id}" />
								<h:graphicImage url="/img/alterar.gif" alt="Alterar Tipo" />
							</h:commandLink>
						</td>
						<td>
							<h:commandLink title="Remover Tipo de Material"
								action="#{tipoMaterialMBean.preRemover}">
								<f:param name="id" value="#{t.id}" />
								<h:graphicImage url="/img/delete.gif" alt="Remover Tipo de Material" />
							</h:commandLink>
						</td>
					</ufrn:checkRole>
					
				</tr>
			</c:forEach>
			
			<tfoot>
				<tr>
					<td colspan="3" style="text-align: center;">
						<h:commandButton value="Cancelar" action="#{tipoMaterialMBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>

	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>