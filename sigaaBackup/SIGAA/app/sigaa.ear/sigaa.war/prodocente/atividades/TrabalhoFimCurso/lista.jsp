<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<ufrn:subSistema teste="lato">
	<%@include file="/lato/menu_coordenador.jsp" %>
</ufrn:subSistema>
<ufrn:subSistema teste="portalDocente">
	<%@include file="/portais/docente/menu_docente.jsp"%>
</ufrn:subSistema>

	<h:outputText value="#{trabalhoFimCurso.create}" />
	<h2><ufrn:subSistema /> &gt; Orientações de Trabalho de Fim de Curso</h2>
	
	<div class="descricaoOperacao">
		<p>Esta tela permite buscar as orientações de trabalho de final de curso. Somente os usuários pertencentes ao curso da coordenação serão visualizados.</p>
		<p>Selecione e preencha se necessário um dos filtro abaixo e pressione buscar.</p>
	</div>
	
	
	<c:if test="${trabalhoFimCurso.gestor}">
		<table class="formulario" width="50%">
			<h:form id="formBusca">
			<caption>Busca por Trabalho de Fim de Curso</caption>
			<tbody>
				<tr>
					<td><input type="radio" id="checkOrientador" name="paramBusca" value="orientador" class="noborder"></td>
					<td><label for="checkOrientador">Orientador:</label></td>
					<td>
						<h:inputText value="#{trabalhoFimCurso.orientador.pessoa.nome}" id="nome" size="60" onfocus="javascript:$('checkOrientador').checked = true;"/>
						 <h:inputHidden value="#{trabalhoFimCurso.orientador.id}" id="idServidor" />
		
						 <ajax:autocomplete source="formBusca:nome" target="formBusca:idServidor"
							baseUrl="/sigaa/ajaxServidor" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
							parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
		
					</td>
				</tr>		
				<tr>
					<td><input type="radio" id="checkDiscente" name="paramBusca" value="discente" class="noborder"></td>
					<td><label for="checkDiscente">Discente:</label></td>
					<td>
						 <h:inputHidden id="idDiscente" value="#{ trabalhoFimCurso.discenteBusca.id }"/>
						 <h:inputText id="nomeDiscente" value="#{ trabalhoFimCurso.discenteBusca.pessoa.nome }" size="90" onfocus="javascript:$('checkDiscente').checked = true;"/>
		
						<ajax:autocomplete source="formBusca:nomeDiscente" target="formBusca:idDiscente"
							baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
							indicator="indicatorDiscente" minimumCharacters="3"
							parameters="status=todos"
							parser="new ResponseXmlToHtmlListParser()" />
		
						<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
					</td>
				</tr>
				<c:if test="${trabalhoFimCurso.portalCoordenadorGraduacao}">
					<tr>
						<td><input type="radio" name="paramBusca" value="todos" id="checkTodos" class="noborder"></td>
						<td><label for="checkTodos">Todos</label></td>
				</tr>
				</c:if>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton id="btBuscar" value="Buscar" action="#{trabalhoFimCurso.buscar}" />
						<h:commandButton id="btCancelar" value="Cancelar" action="#{trabalhoFimCurso.cancelar}" onclick="#{confirm}" immediate="true" />
					</td>
				</tr>
			</tfoot>
			</h:form>
		</table>
	
		<br/>	
	</c:if>
	<h:form>
		<div class="infoAltRem" style="width: 100%">
			<c:if test="${!trabalhoFimCurso.portalCoordenadorGraduacao}">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/> <h:commandLink action="#{trabalhoFimCurso.preCadastrar}" value="Cadastrar nova Orientação de Trabalho de Fim de Curso"></h:commandLink>
			</c:if>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
		    <h:graphicImage value="/img/link.gif" style="overflow: visible;"/>: Anexar o Arquivo
		    <img src="/shared/img/icones/download.png"/>: Baixar Arquivo da Produção
		</div>
	</h:form>

<h:form>
	<table class="listagem">
	<caption class="listagem">Lista de Orientações de Trabalhos de Conclusão de Curso</caption>
		<thead>
			<tr>
				<td style="text-align: center;">Ano</td>
				<td>Título</td>
				<td style="text-align: center;">Data de Defesa</td>
				<td>Orientando</td>
				<td>Orientador</td>
				<td>Tipo</td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</thead>

		<c:set var="lista" value="#{trabalhoFimCurso.allOrdenado}" />
		<c:if test="${empty lista}">
			<tr>
				<td colspan="7">
				<br />
				<center>
					<span style="color:red;">Nenhum Trabalho de Conclusão de Curso encontrado.</span>
				</center>
				</td>
			</tr>
		</c:if>

		<c:forEach items="#{lista}" var="item"  varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td style="text-align: center;">${item.ano}</td>
				<td>${item.titulo}</td>
				<td align="center"><fmt:formatDate value="${item.dataDefesa}" pattern="dd/MM/yyyy"/></td>
				<c:if test="${ not empty item.orientando.pessoa.nome}">
					<td>${item.orientando.pessoa.nome}</td>
				</c:if>
				<c:if test="${empty  item.orientando.pessoa.nome}">
					<td>${item.orientandoString}</td>
				</c:if>
				<td>${item.servidor.pessoa.nome}</td>
				<td>${item.tipoTrabalhoConclusao.descricao}</td>
				<td width="15">
					<h:commandLink id="btAlterar" title="Alterar" action="#{trabalhoFimCurso.atualizar}">
						<h:graphicImage url="/img/alterar.gif"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>				
				</td>
				<td width="15">
					<h:commandLink id="btRemover" title="Remover" action="#{trabalhoFimCurso.remover}" onclick="#{confirmDelete}" >
						<h:graphicImage url="/img/delete.gif"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>

				<td width="15">
					<h:commandLink id="btAnexar" title="Anexar o Arquivo" action="#{trabalhoFimCurso.preEnviarArquivo}">
						<h:graphicImage url="/img/link.gif"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>				
				</td>
				<td width="15">
				   <c:if test="${item.idArquivo != null}">			
					<html:link action="/enviarAquivo?dispatch=enviar&idarquivo=${item.idArquivo}">
						<img src="/shared/img/icones/download.png" title="Baixar Arquivo da Produção" /> 
					</html:link>					
				   </c:if>
				</td>
			</tr>
		</c:forEach>
	</table>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>