<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Disciplina de Qualificação</h2>
	<br>
	<h:form id="form" >
		<center>
			<table class="formulario" width="70%">
			<caption class="listagem">Buscar Disciplina de Qualificação</caption>
				<tr>
					<td>
					<h:selectBooleanCheckbox id="bServidor" value="#{disciplinaQualificacao.buscaServidor}"/>
					</td>
					<th>Docente:</th>

					<td>
						<h:inputHidden id="id" value="#{disciplinaQualificacao.idServidor}"/>
						<h:inputText id="nomeServidor"
							value="#{disciplinaQualificacao.obj.qualificacaoDocente.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
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
				<td>
				 <h:selectBooleanCheckbox id="bUnidade" value="#{disciplinaQualificacao.buscaUnidade}"/>
				</td>
					<th>Departamento:</th>

					<td>
					<h:selectOneMenu value="#{disciplinaQualificacao.idUnidade}" style="width: 400px"
					disabled="#{disciplinaQualificacao.readOnly}" disabledClass="#{disciplinaQualificacao.disableClass}"
					id="departamento">
					<f:selectItem itemValue="-1" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
					</h:selectOneMenu></td>
				</tr>


				<tr>
					<td align="center" colspan="4"> <h:commandButton actionListener="#{disciplinaQualificacao.buscar}" onclick="submit()" value="Buscar"/> </td>
				</tr>
			</table>
		</center>
	</h:form>
	<br/>
	<br/>
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: <h:commandLink action="#{disciplinaQualificacao.preCadastrar}" value="Cadastrar Nova Disciplina de Qualificação"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Disciplina de Qualificação
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Disciplina de Qualificação<br/>
		</div>
	</h:form>
	<h:outputText value="#{disciplinaQualificacao.create}" />
	<table class=listagem>
		<caption class="listagem">Lista de Disciplinas de Qualificação</caption>
		<thead>
			<td>Disciplina</td>
			<td>Conceito</td>
			<td>Qualificação</td>
			<td></td>
			<td></td>
		</thead>
		<c:forEach items="${disciplinaQualificacao.allAtividades}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.disciplina}</td>
				<td>${item.conceito}</td>
				<td>${item.qualificacaoDocente.instituicao}</td>
				<h:form>
					<td width=20><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/alterar.gif" value="Alterar"
						action="#{disciplinaQualificacao.atualizar}" /></td>
				</h:form>
				<h:form>
					<td width=25><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/delete.gif" alt="Remover"
						action="#{disciplinaQualificacao.remover}" onclick="javascript:if(confirm('Deseja realmente REMOVER essa atividade ?')){ return true;} return false; void(0);" /></td>
				</h:form>
			</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
