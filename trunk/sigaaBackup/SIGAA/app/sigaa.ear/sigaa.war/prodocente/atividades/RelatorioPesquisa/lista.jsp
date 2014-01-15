<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h2><ufrn:subSistema/> > Relatório</h2>

<h:form id="form" >
		<center>
			<table class="formulario" width="70%">
			<caption class="listagem">Buscar Relatório</caption>
				<tr>

					<th>Docente:</th>

					<td>
						<h:inputHidden id="id" value="#{relatorio.idServidor}"/>
						<h:inputText id="nomeServidor"
							value="#{relatorio.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
							source="form:nomeServidor" target="form:id"
							baseUrl="/sigaa/ajaxDocente" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
							parser="new ResponseXmlToHtmlListParser()" /> </td>
						<td>
						<span id="indicator"
							style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
						</td>

				</tr>
				<tr>
					<td align="center" colspan="4"> <h:commandButton actionListener="#{relatorio.buscar}" onclick="submit()" value="Buscar"/> </td>
				</tr>
			</table>
		</center>
	</h:form>
	<br/>
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/> <h:commandLink action="#{relatorio.preCadastrar}" value="Cadastrar Novo Relatório"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Relatório
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Relatório<br/>
		</div>
	</h:form>
<h:outputText value="#{relatorio.create}"/>
<table class=listagem style="width:100%" border="1">
	<caption class="listagem"> Lista de relatorios</caption>
	<thead>


		<td>Titulo</td>
		<td>Tipo de Participação</td>
		<td>Data de Aprovação</td>
		<td>Docente</td>
		<td>Tipo Relatório</td>
		<td>Agência Financiadora</td>
		<td></td>
		<td></td>

	</thead>
<c:forEach items="${relatorio.allAtividades}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

<td>${item.titulo}</td>
<td>${item.participacao.descricao}</td>
<td> <ufrn:format type="data" name="item" property="dataAprovacao"/></td>
<td>${item.servidor.pessoa.nome}</td>
<td>
	<c:if test="${item.relatorioFinal}">
		Relatório Final
	</c:if>

	<c:if test="${!item.relatorioFinal}">
		Relatório Parcial
	</c:if>
 </td>
<td>${item.agencia.nome}</td>
<h:form>
<td  width=20>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/alterar.gif" value="Alterar" alt="Alterar" action="#{relatorio.atualizar}" id="alterar"/>
</td>
</h:form>
<h:form>
<td  width=25>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/delete.gif" alt="Remover" action="#{relatorio.remover}" id="remover" onclick="javascript:if(confirm('Deseja realmente REMOVER essa atividade ?')){ return true;} return false; void(0);" />
</td>
</h:form>
</tr>
</c:forEach>
</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
