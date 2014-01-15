<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/painel_detalhar_pd.js"></script>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<c:set var="dirBase" value="/prodocente/producao/" scope="session"/>
	<h2><ufrn:subSistema /> > Relatório de Produtividade do docente</h2>
	<h:form>
	<div class="infoAltRem">
   	    <h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/> <h:commandLink action="#{relatorioAtividades.preCadastrar}" value="Cadastrar"></h:commandLink>
	    <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
	</div>
	</h:form>

	<h:outputText value="#{relatorioAtividades.create}" />
	
	<table class="listagem" style="width:100%">
		<caption class="listagem">Lista de Relatórios</caption>
		<thead>

			<td>Título</td>
			<td>Data de Criação</td>
			<td>Usuário criador</td>
			<td></td>
			<td></td>
		</thead>

		<% int i = 10; %>
		<c:forEach items="${relatorioAtividades.allAtivos}" var="item" varStatus="status" end="40">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.titulo}</td>
				<td><ufrn:format type="data" name="item" property="dataCriacao"/>
				<td>${item.usuario.pessoa.nome}</td>
				<td width=20>
					<h:form>
					<input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" value="Alterar" alt="Alterar"
						action="#{relatorioAtividades.atualizar}" />
					</h:form>     
				</td>
				<td width=25>
				 <h:form>
				   <input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/delete.gif" alt="Remover"
						action="#{relatorioAtividades.remover}" onclick="#{confirmDelete}"/>
				 </h:form>
				</td>
			</tr>
		</c:forEach>
	</table>
	<br />

<h:outputText value="#{relatorioAtividades.dropList}" />
</f:view>

<script type="text/javascript">
<!--
	function exibirPainel(id, todosBotoes, idArquivo) {
		var getURL = function(t) {
			var url = t.responseText;
			PainelDetalhar.show(url, todosBotoes, idArquivo);
		}
		// buscar url
		var ajax = new Ajax.Request('/sigaa/ajaxPreValidacaoProDoc',
		{
			method: 'get',
			parameters: 'id=' + id +'&idArquivo=' + idArquivo,
			onSuccess: getURL
		});
	}

//-->
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>