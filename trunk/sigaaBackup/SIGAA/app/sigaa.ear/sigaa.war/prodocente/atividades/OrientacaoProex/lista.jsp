<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema /> > Orientação Proex</h2>

	<h:form>
		<div class="infoAltRem">
			<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" />
			<ufrn:link action="/prodocente/atividades/OrientacaoProex/form.jsf" value="Cadastrar nova Orientação PROEX" aba="proex"/>
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Orientação PROEX 
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Orientação PROEX <br />
		</div>
	</h:form>

<h:outputText value="#{orientacaoProex.create}"/>

<table class=listagem>
	<caption class="listagem"> Lista de Orientação Proexs</caption>
		<thead>
		 <td>servidor</td>
		 <td>titulo</td>
		 <td>alunos</td>
		 <td>dataInicio</td>
		 <td>dataFinal</td>
		 <td>nomeAluno</td>
		 <td>instituicao</td>
		 <td>orientacao</td>
		 <td>departamento</td>
	   	 <td>financiamento</td>
		 <td colspan="2"></td>
		</thead>

	<c:forEach items="${orientacaoProex.allAtividades}" var="item">
	 <tr>
		 <td>${item.servidor}</td>
		 <td>${item.titulo}</td>
		 <td>${item.alunos}</td>
		 <td>${item.dataInicio}</td>
		 <td>${item.dataFinal}</td>
		 <td>${item.nomeAluno}</td>
		 <td>${item.instituicao}</td>
		 <td>${item.orientacao}</td>
		 <td>${item.departamento}</td>
		 <td>${item.financiamento}</td>
		 <td  width=20>
		   <h:form>
			<input type="hidden" value="${item.id}" name="id"/>
			<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{orientacaoProex.atualizar}"/>
		   </h:form>
		 </td>
		 <td  width=25>
		   <h:form>
			<input type="hidden" value="${item.id}" name="id"/>
			<h:commandButton image="/img/delete.gif" alt="Remover" action="#{orientacaoProex.remover}" onclick="javascript:if(confirm('Deseja realmente REMOVER essa atividade ?')){ return true;} return false; void(0);" />
		   </h:form>
		 </td>
	 </tr>
	</c:forEach>
</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>