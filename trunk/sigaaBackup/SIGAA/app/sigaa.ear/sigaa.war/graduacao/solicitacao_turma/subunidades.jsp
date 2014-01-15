<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>

<h:messages showDetail="true" />

<h2 class="title"><ufrn:subSistema /> > Solicitação de Abertura de Turma</h2>

<div class="descricaoOperacao">
	<p>Caro Coordenador, <p> <br/>
	<p>
		Você selecionou um componente curricular do tipo bloco, portanto, <b>é necessário selecionar a subunidade do bloco que deseja operar.</b>
	</p>
</div>

<center>
<div class="infoAltRem"><h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:
Selecionar SubUnidade<br />
</div>
</center>

<table class="listagem">
<caption class="listagem">Componentes Curriculares Encontrados</caption>
	<thead>
		<tr>
			<td>Código</td>
			<td>Nome</td>
			<td>Créditos</td>
			<td>Carga Horária</td>
			<td>Tipo</td>
			<td></td>
		</tr>
	</thead>

	<c:forEach items="${componenteCurricular.subunidades}" var="item" varStatus="status">
		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td>${item.detalhes.codigo}</td>
			<td>${item.detalhes.nome}</td>
			<td>${item.detalhes.crTotal}</td>
			<td>${item.detalhes.chTotal}</td>
			<td>${item.tipoComponente.descricao}</td>


			<td width=20>
			<h:form>
				<input type="hidden" value="${item.id}" name="id" /> 
				<h:commandButton image="/img/seta.gif" styleClass="noborder" value="Selecionar Subunidade"
				action="#{componenteCurricular.selecionarComponente}" />
			</h:form>
			</td>
		</tr>
	</c:forEach>

</table>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>