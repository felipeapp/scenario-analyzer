<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Entidade Financiadora</h2>
	<h:outputText value="#{entidadeFinanciadora.create}" />

	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{entidadeFinanciadora.preCadastrar}" value="Cadastrar"/>
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
				</h:form>
			</div>
	</center>

	<table class="listagem">
		<caption class="listagem">Lista de entidades Financiadoras</caption>
		<thead>
			<tr>
				<td>Nome</td>
				<td>Sigla</td>
				<td>UF</td>
				<td>País</td>
				<td>Classificação</td>
				<td>Grupo</td>
				<td></td>
				<td></td>
			</tr>
		</thead>

		<c:forEach items="${entidadeFinanciadora.all}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.nome}</td>
				<td>${item.sigla}</td>
				<td>${item.unidadeFederativa.sigla}</td>
				<td>${item.pais.nome}</td>
				<td>${item.classificacaoFinanciadora.descricao}</td>
				<td>${item.grupoEntidadeFinanciadora.nome}</td>
				<td width=20>
					<h:form><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" alt="Alterar"
						action="#{entidadeFinanciadora.atualizar}" /></h:form>
				</td>
				<td width=25>
					<h:form><input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/delete.gif" alt="Remover"
						action="#{entidadeFinanciadora.remover}" onclick="return confirmacao();"/></h:form>
				</td>
			</tr>
		</c:forEach>
	</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">
<!--	
	function confirmacao(){
		return confirm('Deseja realmente excluir esta entidade?');
	}	
//-->
</script>