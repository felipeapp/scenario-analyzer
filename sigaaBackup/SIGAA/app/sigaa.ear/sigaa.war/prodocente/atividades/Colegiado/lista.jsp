<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h2>colegiado</h2><br>

<h:form id="form" >
		<center>
			<table class="formulario" width="70%">
			<caption class="listagem">Buscar Colegiado</caption>
				<tr>
					<td>
					<h:selectBooleanCheckbox id="bServidor" value="#{colegiado.buscaServidor}"/>
					</td>
					<th>Docente:</th>
	
					<td>
						<h:inputHidden id="id" value="#{colegiado.idServidor}"/>
						<h:inputText id="nomeServidor"
							value="#{colegiado.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
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
				 <h:selectBooleanCheckbox id="bUnidade" value="#{colegiado.buscaUnidade}"/>
				</td>
					<th>Departamento:</th>

					<td>
					<h:selectOneMenu value="#{colegiado.idUnidade}" style="width: 400px"
					disabled="#{colegiado.readOnly}" disabledClass="#{colegiado.disableClass}"
					id="departamento">
					<f:selectItem itemValue="-1" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
					</h:selectOneMenu></td>
				</tr>	
				
				
				<tr>
					<td align="center" colspan="4"> <h:commandButton actionListener="#{colegiado.buscar}" onclick="submit()" value="Buscar"/> </td>
				</tr>
			</table>
		</center>
	</h:form>
	
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: <h:commandLink action="#{colegiado.preCadastrar}" value="Cadastrar Novo Colegiado"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Colegiado
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Colegiado <br/>
		</div>
	</h:form>
	
<h:outputText value="#{colegiado.create}"/>


<table class=listagem border="1" width="90%">
	<caption class="listagem"> Lista de colegiados</caption>
	<thead>

		<td>Tipo Membro Colegiado</td>
		<td>Departamento</td>
		<td>Instituição</td>
		<td>Periodo Inicio</td>
		<td>Comissão</td>
		<td>Numero Reuniões</td>
		<td>Servidor</td>
		<td>Tipo Comissao Colegiado</td>
		<td>Validação</th>
		<td></td><td></td>
	</thead>
<c:forEach items="${colegiado.allAtividades}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
<td>${item.tipoMembroColegiado.descricao}</td>
<td>${item.departamento}</td>
<c:if test="${item.ies.nome!=null }">
<td>${item.ies.nome}</td>
</c:if>
<c:if test="${item.ies.nome==null }">
<td>${item.instituicao}</td>
</c:if>
<td><ufrn:format name="item" property="periodoInicio" type="data"/></td>
<td>${item.comissao}</td>
<td>${item.numeroReunioes}</td>
<td>${item.servidor.pessoa.nome}</td>
<td>${item.tipoComissaoColegiado.descricao}</td>
<td>${item.validacao?"Sim":"Não"}</td>
<h:form>
<td  width=20>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/alterar.gif" value="Alterar"  id="alterar" action="#{colegiado.atualizar}"/>
</td>
</h:form>
<h:form>
<td  width=25>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/delete.gif" alt="Remover" id="remover" action="#{colegiado.remover}" onclick="javascript:if(confirm('Deseja realmente REMOVER essa atividade ?')){ return true;} return false; void(0);"  />
</td>
</h:form>
</tr>
</c:forEach>
</table>
<center>
	<h:form>
		<h:messages showDetail="true"/>
		<h:commandButton image="/img/voltar_des.gif" actionListener="#{paginacao.previousPage}"/>
		<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true">
			<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
		</h:selectOneMenu>
		<h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}"/>
	</h:form>
	</center>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
