<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<c:set var="dirBase" value="/prodocente/atividades/" scope="session"/>
	<h2> <ufrn:subSistema /> > PET </h2>

	<h:form prependId="false">
	<div class="infoAltRem">
		 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/> <h:commandLink action="#{petBean.preCadastrar}" value="Cadastrar Novo PET" />
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar PET
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover PET<br/>
	</div>

	<table class="listagem" style="width:100%">
		<caption class="listagem">Lista de PETs</caption>
		<thead>
			<tr>
				<th>Descrição</th>
				<th>Curso</th>
				<th>Área de Conhecimento</th>
				<th colspan="2"> </th>
			</tr>
		</thead>
		<c:forEach items="#{petBean.allAtivos}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<td>${item.curso.nome}</td>
				<td>${item.nomeAreaConhecimentoCnpq}</td>
				<td width="16">
					<h:commandLink title="Alterar programa" action="#{petBean.atualizar}" id="atualizar">
						<h:graphicImage url="/img/alterar.gif"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>
				<td width="16">
					<h:commandLink title="Remover Programa" action="#{petBean.inativar}" id="remover"
							onclick="javascript:if(confirm('Deseja realmente REMOVER esse grupo?')){ return true;} return false; void(0);">
						<h:graphicImage url="/img/delete.gif"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
	</table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
