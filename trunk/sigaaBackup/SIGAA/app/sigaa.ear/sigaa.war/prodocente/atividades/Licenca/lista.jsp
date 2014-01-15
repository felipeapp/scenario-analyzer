<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Licença/Afastamento</h2>
<h:form id="form" >
		<center>
			<table class="formulario" width="70%">
			<caption class="listagem">Buscar Licença/Afastamento</caption>
				<tr>

					<th>Docente:</th>

					<td>
						<h:inputHidden id="id" value="#{licenca.idServidor}"/>
						<h:inputText id="nomeServidor"
							value="#{licenca.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
							source="form:nomeServidor" target="form:id"
							baseUrl="/sigaa/ajaxDocente" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
							parser="new ResponseXmlToHtmlListParser()" /> </td>
						<td>
						<span id="indicator"
							style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
						</td>

				</tr>

				<tr>
					<td align="center" colspan="2"> <h:commandButton actionListener="#{licenca.buscar}" onclick="submit()" value="Buscar"/> </td>
				</tr>
			</table>
		</center>
	</h:form>
	<br/>
	<br/>
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/> <h:commandLink action="#{licenca.preCadastrar}" value="Cadastrar Nova Licença"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Licença
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Licença<br/>
		</div>
	</h:form>
<h:outputText value="#{licenca.create}"/>
<table class=listagem>
<caption class="listagem">Lista de Licença/Afastamento</caption>
<thead>
<td>Servidor</td>
<td>Periodo Inicio</td>
<td>Periodo Fim</td>
<td>Tipo de Afastamento</td>
<td></td>
<td></td>
</thead>
<c:forEach items="${licenca.allAtividades}" var="item" varStatus="status">
	<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
<td>${item.servidor.pessoa.nome}</td>
<td><ufrn:format type="data" name="item" property="periodoInicio"/> </td>
<td><ufrn:format type="data" name="item" property="periodoFim"></ufrn:format> </td>
<td>${item.afastamento.descricao}</td>
<h:form>
<td  width=20>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{licenca.atualizar}"/>
</td>
</h:form>
<h:form>
<td  width=25>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/delete.gif" alt="Remover" action="#{licenca.remover}" onclick="javascript:if(confirm('Deseja realmente REMOVER essa atividade ?')){ return true;} return false; void(0);" />
</td>
</h:form>
</tr>
</c:forEach>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
