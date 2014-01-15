<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/tutor/menu_tutor.jsp" %>
<h2><ufrn:subSistema /> > Itens de Avaliação</h2>
<br>

<h:form>
	<h:outputText value="#{ fichaAvaliacaoEad.create }"/>

	<table class="formulario" width="70%">
	<caption>Avaliação Semanal</caption>
	<tr><td>Discente: </td><td>${ fichaAvaliacaoEad.discente.pessoa.nome }</td></tr>
	<tr><td>Matrícula: </td><td>${ fichaAvaliacaoEad.discente.matricula }</td></tr>
	<tr><td>Pólo: </td><td>${ fichaAvaliacaoEad.discente.polo.cidade.nomeUF }</td></tr>
	<tr><td>Curso: </td><td>${ fichaAvaliacaoEad.discente.curso.descricao }</td></tr>
	<tr><td>Tutor: </td><td>${ usuario.pessoa.nome }</td></tr>
	<tr><td>Semana: </td><td>${ fichaAvaliacaoEad.semanaEscolhida }</td></tr>
	<tfoot>
	<tr><td colspan="2">
		<h:commandButton value="Inserir Avaliação" action="#{ fichaAvaliacaoEad.novaAvaliacaoSemanal }" rendered="#{ fichaAvaliacaoEad.novaAvaliacao }"/>
		<h:commandButton value="Alterar Avaliação" action="#{ fichaAvaliacaoEad.novaAvaliacaoSemanal }" rendered="#{ !fichaAvaliacaoEad.novaAvaliacao }"/>
		<h:commandButton value="Mostrar Fichas de Avaliação" action="#{ fichaAvaliacaoEad.mostrarFicha }"/>
		<h:commandButton value="Cancelar" action="#{ fichaAvaliacaoEad.cancelar }"/>
	</td></tr>
	</tfoot>
	</table>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>