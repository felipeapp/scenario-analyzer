<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<html>
	<f:view>
		<h2><ufrn:subSistema /> > Coordena��o de Curso</h2><br>
		<h:form id="form" >
			<center>
				<table class="formulario" width="70%">
				<caption class="listagem">Buscar Coordena��o de Curso</caption>
					<tr>
						<th  width="20%">Docente:</th>
						<td width="55%">
							<h:inputHidden id="id" value="#{coordenacao.idServidor}"/>
							<h:inputText id="nomeServidor"
								value="#{coordenacao.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
								source="form:nomeServidor" target="form:id"
								baseUrl="/sigaa/ajaxDocente" className="autocomplete"
								indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
								parser="new ResponseXmlToHtmlListParser()" /> 
						</td>
						<td>
							<span id="indicator"
								style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
						</td>

					</tr>

					<tr>
						<td align="center" colspan="4"> <h:commandButton actionListener="#{coordenacao.buscar}" onclick="submit()" value="Buscar"/> </td>
					</tr>
				</table>
			</center>
		</h:form>
		<br/>
		<h:form>
		<div class="infoAltRem">
			<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/> <h:commandLink action="#{coordenacao.preCadastrar}" value="Cadastrar Nova Coordena��o de Curso"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Coordena��o de Curso <br/>
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Coordena��o de Curso <br/>
		</div>
	</h:form>
		<h:outputText value="#{coordenacao.create}"/>
			<table class=listagem border="1">
			<caption class="listagem"> Lista de Coordena��o de Cursos</caption>
			<thead>
					<td>Servidor</td>
					<td>Nome do Curso</td>
					<td align="center">Pago</td>
					<td align="center">Per�odo Inicio</td>
					<td align="center">Per�odo Fim</td>
					<td></td>
					<td></td>
			</thead>
				<c:forEach items="${coordenacao.allAtividades}" var="item"  varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${item.servidor.pessoa.nome}</td>
						<td>${item.nome}</td>
						<td align="center">${item.pago ? "Sim" : "N�o"}</td>
						<td align="center"><ufrn:format type="data" name="item" property="periodoInicio" ></ufrn:format> </td>
						<td align="center"><ufrn:format type="data" name="item" property="periodoFim" ></ufrn:format> </td>
						<td  width=20>
							<h:form>
								<input type="hidden" value="${item.id}" name="id"/>
								<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{coordenacao.atualizar}" title="Alterar Coordena��o de Curso"/>
							</h:form>
						</td>
						<td  width=25>
							<h:form>
								<input type="hidden" value="${item.id}" name="id"/>
								<h:commandButton image="/img/delete.gif" alt="Remover" title="Remover Coordena��o de Curso" action="#{coordenacao.remover}" onclick="javascript:if(confirm('Deseja realmente REMOVER essa Coordena��o de Curso?')){ return true;} return false; void(0);" />
							</h:form>
						</td>
				</tr>
				</c:forEach>
			</table>
			
	</f:view>
</html>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
