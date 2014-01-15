<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2><ufrn:subSistema /> > Classificação do PET</h2><br>
	
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/> <h:commandLink action="#{classificacaoPet.preCadastrar}" value="Cadastrar Novo Tipo de Classificação do PET"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Tipo de Classificação do PET <br />
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Tipo de Classificação do PET
		</div>
	</h:form>

	<h:outputText value="#{classificacaoPet.create}"/>
	  <table class=listagem>
		<caption class="listagem" style="width:100%" border="1">
		  Lista de Tipo de Classificação do PET
		</caption>
		  <thead>
		 	<td>Descrição</td>
			<td colspan="2"></td>
		  </thead>
	<c:forEach items="${classificacaoPet.allAtivos}" var="item" varStatus="status">
	   <tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		<td>${item.descricao}</td>
		<h:form>
			<td  width=20>
				<input type="hidden" value="${item.id}" name="id"/>
				<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{classificacaoPet.atualizar}"/>
			</td>
		</h:form>
		<h:form>
			<td  width=25>
				<input type="hidden" value="${item.id}" name="id"/>
				<h:commandButton image="/img/delete.gif" alt="Remover" action="#{classificacaoPet.remover}" onclick="javascript:if(confirm('Deseja realmente REMOVER essa atividade ?')){ return true;} return false; void(0);" />
			</td>
		</h:form>
	   </tr>
	</c:forEach>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>