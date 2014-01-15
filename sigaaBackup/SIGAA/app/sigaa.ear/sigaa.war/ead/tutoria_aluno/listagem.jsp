<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<script>
	// Muda o nome do jQuery para J, evitando conflitos com a Prototype.
	var J = jQuery.noConflict();
</script>

<f:view>
	<h2><ufrn:subSistema /> > Consulta de Tutorias de Alunos</h2>
	<h:outputText value="#{tutoriaAluno.create}" />
	<h:form id="busca">
	
		<table class="formulario" width="40%">
			<caption>Busca por Tutorias</caption>
			<tbody>
				<tr>
					<td width="3%"><input type="radio" id="checkTutor" name="paramBusca" value="tutor" class="noborder"></td>
					<td width="5%"><label for="checkTutor">Tutor: </label></td>
					<td><h:inputText value="#{tutoriaAluno.consultaTutor }" size="40" onfocus="marcaCheckBox('checkTutor')" /></td>
				</tr>
				<tr>
					<td width="3%"><input type="radio" id="checkAluno" name="paramBusca" value="aluno" class="noborder"></td>
					<td width="5%"><label for="checkAluno">Aluno: </label></td>
					<td><h:inputText value="#{tutoriaAluno.consultaAluno }" size="40" onfocus="marcaCheckBox('checkAluno')" /></td>
				</tr>
				<tr>
					<td><input type="radio" name="paramBusca" value="todos" id="checkTodos" class="noborder"></td>
					<td><label for="checkTodos">Todos</label></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{tutoriaAluno.buscarTutoria}" />
					 	<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{tutoriaAluno.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>

		
		<c:if test="${not empty tutoriaAluno.resultadosBusca}">
			<br>
			<center>
			<div class="infoAltRem"><h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />:
			Alterar Tutoria<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:
			Remover Tutoria<br />
			</div>
			</center>
			<table class=listagem>
				<caption class="listagem">Tutorias Encontradas (${tutoriaAluno.totalTutorias})</caption>
				<thead>
					<tr>
						<td>Tutor</td>
						<td>Aluno</td>
						<td width="8%">Início</td>
						<td width="8%">Fim</td>
						<td></td>
						<td></td>
					</tr>
				</thead>
				<c:forEach items="#{tutoriaAluno.resultadosBusca}" var="item">
					<tr>
						<td>${item.tutor.pessoa.nome}</td>
						<td>${item.aluno.pessoa.nome}</td>
						<td><ufrn:format type="data" valor="${item.inicio }" /></td>
						<td><ufrn:format type="data" valor="${item.fim}" /></td>
						<td width=20>
							 <h:commandLink styleClass="noborder" title="Alterar Tutoria" id="alterarTuroria"
								action="#{tutoriaAluno.atualizar}" >
								<h:graphicImage value="/img/alterar.gif" />
								<f:param  value="#{item.id}" name="id" />
							</h:commandLink>									
						</td>
						<td width=25>
							<h:commandLink styleClass="noborder" title="Remover Tutoria" action="#{tutoriaAluno.preRemover}">
								<h:graphicImage value="/img/delete.gif" />
								<f:param  value="#{item.id}" name="id" />
							</h:commandLink>	
						</td>		
					</tr>
				</c:forEach>
				
			</table>
			<c:if test="${tutoriaAluno.buscaTodos}">
				<div style="text-align: center;">
					<h:commandLink onclick="paginaAnterior()" rendered="#{tutoriaAluno.paginaAtual > 0}">
						<h:graphicImage value="/img/voltar.gif" />
					</h:commandLink>
				    <h:selectOneMenu id="paginas" value="#{paginacao.paginaAtual}" valueChangeListener="#{tutoriaAluno.changePage}" onchange="submit()" immediate="true">
						<f:selectItems id="paramPagina" value="#{tutoriaAluno.listagemPaginas}"/>
	   				</h:selectOneMenu>
					<h:commandLink onclick="proximaPagina()" rendered="#{tutoriaAluno.paginaAtual < tutoriaAluno.totalPaginasPaginacao-1}">
						<h:graphicImage value="/img/avancar.gif" />
					</h:commandLink>		
				</div>
			</c:if>	
		</c:if>
		
	</h:form>
</f:view>

<input type="hidden" id="pagina" value="${ tutoriaAluno.paginaAtual }"/>
<script type="text/javascript">
var paginaAtual = parseInt(getEl('pagina').dom.value);

function proximaPagina() {
	var select = J("#busca\\:paginas");
	select.val(paginaAtual+1);
}

function paginaAnterior() {
	var select = J("#busca\\:paginas");
	select.val(paginaAtual-1);
}

function clear() {
	var select = J("#busca\\:paginas");
	if ( paginaAtual == 0 )
		select.val(0);
}

clear();
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
