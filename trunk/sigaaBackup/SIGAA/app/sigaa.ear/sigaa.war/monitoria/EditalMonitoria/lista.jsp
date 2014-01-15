<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2><ufrn:subSistema /> > Lista de Editais de Monitoria</h2>
	<br>

		<h:outputText value="#{editalMonitoria.create}"/>
	
	<center>
			<h:messages/>
			<div class="infoAltRem">
			    <img src="/shared/img/adicionar.gif" style="overflow: visible;"/>
                <a href="${ctx}/monitoria/EditalMonitoria/form.jsf">Cadastrar Novo Edital</a>
			    <h:graphicImage value="/img/view.gif"    style="overflow: visible;"/>: Visualizar Arquivo			    
			    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
			    <h:graphicImage value="/img/seta.gif" 	style="overflow: visible;"/>: Enviar Arquivo do Edital
			    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
			</div>

	</center>
	<h:form>
	<table class="listagem">
			<caption class="listagem">Editais de Monitoria</caption>
			<thead>
			<tr>
				<th> Descrição</th>
				<th> Tipo</th>
				<th> Início Submissão </th>
				<th> Fim Submissão </th>
				<th> Bolsas </th>
				<th></th>
				<th></th>
				<th></th>								
				<th></th>								
			</tr>
			</thead>

<tbody>
	<c:forEach items="#{editalMonitoria.allAtivos}" var="edital">
		<tr>
			<td> ${edital.descricao} </td>
			<td> ${edital.tipoString} </td>
			<td> <fmt:formatDate value="${edital.inicioSubmissao}" pattern="dd/MM/yyyy"/> </td> 
			<td> <fmt:formatDate value="${edital.fimSubmissao}" pattern="dd/MM/yyyy"/></td>
			<td> ${edital.numeroBolsas} </td>

			<td width="2%">
                <h:commandLink title="Ver Arquivo" action="#{editalMBean.viewArquivo}" style="border: 0;">
                     <f:param name="id" value="#{edital.edital.id}"/>
                     <h:graphicImage url="/img/view.gif" />
                </h:commandLink>
			</td>
			<td width="2%">
				<h:commandLink title="Alterar" action="#{editalMonitoria.atualizar}"  style="border: 0;">
				      <f:param name="id" value="#{edital.id}"/>
				      <h:graphicImage url="/img/alterar.gif" />
				</h:commandLink>
			</td>
			<td width="2%">
				<h:commandLink action="#{editalMonitoria.populaEnviaArquivo}"  style="border: 0;">
				      <f:param name="id" value="#{edital.id}"/>
				      <h:graphicImage url="/img/seta.gif" />
				</h:commandLink>
			</td>
			<td width="2%">			
				<h:commandLink action="#{editalMonitoria.preRemover}"  style="border: 0;">
				      <f:param name="id" value="#{edital.id}"/>
				      <h:graphicImage url="/img/delete.gif" />
				</h:commandLink>
			</td>
	</c:forEach>
</tbody>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>