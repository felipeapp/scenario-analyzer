<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
<h2>Orientação</h2>
<h:form id="form" >
		<center>
			<table class="formulario" width="100%">
			<caption class="listagem">Buscar Orientação</caption>
				<tr>

					<th width="30%">Docente:</th>

					<td align="left">
						<h:inputHidden id="id" value="#{orientacao.idServidor}"/>
						<h:inputText id="nomeServidor"
							value="#{orientacao.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
							source="form:nomeServidor" target="form:id"
							baseUrl="/sigaa/ajaxDocente" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
							parser="new ResponseXmlToHtmlListParser()" /> 
					</td>
					<td width="20%" align="left" > <h:commandButton actionListener="#{orientacao.buscar}" onclick="submit()" value="Buscar"/> </td>	
						
						<td>
						<span id="indicator"
							style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
						</td>

				</tr>

			</table>
		</center>
	</h:form>
	<br/>
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>:
			 <c:if test="${orientacao.tipoOrientacao }">
			  <h:commandLink action="#{orientacao.preCadastrar}" value="Cadastrar Novo Orientação"></h:commandLink>
		    </c:if>
		    <c:if test="${!orientacao.tipoOrientacao }">
			  <h:commandLink action="#{orientacao.preCadastrarResidencia}" value="Cadastrar Novo Orientação"></h:commandLink>
		    </c:if>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: AlterarOrientação
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Orientação
		</div>
	</h:form>
<h:outputText value="#{orientacao.create}"/>
<table class="listagem" border="1">
<caption class="listagem"> Lista de orientacaos</caption>
<thead>

<td>Docente</td>
<td>Tipo Orientação Docente</td>
<td>Nome do Aluno</td>
<td>Data Inicio</td>
<td>Data Fim</td>
<td>Data Alteracao</td>
<td>Tipo de Orientação</td>
<td></td><td></td>
</thead>
<c:forEach items="${orientacao.allAtividades}" var="item" varStatus="status">
	<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
<c:if test="${item.aluno.id==null }"></c:if>
<td>${item.servidor.pessoa.nome}</td>
<td>${item.orientacao.descricao}</td>
<c:if test="${item.aluno.id==null }">
<td>${item.nomeAluno}</td>
</c:if>
<c:if test="${item.aluno.id!=null }">
<td>${item.aluno.pessoa.nome}</td>
</c:if>
<td><ufrn:format type="data" name="item" property="dataInicio" ></ufrn:format> </td>
<td><ufrn:format type="data" name="item" property="dataFim" ></ufrn:format> </td>
<td>${item.dataAlteracao}</td>
<td>${item.tipoOrientacao.descricao}</td>
<h:form>
<td  width=20>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{orientacao.atualizar}"/>
</td>
</h:form>
<h:form>
<td  width=25>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/delete.gif" alt="Remover" action="#{orientacao.remover}" onclick="javascript:if(confirm('Deseja realmente REMOVER essa atividade ?')){ return true;} return false; void(0);" />
</td>
</h:form>
</tr>
</c:forEach>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
