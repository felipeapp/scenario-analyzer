<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h:outputText value="#{atividadeEnsino.create}"/>
<h2>Atividade de Ensino</h2><br>
<h:form id="form" >
		<center>
			<table class="formulario" width="70%">
			<caption class="listagem">Buscar Atividade de Ensino</caption>
				<tr>

					<th>Docente:</th>

					<td>
						<h:inputHidden id="id" value="#{atividadeEnsino.idServidor}"/>
						<h:inputText id="nomeServidor"
							value="#{atividadeEnsino.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
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
					<td align="center" colspan="4"> <h:commandButton actionListener="#{atividadeEnsino.buscar}" onclick="submit()" value="Buscar"/> </td>
				</tr>
			</table>
		</center>
	</h:form>
	<br/>
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: <h:commandLink action="#{atividadeEnsino.preCadastrar}" value="Cadastrar Nova Atividade de Ensino"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Atividade Ensino<br/>
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Atividade Ensino <br/>
		</div>
	</h:form>

<table class="listagem" border="1">
	<caption class="listagem"> Lista de Atividades de Ensinos</caption>
	<thead>
		<td>Servidor</td>
		<td>Tipo Atividade</td>
		<td>Código Disciplina</td>
		<td>CódigoTurma</td>
		<td>Disciplina</td>
		<td>Carga Horária</td>
		<td>Semestre</td>
		<td>Ano</td>
		<td>Carga Horária Docente</td>

		<td></td>
		<td></td>
		</th>
		<c:forEach items="${atividadeEnsino.allAtividades}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

		<td>${item.servidor.pessoa.nome}</td>
		<td>${item.tipoAtividadeEnsino.descricao } </td>
		<td>${item.codigoDisciplina}</td>
		<td>${item.codigoTurma}</td>
		<td>${item.disciplina}</td>
		<td>${item.cargaHoraria}</td>
		<td>${item.semestre}</td>
		<td>${item.ano}</td>
		<td>${item.cargaHorariaDocente}</td>



<h:form>
<td  width=20>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{atividadeEnsino.atualizar}"/>
</td>
</h:form>
<h:form>
<td  width=25>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/delete.gif" alt="Remover" action="#{atividadeEnsino.remover}" onclick="javascript:if(confirm('Deseja realmente apagar essa produção ?')){ return true;} return false; void(0);" />
</td>
</h:form>
</tr>
</c:forEach>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
