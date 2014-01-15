<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
  <h2><ufrn:subSistema /> > Coordena��o de Resid�ncia M�dica</h2>
	
	<h:form>
		<div class="infoAltRem">
			<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" /> 
			<h:commandLink action="#{coordenacaoResidenciaMedica.direcionar}" value="Cadastrar Nova Coordena��o Resid�ncia M�dica" />
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>:Alterar Coordena��o Resid�ncia M�dica<br />
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>:Remover Coordena��o Resid�ncia M�dica
			<br />
		</div>


	<table class="listagem" style="width: 90%">
	  <caption class="listagem">Lista das Coordena��es das Resind�ncias M�dicas</caption>
		<thead>
			<tr>
				<td>Servidor</td>
				<td>Programa</td>
				<td style="text-align: center">Data de In�cio</td>
				<td style="text-align: center">Data Final</td>
				<td colspan="2"></td>
			</tr>
		</thead>
		<c:forEach items="#{coordenacaoResidenciaMedica.all}" var="linha" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td> ${linha.servidor.pessoa.nome}</td>
				<td> ${linha.programaResidenciaMedica.nome}</td>
				<td align="center"><ufrn:format valor="${linha.inicio}" type="data" /></td>
				<td align="center"><ufrn:format valor="${linha.fim}" type="data" /></td>
				<td width="2%">
				<h:commandLink title="Alterar" style="border: 0;" action="#{coordenacaoResidenciaMedica.atualizar}" >
					<f:param name="id" value="#{linha.id}" />
					<h:graphicImage url="/img/alterar.gif" alt="Alterar" />
				</h:commandLink>
				</td>
				<td width="2%">
				<h:commandLink title="Remover" style="border: 0;" action="#{coordenacaoResidenciaMedica.remover}" onclick="#{confirmDelete}" >
					<f:param name="id" value="#{linha.id}" />
					<h:graphicImage url="/img/delete.gif" alt="Remover" />
				</h:commandLink>
				</td>
			</tr>
			
		</c:forEach>
	</table>
  </h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>