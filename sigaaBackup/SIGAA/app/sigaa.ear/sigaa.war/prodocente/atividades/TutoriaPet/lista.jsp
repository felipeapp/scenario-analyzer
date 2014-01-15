<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<c:set var="dirBase" value="/prodocente/atividades/" scope="session"/>
	<h2>Tutoria PET</h2>

	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: <h:commandLink action="#{tutoriaPet.preCadastrar}" value="Cadastrar Nova Tutoria"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Pet
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Pet<br/>
		</div>
	</h:form>

	<h:form id="form">
	<table class="listagem" style="width:100%">
		<caption class="listagem">Lista de Tutorias</caption>
		<thead>
			<tr>
				<td>Grupo PET</td>
				<td>Docente</td>
				<td>Data Início</td>
				<td>Data Fim</td>
				<td></td>
				<td></td>
			</tr>
		</thead>
		<c:forEach items="#{tutoriaPet.allAtivos}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.pet.descricao}</td>
				<td>${item.servidor.nome}</td>
				<td> <ufrn:format valor="${item.periodoInicio}" type="data"/> </td>
				<td> <ufrn:format valor="${item.periodoFim}" type="data"/></td>
				<td width="16">
					<h:commandLink title="Alterar programa" action="#{tutoriaPet.atualizar}" id="alterar">
						<h:graphicImage url="/img/alterar.gif"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>
				<td width="16">
					<h:commandLink title="Remover Programa" action="#{tutoriaPet.remover}"  id="remover"
							onclick="javascript:if(confirm('Deseja realmente REMOVER esta tutoria?')){ return true;} return false; void(0);">
						<h:graphicImage url="/img/delete.gif"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
		</h:form>
	</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
