<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2><ufrn:subSistema /> > Monitoria</h2><br>
	
	<h:form>
		<div class="infoAltRem">
			<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/> <h:commandLink action="#{monitoria.preCadastrar}" value="Cadastrar Nova Monitoria" />
		    <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Tipo de Monitoria <br />
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Tipo de Monitoria
		</div>
	</h:form>

	<h:outputText value="#{monitoria.create}"/>
	  <table class=listagem>
		<caption class="listagem" style="width:100%" border="1">
		  Lista de Monitorias
		</caption>
		  <thead>
		 	<tr>
			 	<td>Nome da Disciplina</td>
			 	<td>Instituição</td>
			 	<td>Período Início</td>
			 	<td>Período Fim</td>
			 	<td>Servidor</td>
			 	<td>Nome do Monitor</td>
			 	<td>Agência Financiadora</td>
				<td colspan="2"></td>
			</tr>
		  </thead>
	<c:forEach items="${monitoria.allAtivos}" var="item" varStatus="status">
	   <tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		<td>${item.nomeDisciplina}</td>
		<td>${item.instituicao}</td>
		<td><ufrn:format valor="${item.periodoInicio}" type="data" /></td>
		<td><ufrn:format valor="${item.periodoFim}" type="data" /></td>
		<td>${item.servidor.pessoa.nome}</td>
			<c:if test="${item.monitor.id!=null }">
				<td>${item.monitor.pessoa.nome}</td>
			</c:if>
			<c:if test="${item.monitor.id==null }">
				<td>${item.nomeMonitor}</td>
			</c:if>
		<td>${item.entidadeFinanciadora.nome}</td>
		<td  width=20>
			<h:form><input type="hidden" value="${item.id}" name="id"/>
			<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{monitoria.atualizar}"/>
			</h:form>
		</td>
		<td  width=25>
			<h:form><input type="hidden" value="${item.id}" name="id"/>
				<h:commandButton image="/img/delete.gif" alt="Remover" action="#{monitoria.remover}" />
			</h:form>
		</td>
	   </tr>
	</c:forEach>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>