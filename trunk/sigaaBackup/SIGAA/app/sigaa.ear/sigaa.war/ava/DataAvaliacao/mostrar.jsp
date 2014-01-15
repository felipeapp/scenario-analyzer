<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<a4j:keepAlive beanName="dataAvaliacao"/>
<h:form>

<style>.campo{display:inline;}</style>

<h:messages showDetail="true" />

		<fieldset><legend>Datas de Avaliação</legend>

		<ul class="form">

			<li>
				<label>Descrição:</label>
				<span style="padding-left:20px;">${dataAvaliacao.object.descricao}</span>
			</li>
			
			<li>
				<label>Data da Avaliação:</label>
				<span style="padding-left:20px;"><fmt:formatDate pattern="dd/MM/yyyy" value="${dataAvaliacao.object.data}" /></span>
			</li>

			<li>
				<label>Hora da Avaliação:</label>
				<span style="padding-left:20px;">${dataAvaliacao.object.hora}</span>
			</li>

		</ul>

		<div class="botoes-show" align="center">
		
		<c:if test="${ turmaVirtual.docente }">
			<h:commandButton action="#{dataAvaliacao.editar}" value="Editar">
				<f:param name="id" value="#{dataAvaliacao.object.id}" /> |
			</h:commandButton>
		</c:if> 
		
		<h:commandButton action="#{dataAvaliacao.listar}" value="<< Voltar" /></div>

		</fieldset>

	</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>
